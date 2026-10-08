package com.aryston.arkea.screen.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

record StatsData(List<General> general, List<ItemStats> items, List<MobStats> mobs) {
    static final List<StatType<Item>> ITEM_COLUMNS = List.of(Stats.ITEM_CRAFTED, Stats.ITEM_USED, Stats.ITEM_BROKEN, Stats.ITEM_PICKED_UP,
        Stats.ITEM_DROPPED);
    static final int COLUMNS = ITEM_COLUMNS.size() + 1;

    static StatsData read(StatsCounter stats) {
        return new StatsData(general(stats), items(stats), mobs(stats));
    }

    static List<Component> columnNames() {
        List<Component> names = new ArrayList<>();
        names.add(Stats.BLOCK_MINED.getDisplayName());
        for (StatType<Item> type : ITEM_COLUMNS) {
            names.add(type.getDisplayName());
        }
        return names;
    }

    private static List<General> general(StatsCounter stats) {
        List<General> rows = new ArrayList<>();
        for (Stat<Identifier> stat : Stats.CUSTOM) {
            String key = "stat." + stat.getValue().toString().replace(':', '.');
            rows.add(new General(stat.getValue(), Component.translatable(key), stat.format(stats.getValue(stat))));
        }
        rows.sort(Comparator.comparing(row -> I18n.get("stat." + row.id().toString().replace(':', '.'))));
        return rows;
    }

    private static List<ItemStats> items(StatsCounter stats) {
        Set<Item> items = new LinkedHashSet<>();
        for (Item item : BuiltInRegistries.ITEM) {
            for (StatType<Item> type : ITEM_COLUMNS) {
                if (type.contains(item) && stats.getValue(type.get(item)) > 0) {
                    items.add(item);
                }
            }
        }
        for (Block block : BuiltInRegistries.BLOCK) {
            if (Stats.BLOCK_MINED.contains(block) && stats.getValue(Stats.BLOCK_MINED.get(block)) > 0) {
                items.add(block.asItem());
            }
        }
        items.remove(Items.AIR);
        List<ItemStats> rows = new ArrayList<>();
        for (Item item : items) {
            int[] values = new int[COLUMNS];
            if (item instanceof BlockItem blockItem && Stats.BLOCK_MINED.contains(blockItem.getBlock())) {
                values[0] = stats.getValue(Stats.BLOCK_MINED.get(blockItem.getBlock()));
            }
            for (int index = 0; index < ITEM_COLUMNS.size(); index++) {
                StatType<Item> type = ITEM_COLUMNS.get(index);
                values[index + 1] = type.contains(item) ? stats.getValue(type.get(item)) : 0;
            }
            rows.add(new ItemStats(item, values));
        }
        return rows;
    }

    private static List<MobStats> mobs(StatsCounter stats) {
        List<MobStats> rows = new ArrayList<>();
        for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
            int killed = stats.getValue(Stats.ENTITY_KILLED.get(type));
            int killedBy = stats.getValue(Stats.ENTITY_KILLED_BY.get(type));
            if (killed > 0 || killedBy > 0) {
                rows.add(new MobStats(type, killed, killedBy));
            }
        }
        rows.sort(Comparator.comparingInt(MobStats::killed).reversed());
        return rows;
    }

    record General(Identifier id, Component name, String value) {
    }

    record ItemStats(Item item, int[] values) {
    }

    record MobStats(EntityType<?> type, int killed, int killedBy) {
    }
}

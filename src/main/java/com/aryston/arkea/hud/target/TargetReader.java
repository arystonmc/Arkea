package com.aryston.arkea.hud.target;

import com.aryston.arkea.mixin.MultiPlayerGameModeAccessor;
import com.aryston.arkea.ui.theme.ArkColors;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.fml.ModList;
import org.jspecify.annotations.Nullable;

final class TargetReader {
    private static final String AGE = "age";
    private static final String MINECRAFT = "minecraft";
    private static final String MINECRAFT_NAME = "Minecraft";
    private static final float PERCENT = 100.0F;
    private static final double SPEED_TO_BLOCKS = 42.16;
    private static final double JUMP_CUBIC = -0.1817584952;
    private static final double JUMP_SQUARE = 3.689713992;
    private static final double JUMP_LINEAR = 2.128599134;
    private static final double JUMP_OFFSET = -0.343930367;
    private static final int GOOD = ArkColors.rgba(123, 201, 95, 1.0F);
    private static final int BAD = ArkColors.ERROR;
    private static final int MOD_COLOR = ArkColors.rgba(127, 167, 232, 1.0F);
    private static final int POWER = ArkColors.rgba(232, 96, 80, 1.0F);

    private TargetReader() {
    }

    static @Nullable TargetInfo read(Minecraft minecraft) {
        HitResult hit = minecraft.hitResult;
        if (hit == null || minecraft.player == null || minecraft.level == null) {
            return null;
        }
        if (hit instanceof EntityHitResult entityHit) {
            return entity(entityHit.getEntity());
        }
        if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK) {
            return block(minecraft, minecraft.level, minecraft.player, blockHit.getBlockPos());
        }
        return null;
    }

    static int modColor() {
        return MOD_COLOR;
    }

    private static @Nullable TargetInfo block(Minecraft minecraft, ClientLevel level, Player player, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return null;
        }
        ItemStack icon = state.getCloneItemStack(level, pos, false);
        if (icon.isEmpty()) {
            icon = new ItemStack(state.getBlock().asItem());
        }
        List<TargetInfo.Line> lines = new ArrayList<>();
        harvest(lines, level, player, pos, state);
        growth(lines, state);
        power(lines, state);
        float breaking = TargetInfo.NONE;
        if (minecraft.gameMode != null && minecraft.gameMode.isDestroying()) {
            MultiPlayerGameModeAccessor gameMode = (MultiPlayerGameModeAccessor) minecraft.gameMode;
            if (pos.equals(gameMode.arkea$destroyBlockPos())) {
                breaking = Math.clamp(gameMode.arkea$destroyProgress(), 0.0F, 1.0F);
            }
        }
        Identifier id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return new TargetInfo("block:" + pos.asLong() + ":" + id, icon, state.getBlock().getName(), modName(id.getNamespace()), lines, 0.0F, 0.0F, 0,
            breaking);
    }

    private static TargetInfo entity(Entity entity) {
        List<TargetInfo.Line> lines = new ArrayList<>();
        ItemStack icon = ItemStack.EMPTY;
        float health = 0.0F;
        float maxHealth = 0.0F;
        int armor = 0;
        if (entity instanceof ItemEntity item) {
            icon = item.getItem();
            lines.add(new TargetInfo.Line(Component.translatable("arkea.target.count", item.getItem().getCount()), ArkColors.TEXT_SOFT, ItemStack.EMPTY));
        } else {
            ItemStack pick = entity.getPickResult();
            icon = pick != null ? pick : ItemStack.EMPTY;
        }
        if (entity instanceof LivingEntity living) {
            health = living.getHealth();
            maxHealth = living.getMaxHealth();
            armor = living.getArmorValue();
            if (living.isBaby()) {
                lines.add(new TargetInfo.Line(Component.translatable("arkea.target.baby"), ArkColors.TEXT_SOFT, ItemStack.EMPTY));
            }
        }
        if (entity instanceof Villager villager) {
            Component profession = villager.getVillagerData().profession().value().name();
            Component level = Component.translatable("merchant.level." + villager.getVillagerData().level());
            lines.add(new TargetInfo.Line(Component.translatable("arkea.target.villager", profession, level), ArkColors.TEXT_SOFT, ItemStack.EMPTY));
        }
        if (entity instanceof AbstractHorse horse) {
            double speed = horse.getAttributeValue(Attributes.MOVEMENT_SPEED) * SPEED_TO_BLOCKS;
            double jump = horse.getAttributeValue(Attributes.JUMP_STRENGTH);
            double height = JUMP_CUBIC * jump * jump * jump + JUMP_SQUARE * jump * jump + JUMP_LINEAR * jump + JUMP_OFFSET;
            lines.add(new TargetInfo.Line(Component.translatable("arkea.target.horse", format(speed), format(height)), ArkColors.TEXT_SOFT, ItemStack.EMPTY));
        }
        Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        return new TargetInfo("entity:" + entity.getId(), icon, entity.getDisplayName(), modName(id.getNamespace()), lines, health, maxHealth, armor,
            TargetInfo.NONE);
    }

    private static void harvest(List<TargetInfo.Line> lines, ClientLevel level, Player player, BlockPos pos, BlockState state) {
        ItemStack tool = tool(state);
        boolean needsTool = state.requiresCorrectToolForDrops();
        if (tool.isEmpty() && !needsTool) {
            return;
        }
        if (!needsTool) {
            lines.add(new TargetInfo.Line(Component.translatable("arkea.target.bestTool"), ArkColors.TEXT_SOFT, tool));
            return;
        }
        boolean harvestable = player.hasCorrectToolForDrops(state, level, pos);
        lines.add(new TargetInfo.Line(Component.translatable(harvestable ? "arkea.target.harvestable" : "arkea.target.needsTool"), harvestable ? GOOD : BAD,
            tool));
    }

    private static ItemStack tool(BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            return new ItemStack(Items.IRON_PICKAXE);
        }
        if (state.is(BlockTags.MINEABLE_WITH_AXE)) {
            return new ItemStack(Items.IRON_AXE);
        }
        if (state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            return new ItemStack(Items.IRON_SHOVEL);
        }
        if (state.is(BlockTags.MINEABLE_WITH_HOE)) {
            return new ItemStack(Items.IRON_HOE);
        }
        return ItemStack.EMPTY;
    }

    private static void growth(List<TargetInfo.Line> lines, BlockState state) {
        for (Property<?> property : state.getProperties()) {
            if (property instanceof IntegerProperty age && AGE.equals(property.getName())) {
                int value = state.getValue(age);
                int max = Collections.max(age.getPossibleValues());
                if (value >= max) {
                    lines.add(new TargetInfo.Line(Component.translatable("arkea.target.mature"), GOOD, ItemStack.EMPTY));
                } else {
                    int percent = Math.round(value * PERCENT / max);
                    lines.add(new TargetInfo.Line(Component.translatable("arkea.target.growth", percent), ArkColors.TEXT_SOFT, ItemStack.EMPTY));
                }
                return;
            }
        }
    }

    private static void power(List<TargetInfo.Line> lines, BlockState state) {
        if (state.hasProperty(BlockStateProperties.POWER)) {
            lines.add(new TargetInfo.Line(Component.translatable("arkea.target.power", state.getValue(BlockStateProperties.POWER)), POWER, ItemStack.EMPTY));
        } else if (state.hasProperty(BlockStateProperties.POWERED)) {
            boolean powered = state.getValue(BlockStateProperties.POWERED);
            lines.add(new TargetInfo.Line(Component.translatable(powered ? "arkea.target.powered" : "arkea.target.unpowered"), powered ? POWER : ArkColors.TEXT_SOFT,
                ItemStack.EMPTY));
        }
    }

    private static Component modName(String namespace) {
        String name = MINECRAFT.equals(namespace) ? MINECRAFT_NAME
            : ModList.get().getModContainerById(namespace).map(container -> container.getModInfo().getDisplayName()).orElse(namespace);
        return Component.literal(name).withStyle(ChatFormatting.ITALIC);
    }

    private static String format(double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }
}

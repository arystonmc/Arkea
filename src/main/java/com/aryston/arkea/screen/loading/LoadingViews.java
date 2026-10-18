package com.aryston.arkea.screen.loading;

import com.aryston.arkea.mixin.ConnectScreenAccessor;
import com.aryston.arkea.mixin.DisconnectedScreenAccessor;
import com.aryston.arkea.mixin.LevelLoadingScreenAccessor;
import com.aryston.arkea.mixin.ProgressScreenAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.jspecify.annotations.Nullable;

final class LoadingViews {
    private static final List<String> SERVER_STEPS = List.of("connect", "login", "join", "terrain");
    private static final List<String> WORLD_STEPS = List.of("data", "server", "spawn", "terrain");
    private static final Set<String> WORLD_DATA_MESSAGES = Set.of("selectWorld.data_read", "selectWorld.resource_load");
    private static final int LOGIN_STEP = 1;
    private static final int JOIN_STEP = 2;
    private static final int TERRAIN_STEP = 3;
    private static final int WORLD_DATA_STEP = 0;
    private static final int WORLD_SERVER_STEP = 1;
    private static final int WORLD_SPAWN_STEP = 2;
    private static final int WORLD_TERRAIN_STEP = 3;
    private static final float PERCENT = 100.0F;
    private static final LoadingView.Span WORLD_DATA = new LoadingView.Span("world.data", 0.0F, 0.12F);
    private static final LoadingView.Span WORLD_SERVER = new LoadingView.Span("world.server", 0.12F, 0.2F);
    private static final LoadingView.Span WORLD_TERRAIN = new LoadingView.Span("world.terrain", 0.2F, 1.0F);
    private static final List<LoadingView.Span> SERVER_CONNECTION = List.of(new LoadingView.Span("server.connect", 0.0F, 0.25F),
        new LoadingView.Span("server.login", 0.25F, 0.5F), new LoadingView.Span("server.join", 0.5F, 0.75F));
    private static final LoadingView.Span SERVER_TERRAIN = new LoadingView.Span("server.terrain", 0.75F, 1.0F);

    private LoadingViews() {
    }

    static boolean supports(Screen screen) {
        Class<?> type = screen.getClass();
        return type == ConnectScreen.class || type == LevelLoadingScreen.class || type == ProgressScreen.class || type == GenericMessageScreen.class
            || type == DisconnectedScreen.class;
    }

    static Optional<LoadingView> of(Screen screen, boolean worldJoin) {
        if (screen instanceof ConnectScreen connect) {
            return Optional.of(connecting(((ConnectScreenAccessor) connect).arkea$status()));
        }
        if (screen instanceof LevelLoadingScreen loading) {
            return Optional.of(level((LevelLoadingScreenAccessor) loading));
        }
        if (screen instanceof GenericMessageScreen && WORLD_DATA_MESSAGES.contains(key(screen.getTitle()))) {
            return Optional.of(world(WORLD_DATA_STEP, WORLD_DATA, LoadingView.NO_PROGRESS, screen.getTitle()));
        }
        if (screen instanceof ProgressScreen progress) {
            return Optional.of(worldJoin ? world(WORLD_DATA_STEP, WORLD_DATA, LoadingView.NO_PROGRESS, null) : progress((ProgressScreenAccessor) progress));
        }
        if (screen instanceof GenericMessageScreen) {
            return Optional.of(LoadingView.of(LoadingView.Mood.BUSY, Component.translatable("arkea.loading.kicker.wait"), screen.getTitle(), null, null,
                LoadingView.NO_PROGRESS, List.of()));
        }
        if (screen instanceof DisconnectedScreen disconnected) {
            Component reason = ((DisconnectedScreenAccessor) disconnected).arkea$details().reason();
            return Optional.of(LoadingView.of(LoadingView.Mood.ERROR, screen.getTitle().copy(), titleOr(Component.translatable("arkea.loading.lost")),
                reason, null, LoadingView.NO_PROGRESS, List.of()));
        }
        return Optional.empty();
    }

    private static LoadingView progress(ProgressScreenAccessor accessor) {
        Component header = accessor.arkea$header() != null ? accessor.arkea$header() : Component.translatable("arkea.loading.working");
        return LoadingView.of(LoadingView.Mood.BUSY, Component.translatable("arkea.loading.kicker.working"), header, null, accessor.arkea$stage(),
            accessor.arkea$stage() != null ? accessor.arkea$progress() / PERCENT : LoadingView.NO_PROGRESS, List.of());
    }

    private static LoadingView connecting(Component status) {
        String key = key(status);
        int step = switch (key) {
            case "connect.negotiating", "connect.authorizing", "connect.encrypting" -> LOGIN_STEP;
            case "connect.joining", "connect.reconfiguring", "connect.reconfiging" -> JOIN_STEP;
            default -> 0;
        };
        return new LoadingView(LoadingView.Mood.BUSY, Component.translatable("arkea.loading.kicker.server"), titleOr(Component.translatable("menu.multiplayer")),
            null, status, SERVER_CONNECTION.get(step), LoadingView.NO_PROGRESS, steps("server", SERVER_STEPS, step), false);
    }

    private static LoadingView level(LevelLoadingScreenAccessor accessor) {
        LevelLoadTracker tracker = accessor.arkea$loadTracker();
        float progress = tracker.hasProgress() ? tracker.serverProgress() : LoadingView.NO_PROGRESS;
        Component terrain = Component.translatable("multiplayer.downloadingTerrain");
        LevelLoadingScreen.Reason reason = accessor.arkea$reason();
        Minecraft minecraft = Minecraft.getInstance();
        if (reason != LevelLoadingScreen.Reason.OTHER) {
            String dimension = reason == LevelLoadingScreen.Reason.NETHER_PORTAL ? "nether" : "end";
            return LoadingView.of(LoadingView.Mood.BUSY, Component.translatable("arkea.loading.kicker.travel"),
                Component.translatable("arkea.loading.dimension." + dimension), null, terrain, progress, List.of());
        }
        if (!minecraft.hasSingleplayerServer()) {
            return new LoadingView(LoadingView.Mood.BUSY, Component.translatable("arkea.loading.kicker.server"),
                titleOr(Component.translatable("menu.multiplayer")), null, terrain, SERVER_TERRAIN, progress, steps("server", SERVER_STEPS, TERRAIN_STEP),
                false);
        }
        if (!tracker.hasProgress()) {
            return world(WORLD_SERVER_STEP, WORLD_SERVER, LoadingView.NO_PROGRESS, null);
        }
        return world(minecraft.level == null ? WORLD_SPAWN_STEP : WORLD_TERRAIN_STEP, WORLD_TERRAIN, progress, null);
    }

    private static LoadingView world(int step, LoadingView.Span span, float progress, @Nullable Component labelOverride) {
        Component label = Component.translatable("arkea.loading.step.world." + WORLD_STEPS.get(step));
        return new LoadingView(LoadingView.Mood.BUSY, Component.translatable("arkea.loading.kicker.world"), worldTitle(), null,
            labelOverride != null ? labelOverride : label, span, progress, steps("world", WORLD_STEPS, step), true);
    }

    private static Component worldTitle() {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (JoinTarget.name() == null && server != null) {
            return Component.literal(server.getWorldData().getLevelName());
        }
        return titleOr(Component.translatable("menu.singleplayer"));
    }

    private static Component titleOr(Component fallback) {
        Component name = JoinTarget.name();
        return name != null ? name : fallback;
    }

    private static String key(Component component) {
        return component.getContents() instanceof TranslatableContents contents ? contents.getKey() : "";
    }

    private static List<LoadingView.Step> steps(String group, List<String> keys, int active) {
        List<LoadingView.Step> steps = new ArrayList<>(keys.size());
        for (int index = 0; index < keys.size(); index++) {
            LoadingView.StepState state = index < active ? LoadingView.StepState.DONE : index == active ? LoadingView.StepState.ACTIVE
                : LoadingView.StepState.WAITING;
            steps.add(new LoadingView.Step(Component.translatable("arkea.loading.step." + group + "." + keys.get(index)), state));
        }
        return steps;
    }
}

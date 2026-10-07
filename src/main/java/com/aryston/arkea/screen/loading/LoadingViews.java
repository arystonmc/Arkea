package com.aryston.arkea.screen.loading;

import com.aryston.arkea.mixin.ConnectScreenAccessor;
import com.aryston.arkea.mixin.DisconnectedScreenAccessor;
import com.aryston.arkea.mixin.LevelLoadingScreenAccessor;
import com.aryston.arkea.mixin.ProgressScreenAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

final class LoadingViews {
    private static final List<String> SERVER_STEPS = List.of("connect", "login", "join", "terrain");
    private static final List<String> WORLD_STEPS = List.of("spawn", "chunks", "enter");
    private static final int LOGIN_STEP = 1;
    private static final int JOIN_STEP = 2;
    private static final int TERRAIN_STEP = 3;
    private static final int CHUNKS_STEP = 1;
    private static final float PERCENT = 100.0F;

    private LoadingViews() {
    }

    static boolean supports(Screen screen) {
        Class<?> type = screen.getClass();
        return type == ConnectScreen.class || type == LevelLoadingScreen.class || type == ProgressScreen.class || type == GenericMessageScreen.class
            || type == DisconnectedScreen.class;
    }

    static Optional<LoadingView> of(Screen screen) {
        if (screen instanceof ConnectScreen connect) {
            return Optional.of(connecting(((ConnectScreenAccessor) connect).arkea$status()));
        }
        if (screen instanceof LevelLoadingScreen loading) {
            return Optional.of(level((LevelLoadingScreenAccessor) loading));
        }
        if (screen instanceof ProgressScreen progress) {
            ProgressScreenAccessor accessor = (ProgressScreenAccessor) progress;
            Component header = accessor.arkea$header() != null ? accessor.arkea$header() : Component.translatable("arkea.loading.working");
            return Optional.of(new LoadingView(LoadingView.Mood.BUSY, Component.translatable("arkea.loading.kicker.working"), header, null,
                accessor.arkea$stage(), accessor.arkea$stage() != null ? accessor.arkea$progress() / PERCENT : LoadingView.NO_PROGRESS, List.of()));
        }
        if (screen instanceof GenericMessageScreen) {
            return Optional.of(new LoadingView(LoadingView.Mood.BUSY, Component.translatable("arkea.loading.kicker.wait"), screen.getTitle(), null, null,
                LoadingView.NO_PROGRESS, List.of()));
        }
        if (screen instanceof DisconnectedScreen disconnected) {
            Component reason = ((DisconnectedScreenAccessor) disconnected).arkea$details().reason();
            return Optional.of(new LoadingView(LoadingView.Mood.ERROR, screen.getTitle().copy(), titleOr(Component.translatable("arkea.loading.lost")),
                reason, null, LoadingView.NO_PROGRESS, List.of()));
        }
        return Optional.empty();
    }

    private static LoadingView connecting(Component status) {
        String key = status.getContents() instanceof TranslatableContents contents ? contents.getKey() : "";
        int step = switch (key) {
            case "connect.negotiating", "connect.authorizing", "connect.encrypting" -> LOGIN_STEP;
            case "connect.joining", "connect.reconfiguring", "connect.reconfiging" -> JOIN_STEP;
            default -> 0;
        };
        return new LoadingView(LoadingView.Mood.BUSY, Component.translatable("arkea.loading.kicker.server"), titleOr(Component.translatable("menu.multiplayer")),
            null, status, step / (float) SERVER_STEPS.size(), steps("server", SERVER_STEPS, step));
    }

    private static LoadingView level(LevelLoadingScreenAccessor accessor) {
        LevelLoadTracker tracker = accessor.arkea$loadTracker();
        float progress = tracker.hasProgress() ? tracker.serverProgress() : LoadingView.NO_PROGRESS;
        Component terrain = Component.translatable("multiplayer.downloadingTerrain");
        LevelLoadingScreen.Reason reason = accessor.arkea$reason();
        if (reason != LevelLoadingScreen.Reason.OTHER) {
            String dimension = reason == LevelLoadingScreen.Reason.NETHER_PORTAL ? "nether" : "end";
            return new LoadingView(LoadingView.Mood.BUSY, Component.translatable("arkea.loading.kicker.travel"),
                Component.translatable("arkea.loading.dimension." + dimension), null, terrain, progress, List.of());
        }
        if (!Minecraft.getInstance().hasSingleplayerServer()) {
            float overall = (TERRAIN_STEP + (Float.isNaN(progress) ? 0.0F : progress)) / SERVER_STEPS.size();
            return new LoadingView(LoadingView.Mood.BUSY, Component.translatable("arkea.loading.kicker.server"),
                titleOr(Component.translatable("menu.multiplayer")), null, terrain, overall, steps("server", SERVER_STEPS, TERRAIN_STEP));
        }
        int step = Float.isNaN(progress) || progress < 1.0F ? 0 : CHUNKS_STEP;
        return new LoadingView(LoadingView.Mood.BUSY, Component.translatable("arkea.loading.kicker.world"), titleOr(Component.translatable("menu.singleplayer")),
            null, terrain, progress, steps("world", WORLD_STEPS, step));
    }

    private static Component titleOr(Component fallback) {
        Component name = JoinTarget.name();
        return name != null ? name : fallback;
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

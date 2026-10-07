package com.aryston.arkea.screen.servers;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.network.EventLoopGroupHolder;

final class ServerPings implements AutoCloseable {
    private static final int THREADS = 5;
    private static final String PINGING_KEY = "multiplayer.status.pinging";

    private final Minecraft minecraft;
    private final ServerStatusPinger pinger = new ServerStatusPinger();
    private final ExecutorService executor = Executors.newFixedThreadPool(THREADS,
        new ThreadFactoryBuilder().setNameFormat("Arkea server pinger #%d").setDaemon(true).build());
    private final Runnable persist;

    ServerPings(Minecraft minecraft, Runnable persist) {
        this.minecraft = minecraft;
        this.persist = persist;
    }

    void ping(ServerData data) {
        if (data.state() != ServerData.State.INITIAL) {
            return;
        }
        data.setState(ServerData.State.PINGING);
        data.motd = CommonComponents.EMPTY;
        data.status = CommonComponents.EMPTY;
        this.executor.submit(() -> {
            try {
                this.pinger.pingServer(data, () -> this.minecraft.execute(this.persist), () -> {
                    boolean compatible = data.protocol == SharedConstants.getCurrentVersion().protocolVersion();
                    data.setState(compatible ? ServerData.State.SUCCESSFUL : ServerData.State.INCOMPATIBLE);
                }, EventLoopGroupHolder.remote(this.minecraft.options.useNativeTransport()));
                if (data.state() == ServerData.State.PINGING && !isPinging(data.motd)) {
                    data.setState(ServerData.State.UNREACHABLE);
                }
            } catch (UnknownHostException exception) {
                this.fail(data, "multiplayer.status.cannot_resolve");
            } catch (Exception exception) {
                this.fail(data, "multiplayer.status.cannot_connect");
            }
        });
    }

    private static boolean isPinging(Component motd) {
        return motd.getContents() instanceof TranslatableContents contents && PINGING_KEY.equals(contents.getKey());
    }

    private void fail(ServerData data, String key) {
        data.setState(ServerData.State.UNREACHABLE);
        data.motd = Component.translatable(key);
    }

    void tick() {
        this.pinger.tick();
    }

    @Override
    public void close() {
        this.pinger.removeAll();
        this.executor.shutdownNow();
    }
}

package com.aryston.arkea.screen.servers;

import com.aryston.arkea.Arkea;
import com.aryston.arkea.screen.loading.JoinTarget;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.overlay.ArkDialog;
import com.aryston.arkea.ui.overlay.ArkToasts;
import com.aryston.arkea.ui.overlay.ToastTone;
import com.aryston.arkea.ui.render.EmptyState;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.screen.ArkWindowScreen;
import com.aryston.arkea.ui.screen.NavGroup;
import com.aryston.arkea.ui.screen.SidebarBrand;
import com.aryston.arkea.ui.text.SearchText;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkButton;
import com.aryston.arkea.ui.widget.ArkIconButton;
import com.aryston.arkea.ui.widget.ArkTab;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.ButtonVariant;
import com.aryston.arkea.ui.widget.IconButtonStyle;
import com.aryston.arkea.ui.widget.NavEntry;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.server.LanServer;
import net.minecraft.client.server.LanServerDetection;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class ArkMultiplayerScreen extends ArkWindowScreen {
    private static final float TAB_GAP = 20.0F;
    private static final float TABS_TO_LIST = 14.0F;
    private static final float ROW_GAP = 6.0F;
    private static final float REFRESH_HEIGHT = 28.0F;
    private static final float REFRESH_LIFT = 2.0F;
    private static final float PLAYERS_RESERVE = 96.0F;
    private static final float ROW_PADDING_RIGHT = 18.0F;
    private static final float PING_COLUMN = 64.0F;
    private static final float ROW_GAP_INNER = 14.0F;
    private static String lastDirectAddress = "";

    private ServerPings pings;
    private ServerIcons icons;
    private final ServerDialogs dialogs;
    private final ServerList servers;
    private final LanServerDetection.LanServerList lanList = new LanServerDetection.LanServerList();
    private final List<ArkWidget> rows = new ArrayList<>();
    private final List<ServerRow> serverRows = new ArrayList<>();
    private List<LanServer> lanServers = List.of();
    private LanServerDetection.@Nullable LanServerDetector lanDetector;
    private Tab tab = Tab.SERVERS;
    private @Nullable ServerData selectedServer;
    private @Nullable LanServer selectedLan;
    private Box tabsArea = Box.EMPTY;
    private Box emptyArea = Box.EMPTY;
    private final List<ArkWidget> header = new ArrayList<>();

    public ArkMultiplayerScreen(Screen lastScreen) {
        super(Component.translatable("menu.multiplayer"), lastScreen);
        this.servers = new ServerList(this.minecraft);
        this.dialogs = new ServerDialogs(this);
    }

    @Override
    public void added() {
        super.added();
        this.pings = new ServerPings(this.minecraft, this.servers::save);
        this.icons = new ServerIcons(this.minecraft);
        this.servers.load();
        if (this.lanDetector == null) {
            try {
                this.lanDetector = new LanServerDetection.LanServerDetector(this.lanList);
                this.lanDetector.start();
            } catch (Exception exception) {
                Arkea.LOGGER.warn("Unable to start LAN server detection", exception);
            }
        }
    }

    @Override
    protected Component crumb() {
        return Component.translatable("arkea.servers.crumb");
    }

    @Override
    protected @Nullable SidebarBrand brand() {
        return null;
    }

    @Override
    protected List<NavGroup> navigation() {
        return List.of();
    }

    @Override
    protected String currentNav() {
        return "";
    }

    @Override
    protected void openNav(NavEntry entry) {
    }

    @Override
    protected @Nullable Component searchHint() {
        return Component.translatable("arkea.servers.search");
    }

    private List<ServerData> visibleServers() {
        String query = SearchText.normalize(this.searchQuery());
        List<ServerData> visible = new ArrayList<>();
        for (int index = 0; index < this.servers.size(); index++) {
            ServerData data = this.servers.get(index);
            if (SearchText.matches(query, data.name, data.ip, data.motd == null ? "" : data.motd.getString())) {
                visible.add(data);
            }
        }
        return visible;
    }

    @Override
    protected float buildContent(Box area) {
        this.rows.clear();
        this.serverRows.clear();
        this.header.clear();
        this.tabsArea = new Box(area.x(), area.y(), area.width(), ArkTab.HEIGHT);
        float tabX = area.x();
        for (Tab each : Tab.values()) {
            ArkTab widget = this.add(new ArkTab(this, each.label(), () -> each == Tab.SERVERS ? this.servers.size() : this.lanServers.size(),
                () -> this.tab == each, () -> this.switchTab(each)));
            widget.key("tab:" + each.name());
            widget.setBounds(new Box(tabX, area.y(), widget.preferredWidth(), ArkTab.HEIGHT));
            this.header.add(widget);
            tabX += widget.preferredWidth() + TAB_GAP;
        }
        ArkButton refresh = this.add(new ArkButton(this, Component.translatable("selectServer.refresh"), ButtonVariant.SUBTLE, this::refresh)
            .icon(Icons.RESET));
        refresh.key("refresh");
        float refreshWidth = refresh.preferredWidth();
        refresh.setBounds(new Box(area.right() - refreshWidth, area.y() + (ArkTab.HEIGHT - REFRESH_HEIGHT) * 0.5F - REFRESH_LIFT, refreshWidth, REFRESH_HEIGHT));
        this.header.add(refresh);
        float y = area.y() + ArkTab.HEIGHT + TABS_TO_LIST;
        float bottom = this.tab == Tab.SERVERS ? this.buildServers(area, y) : this.buildLan(area, y);
        this.emptyArea = this.rows.isEmpty() ? new Box(area.x(), y, area.width(), EmptyState.HEIGHT) : Box.EMPTY;
        return this.rows.isEmpty() ? y + EmptyState.HEIGHT : bottom;
    }

    private float buildServers(Box area, float top) {
        List<ServerData> visible = this.visibleServers();
        if (this.selectedServer == null || !visible.contains(this.selectedServer)) {
            this.selectedServer = visible.isEmpty() ? null : visible.getFirst();
        }
        float y = top;
        for (ServerData data : visible) {
            this.pings.ping(data);
            boolean selected = data == this.selectedServer;
            List<ArkWidget> actions = selected ? this.actions(data, area.right(), y) : List.of();
            ServerRow row = this.add(new ServerRow(this, data, () -> this.icons.icon(data), () -> data == this.selectedServer, () -> this.select(data),
                () -> this.join(data), actions));
            row.key("server:" + data.ip + ":" + data.name);
            row.setBounds(new Box(area.x(), y, area.width(), ServerRow.HEIGHT));
            this.rows.add(row);
            this.serverRows.add(row);
            y += ServerRow.HEIGHT + ROW_GAP;
        }
        return y - ROW_GAP;
    }

    private List<ArkWidget> actions(ServerData data, float rowRight, float rowY) {
        int index = this.indexOf(data);
        List<ArkWidget> actions = new ArrayList<>();
        if (index > 0) {
            actions.add(this.action("up", Icons.UP, Component.translatable("arkea.servers.up"), () -> this.move(data, -1)));
        }
        if (index >= 0 && index < this.servers.size() - 1) {
            actions.add(this.action("down", Icons.DOWN, Component.translatable("arkea.servers.down"), () -> this.move(data, 1)));
        }
        actions.add(this.action("copy", Icons.COPY, Component.translatable("arkea.servers.copy"), () -> this.copyAddress(data)));
        actions.add(this.action("edit", Icons.EDIT, Component.translatable("selectServer.edit"), () -> this.dialogs.edit(data)));
        actions.add(this.action("delete", Icons.TRASH, Component.translatable("selectServer.delete"), () -> this.dialogs.delete(data)));
        float x = rowRight - ROW_PADDING_RIGHT - PING_COLUMN - ROW_GAP_INNER - PLAYERS_RESERVE
            - actions.size() * (ServerRow.ACTION_SIZE + ServerRow.ACTION_GAP);
        float y = rowY + (ServerRow.HEIGHT - ServerRow.ACTION_SIZE) * 0.5F;
        for (ArkWidget action : actions) {
            action.setBounds(new Box(x, y, ServerRow.ACTION_SIZE, ServerRow.ACTION_SIZE));
            x += ServerRow.ACTION_SIZE + ServerRow.ACTION_GAP;
        }
        return actions;
    }

    private ArkIconButton action(String name, Icon icon, Component label, Runnable run) {
        ArkIconButton button = this.add(new ArkIconButton(this, icon, label, name.equals("delete") ? IconButtonStyle.DANGER : IconButtonStyle.TILE, run));
        button.key("server-action:" + name);
        button.setTooltip(label);
        return button;
    }

    private float buildLan(Box area, float top) {
        if (this.selectedLan == null || !this.lanServers.contains(this.selectedLan)) {
            this.selectedLan = this.lanServers.isEmpty() ? null : this.lanServers.getFirst();
        }
        float y = top;
        for (LanServer server : this.lanServers) {
            LanRow row = this.add(new LanRow(this, server, this.minecraft.options.hideServerAddress, () -> server == this.selectedLan,
                () -> this.selectLan(server), () -> this.joinLan(server)));
            row.key("lan:" + server.getAddress());
            row.setBounds(new Box(area.x(), y, area.width(), ServerRow.HEIGHT));
            this.rows.add(row);
            y += ServerRow.HEIGHT + ROW_GAP;
        }
        return y - ROW_GAP;
    }

    @Override
    protected void renderContent(UiGraphics graphics, float mouseX, float mouseY) {
        this.renderRow(graphics, 0, () -> {
            float line = graphics.scale().snapThickness(1.0F);
            graphics.fill(this.tabsArea.x(), this.tabsArea.bottom() - line, this.tabsArea.width(), line, ArkColors.BORDER_DEFAULT);
            for (ArkWidget widget : this.header) {
                widget.render(graphics, mouseX, mouseY);
            }
        });
        for (int index = 0; index < this.rows.size(); index++) {
            ArkWidget row = this.rows.get(index);
            this.renderRow(graphics, index + 1, () -> {
                row.render(graphics, mouseX, mouseY);
                if (row instanceof ServerRow serverRow) {
                    serverRow.renderActions(graphics, mouseX, mouseY);
                }
            });
        }
        if (this.rows.isEmpty()) {
            this.renderRow(graphics, 1, () -> this.renderEmpty(graphics));
        }
    }

    private void renderEmpty(UiGraphics graphics) {
        if (this.tab == Tab.LAN) {
            EmptyState.render(graphics, this.emptyArea, Component.translatable("arkea.servers.lan.scanning"),
                Component.translatable("arkea.servers.lan.hint"), true);
            return;
        }
        boolean searching = !this.searchQuery().isBlank() && this.servers.size() > 0;
        EmptyState.render(graphics, this.emptyArea, Component.translatable(searching ? "arkea.servers.empty.search" : "arkea.servers.empty.none"),
            Component.translatable(searching ? "arkea.servers.empty.search.hint" : "arkea.servers.empty.none.hint"), false);
    }

    private int indexOf(ServerData data) {
        for (int index = 0; index < this.servers.size(); index++) {
            if (this.servers.get(index) == data) {
                return index;
            }
        }
        return -1;
    }

    private void switchTab(Tab next) {
        if (this.tab != next) {
            this.tab = next;
            this.rebuild();
        }
    }

    private void select(ServerData data) {
        if (data != this.selectedServer) {
            this.selectedServer = data;
            this.rebuild();
        }
    }

    private void selectLan(LanServer server) {
        if (server != this.selectedLan) {
            this.selectedLan = server;
            this.rebuild();
        }
    }

    private void move(ServerData data, int offset) {
        int index = this.indexOf(data);
        int target = index + offset;
        if (index < 0 || target < 0 || target >= this.servers.size()) {
            return;
        }
        this.servers.swap(index, target);
        this.servers.save();
        this.rebuild();
    }

    private void copyAddress(ServerData data) {
        this.minecraft.keyboardHandler.setClipboard(data.ip);
        ArkToasts.show(ToastTone.SUCCESS, Component.translatable("arkea.servers.copied", data.ip));
    }

    private void refresh() {
        this.servers.load();
        this.selectedServer = null;
        this.rebuild();
    }

    void saveServer(@Nullable ServerData existing, String name, String address, ServerData.ServerPackStatus pack) {
        if (existing != null) {
            existing.name = name;
            existing.ip = address;
            existing.setState(ServerData.State.INITIAL);
            existing.setResourcePackStatus(pack);
            this.servers.save();
            ArkToasts.show(ToastTone.SUCCESS, Component.translatable("arkea.servers.saved", name));
            this.rebuild();
            return;
        }
        ServerData created = new ServerData(name, address, ServerData.Type.OTHER);
        created.setResourcePackStatus(pack);
        ServerData hidden = this.servers.unhide(address);
        if (hidden != null) {
            hidden.copyNameIconFrom(created);
            hidden.setResourcePackStatus(pack);
            created = hidden;
        } else {
            this.servers.add(created, false);
        }
        this.servers.save();
        this.selectedServer = created;
        this.tab = Tab.SERVERS;
        ArkToasts.show(ToastTone.SUCCESS, Component.translatable("arkea.servers.added", name));
        this.rebuild();
    }

    void removeServer(ServerData data) {
        this.servers.remove(data);
        this.servers.save();
        this.selectedServer = null;
        ArkToasts.show(ToastTone.SUCCESS, Component.translatable("arkea.servers.deleted", data.name));
        this.rebuild();
    }

    private void directConnect(String address) {
        lastDirectAddress = address;
        ServerData known = this.servers.get(address);
        if (known != null) {
            this.join(known);
            return;
        }
        ServerData created = new ServerData(Component.translatable("selectServer.defaultName").getString(), address, ServerData.Type.OTHER);
        this.servers.add(created, true);
        this.servers.save();
        this.join(created);
    }

    private void join(ServerData data) {
        JoinTarget.server(data);
        this.leave(() -> ConnectScreen.startConnecting(this, this.minecraft, ServerAddress.parseString(data.ip), data, false, null));
    }

    private void joinLan(LanServer server) {
        this.join(new ServerData(server.getMotd(), server.getAddress(), ServerData.Type.LAN));
    }

    private void joinSelected() {
        if (this.tab == Tab.SERVERS && this.selectedServer != null) {
            this.join(this.selectedServer);
        } else if (this.tab == Tab.LAN && this.selectedLan != null) {
            this.joinLan(this.selectedLan);
        }
    }

    @Override
    protected Component footerNote() {
        return Component.translatable("arkea.servers.note");
    }

    @Override
    protected List<ArkButton> footerButtons() {
        ArkButton direct = new ArkButton(this, Component.translatable("selectServer.direct"), ButtonVariant.SECONDARY,
            () -> this.dialogs.direct(lastDirectAddress, this::directConnect));
        ArkButton add = new ArkButton(this, Component.translatable("selectServer.add"), ButtonVariant.SECONDARY, this.dialogs::add);
        add.key("add");
        direct.key("direct");
        ArkButton join = new ArkButton(this, Component.translatable("selectServer.select"), ButtonVariant.PRIMARY, this::joinSelected);
        join.setActive(this.tab == Tab.SERVERS ? this.selectedServer != null : this.selectedLan != null);
        return List.of(direct, add, join);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.isInteractive() && event.key() == InputConstants.KEY_F5) {
            this.refresh();
            return true;
        }
        if (this.isInteractive() && this.tab == Tab.SERVERS && this.selectedServer != null && event.hasShiftDown() && (event.isUp() || event.isDown())) {
            this.move(this.selectedServer, event.isUp() ? -1 : 1);
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void tick() {
        super.tick();
        this.pings.tick();
        List<LanServer> found = this.lanList.takeDirtyServers();
        if (found != null) {
            this.lanServers = List.copyOf(found);
            this.rebuild();
        }
    }

    void showDialog(ArkDialog dialog) {
        this.openDialog(dialog);
    }

    void hideDialog() {
        this.closeDialog();
    }

    @Override
    public void removed() {
        if (this.lanDetector != null) {
            this.lanDetector.interrupt();
            this.lanDetector = null;
        }
        this.pings.close();
        this.icons.close();
        super.removed();
    }

    private enum Tab {
        SERVERS("arkea.servers.tab.servers"),
        LAN("arkea.servers.tab.lan");

        private final String key;

        Tab(String key) {
            this.key = key;
        }

        Component label() {
            return Component.translatable(this.key);
        }
    }
}

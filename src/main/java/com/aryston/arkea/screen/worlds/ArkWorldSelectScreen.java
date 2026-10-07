package com.aryston.arkea.screen.worlds;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.screen.loading.JoinTarget;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.overlay.ArkDialog;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.screen.ArkWindowScreen;
import com.aryston.arkea.ui.screen.NavGroup;
import com.aryston.arkea.ui.screen.SidebarBrand;
import com.aryston.arkea.ui.text.SearchText;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkButton;
import com.aryston.arkea.ui.widget.ArkIconButton;
import com.aryston.arkea.ui.widget.ArkTextField;
import com.aryston.arkea.ui.widget.ArkTextLink;
import com.aryston.arkea.ui.widget.ButtonVariant;
import com.aryston.arkea.ui.widget.IconButtonStyle;
import com.aryston.arkea.ui.widget.NavEntry;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelSummary;
import org.jspecify.annotations.Nullable;

public final class ArkWorldSelectScreen extends ArkWindowScreen {
    private static final float PANEL_GAP = 24.0F;
    private static final float LIST_HEADER = 18.0F;
    private static final float LIST_GAP = 8.0F;
    private static final float EMPTY_PADDING = 64.0F;
    private static final float EMPTY_LINE_GAP = 10.0F;
    private static final TextStyle LIST_LABEL = TextStyle.of(10.0F).spacing(1.0F);
    private static final TextStyle SORT = TextStyle.of(10.0F);
    private static final TextStyle EMPTY_TITLE = TextStyle.of(14.0F);
    private static final TextStyle EMPTY_TEXT = TextStyle.of(11.0F);

    private final WorldLibrary library;
    private final WorldOperations operations;
    private final WorldDialogs dialogs;
    private final List<WorldRow> rows = new ArrayList<>();
    private @Nullable WorldDetails details;
    private @Nullable String selectedId;
    private int shownVersion = -1;
    private Box listArea = Box.EMPTY;
    private float listTop;
    private @Nullable ArkTextLink sortLink;

    public ArkWorldSelectScreen(Screen lastScreen) {
        super(Component.translatable("menu.singleplayer"), lastScreen);
        this.library = new WorldLibrary(this.minecraft);
        this.operations = new WorldOperations(this.minecraft, this, this.library::load);
        this.dialogs = new WorldDialogs(this, this.operations, this.library);
    }

    @Override
    public void added() {
        super.added();
        this.library.load();
    }

    @Override
    protected Component crumb() {
        return Component.translatable("arkea.worlds.crumb");
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
        return Component.translatable("arkea.worlds.search");
    }

    private List<LevelSummary> visibleWorlds() {
        String query = SearchText.normalize(this.searchQuery());
        return this.library.worlds().stream()
            .filter(summary -> SearchText.matches(query, summary.getLevelName(), summary.getLevelId()))
            .sorted(ArkeaConfig.WORLD_SORT.get().order())
            .toList();
    }

    private @Nullable LevelSummary selected(List<LevelSummary> worlds) {
        for (LevelSummary summary : worlds) {
            if (summary.getLevelId().equals(this.selectedId)) {
                return summary;
            }
        }
        return worlds.isEmpty() ? null : worlds.getFirst();
    }

    @Override
    protected float buildContent(Box area) {
        this.shownVersion = this.library.version();
        this.rows.clear();
        List<LevelSummary> worlds = this.visibleWorlds();
        LevelSummary selected = this.selected(worlds);
        this.selectedId = selected != null ? selected.getLevelId() : this.selectedId;
        float listWidth = area.width() - WorldDetails.WIDTH - PANEL_GAP;
        this.listArea = new Box(area.x(), area.y(), listWidth, LIST_HEADER);
        Component sortLabel = ArkeaConfig.WORLD_SORT.get().label();
        ArkTextLink sort = this.add(new ArkTextLink(this, sortLabel, SORT, ArkColors.TEXT_LABEL, this::cycleSort));
        sort.key("sort");
        sort.setTooltip(Component.translatable("arkea.worlds.sort.tooltip"));
        float sortWidth = sort.preferredWidth();
        sort.setBounds(new Box(area.x() + listWidth - sortWidth, area.y() + (LIST_HEADER - sort.preferredHeight()) * 0.5F, sortWidth,
            sort.preferredHeight()));
        this.sortLink = sort;
        float y = area.y() + LIST_HEADER + LIST_GAP;
        this.listTop = y;
        for (LevelSummary summary : worlds) {
            String id = summary.getLevelId();
            WorldRow row = this.add(new WorldRow(this, summary, new WorldImage(this.library, summary), () -> id.equals(this.selectedId),
                () -> this.select(id), () -> this.play(summary)));
            row.key("world:" + id);
            row.setBounds(new Box(area.x(), y, listWidth, WorldRow.HEIGHT));
            this.rows.add(row);
            y += WorldRow.HEIGHT + LIST_GAP;
        }
        float bottom = worlds.isEmpty() ? this.listTop + EMPTY_PADDING * 2.0F : y - LIST_GAP;
        this.details = selected != null ? this.buildDetails(selected) : null;
        if (this.details != null) {
            bottom = Math.max(bottom, this.details.layout(area.right() - WorldDetails.WIDTH, area.y()));
        }
        return bottom;
    }

    private WorldDetails buildDetails(LevelSummary summary) {
        ArkButton play = this.add(new ArkButton(this, summary.primaryActionMessage(), ButtonVariant.PRIMARY, () -> this.play(summary)).icon(Icons.PLAY));
        play.key("details:play");
        play.setActive(summary.primaryActionActive());
        ArkButton edit = this.add(new ArkButton(this, Component.translatable("selectWorld.edit"), ButtonVariant.SUBTLE, () -> this.dialogs.edit(summary)));
        edit.key("details:edit");
        edit.setActive(summary.canEdit());
        if (summary.requiresFileFixing()) {
            edit.setTooltip(Component.translatable("selectWorld.requiresFileFixingTooltip.edit"));
        }
        ArkButton recreate = this.add(new ArkButton(this, Component.translatable("selectWorld.recreate"), ButtonVariant.SUBTLE,
            () -> this.operations.recreate(summary)));
        recreate.key("details:recreate");
        recreate.setActive(summary.canRecreate());
        if (summary.requiresFileFixing()) {
            recreate.setTooltip(Component.translatable("selectWorld.requiresFileFixingTooltip.recreate"));
        }
        Component deleteLabel = Component.translatable("arkea.worlds.delete.tooltip", summary.getLevelName());
        ArkIconButton delete = this.add(new ArkIconButton(this, Icons.TRASH, deleteLabel, IconButtonStyle.DANGER, () -> this.dialogs.delete(summary)));
        delete.key("details:delete");
        delete.setTooltip(deleteLabel);
        delete.setActive(summary.canDelete());
        return new WorldDetails(summary, new WorldImage(this.library, summary), () -> this.library.facts(summary), play, edit, recreate, delete);
    }

    @Override
    protected void renderContent(UiGraphics graphics, float mouseX, float mouseY) {
        TextMetrics metrics = graphics.metrics();
        float labelY = this.listArea.y() + (LIST_HEADER - metrics.capHeight(LIST_LABEL)) * 0.5F;
        this.renderRow(graphics, 0, () -> {
            graphics.text(this.countLabel().getString(), this.listArea.x(), labelY, LIST_LABEL, ArkColors.TEXT_LABEL);
            if (this.sortLink != null) {
                this.sortLink.render(graphics, mouseX, mouseY);
            }
        });
        for (int index = 0; index < this.rows.size(); index++) {
            WorldRow row = this.rows.get(index);
            this.renderRow(graphics, index + 1, () -> row.render(graphics, mouseX, mouseY));
        }
        if (this.rows.isEmpty()) {
            this.renderRow(graphics, 1, () -> this.renderEmpty(graphics));
        }
        WorldDetails shown = this.details;
        if (shown != null) {
            this.renderRow(graphics, 1, () -> shown.render(graphics, mouseX, mouseY));
        }
    }

    private Component countLabel() {
        if (this.library.isLoading()) {
            return Component.translatable("arkea.worlds.loading");
        }
        return Component.translatable("arkea.worlds.count", this.visibleWorlds().size());
    }

    private void renderEmpty(UiGraphics graphics) {
        if (this.library.isLoading()) {
            return;
        }
        TextMetrics metrics = graphics.metrics();
        boolean searching = !this.searchQuery().isBlank() && !this.library.worlds().isEmpty();
        Component error = this.library.error();
        Component title = error != null ? error : Component.translatable(searching ? "arkea.worlds.empty.search" : "arkea.worlds.empty.none");
        Component text = Component.translatable(searching ? "arkea.worlds.empty.search.hint" : "arkea.worlds.empty.none.hint");
        float centerX = this.listArea.centerX();
        float y = this.listTop + EMPTY_PADDING;
        String titleText = title.getString();
        graphics.text(titleText, centerX - metrics.width(titleText, EMPTY_TITLE) * 0.5F, y, EMPTY_TITLE, ArkColors.TEXT_PRIMARY);
        String hint = text.getString();
        graphics.text(hint, centerX - metrics.width(hint, EMPTY_TEXT) * 0.5F, y + metrics.capHeight(EMPTY_TITLE) + EMPTY_LINE_GAP, EMPTY_TEXT,
            ArkColors.TEXT_FAINT);
    }

    private void select(String levelId) {
        if (levelId.equals(this.selectedId)) {
            return;
        }
        this.selectedId = levelId;
        this.rebuild();
    }

    private void cycleSort() {
        ArkeaConfig.set(ArkeaConfig.WORLD_SORT, ArkeaConfig.WORLD_SORT.get().next());
        this.rebuild();
    }

    private void play(LevelSummary summary) {
        if (!summary.primaryActionActive()) {
            return;
        }
        JoinTarget.set(summary.getLevelName());
        this.leave(() -> this.operations.play(summary));
    }

    private @Nullable LevelSummary selectedSummary() {
        return this.selected(this.visibleWorlds());
    }

    @Override
    protected Component footerNote() {
        if (this.library.isLoading()) {
            return Component.translatable("arkea.worlds.loading");
        }
        return Component.translatable("arkea.worlds.note", this.library.worlds().size());
    }

    @Override
    protected List<ArkButton> footerButtons() {
        ArkButton create = new ArkButton(this, Component.translatable("selectWorld.create"), ButtonVariant.SECONDARY,
            () -> this.leave(this.operations::createNew));
        LevelSummary selected = this.selectedSummary();
        ArkButton play = new ArkButton(this, Component.translatable("selectWorld.select"), ButtonVariant.PRIMARY, () -> {
            LevelSummary current = this.selectedSummary();
            if (current != null) {
                this.play(current);
            }
        });
        play.setActive(selected != null && selected.primaryActionActive());
        return List.of(create, play);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.isInteractive() && event.key() == InputConstants.KEY_F5) {
            this.library.load();
            return true;
        }
        if (this.isInteractive() && event.key() == InputConstants.KEY_DELETE && !(this.getFocused() instanceof ArkTextField)) {
            LevelSummary selected = this.selectedSummary();
            if (selected != null && selected.canDelete()) {
                this.dialogs.delete(selected);
                return true;
            }
        }
        return super.keyPressed(event);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.library.version() != this.shownVersion) {
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
        this.library.close();
        super.removed();
    }
}

package com.aryston.arkea.screen.worlds;

import com.aryston.arkea.mixin.CreateWorldScreenInvoker;
import com.aryston.arkea.screen.loading.JoinTarget;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.overlay.TooltipHint;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.screen.ArkWindowScreen;
import com.aryston.arkea.ui.screen.NavGroup;
import com.aryston.arkea.ui.screen.SettingsPanel;
import com.aryston.arkea.ui.screen.SettingsSection;
import com.aryston.arkea.ui.screen.SidebarBrand;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkButton;
import com.aryston.arkea.ui.widget.ArkCycle;
import com.aryston.arkea.ui.widget.ArkSegmented;
import com.aryston.arkea.ui.widget.ArkSwitch;
import com.aryston.arkea.ui.widget.ArkTab;
import com.aryston.arkea.ui.widget.ArkTextField;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.ButtonVariant;
import com.aryston.arkea.ui.widget.ItemContent;
import com.aryston.arkea.ui.widget.ListCycleModel;
import com.aryston.arkea.ui.widget.NavEntry;
import com.aryston.arkea.ui.widget.SettingRow;
import com.aryston.arkea.ui.widget.TextFieldState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.client.gui.screens.worldselection.WorldCreationGameRulesScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import org.jspecify.annotations.Nullable;

public final class ArkCreateWorldScreen extends ArkWindowScreen {
    private static final Map<CreateWorldScreen, CreateTab> TABS = new WeakHashMap<>();
    private static final float TAB_GAP = 28.0F;
    private static final float TABS_TO_CONTENT = 24.0F;
    private static final float FIELD_WIDTH = 220.0F;
    private static final int NAME_LENGTH = 32;
    private static final int SEED_LENGTH = 64;
    private static final List<WorldCreationUiState.SelectedGameMode> MODES = List.of(WorldCreationUiState.SelectedGameMode.SURVIVAL,
        WorldCreationUiState.SelectedGameMode.HARDCORE, WorldCreationUiState.SelectedGameMode.CREATIVE);

    private final CreateWorldScreen vanilla;
    private final WorldCreationUiState state;
    private final TextFieldState name = new TextFieldState(NAME_LENGTH);
    private final TextFieldState seed = new TextFieldState(SEED_LENGTH);
    private final List<ArkWidget> tabs = new ArrayList<>();
    private SettingsPanel panel = new SettingsPanel();
    private Box tabsArea = Box.EMPTY;

    public ArkCreateWorldScreen(CreateWorldScreen vanilla) {
        super(Component.translatable("selectWorld.create"), vanilla);
        this.vanilla = vanilla;
        this.state = vanilla.getUiState();
        this.name.setText(this.state.getName());
        this.seed.setText(this.state.getSeed());
    }

    private CreateTab tab() {
        return TABS.getOrDefault(this.vanilla, CreateTab.GAME);
    }

    @Override
    protected Component crumb() {
        return Component.translatable("arkea.create.crumb");
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
    protected float buildContent(Box area) {
        this.tabs.clear();
        this.panel = new SettingsPanel();
        this.tabsArea = new Box(area.x(), area.y(), area.width(), ArkTab.HEIGHT);
        float x = area.x();
        for (CreateTab each : CreateTab.values()) {
            ArkTab widget = this.add(new ArkTab(this, each.label(), null, () -> this.tab() == each, () -> this.switchTab(each)).icon(each.icon()));
            widget.key("tab:" + each.name());
            widget.setBounds(new Box(x, area.y(), widget.preferredWidth(), ArkTab.HEIGHT));
            this.tabs.add(widget);
            x += widget.preferredWidth() + TAB_GAP;
        }
        switch (this.tab()) {
            case GAME -> this.addGame();
            case WORLD -> this.addWorld();
            case MORE -> this.addMore();
        }
        return this.panel.layout(area, area.y() + ArkTab.HEIGHT + TABS_TO_CONTENT);
    }

    private void addGame() {
        SettingsSection section = this.panel.section(Component.translatable("arkea.create.section.world"), true).columns(2);
        ArkTextField nameField = this.add(new ArkTextField(this, Component.translatable("selectWorld.newWorld"), this.name, this.state::setName).icon(null));
        nameField.key("name");
        nameField.setTooltip(Component.translatable("selectWorld.targetFolder", this.state.getTargetFolder()));
        section.add(new SettingRow(this.content(Icons.WORLD, "selectWorld.enterName", "arkea.create.name.description")), nameField, FIELD_WIDTH,
            SettingsPanel.CONTROL_HEIGHT);
        List<Component> modes = MODES.stream().map(mode -> mode.displayName).toList();
        ArkSegmented mode = this.add(new ArkSegmented(this, Component.translatable("selectWorld.gameMode"), modes,
            () -> Math.max(0, MODES.indexOf(this.state.getGameMode())), index -> {
                this.state.setGameMode(MODES.get(index));
                this.rebuild();
            }));
        mode.key("mode");
        mode.setActive(!this.state.isDebug());
        section.add(new SettingRow(new ItemContent(Icons.USER, Component.translatable("selectWorld.gameMode"), this.state.getGameMode().getInfo())), mode,
            mode.preferredWidth(), ArkSegmented.HEIGHT);
        ArkCycle difficulty = this.add(new ArkCycle(this, Component.translatable("options.difficulty"), new ListCycleModel<>(
            () -> Arrays.asList(Difficulty.values()), this.state::getDifficulty, this.state::setDifficulty, Difficulty::getDisplayName)));
        difficulty.key("difficulty");
        difficulty.setActive(!this.state.isHardcore());
        section.add(new SettingRow(this.content(Icons.SWORD, "options.difficulty", "arkea.create.difficulty.description"))
            .tooltip(() -> this.state.getDifficulty().getInfo()), difficulty, ArkCycle.WIDTH, ArkCycle.HEIGHT);
        this.toggle(section, this.content(Icons.CMD, "selectWorld.allowCommands", "arkea.create.commands.description"), this.state::isAllowCommands,
            this.state::setAllowCommands, !this.state.isHardcore() && !this.state.isDebug());
    }

    private void addWorld() {
        SettingsSection section = this.panel.section(Component.translatable("arkea.create.section.generation"), true).columns(2);
        ArkCycle type = this.add(new ArkCycle(this, Component.translatable("selectWorld.mapType"), new ListCycleModel<>(this.state::getNormalPresetList,
            this.state::getWorldType, entry -> {
                this.state.setWorldType(entry);
                this.rebuild();
            }, WorldCreationUiState.WorldTypeEntry::describePreset)));
        type.key("type");
        type.setActive(this.state.getWorldType().preset() != null);
        SettingRow typeRow = new SettingRow(this.content(Icons.LAYERS, "selectWorld.mapType", "arkea.create.type.description"));
        if (this.state.getWorldType().isAmplified()) {
            typeRow.tooltip(() -> Component.translatable("generator.minecraft.amplified.info"));
        }
        section.add(typeRow, type, ArkCycle.WIDTH, ArkCycle.HEIGHT);
        ArkTextField seedField = this.add(new ArkTextField(this, Component.translatable("arkea.create.seed.hint"), this.seed, this.state::setSeed).icon(null));
        seedField.key("seed");
        section.add(new SettingRow(this.content(Icons.SEED, "arkea.create.seed", "arkea.create.seed.description")), seedField, FIELD_WIDTH,
            SettingsPanel.CONTROL_HEIGHT);
        this.toggle(section, this.content(Icons.HOUSE, "selectWorld.mapFeatures", "arkea.create.structures.description"), this.state::isGenerateStructures,
            this.state::setGenerateStructures, !this.state.isDebug());
        this.toggle(section, this.content(Icons.CHEST, "selectWorld.bonusItems", "arkea.create.bonus.description"), this.state::isBonusChest,
            this.state::setBonusChest, !this.state.isHardcore() && !this.state.isDebug());
        PresetEditor editor = this.state.getPresetEditor();
        if (editor != null && !this.state.isDebug()) {
            this.link(section, this.content(Icons.SLIDERS, "selectWorld.customizeType", "arkea.create.customize.description"), "selectWorld.customizeType",
                () -> this.minecraft.gui.setScreen(editor.createEditScreen(this.vanilla, this.state.getSettings())));
        }
    }

    private void addMore() {
        SettingsSection section = this.panel.section(Component.translatable("arkea.create.section.advanced"), true).columns(2);
        this.link(section, this.content(Icons.RULES, "selectWorld.gameRules", "arkea.create.rules.description"), "arkea.create.rules.button",
            this::openGameRules);
        this.link(section, this.content(Icons.PACK, "selectWorld.dataPacks", "arkea.create.packs.description"), "arkea.create.packs.button",
            () -> this.invoker().arkea$openDataPacks(this.state.getSettings().dataConfiguration()));
        this.link(section, this.content(Icons.FLASK, "selectWorld.experiments", "arkea.create.experiments.description"), "arkea.create.experiments.button",
            () -> this.invoker().arkea$openExperiments(this.state.getSettings().dataConfiguration()));
    }

    private ItemContent content(Icon icon, String nameKey, String descriptionKey) {
        return new ItemContent(icon, Component.translatable(nameKey), Component.translatable(descriptionKey));
    }

    private void toggle(SettingsSection section, ItemContent content, BooleanSupplier getter, Consumer<Boolean> setter, boolean active) {
        ArkSwitch widget = this.add(new ArkSwitch(this, content.label(), getter, setter));
        widget.key("switch:" + content.label().getString());
        widget.setActive(active);
        section.add(new SettingRow(content).litWhen(getter), widget, ArkSwitch.WIDTH, ArkSwitch.HEIGHT);
    }

    private void link(SettingsSection section, ItemContent content, String labelKey, Runnable action) {
        ArkButton button = this.add(new ArkButton(this, Component.translatable(labelKey), ButtonVariant.SUBTLE, action).trailingIcon(Icons.CHEVRON_RIGHT));
        button.key("link:" + labelKey);
        section.add(new SettingRow(content), button, button.preferredWidth(), SettingsPanel.CONTROL_HEIGHT);
    }

    private void openGameRules() {
        this.minecraft.gui.setScreen(new WorldCreationGameRulesScreen(
            this.state.getGameRules().copy(this.state.getSettings().dataConfiguration().enabledFeatures()), rules -> {
                this.minecraft.gui.setScreen(this.vanilla);
                rules.ifPresent(this.state::setGameRules);
            }));
    }

    private CreateWorldScreenInvoker invoker() {
        return (CreateWorldScreenInvoker) this.vanilla;
    }

    private void switchTab(CreateTab next) {
        if (this.tab() != next) {
            TABS.put(this.vanilla, next);
            this.rebuild();
        }
    }

    @Override
    protected void renderContent(UiGraphics graphics, float mouseX, float mouseY) {
        this.renderRow(graphics, 0, () -> {
            float line = graphics.scale().snapThickness(1.0F);
            graphics.fill(this.tabsArea.x(), this.tabsArea.bottom() - line, this.tabsArea.width(), line, ArkColors.BORDER_DEFAULT);
            for (ArkWidget widget : this.tabs) {
                widget.render(graphics, mouseX, mouseY);
            }
        });
        this.panel.render(graphics, mouseX, mouseY, (row, draw) -> this.renderRow(graphics, row, draw), 1);
    }

    @Override
    protected @Nullable TooltipHint hoveredHint() {
        TooltipHint widgetHint = super.hoveredHint();
        return widgetHint != null ? widgetHint : this.panel.hoveredHint();
    }

    @Override
    protected Box revealBox(ArkWidget widget) {
        Box row = this.panel.rowOf(widget);
        return row != null ? row : widget.bounds();
    }

    @Override
    protected Component footerNote() {
        return Component.translatable("arkea.create.note");
    }

    @Override
    protected List<ArkButton> footerButtons() {
        ArkButton cancel = new ArkButton(this, CommonComponents.GUI_CANCEL, ButtonVariant.GHOST, this::onClose);
        ArkButton create = new ArkButton(this, Component.translatable("selectWorld.create"), ButtonVariant.PRIMARY, this::create);
        create.key("create");
        return List.of(cancel, create);
    }

    private void create() {
        JoinTarget.set(this.state.getName());
        this.leave(() -> this.invoker().arkea$create());
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (super.keyPressed(event)) {
            return true;
        }
        if (this.isInteractive() && event.isConfirmation()) {
            this.create();
            return true;
        }
        return false;
    }

    @Override
    public void onClose() {
        this.leave(this.vanilla::popScreen);
    }

    private enum CreateTab {
        GAME(Icons.USER),
        WORLD(Icons.GLOBE),
        MORE(Icons.SLIDERS);

        private final Icon icon;

        CreateTab(Icon icon) {
            this.icon = icon;
        }

        Icon icon() {
            return this.icon;
        }

        Component label() {
            return Component.translatable("arkea.create.tab." + this.name().toLowerCase(Locale.ROOT));
        }
    }
}

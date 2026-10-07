package com.aryston.arkea.screen.options;

import com.aryston.arkea.screen.options.control.OptionControl;
import com.aryston.arkea.screen.options.control.OptionControls;
import com.aryston.arkea.screen.options.control.OptionText;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.overlay.ArkDialog;
import com.aryston.arkea.ui.overlay.DialogContent;
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
import com.aryston.arkea.ui.widget.ArkBanner;
import com.aryston.arkea.ui.widget.ArkButton;
import com.aryston.arkea.ui.widget.ArkSwitch;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.ButtonVariant;
import com.aryston.arkea.ui.widget.ItemContent;
import com.aryston.arkea.ui.widget.NavEntry;
import com.aryston.arkea.ui.widget.SettingRow;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public abstract class OptionsPageScreen extends ArkWindowScreen {
    private static final float BANNER_GAP = 24.0F;
    private static final float RESET_DIALOG_WIDTH = 440.0F;

    protected final Options options;
    private final OptionsPage page;
    private final List<Runnable> resets = new ArrayList<>();
    private SettingsPanel panel = new SettingsPanel();
    private @Nullable ArkBanner banner;

    protected OptionsPageScreen(OptionsPage page, Screen lastScreen) {
        super(page.title(), lastScreen);
        this.page = page;
        this.options = this.minecraft.options;
    }

    protected abstract void addSettings(SettingsPanel settings);

    protected @Nullable ArkBanner banner() {
        return null;
    }

    @Override
    protected Component crumb() {
        return Component.translatable("arkea.options.crumb." + this.page.key());
    }

    @Override
    protected SidebarBrand brand() {
        return OptionsNavigation.brand();
    }

    @Override
    protected List<NavGroup> navigation() {
        return OptionsNavigation.groups();
    }

    @Override
    protected String currentNav() {
        return this.page.id();
    }

    @Override
    protected void openNav(NavEntry entry) {
        OptionsPage target = OptionsPage.valueOf(entry.id());
        if (target == this.page) {
            return;
        }
        if (target == OptionsPage.OVERVIEW) {
            this.switchTo(this::overview);
        } else if (target.isWindow()) {
            this.switchTo(() -> target.open(this.pageParent(), this.minecraft));
        } else {
            this.navigate(() -> target.open(this, this.minecraft));
        }
    }

    private Screen overview() {
        if (this.lastScreen instanceof OptionsPageScreen parent) {
            return parent.overview();
        }
        return this.lastScreen instanceof ArkOptionsScreen ? this.lastScreen : new ArkOptionsScreen(this.lastScreen);
    }

    private Screen pageParent() {
        return this.lastScreen instanceof OptionsPageScreen parent ? parent.pageParent() : this.lastScreen;
    }

    @Override
    protected float buildContent(Box area) {
        this.panel = new SettingsPanel();
        this.resets.clear();
        float y = area.y();
        this.banner = this.banner();
        if (this.banner != null) {
            this.add(this.banner).setBounds(new Box(area.x(), y, area.width(), ArkBanner.HEIGHT));
            y += ArkBanner.HEIGHT + BANNER_GAP;
        }
        this.addSettings(this.panel);
        return this.panel.layout(area, y);
    }

    @Override
    protected void renderContent(UiGraphics graphics, float mouseX, float mouseY) {
        int index = 0;
        ArkBanner shown = this.banner;
        if (shown != null) {
            this.renderRow(graphics, index++, () -> shown.render(graphics, mouseX, mouseY));
        }
        this.panel.render(graphics, mouseX, mouseY, (row, draw) -> this.renderRow(graphics, row, draw), index);
    }

    protected ArkWidget option(SettingsSection section, OptionInstance<?> option, Icon icon) {
        return this.option(section, option, icon, OptionControls.create(this, option));
    }

    protected ArkWidget dropdown(SettingsSection section, OptionInstance<?> option, Icon icon) {
        return this.option(section, option, icon, OptionControls.dropdown(this, option));
    }

    protected ArkWidget option(SettingsSection section, OptionInstance<?> option, Icon icon, OptionControl control) {
        SettingRow row = new SettingRow(new ItemContent(icon, OptionText.name(option.caption), OptionText.description(option.caption)))
            .litWhen(control.lit())
            .tooltip(() -> OptionText.tooltip(option));
        section.add(row, this.add(control.widget()), control.width(), control.height());
        this.resets.add(control.reset());
        if (option == this.options.narrator()) {
            control.widget().setActive(this.minecraft.getNarrator().isActive());
        }
        return control.widget();
    }

    protected ArkSwitch toggle(SettingsSection section, ItemContent content, BooleanSupplier getter, Consumer<Boolean> setter, boolean defaultValue) {
        ArkSwitch widget = this.add(new ArkSwitch(this, content.label(), getter, setter));
        section.add(new SettingRow(content).litWhen(getter), widget, ArkSwitch.WIDTH, ArkSwitch.HEIGHT);
        this.resets.add(() -> {
            if (getter.getAsBoolean() != defaultValue) {
                setter.accept(defaultValue);
            }
        });
        return widget;
    }

    protected ArkButton link(SettingsSection section, ItemContent content, Component label, Runnable action) {
        ArkButton button = this.add(new ArkButton(this, label, ButtonVariant.SUBTLE, action).trailingIcon(Icons.CHEVRON_RIGHT));
        section.add(new SettingRow(content), button, button.preferredWidth(), SettingsPanel.CONTROL_HEIGHT);
        return button;
    }

    protected void openPage(OptionsPage target, Supplier<Screen> screen) {
        if (target.isWindow()) {
            this.switchTo(screen);
        } else {
            this.navigate(screen);
        }
    }

    protected Component section(String key) {
        return Component.translatable("arkea.options.section." + key);
    }

    @Override
    protected Component footerNote() {
        return Component.translatable("arkea.options.saved");
    }

    @Override
    protected List<ArkButton> footerButtons() {
        ArkButton reset = new ArkButton(this, this.resetLabel(), ButtonVariant.SECONDARY, this::confirmReset);
        reset.setActive(this.canReset());
        return List.of(reset, new ArkButton(this, CommonComponents.GUI_DONE, ButtonVariant.PRIMARY, this::onClose));
    }

    protected Component resetLabel() {
        return Component.translatable("arkea.options.reset");
    }

    protected boolean canReset() {
        return true;
    }

    private void confirmReset() {
        DialogContent content = new DialogContent(Component.translatable("arkea.options.reset.title", this.title),
            Component.translatable("arkea.options.reset.message"), Icons.RESET, ArkColors.WARNING);
        ArkDialog dialog = new ArkDialog(this, content, RESET_DIALOG_WIDTH, this::closeDialog);
        dialog.button(new ArkButton(this, CommonComponents.GUI_CANCEL, ButtonVariant.SUBTLE, this::closeDialog));
        dialog.button(new ArkButton(this, this.resetLabel(), ButtonVariant.DANGER, () -> {
            this.resetDefaults();
            this.closeDialog();
        }));
        this.openDialog(dialog);
    }

    protected void resetDefaults() {
        for (Runnable reset : this.resets) {
            reset.run();
        }
        this.options.save();
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
    public void removed() {
        this.options.save();
        super.removed();
    }
}

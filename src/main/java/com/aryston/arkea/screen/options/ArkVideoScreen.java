package com.aryston.arkea.screen.options;

import com.aryston.arkea.screen.options.control.OptionControl;
import com.aryston.arkea.screen.options.control.OptionControls;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.screen.SettingsPanel;
import com.aryston.arkea.ui.screen.SettingsSection;
import com.aryston.arkea.ui.widget.ArkBanner;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.MacosUtil;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.TextureFilteringMethod;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.UnsupportedGraphicsWarningScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public final class ArkVideoScreen extends OptionsPageScreen {
    private static final Identifier HELION_PREVIEW = Identifier.fromNamespaceAndPath("arkea", "textures/gui/helion_preview.png");
    private static final Identifier HELION_LOGO = Identifier.fromNamespaceAndPath("arkea", "textures/gui/helion_logo.png");
    private static final ArkBanner.Banner.Image PREVIEW_CROP = new ArkBanner.Banner.Image(HELION_PREVIEW, 0.029F, 0.0F, 0.993F, 1.0F);
    private static final float MODE_WIDTH = 200.0F;
    private static final int CURRENT_MODE = -1;
    private static final Component IMPROVED_TRANSPARENCY = Component.translatable("options.improvedTransparency").withStyle(ChatFormatting.ITALIC);

    private final GpuWarnlistManager warnlist;
    private int oldMipmaps;
    private int oldAnisotropy;
    private TextureFilteringMethod oldFiltering;
    private @Nullable ArkWidget anisotropy;

    public ArkVideoScreen(Screen lastScreen) {
        super(OptionsPage.VIDEO, lastScreen);
        this.warnlist = this.minecraft.getGpuWarnlistManager();
        this.warnlist.resetWarnings();
        this.rememberTextureSettings();
    }

    private void rememberTextureSettings() {
        this.oldMipmaps = this.options.mipmapLevels().get();
        this.oldAnisotropy = this.options.maxAnisotropyBit().get();
        this.oldFiltering = this.options.textureFiltering().get();
    }

    @Override
    protected @Nullable ArkBanner banner() {
        if (!OptionsPage.HELION.isAvailable()) {
            return null;
        }
        ArkBanner.Banner banner = new ArkBanner.Banner(PREVIEW_CROP, HELION_LOGO, Component.translatable("arkea.options.helion.banner.title"),
            Component.translatable("arkea.options.helion.banner.subtitle"), Component.translatable("arkea.options.helion.banner.action"));
        return new ArkBanner(this, banner, () -> this.navigate(() -> OptionsPage.HELION.open(this, this.minecraft)));
    }

    @Override
    protected void addSettings(SettingsPanel settings) {
        Options options = this.options;
        SettingsSection display = settings.section(this.section("display"), false);
        this.option(display, options.fullscreen(), Icons.EXPAND);
        this.option(display, options.exclusiveFullscreen(), Icons.MONITOR);
        this.addFullscreenMode(display);
        this.option(display, options.framerateLimit(), Icons.GAUGE);
        this.option(display, options.enableVsync(), Icons.VSYNC);
        this.option(display, options.inactivityFpsLimit(), Icons.CLOCK);
        this.option(display, options.guiScale(), Icons.UISCALE);
        this.option(display, options.gamma(), Icons.SUN);
        this.option(display, options.preferredGraphicsBackend(), Icons.CPU);
        if (MacosUtil.IS_MACOS) {
            this.option(display, options.macFullscreenMenuVisibility(), Icons.EXPAND);
        }
        SettingsSection world = settings.section(this.section("world"), false);
        this.option(world, options.renderDistance(), Icons.CHUNKS);
        this.option(world, options.simulationDistance(), Icons.CLOCK);
        this.option(world, options.entityDistanceScaling(), Icons.ENTITY);
        this.option(world, options.biomeBlendRadius(), Icons.BLEND);
        this.option(world, options.cloudStatus(), Icons.CLOUD);
        this.option(world, options.cloudRange(), Icons.CLOUD);
        this.option(world, options.particles(), Icons.PARTICLES);
        this.option(world, options.weatherRadius(), Icons.RAIN);
        this.option(world, options.prioritizeChunkUpdates(), Icons.CPU);
        SettingsSection quality = settings.section(this.section("quality"), false);
        this.option(quality, options.graphicsPreset(), Icons.PLANE);
        this.option(quality, options.ambientOcclusion(), Icons.BULB);
        this.option(quality, options.mipmapLevels(), Icons.MIPMAP);
        this.option(quality, options.entityShadows(), Icons.SHADOW);
        this.option(quality, options.cutoutLeaves(), Icons.LEAF);
        this.option(quality, options.improvedTransparency(), Icons.LAYERS);
        this.option(quality, options.textureFiltering(), Icons.FILTER);
        this.anisotropy = this.option(quality, options.maxAnisotropyBit(), Icons.FILTER);
        this.option(quality, options.menuBackgroundBlurriness(), Icons.BLUR);
        SettingsSection preferences = settings.section(this.section("preferences"), false);
        this.option(preferences, options.showAutosaveIndicator(), Icons.CHEST);
        this.option(preferences, options.vignette(), Icons.EYE);
        this.option(preferences, options.attackIndicator(), Icons.CROSS);
        this.option(preferences, options.chunkSectionFadeInTime(), Icons.WAVES);
        this.updateAnisotropy();
    }

    private void addFullscreenMode(SettingsSection section) {
        Window window = this.minecraft.getWindow();
        Monitor monitor = window.findBestMonitor();
        int initial = monitor == null ? CURRENT_MODE : window.getPreferredFullscreenVideoMode().map(monitor::indexOfMode).orElse(CURRENT_MODE);
        int last = monitor != null ? monitor.modeCount() - 1 : CURRENT_MODE;
        OptionInstance<Integer> mode = new OptionInstance<>(
            "options.fullscreen.exclusive.mode",
            OptionInstance.cachedConstantTooltip(Component.translatable("options.fullscreen.exclusive.mode.tooltip")),
            (caption, value) -> Options.genericValueLabel(caption, modeLabel(monitor, value)),
            new OptionInstance.IntRange(CURRENT_MODE, last),
            initial,
            value -> {
                if (monitor != null) {
                    window.setPreferredFullscreenVideoMode(value == CURRENT_MODE ? Optional.empty() : Optional.of(monitor.mode(value)));
                }
            });
        List<Integer> modes = IntStream.rangeClosed(CURRENT_MODE, last).boxed().toList();
        OptionControl control = OptionControls.dropdown(this, mode, () -> modes, mode::set, () -> mode.set(CURRENT_MODE));
        this.option(section, mode, Icons.MONITOR, new OptionControl(control.widget(), MODE_WIDTH, control.height(), control.lit(), control.reset()))
            .setActive(monitor != null);
    }

    private static Component modeLabel(@Nullable Monitor monitor, int value) {
        if (monitor == null) {
            return Component.translatable("options.fullscreen.unavailable");
        }
        if (value == CURRENT_MODE) {
            return Component.translatable("options.fullscreen.current");
        }
        VideoMode mode = monitor.mode(value);
        return Component.translatable("arkea.option.fullscreen.mode.entry", mode.getWidth(), mode.getHeight(), mode.refreshRateLabel());
    }

    private void updateAnisotropy() {
        if (this.anisotropy != null) {
            this.anisotropy.setActive(this.options.textureFiltering().get() == TextureFilteringMethod.ANISOTROPIC);
        }
    }

    @Override
    public void tick() {
        this.updateAnisotropy();
        super.tick();
    }

    @Override
    protected Component footerNote() {
        if (this.options.isRestartRequiredToApplyVideoSettings()) {
            return Component.translatable("arkea.options.restart");
        }
        return super.footerNote();
    }

    @Override
    protected void resetDefaults() {
        super.resetDefaults();
        this.options.graphicsPreset().set(OptionControls.initialValue(this.options.graphicsPreset()));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!super.mouseClicked(event, doubleClick)) {
            return false;
        }
        if (this.warnlist.isShowingWarning()) {
            this.showGraphicsWarning();
        }
        return true;
    }

    private void showGraphicsWarning() {
        List<Component> message = new ArrayList<>(List.of(Component.translatable("options.graphics.warning.message", IMPROVED_TRANSPARENCY, IMPROVED_TRANSPARENCY),
            CommonComponents.NEW_LINE));
        addWarning(message, "options.graphics.warning.renderer", this.warnlist.getRendererWarnings());
        addWarning(message, "options.graphics.warning.vendor", this.warnlist.getVendorWarnings());
        addWarning(message, "options.graphics.warning.version", this.warnlist.getVersionWarnings());
        Component title = Component.translatable("options.graphics.warning.title").withStyle(ChatFormatting.RED);
        this.minecraft.gui.setScreen(new UnsupportedGraphicsWarningScreen(title, message, ImmutableList.of(
            new UnsupportedGraphicsWarningScreen.ButtonOption(Component.translatable("options.graphics.warning.accept"), button -> {
                this.options.improvedTransparency().set(true);
                Minecraft.getInstance().levelExtractor.allChanged();
                this.warnlist.dismissWarning();
                this.minecraft.gui.setScreen(this);
            }),
            new UnsupportedGraphicsWarningScreen.ButtonOption(Component.translatable("options.graphics.warning.cancel"), button -> {
                this.warnlist.dismissWarning();
                this.options.improvedTransparency().set(false);
                this.minecraft.gui.setScreen(this);
            }))) {
        });
    }

    private static void addWarning(List<Component> message, String key, @Nullable String warnings) {
        if (warnings != null) {
            message.add(CommonComponents.NEW_LINE);
            message.add(Component.translatable(key, warnings).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.minecraft.hasControlDown() && this.options.guiScale().values() instanceof OptionInstance.ClampingLazyMaxIntRange range) {
            int current = this.options.guiScale().get();
            int adjusted = current == 0 ? range.maxInclusive() + 1 : current;
            int next = adjusted + (int) Math.signum(scrollY);
            if (next != 0 && next <= range.maxInclusive() && next >= range.minInclusive()) {
                this.options.guiScale().set(next);
                return true;
            }
            return false;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void removed() {
        this.minecraft.getWindow().changeFullscreenVideoMode();
        if (this.options.mipmapLevels().get() != this.oldMipmaps || this.options.maxAnisotropyBit().get() != this.oldAnisotropy
            || this.options.textureFiltering().get() != this.oldFiltering) {
            this.minecraft.updateMaxMipLevel(this.options.mipmapLevels().get());
            this.minecraft.delayTextureReload();
            this.rememberTextureSettings();
        }
        super.removed();
    }
}

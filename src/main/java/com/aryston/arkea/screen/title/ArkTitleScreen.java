package com.aryston.arkea.screen.title;

import com.aryston.arkea.Arkea;
import com.aryston.arkea.mixin.SplashRendererAccessor;
import com.aryston.arkea.screen.options.OptionsPage;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Timeline;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.overlay.ArkDialog;
import com.aryston.arkea.ui.overlay.DialogContent;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.screen.ArkScreen;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkButton;
import com.aryston.arkea.ui.widget.ArkIconButton;
import com.aryston.arkea.ui.widget.ArkMenuButton;
import com.aryston.arkea.ui.widget.ArkTextLink;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.ButtonVariant;
import com.aryston.arkea.ui.widget.IconButtonStyle;
import com.aryston.arkea.ui.widget.ItemContent;
import com.aryston.arkea.ui.widget.MenuButtonStyle;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.realmsclient.RealmsMainScreen;
import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.NoticeWithLinkScreen;
import net.minecraft.client.gui.screens.friends.FriendsOverlayScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OnlineOptionsScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.level.storage.LevelSummary;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.modlist.ModListScreen;
import org.jspecify.annotations.Nullable;

public final class ArkTitleScreen extends ArkScreen {
    private static final float EDGE = 96.0F;
    private static final float COLUMN_WIDTH = 360.0F;
    private static final float HEADER_HEIGHT = 80.0F;
    private static final float HEADER_GAP = 40.0F;
    private static final float EDITION_OFFSET = 68.0F;
    private static final float SPLASH_X = 330.0F;
    private static final float SPLASH_Y = 52.0F;
    private static final float PRIMARY_HEIGHT = 58.0F;
    private static final float BUTTON_HEIGHT = 48.0F;
    private static final float BUTTON_GAP = 8.0F;
    private static final float UTILITY_MARGIN = 12.0F;
    private static final float UTILITY_SIZE = 40.0F;
    private static final float CARD_BOTTOM = 112.0F;
    private static final float CARD_LABEL_GAP = 10.0F;
    private static final float FOOTER_BOTTOM = 28.0F;
    private static final float FOOTER_GAP = 16.0F;
    private static final float LOGO_SIZE = 14.0F;
    private static final float LOGO_GAP = 6.0F;
    private static final float COLUMN_SLIDE = 28.0F;
    private static final float BUTTON_SLIDE = 22.0F;
    private static final float CARD_SLIDE = 18.0F;
    private static final float QUIT_DIALOG_WIDTH = 380.0F;
    private static final int UTILITY_DELAY = 360;
    private static final float GRADIENT_FIRST = 0.38F;
    private static final float GRADIENT_SECOND = 0.72F;
    private static final int SHADE_LEFT = ArkColors.rgba(8, 9, 8, 0.90F);
    private static final int SHADE_FIRST = ArkColors.rgba(8, 9, 8, 0.62F);
    private static final int SHADE_SECOND = ArkColors.rgba(8, 9, 8, 0.12F);
    private static final int SHADE_RIGHT = ArkColors.rgba(8, 9, 8, 0.35F);
    private static final int VIGNETTE = ArkColors.rgba(0, 0, 0, 0.60F);
    private static final int HERO_SHADOW = ArkColors.rgba(0, 0, 0, 0.45F);
    private static final int LABEL_SHADOW = ArkColors.rgba(0, 0, 0, 0.60F);
    private static final TextStyle HERO = TextStyle.of(56.0F).spacing(6.0F).shadow(HERO_SHADOW, 4.0F);
    private static final TextStyle EDITION = TextStyle.of(12.0F).spacing(10.0F);
    private static final TextStyle CARD_LABEL = TextStyle.of(10.0F).spacing(1.0F).shadow(LABEL_SHADOW, 1.0F);
    private static final TextStyle FOOTER = TextStyle.of(10.0F);
    private static final IconButtonStyle UTILITY_STYLE = IconButtonStyle.brightening(
        ArkColors.rgba(16, 16, 18, 0.60F), ArkColors.BORDER_DEFAULT, ArkColors.TEXT_SOFT, 16.0F);

    private final List<ArkWidget> menuButtons = new ArrayList<>();
    private final List<ArkWidget> utilityButtons = new ArrayList<>();
    private Box column = Box.EMPTY;
    private Box cardGroup = Box.EMPTY;
    private @Nullable SplashText splash;
    private @Nullable JumpBackInCard card;
    private @Nullable ArkTextLink credits;
    private @Nullable RecentWorld recentWorld;
    private @Nullable FaviconTexture worldIcon;
    private long worldShownAt;
    private int generation;

    public ArkTitleScreen() {
        super(Component.translatable("narrator.screen.title"));
        this.minecraft.gameRenderer.panorama().startSpin();
    }

    @Override
    public void added() {
        super.added();
        int current = ++this.generation;
        RecentWorld.load(this.minecraft).thenAcceptAsync(world -> this.onWorldLoaded(current, world), this.minecraft);
    }

    @Override
    public void removed() {
        this.generation++;
        this.releaseWorldIcon();
        this.recentWorld = null;
        super.removed();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected int exitDuration() {
        return Motion.TITLE_SCREEN_OUT;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        this.minecraft.gameRenderer.panorama().extractRenderState(graphics, this.width, this.height);
    }

    @Override
    protected void buildUi() {
        UiScale scale = this.uiScale();
        this.menuButtons.clear();
        this.utilityButtons.clear();
        float columnHeight = HEADER_HEIGHT + HEADER_GAP + PRIMARY_HEIGHT + (BUTTON_HEIGHT + BUTTON_GAP) * 4.0F + UTILITY_MARGIN + UTILITY_SIZE;
        this.column = new Box(EDGE, (scale.canvasHeight() - columnHeight) * 0.5F, COLUMN_WIDTH, columnHeight);
        this.buildMenu();
        this.buildUtilityRow();
        this.buildCard();
        this.buildFooter();
        this.buildSplash();
    }

    private void buildSplash() {
        if (this.minecraft.options.hideSplashTexts().get()) {
            return;
        }
        if (this.splash == null) {
            Component first = this.nextSplash();
            if (first == null) {
                return;
            }
            this.splash = new SplashText(this, first, () -> Optional.ofNullable(this.nextSplash()).orElse(first));
        }
        this.add(this.splash);
        this.splash.place(this.column.x() + SPLASH_X, this.column.y() + SPLASH_Y);
    }

    private @Nullable Component nextSplash() {
        SplashRenderer renderer = this.minecraft.gui.splashManager().getSplash();
        return renderer != null ? ((SplashRendererAccessor) renderer).arkea$splash() : null;
    }

    private void buildMenu() {
        Component blocked = MultiplayerAccess.blockedReason(this.minecraft);
        float y = this.column.y() + HEADER_HEIGHT + HEADER_GAP;
        y = this.addMenuButton(new ItemContent(Icons.USER, Component.translatable("menu.singleplayer"),
            Component.translatable("arkea.title.singleplayer.description")), MenuButtonStyle.primary(), PRIMARY_HEIGHT,
            () -> this.navigate(() -> new SelectWorldScreen(this)), null, y);
        y = this.addMenuButton(new ItemContent(Icons.GLOBE, Component.translatable("menu.multiplayer"),
            Component.translatable("arkea.title.multiplayer.description")), MenuButtonStyle.secondary(), BUTTON_HEIGHT,
            this::openMultiplayer, blocked, y);
        y = this.addMenuButton(new ItemContent(Icons.CLOUD, Component.translatable("menu.online"),
            Component.translatable("arkea.title.realms.description")), MenuButtonStyle.secondary(), BUTTON_HEIGHT,
            () -> this.navigate(() -> new RealmsMainScreen(this)), blocked, y);
        y = this.addMenuButton(new ItemContent(Icons.PACK, Component.translatable("fml.menu.mods"),
            Component.translatable("arkea.title.mods.description", ModList.get().size())), MenuButtonStyle.secondary(), BUTTON_HEIGHT,
            () -> this.navigate(() -> ModListScreen.create(this)), null, y);
        this.addMenuButton(new ItemContent(Icons.SLIDERS, Component.translatable("options.title"),
            Component.translatable("arkea.title.options.description")), MenuButtonStyle.secondary(), BUTTON_HEIGHT,
            () -> this.navigate(() -> new OptionsScreen(this, this.minecraft.options)), null, y);
    }

    private float addMenuButton(ItemContent content, MenuButtonStyle style, float height, Runnable action,
                                @Nullable Component disabledReason, float y) {
        ArkMenuButton button = this.add(new ArkMenuButton(this, content, style, action));
        button.setBounds(new Box(this.column.x(), y, COLUMN_WIDTH, height));
        if (disabledReason != null) {
            button.setActive(false);
            button.setTooltip(disabledReason);
        }
        this.menuButtons.add(button);
        return y + height + BUTTON_GAP;
    }

    private void buildUtilityRow() {
        float y = this.column.bottom() - UTILITY_SIZE;
        float quitWidth = COLUMN_WIDTH - (UTILITY_SIZE + BUTTON_GAP) * 3.0F;
        ArkMenuButton quit = this.add(new ArkMenuButton(this, new ItemContent(Icons.POWER, Component.translatable("menu.quit"), null),
            MenuButtonStyle.dangerInline(), this::askQuit));
        quit.setBounds(new Box(this.column.x(), y, quitWidth, UTILITY_SIZE));
        this.utilityButtons.add(quit);
        float x = this.column.x() + quitWidth + BUTTON_GAP;
        x = this.addUtility(Icons.GLOBE, Component.translatable("options.language.tooltip"),
            () -> this.navigate(() -> new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), x, y);
        x = this.addUtility(Icons.ACCESS, Component.translatable("options.accessibility.tooltip"),
            () -> this.navigate(() -> new AccessibilityOptionsScreen(this, this.minecraft.options)), x, y);
        ArkIconButton friends = this.add(new ArkIconButton(this, Icons.ENTITY, Component.translatable("gui.friends.open.tooltip"), UTILITY_STYLE,
            this::openFriends));
        friends.setBounds(new Box(x, y, UTILITY_SIZE, UTILITY_SIZE));
        friends.setTooltip(Component.translatable("gui.friends.open.tooltip"));
        friends.setActive(!this.minecraft.isDemo() && !this.minecraft.isOfflineDeveloperMode());
        this.utilityButtons.add(friends);
    }

    private float addUtility(Icon icon, Component label, Runnable action, float x, float y) {
        ArkIconButton button = this.add(new ArkIconButton(this, icon, label, UTILITY_STYLE, action));
        button.setBounds(new Box(x, y, UTILITY_SIZE, UTILITY_SIZE));
        button.setTooltip(label);
        this.utilityButtons.add(button);
        return x + UTILITY_SIZE + BUTTON_GAP;
    }

    private void buildCard() {
        this.card = null;
        if (this.recentWorld == null) {
            return;
        }
        UiScale scale = this.uiScale();
        float labelHeight = this.metrics().capHeight(CARD_LABEL);
        float groupHeight = labelHeight + CARD_LABEL_GAP + JumpBackInCard.HEIGHT;
        this.cardGroup = new Box(scale.canvasWidth() - EDGE - JumpBackInCard.WIDTH, scale.canvasHeight() - CARD_BOTTOM - groupHeight,
            JumpBackInCard.WIDTH, groupHeight);
        LevelSummary summary = this.recentWorld.summary();
        JumpBackInCard.CardText text = new JumpBackInCard.CardText(Component.literal(summary.getLevelName()), lastPlayedText(summary),
            modeText(summary), Component.translatable("arkea.title.play"));
        this.card = this.add(new JumpBackInCard(this, text, this::playRecentWorld));
        this.card.setBounds(new Box(this.cardGroup.x(), this.cardGroup.bottom() - JumpBackInCard.HEIGHT, JumpBackInCard.WIDTH, JumpBackInCard.HEIGHT));
        this.card.setIcon(this.worldIcon != null ? this.worldIcon.textureLocation() : null);
    }

    private void buildFooter() {
        Component text = Component.translatable("title.credits");
        this.credits = this.add(new ArkTextLink(this, text, FOOTER, ArkColors.TEXT_FAINT,
            () -> this.navigate(() -> new CreditsAndAttributionScreen(this))));
        UiScale scale = this.uiScale();
        float brandWidth = LOGO_SIZE + LOGO_GAP + this.metrics().width(this.brandText(), FOOTER);
        float height = this.credits.preferredHeight();
        float x = scale.canvasWidth() - EDGE - brandWidth - FOOTER_GAP - this.credits.preferredWidth();
        this.credits.setBounds(new Box(x, scale.canvasHeight() - FOOTER_BOTTOM - height, this.credits.preferredWidth(), height));
    }

    @Override
    protected void renderUi(UiGraphics graphics, float mouseX, float mouseY) {
        this.renderBackdrop(graphics);
        long opened = this.sinceOpened();
        float exit = this.isLeaving() ? Timeline.exit(this.sinceLeft(), Motion.TITLE_SCREEN_OUT) : 1.0F;
        float columnEnter = Timeline.enter(opened, 0, Motion.TITLE_SCREEN_IN);
        float columnShift = -COLUMN_SLIDE * (1.0F - columnEnter) - COLUMN_SLIDE * (1.0F - exit);
        graphics.push();
        graphics.fade(columnEnter * exit);
        graphics.translate(columnShift, 0.0F);
        this.renderHeader(graphics);
        if (this.splash != null && !this.minecraft.options.hideSplashTexts().get()) {
            this.splash.render(graphics, mouseX, mouseY);
        }
        for (int index = 0; index < this.menuButtons.size(); index++) {
            int delay = Motion.TITLE_BUTTON_FIRST_DELAY + index * Motion.TITLE_BUTTON_STAGGER;
            float enter = Timeline.enter(opened, delay, Motion.TITLE_BUTTON_IN);
            this.renderEntering(graphics, this.menuButtons.get(index), enter, mouseX, mouseY);
        }
        float utilityEnter = Timeline.enter(opened, UTILITY_DELAY, Motion.TITLE_BUTTON_IN);
        for (ArkWidget widget : this.utilityButtons) {
            this.renderEntering(graphics, widget, utilityEnter, mouseX, mouseY);
        }
        graphics.pop();
        this.renderSliding(graphics, Timeline.enter(opened, Motion.TITLE_CARD_DELAY, Motion.TITLE_SCREEN_IN) * exit,
            () -> this.renderFooter(graphics, mouseX, mouseY));
        if (this.card != null) {
            float appear = Timeline.enter(this.now() - this.worldShownAt, 0, Motion.TITLE_SCREEN_IN);
            this.renderSliding(graphics, appear * exit, () -> this.renderCard(graphics, this.card, mouseX, mouseY));
        }
    }

    private void renderSliding(UiGraphics graphics, float progress, Runnable content) {
        graphics.push();
        graphics.fade(progress);
        graphics.translate(0.0F, CARD_SLIDE * (1.0F - progress));
        content.run();
        graphics.pop();
    }

    private void renderBackdrop(UiGraphics graphics) {
        UiScale scale = graphics.scale();
        float width = scale.canvasWidth();
        float height = scale.canvasHeight();
        float first = width * GRADIENT_FIRST;
        float second = width * GRADIENT_SECOND;
        graphics.gradientHorizontal(0.0F, 0.0F, first, height, SHADE_LEFT, SHADE_FIRST);
        graphics.gradientHorizontal(first, 0.0F, second - first, height, SHADE_FIRST, SHADE_SECOND);
        graphics.gradientHorizontal(second, 0.0F, width - second, height, SHADE_SECOND, SHADE_RIGHT);
        graphics.vignette(new Box(0.0F, 0.0F, width, height), VIGNETTE);
    }

    private void renderHeader(UiGraphics graphics) {
        String hero = Component.translatable("arkea.title.hero").getString();
        graphics.text(hero, this.column.x(), this.column.y(), HERO, ArkColors.TEXT_PRIMARY);
        String edition = Component.translatable("arkea.title.edition").getString();
        graphics.text(edition, this.column.x(), this.column.y() + EDITION_OFFSET, EDITION, ArkColors.TEXT_SOFT);
    }

    private void renderEntering(UiGraphics graphics, ArkWidget widget, float enter, float mouseX, float mouseY) {
        graphics.push();
        graphics.fade(enter);
        graphics.translate(-BUTTON_SLIDE * (1.0F - enter), 0.0F);
        widget.render(graphics, mouseX, mouseY);
        graphics.pop();
    }

    private void renderCard(UiGraphics graphics, JumpBackInCard jumpBackIn, float mouseX, float mouseY) {
        String label = Component.translatable("arkea.title.jump_back_in").getString();
        graphics.text(label, this.cardGroup.x(), this.cardGroup.y(), CARD_LABEL, ArkColors.TEXT_SOFT);
        jumpBackIn.render(graphics, mouseX, mouseY);
    }

    private void renderFooter(UiGraphics graphics, float mouseX, float mouseY) {
        UiScale scale = graphics.scale();
        TextMetrics metrics = graphics.metrics();
        float textHeight = metrics.capHeight(FOOTER);
        float y = scale.canvasHeight() - FOOTER_BOTTOM - textHeight;
        String version = Component.translatable("arkea.title.version", SharedConstants.getCurrentVersion().name(), ModList.get().size()).getString();
        graphics.text(version, EDGE, y, FOOTER, ArkColors.TEXT_FAINT);
        if (this.credits != null) {
            this.credits.render(graphics, mouseX, mouseY);
        }
        String brand = this.brandText();
        float brandX = scale.canvasWidth() - EDGE - metrics.width(brand, FOOTER);
        float logoX = brandX - LOGO_GAP - LOGO_SIZE;
        graphics.image(OptionsPage.ARKEA_LOGO, new Box(logoX, y + textHeight * 0.5F - LOGO_SIZE * 0.5F, LOGO_SIZE, LOGO_SIZE), 0.0F, 0.0F, 1.0F, 1.0F,
            ArkColors.TEXT_PRIMARY);
        graphics.text(brand, brandX, y, FOOTER, ArkColors.TEXT_MUTED);
    }

    private String brandText() {
        return Component.translatable("arkea.title.brand").getString();
    }

    private void openMultiplayer() {
        this.navigate(() -> this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this));
    }

    private void openFriends() {
        this.leave(() -> OnlineOptionsScreen.confirmFriendsListEnabled(this.minecraft,
            () -> this.minecraft.gui.setScreen(new FriendsOverlayScreen(this)), this));
    }

    private void askQuit() {
        DialogContent content = new DialogContent(Component.translatable("arkea.title.quit.title"),
            Component.translatable("arkea.title.quit.message"), Icons.POWER, ArkColors.ERROR);
        ArkDialog dialog = new ArkDialog(this, content, QUIT_DIALOG_WIDTH, this::closeDialog);
        dialog.button(new ArkButton(this, CommonComponents.GUI_CANCEL, ButtonVariant.SUBTLE, this::closeDialog));
        dialog.button(new ArkButton(this, Component.translatable("menu.quit"), ButtonVariant.DANGER, this::quitGame));
        dialog.keepOpenOnScrimClick();
        this.openDialog(dialog);
    }

    private void quitGame() {
        this.closeDialog();
        this.leave(this.minecraft::stop);
    }

    private void playRecentWorld() {
        if (this.recentWorld == null) {
            return;
        }
        LevelSummary summary = this.recentWorld.summary();
        if (summary instanceof LevelSummary.SymlinkLevelSummary) {
            this.navigate(() -> NoticeWithLinkScreen.createWorldSymlinkWarningScreen(() -> this.minecraft.gui.setScreen(this)));
            return;
        }
        this.leave(() -> this.minecraft.createWorldOpenFlows().openWorld(summary.getLevelId(), () -> this.minecraft.gui.setScreen(this)));
    }

    private void onWorldLoaded(int requestGeneration, Optional<RecentWorld> world) {
        if (requestGeneration != this.generation || world.isEmpty()) {
            return;
        }
        this.recentWorld = world.get();
        this.uploadWorldIcon(this.recentWorld);
        this.worldShownAt = Math.max(this.now(), this.now() - this.sinceOpened() + Motion.TITLE_CARD_DELAY);
        this.rebuildWidgets();
    }

    private void uploadWorldIcon(RecentWorld world) {
        this.releaseWorldIcon();
        byte[] bytes = world.iconBytes();
        if (bytes == null) {
            return;
        }
        FaviconTexture icon = FaviconTexture.forWorld(this.minecraft.getTextureManager(), world.summary().getLevelId());
        try {
            icon.upload(NativeImage.read(bytes));
            this.worldIcon = icon;
        } catch (IOException | IllegalArgumentException exception) {
            Arkea.LOGGER.warn("Could not load the icon of world {}", world.summary().getLevelId(), exception);
            icon.close();
        }
    }

    private void releaseWorldIcon() {
        if (this.worldIcon != null) {
            this.worldIcon.close();
            this.worldIcon = null;
        }
    }

    private static Component lastPlayedText(LevelSummary summary) {
        LastPlayed lastPlayed = LastPlayed.describe(summary.getLastPlayed(), System.currentTimeMillis(), ZoneId.systemDefault(), Locale.getDefault());
        return switch (lastPlayed.day()) {
            case TODAY -> Component.translatable("arkea.title.last_played.today", lastPlayed.time());
            case YESTERDAY -> Component.translatable("arkea.title.last_played.yesterday", lastPlayed.time());
            case EARLIER -> Component.translatable("arkea.title.last_played.earlier", lastPlayed.date());
        };
    }

    private static Component modeText(LevelSummary summary) {
        String mode = summary.isHardcore() ? "hardcore" : summary.getGameMode().getName();
        String text = Component.translatable("selectWorld.gameMode." + mode).getString().toUpperCase(Locale.ROOT);
        return Component.literal(text);
    }
}

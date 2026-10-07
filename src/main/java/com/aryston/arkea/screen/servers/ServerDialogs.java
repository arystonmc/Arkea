package com.aryston.arkea.screen.servers;

import com.aryston.arkea.ui.overlay.ArkDialog;
import com.aryston.arkea.ui.overlay.DialogContent;
import com.aryston.arkea.ui.overlay.DialogForm;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.ArkButton;
import com.aryston.arkea.ui.widget.ArkSegmented;
import com.aryston.arkea.ui.widget.ArkTextField;
import com.aryston.arkea.ui.widget.ButtonVariant;
import com.aryston.arkea.ui.widget.TextFieldState;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

final class ServerDialogs {
    private static final float WIDTH = 420.0F;
    private static final float FIELD_HEIGHT = 32.0F;
    private static final float PACKS_HEIGHT = 28.0F;
    private static final int NAME_LENGTH = 128;
    private static final int ADDRESS_LENGTH = 256;

    private final ArkMultiplayerScreen screen;

    ServerDialogs(ArkMultiplayerScreen screen) {
        this.screen = screen;
    }

    void add() {
        this.edit(null);
    }

    void edit(@Nullable ServerData server) {
        boolean adding = server == null;
        TextFieldState name = new TextFieldState(NAME_LENGTH);
        name.setText(adding ? "" : server.name);
        TextFieldState address = new TextFieldState(ADDRESS_LENGTH);
        address.setText(adding ? "" : server.ip);
        ServerData.ServerPackStatus[] statuses = ServerData.ServerPackStatus.values();
        ServerData.ServerPackStatus[] pack = {adding ? ServerData.ServerPackStatus.PROMPT : server.getResourcePackStatus()};
        Component title = Component.translatable(adding ? "manageServer.add.title" : "manageServer.edit.title");
        ArkDialog dialog = new ArkDialog(this.screen, new DialogContent(title, Component.empty(), Icons.SERVER, Theme.accent().light()), WIDTH,
            this.screen::hideDialog);
        boolean[] touched = {false};
        ArkButton submit = new ArkButton(this.screen, Component.translatable(adding ? "arkea.servers.add.confirm" : "selectWorld.edit.save"),
            ButtonVariant.PRIMARY, () -> {
                this.screen.hideDialog();
                String finalName = name.text().isBlank() ? Component.translatable("selectServer.defaultName").getString() : name.text().strip();
                this.screen.saveServer(server, finalName, address.text().strip(), pack[0]);
            });
        submit.setActive(isValid(address.text()));
        ArkTextField nameField = new ArkTextField(this.screen, Component.translatable("selectServer.defaultName"), name, value -> { }).icon(null);
        ArkTextField addressField = new ArkTextField(this.screen, Component.translatable("arkea.servers.address.hint"), address, value -> {
            touched[0] = true;
            submit.setActive(isValid(value));
        }).icon(null);
        List<Component> labels = Arrays.stream(statuses).map(ServerData.ServerPackStatus::getName).toList();
        ArkSegmented packs = new ArkSegmented(this.screen, Component.translatable("manageServer.resourcePack"), labels, () -> pack[0].ordinal(),
            index -> pack[0] = statuses[index]);
        dialog.content(new DialogForm()
            .label(Component.translatable("arkea.servers.name"))
            .field(nameField, FIELD_HEIGHT)
            .label(Component.translatable("arkea.servers.address"))
            .field(addressField, FIELD_HEIGHT)
            .message(() -> touched[0] && !isValid(address.text()) ? Component.translatable("arkea.servers.address.invalid") : null, ArkColors.ERROR)
            .labeled(Component.translatable("manageServer.resourcePack"), packs, packs.preferredWidth(), PACKS_HEIGHT));
        dialog.button(new ArkButton(this.screen, CommonComponents.GUI_CANCEL, ButtonVariant.SUBTLE, this.screen::hideDialog));
        dialog.button(submit);
        this.screen.showDialog(dialog);
    }

    void direct(String lastAddress, Consumer<String> connect) {
        TextFieldState address = new TextFieldState(ADDRESS_LENGTH);
        address.setText(lastAddress);
        ArkDialog dialog = new ArkDialog(this.screen, new DialogContent(Component.translatable("selectServer.direct"), Component.empty(), Icons.CONNECT,
            Theme.accent().light()), WIDTH, this.screen::hideDialog);
        boolean[] touched = {false};
        ArkButton submit = new ArkButton(this.screen, Component.translatable("selectServer.select"), ButtonVariant.PRIMARY, () -> {
            this.screen.hideDialog();
            connect.accept(address.text().strip());
        });
        submit.setActive(isValid(address.text()));
        ArkTextField field = new ArkTextField(this.screen, Component.translatable("arkea.servers.address.hint"), address, value -> {
            touched[0] = true;
            submit.setActive(isValid(value));
        }).icon(null);
        dialog.content(new DialogForm()
            .label(Component.translatable("arkea.servers.address"))
            .field(field, FIELD_HEIGHT)
            .message(() -> touched[0] && !isValid(address.text()) ? Component.translatable("arkea.servers.address.invalid") : null, ArkColors.ERROR));
        dialog.button(new ArkButton(this.screen, CommonComponents.GUI_CANCEL, ButtonVariant.SUBTLE, this.screen::hideDialog));
        dialog.button(submit);
        this.screen.showDialog(dialog);
    }

    void delete(ServerData server) {
        DialogContent content = new DialogContent(Component.translatable("arkea.servers.delete.title", server.name),
            Component.translatable("arkea.servers.delete.message"), Icons.TRASH, ArkColors.ERROR);
        ArkDialog dialog = new ArkDialog(this.screen, content, WIDTH, this.screen::hideDialog);
        dialog.button(new ArkButton(this.screen, CommonComponents.GUI_CANCEL, ButtonVariant.SUBTLE, this.screen::hideDialog));
        dialog.button(new ArkButton(this.screen, Component.translatable("selectServer.deleteButton"), ButtonVariant.DANGER, () -> {
            this.screen.hideDialog();
            this.screen.removeServer(server);
        }));
        this.screen.showDialog(dialog);
    }

    private static boolean isValid(String address) {
        return !address.isBlank() && ServerAddress.isValidAddress(address.strip());
    }
}

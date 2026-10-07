package com.aryston.arkea.integration;

import com.aryston.arkea.Arkea;
import com.mojang.blaze3d.platform.InputConstants;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.neoforged.neoforge.client.settings.KeyModifier;

/**
 * NeoForge 26.3.0.51-beta parses a saved key with a modifier ("key.keyboard.j:CONTROL") as a key name first, fails and unbinds
 * the mapping on every start. This puts those bindings back right after the options are loaded, before the game saves them again.
 */
public final class KeyModifierRepair {
    private static final String PREFIX = "key_";
    private static final char SEPARATOR = ':';

    private KeyModifierRepair() {
    }

    public static void run(Options options) {
        Path file = options.getFile().toPath();
        if (!Files.isRegularFile(file)) {
            return;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            Arkea.LOGGER.warn("Could not read {} to restore key modifiers", file, exception);
            return;
        }
        int restored = 0;
        for (String line : lines) {
            if (restore(line)) {
                restored++;
            }
        }
        if (restored > 0) {
            KeyMapping.resetMapping();
            Arkea.LOGGER.info("Restored {} key bindings with modifiers", restored);
        }
    }

    static boolean restore(String line) {
        if (!line.startsWith(PREFIX)) {
            return false;
        }
        int nameEnd = line.indexOf(SEPARATOR);
        int modifierStart = line.lastIndexOf(SEPARATOR);
        if (nameEnd < 0 || modifierStart <= nameEnd) {
            return false;
        }
        KeyMapping mapping = KeyMapping.get(line.substring(PREFIX.length(), nameEnd));
        KeyModifier modifier = KeyModifier.valueFromString(line.substring(modifierStart + 1));
        if (mapping == null || modifier == KeyModifier.NONE || !mapping.isUnbound()) {
            return false;
        }
        try {
            mapping.setKeyModifierAndCode(modifier, InputConstants.getKey(line.substring(nameEnd + 1, modifierStart)));
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}

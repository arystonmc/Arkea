package com.aryston.arkea.background;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import org.jspecify.annotations.Nullable;

public record BackgroundEntry(Path folder, String name, Kind kind, int width, int height, int fps, int frames, long bytes, long created) {
    static final String MANIFEST = "background.json";
    static final String THUMBNAIL = "thumbnail.jpg";
    static final String IMAGE = "image.jpg";
    static final String FRAMES = "frames";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public String id() {
        return this.folder.getFileName().toString();
    }

    public Path thumbnail() {
        return this.folder.resolve(THUMBNAIL);
    }

    public Path image() {
        return this.folder.resolve(IMAGE);
    }

    public Path frame(int index) {
        return frame(this.folder, index);
    }

    static Path frame(Path folder, int index) {
        return folder.resolve(FRAMES).resolve(String.format(Locale.ROOT, "%05d.jpg", index));
    }

    public float seconds() {
        return this.kind == Kind.VIDEO && this.fps > 0 ? this.frames / (float) this.fps : 0.0F;
    }

    static @Nullable BackgroundEntry read(Path folder) {
        Path manifest = folder.resolve(MANIFEST);
        if (!Files.isRegularFile(manifest)) {
            return null;
        }
        try (Reader reader = Files.newBufferedReader(manifest, StandardCharsets.UTF_8)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            Kind kind = Kind.valueOf(json.get("kind").getAsString().toUpperCase(Locale.ROOT));
            return new BackgroundEntry(folder, json.get("name").getAsString(), kind, json.get("width").getAsInt(), json.get("height").getAsInt(),
                json.has("fps") ? json.get("fps").getAsInt() : 0, json.has("frames") ? json.get("frames").getAsInt() : 1,
                json.has("bytes") ? json.get("bytes").getAsLong() : 0L, json.has("created") ? json.get("created").getAsLong() : 0L);
        } catch (IOException | RuntimeException exception) {
            return null;
        }
    }

    void write() throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("format", 1);
        json.addProperty("name", this.name);
        json.addProperty("kind", this.kind.name().toLowerCase(Locale.ROOT));
        json.addProperty("width", this.width);
        json.addProperty("height", this.height);
        json.addProperty("fps", this.fps);
        json.addProperty("frames", this.frames);
        json.addProperty("bytes", this.bytes);
        json.addProperty("created", this.created);
        try (Writer writer = Files.newBufferedWriter(this.folder.resolve(MANIFEST), StandardCharsets.UTF_8)) {
            GSON.toJson(json, writer);
        }
    }

    BackgroundEntry movedTo(Path newFolder) {
        return new BackgroundEntry(newFolder, this.name, this.kind, this.width, this.height, this.fps, this.frames, this.bytes, this.created);
    }

    public enum Kind {
        VIDEO,
        IMAGE
    }
}

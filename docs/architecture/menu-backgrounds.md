# Arkea Architecture: Menu Backgrounds

Part of the [Arkea architecture](../ARCHITECTURE.md).

## Menu Backgrounds

- The player picks a background on the Arkea settings page. `PanoramaMixin` cancels `Panorama.extractRenderState` and `MenuBackground` draws the selected background instead, so it shows wherever vanilla would show the panorama: the title screen, the options window over it and other menus.
- Imports run once, on a worker thread: `BackgroundImporter` turns a video, GIF or picture into a folder with JPEG frames (or one JPEG), a thumbnail and `background.json`. Nothing is converted while the menu is open, and the original file is never needed again.
- Videos convert in parallel: `VideoConverter` splits them at their key frames, because every key frame to key frame part decodes on its own. Each `VideoSegment` decodes with its own JCodec decoder, skips non-reference frames that no output frame shows (half the frames of a 60 fps video with B frames), and `PictureScaler` shrinks the YUV planes before converting colors, so only 720p pixels are converted. `FrameWriter` encodes the JPEGs on separate threads. The number of decoders follows the cores and the free memory (a 4K decoder needs about 200 MB).
- Speed limits: decoding stays the cost. A 1080p clip of 20 seconds takes about 10 seconds, a 4K 60 fps clip of 49 seconds about a minute on 16 threads. Only a hardware or native decoder would bring 4K imports down to a few seconds.
- The Arkea page scans the library when it opens and polls the folder once a second; a dropped file is imported once `DropFolder` sees the same size and date twice and no program writes to it.
- Why JPEG frames: decoding H.264 in Java (JCodec) runs at about 30 to 50 frames per second for 720p, too close to the frame budget of the menu, and JCodec cannot re-encode fast enough. STB decodes a 720p JPEG in about 3.5 ms, the frames load lazily from disk and a GIF would only lose colors (256 per frame) and be larger. A 12 second 720p video takes about 6 MB.
- Playback: `BackgroundPlayer` decodes upcoming frames on a daemon thread into a queue of three `NativeImage`s; the render thread copies one into a `DynamicTexture` when it is due. A background not drawn for three seconds is released.
- The texture is drawn with a linear, clamp to edge sampler from `RenderSystem.getSamplerCache()` through `GuiGraphicsExtractor.blit`, cover fitted to the window.
- JCodec is embedded with Jar-in-Jar; only the importer touches it.

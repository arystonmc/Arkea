# Arkea Code Map: `com.aryston.arkea.background`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.background`

#### BackgroundLibrary
- Path: `src/main/java/com/aryston/arkea/background/BackgroundLibrary.java`
- Role: The menu backgrounds in `<game folder>/arkea/backgrounds`: scans the entry folders when the Arkea page opens and polls the folder once a second while it is open (`poll`), imports files one at a time on a background worker (into a temporary folder, then an atomic move), auto imports loose media files dropped into the folder once `DropFolder` reports them settled (the originals move to `imported/`, or `failed/`), removes leftover temporary folders only on the first scan, selects the new entry after an import, deletes entries and counts changes in `version` so the settings page rebuilds.
- Depends on: `BackgroundImporter`, `BackgroundEntry`, `ImportJob`, `DropFolder`, `WorkerThreads`, `ArkeaConfig`, `MenuBackground`.

#### DropFolder
- Path: `src/main/java/com/aryston/arkea/background/DropFolder.java`
- Role: Tells which loose files in the backgrounds folder have finished copying: same size and modification time on two polls in a row, and no other program holds the file open for writing. Files whose import was cancelled are ignored until they change.

#### BackgroundEntry
- Path: `src/main/java/com/aryston/arkea/background/BackgroundEntry.java`
- Role: One imported background: name, kind (video or image), size, frame rate, frame count, bytes and date, stored as `background.json` next to `thumbnail.jpg` and either `image.jpg` or `frames/00000.jpg`...

#### BackgroundImporter
- Path: `src/main/java/com/aryston/arkea/background/BackgroundImporter.java`
- Role: Converts MP4, M4V, MOV, GIF, PNG, JPG and BMP into a background folder. Videos and animations become a JPEG frame sequence at their own frame rate (at most 30 fps, 1280 x 720 and 60 seconds); Videos go to `VideoConverter`, GIFs are converted frame by frame here; stills and single frame files become one image of at most 1920 x 1080. Writes a 384 x 216 thumbnail and the manifest, reports progress and stops when cancelled.
- Notes: Playing JPEG frames needs no video decoder at runtime; JPEG decoding through STB is about ten times faster than decoding H.264 in Java, so playback stays cheap.

#### MediaFrames
- Path: `src/main/java/com/aryston/arkea/background/MediaFrames.java`
- Role: A source of decoded frames with their time, for the importer.

#### VideoConverter
- Path: `src/main/java/com/aryston/arkea/background/VideoConverter.java`
- Role: Converts an MP4 or MOV into the JPEG frame sequence in parallel. Splits the video at its key frames into `VideoSegment`s, decodes them on a pool of low priority threads (one less than the cores, fewer when the free memory cannot hold that many decoders) and hands the frames to `FrameWriter`. Stops every thread on cancel or the first error and reports that error.
- Depends on: `VideoSegment`, `VideoPlan`, `FrameWriter`, `WorkerThreads`.
- Notes: JCodec decodes about 8 frames per second of 4K H.264 on one core; the parallel segments and the skipped frames make a 17 second 4K 60 fps clip take about 20 seconds instead of over three minutes.

#### VideoSegment
- Path: `src/main/java/com/aryston/arkea/background/VideoSegment.java`
- Role: One key frame to key frame part of a video, decoded with its own file channel and JCodec decoder. Reads the presentation times of its packets first, maps them to output frames with `FrameSlots`, then decodes in decode order, skips non-reference frames that no output frame shows without decoding them, and scales only shown frames with `PictureScaler`.

#### VideoPlan
- Path: `src/main/java/com/aryston/arkea/background/VideoPlan.java`
- Role: Time origin, output frame rate, frame limit and codec of a video, shared by its segments.

#### FrameSlots
- Path: `src/main/java/com/aryston/arkea/background/FrameSlots.java`
- Role: Maps the presentation times of source frames to the output frames each one fills at the output frame rate (a 60 fps frame between two 30 fps outputs fills none, a slow frame fills several).

#### NalUnits
- Path: `src/main/java/com/aryston/arkea/background/NalUnits.java`
- Role: Reads the first slice header of an Annex B H.264 packet and tells whether no other frame references it, so it can be skipped.

#### PictureScaler
- Path: `src/main/java/com/aryston/arkea/background/PictureScaler.java`
- Role: Turns a decoded JCodec picture into an RGB image of at most the given size: area averages the YUV 4:2:0 planes inside the crop and converts only the small result (BT.709 for HD, BT.601 otherwise, full range for JPEG YUV). Other pixel formats go through JCodec's color transform and `BackgroundImporter.fit`.

#### FrameWriter
- Path: `src/main/java/com/aryston/arkea/background/FrameWriter.java`
- Role: Encodes scaled frames to JPEG on its own threads and writes them to every output frame they fill, with a bounded queue so decoders wait instead of filling the memory. Writes the thumbnail from output frame 0 and keeps the first error. `close` waits for running encoders, so a cancelled import can delete its folder.

#### WorkerThreads
- Path: `src/main/java/com/aryston/arkea/background/WorkerThreads.java`
- Role: Thread factory for numbered, low priority daemon threads of the importer.

#### GifFrames
- Path: `src/main/java/com/aryston/arkea/background/GifFrames.java`
- Role: Reads animated GIFs with ImageIO and composes every frame on the logical screen with its offset, delay and disposal method.

#### ImportJob
- Path: `src/main/java/com/aryston/arkea/background/ImportJob.java`
- Role: State and progress of one running import, shown as a tile; can be cancelled.

#### JpegImages
- Path: `src/main/java/com/aryston/arkea/background/JpegImages.java`
- Role: Decodes a JPEG file into a `NativeImage` with STB (`NativeImage.read` only accepts PNG).

#### BackgroundPlayer
- Path: `src/main/java/com/aryston/arkea/background/BackgroundPlayer.java`
- Role: The texture of the selected background. An image is loaded once; a video decodes the next frames on a daemon thread into a queue of three and uploads one frame when it is due, looping at the end.

#### MenuBackground
- Path: `src/main/java/com/aryston/arkea/background/MenuBackground.java`
- Role: Draws the selected background instead of the vanilla panorama, cover fitted and linear filtered, with a slow pan and zoom over still images when enabled. Frees the player three seconds after the last draw and remembers a background that failed to load so it falls back to the panorama.

#### BackgroundThumbnails
- Path: `src/main/java/com/aryston/arkea/background/BackgroundThumbnails.java`
- Role: Thumbnail textures of the gallery, freed when the settings page closes.

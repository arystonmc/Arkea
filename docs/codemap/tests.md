# Arkea Code Map: Tests

Part of the [Arkea code map](../CODEMAP.md).

Plain JUnit 5 tests without a running game, run by `./gradlew build` and the CI. Each test class sits in the package of the class it checks.

| File | What it checks |
|---|---|
| `src/test/java/com/aryston/arkea/LanguageFilesTest.java` | Every language file has the same keys and every config entry has a `.tooltip` translation. |
| `src/test/java/com/aryston/arkea/ui/anim/ProgressMotionTest.java` | Known fractions map into their span, unknown ones creep without reaching the end, the shown progress follows smoothly and never moves back. |
| `src/test/java/com/aryston/arkea/ui/anim/CubicBezierTest.java` | Fixed endpoints, linear identity, the CSS `ease` reference value, the standard curve rising early and the spring overshooting. |
| `src/test/java/com/aryston/arkea/ui/anim/PresenceTest.java` | Hidden start, enter, exit kept alive until it ends, smooth reversal midway, delayed enter. |
| `src/test/java/com/aryston/arkea/ui/anim/TransitionTest.java` | Reaching the target, retargeting from the current value, snapping. |
| `src/test/java/com/aryston/arkea/ui/anim/TimelineTest.java` | Delayed enter, exit from one to zero, oscillation loop. |
| `src/test/java/com/aryston/arkea/ui/layout/UiScaleTest.java` | One to one at 1366 x 768, quarter steps, the design fits every common window, coordinate round trip, one physical pixel minimum for lines, the window based reference scale ignoring the GUI scale. |
| `src/test/java/com/aryston/arkea/ui/render/SvgPathParserTest.java` | Absolute and relative lines, closing, implicit commands, compact numbers, arcs on their radius, cubic end points, every catalogue icon parses, invalid data is rejected. |
| `src/test/java/com/aryston/arkea/ui/render/SoftEdgeProfileTest.java` | Shadow edge coverage and the vignette fade. |
| `src/test/java/com/aryston/arkea/ui/text/SearchTextTest.java` | Case and accent insensitive matching, Turkish and German letters. |
| `src/test/java/com/aryston/arkea/ui/widget/TextFieldStateTest.java` | Typing at the caret with a length limit, deleting characters and words, select all, word jumps. |
| `src/test/java/com/aryston/arkea/ui/widget/SliderRangeTest.java` | Slider fraction, step snapping, clamping and an empty range. |
| `src/test/java/com/aryston/arkea/screen/options/control/OptionTextTest.java` | Vanilla "Caption: value" labels reduce to the value, percent labels use the Arkea key, unknown labels stay. |
| `src/test/java/com/aryston/arkea/background/BackgroundImporterTest.java` | File type detection, frame rate rounding and the 30 fps cap, scaling without upscaling, image, GIF and video conversion (frame order with B frames from `src/test/resources/background/ramp.mp4`), single frame GIFs, cancelling, manifest round trip and broken manifests. |
| `src/test/java/com/aryston/arkea/background/FrameSlotsTest.java` | 60 fps frames between two 30 fps outputs are skipped, slow frames fill several outputs, the frame limit, unknown times. |
| `src/test/java/com/aryston/arkea/background/NalUnitsTest.java` | Non-reference slices are disposable, reference and key frames are kept, parameter sets and SEI before the slice are skipped, data without a slice is kept. |
| `src/test/java/com/aryston/arkea/background/PictureScalerTest.java` | Limited and full range colors, shrinking 4K to 720p, the crop hides coded padding, area averaging. |
| `src/test/java/com/aryston/arkea/background/DropFolderTest.java` | A dropped file settles on the second poll, a growing file waits, missing files are ignored. |
| `src/test/java/com/aryston/arkea/hud/HudMotionTest.java` | Bars ease and keep a ghost after damage but not after healing, the selection slides and pops, level up pops the level. |
| `src/test/java/com/aryston/arkea/screen/title/LastPlayedTest.java` | Today, yesterday across midnight, and the localized date for older worlds. |

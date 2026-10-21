# Arkea Code Map: `com.aryston.arkea.ui.anim`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.ui.anim`

#### Easing
- Path: `src/main/java/com/aryston/arkea/ui/anim/Easing.java`
- Role: Easing function interface with the design curves: `STANDARD` (enter), `EXIT` (ease-in), `EASE` (CSS default for hover), `EASE_OUT`, `EASE_IN_OUT`, `SPRING` (switch knob), `LINEAR`.

#### CubicBezier
- Path: `src/main/java/com/aryston/arkea/ui/anim/CubicBezier.java`
- Role: CSS `cubic-bezier()` easing, solved with Newton steps and a bisection fallback.

#### Motion
- Path: `src/main/java/com/aryston/arkea/ui/anim/Motion.java`
- Role: Animation durations and delays in milliseconds from the design tokens.

#### Transition
- Path: `src/main/java/com/aryston/arkea/ui/anim/Transition.java`
- Role: A value that eases toward a target; retargeting starts from the current value. Drives hover, press and zoom blends.

#### Presence
- Path: `src/main/java/com/aryston/arkea/ui/anim/Presence.java`
- Role: Shown or hidden state with an eased enter and a shorter ease-in exit. Keeps the element alive until the exit ends (`isGone`) and reverses smoothly when toggled midway.

#### Timeline
- Path: `src/main/java/com/aryston/arkea/ui/anim/Timeline.java`
- Role: Enter progress with a delay (staggered lists) and exit progress from elapsed time.

#### Oscillation
- Path: `src/main/java/com/aryston/arkea/ui/anim/Oscillation.java`
- Role: Smooth 0 to 1 to 0 loop for pulses (splash text).

#### ProgressMotion
- Path: `src/main/java/com/aryston/arkea/ui/anim/ProgressMotion.java`
- Role: Progress bar motion: maps a fraction into its span, lets an unknown fraction creep toward 90 % of its span, and follows the target smoothly (140 ms time constant) without ever going back.

# Arkea Code Map: `com.aryston.arkea.hud.chat`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.hud.chat`

#### ArkChat
- Path: `src/main/java/com/aryston/arkea/hud/chat/ArkChat.java`
- Role: Arkea chat while `CHAT` is on: about 0.85 of the vanilla chat scale snapped to whole physical pixels per font pixel (crisp text), at most 42 percent of the screen width (at least 180 GUI units, never wider than the vanilla setting), above the left column of the Arkea HUD (108 design pixels, converted with the HUD scale, `base`) and, while the chat screen is open, higher by the height of the input bar (`ChatInput`). Holds the frame state the mixin reports: the drawing graphics and the line being visited. Smooth scrolling: `scroll` forwards wheel and page key scrolls to vanilla and hands the lines actually scrolled to `ChatScroll`; while it moves, `firstLine` and `lineCount` widen the lines `forEachLine` visits, `scrollShift` offsets them and `begin` clips the chat to its area with a scissor. `split` wraps a message 11 GUI units narrower when it has a sender, to leave room for the head.
- Depends on: `ChatStyle`, `ChatEntries`, `ChatInput`, `ChatScroll`, `HudSettings`, `ChatComponentAccessor`.

#### ChatStyle
- Path: `src/main/java/com/aryston/arkea/hud/chat/ChatStyle.java`
- Role: The `ChatGraphicsAccess` wrapper of the Arkea chat. Message backgrounds become dark plates with a one pixel line in the sender's color (accent otherwise) and a faint divider above the first line of each message; the sender's face (hat layer included) is drawn at the first line and all its lines are indented. New messages slide in from the left and fade in, and every visible line starts lower by the height of the messages still entering, so the chat moves up smoothly; while the chat scrolls, lines are offset by `ArkChat.scrollShift`. Every offset goes through `updatePose`, so the vanilla hover and click detection follows the drawn text.

#### ChatInput
- Path: `src/main/java/com/aryston/arkea/hud/chat/ChatInput.java`
- Role: The chat input bar of the Arkea chat: as wide as the chat plates and right under them, in the space the chat leaves when it moves up while the chat screen is open (`lift`, eased with `Motion.CHAT_OPEN`). Places the vanilla `EditBox` on the bar, paints the bar in the chat scale (dark plate, accent stripe and divider) and gives the command suggestions their anchor above it.

#### ChatScroll
- Path: `src/main/java/com/aryston/arkea/hud/chat/ChatScroll.java`
- Role: Smooth chat scrolling: an offset in lines that eases back to zero (`Motion.CHAT_SCROLL`) after every scroll, the extra lines below and above the page needed while it moves, the first line and line count `forEachLine` visits and the resulting vertical shift.

#### ChatEntries, ChatEntry
- Path: `src/main/java/com/aryston/arkea/hud/chat/ChatEntries.java`, `src/main/java/com/aryston/arkea/hud/chat/ChatEntry.java`
- Role: What Arkea knows about each `GuiMessage` (weak map): its sender, when it arrived (`HudClock`, so the interface check can slow it down), its first wrapped line and line count, and the entrance progress (`Motion.CHAT_ENTRY`, standard easing). Senders announced by `ChatSenders` wait in a weak map keyed by the final message component until the message is wrapped. Messages older than two GUI ticks when first seen (history after a rescale or a reload) do not animate.

#### ChatSender
- Path: `src/main/java/com/aryston/arkea/hud/chat/ChatSender.java`
- Role: Sender id and name color of a message; draws the face from the player list skin, or the default skin of the id when the player has left.

#### ChatSenders
- Path: `src/main/java/com/aryston/arkea/hud/chat/ChatSenders.java`
- Role: `ClientChatReceivedEvent` listener at the lowest priority. Player chat takes the sender id from the event; disguised chat looks the name of the bound chat type up in the player list; system chat (plugins) guesses the sender from an online player name that stands as a whole word before the first `:`, `>` or `»` within 48 characters. With the Arkea chat on, plain `chat.type.text` messages are rebuilt as colored name, a gray "»" and the message (team colors and the name's click events are kept); the local player's name takes the accent color, others a hue from their id.

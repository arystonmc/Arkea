package com.aryston.arkea.hud.chat;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import java.util.List;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;

public final class ChatEntry {
    static final long SETTLED = 0L;

    private final @Nullable ChatSender sender;
    private final long born;
    private @Nullable FormattedCharSequence firstLine;
    private int lineCount = 1;

    ChatEntry(@Nullable ChatSender sender, long born) {
        this.sender = sender;
        this.born = born;
    }

    public @Nullable ChatSender sender() {
        return this.sender;
    }

    public int lineCount() {
        return this.lineCount;
    }

    public boolean startsWith(FormattedCharSequence line) {
        return this.firstLine == line;
    }

    public float appear(long now) {
        if (this.born == SETTLED) {
            return 1.0F;
        }
        float progress = Math.clamp((now - this.born) / (float) Motion.CHAT_ENTRY, 0.0F, 1.0F);
        return Easing.STANDARD.apply(progress);
    }

    void lines(List<FormattedCharSequence> lines) {
        this.firstLine = lines.isEmpty() ? null : lines.getFirst();
        this.lineCount = Math.max(1, lines.size());
    }
}

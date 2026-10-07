package com.aryston.arkea.background;

import org.jcodec.common.model.Packet;

record VideoPlan(double origin, int fps, int maxFrames, int totalFrames, boolean h264) {
    double time(Packet packet) {
        return packet.getPtsD() - this.origin;
    }

    boolean canSkip(Packet packet) {
        return this.h264 && NalUnits.isDisposableFrame(packet.getData());
    }
}

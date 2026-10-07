package com.aryston.arkea.background;

import java.nio.file.Path;

public final class ImportJob {
    private final String name;
    private final Path source;
    private volatile float progress;
    private volatile State state = State.WAITING;
    private volatile String error = "";
    private volatile boolean cancelled;

    ImportJob(String name, Path source) {
        this.name = name;
        this.source = source;
    }

    public String name() {
        return this.name;
    }

    public Path source() {
        return this.source;
    }

    public float progress() {
        return this.progress;
    }

    public State state() {
        return this.state;
    }

    public String error() {
        return this.error;
    }

    public void cancel() {
        this.cancelled = true;
    }

    boolean isCancelled() {
        return this.cancelled;
    }

    void progress(float value) {
        this.progress = value;
    }

    void state(State value) {
        this.state = value;
    }

    void fail(String message) {
        this.error = message;
        this.state = State.FAILED;
    }

    public enum State {
        WAITING,
        RUNNING,
        DONE,
        FAILED,
        CANCELLED
    }
}

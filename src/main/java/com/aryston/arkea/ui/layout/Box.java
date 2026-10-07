package com.aryston.arkea.ui.layout;

public record Box(float x, float y, float width, float height) {
    public static final Box EMPTY = new Box(0.0F, 0.0F, 0.0F, 0.0F);

    public float right() {
        return this.x + this.width;
    }

    public float bottom() {
        return this.y + this.height;
    }

    public float centerX() {
        return this.x + this.width * 0.5F;
    }

    public float centerY() {
        return this.y + this.height * 0.5F;
    }

    public boolean contains(float pointX, float pointY) {
        return pointX >= this.x && pointX < this.right() && pointY >= this.y && pointY < this.bottom();
    }

    public Box offset(float deltaX, float deltaY) {
        return new Box(this.x + deltaX, this.y + deltaY, this.width, this.height);
    }

    public Box inset(float amount) {
        return new Box(this.x + amount, this.y + amount, this.width - amount * 2.0F, this.height - amount * 2.0F);
    }

    public Box expand(float amount) {
        return this.inset(-amount);
    }

    public Box intersect(Box other) {
        float left = Math.max(this.x, other.x);
        float top = Math.max(this.y, other.y);
        float right = Math.min(this.right(), other.right());
        float bottom = Math.min(this.bottom(), other.bottom());
        return new Box(left, top, Math.max(0.0F, right - left), Math.max(0.0F, bottom - top));
    }
}

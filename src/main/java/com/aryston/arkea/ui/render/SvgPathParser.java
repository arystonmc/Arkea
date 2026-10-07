package com.aryston.arkea.ui.render;

import java.util.ArrayList;
import java.util.List;

public final class SvgPathParser {
    private static final int CURVE_SEGMENTS = 12;
    private static final double ARC_SEGMENT_ANGLE = Math.PI / 12.0;
    private static final double FULL_TURN = Math.PI * 2.0;
    private static final float MIN_POINT_DISTANCE = 1.0E-4F;

    private final PathTokenizer tokens;
    private final List<Polyline> polylines = new ArrayList<>();
    private final List<Float> current = new ArrayList<>();
    private float x;
    private float y;
    private float startX;
    private float startY;
    private float controlX;
    private float controlY;
    private char previousCommand;

    private SvgPathParser(String path) {
        this.tokens = new PathTokenizer(path);
    }

    public static List<Polyline> parse(String path) {
        SvgPathParser parser = new SvgPathParser(path);
        parser.run();
        return List.copyOf(parser.polylines);
    }

    private void run() {
        char command = 0;
        while (this.tokens.hasMore()) {
            if (this.tokens.peekIsCommand()) {
                command = this.tokens.nextCommand();
            } else if (command == 0 || Character.toUpperCase(command) == 'Z') {
                throw new IllegalArgumentException("Expected a command in icon path data");
            }
            command = this.execute(command);
        }
        this.finish(false);
    }

    private char execute(char command) {
        boolean relative = Character.isLowerCase(command);
        char upper = Character.toUpperCase(command);
        switch (upper) {
            case 'M' -> {
                this.moveTo(this.absoluteX(this.tokens.nextNumber(), relative), this.absoluteY(this.tokens.nextNumber(), relative));
                this.previousCommand = upper;
                return relative ? 'l' : 'L';
            }
            case 'L' -> this.lineTo(this.absoluteX(this.tokens.nextNumber(), relative), this.absoluteY(this.tokens.nextNumber(), relative));
            case 'H' -> this.lineTo(this.absoluteX(this.tokens.nextNumber(), relative), this.y);
            case 'V' -> this.lineTo(this.x, this.absoluteY(this.tokens.nextNumber(), relative));
            case 'C' -> this.cubic(relative);
            case 'S' -> this.smoothCubic(relative);
            case 'Q' -> this.quadratic(relative);
            case 'T' -> this.smoothQuadratic(relative);
            case 'A' -> this.arc(relative);
            case 'Z' -> this.closePath();
            default -> throw new IllegalArgumentException("Unsupported path command " + command);
        }
        this.previousCommand = upper;
        return command;
    }

    private float absoluteX(float value, boolean relative) {
        return relative ? this.x + value : value;
    }

    private float absoluteY(float value, boolean relative) {
        return relative ? this.y + value : value;
    }

    private void moveTo(float newX, float newY) {
        this.finish(false);
        this.x = newX;
        this.y = newY;
        this.startX = newX;
        this.startY = newY;
        this.addPoint(newX, newY);
    }

    private void lineTo(float newX, float newY) {
        this.ensureStarted();
        this.x = newX;
        this.y = newY;
        this.addPoint(newX, newY);
    }

    private void closePath() {
        this.finish(true);
        this.x = this.startX;
        this.y = this.startY;
    }

    private void cubic(boolean relative) {
        float firstX = this.absoluteX(this.tokens.nextNumber(), relative);
        float firstY = this.absoluteY(this.tokens.nextNumber(), relative);
        float secondX = this.absoluteX(this.tokens.nextNumber(), relative);
        float secondY = this.absoluteY(this.tokens.nextNumber(), relative);
        float endX = this.absoluteX(this.tokens.nextNumber(), relative);
        float endY = this.absoluteY(this.tokens.nextNumber(), relative);
        this.cubicTo(firstX, firstY, secondX, secondY, endX, endY);
    }

    private void smoothCubic(boolean relative) {
        boolean continues = this.previousCommand == 'C' || this.previousCommand == 'S';
        float firstX = continues ? 2.0F * this.x - this.controlX : this.x;
        float firstY = continues ? 2.0F * this.y - this.controlY : this.y;
        float secondX = this.absoluteX(this.tokens.nextNumber(), relative);
        float secondY = this.absoluteY(this.tokens.nextNumber(), relative);
        float endX = this.absoluteX(this.tokens.nextNumber(), relative);
        float endY = this.absoluteY(this.tokens.nextNumber(), relative);
        this.cubicTo(firstX, firstY, secondX, secondY, endX, endY);
    }

    private void cubicTo(float firstX, float firstY, float secondX, float secondY, float endX, float endY) {
        this.ensureStarted();
        float originX = this.x;
        float originY = this.y;
        for (int step = 1; step <= CURVE_SEGMENTS; step++) {
            float t = step / (float) CURVE_SEGMENTS;
            float inverse = 1.0F - t;
            float a = inverse * inverse * inverse;
            float b = 3.0F * inverse * inverse * t;
            float c = 3.0F * inverse * t * t;
            float d = t * t * t;
            this.addPoint(a * originX + b * firstX + c * secondX + d * endX, a * originY + b * firstY + c * secondY + d * endY);
        }
        this.controlX = secondX;
        this.controlY = secondY;
        this.x = endX;
        this.y = endY;
    }

    private void quadratic(boolean relative) {
        float controlPointX = this.absoluteX(this.tokens.nextNumber(), relative);
        float controlPointY = this.absoluteY(this.tokens.nextNumber(), relative);
        float endX = this.absoluteX(this.tokens.nextNumber(), relative);
        float endY = this.absoluteY(this.tokens.nextNumber(), relative);
        this.quadraticTo(controlPointX, controlPointY, endX, endY);
    }

    private void smoothQuadratic(boolean relative) {
        boolean continues = this.previousCommand == 'Q' || this.previousCommand == 'T';
        float controlPointX = continues ? 2.0F * this.x - this.controlX : this.x;
        float controlPointY = continues ? 2.0F * this.y - this.controlY : this.y;
        float endX = this.absoluteX(this.tokens.nextNumber(), relative);
        float endY = this.absoluteY(this.tokens.nextNumber(), relative);
        this.quadraticTo(controlPointX, controlPointY, endX, endY);
    }

    private void quadraticTo(float controlPointX, float controlPointY, float endX, float endY) {
        this.ensureStarted();
        float originX = this.x;
        float originY = this.y;
        for (int step = 1; step <= CURVE_SEGMENTS; step++) {
            float t = step / (float) CURVE_SEGMENTS;
            float inverse = 1.0F - t;
            float a = inverse * inverse;
            float b = 2.0F * inverse * t;
            float c = t * t;
            this.addPoint(a * originX + b * controlPointX + c * endX, a * originY + b * controlPointY + c * endY);
        }
        this.controlX = controlPointX;
        this.controlY = controlPointY;
        this.x = endX;
        this.y = endY;
    }

    private void arc(boolean relative) {
        float radiusX = Math.abs(this.tokens.nextNumber());
        float radiusY = Math.abs(this.tokens.nextNumber());
        double rotation = Math.toRadians(this.tokens.nextNumber());
        boolean largeArc = this.tokens.nextFlag();
        boolean sweep = this.tokens.nextFlag();
        float endX = this.absoluteX(this.tokens.nextNumber(), relative);
        float endY = this.absoluteY(this.tokens.nextNumber(), relative);
        if (radiusX == 0.0F || radiusY == 0.0F) {
            this.lineTo(endX, endY);
            return;
        }
        this.ensureStarted();
        this.arcTo(new ArcRequest(radiusX, radiusY, rotation, largeArc, sweep, endX, endY));
    }

    private void arcTo(ArcRequest request) {
        double cos = Math.cos(request.rotation());
        double sin = Math.sin(request.rotation());
        double halfDeltaX = (this.x - request.endX()) / 2.0;
        double halfDeltaY = (this.y - request.endY()) / 2.0;
        double primeX = cos * halfDeltaX + sin * halfDeltaY;
        double primeY = -sin * halfDeltaX + cos * halfDeltaY;
        double radiusX = request.radiusX();
        double radiusY = request.radiusY();
        double lambda = primeX * primeX / (radiusX * radiusX) + primeY * primeY / (radiusY * radiusY);
        if (lambda > 1.0) {
            double grow = Math.sqrt(lambda);
            radiusX *= grow;
            radiusY *= grow;
        }
        double numerator = radiusX * radiusX * radiusY * radiusY - radiusX * radiusX * primeY * primeY - radiusY * radiusY * primeX * primeX;
        double denominator = radiusX * radiusX * primeY * primeY + radiusY * radiusY * primeX * primeX;
        double factor = Math.sqrt(Math.max(0.0, numerator / denominator));
        if (request.largeArc() == request.sweep()) {
            factor = -factor;
        }
        double centerPrimeX = factor * radiusX * primeY / radiusY;
        double centerPrimeY = -factor * radiusY * primeX / radiusX;
        double centerX = cos * centerPrimeX - sin * centerPrimeY + (this.x + request.endX()) / 2.0;
        double centerY = sin * centerPrimeX + cos * centerPrimeY + (this.y + request.endY()) / 2.0;
        double startAngle = angle(1.0, 0.0, (primeX - centerPrimeX) / radiusX, (primeY - centerPrimeY) / radiusY);
        double sweepAngle = angle((primeX - centerPrimeX) / radiusX, (primeY - centerPrimeY) / radiusY,
            (-primeX - centerPrimeX) / radiusX, (-primeY - centerPrimeY) / radiusY);
        if (!request.sweep() && sweepAngle > 0.0) {
            sweepAngle -= FULL_TURN;
        } else if (request.sweep() && sweepAngle < 0.0) {
            sweepAngle += FULL_TURN;
        }
        int segments = Math.max(1, (int) Math.ceil(Math.abs(sweepAngle) / ARC_SEGMENT_ANGLE));
        for (int step = 1; step <= segments; step++) {
            double theta = startAngle + sweepAngle * step / segments;
            double pointX = cos * radiusX * Math.cos(theta) - sin * radiusY * Math.sin(theta) + centerX;
            double pointY = sin * radiusX * Math.cos(theta) + cos * radiusY * Math.sin(theta) + centerY;
            this.addPoint((float) pointX, (float) pointY);
        }
        this.x = request.endX();
        this.y = request.endY();
    }

    private static double angle(double fromX, double fromY, double toX, double toY) {
        return Math.atan2(fromX * toY - fromY * toX, fromX * toX + fromY * toY);
    }

    private void ensureStarted() {
        if (this.current.isEmpty()) {
            this.addPoint(this.x, this.y);
        }
    }

    private void addPoint(float pointX, float pointY) {
        int size = this.current.size();
        if (size >= 2) {
            float lastX = this.current.get(size - 2);
            float lastY = this.current.get(size - 1);
            if (Math.abs(lastX - pointX) < MIN_POINT_DISTANCE && Math.abs(lastY - pointY) < MIN_POINT_DISTANCE) {
                return;
            }
        }
        this.current.add(pointX);
        this.current.add(pointY);
    }

    private void finish(boolean closed) {
        if (this.current.isEmpty()) {
            return;
        }
        float[] points = new float[this.current.size()];
        for (int index = 0; index < points.length; index++) {
            points[index] = this.current.get(index);
        }
        this.polylines.add(new Polyline(points, closed));
        this.current.clear();
    }

    private record ArcRequest(float radiusX, float radiusY, double rotation, boolean largeArc, boolean sweep, float endX, float endY) {
    }
}

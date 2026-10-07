package com.aryston.arkea.ui.render;

import java.util.List;

public record Icon(List<Polyline> polylines, float viewWidth, float viewHeight, IconStyle style) {
    private static final float DEFAULT_VIEW_SIZE = 16.0F;

    public static Icon stroke(String path) {
        return new Icon(SvgPathParser.parse(path), DEFAULT_VIEW_SIZE, DEFAULT_VIEW_SIZE, IconStyle.STROKE);
    }

    public static Icon fill(String path) {
        return new Icon(SvgPathParser.parse(path), DEFAULT_VIEW_SIZE, DEFAULT_VIEW_SIZE, IconStyle.FILL);
    }

    public static Icon pixels(String path, float viewWidth, float viewHeight) {
        return new Icon(SvgPathParser.parse(path), viewWidth, viewHeight, IconStyle.FILL);
    }
}

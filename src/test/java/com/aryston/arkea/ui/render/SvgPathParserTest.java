package com.aryston.arkea.ui.render;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class SvgPathParserTest {
    private static final float TOLERANCE = 1.0E-3F;

    @Test
    void parsesAbsoluteAndRelativeLines() {
        List<Polyline> polylines = SvgPathParser.parse("M2 4h12M2 8l3 2");
        assertEquals(2, polylines.size());
        Polyline first = polylines.getFirst();
        assertEquals(14.0F, first.x(1), TOLERANCE);
        assertEquals(4.0F, first.y(1), TOLERANCE);
        Polyline second = polylines.get(1);
        assertEquals(5.0F, second.x(1), TOLERANCE);
        assertEquals(10.0F, second.y(1), TOLERANCE);
    }

    @Test
    void closePathMarksThePolylineClosed() {
        Polyline square = SvgPathParser.parse("M0 0h1v1H0z").getFirst();
        assertTrue(square.closed());
        assertEquals(4, square.size());
    }

    @Test
    void implicitCommandsRepeatAfterMove() {
        Polyline polyline = SvgPathParser.parse("M2 4.5L8 1.5l6 3v7l-6 3-6-3z").getFirst();
        assertEquals(2.0F, polyline.x(polyline.size() - 1), TOLERANCE);
        assertEquals(11.5F, polyline.y(polyline.size() - 1), TOLERANCE);
    }

    @Test
    void compactNumbersAreSeparated() {
        Polyline polyline = SvgPathParser.parse("M.5-1.5L1.5.5").getFirst();
        assertEquals(0.5F, polyline.x(0), TOLERANCE);
        assertEquals(-1.5F, polyline.y(0), TOLERANCE);
        assertEquals(1.5F, polyline.x(1), TOLERANCE);
        assertEquals(0.5F, polyline.y(1), TOLERANCE);
    }

    @Test
    void fullCircleArcsStayOnTheRadius() {
        Polyline circle = SvgPathParser.parse("M8 1.5a6.5 6.5 0 1 0 0 13 6.5 6.5 0 0 0 0-13z").getFirst();
        assertTrue(circle.size() > 16);
        for (int index = 0; index < circle.size(); index++) {
            float deltaX = circle.x(index) - 8.0F;
            float deltaY = circle.y(index) - 8.0F;
            assertEquals(6.5F, (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY), 0.01F);
        }
    }

    @Test
    void cubicCurvesEndAtTheirEndPoint() {
        Polyline curve = SvgPathParser.parse("M8 1.5c2 2 2 11 0 13").getFirst();
        assertEquals(8.0F, curve.x(curve.size() - 1), TOLERANCE);
        assertEquals(14.5F, curve.y(curve.size() - 1), TOLERANCE);
        assertFalse(curve.closed());
    }

    @Test
    void everyCatalogueIconParses() throws IllegalAccessException {
        for (var field : Icons.class.getFields()) {
            Icon icon = (Icon) field.get(null);
            assertFalse(icon.polylines().isEmpty(), field.getName());
        }
    }

    @Test
    void rejectsNumbersBeforeTheFirstCommand() {
        assertThrows(IllegalArgumentException.class, () -> SvgPathParser.parse("4 4L2 2"));
    }
}

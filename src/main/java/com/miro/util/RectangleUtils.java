package com.miro.util;

import com.infomatiq.jsi.Rectangle;
import com.miro.entity.Widget;

/**
 * Useful methods to work with rectangles.
 */
public class RectangleUtils {

    private RectangleUtils() {
        throw new UnsupportedOperationException();
    }

    private static Rectangle formRectangle(int x, int y, int width, int height) {
        float halfWidth = width / 2.0f;
        float halfHeight = height / 2.0f;
        return new Rectangle(
                x + halfWidth,
                y + halfHeight,
                x - halfWidth,
                y - halfHeight
        );
    }

    public static Rectangle getRectangle(Widget widget) {
        return formRectangle(widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
    }
}

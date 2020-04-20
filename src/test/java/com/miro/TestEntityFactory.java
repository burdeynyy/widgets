package com.miro;

import com.miro.entity.Widget;
import com.miro.web.dto.WidgetDto;

import static com.miro.TestConstants.*;
import static com.miro.TestConstants.Y_CHANGED;


/**
 * Factory to create test objects.
 */
public final class TestEntityFactory {

    private TestEntityFactory() {
        throw new UnsupportedOperationException();
    }

    public static Widget createDefaultWidget() {
        Widget widget = new Widget();
        widget.setX(X);
        widget.setY(Y);
        widget.setZ(Z_INDEX);
        widget.setWidth(WIDTH);
        widget.setHeight(HEIGHT);

        return widget;
    }

    public static WidgetDto createDefaultWidgetDto() {
        WidgetDto widget = new WidgetDto();
        widget.setX(X);
        widget.setY(Y);
        widget.setZ(Z_INDEX);
        widget.setWidth(WIDTH);
        widget.setHeight(HEIGHT);

        return widget;
    }

    public static Widget createChangedDefaultWidget(Integer id) {
        Widget widget = new Widget();
        widget.setId(id);
        widget.setX(X_CHANGED);
        widget.setY(Y_CHANGED);
        widget.setZ(Z_INDEX_CHANGED);
        widget.setWidth(WIDTH_CHANGED);
        widget.setHeight(HEIGHT_CHANGED);

        return widget;
    }


    public static Widget createWidgetWithZSpecified(Integer zIndex) {
        Widget widget = new Widget();
        widget.setX(X);
        widget.setY(Y);
        widget.setZ(zIndex);
        widget.setWidth(WIDTH);
        widget.setHeight(HEIGHT);

        return widget;
    }

    public static Widget createDefaultWidgetWithIdAndZSpecified(Integer id, Integer zIndexChanged) {
        Widget widget = new Widget();
        widget.setId(id);
        widget.setX(X);
        widget.setY(Y);
        widget.setZ(zIndexChanged);
        widget.setWidth(WIDTH);
        widget.setHeight(HEIGHT);
        return widget;
    }

}

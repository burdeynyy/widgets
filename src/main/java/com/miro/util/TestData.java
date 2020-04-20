package com.miro.util;

import com.miro.entity.Widget;

import java.util.ArrayList;
import java.util.List;

/**
 * Class containing data for native data storage that are used as initial data and in tests.
 * Graphical representation of the data you can find in the project root folder.
 */
public class TestData {

    private TestData() {
        throw new UnsupportedOperationException();
    }

    public static List<Widget> getTestData() {
        List<Widget> widgets = new ArrayList<>();

        int[][] coords = {
                //x  y  w  h
                {-6, 9, 4, 4},
                {0, 9, 4, 4},
                {7, 8, 4, 4},
                {7, 6, 8, 4},
                {-3, 5, 2, 2},
                {-6, 2, 2, 8},
                {3, 3, 4, 4},
                {1, 0, 4, 6},
                {7, 0, 2, 4},
                {-1, -1, 4, 4},
                {1, -1, 2, 2},
                {-2, -2, 2, 2},
                {6, -6, 6, 6},
                {-5, -7, 4, 6},
        };
        for (int[] coord : coords) {
            widgets.add(makeWidget(coord[0], coord[1], coord[2], coord[3]));
        }

        return widgets;
    }


    private static Widget makeWidget(int x, int y, int width, int height) {
        Widget widget = new Widget();
        widget.setHeight(height);
        widget.setWidth(width);
        widget.setX(x);
        widget.setY(y);
        return widget;
    }

}

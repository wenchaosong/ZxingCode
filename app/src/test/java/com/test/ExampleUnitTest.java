package com.test;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        // 0 --> 125
        int a = 125;

        int mLength = 50;
        double value = 30;
        float y = 48;

        // 0.5
        double cos = Math.cos(2 * Math.PI / 360 * value);
        // -0.3048106211022167
        double sin = Math.sin(2 * Math.PI / 360 * value);

        float y1 = (float) (Math.abs(mLength * Math.cos(value)) + y);
        float y2 = (float) (mLength * Math.cos(value) + y);

        assertEquals(10, sin, 0);
    }
}
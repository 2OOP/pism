package org.toop.frontend.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ColorTest {

    private Color color;

    @BeforeEach
    void setUp() {
        color = new Color(0.1f, 0.5f, 0.9f);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(0.1f, color.r(), 0.0001, "Red component should match");
        assertEquals(0.5f, color.g(), 0.0001, "Green component should match");
        assertEquals(0.9f, color.b(), 0.0001, "Blue component should match");
    }

    @Test
    void testDifferentColorValues() {
        Color c = new Color(1.0f, 0.0f, 0.5f);

        assertEquals(1.0f, c.r(), 0.0001);
        assertEquals(0.0f, c.g(), 0.0001);
        assertEquals(0.5f, c.b(), 0.0001);
    }
}

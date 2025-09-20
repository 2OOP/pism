package org.toop.frontend.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoundsTest {

    private Bounds bounds;

    @BeforeEach
    void setUp() {
        bounds = new Bounds(10, 20, 100, 50);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(10, bounds.getX());
        assertEquals(20, bounds.getY());
        assertEquals(100, bounds.getWidth());
        assertEquals(50, bounds.getHeight());
    }

    @Test
    void testSetUpdatesFields() {
        bounds.set(5, 15, 50, 25);

        assertEquals(5, bounds.getX());
        assertEquals(15, bounds.getY());
        assertEquals(50, bounds.getWidth());
        assertEquals(25, bounds.getHeight());
    }

    @Test
    void testCheckInsideBounds() {
        // Points inside the bounds
        assertTrue(bounds.check(10, 20));      // top-left corner
        assertTrue(bounds.check(110, 70));     // bottom-right corner
        assertTrue(bounds.check(60, 45));      // inside
    }

    @Test
    void testCheckOutsideBounds() {
        // Points outside the bounds
        assertFalse(bounds.check(9, 20));      // left
        assertFalse(bounds.check(10, 19));     // above
        assertFalse(bounds.check(111, 70));    // right
        assertFalse(bounds.check(110, 71));    // below
    }

    @Test
    void testCheckOnEdgeBounds() {
        // Points on the edges should be considered inside
        assertTrue(bounds.check(10, 20));      // top-left
        assertTrue(bounds.check(110, 20));     // top-right
        assertTrue(bounds.check(10, 70));      // bottom-left
        assertTrue(bounds.check(110, 70));     // bottom-right
    }
}
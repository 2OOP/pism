//package org.toop.game;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//class PlayerTest {
//	private Player playerA;
//	private Player playerB;
//	private Player playerC;
//
//	@BeforeEach
//	void setup() {
//		playerA = new Player("test A", 'x', 'Z', 'i');
//		playerB = new Player("test B", 'O', (char)12, (char)-34, 's');
//		playerC = new Player("test C", (char)9, '9', (char)-9, '0', 'X', 'O');
//	}
//
//	@Test
//	void testNameGetter_returnsTrueForValidName() {
//		assertEquals("test A", playerA.name());
//		assertEquals("test B", playerB.name());
//		assertEquals("test C", playerC.name());
//	}
//
//	@Test
//	void testValuesGetter_returnsTrueForValidValues() {
//		final char[] valuesA = playerA.values();
//		assertEquals('x', valuesA[0]);
//		assertEquals('Z', valuesA[1]);
//		assertEquals('i', valuesA[2]);
//
//		final char[] valuesB = playerB.values();
//		assertEquals('O', valuesB[0]);
//		assertEquals(12, valuesB[1]);
//		assertEquals((char)-34, valuesB[2]);
//		assertEquals('s', valuesB[3]);
//
//		final char[] valuesC = playerC.values();
//		assertEquals((char)9, valuesC[0]);
//		assertEquals('9', valuesC[1]);
//		assertEquals((char)-9, valuesC[2]);
//		assertEquals('0', valuesC[3]);
//		assertEquals('X', valuesC[4]);
//		assertEquals('O', valuesC[5]);
//	}
//}
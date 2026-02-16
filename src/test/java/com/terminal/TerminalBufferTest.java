package com.terminal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class TerminalBufferTest {

    private TerminalBuffer buffer;

    @BeforeEach
    void setUp() {
        buffer = new TerminalBuffer(80, 24, 1000);
    }// 80 x 24 as said in instructions

    @Test
    void testBufferCreation() {
        assertEquals(80, buffer.getWidth());
        assertEquals(24, buffer.getHeight());
        assertEquals(1000, buffer.getMaxScrollback());
        assertEquals(0, buffer.getCursorRow());
        assertEquals(0, buffer.getCursorCol());
    }

    @Test
    void testWriteText() {
        buffer.writeText("Hello");

        assertEquals(5, buffer.getCursorCol());
        assertEquals(0, buffer.getCursorRow());

        buffer.writeText("I'm Neofytos");
        assertEquals(17, buffer.getCursorCol());
        assertTrue(buffer.getLineAsString(0).startsWith("Hello"));
    }

    @Test
    void testWriteTextWrapping() {
        String longText = "A".repeat(84);
        buffer.writeText(longText);

        //expecting first row to be filled with 80 A's, and second row to include the remaining 4
        assertEquals(4, buffer.getCursorCol());
        assertEquals(1, buffer.getCursorRow());
    }

    @Test
    void testCursorMovement() {
        buffer.moveCursorDown(5);
        buffer.moveCursorRight(10);

        assertEquals(5, buffer.getCursorRow());
        assertEquals(10, buffer.getCursorCol());
    }

    @Test
    void testCursorBounds() {
        buffer.moveCursorDown(100);
        buffer.moveCursorRight(200);

        assertEquals(23, buffer.getCursorRow());  // cannot exceed max row
        assertEquals(79, buffer.getCursorCol());  // cannot exceed max col

        buffer.moveCursorUp(100);
        buffer.moveCursorLeft(200);
        //cursor returns where he started at (0,0) staying in bounds
        assertEquals(0, buffer.getCursorRow());
        assertEquals(0, buffer.getCursorCol());
    }

    @Test
    void testSetCursorPosition() {
        buffer.setCursorPosition(10, 20);
        assertEquals(10, buffer.getCursorRow());
        assertEquals(20, buffer.getCursorCol());

        //checking bounds
        buffer.setCursorPosition(-5, -10);
        assertEquals(0, buffer.getCursorRow());
        assertEquals(0, buffer.getCursorCol());

        //checking bounds
        buffer.setCursorPosition(100, 100);
        assertEquals(23, buffer.getCursorRow());
        assertEquals(79, buffer.getCursorCol());
    }

    @Test
    void testInsertText() {
        buffer.writeText("Sunny");
        buffer.setCursorPosition(0, 5);
        buffer.insertText(" day!");

        assertTrue(buffer.getLineAsString(0).contains("Sunny day!"));
    }

    @Test
    void testFillLine() {
        buffer.fillLine(0, '&');// Should fill entire line with &
        String line = buffer.getLineAsString(0);

        assertTrue(line.startsWith("&&&"));
        assertEquals(80, line.length());
    }

    @Test
    void testFillLineOutOfBounds() {
        buffer.fillLine(-1, 'X');      // Below range
        buffer.fillLine(24, 'X');      // Above range (max is 23)
        buffer.fillLine(100, 'X');     // Way above range

        // Verify nothing was written
        assertEquals("", buffer.getLineAsString(-1));
        assertEquals("", buffer.getLineAsString(100));

        // Verify valid rows are still empty
        for (int i = 0; i < 24; i++) {
            assertFalse(buffer.getLineAsString(i).contains("X"));
        }
    }

    @Test
    void testClearScreen() {
        buffer.writeText("Dummy content");
        buffer.clearScreen();

        // Cursor should reset to origin
        assertEquals(0, buffer.getCursorRow());
        assertEquals(0, buffer.getCursorCol());

        // Screen should contain only empty cells (spaces) and newlines
        String content = buffer.getScreenContent();
        for (char c : content.toCharArray()) {
            if (c != ' ' && c != '\n') {
                fail("Screen should be empty after clear");
            }
        }
    }
}
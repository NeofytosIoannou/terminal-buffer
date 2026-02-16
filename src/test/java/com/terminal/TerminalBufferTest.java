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

    @Test
    void testInsertLineAtBottom() {
        buffer.writeText("Line 1");
        int initialSize = buffer.getScrollbackSize();

        for (int i = 0; i < 30; i++) {
            buffer.insertLineAtBottom();
        }

        // All 30 inserted lines push screen content to scrollback
        assertTrue(buffer.getScrollbackSize() > initialSize);
        assertEquals(30, buffer.getScrollbackSize());  // Add this line
    }

    @Test
    void testScrollback() {
        for (int i = 0; i < 30; i++) {
            buffer.setCursorPosition(23, 0);
            buffer.writeText("Line " + i);
            buffer.insertLineAtBottom();
        }

        assertTrue(buffer.getScrollbackSize() > 0);
        assertTrue(buffer.getScrollbackSize() <= 1000);
        assertEquals(30,buffer.getScrollbackSize());
    }
    @Test
    void testScrollbackPreservesContent() {
        // Fill screen with numbered lines
        for (int i = 1; i <= 24; i++) {
            buffer.setCursorPosition(i - 1, 0);
            buffer.writeText("Line " + i);
        }

        assertEquals(0, buffer.getScrollbackSize());

        // Insert 5 lines at bottom
        for (int i = 0; i < 5; i++) {
            buffer.insertLineAtBottom();
        }

        // 5 lines scrolled off
        assertEquals(5, buffer.getScrollbackSize());

        // Verify scrollback content
        assertEquals("Line 1", buffer.getLineAsString(-5).trim());
        assertEquals("Line 2", buffer.getLineAsString(-4).trim());
        assertEquals("Line 3", buffer.getLineAsString(-3).trim());
        assertEquals("Line 4", buffer.getLineAsString(-2).trim());
        assertEquals("Line 5", buffer.getLineAsString(-1).trim());

        // Screen top now shows Line 6 (not Line 1)
        assertTrue(buffer.getLineAsString(0).startsWith("Line 6"));

        // Screen bottom has empty lines
        assertTrue(buffer.getLineAsString(23).trim().isEmpty());
    }



    @Test
    void testScrollbackLimit() {
        TerminalBuffer smallBuffer = new TerminalBuffer(80, 24, 10);

        for (int i = 0; i < 50; i++) {
            smallBuffer.insertLineAtBottom();
        }

        assertTrue(smallBuffer.getScrollbackSize() <= 10);
    }

    @Test
    void testGetCharAt() {
        buffer.writeText("Test");

        assertEquals('T', buffer.getCharAt(0, 0));
        assertEquals('e', buffer.getCharAt(0, 1));
        assertEquals('s', buffer.getCharAt(0, 2));
        assertEquals('t', buffer.getCharAt(0, 3));
    }

    @Test
    void testGetLineAsString() {
        buffer.writeText("Hello World");
        String line = buffer.getLineAsString(0);

        assertTrue(line.startsWith("Hello World"));
        assertEquals(80, line.length());
    }

    @Test
    void testGetScreenContent() {
        buffer.writeText("Line 1");
        buffer.setCursorPosition(1, 0);
        buffer.writeText("Line 2");

        String content = buffer.getScreenContent();
        String[] lines = content.split("\n");

        // Should return all 24 screen rows, even if empty
        assertEquals(24, lines.length);
        assertTrue(lines[0].startsWith("Line 1"));
        assertTrue(lines[1].startsWith("Line 2"));

        // Row 2 should be empty (80 spaces)
        assertEquals(80, lines[2].length());
        assertTrue(lines[2].trim().isEmpty());
    }
    @Test
    void testGetScreenContentEmpty() {
        String content = buffer.getScreenContent();
        String[] lines = content.split("\n");

        // Even empty screen should return 24 rows
        assertEquals(24, lines.length);

        // All rows should be 80 spaces
        for (String line : lines) {
            assertEquals(80, line.length());
            assertTrue(line.trim().isEmpty());
        }
    }

    @Test
    void testAttributes() {
        CellAttributes.Style boldStyle = new CellAttributes.Style(true, false, false);
        buffer.setCurrentAttributes(
                CellAttributes.Color.RED,
                CellAttributes.Color.BLACK,
                boldStyle
        );

        buffer.writeText("Red");

        CellAttributes attrs = buffer.getAttributesAt(0, 0);
        assertEquals(CellAttributes.Color.RED, attrs.getForeground());
        assertEquals(CellAttributes.Color.BLACK, attrs.getBackground());
        assertTrue(attrs.getStyle().isBold());
    }

    @Test
    void testClearAll() {
        for (int i = 0; i < 30; i++) {
            buffer.insertLineAtBottom();
        }

        assertTrue(buffer.getScrollbackSize() > 0);
        buffer.clearAll();
        assertEquals(0, buffer.getScrollbackSize());
    }

}

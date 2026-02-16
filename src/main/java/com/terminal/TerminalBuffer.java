package com.terminal;

import java.util.*;

public class TerminalBuffer {
    private int width;
    private int height;
    private final int maxScrollback;

    private  List<Line> screen;
    // Scrollback uses Deque because we add/remove from both ends
    private  Deque<Line> scrollback;

    private int cursorRow;
    private int cursorCol;

    private CellAttributes currentAttributes;

    public TerminalBuffer(int width, int height, int maxScrollback) {
        this.width = width;
        this.height = height;
        this.maxScrollback = maxScrollback;

        this.screen = new ArrayList<>(height);
        for (int i = 0; i < height; i++) {
            screen.add(new Line(width));
        }

        this.scrollback = new LinkedList<>();
        this.cursorRow = 0;
        this.cursorCol = 0;
        this.currentAttributes = new CellAttributes();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMaxScrollback() {
        return maxScrollback;
    }

    public void setCurrentAttributes(CellAttributes.Color foreground, CellAttributes.Color background, CellAttributes.Style style) {
        this.currentAttributes = new CellAttributes(foreground, background, style);
    }

    public CellAttributes getCurrentAttributes() {
        return currentAttributes.copy();
    }

    public int getCursorRow() {
        return cursorRow;
    }

    public int getCursorCol() {
        return cursorCol;
    }



    public void setCursorPosition(int row, int col) {
        this.cursorRow = Math.max(0, Math.min(height - 1, row));
        this.cursorCol = Math.max(0, Math.min(width - 1, col));
    }

    public void moveCursorUp(int n) {
        cursorRow = Math.max(0, cursorRow - n);
    }

    public void moveCursorDown(int n) {
        cursorRow = Math.min(height - 1, cursorRow + n);
    }

    public void moveCursorLeft(int n) {
        cursorCol = Math.max(0, cursorCol - n);
    }

    public void moveCursorRight(int n) {
        cursorCol = Math.min(width - 1, cursorCol + n);
    }

    //overwrite mode - replacing existing characters
    public void writeText(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }

        for (char ch : text.toCharArray()) {
            if (cursorCol >= width) {
                cursorCol = 0;
                cursorRow++;
                if (cursorRow >= height) {
                    scrollUp();
                    cursorRow = height - 1;
                }
            }

            Cell cell = new Cell(ch, currentAttributes);
            screen.get(cursorRow).setCell(cursorCol, cell);

            if (cell.isWide()) {
                cursorCol++;
                if (cursorCol < width) {
                    Cell emptyCell = new Cell(' ', currentAttributes);
                    screen.get(cursorRow).setCell(cursorCol, emptyCell);
                }
            }

            cursorCol++;
        }
    }
    //writing mode - writing where in blank spaces
    public void insertText(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }

        Line currentLine = screen.get(cursorRow);

        for (char ch : text.toCharArray()) {
            if (cursorCol >= width) {
                cursorCol = 0;
                cursorRow++;
                if (cursorRow >= height) {
                    scrollUp();
                    cursorRow = height - 1;
                }
                currentLine = screen.get(cursorRow);
            }

            for (int i = width - 1; i > cursorCol; i--) {
                currentLine.setCell(i, currentLine.getCell(i - 1));
            }

            Cell cell = new Cell(ch, currentAttributes);
            currentLine.setCell(cursorCol, cell);

            cursorCol++;
        }
    }

    public void fillLine(int row, char ch) {
        if (row >= 0 && row < height) {
            screen.get(row).fill(ch, currentAttributes);
        }
    }

    public void insertLineAtBottom() {
        screen.add(new Line(width));
        if (screen.size() > height) {
            Line topLine = screen.removeFirst();
            scrollback.addLast(topLine);

            while (scrollback.size() > maxScrollback) {
                scrollback.removeFirst();
            }
        }
    }

    public void clearScreen() {
        for (int i = 0; i < height; i++) {
            screen.set(i, new Line(width));
        }
        cursorRow = 0;
        cursorCol = 0;
    }

    public void clearAll() {
        clearScreen();
        scrollback.clear();
    }

    public char getCharAt(int row, int col) {
        return getCellAt(row, col).getCharacter();
    }

    public CellAttributes getAttributesAt(int row, int col) {
        return getCellAt(row, col).getAttributes();
    }

    private Cell getCellAt(int row, int col) {
        if (col < 0 || col >= width) {
            return new Cell();
        }

        if (row < 0) {
            int scrollbackIndex = scrollback.size() + row;
            if (scrollbackIndex >= 0 && scrollbackIndex < scrollback.size()) {
                return ((LinkedList<Line>) scrollback).get(scrollbackIndex).getCell(col);
            }
            return new Cell();
        } else if (row < height) {
            return screen.get(row).getCell(col);
        }

        return new Cell();
    }

    public String getLineAsString(int row) {
        if (row < 0) {
            int scrollbackIndex = scrollback.size() + row;
            if (scrollbackIndex >= 0 && scrollbackIndex < scrollback.size())
                return "";
        } else if (row < height) {
            return screen.get(row).asString();
        }
        return "";
    }
        private void scrollUp() {
        if (!screen.isEmpty()) {
            Line topLine = screen.remove(0);
            scrollback.addLast(topLine);

            while (scrollback.size() > maxScrollback) {
                scrollback.removeFirst();
            }

            screen.add(new Line(width));
        }
    }

}
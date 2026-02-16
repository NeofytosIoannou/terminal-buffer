package com.terminal;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single row of cells in the terminal.
 */
class Line {
    private List<Cell> cells;

    public Line(int width) {
        this.cells = new ArrayList<>(width);
        for (int i = 0; i < width; i++) {
            cells.add(new Cell());
        }
    }

    public Cell getCell(int col) {
        if (col < 0 || col >= cells.size()) {
            return new Cell();  // return empty cell for out-of-bounds
        }
        return cells.get(col);
    }

    public void setCell(int col, Cell cell) {
        if (col >= 0 && col < cells.size()) {
            cells.set(col, cell.copy());
        }
    }

    public int getWidth() {
        return cells.size();
    }

    /**
     * Converts the line to a string by concatenating all cell characters.
     */
    public String asString() {
        StringBuilder sb = new StringBuilder();
        for (Cell cell : cells) {
            sb.append(cell.getCharacter());
        }
        return sb.toString();
    }

    public void fill(char ch, CellAttributes attrs) {
        for (int i = 0; i < cells.size(); i++) {
            cells.set(i, new Cell(ch, attrs));
        }
    }

    /**
     * Resizes the line by adding or removing cells.
     * When shrinking, cells are truncated from the end.
     */
    public void resize(int newWidth) {
        if (newWidth > cells.size()) {
            // expand - add empty cells
            while (cells.size() < newWidth) {
                cells.add(new Cell());
            }
        } else if (newWidth < cells.size()) {
            // shrink - truncate
            cells = new ArrayList<>(cells.subList(0, newWidth));
        }
    }
}
package com.terminal;

/**
 * Represents a single character cell in the terminal buffer.
 * Each cell contains a character, visual attributes, and width information.
 */
public class Cell {
    private char character;
    private CellAttributes attributes;
    private boolean isWide;  //Bonus: true for CJK characters that occupy 2 cells

    public Cell() {
        this.character = ' ';
        this.attributes = new CellAttributes();
        this.isWide = false;
    }

    public Cell(char character, CellAttributes attributes) {
        this.character = character;
        this.attributes = attributes.copy();
        this.isWide = isWideCharacter(character);
    }

    public Cell copy() {
        Cell cell = new Cell();
        cell.character = this.character;
        cell.attributes = this.attributes.copy();
        cell.isWide = this.isWide;
        return cell;
    }

    /**
     * Checks if a character is wide (occupies 2 terminal cells).
     * Covers CJK ideographs and fullwidth forms.
     */
    private static boolean isWideCharacter(char ch) {
        int codePoint = (int) ch;
        return (codePoint >= 0x1100 && codePoint <= 0x115F) ||  // Hangul Jamo
                (codePoint >= 0x2E80 && codePoint <= 0x9FFF) ||  // CJK Unified Ideographs
                (codePoint >= 0xAC00 && codePoint <= 0xD7AF) ||  // Hangul Syllables
                (codePoint >= 0xFF00 && codePoint <= 0xFFEF);    // Fullwidth Forms
    }

    public char getCharacter() {
        return character;
    }

    public CellAttributes getAttributes() {
        return attributes;
    }

    public boolean isWide() {
        return isWide;
    }

    public void setCharacter(char character) {
        this.character = character;
        this.isWide = isWideCharacter(character);
    }

    public void setAttributes(CellAttributes attributes) {
        this.attributes = attributes.copy();
    }
}
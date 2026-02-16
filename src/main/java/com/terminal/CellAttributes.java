package com.terminal;

/**
 * Represents the visual attributes of a terminal cell (color and style).
 * Uses defensive copying to prevent shared mutable state.
 */
public class CellAttributes {

    public enum Color {
        BLACK, RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, WHITE,
        BRIGHT_BLACK, BRIGHT_RED, BRIGHT_GREEN, BRIGHT_YELLOW,
        BRIGHT_BLUE, BRIGHT_MAGENTA, BRIGHT_CYAN, BRIGHT_WHITE,
        DEFAULT
    }

    public static class Style {
        private boolean bold;
        private boolean italic;
        private boolean underline;

        public Style() {
            this(false, false, false);
        }

        public Style(boolean bold, boolean italic, boolean underline) {
            this.bold = bold;
            this.italic = italic;
            this.underline = underline;
        }

        public Style copy() {
            return new Style(bold, italic, underline);
        }

        public boolean isBold() { return bold; }
        public boolean isItalic() { return italic; }
        public boolean isUnderline() { return underline; }

        public void setBold(boolean bold) { this.bold = bold; }
        public void setItalic(boolean italic) { this.italic = italic; }
        public void setUnderline(boolean underline) { this.underline = underline; }
    }

    private Color foreground;
    private Color background;
    private Style style;

    public CellAttributes() {
        this.foreground = Color.DEFAULT;
        this.background = Color.DEFAULT;
        this.style = new Style();
    }

    public CellAttributes(Color foreground, Color background, Style style) {
        this.foreground = foreground;
        this.background = background;
        this.style = style.copy();
    }

    public CellAttributes copy() {
        return new CellAttributes(foreground, background, style);
    }

    public Color getForeground() { return foreground; }
    public Color getBackground() { return background; }
    public Style getStyle() { return style; }

    public void setForeground(Color foreground) { this.foreground = foreground; }
    public void setBackground(Color background) { this.background = background; }
}
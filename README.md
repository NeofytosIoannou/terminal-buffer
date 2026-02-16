# Terminal Text Buffer

A Java implementation of a terminal text buffer for the JetBrains internship project.

## Overview

This project implements a terminal text buffer that stores and manipulates displayed text in a grid format. The buffer maintains a visible screen area and scrollback history, similar to how real terminal emulators work.

## Features

- **Character Grid**: 80×24 character cells (configurable dimensions)
- **Cursor Management**: Full cursor positioning and movement with bounds checking
- **Text Operations**: Write (overwrite) and insert modes with automatic line wrapping
- **Scrollback Buffer**: Preserves lines that scroll off the top of the screen
- **Cell Attributes**: Support for colors (16 standard terminal colors) and styles (bold, italic, underline)
- **Wide Character Support**: Handles CJK ideographs and fullwidth characters that occupy 2 cells
- **Resize**: Dynamic buffer resizing while preserving content

## Project Structure
```
src/
├── main/java/com/terminal/
│   ├── CellAttributes.java    # Color, style, and cell formatting
│   ├── Cell.java              # Individual character cell
│   ├── Line.java              # Row of cells
│   └── TerminalBuffer.java    # Main buffer implementation
└── test/java/com/terminal/
    └── TerminalBufferTest.java # Unit tests (20 tests)
```

## Build
```bash
# Build and test
./gradlew build

# Clean build
./gradlew clean build

# Run tests with details
./gradlew test --info
```

All tests should pass. Test coverage includes:
- Buffer creation and dimensions
- Text writing and insertion
- Cursor movement and bounds checking
- Scrollback functionality
- Cell attributes (colors and styles)
- Wide character handling
- Screen clearing and resizing

## Implementation Details

### Key Design Decisions

- **Scrollback Storage**: Uses `Deque` (LinkedList) for efficient add/remove operations from both ends
- **Screen Storage**: Uses `ArrayList` for indexed access to screen rows
- **Defensive Copying**: Cell attributes are copied to prevent shared mutable state between cells
- **Wide Character Handling**: Detects CJK ideographs and fullwidth forms, reserving 2 cells per character

### Core Operations

**Write vs Insert**:
- `writeText()`: Overwrites existing content at cursor position
- `insertText()`: Shifts existing content right before inserting

**Scrollback**:
- Lines that scroll off the top of the screen are preserved in scrollback
- Configurable maximum scrollback size (default: 1000 lines)
- Scrollback is accessible using negative row indices

## Requirements

- Java 11 or higher
- Gradle 7.0+ (or use included Gradle wrapper)

## Author

Neofytos Ioannou

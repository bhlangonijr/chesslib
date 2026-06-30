# Chesslib
### A Lightweight, High-Performance Java Chess Library

[![JitPack](https://jitpack.io/v/bhlangonijr/chesslib.svg)](https://jitpack.io/#bhlangonijr/chesslib)
![Build Status](https://github.com/bhlangonijr/chesslib/actions/workflows/maven.yml/badge.svg)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

**Chesslib** is a clean, efficient Java library for legal move generation, position validation, and PGN/FEN parsing. It is designed for developers who need a robust engine backbone with high-speed performance and a low memory footprint.

Whether you are building a chess UI, a server-side validator, or a full-strength engine, Chesslib provides the tools you need.

---

## Quick Start

### Installation

Add the JitPack repository and the dependency to your project build file.

#### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.bhlangonijr</groupId>
    <artifactId>chesslib</artifactId>
    <version>1.3.6</version>
</dependency>

```

#### Gradle

```groovy
repositories { maven { url 'https://jitpack.io' } }
dependencies { implementation 'com.github.bhlangonijr:chesslib:1.3.6' }

```

### Hello World: Making Moves

```java
import com.github.bhlangonijr.chesslib.*;

public class Main {
    public static void main(String[] args) {
        // Create a new board in the starting position
        Board board = new Board();

        // Make a move using SAN (Standard Algebraic Notation)
        board.doMove("e4"); 
        
        // Make a move using Square constants
        board.doMove(new Move(Square.E7, Square.E5));

        // Print the board to console
        System.out.println(board.toString());
    }
}

```

**Output:**

```text
r n b q k b n r
p p p p . p p p
. . . . . . . .
. . . . p . . .
. . . . P . . .
. . . . . . . .
P P P P . P P P
R N B Q K B N R
Side: WHITE

```

---

## API Cheat Sheet

The `Board` class is the heart of the library. Here are the most essential commands for manipulating state.

### ♟️ Move Execution & History

| Action | Method | Description |
| --- | --- | --- |
| **Do Move (SAN)** | `board.doMove("Nf3")` | Parses and executes a SAN move string. |
| **Do Move (Object)** | `board.doMove(new Move(sq1, sq2))` | Executes a move object (faster). |
| **Undo Move** | `board.undoMove()` | Reverts the last move and returns it. |
| **Get Legal Moves** | `board.legalMoves()` | Returns a `List<Move>` of all valid moves. |
| **Check Legality** | `board.isMoveLegal(move, true)` | returns `boolean` if a specific move is valid. |

### State Inspection

| Action | Method | Description |
| --- | --- | --- |
| **Current Side** | `board.getSideToMove()` | Returns `Side.WHITE` or `Side.BLACK`. |
| **Get Piece** | `board.getPiece(Square.E4)` | Returns the piece at a square (or `Piece.NONE`). |
| **Find Pieces** | `board.getPieceLocation(Piece.WHITE_KING)` | Returns `List<Square>` of that piece type. |
| **Game Over?** | `board.isMated()` | Returns `true` if the side to move is checkmated. |
| **Stalemate?** | `board.isStaleMate()` | Returns `true` if no moves are possible but not in check. |
| **In Check?** | `board.isKingAttacked()` | Returns `true` if the current King is under attack. |
| **Draw?** | `board.isDraw()` | Checks all draw rules (repetition, 50-move, insufficient material). |

### Persistence (FEN)

| Action | Method | Description |
| --- | --- | --- |
| **Get FEN** | `board.getFen()` | Exports current state to FEN string. |
| **Load FEN** | `board.loadFromFen(fenString)` | Resets board to the specific FEN position. |

---

## ⚡ Power Features

### 1. PGN Parsing (Large Databases)

Chesslib includes a streaming `PgnIterator` that allows you to parse multi-gigabyte PGN files without memory exhaustion.

```java
// Iterate through a PGN file game by game
PgnIterator games = new PgnIterator("grandmaster_games.pgn");

for (Game game : games) {
    System.out.println("Event: " + game.getEvent());
    System.out.println("White: " + game.getWhitePlayer().getName());
    System.out.println("Black: " + game.getBlackPlayer().getName());
    
    // Load the moves to replay them
    game.loadMoveText();
    MoveList moves = game.getHalfMoves();
}

```

### 2. MoveList & SAN Handling

Easily convert between standard chess notation and internal move objects.

```java
// Load a game sequence from a string
MoveList list = new MoveList();
list.loadFromSan("1. e4 e5 2. Nf3 Nc6 3. Bb5 a6");

// Export to SAN array
String[] sanMoves = list.toSanArray();

// Apply all moves to a board
Board board = new Board();
for (Move move : list) {
    board.doMove(move);
}

```

### 3. Bitboards (For Engine Devs)

Access the raw 64-bit integer (long) representations of the board for high-performance bitwise operations.

```java
// Get a bitmask of all White Pawns
long whitePawns = board.getBitboard(Piece.WHITE_PAWN);

// Get a bitmask of all occupied squares
long allPieces = board.getBitboard();

// Example: Check if A1 is occupied using bitwise AND
boolean isOccupied = (allPieces & (1L << Square.A1.ordinal())) != 0;

```

### 4. Event Listeners

Hooks are available for UI updates or logging when the board state changes.

```java
board.addEventListener(BoardEventType.ON_MOVE, event -> {
    Move move = (Move) event;
    System.out.println("Move played: " + move);
    // Trigger GUI update here...
});

```

### 5. Board Comparison & Hashing

* **`board.getZobristKey()`**: Returns the Zobrist hash (long) for rapid transposition table lookups.
* **`board.strictEquals(otherBoard)`**: Compares position **AND** move history (crucial for 3-fold repetition detection).

---

## Performance & Testing

### Perft (Performance Test)

Validate the move generator against known node counts. This is standard practice when developing engines to ensure bug-free move generation.

```java
// Calculate nodes at depth 5 (Standard Start Position)
// Should return 4,865,609
long nodes = Perft.perft(board, 5); 

```

---

## Contributing

We welcome pull requests! Please ensure all tests pass before submitting.

* **Bug Reports:** Please provide a minimal reproduction case (FEN + Move).
* **Feature Requests:** Open an issue to discuss.

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](https://www.google.com/search?q=LICENSE) file for details.

> **Showcase:** Check out [kengine](https://github.com/bhlangonijr/kengine), a reference chess engine implementation using Chesslib.

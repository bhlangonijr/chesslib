Simple Java Chess Library
=========================

Chesslib is a simple java chess library for doing things like generating
legal chess moves given a chessboard [position](https://en.wikipedia.org/wiki/Chess#Setup),
parse a chess game stored in [PGN](https://en.wikipedia.org/wiki/Portable_Game_Notation) or [FEN](https://en.wikipedia.org/wiki/Forsyth–Edwards_Notation) format and many other things.

### Building/Installing
    mvn clean compile package install

### Usage

#### Create a chessboard and make a move

```java
    // Creates a new chessboard in the standard initial position
    Board board = new Board();

    //Make a move from E2 to E4 squares
    board.doMove(new Move(Square.E2, Square.E4));

    //print the chessboard in a human-readable form
    System.out.println(board.toString());
```
Result:
```
rnbqkbnr
pppppppp


    P

PPPP PPP
RNBQKBNR
Side: BLACK
```
#### Undo a move

```java
    // Undo a move from the stack and return it
    Move move = board.undoMove();

```

#### Load a chessboard position from [FEN](https://en.wikipedia.org/wiki/Forsyth–Edwards_Notation) notation

```java
    // Load a FEN position into the chessboard
    String fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
    Board board = new Board();
    board.loadFromFEN(fen);

    //Find the square locations of black bishops
    List<Square> blackBishopSquares = board.getPieceLocation(Piece.BLACK_BISHOP);

    //Get the piece at A1 square...
    Piece piece = board.getPiece(Square.A1);
```

#### Generate all chess legal-moves for the current position

```java
    // Generate legal chess moves for the current position
    Board board = new Board();
    MoveList moves = MoveGenerator.generateLegalMoves(board);
    System.out.println("Legal moves: " + moves);
```
Result:
```
  a2a3 a2a4 b2b3 b2b4 c2c3 c2c4 d2d3 d2d4 e2e3 e2e4 f2f3 f2f4 g2g3 g2g4 h2h3 h2h4 b1a3 b1c3 g1f3 g1h3
```

#### Checking for position situations

The current chessboard state can be checked using methods like `board.isDraw()`, `board.isInsufficientMaterial()`,
`board.isStaleMate()`, `board.isKingAttacked()`, `board.isMated()`, `board.getSideToMove()`, etc.

#### Load a chess game collection from a [PGN](https://en.wikipedia.org/wiki/Portable_Game_Notation) file

```java
    PgnHolder pgn = new PgnHolder("/opt/games/linares_2002.pgn");
    pgn.loadPgn();
    for (Game game: pgn.getGame()) {
        game.loadMoveText();
        MoveList moves = game.getHalfMoves();
        Board board = new Board();
        //Replay all the moves from the game and print the final position in FEN format
        for (Move move: moves) {
            board.doMove(move);
        }
        System.out.println("FEN: " + board.getFEN());
    }
```
You could have done the same by only:
```java
    ...
    board.loadFromFEN(moves.getFEN());

```

### Advanced usage

[TODO]


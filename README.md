Simple Java Chess Library
=========================

Chesslib is a simple java chess library for generating
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

#### Get FEN string from chessboard

```java
    System.out.println(board.getFen());

```


#### Load a chessboard position from [FEN](https://en.wikipedia.org/wiki/Forsyth–Edwards_Notation) notation

```java
    // Load a FEN position into the chessboard
    String fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
    Board board = new Board();
    board.loadFromFen(fen);

    //Find the square locations of black bishops
    List<Square> blackBishopSquares = board.getPieceLocation(Piece.BLACK_BISHOP);

    //Get the piece at A1 square...
    Piece piece = board.getPiece(Square.A1);
```
#### MoveList

`MoveList` stores a list of moves played in the chessboard. When created it assumes the initial 
position of a regular chess game. Arbitrary moves from a chess game can be loaded using SAN or LAN string: 

```java
    String san = "e4 Nc6 d4 Nf6 d5 Ne5 Nf3 d6 Nxe5 dxe5 Bb5+ Bd7 Bxd7+ Qxd7 Nc3 e6 O-O exd5 ";
    MoveList list = new MoveList();
    list.loadFromSan(san);
    
    System.out.println("FEN of final position: " + list.getFen());

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

Relaying the legal moves to the chessboard:

```java
    ...
    for (Move move : moves) {
        board.doMove();
        //do something
        board.undoMove();
    }
    System.out.println("Legal moves: " + moves);
```

#### Checking chessboard situation

Chessboard situation can be checked using the methods:
 
  - `board.isDraw()`
  - `board.isInsufficientMaterial()`
  - `board.isStaleMate()`
  - `board.isKingAttacked()`
  - `board.isMated()`
  - `board.getSideToMove()`
  - ...

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
        System.out.println("FEN: " + board.getFen());
    }
```
You could achieve the same by loading the move list final FEN position:
```java
    ...
    board.loadFromFen(moves.getFen());

```

### Sanity checking of chesslib move generation with Perft

Perft, (performance test, move path enumeration) is a debugging function to walk the 
move generation tree of strictly legal moves to count all the leaf nodes of a certain depth.
Example of a perft function using chesslib: 

```java
    private long perft(Board board, int depth, int ply) throws MoveGeneratorException {

        if (depth == 0) {
            return 1;
        }
        long nodes = 0;      
        MoveList moves = MoveGenerator.generateLegalMoves(board);
        for (Move move : moves) {
            board.doMove(move);
            nodes += perft(board, depth - 1, ply + 1);
            board.undoMove();
        }
        return nodes;
    }
``` 

There are plenty of known results for Perft tests on a given set of chess positions.
It can be tested against the library to check if it's reliably generating moves and while 
keeping the `Board` in a consistent state, e.g.:

```java
    @Test
    public void testPerftInitialPosition() throws MoveGeneratorException {

        Board board = new Board();
        board.setEnableEvents(false);
        board.loadFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

        long nodes = perft(board, 5, 1);
        assertEquals(4865609, nodes);
    }
``` 

It's known that from the initial standard chess position, there should have exactly 4865609 positions
for depth 5. Deviation from this number would imply a bug in move generation or keeping the board state. 

### Advanced usage

[TODO]


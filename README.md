Simple Java Chess Library
=========================

[![](https://jitpack.io/v/bhlangonijr/chesslib.svg)](https://jitpack.io/#bhlangonijr/chesslib) ![mvn build](https://github.com/bhlangonijr/chesslib/actions/workflows/maven.yml/badge.svg)


Chesslib is a simple java chess library for generating
legal chess moves given a chessboard [position](https://en.wikipedia.org/wiki/Chess#Setup),
parse a chess game stored in [PGN](https://en.wikipedia.org/wiki/Portable_Game_Notation) or [FEN](https://en.wikipedia.org/wiki/Forsyth–Edwards_Notation) format and many other things.

# Table of Contents

1. [Building/Installing](#Building)
2. [Usage](#Usage)
3. [Create a chessboard and make a move](#Create_a_chessboard_and_make_a_move)
4. [Undo a move](#Undo_a_move)
5. [Get FEN string from chessboard](#Get_FEN_string_from_chessboard)
6. [Load a chessboard position from FEN notation](#Load_a_chessboard_position_from_notation)
7. [MoveList](#MoveList)
8. [Generate all chess legal-moves for the current position](#Generate_all_chess_legal_moves_for_the_current_position)
9. [Checking chessboard situation](#Checking_chessboard_situation)
10. [Comparing boards](#Comparing_boards)
11. [Load a chess game collection from a file](#Load_a_chess_game_collection_from_a_PGN_file)
12. [Advanced usage](#Advanced_usage)
13. [Sanity checking of chesslib move generation with Perft](#Sanity_checking_of_chesslib_move_generation_with_Perft)
14. [Creating a full-fledged chess engine](#Creating_a_full_fledged_chess_engine)
15. [Capturing and reacting to events](#Capturing_and_reacting_to_events)

# <a name="Building"></a> Building/Installing
## From source

```
$ git clone git@github.com:bhlangonijr/chesslib.git
$ cd chesslib/
$ mvn clean compile package install
```

## From repo

Chesslib dependency can be added via the jitpack repository.

## Maven

```xml
<repositories>
  ...
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

```xml
<dependency>
  <groupId>com.github.bhlangonijr</groupId>
  <artifactId>chesslib</artifactId>
  <version>1.3.3</version>
</dependency>
```

## Gradle

```groovy
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
```

```groovy
dependencies {
    ...
    compile 'com.github.bhlangonijr:chesslib:1.3.3'
    ...
}
```

# <a name="Usage"></a> Usage

## <a name="Create_a_chessboard_and_make_a_move"></a> Create a chessboard and make a move

```java
    // Creates a new chessboard in the standard initial position
    Board board = new Board();

    //Make a move from E2 to E4 squares
    board.doMove(new Move(Square.E2, Square.E4));

    //print the chessboard in a human-readable form
    System.out.println(board.toString());
```

Alternatively one could just specify the move using SAN, e.g.:
```java
    Board board = new Board();
    board.doMove("e4");
```

Result:
```text
rnbqkbnr
pppppppp


    P

PPPP PPP
RNBQKBNR
Side: BLACK
```
## <a name="Undo_a_move"></a> Undo a move

```java
    // Undo the last move from the stack and return it
    Move move = board.undoMove();
```

## <a name="Get_FEN_string_from_chessboard"></a> Get FEN string from chessboard

```java
    System.out.println(board.getFen());
```

## <a name="Load_a_chessboard_position_from_notation"></a> Load a chessboard position from [FEN](https://en.wikipedia.org/wiki/Forsyth–Edwards_Notation) notation

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
## <a name="MoveList"></a> MoveList

`MoveList` stores a list of moves played in the chessboard. When created it assumes the initial 
position of a regular chess game. Arbitrary moves from a chess game can be loaded using SAN or LAN string: 

```java
    String san = "e4 Nc6 d4 Nf6 d5 Ne5 Nf3 d6 Nxe5 dxe5 Bb5+ Bd7 Bxd7+ Qxd7 Nc3 e6 O-O exd5 ";
    MoveList list = new MoveList();
    list.loadFromSan(san);
    
    System.out.println("FEN of final position: " + list.getFen());
```

## <a name="Generate_all_chess_legal_moves_for_the_current_position"></a> Generate all chess legal-moves for the current position

```java
    // Generate legal chess moves for the current position
    Board board = new Board();
    List<Move> moves = board.legalMoves();
    System.out.println("Legal moves: " + moves);
```
Result:
```text
  a2a3 a2a4 b2b3 b2b4 c2c3 c2c4 d2d3 d2d4 e2e3 e2e4 f2f3 f2f4 g2g3 g2g4 h2h3 h2h4 b1a3 b1c3 g1f3 g1h3
```

Relaying the legal moves to the chessboard:

```java
    ...
    for (Move move : moves) {
        board.doMove(move);
        //do something
        board.undoMove();
    }
    System.out.println("Legal moves: " + moves);
```

## <a name="Checking_chessboard_situation"></a> Checking chessboard situation

Chessboard situation can be checked using the methods:
 
  - `board.isDraw()`
  - `board.isInsufficientMaterial()`
  - `board.isStaleMate()`
  - `board.isKingAttacked()`
  - `board.isMated()`
  - `board.getSideToMove()`
  - ...
  
  
## <a name="Comparing_boards"></a> Comparing boards

There are two methods for comparing boards:
 
  - `board.equals(board2)`: Compares ignoring the board history
  - `board.strictEquals(board2)`: Compares the board and its history

## <a name="Load_a_chess_game_collection_from_a_PGN_file"></a> Load a chess game collection from a [PGN](https://en.wikipedia.org/wiki/Portable_Game_Notation) file

```java
    PgnHolder pgn = new PgnHolder("/opt/games/linares_2002.pgn");
    pgn.loadPgn();
    for (Game game: pgn.getGames()) {
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
Iterating over a PGN file games using the `PgnIterator`:
```java
    PgnIterator games = new PgnIterator("/opt/games/linares_2002.pgn");
    for (Game game: games) {
        System.out.println("Game: " + game);
    }
```
Note: The iterator is highly recommended for processing large PGN files as it is not retaining in the memory
intermediate objects loaded during the process of each iteration.

Capturing the comments from each move:

```java
    PgnIterator games = new PgnIterator("src/test/resources/rav_alternative.pgn");
    for (Game game: games) {
        String[] moves = game.getHalfMoves().toSanArray();
        Map<Integer, String> comments = game.getComments();
        for (int i = 0; i < moves.length; i++) {
            String ply = ((i + 2) / 2) + (i % 2 != 0 ? ".." : " ");
            String move = moves[i];
            String comment = comments.get(i + 1) + "";
            System.out.println(ply + move + " " + comment.trim());
        }
    }
```
The output should be something like: 
```text
1 e4 Ponomariov plays 1. e4 in much the same way as any of the other top-level GMs.
1..e6 Now, along with Pe4 there is an indication Black will place pawns on light-color squares to prevent Bf1 from ever being dangerous. White will probably have to meet 2...d5 with e4-e5 to open the d3-h7 diagonal. So, White needs a Pd4 to support Pe5.
```

# <a name="Advanced_usage"></a> Advanced usage

## <a name="Sanity_checking_of_chesslib_move_generation_with_Perft"></a> Sanity checking of chesslib move generation with Perft

Perft, (performance test, move path enumeration) is a debugging function to walk the 
move generation tree of strictly legal moves to count all the leaf nodes of a certain depth.
Example of a perft function using chesslib: 

```java
    private long perft(Board board, int depth, int ply) throws MoveGeneratorException {

        if (depth == 0) {
            return 1;
        }
        long nodes = 0;      
        List<Move> moves = board.legalMoves();
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

## <a name="Creating_a_full_fledged_chess_engine"></a> Creating a full-fledged chess engine

[kengine](https://github.com/bhlangonijr/kengine) is a minimalistic chess engine built on top of kotlin and chesslib to 
showcase a more advanced use case.

## <a name="Capturing_and_reacting_to_events"></a> Capturing and reacting to events

Actions occurring in the chessboard or when loading a PGN file are emitted as events by the library so that it can
be captured by a GUI, for example:

### <a name="Listening_to_PGN_loading_progress"></a> Listening to PGN loading progress

Create your listener:

```java
class MyListener implements PgnLoadListener {
    
    private int games = 0;

     @Override
    public void notifyProgress(int games) {
        System.out.println("Loaded " + games + " games...");
    }
}
```

Add the listener to `PgnHolder` object and load the games:

```java
    PgnHolder pgn = new PgnHolder(".../games.pgn");
    // add your listener
    pgn.getListener().add(myListener);
    pgn.loadPgn();
```

Example implementing a `SwingWorker` to update a Swing `ProgressBarDialog` with PGN loading status: 

```java

private final ProgressBarDialog progress = new ProgressBarDialog("PGN Loader", frame);
...
private void init() {
    ...
    LoadPGNWorker loadPGNWorker = new LoadPGNWorker();
    loadPGNWorker.addPropertyChangeListener(new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent e) {                
            progress.getProgressBar().setIndeterminate(true);
            progress.getProgressBar().setValue((Integer) e.getNewValue());
        }
    });
    loadPGNWorker.execute();

}
...
private class LoadPGNWorker extends SwingWorker<Integer, Integer> implements PgnLoadListener {

    @Override
    protected Integer doInBackground() throws Exception {
        try {
            getPgnHolder().getListener().add(this);
            loadPGN();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(owner, errorMessageFromBundle + e.getMessage(), JOptionPane.ERROR_MESSAGE);
            log.error("Error loading pgn", e);
        } finally {
            progress.dispose();
        }
        return getPgnHolder().getSize();
    }
    
    @Override
    protected void done() {
        setProgress(100);
    }
    
    @Override
    public void notifyProgress(int games) {
        setProgress(Math.min(90, games));
        progress.getLabel().setText("Loading games...");
    }
}
```

### <a name="Listening_to_chessboard_events"></a> Listening to chessboard events

Moves played and game statuses are emitted by the `Board` whenever these actions happen.

Implement your `Board` listener:
```java
class MyBoardListener implements BoardEventListener {

    @Override
    public void onEvent(BoardEvent event) {

        if (event.getType() == BoardEventType.ON_MOVE) {
            Move move = (Move) event;
            System.out.println("Move " + move + " was played");
        }
    }
}
```

Add your listener to `Board` and listen to played moves events:
```java
    Board board = new Board();
    board.addEventListener(BoardEventType.ON_MOVE, new MyBoardListener());    

    handToGui(board);
    ...
```

* Beware that listeners are executed using the calling thread that updated the `Board` and 
depending on your listener processing requirements you'd want to hand the execution off 
to a separate thread like in a threadpool:  

```java
    public void onEvent(BoardEvent event) {

        executors.submit(myListenerRunnable);
    }
```

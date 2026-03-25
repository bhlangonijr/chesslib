/*
 * Copyright 2017 Ben-Hur Carlos Vieira Langoni Junior
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.bhlangonijr.chesslib.game;

import java.util.ArrayList;
import java.util.List;

import com.github.bhlangonijr.chesslib.CastleRight;
import com.github.bhlangonijr.chesslib.Constants;
import com.github.bhlangonijr.chesslib.File;
import com.github.bhlangonijr.chesslib.Rank;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

/**
 * The definition of a game context, a support structure used to provide contextual information to a chess position, and
 * most importantly to validate special moves in a uniform and consistent way according to the chess variation (e.g.
 * castle moves).
 */
public class GameContext {

    /**
     * The definition of the white king shift in the default short castle move.
     */
    protected Move whiteoo;
    /**
     * The definition of the white king shift in the default long castle move.
     */
    protected Move whiteooo;
    /**
     * The definition of the black king shift in the default short castle move.
     */
    protected Move blackoo;
    /**
     * The definition of the black king shift in the default long castle move.
     */
    protected Move blackooo;

    /**
     * The definition of the white rook shift in the default short castle move.
     */
    protected Move whiteRookoo;
    /**
     * The definition of the white rook shift in the default long castle move.
     */
    protected Move whiteRookooo;
    /**
     * The definition of the black rook shift in the default short castle move.
     */
    protected Move blackRookoo;
    /**
     * The definition of the black rook shift in the default long castle move.
     */
    protected Move blackRookooo;

    /**
     * The list of squares crossed by the white king in the default short castle move.
     */
    protected List<Square> whiteooSquares;
    /**
     * The list of squares crossed by the white king in the default long castle move.
     */
    protected List<Square> whiteoooSquares;
    /**
     * The list of squares crossed by the black king in the default short castle move.
     */
    protected List<Square> blackooSquares;
    /**
     * The list of squares crossed by the black king in the default long castle move.
     */
    protected List<Square> blackoooSquares;

    /**
     * The bitboard representation of the squares crossed by the white king in the default short castle move.
     */
    protected long whiteooSquaresBb;
    /**
     * The bitboard representation of the squares crossed by the white king in the default long castle move.
     */
    protected long whiteoooSquaresBb;
    /**
     * The bitboard representation of the squares crossed by the black king in the default short castle move.
     */
    protected long blackooSquaresBb;
    /**
     * The bitboard representation of the squares crossed by the black king in the default long castle move.
     */
    protected long blackoooSquaresBb;

    /**
     * The bitboard representation of all the squares involved in the default short castle move of white.
     */
    protected long whiteooAllSquaresBb;
    /**
     * The bitboard representation of all the squares involved in the default long castle move of white.
     */
    protected long whiteoooAllSquaresBb;
    /**
     * The bitboard representation of all the squares involved in the default short castle move of black.
     */
    protected long blackooAllSquaresBb;
    /**
     * The bitboard representation of all the squares involved in the default long castle move of black.
     */
    protected long blackoooAllSquaresBb;

    /**
     * The initial position of the game, as a Forsyth-Edwards Notation (FEN) string.
     */
    protected String startFEN;

    /**
     * The file of the white king-side rook (for Shredder-FEN output in Chess960).
     */
    protected File whiteRookooFile;
    /**
     * The file of the white queen-side rook (for Shredder-FEN output in Chess960).
     */
    protected File whiteRookoooFile;
    /**
     * The file of the black king-side rook (for Shredder-FEN output in Chess960).
     */
    protected File blackRookooFile;
    /**
     * The file of the black queen-side rook (for Shredder-FEN output in Chess960).
     */
    protected File blackRookoooFile;

    /**
     * The game mode.
     */
    protected GameMode gameMode;
    /**
     * The type of the chess variation.
     */
    protected VariationType variationType;
    /**
     * The chess event.
     */
    protected Event event;

    /**
     * Constructs a new game context using the default game mode and chess variation.
     * <p>
     * Same as invoking {@code new GameContext(GameMode.HUMAN_VS_HUMAN, VariationType.NORMAL)}.
     */
    public GameContext() {
        this(GameMode.HUMAN_VS_HUMAN, VariationType.NORMAL);
    }

    /**
     * Constructs a new game context using the provided game mode and chess variation.
     *
     * @param gameMode      the game mode
     * @param variationType the chess variation
     */
    public GameContext(GameMode gameMode, VariationType variationType) {
        setGameMode(gameMode);
        setVariationType(variationType);
        if (variationType.equals(VariationType.NORMAL)) {
            loadDefaults();
        }
    }

    private static long squareListToBb(List<Square> list) {
        long r = 0L;
        for (Square s : list) {
            r |= s.getBitboard();
        }
        return r;
    }

    private void loadDefaults() {
        //load standard values
        setWhiteoo(Constants.DEFAULT_WHITE_OO);
        setWhiteooo(Constants.DEFAULT_WHITE_OOO);
        setBlackoo(Constants.DEFAULT_BLACK_OO);
        setBlackooo(Constants.DEFAULT_BLACK_OOO);

        setWhiteRookoo(Constants.DEFAULT_WHITE_ROOK_OO);
        setWhiteRookooo(Constants.DEFAULT_WHITE_ROOK_OOO);
        setBlackRookoo(Constants.DEFAULT_BLACK_ROOK_OO);
        setBlackRookooo(Constants.DEFAULT_BLACK_ROOK_OOO);

        setWhiteooSquares(Constants.DEFAULT_WHITE_OO_SQUARES);
        setWhiteoooSquares(Constants.DEFAULT_WHITE_OOO_SQUARES);
        setBlackooSquares(Constants.DEFAULT_BLACK_OO_SQUARES);
        setBlackoooSquares(Constants.DEFAULT_BLACK_OOO_SQUARES);

        setWhiteooSquaresBb(squareListToBb(Constants.DEFAULT_WHITE_OO_SQUARES));
        setWhiteoooSquaresBb(squareListToBb(Constants.DEFAULT_WHITE_OOO_SQUARES));
        setBlackooSquaresBb(squareListToBb(Constants.DEFAULT_BLACK_OO_SQUARES));
        setBlackoooSquaresBb(squareListToBb(Constants.DEFAULT_BLACK_OOO_SQUARES));

        setWhiteooAllSquaresBb(squareListToBb(Constants.DEFAULT_WHITE_OO_ALL_SQUARES));
        setWhiteoooAllSquaresBb(squareListToBb(Constants.DEFAULT_WHITE_OOO_ALL_SQUARES));
        setBlackooAllSquaresBb(squareListToBb(Constants.DEFAULT_BLACK_OO_ALL_SQUARES));
        setBlackoooAllSquaresBb(squareListToBb(Constants.DEFAULT_BLACK_OOO_ALL_SQUARES));

        setStartFEN(Constants.startStandardFENPosition);
    }

    /**
     * Configures this game context for Chess960 based on the actual king and rook positions.
     *
     * @param whiteKing     the starting square of the white king
     * @param whiteRookOO   the starting square of the white king-side rook (may be null if no king-side castling)
     * @param whiteRookOOO  the starting square of the white queen-side rook (may be null if no queen-side castling)
     * @param blackKing     the starting square of the black king
     * @param blackRookOO   the starting square of the black king-side rook (may be null if no king-side castling)
     * @param blackRookOOO  the starting square of the black queen-side rook (may be null if no queen-side castling)
     */
    public void loadChess960(Square whiteKing, Square whiteRookOO, Square whiteRookOOO,
                             Square blackKing, Square blackRookOO, Square blackRookOOO) {
        setVariationType(VariationType.CHESS960);

        // King castle moves: king_from -> king_final_square
        setWhiteoo(new Move(whiteKing, Square.G1));
        setWhiteooo(new Move(whiteKing, Square.C1));
        setBlackoo(new Move(blackKing, Square.G8));
        setBlackooo(new Move(blackKing, Square.C8));

        // Rook castle moves: rook_from -> rook_final_square
        if (whiteRookOO != null) {
            setWhiteRookoo(new Move(whiteRookOO, Square.F1));
            whiteRookooFile = whiteRookOO.getFile();
        }
        if (whiteRookOOO != null) {
            setWhiteRookooo(new Move(whiteRookOOO, Square.D1));
            whiteRookoooFile = whiteRookOOO.getFile();
        }
        if (blackRookOO != null) {
            setBlackRookoo(new Move(blackRookOO, Square.F8));
            blackRookooFile = blackRookOO.getFile();
        }
        if (blackRookOOO != null) {
            setBlackRookooo(new Move(blackRookOOO, Square.D8));
            blackRookoooFile = blackRookOOO.getFile();
        }

        // Squares the king passes through (for attack checks) - between king start and king dest, inclusive of dest
        setWhiteooSquares(computeKingPassSquares(whiteKing, Square.G1));
        setWhiteoooSquares(computeKingPassSquares(whiteKing, Square.C1));
        setBlackooSquares(computeKingPassSquares(blackKing, Square.G8));
        setBlackoooSquares(computeKingPassSquares(blackKing, Square.C8));

        setWhiteooSquaresBb(squareListToBb(getWhiteooSquares()));
        setWhiteoooSquaresBb(squareListToBb(getWhiteoooSquares()));
        setBlackooSquaresBb(squareListToBb(getBlackooSquares()));
        setBlackoooSquaresBb(squareListToBb(getBlackoooSquares()));

        // All squares that must be empty for castling (between king and king dest + between rook and rook dest)
        // Exclude the king and rook starting squares themselves
        setWhiteooAllSquaresBb(computeAllEmptySquaresBb(whiteKing, Square.G1, whiteRookOO, Square.F1));
        setWhiteoooAllSquaresBb(computeAllEmptySquaresBb(whiteKing, Square.C1, whiteRookOOO, Square.D1));
        setBlackooAllSquaresBb(computeAllEmptySquaresBb(blackKing, Square.G8, blackRookOO, Square.F8));
        setBlackoooAllSquaresBb(computeAllEmptySquaresBb(blackKing, Square.C8, blackRookOOO, Square.D8));
    }

    /**
     * Computes the squares the king passes through during castling (inclusive of destination, exclusive of start).
     * These squares must not be attacked.
     */
    private static List<Square> computeKingPassSquares(Square kingFrom, Square kingTo) {
        List<Square> squares = new ArrayList<>();
        if (kingFrom == null || kingTo == null) return squares;
        int fromFile = kingFrom.getFile().ordinal();
        int toFile = kingTo.getFile().ordinal();
        Rank rank = kingFrom.getRank();
        if (fromFile == toFile) {
            // King doesn't move (e.g., king on G1 doing O-O) - just include the destination
            squares.add(kingTo);
            return squares;
        }
        int step = fromFile < toFile ? 1 : -1;
        // Include all squares from kingFrom+step to kingTo (inclusive)
        for (int f = fromFile + step; ; f += step) {
            squares.add(Square.encode(rank, File.allFiles[f]));
            if (f == toFile) break;
        }
        return squares;
    }

    /**
     * Computes the bitboard of all squares that must be empty for a Chess960 castle move.
     * This includes squares between king start and king dest, and between rook start and rook dest,
     * excluding the king and rook starting squares themselves.
     */
    private static long computeAllEmptySquaresBb(Square kingFrom, Square kingTo,
                                                  Square rookFrom, Square rookTo) {
        if (kingFrom == null || kingTo == null || rookFrom == null || rookTo == null) return 0L;
        long bb = 0L;
        Rank rank = kingFrom.getRank();

        // Squares between king start and king dest (inclusive of both endpoints)
        int kf1 = Math.min(kingFrom.getFile().ordinal(), kingTo.getFile().ordinal());
        int kf2 = Math.max(kingFrom.getFile().ordinal(), kingTo.getFile().ordinal());
        for (int f = kf1; f <= kf2; f++) {
            bb |= Square.encode(rank, File.allFiles[f]).getBitboard();
        }

        // Squares between rook start and rook dest (inclusive of both endpoints)
        int rf1 = Math.min(rookFrom.getFile().ordinal(), rookTo.getFile().ordinal());
        int rf2 = Math.max(rookFrom.getFile().ordinal(), rookTo.getFile().ordinal());
        for (int f = rf1; f <= rf2; f++) {
            bb |= Square.encode(rank, File.allFiles[f]).getBitboard();
        }

        // Exclude king and rook starting squares (they are occupied by our own pieces that will move)
        bb &= ~kingFrom.getBitboard();
        bb &= ~rookFrom.getBitboard();

        return bb;
    }

    /**
     * Returns the file of the white king-side rook (for Shredder-FEN output).
     * @return the file, or null if not set
     */
    public File getWhiteRookooFile() { return whiteRookooFile; }

    /**
     * Returns the file of the white queen-side rook (for Shredder-FEN output).
     * @return the file, or null if not set
     */
    public File getWhiteRookoooFile() { return whiteRookoooFile; }

    /**
     * Returns the file of the black king-side rook (for Shredder-FEN output).
     * @return the file, or null if not set
     */
    public File getBlackRookooFile() { return blackRookooFile; }

    /**
     * Returns the file of the black queen-side rook (for Shredder-FEN output).
     * @return the file, or null if not set
     */
    public File getBlackRookoooFile() { return blackRookoooFile; }

    /**
     * Returns an unambiguous castle move by king given the provided side and castle rights, if possible. A castle move
     * is not ambiguous if only one side of the board has castle rights, either king-side or queen-side (not both).
     *
     * @param side        the side to move
     * @param castleRight the castle rights available for the moving side
     * @return the move representing the castle move of the king, if available and not ambiguous, or null otherwise
     */
    public Move getKingCastleMove(Side side, CastleRight castleRight) {
        Move move = null;
        if (Side.WHITE.equals(side)) {
            if (CastleRight.KING_SIDE.equals(castleRight)) {
                move = getWhiteoo();
            } else if (CastleRight.QUEEN_SIDE.equals(castleRight)) {
                move = getWhiteooo();
            }
        } else {
            if (CastleRight.KING_SIDE.equals(castleRight)) {
                move = getBlackoo();
            } else if (CastleRight.QUEEN_SIDE.equals(castleRight)) {
                move = getBlackooo();
            }
        }
        return move;
    }

    /**
     * Returns an unambiguous castle move by rook given the provided side and castle rights, if possible. A castle move
     * is not ambiguous if only one side of the board has castle rights, either king-side or queen-side (not both).
     *
     * @param side        the side to move
     * @param castleRight the castle rights available for the moving side
     * @return the move representing the castle move of the rook, if available and not ambiguous, or null otherwise
     */
    public Move getRookCastleMove(Side side, CastleRight castleRight) {
        Move move = null;
        if (Side.WHITE.equals(side)) {
            if (CastleRight.KING_SIDE.equals(castleRight)) {
                move = getWhiteRookoo();
            } else if (CastleRight.QUEEN_SIDE.equals(castleRight)) {
                move = getWhiteRookooo();
            }
        } else {
            if (CastleRight.KING_SIDE.equals(castleRight)) {
                move = getBlackRookoo();
            } else if (CastleRight.QUEEN_SIDE.equals(castleRight)) {
                move = getBlackRookooo();
            }
        }
        return move;
    }

    /**
     * Checks if the move is a castle move or not.
     *
     * @param move the move to check
     * @return {@code true} if the move is a castle one
     */
    public boolean isCastleMove(final Move move) {
        return move.equals(getWhiteoo()) ||
                move.equals(getWhiteooo()) ||
                move.equals(getBlackoo()) ||
                move.equals(getBlackooo());
    }

    /**
     * Checks if the castle move is valid according to the castle rights.
     *
     * @param move        the move to check
     * @param castleRight the castle rights to compare the move against
     * @return {@code true} if the castle move is valid
     */
    public boolean hasCastleRight(final Move move, final CastleRight castleRight) {

        final CastleRight r = castleRight;

        return (CastleRight.KING_AND_QUEEN_SIDE.equals(r)) ||
                (move.equals(getWhiteoo()) && CastleRight.KING_SIDE.equals(r)) ||
                (move.equals(getBlackoo()) && CastleRight.KING_SIDE.equals(r)) ||
                (move.equals(getWhiteooo()) && CastleRight.QUEEN_SIDE.equals(r)) ||
                (move.equals(getBlackooo()) && CastleRight.QUEEN_SIDE.equals(r));
    }

    /**
     * Checks if the move is a king-side (short) castle move.
     *
     * @param move the move to check
     * @return {@code true} if the move is a king-side castle one
     */
    public boolean isKingSideCastle(Move move) {
        return move.equals(getWhiteoo()) ||
                move.equals(getBlackoo());
    }

    /**
     * Checks if the move is a queen-side (long) castle move.
     *
     * @param move the move to check
     * @return {@code true} if the move is a queen-side castle one
     */
    public boolean isQueenSideCastle(Move move) {
        return move.equals(getWhiteooo()) ||
                move.equals(getBlackooo());
    }

    /**
     * Returns white king move in case of short castle.
     *
     * @return the short castle white king move
     */
    public Move getWhiteoo() {
        return whiteoo;
    }

    /**
     * Sets the white king move in case of short castle.
     *
     * @param whiteoo the short castle white king move to set
     */
    public void setWhiteoo(Move whiteoo) {
        this.whiteoo = whiteoo;
    }

    /**
     * Returns white king move in case of long castle.
     *
     * @return the long castle white king move
     */
    public Move getWhiteooo() {
        return whiteooo;
    }

    /**
     * Sets the white king move in case of long castle.
     *
     * @param whiteooo the long castle white king move to set
     */
    public void setWhiteooo(Move whiteooo) {
        this.whiteooo = whiteooo;
    }

    /**
     * Returns black king move in case of short castle.
     *
     * @return the short castle black king move
     */
    public Move getBlackoo() {
        return blackoo;
    }

    /**
     * Sets the black king move in case of short castle.
     *
     * @param blackoo the short castle black king move to set
     */
    public void setBlackoo(Move blackoo) {
        this.blackoo = blackoo;
    }

    /**
     * Returns black king move in case of long castle.
     *
     * @return the long castle black king move
     */
    public Move getBlackooo() {
        return blackooo;
    }

    /**
     * Sets the black king move in case of long castle.
     *
     * @param blackooo the long castle black king move to set
     */
    public void setBlackooo(Move blackooo) {
        this.blackooo = blackooo;
    }

    /**
     * Returns white rook move in case of short castle.
     *
     * @return the short castle white rook move
     */
    public Move getWhiteRookoo() {
        return whiteRookoo;
    }

    /**
     * Sets the white rook move in case of short castle.
     *
     * @param whiteRookoo the short castle white rook move to set
     */
    public void setWhiteRookoo(Move whiteRookoo) {
        this.whiteRookoo = whiteRookoo;
    }

    /**
     * Returns white rook move in case of long castle.
     *
     * @return the long castle white rook move
     */
    public Move getWhiteRookooo() {
        return whiteRookooo;
    }

    /**
     * Sets the white rook move in case of long castle.
     *
     * @param whiteRookooo the long castle white rook move to set
     */
    public void setWhiteRookooo(Move whiteRookooo) {
        this.whiteRookooo = whiteRookooo;
    }

    /**
     * Returns black rook move in case of short castle.
     *
     * @return the short castle black rook move
     */
    public Move getBlackRookoo() {
        return blackRookoo;
    }

    /**
     * Sets the black rook move in case of short castle.
     *
     * @param blackRookoo the short castle black rook move to set
     */
    public void setBlackRookoo(Move blackRookoo) {
        this.blackRookoo = blackRookoo;
    }

    /**
     * Returns black rook move in case of long castle.
     *
     * @return the long castle black rook move
     */
    public Move getBlackRookooo() {
        return blackRookooo;
    }

    /**
     * Sets the black rook move in case of long castle.
     *
     * @param blackRookooo the long castle black rook move to set
     */
    public void setBlackRookooo(Move blackRookooo) {
        this.blackRookooo = blackRookooo;
    }

    /**
     * Returns the initial position of the game as a Forsyth-Edwards Notation (FEN) string.
     *
     * @return the initial position in FEN notation
     */
    public String getStartFEN() {
        return startFEN;
    }

    /**
     * Sets the initial position of the game, provided as a Forsyth-Edwards Notation (FEN).
     *
     * @param startFEN the initial position to set
     */
    public void setStartFEN(String startFEN) {
        this.startFEN = startFEN;
    }

    /**
     * Returns the game mode.
     *
     * @return the game mode
     */
    public GameMode getGameMode() {
        return gameMode;
    }

    /**
     * Sets the game mode.
     *
     * @param gameMode the game mode to set
     */
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * Returns the type of the chess variation.
     *
     * @return the type of the chess variation
     */
    public VariationType getVariationType() {
        return variationType;
    }

    /**
     * Sets the type of the chess variation.
     *
     * @param variationType the chess variation type to set
     */
    public void setVariationType(VariationType variationType) {
        this.variationType = variationType;
    }

    /**
     * Returns the list of squares crossed by the white king in the short castle move.
     *
     * @return the squares crossed by the white king in the short castle move
     */
    public List<Square> getWhiteooSquares() {
        return whiteooSquares;
    }

    /**
     * Sets the list of squares crossed by the white king in the short castle move.
     *
     * @param whiteooSquares the list of squares to set
     */
    public void setWhiteooSquares(List<Square> whiteooSquares) {
        this.whiteooSquares = whiteooSquares;
    }

    /**
     * Returns the list of squares crossed by the white king in the long castle move.
     *
     * @return the squares crossed by the white king in the long castle move
     */
    public List<Square> getWhiteoooSquares() {
        return whiteoooSquares;
    }

    /**
     * Sets the list of squares crossed by the white king in the long castle move.
     *
     * @param whiteoooSquares the list of squares to set
     */
    public void setWhiteoooSquares(List<Square> whiteoooSquares) {
        this.whiteoooSquares = whiteoooSquares;
    }

    /**
     * Returns the list of squares crossed by the black king in the short castle move.
     *
     * @return the squares crossed by the black king in the short castle move
     */
    public List<Square> getBlackooSquares() {
        return blackooSquares;
    }

    /**
     * Sets the list of squares crossed by the black king in the short castle move.
     *
     * @param blackooSquares the list of squares to set
     */
    public void setBlackooSquares(List<Square> blackooSquares) {
        this.blackooSquares = blackooSquares;
    }

    /**
     * Returns the list of squares crossed by the black king in the long castle move.
     *
     * @return the squares crossed by the black king in the long castle move
     */
    public List<Square> getBlackoooSquares() {
        return blackoooSquares;
    }

    /**
     * Sets the list of squares crossed by the black king in the long castle move.
     *
     * @param blackoooSquares the list of squares to set
     */
    public void setBlackoooSquares(List<Square> blackoooSquares) {
        this.blackoooSquares = blackoooSquares;
    }

    /**
     * Returns the bitboard representation of the squares crossed by the white king in the short castle move.
     *
     * @return the bitboard representing the squares crossed by the white king in the short castle move
     */
    public long getWhiteooSquaresBb() {
        return whiteooSquaresBb;
    }

    /**
     * Sets the bitboard representation of the squares crossed by the white king in the short castle move.
     *
     * @param whiteooSquaresBb the bitboard to set
     */
    public void setWhiteooSquaresBb(long whiteooSquaresBb) {
        this.whiteooSquaresBb = whiteooSquaresBb;
    }

    /**
     * Returns the bitboard representation of the squares crossed by the white king in the long castle move.
     *
     * @return the bitboard representing the squares crossed by the white king in the long castle move
     */
    public long getWhiteoooSquaresBb() {
        return whiteoooSquaresBb;
    }

    /**
     * Sets the bitboard representation of the squares crossed by the white king in the long castle move.
     *
     * @param whiteoooSquaresBb the bitboard to set
     */
    public void setWhiteoooSquaresBb(long whiteoooSquaresBb) {
        this.whiteoooSquaresBb = whiteoooSquaresBb;
    }

    /**
     * Returns the bitboard representation of the squares crossed by the black king in the short castle move.
     *
     * @return the bitboard representing the squares crossed by the black king in the short castle move
     */
    public long getBlackooSquaresBb() {
        return blackooSquaresBb;
    }

    /**
     * Sets the bitboard representation of the squares crossed by the black king in the short castle move.
     *
     * @param blackooSquaresBb the bitboard to set
     */
    public void setBlackooSquaresBb(long blackooSquaresBb) {
        this.blackooSquaresBb = blackooSquaresBb;
    }

    /**
     * Returns the bitboard representation of the squares crossed by the black king in the long castle move.
     *
     * @return the bitboard representing the squares crossed by the black king in the long castle move
     */
    public long getBlackoooSquaresBb() {
        return blackoooSquaresBb;
    }

    /**
     * Sets the bitboard representation of the squares crossed by the black king in the long castle move.
     *
     * @param blackoooSquaresBb the bitboard to set
     */
    public void setBlackoooSquaresBb(long blackoooSquaresBb) {
        this.blackoooSquaresBb = blackoooSquaresBb;
    }

    /**
     * Returns the bitboard representation of all the squares involved in the short castle move of white.
     *
     * @return the bitboard representing the squares involved in the short castle move of white
     */
    public long getWhiteooAllSquaresBb() {
        return whiteooAllSquaresBb;
    }

    /**
     * Sets the bitboard representation of all the squares involved in the short castle move of white.
     *
     * @param whiteooAllSquaresBb the bitboard to set
     * @return this game context instance
     */
    public GameContext setWhiteooAllSquaresBb(long whiteooAllSquaresBb) {
        this.whiteooAllSquaresBb = whiteooAllSquaresBb;
        return this;
    }

    /**
     * Returns the bitboard representation of all the squares involved in the long castle move of white.
     *
     * @return the bitboard representing the squares involved in the long castle move of white
     */
    public long getWhiteoooAllSquaresBb() {
        return whiteoooAllSquaresBb;
    }

    /**
     * Sets the bitboard representation of all the squares involved in the long castle move of white.
     *
     * @param whiteoooAllSquaresBb the bitboard to set
     * @return this game context instance
     */
    public GameContext setWhiteoooAllSquaresBb(long whiteoooAllSquaresBb) {
        this.whiteoooAllSquaresBb = whiteoooAllSquaresBb;
        return this;
    }

    /**
     * Returns the bitboard representation of all the squares involved in the short castle move of black.
     *
     * @return the bitboard representing the squares involved in the short castle move of black
     */
    public long getBlackooAllSquaresBb() {
        return blackooAllSquaresBb;
    }

    /**
     * Sets the bitboard representation of all the squares involved in the short castle move of black.
     *
     * @param blackooAllSquaresBb the bitboard to set
     * @return this game context instance
     */
    public GameContext setBlackooAllSquaresBb(long blackooAllSquaresBb) {
        this.blackooAllSquaresBb = blackooAllSquaresBb;
        return this;
    }

    /**
     * Returns the bitboard representation of all the squares involved in the long castle move of black.
     *
     * @return the bitboard representing the squares involved in the long castle move of black
     */
    public long getBlackoooAllSquaresBb() {
        return blackoooAllSquaresBb;
    }

    /**
     * Sets the bitboard representation of all the squares involved in the long castle move of black.
     *
     * @param blackoooAllSquaresBb the bitboard to set
     * @return this game context instance
     */
    public GameContext setBlackoooAllSquaresBb(long blackoooAllSquaresBb) {
        this.blackoooAllSquaresBb = blackoooAllSquaresBb;
        return this;
    }

    /**
     * Returns the short castle king move based on the side to move.
     *
     * @param side the side to move
     * @return the short castle king move
     */
    public Move getoo(Side side) {
        return Side.WHITE.equals(side) ? getWhiteoo() : getBlackoo();
    }

    /**
     * Returns the long castle king move based on the side to move.
     *
     * @param side the side to move
     * @return the long castle king move
     */
    public Move getooo(Side side) {
        return Side.WHITE.equals(side) ? getWhiteooo() : getBlackooo();
    }

    /**
     * Returns the short castle rook move based on the side to move.
     *
     * @param side the side to move
     * @return the short castle rook move
     */
    public Move getRookoo(Side side) {
        return Side.WHITE.equals(side) ? getWhiteRookoo() : getBlackRookoo();
    }

    /**
     * Returns the long castle rook move based on the side to move.
     *
     * @param side the side to move
     * @return the long castle rook move
     */
    public Move getRookooo(Side side) {
        return Side.WHITE.equals(side) ? getWhiteRookooo() : getBlackRookooo();
    }

    /**
     * Returns the list of squares crossed by the king in the short castle move based on the side to move.
     *
     * @param side the side to move
     * @return the squares crossed by the king in the short castle move
     */
    public List<Square> getooSquares(Side side) {
        return Side.WHITE.equals(side) ?
                getWhiteooSquares() : getBlackooSquares();
    }

    /**
     * Returns the list of squares crossed by the king in the long castle move based on the side to move.
     *
     * @param side the side to move
     * @return the squares crossed by the king in the long castle move
     */
    public List<Square> getoooSquares(Side side) {
        return Side.WHITE.equals(side) ?
                getWhiteoooSquares() : getBlackoooSquares();
    }

    /**
     * Returns the bitboard representation of the squares crossed by the king in the short castle move based on the side
     * to move.
     *
     * @param side the side to move
     * @return the bitboard representing the squares crossed by the king in the short castle move
     */
    public long getooSquaresBb(Side side) {
        return Side.WHITE.equals(side) ?
                getWhiteooSquaresBb() : getBlackooSquaresBb();
    }

    /**
     * Returns the bitboard representation of the squares crossed by the king in the long castle move based on the side
     * to move.
     *
     * @param side the side to move
     * @return the bitboard representing the squares crossed by the king in the long castle move
     */
    public long getoooSquaresBb(Side side) {
        return Side.WHITE.equals(side) ?
                getWhiteoooSquaresBb() : getBlackoooSquaresBb();
    }

    /**
     * Returns the bitboard representation of all the squares involved in the short castle move based on the side to
     * move.
     *
     * @param side the side to move
     * @return the bitboard representing the squares involved in the short castle move
     */
    public long getooAllSquaresBb(Side side) {
        return Side.WHITE.equals(side) ?
                getWhiteooAllSquaresBb() : getBlackooAllSquaresBb();
    }

    /**
     * Returns the bitboard representation of all the squares involved in the long castle move based on the side to
     * move.
     *
     * @param side the side to move
     * @return the bitboard representing the squares involved in the long castle move
     */
    public long getoooAllSquaresBb(Side side) {
        return Side.WHITE.equals(side) ?
                getWhiteoooAllSquaresBb() : getBlackoooAllSquaresBb();
    }

    /**
     * Returns the chess event.
     *
     * @return the chess event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Sets the chess event.
     *
     * @param event the chess event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }
}

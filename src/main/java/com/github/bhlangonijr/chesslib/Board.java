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

package com.github.bhlangonijr.chesslib;

import static com.github.bhlangonijr.chesslib.Bitboard.extractLsb;
import static com.github.bhlangonijr.chesslib.Constants.emptyMove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import com.github.bhlangonijr.chesslib.game.GameContext;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.util.XorShiftRandom;

/**
 * The definition of a chessboard position and its status. It exposes methods to manipulate the board, evolve the
 * position moving pieces around, revert already performed moves, and retrieve the status of the current configuration
 * on the board. Furthermore, it offers a handy way for loading a position from a Forsyth-Edwards Notation (FEN) string
 * and exporting it in the same format.
 * <p/>
 * Each position in uniquely identified by hashes that could be retrieved using {@link Board#getIncrementalHashKey()}
 * and {@link Board#getZobristKey()} methods. Also, the implementation supports comparison against other board instances
 * using either the strict ({@link Board#strictEquals(Object)}) or the non-strict ({@link Board#equals(Object)}) mode.
 * <p/>
 * The board can be observed registering {@link BoardEventListener}s for particular types of events. Moreover, the
 * {@link Board} class itself is a {@link BoardEvent}, and hence it can be passed to the observers of the
 * {@link BoardEventType#ON_LOAD} events, emitted when a new chess position is loaded from an external source (e.g. a
 * FEN string).
 */
public class Board implements Cloneable, BoardEvent {

    private static final List<Long> keys = new ArrayList<>();
    private static final long RANDOM_SEED = 49109794719L;
    private static final int ZOBRIST_TABLE_SIZE = 2000;

    static {
        final XorShiftRandom random = new XorShiftRandom(RANDOM_SEED);
        for (int i = 0; i < ZOBRIST_TABLE_SIZE; i++) {
            long key = random.nextLong();
            keys.add(key);
        }
    }

    private final LinkedList<MoveBackup> backup;
    private final EnumMap<BoardEventType, List<BoardEventListener>> eventListener;
    private final long[] bitboard;
    private final long[] bbSide;
    private final Piece[] occupation;
    private final EnumMap<Side, CastleRight> castleRight;
    private final LinkedList<Long> history = new LinkedList<>();
    private Side sideToMove;
    private Square enPassantTarget;
    private Square enPassant;
    private Integer moveCounter;
    private Integer halfMoveCounter;
    private GameContext context;
    private boolean enableEvents;
    private final boolean updateHistory;
    private long incrementalHashKey;

    /**
     * Constructs a new board using a default game context. The board will keep its history updated, that is, will store
     * a hash value for each position encountered.
     *
     * @see Board#Board(GameContext, boolean)
     */
    public Board() {
        this(new GameContext(), true);
    }

    /**
     * Constructs a new board, using the game context provided in input. When history updates are enabled, the board
     * will keep the hashes of all positions encountered.
     *
     * @param gameContext   the game context to use for this board
     * @param updateHistory whether to keep the history updated or not
     */
    public Board(GameContext gameContext, boolean updateHistory) {

        bitboard = new long[Piece.allPieces.length];
        bbSide = new long[Side.allSides.length];
        occupation = new Piece[Square.values().length];
        castleRight = new EnumMap<>(Side.class);
        backup = new LinkedList<>();
        context = gameContext;
        eventListener = new EnumMap<>(BoardEventType.class);
        this.updateHistory = updateHistory;
        setSideToMove(Side.WHITE);
        setEnPassantTarget(Square.NONE);
        setEnPassant(Square.NONE);
        setMoveCounter(1);
        setHalfMoveCounter(0);
        for (BoardEventType evt : BoardEventType.values()) {
            eventListener.put(evt, new CopyOnWriteArrayList<>());
        }
        loadFromFen(gameContext.getStartFEN());
        setEnableEvents(true);
    }

    /*
     * does move lead to a promotion?
     */
    private static boolean isPromoRank(Side side, Move move) {
        if (side.equals(Side.WHITE) &&
                move.getTo().getRank().equals(Rank.RANK_8)) {
            return true;
        } else return side.equals(Side.BLACK) &&
                move.getTo().getRank().equals(Rank.RANK_1);

    }

    private static Square findEnPassantTarget(Square sq, Side side) {
        Square ep = Square.NONE;
        if (!Square.NONE.equals(sq)) {
            ep = Side.WHITE.equals(side) ?
                    Square.encode(Rank.RANK_5, sq.getFile()) :
                    Square.encode(Rank.RANK_4, sq.getFile());
        }
        return ep;
    }

    private static Square findEnPassant(Square sq, Side side) {
        Square ep = Square.NONE;
        if (!Square.NONE.equals(sq)) {
            ep = Side.WHITE.equals(side) ?
                    Square.encode(Rank.RANK_3, sq.getFile()) :
                    Square.encode(Rank.RANK_6, sq.getFile());
        }
        return ep;
    }

    private static IntStream zeroToSeven() {
        return IntStream.iterate(0, i -> i + 1).limit(8);
    }

    private static IntStream sevenToZero() {
        return IntStream.iterate(7, i -> i - 1).limit(8);
    }

    /**
     * Executes a move on the board, specified in Short Algebraic Notation (SAN). It returns {@code true} if the
     * operation has been successful and the position changed after the move. It performs a full validation of the board
     * status to assess the outcome of the operation.
     * <p/>
     * <b>N.B.</b>: the method does not check whether the move is legal or not according to the standard chess rules,
     * but rather if the resulting configuration is valid. For instance, it is totally fine to move the king by two or
     * more squares, or a rook beyond its friendly pieces, as long as the position obtained after the move does not
     * violate any chess constraint.
     *
     * @param move the move to execute in SAN notation, such as {@code Nc3}
     * @return {@code true} if the move was successful and the resulting position is valid
     */
    public boolean doMove(final String move) {

        MoveList moves = new MoveList(this.getFen());
        moves.addSanMove(move, true, true);
        return doMove(moves.removeLast(), true);
    }

    /**
     * Executes a move on the board without performing a full validation of the position. It returns {@code true} if the
     * operation has been successful and the position changed after the move.
     * <p/>
     * Same as invoking {@code doMove(move, false)}.
     *
     * @param move the move to execute
     * @return {@code true} if the move was successful and the resulting position is valid
     * @see #doMove(Move, boolean)
     */
    public boolean doMove(final Move move) {
        return doMove(move, false);
    }

    /**
     * Executes a move on the board. It returns {@code true} if the operation has been successful and the position
     * changed after the move. When a full validation is requested, additional checks are performed to assess the
     * outcome of the operation, such as if the side to move is the expected one, if castling or promotion moves are
     * allowed, if the move replaces another piece of the same side, etc.
     * <p/>
     * <b>N.B.</b>: the method does not check whether the move is legal or not according to the standard chess rules,
     * but rather if the resulting configuration is valid. For instance, it is totally fine to move the king by two or
     * more squares, or a rook beyond its friendly pieces, as long as the position obtained after the move does not
     * violate any chess constraint.
     *
     * @param move           the move to execute
     * @param fullValidation whether to perform a full validation of the position or not
     * @return {@code true} if the move was successful and the resulting position is valid
     */
    public boolean doMove(final Move move, boolean fullValidation) {

        if (!isMoveLegal(move, fullValidation)) {
            return false;
        }

        Piece movingPiece = getPiece(move.getFrom());
        Side side = getSideToMove();

        MoveBackup backupMove = new MoveBackup(this, move);
        final boolean isCastle = context.isCastleMove(move);

        incrementalHashKey ^= getSideKey(getSideToMove());
        if (getEnPassantTarget() != Square.NONE) {
            incrementalHashKey ^= getEnPassantKey(getEnPassantTarget());
        }

        if (PieceType.KING.equals(movingPiece.getPieceType())) {
            if (isCastle) {
                if (context.hasCastleRight(move, getCastleRight(side))) {
                    CastleRight c = context.isKingSideCastle(move) ? CastleRight.KING_SIDE :
                            CastleRight.QUEEN_SIDE;
                    Move rookMove = context.getRookCastleMove(side, c);
                    movePiece(rookMove, backupMove);
                } else {
                    return false;
                }
            }
            if (getCastleRight(side) != CastleRight.NONE) {
                incrementalHashKey ^= getCastleRightKey(side);
                getCastleRight().put(side, CastleRight.NONE);
            }
        } else if (PieceType.ROOK == movingPiece.getPieceType()
                && CastleRight.NONE != getCastleRight(side)) {
            final Move oo = context.getRookoo(side);
            final Move ooo = context.getRookooo(side);

            if (move.getFrom() == oo.getFrom()) {
                if (CastleRight.KING_AND_QUEEN_SIDE == getCastleRight(side)) {
                    incrementalHashKey ^= getCastleRightKey(side);
                    getCastleRight().put(side, CastleRight.QUEEN_SIDE);
                    incrementalHashKey ^= getCastleRightKey(side);
                } else if (CastleRight.KING_SIDE == getCastleRight(side)) {
                    incrementalHashKey ^= getCastleRightKey(side);
                    getCastleRight().put(side, CastleRight.NONE);
                }
            } else if (move.getFrom() == ooo.getFrom()) {
                if (CastleRight.KING_AND_QUEEN_SIDE == getCastleRight(side)) {
                    incrementalHashKey ^= getCastleRightKey(side);
                    getCastleRight().put(side, CastleRight.KING_SIDE);
                    incrementalHashKey ^= getCastleRightKey(side);
                } else if (CastleRight.QUEEN_SIDE == getCastleRight(side)) {
                    incrementalHashKey ^= getCastleRightKey(side);
                    getCastleRight().put(side, CastleRight.NONE);
                }
            }
        }

        Piece capturedPiece = movePiece(move, backupMove);

        if (PieceType.ROOK == capturedPiece.getPieceType()) {
            final Move oo = context.getRookoo(side.flip());
            final Move ooo = context.getRookooo(side.flip());
            if (move.getTo() == oo.getFrom()) {
                if (CastleRight.KING_AND_QUEEN_SIDE == getCastleRight(side.flip())) {
                    incrementalHashKey ^= getCastleRightKey(side.flip());
                    getCastleRight().put(side.flip(), CastleRight.QUEEN_SIDE);
                    incrementalHashKey ^= getCastleRightKey(side.flip());
                } else if (CastleRight.KING_SIDE == getCastleRight(side.flip())) {
                    incrementalHashKey ^= getCastleRightKey(side.flip());
                    getCastleRight().put(side.flip(), CastleRight.NONE);
                }
            } else if (move.getTo() == ooo.getFrom()) {
                if (CastleRight.KING_AND_QUEEN_SIDE == getCastleRight(side.flip())) {
                    incrementalHashKey ^= getCastleRightKey(side.flip());
                    getCastleRight().put(side.flip(), CastleRight.KING_SIDE);
                    incrementalHashKey ^= getCastleRightKey(side.flip());
                } else if (CastleRight.QUEEN_SIDE == getCastleRight(side.flip())) {
                    incrementalHashKey ^= getCastleRightKey(side.flip());
                    getCastleRight().put(side.flip(), CastleRight.NONE);
                }
            }
        }

        if (Piece.NONE == capturedPiece) {
            setHalfMoveCounter(getHalfMoveCounter() + 1);
        } else {
            setHalfMoveCounter(0);
        }

        setEnPassantTarget(Square.NONE);
        setEnPassant(Square.NONE);

        if (PieceType.PAWN == movingPiece.getPieceType()) {
            if (Math.abs(move.getTo().getRank().ordinal() -
                    move.getFrom().getRank().ordinal()) == 2) {
                Piece otherPawn = Piece.make(side.flip(), PieceType.PAWN);
                setEnPassant(findEnPassant(move.getTo(), side));
                if (hasPiece(otherPawn, move.getTo().getSideSquares()) &&
                        verifyNotPinnedPiece(side, getEnPassant(), move.getTo())) {
                    setEnPassantTarget(move.getTo());
                    incrementalHashKey ^= getEnPassantKey(getEnPassantTarget());
                }
            }
            setHalfMoveCounter(0);
        }

        if (side == Side.BLACK) {
            setMoveCounter(getMoveCounter() + 1);
        }

        setSideToMove(side.flip());
        incrementalHashKey ^= getSideKey(getSideToMove());

        if (updateHistory) {
            getHistory().addLast(getIncrementalHashKey());
        }

        backup.add(backupMove);
        // call listeners
        if (isEnableEvents() && eventListener.get(BoardEventType.ON_MOVE).size() > 0) {
            for (BoardEventListener evl : eventListener.get(BoardEventType.ON_MOVE)) {
                evl.onEvent(move);
            }
        }
        return true;
    }

    /**
     * Executes a <i>null</i> move on the board. It returns {@code true} if the operation has been successful.
     * <p/>
     * A null move it is a special move that does not change the position of any piece, but simply updates the history
     * of the board and switches the side to move. It could be useful in some scenarios to implement a <i>"passing
     * turn"</i> behavior.
     *
     * @return {@code true} if the null move was successful
     */
    public boolean doNullMove() {

        Side side = getSideToMove();
        MoveBackup backupMove = new MoveBackup(this, emptyMove);

        setHalfMoveCounter(getHalfMoveCounter() + 1);

        setEnPassantTarget(Square.NONE);
        setEnPassant(Square.NONE);

        incrementalHashKey ^= getSideKey(getSideToMove());
        setSideToMove(side.flip());
        incrementalHashKey ^= getSideKey(getSideToMove());
        if (updateHistory) {
            getHistory().addLast(getIncrementalHashKey());
        }
        backup.add(backupMove);
        return true;
    }

    /**
     * Reverts the latest move played on the board and returns it. If no moves were previously executed, it returns
     * null.
     *
     * @return the reverted move, or null if no previous moves were played
     */
    public Move undoMove() {
        Move move = null;
        final MoveBackup b = backup.removeLast();
        if (updateHistory) {
            getHistory().removeLast();
        }
        if (b != null) {
            move = b.getMove();
            b.restore(this);
        }
        // call listeners
        if (isEnableEvents() &&
                eventListener.get(BoardEventType.ON_UNDO_MOVE).size() > 0) {
            for (BoardEventListener evl :
                    eventListener.get(BoardEventType.ON_UNDO_MOVE)) {
                evl.onEvent(b);
            }
        }
        return move;
    }

    /**
     * Moves a piece on the board and updates the backup passed in input. It returns the captured piece, if any, or
     * {@link Piece#NONE} otherwise.
     * <p/>
     * Same as invoking {@code movePiece(move.getFrom(), move.getTo(), move.getPromotion(), backup)}.
     *
     * @param move   the move to perform
     * @param backup the move backup to update
     * @return the captured piece, if present, or {@link Piece#NONE} otherwise
     * @see Board#movePiece(Square, Square, Piece, MoveBackup)
     */
    protected Piece movePiece(Move move, MoveBackup backup) {
        return movePiece(move.getFrom(), move.getTo(), move.getPromotion(), backup);
    }

    /**
     * Moves a piece on the board and updates the backup passed in input. It returns the captured piece, if any, or
     * {@link Piece#NONE} otherwise. The piece movement is described by its starting and destination squares, and by the
     * piece to promote the moving piece to in case of a promotion.
     *
     * @param from      the starting square of the piece
     * @param to        the destination square of the piece
     * @param promotion the piece to set on the board to replace the moving piece after its promotion, or
     *                  {@link Piece#NONE} in case the move is not a promotion
     * @param backup    the move backup to update
     * @return the captured piece, if present, or {@link Piece#NONE} otherwise
     */
    protected Piece movePiece(Square from, Square to, Piece promotion, MoveBackup backup) {
        Piece movingPiece = getPiece(from);
        Piece capturedPiece = getPiece(to);

        unsetPiece(movingPiece, from);
        if (!Piece.NONE.equals(capturedPiece)) {
            unsetPiece(capturedPiece, to);
        }
        if (!Piece.NONE.equals(promotion)) {
            setPiece(promotion, to);
        } else {
            setPiece(movingPiece, to);
        }

        if (PieceType.PAWN.equals(movingPiece.getPieceType()) &&
                !Square.NONE.equals(getEnPassantTarget()) &&
                !to.getFile().equals(from.getFile()) &&
                Piece.NONE.equals(capturedPiece)) {
            capturedPiece = getPiece(getEnPassantTarget());
            if (backup != null && !Piece.NONE.equals(capturedPiece)) {
                unsetPiece(capturedPiece, getEnPassantTarget());
                backup.setCapturedSquare(getEnPassantTarget());
                backup.setCapturedPiece(capturedPiece);
            }
        }
        return capturedPiece;
    }

    /**
     * Reverts the effects of a piece previously moved. It restores the moved piece where it was and cancels any
     * possible promotion to another piece.
     */
    protected void undoMovePiece(Move move) {
        Square from = move.getFrom();
        Square to = move.getTo();
        Piece promotion = move.getPromotion();
        Piece movingPiece = getPiece(to);

        unsetPiece(movingPiece, to);

        if (!Piece.NONE.equals(promotion)) {
            setPiece(Piece.make(getSideToMove(), PieceType.PAWN), from);
        } else {
            setPiece(movingPiece, from);
        }
    }

    /**
     * Searches the piece in any of the squares provided in input and returns {@code true} if found.
     *
     * @param piece    the piece to search in any of the given squares
     * @param location an array of squares where to look the piece for
     * @return {@code true} if the piece is found
     */
    public boolean hasPiece(Piece piece, Square[] location) {
        for (Square sq : location) {
            if ((getBitboard(piece) & sq.getBitboard()) != 0L) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the piece at the specified square, or {@link Piece#NONE} if the square is empty.
     *
     * @param sq the square to get the piece from
     * @return the found piece, or {@link Piece#NONE} if no piece is present on the square
     */
    public Piece getPiece(Square sq) {

        return occupation[sq.ordinal()];
    }

    /**
     * Returns the bitboard that represents all the pieces on the board, for both sides.
     *
     * @return the bitboard of all the pieces on the board
     */
    public long getBitboard() {
        return bbSide[0] | bbSide[1];
    }

    /**
     * Returns the bitboard that represents all the pieces of a given side and type present on the board.
     *
     * @param piece the piece for which the bitboard must be returned
     * @return the bitboard of the given piece definition
     */
    public long getBitboard(Piece piece) {
        return bitboard[piece.ordinal()];
    }

    /**
     * Returns the bitboard that represents all the pieces of a given side present on the board.
     *
     * @param side the side for which the bitboard must be returned
     * @return the bitboard of all the pieces of the side
     */
    public long getBitboard(Side side) {
        return bbSide[side.ordinal()];
    }

    /**
     * Returns the bitboards that represents all the pieces present on the board, one for each side. The bitboard for
     * white is stored at index 0, the bitboard for black at index 1.
     *
     * @return the bitboards of all the pieces for both sides
     */
    public long[] getBbSide() {
        return bbSide;
    }

    /**
     * Returns the list of squares that contain all the pieces of a given side and type.
     *
     * @param piece the piece for which the list of squares must be returned
     * @return the list of squares that contain the given piece definition
     */
    public List<Square> getPieceLocation(Piece piece) {
        if (getBitboard(piece) != 0L) {
            return Bitboard.bbToSquareList(getBitboard(piece));
        }
        return Collections.emptyList();
    }

    /**
     * Returns the square of the first piece of a given side and type found on the board, scanning from lower
     * ranks/files. If no piece is found, {@link Square#NONE} is returned.
     *
     * @param piece the piece for which the first encountered square must be returned
     * @return the first square that contain the given piece definition, or {@link Square#NONE} if the piece is not
     * found
     */
    public Square getFistPieceLocation(Piece piece) {
        if (getBitboard(piece) != 0L) {
            return Square.squareAt(Bitboard.bitScanForward(getBitboard(piece)));
        }
        return Square.NONE;
    }

    /**
     * Returns the next side to move.
     *
     * @return the next side to move
     */
    public Side getSideToMove() {
        return sideToMove;
    }

    /**
     * Sets the next side to move.
     *
     * @param sideToMove the side to move to set
     */
    public void setSideToMove(Side sideToMove) {
        this.sideToMove = sideToMove;
    }

    /**
     * Returns the target square of an en passant capture, if any. In other words, the square which contains the pawn
     * that can be captured en passant.
     *
     * @return the en passant target square, or {@link Square#NONE} if en passant is not possible
     * @see Board#getEnPassant()
     */
    public Square getEnPassantTarget() {
        return enPassantTarget;
    }

    /**
     * Sets the en passant target square.
     *
     * @param enPassant the en passant target square to set
     * @see Board#getEnPassantTarget()
     */
    public void setEnPassantTarget(Square enPassant) {
        this.enPassantTarget = enPassant;
    }

    /**
     * Returns the destination square of an en passant capture, if any. In other words, the square a pawn will move to
     * in case an enemy pawn is captured en passant.
     *
     * @return the en passant destination square, or {@link Square#NONE} if en passant is not possible
     * @see Board#getEnPassantTarget()
     */
    public Square getEnPassant() {
        return enPassant;
    }

    /**
     * Sets the en passant destination square.
     *
     * @param enPassant the en passant destination square to set
     * @see Board#getEnPassant()
     */
    public void setEnPassant(Square enPassant) {
        this.enPassant = enPassant;
    }

    /**
     * Returns the counter of full moves played. The counter is incremented after each move played by black.
     *
     * @return the counter of full moves
     */
    public Integer getMoveCounter() {
        return moveCounter;
    }

    /**
     * Sets the counter of full moves.
     *
     * @param moveCounter the counter of full moves to set
     * @see Board#getMoveCounter()
     */
    public void setMoveCounter(Integer moveCounter) {
        this.moveCounter = moveCounter;
    }

    /**
     * Returns the counter of half moves. The counter is incremented after each capture or pawn move, and it is used to
     * apply the fifty-move rule.
     *
     * @return the counter of half moves
     */
    public Integer getHalfMoveCounter() {
        return halfMoveCounter;
    }

    /**
     * Sets the counter of half moves.
     *
     * @param halfMoveCounter the counter of half moves to set
     * @see Board#getHalfMoveCounter()
     */
    public void setHalfMoveCounter(Integer halfMoveCounter) {
        this.halfMoveCounter = halfMoveCounter;
    }

    /**
     * Returns the castle right of a given side.
     *
     * @param side the side for which the castle right must be returned
     * @return the castle right of the side
     */
    public CastleRight getCastleRight(Side side) {
        return castleRight.get(side);
    }

    /**
     * Returns the castle rights for both sides, stored in an {@link EnumMap}.
     *
     * @return the map containing the castle rights for both sides
     */
    public EnumMap<Side, CastleRight> getCastleRight() {
        return castleRight;
    }

    /**
     * Returns the game context used for this board.
     *
     * @return the game context
     */
    public GameContext getContext() {
        return context;
    }

    /**
     * Sets the game context of the board.
     *
     * @param context the game context to set
     */
    public void setContext(GameContext context) {
        this.context = context;
    }

    /**
     * Returns the current ordered list of move backups generated from the moves performed on the board.
     *
     * @return the list of move backups
     */
    public LinkedList<MoveBackup> getBackup() {
        return backup;
    }

    /**
     * Clears the entire board and resets its status and all the flags to their default value.
     */
    public void clear() {
        setSideToMove(Side.WHITE);
        setEnPassantTarget(Square.NONE);
        setEnPassant(Square.NONE);
        setMoveCounter(0);
        setHalfMoveCounter(0);
        getHistory().clear();

        Arrays.fill(bitboard, 0L);
        Arrays.fill(bbSide, 0L);
        Arrays.fill(occupation, Piece.NONE);
        backup.clear();
        incrementalHashKey = 0;
    }

    /**
     * Sets a piece on a square.
     * <p/>
     * The operation does not perform any move, but rather simply puts a piece onto a square.
     *
     * @param piece the piece to be placed on the square
     * @param sq    the square the piece has to be set to
     */
    public void setPiece(Piece piece, Square sq) {
        bitboard[piece.ordinal()] |= sq.getBitboard();
        bbSide[piece.getPieceSide().ordinal()] |= sq.getBitboard();
        occupation[sq.ordinal()] = piece;
        if (piece != Piece.NONE && sq != Square.NONE) {
            incrementalHashKey ^= getPieceSquareKey(piece, sq);
        }
    }

    /**
     * Unsets a piece from a square.
     *
     * @param piece the piece to be removed from the square
     * @param sq    the square the piece has to be unset from
     */
    public void unsetPiece(Piece piece, Square sq) {
        bitboard[piece.ordinal()] ^= sq.getBitboard();
        bbSide[piece.getPieceSide().ordinal()] ^= sq.getBitboard();
        occupation[sq.ordinal()] = Piece.NONE;
        if (piece != Piece.NONE && sq != Square.NONE) {
            incrementalHashKey ^= getPieceSquareKey(piece, sq);
        }
    }

    /**
     * Loads a specific chess position from a valid Forsyth-Edwards Notation (FEN) string. The status of the current
     * board is replaced with the one of the FEN string (e.g. en passant squares, castle rights, etc.).
     *
     * @param fen the FEN string representing the chess position to load
     */
    public void loadFromFen(String fen) {
        clear();
        String squares = fen.substring(0, fen.indexOf(' '));
        String state = fen.substring(fen.indexOf(' ') + 1);

        String[] ranks = squares.split("/");
        int file;
        int rank = 7;
        for (String r : ranks) {
            file = 0;
            for (int i = 0; i < r.length(); i++) {
                char c = r.charAt(i);
                if (Character.isDigit(c)) {
                    file += Integer.parseInt(c + "");
                } else {
                    Square sq = Square.encode(Rank.allRanks[rank], File.allFiles[file]);
                    setPiece(Piece.fromFenSymbol(String.valueOf(c)), sq);
                    file++;
                }
            }
            rank--;
        }

        sideToMove = state.toLowerCase().charAt(0) == 'w' ? Side.WHITE : Side.BLACK;

        if (state.contains("KQ")) {
            castleRight.put(Side.WHITE, CastleRight.KING_AND_QUEEN_SIDE);
        } else if (state.contains("K")) {
            castleRight.put(Side.WHITE, CastleRight.KING_SIDE);
        } else if (state.contains("Q")) {
            castleRight.put(Side.WHITE, CastleRight.QUEEN_SIDE);
        } else {
            castleRight.put(Side.WHITE, CastleRight.NONE);
        }

        if (state.contains("kq")) {
            castleRight.put(Side.BLACK, CastleRight.KING_AND_QUEEN_SIDE);
        } else if (state.contains("k")) {
            castleRight.put(Side.BLACK, CastleRight.KING_SIDE);
        } else if (state.contains("q")) {
            castleRight.put(Side.BLACK, CastleRight.QUEEN_SIDE);
        } else {
            castleRight.put(Side.BLACK, CastleRight.NONE);
        }

        String[] flags = state.split(" ");

        if (flags.length >= 3) {
            String s = flags[2].toUpperCase().trim();
            if (!s.equals("-")) {
                Square ep = Square.valueOf(s);
                setEnPassant(ep);
                setEnPassantTarget(findEnPassantTarget(ep, sideToMove));
                if (!pawnCanBeCapturedEnPassant()) {
                    setEnPassantTarget(Square.NONE);
                }
            } else {
                setEnPassant(Square.NONE);
                setEnPassantTarget(Square.NONE);
            }
            if (flags.length >= 4) {
                halfMoveCounter = Integer.parseInt(flags[3]);
                if (flags.length >= 5) {
                    moveCounter = Integer.parseInt(flags[4]);
                }
            }
        }

        incrementalHashKey = getZobristKey();
        if (updateHistory) {
            getHistory().addLast(this.getZobristKey());
        }
        // call listeners
        if (isEnableEvents() &&
                eventListener.get(BoardEventType.ON_LOAD).size() > 0) {
            for (BoardEventListener evl :
                    eventListener.get(BoardEventType.ON_LOAD)) {
                evl.onEvent(Board.this);
            }
        }
    }

    /**
     * Generates the Forsyth-Edwards Notation (FEN) representation of the current position and its status. Full and half
     * moves counters are included in the output.
     * <p/>
     * Same as invoking {@code getFen(true, false)}.
     *
     * @return the string that represents the current position in FEN notation
     * @see Board#getFen(boolean, boolean)
     */
    public String getFen() {
        return getFen(true);
    }

    /**
     * Generates the Forsyth-Edwards Notation (FEN) representation of the current position and its status. Full and half
     * moves counters are included in the output if the relative flag is enabled.
     * <p/>
     * Same as invoking {@code getFen(includeCounters, false)}.
     *
     * @param includeCounters if {@code true}, move counters are included in the resulting string
     * @return the string that represents the current position in FEN notation
     * @see Board#getFen(boolean, boolean)
     */
    public String getFen(boolean includeCounters) {
        return getFen(includeCounters, false);
    }

    /**
     * Generates the Forsyth-Edwards Notation (FEN) representation of the current position and its status. Full and half
     * moves counters are included in the output if the relative flag is enabled. Furthermore, it is possible to control
     * whether to include the en passant square in the result only when the pawn can be captured or every time the en
     * passant target exists.
     *
     * @param includeCounters                 if {@code true}, move counters are included in the resulting string
     * @param onlyOutputEnPassantIfCapturable if {@code true}, the en passant square is included in the output only if
     *                                        the pawn that just moved can be captured. Otherwise, if {@code false}, the
     *                                        en passant square is always included in the output when the en passant
     *                                        target exists
     * @return the string that represents the current position in FEN notation
     */
    public String getFen(boolean includeCounters, boolean onlyOutputEnPassantIfCapturable) {

        StringBuffer fen = new StringBuffer();
        int emptySquares = 0;
        for (int i = 7; i >= 0; i--) {
            Rank r = Rank.allRanks[i];
            if (r == Rank.NONE) {
                continue;
            }
            for (File f : File.allFiles) {
                if (f == File.NONE) {
                    continue;
                }
                Square sq = Square.encode(r, f);
                Piece piece = getPiece(sq);
                if (Piece.NONE.equals(piece)) {
                    emptySquares++;
                } else {
                    if (emptySquares > 0) {
                        fen.append(emptySquares);
                        emptySquares = 0;
                    }
                    fen.append(piece.getFenSymbol());
                }
                if (f != File.FILE_H) {
                    continue;
                }
                if (emptySquares > 0) {
                    fen.append(emptySquares);
                    emptySquares = 0;
                }
                if (r != Rank.RANK_1) {
                    fen.append("/");
                }
            }
        }

        if (Side.WHITE.equals(sideToMove)) {
            fen.append(" w");
        } else {
            fen.append(" b");
        }

        String rights = "";
        if (CastleRight.KING_AND_QUEEN_SIDE.
                equals(castleRight.get(Side.WHITE))) {
            rights += "KQ";
        } else if (CastleRight.KING_SIDE.
                equals(castleRight.get(Side.WHITE))) {
            rights += "K";
        } else if (CastleRight.QUEEN_SIDE.
                equals(castleRight.get(Side.WHITE))) {
            rights += "Q";
        }

        if (CastleRight.KING_AND_QUEEN_SIDE.
                equals(castleRight.get(Side.BLACK))) {
            rights += "kq";
        } else if (CastleRight.KING_SIDE.
                equals(castleRight.get(Side.BLACK))) {
            rights += "k";
        } else if (CastleRight.QUEEN_SIDE.
                equals(castleRight.get(Side.BLACK))) {
            rights += "q";
        }

        if (rights.equals("")) {
            fen.append(" -");
        } else {
            fen.append(" " + rights);
        }

        if (Square.NONE.equals(getEnPassant())
                || (onlyOutputEnPassantIfCapturable
                && !pawnCanBeCapturedEnPassant())) {
            fen.append(" -");
        } else {
            fen.append(" ");
            fen.append(getEnPassant().toString().toLowerCase());
        }

        if (includeCounters) {
            fen.append(" ");
            fen.append(getHalfMoveCounter());

            fen.append(" ");
            fen.append(getMoveCounter());
        }

        return fen.toString();
    }

    /**
     * Returns an array of pieces that represents the current position on the board. For each index, the array holds
     * the piece present on the square with the same index, or {@link Piece#NONE} if the square is empty.
     *
     * @return the array that contains the pieces on the board
     */
    public Piece[] boardToArray() {

        final Piece[] pieces = new Piece[65];
        pieces[64] = Piece.NONE;

        for (Square square : Square.values()) {
            if (!Square.NONE.equals(square)) {
                pieces[square.ordinal()] = getPiece(square);
            }
        }

        return pieces;
    }

    /**
     * The type of board events this data structure represents when notified to its observers.
     *
     * @return the board event type {@link BoardEventType#ON_LOAD}
     */
    @Override
    public BoardEventType getType() {
        return BoardEventType.ON_LOAD;
    }

    /**
     * Returns an {@link EnumMap} of the event listeners registered to this board. Each entry of the map contains the
     * list of observers for a particular type of events.
     *
     * @return the event listeners registered to this board
     */
    public EnumMap<BoardEventType, List<BoardEventListener>> getEventListener() {
        return eventListener;
    }

    /**
     * Registers to the board a new listener for a specified event type.
     * <p/>
     * It returns a reference to this board to fluently chain other calls for registering (or deregistering) other
     * listeners.
     *
     * @param eventType the board event type observed by the listener
     * @param listener  the listener to register
     * @return this board
     */
    public Board addEventListener(BoardEventType eventType, BoardEventListener listener) {
        getEventListener().get(eventType).add(listener);
        return this;
    }

    /**
     * Deregisters from the board a listener for a specified event type.
     * <p/>
     * It returns a reference to this board to fluently chain other calls for deregistering (or registering) other
     * listeners.
     *
     * @param eventType the board event type observed by the listener
     * @param listener  the listener to deregister
     * @return this board
     */
    public Board removeEventListener(BoardEventType eventType, BoardEventListener listener) {
        if (getEventListener() != null && getEventListener().get(eventType) != null) {
            getEventListener().get(eventType).remove(listener);
        }
        return this;
    }

    /**
     * Returns the bitboard representing the pieces of a specific side that can attack the given square.
     * <p/>
     * Same as invoking {@code squareAttackedBy(square, side, getBitboard())}.
     *
     * @param square the target square
     * @param side   the attacking side
     * @return the bitboard of all the pieces of the given side that can attack the square
     * @see Board#squareAttackedBy(Square, Side, long)
     */
    public long squareAttackedBy(Square square, Side side) {
        return squareAttackedBy(square, side, getBitboard());
    }

    /**
     * Returns the bitboard representing the pieces of a specific side that can attack the given square. It takes a
     * bitboard mask in input to filter the result for a specific set of occupied squares only.
     *
     * @param square the target square
     * @param side   the attacking side
     * @param occ    a mask of occupied squares
     * @return the bitboard of all the pieces of the given side that can attack the square
     */
    public long squareAttackedBy(Square square, Side side, long occ) {
        long result;
        result = Bitboard.getPawnAttacks(side.flip(), square) &
                getBitboard(Piece.make(side, PieceType.PAWN)) & occ;
        result |= Bitboard.getKnightAttacks(square, occ) &
                getBitboard(Piece.make(side, PieceType.KNIGHT));
        result |= Bitboard.getBishopAttacks(occ, square) &
                ((getBitboard(Piece.make(side, PieceType.BISHOP)) |
                        getBitboard(Piece.make(side, PieceType.QUEEN))));
        result |= Bitboard.getRookAttacks(occ, square) &
                ((getBitboard(Piece.make(side, PieceType.ROOK)) |
                        getBitboard(Piece.make(side, PieceType.QUEEN))));
        result |= Bitboard.getKingAttacks(square, occ) &
                getBitboard(Piece.make(side, PieceType.KING));
        return result;
    }

    /**
     * Returns the bitboard representing the pieces of a specific side and type that can attack the given square.
     *
     * @param square the target square
     * @param side   the attacking side
     * @param type   the type of the attacking pieces
     * @return the bitboard of all the pieces of the given side and type that can attack the square
     */
    public long squareAttackedByPieceType(Square square, Side side, PieceType type) {
        long result = 0L;
        long occ = getBitboard();
        switch (type) {
            case PAWN:
                result = Bitboard.getPawnAttacks(side.flip(), square) &
                        getBitboard(Piece.make(side, PieceType.PAWN));
                break;
            case KNIGHT:
                result = Bitboard.getKnightAttacks(square, occ) &
                        getBitboard(Piece.make(side, PieceType.KNIGHT));
                break;
            case BISHOP:
                result = Bitboard.getBishopAttacks(occ, square) &
                        getBitboard(Piece.make(side, PieceType.BISHOP));
                break;
            case ROOK:
                result = Bitboard.getRookAttacks(occ, square) &
                        getBitboard(Piece.make(side, PieceType.ROOK));
                break;
            case QUEEN:
                result = Bitboard.getQueenAttacks(occ, square) &
                        getBitboard(Piece.make(side, PieceType.QUEEN));
                break;
            case KING:
                result |= Bitboard.getKingAttacks(square, occ) &
                        getBitboard(Piece.make(side, PieceType.KING));
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * Returns the square occupied by the king of the given side.
     *
     * @param side the side of the king
     * @return the square occupied by the king
     */
    public Square getKingSquare(Side side) {
        Square result = Square.NONE;
        long piece = getBitboard(Piece.make(side, PieceType.KING));
        if (piece != 0L) {
            int sq = Bitboard.bitScanForward(piece);
            return Square.squareAt(sq);
        }
        return result;
    }

    /**
     * Checks if the king of the side to move is attacked by any enemy piece.
     *
     * @return {@code true} if the king of the next side to move is attacked
     */
    public boolean isKingAttacked() {
        return squareAttackedBy(getKingSquare(getSideToMove()), getSideToMove().flip()) != 0;
    }

    /**
     * Checks if any of the squares provided in input is attacked by the given side in the current position.
     *
     * @param squares the target squares
     * @param side    the attacking side
     * @return {@code true} if any square is attacked
     */
    public boolean isSquareAttackedBy(List<Square> squares, Side side) {
        for (Square sq : squares) {
            if (squareAttackedBy(sq, side) != 0L) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifies if the move still to be executed will leave the resulting board in a valid (legal) position. Optionally,
     * it can perform a full validation, a stricter check to assess if the final board configuration could be considered
     * valid or not.
     * <p/>
     * The full validation checks:
     * <ul>
     *     <li>if a piece is actually moving;</li>
     *     <li>if the moving side is the next side to move in the position;</li>
     *     <li>if the destination square does not contain a piece of the same side of the moving one;</li>
     *     <li>in case of a promotion, if a promoting piece is present;</li>
     *     <li>in case of castling, if the castle move can be performed.</li>
     * </ul>
     * <b>N.B.</b>: the method does not check whether the move is legal or not according to the standard chess rules,
     * but only if the resulting configuration is valid. For instance, it is considered valid moving the king by two or
     * more squares, or a rook beyond its friendly pieces, as long as the position obtained after the move does not
     * violate any chess constraint.
     *
     * @param move           the move to validate
     * @param fullValidation performs a full validation of the move
     * @return {@code true} if the move is considered valid
     */
    public boolean isMoveLegal(Move move, boolean fullValidation) {

        final Piece fromPiece = getPiece(move.getFrom());
        final Side side = getSideToMove();
        final PieceType fromType = fromPiece.getPieceType();
        final Piece capturedPiece = getPiece(move.getTo());

        if (fullValidation) {
            if (Piece.NONE.equals(fromPiece)) {
                throw new RuntimeException("From piece cannot be null");
            }

            if (fromPiece.getPieceSide().equals(capturedPiece.getPieceSide())) {
                return false;
            }

            if (!side.equals(fromPiece.getPieceSide())) {
                return false;
            }

            boolean pawnPromoting = fromPiece.getPieceType().equals(PieceType.PAWN) &&
                    isPromoRank(side, move);
            boolean hasPromoPiece = !move.getPromotion().equals(Piece.NONE);

            if (hasPromoPiece != pawnPromoting) {
                return false;
            }
            if (fromType.equals(PieceType.KING)) {
                if (getContext().isKingSideCastle(move)) {
                    if (getCastleRight(side).equals(CastleRight.KING_AND_QUEEN_SIDE) ||
                            (getCastleRight(side).equals(CastleRight.KING_SIDE))) {
                        if ((getBitboard() & getContext().getooAllSquaresBb(side)) == 0L) {
                            return !isSquareAttackedBy(getContext().getooSquares(side), side.flip());
                        }
                    }
                    return false;
                }
                if (getContext().isQueenSideCastle(move)) {
                    if (getCastleRight(side).equals(CastleRight.KING_AND_QUEEN_SIDE) ||
                            (getCastleRight(side).equals(CastleRight.QUEEN_SIDE))) {
                        if ((getBitboard() & getContext().getoooAllSquaresBb(side)) == 0L) {
                            return !isSquareAttackedBy(getContext().getoooSquares(side), side.flip());
                        }
                    }
                    return false;
                }
            }
        }
        if (fromType.equals(PieceType.KING)) {
            if (squareAttackedBy(move.getTo(), side.flip()) != 0L) {
                return false;
            }
        }
        Square kingSq = (fromType.equals(PieceType.KING) ?
                move.getTo() : getKingSquare(side));
        Side other = side.flip();
        long moveTo = move.getTo().getBitboard();
        long moveFrom = move.getFrom().getBitboard();
        long ep = getEnPassantTarget() != Square.NONE && move.getTo() == getEnPassant() &&
                (fromType.equals(PieceType.PAWN)) ? getEnPassantTarget().getBitboard() : 0;
        long allPieces = (getBitboard() ^ moveFrom ^ ep) | moveTo;

        long bishopAndQueens = ((getBitboard(Piece.make(other, PieceType.BISHOP)) |
                getBitboard(Piece.make(other, PieceType.QUEEN)))) & ~moveTo;

        if (bishopAndQueens != 0L &&
                (Bitboard.getBishopAttacks(allPieces, kingSq) & bishopAndQueens) != 0L) {
            return false;
        }

        long rookAndQueens = ((getBitboard(Piece.make(other, PieceType.ROOK)) |
                getBitboard(Piece.make(other, PieceType.QUEEN)))) & ~moveTo;

        if (rookAndQueens != 0L &&
                (Bitboard.getRookAttacks(allPieces, kingSq) & rookAndQueens) != 0L) {
            return false;
        }

        long knights = (getBitboard(Piece.make(other, PieceType.KNIGHT))) & ~moveTo;

        if (knights != 0L &&
                (Bitboard.getKnightAttacks(kingSq, allPieces) & knights) != 0L) {
            return false;
        }

        long pawns = (getBitboard(Piece.make(other, PieceType.PAWN))) & ~moveTo & ~ep;

        return pawns == 0L ||
                (Bitboard.getPawnAttacks(side, kingSq) & pawns) == 0L;
    }

    /**
     * Checks if the squares of a move are consistent, that is, if the destination square is attacked by the piece
     * placed on the starting square.
     *
     * @return {@code true} if the move is coherent
     */
    public boolean isAttackedBy(Move move) {

        PieceType pieceType = getPiece(move.getFrom()).getPieceType();
        assert (!PieceType.NONE.equals(pieceType));
        Side side = getSideToMove();
        long attacks = 0L;
        switch (pieceType) {
            case PAWN:
                if (!move.getFrom().getFile().equals(move.getTo().getFile())) {
                    attacks = Bitboard.getPawnCaptures(side, move.getFrom(),
                            getBitboard(), getEnPassantTarget());
                } else {
                    attacks = Bitboard.getPawnMoves(side, move.getFrom(), getBitboard());
                }
                break;
            case KNIGHT:
                attacks = Bitboard.getKnightAttacks(move.getFrom(), ~getBitboard(side));
                break;
            case BISHOP:
                attacks = Bitboard.getBishopAttacks(getBitboard(), move.getFrom());
                break;
            case ROOK:
                attacks = Bitboard.getRookAttacks(getBitboard(), move.getFrom());
                break;
            case QUEEN:
                attacks = Bitboard.getQueenAttacks(getBitboard(), move.getFrom());
                break;
            case KING:
                attacks = Bitboard.getKingAttacks(move.getFrom(), ~getBitboard(side));
                break;
            default:
                break;
        }
        return (attacks & move.getTo().getBitboard()) != 0L;
    }

    /**
     * Returns the history of the board, represented by the hashes of all the positions occurred on the board.
     *
     * @return the list of hashes of all the positions occurred on the board
     * @see Board#getIncrementalHashKey()
     */
    public LinkedList<Long> getHistory() {
        return history;
    }

    /**
     * Verifies in the current position if the king of the side to move is mated.
     *
     * @return {@code true} if the king of the side to move is checkmated
     */
    public boolean isMated() {
        try {
            if (isKingAttacked()) {
                final List<Move> l = MoveGenerator.generateLegalMoves(this);
                if (l.size() == 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * Verifies if the current position is a forced draw because any of the standard chess rules. Specifically, the
     * method checks for:
     * <ul>
     *     <li>threefold repetition;</li>
     *     <li>insufficient material;</li>
     *     <li>fifty-move rule;</li>
     *     <li>stalemate.</li>
     * </ul>
     *
     * @return {@code true} if the position is a draw
     */
    public boolean isDraw() {
        if (isRepetition()) {
            return true;
        }
        if (isInsufficientMaterial()) {
            return true;
        }
        if (getHalfMoveCounter() >= 100) {
            return true;
        }
        return isStaleMate();

    }

    /**
     * Verifies if the current position has been repeated at least <i>n</i> times, where <i>n</i> is provided in input.
     *
     * @return {@code true} if the position has been repeated at least <i>n</i> times
     */
    public boolean isRepetition(int n) {

        final int i = Math.min(getHistory().size() - 1, getHalfMoveCounter());
        if (getHistory().size() >= 4) {
            long lastKey = getHistory().get(getHistory().size() - 1);
            int rep = 0;
            for (int x = 4; x <= i; x += 2) {
                final long k = getHistory().get(getHistory().size() - x - 1);
                if (k == lastKey && ++rep >= n - 1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Verifies if the current position has been repeated at least three times (threefold repetition).
     * <p/>
     * Same as invoking {@code isRepetition(3)}.
     *
     * @return {@code true} if the position has been repeated at least three times
     * @see Board#isRepetition(int)
     */
    public boolean isRepetition() {

        return isRepetition(3);
    }

    /**
     * Verifies if the current position has insufficient material to continue the game, and thus it must be considered a
     * forced draw.
     *
     * @return {@code true} if the position has insufficient material
     */
    public boolean isInsufficientMaterial() {

        if ((getBitboard(Piece.WHITE_QUEEN) +
                getBitboard(Piece.BLACK_QUEEN) +
                getBitboard(Piece.WHITE_ROOK) +
                getBitboard(Piece.BLACK_ROOK)) != 0L) {
            return false;
        }

        final long pawns = getBitboard(Piece.WHITE_PAWN) | getBitboard(Piece.BLACK_PAWN);
        if (pawns == 0L) {
            long count = Long.bitCount(getBitboard());
            int whiteCount = Long.bitCount(getBitboard(Side.WHITE));
            int blackCount = Long.bitCount(getBitboard(Side.BLACK));
            if (count == 4) {
                int whiteBishopCount = Long.bitCount(getBitboard(Piece.WHITE_BISHOP));
                int blackBishopCount = Long.bitCount(getBitboard(Piece.BLACK_BISHOP));
                if (whiteCount > 1 && blackCount > 1) {
                    return !((whiteBishopCount == 1 && blackBishopCount == 1) &&
                            getFistPieceLocation(Piece.WHITE_BISHOP).isLightSquare() !=
                                    getFistPieceLocation(Piece.BLACK_BISHOP).isLightSquare());
                }
                if (whiteCount == 3 || blackCount == 3) {
                    if (whiteBishopCount == 2 &&
                            ((Bitboard.lightSquares & getBitboard(Piece.WHITE_BISHOP)) == 0L ||
                                    (Bitboard.darkSquares & getBitboard(Piece.WHITE_BISHOP)) == 0L)) {
                        return true;
                    } else return blackBishopCount == 2 &&
                            ((Bitboard.lightSquares & getBitboard(Piece.BLACK_BISHOP)) == 0L ||
                                    (Bitboard.darkSquares & getBitboard(Piece.BLACK_BISHOP)) == 0L);
                } else {
                    return Long.bitCount(getBitboard(Piece.WHITE_KNIGHT)) == 2 ||
                            Long.bitCount(getBitboard(Piece.BLACK_KNIGHT)) == 2;
                }
            } else {
                if ((getBitboard(Piece.WHITE_KING) | getBitboard(Piece.WHITE_BISHOP)) == getBitboard(Side.WHITE) &&
                        ((getBitboard(Piece.BLACK_KING) | getBitboard(Piece.BLACK_BISHOP)) == getBitboard(Side.BLACK))) {
                    return (((Bitboard.lightSquares & getBitboard(Piece.WHITE_BISHOP)) == 0L) &&
                            ((Bitboard.lightSquares & getBitboard(Piece.BLACK_BISHOP)) == 0L)) ||
                            ((Bitboard.darkSquares & getBitboard(Piece.WHITE_BISHOP)) == 0L) &&
                                    ((Bitboard.darkSquares & getBitboard(Piece.BLACK_BISHOP)) == 0L);
                }
                return count < 4;
            }
        }

        return false;
    }

    /**
     * Verifies in the current position if the king of the side to move is stalemated, and thus if the position must be
     * considered a forced draw.
     *
     * @return {@code true} if the king of the side to move is stalemated
     */
    public boolean isStaleMate() {
        try {
            if (!isKingAttacked()) {
                List<Move> l = MoveGenerator.generateLegalMoves(this);
                if (l.size() == 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * Returns whether the notifications of board events are enabled or not.
     *
     * @return {@code true} if board events are notified to observers
     */
    public boolean isEnableEvents() {
        return enableEvents;
    }

    /**
     * Sets the flag that controls the notification of board events. If {@code true}, board events are emitted,
     * otherwise they are turned off.
     *
     * @param enableEvents whether the notification of board events is enabled or not
     */
    public void setEnableEvents(boolean enableEvents) {
        this.enableEvents = enableEvents;
    }

    /**
     * Returns the unique position ID for the current position and status. The identifier is nothing more than the
     * Forsyth-Edwards Notation (FEN) representation of the board without the move counters.
     * <p/>
     * Although this is a reliable way for identifying a unique position, it is much slower than using
     * {@link Board#hashCode()} or {@link Board#getZobristKey()}.
     *
     * @return the unique position ID
     * @see Board#hashCode()
     * @see Board#getZobristKey()
     */
    public String getPositionId() {
        String[] parts = this.getFen(false).split(" ");
        return parts[0] + " " + parts[1] + " " + parts[2] +
                (this.getEnPassantTarget() != Square.NONE ? parts[3] : "-");
    }

    /**
     * Returns the list of all possible legal moves for the current position according to the standard rules of chess.
     * If such moves are played, it is guaranteed the resulting position will also be legal.
     *
     * @return the list of legal moves available in the current position
     */
    public List<Move> legalMoves() {

        return MoveGenerator.generateLegalMoves(this);
    }

    /**
     * Returns the list of all possible pseudo-legal moves for the current position.
     * <p/>
     * A move is considered pseudo-legal when it is legal according to the standard rules of chess piece movements, but
     * the resulting position might not be legal because of other rules (e.g. checks to the king).
     *
     * @return the list of pseudo-legal moves available in the current position
     */
    public List<Move> pseudoLegalMoves() {

        return MoveGenerator.generatePseudoLegalMoves(this);
    }

    /**
     * Returns the list of all possible pseudo-legal captures for the current position.
     * <p/>
     * A move is considered a pseudo-legal capture when it takes an enemy piece and it is legal according to the
     * standard rules of chess piece movements, but the resulting position might not be legal because of other rules
     * (e.g. checks to the king).
     *
     * @return the list of pseudo-legal captures available in the current position
     */
    public List<Move> pseudoLegalCaptures() {

        return MoveGenerator.generatePseudoLegalCaptures(this);
    }

    /**
     * Checks if this board is equivalent to another.
     * <p/>
     * Two boards are considered equivalent when:
     * <ul>
     *     <li>the pieces are the same, placed on the very same squares;</li>
     *     <li>the side to move is the same;</li>
     *     <li>the castling rights are the same;</li>
     *     <li>the en passant target is the same.</li>
     * </ul>
     *
     * @param obj the other object reference to compare to this board
     * @return {@code true} if this board and the object reference are equivalent
     * @see Board#strictEquals(Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Board) {
            Board board = (Board) obj;
            for (Piece piece : Piece.allPieces) {
                if (piece != Piece.NONE && getBitboard(piece) != board.getBitboard(piece)) {
                    return false;
                }
            }
            return getSideToMove() == board.getSideToMove()
                    && getCastleRight(Side.WHITE) == board.getCastleRight(Side.WHITE)
                    && getCastleRight(Side.BLACK) == board.getCastleRight(Side.BLACK)
                    && getEnPassant() == board.getEnPassant()
                    && getEnPassantTarget() == board.getEnPassantTarget();

        }
        return false;
    }

    /**
     * Checks if this board is equivalent to another performing a strict comparison.
     * <p/>
     * Two boards are considered strictly equivalent when:
     * <ul>
     *     <li>they are equivalent;</li>
     *     <li>their history is the same.</li>
     * </ul>
     *
     * @param obj the other object reference to compare to this board
     * @return {@code true} if this board and the object reference are strictly equivalent
     * @see Board#equals(Object)
     */
    public boolean strictEquals(Object obj) {
        if (obj instanceof Board) {
            Board board = (Board) obj;
            return equals(board) && board.getHistory().equals(this.getHistory());
        }
        return false;
    }

    /**
     * Returns a hash code value for this board.
     *
     * @return a hash value for this board
     */
    @Override
    public int hashCode() {
        return (int) incrementalHashKey;
    }

    /**
     * Returns a Zobrist hash code value for this board. A Zobrist hashing assures the same position returns the same
     * hash value. It is calculated using the position of the pieces, the side to move, the castle rights and the en
     * passant target.
     *
     * @return a Zobrist hash value for this board
     * @see <a href="https://en.wikipedia.org/wiki/Zobrist_hashing">Zobrist hashing in Wikipedia</a>
     */
    public long getZobristKey() {
        long hash = 0;
        if (getCastleRight(Side.WHITE) != CastleRight.NONE) {
            hash ^= getCastleRightKey(Side.WHITE);
        }
        if (getCastleRight(Side.BLACK) != CastleRight.NONE) {
            hash ^= getCastleRightKey(Side.BLACK);
        }
        for (Square sq : Square.values()) {
            Piece piece = getPiece(sq);
            if (!Piece.NONE.equals(piece) && !Square.NONE.equals(sq)) {
                hash ^= getPieceSquareKey(piece, sq);
            }
        }
        hash ^= getSideKey(getSideToMove());

        if (Square.NONE != getEnPassantTarget() &&
                pawnCanBeCapturedEnPassant()) {
            hash ^= getEnPassantKey(getEnPassantTarget());
        }
        return hash;
    }

    private long getCastleRightKey(Side side) {
        return keys.get(3 * getCastleRight(side).ordinal() + 300 + 3 * side.ordinal());
    }

    private long getSideKey(Side side) {
        return keys.get(3 * side.ordinal() + 500);
    }

    private long getEnPassantKey(Square enPassantTarget) {
        return keys.get(3 * enPassantTarget.ordinal() + 400);
    }

    private long getPieceSquareKey(Piece piece, Square square) {
        return keys.get(57 * piece.ordinal() + 13 * square.ordinal());
    }

    /**
     * Returns a human-readable representation of the board taking the perspective of white, with the 1st rank at the
     * bottom and the 8th rank at the top.
     * <p/>
     * Same as invoking {@code toStringFromViewPoint(Side.WHITE)}.
     *
     * @return a string representation of the board from white player's point of view
     * @see Board#toStringFromViewPoint(Side)
     */
    public String toStringFromWhiteViewPoint() {
        return toStringFromViewPoint(Side.WHITE);
    }

    /**
     * Returns a human-readable representation of the board taking the perspective of black, with the 8th rank at the
     * bottom and the 1st rank at the top.
     * <p/>
     * Same as invoking {@code toStringFromViewPoint(Side.BLACK)}.
     *
     * @return a string representation of the board from black player's point of view
     * @see Board#toStringFromViewPoint(Side)
     */
    public String toStringFromBlackViewPoint() {
        return toStringFromViewPoint(Side.BLACK);
    }

    /**
     * Returns a human-readable representation of the board taking the perspective of one side, with the 1st rank at the
     * bottom in case of white, or the 8th rank at the bottom in case of black.
     *
     * @param side the side whose home rank should be at the bottom of the resulting representation
     * @return a string representation of the board using one of the two player's point of view
     */
    public String toStringFromViewPoint(Side side) {
        StringBuilder sb = new StringBuilder();

        final Supplier<IntStream> rankIterator = side == Side.WHITE
                ? Board::sevenToZero : Board::zeroToSeven;
        final Supplier<IntStream> fileIterator = side == Side.WHITE
                ? Board::zeroToSeven : Board::sevenToZero;

        rankIterator.get().forEach(i -> {
            Rank r = Rank.allRanks[i];
            fileIterator.get().forEach(n -> {
                File f = File.allFiles[n];
                if (!File.NONE.equals(f) && !Rank.NONE.equals(r)) {
                    Square sq = Square.encode(r, f);
                    Piece piece = getPiece(sq);
                    sb.append(piece.getFenSymbol());
                }
            });
            sb.append("\n");
        });

        return sb.toString();
    }

    /**
     * Returns a string representation of this board.
     * <p/>
     * The result of {@link Board#toStringFromWhiteViewPoint()} is used to print the position of the board.
     *
     * @return a string representation of the board
     * @see Board#toStringFromWhiteViewPoint()
     */
    @Override
    public String toString() {
        return toStringFromWhiteViewPoint() + "Side: " + getSideToMove();
    }

    /**
     * Returns a reference to a copy of the board. The board history is copied as well.
     *
     * @return a copy of the board
     */
    @Override
    public Board clone() {
        Board copy = new Board(getContext(), this.updateHistory);
        copy.loadFromFen(this.getFen());
        copy.setEnPassantTarget(this.getEnPassantTarget());
        copy.getHistory().clear();
        for (long key : getHistory()) {
            copy.getHistory().add(key);
        }
        return copy;
    }

    /**
     * Returns the current incremental hash key. This hash value changes every time the position changes, hence it is
     * unique for every position.
     *
     * @return the current incremental hash key
     */
    public long getIncrementalHashKey() {
        return incrementalHashKey;
    }

    /**
     * Sets the current incremental hash key, replacing the previous one.
     *
     * @param hashKey the incremental hash key to set
     */
    public void setIncrementalHashKey(long hashKey) {
        incrementalHashKey = hashKey;
    }

    private boolean pawnCanBeCapturedEnPassant() {
        return
                squareAttackedByPieceType(getEnPassant(), getSideToMove(), PieceType.PAWN) != 0
                        && verifyNotPinnedPiece(getSideToMove().flip(), getEnPassant(), getEnPassantTarget());
    }

    private boolean verifyNotPinnedPiece(Side side, Square enPassant, Square target) {

        long pawns = Bitboard.getPawnAttacks(side, enPassant) & getBitboard(Piece.make(side.flip(), PieceType.PAWN));
        return pawns != 0 && verifyAllPins(pawns, side, enPassant, target);
    }

    private boolean verifyAllPins(long pawns, Side side, Square enPassant, Square target) {

        long onePawn = extractLsb(pawns);
        long otherPawn = pawns ^ onePawn;
        if (onePawn != 0L && verifyKingIsNotAttackedWithoutPin(side, enPassant, target, onePawn)) {
            return true;
        }
        return verifyKingIsNotAttackedWithoutPin(side, enPassant, target, otherPawn);
    }

    private boolean verifyKingIsNotAttackedWithoutPin(Side side, Square enPassant, Square target, long pawns) {

        return squareAttackedBy(getKingSquare(side.flip()), side, removePieces(enPassant, target, pawns)) == 0L;
    }

    private long removePieces(Square enPassant, Square target, long pieces) {

        return (getBitboard() ^ pieces ^ target.getBitboard()) | enPassant.getBitboard();
    }
}

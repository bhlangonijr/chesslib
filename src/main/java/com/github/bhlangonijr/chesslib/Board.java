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

import com.github.bhlangonijr.chesslib.game.GameContext;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.util.XorShiftRandom;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.github.bhlangonijr.chesslib.Bitboard.extractLsb;
import static com.github.bhlangonijr.chesslib.Constants.emptyMove;

/**
 * Chessboard data structure
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
     * Instantiates a new Board.
     */
    public Board() {
        this(new GameContext(), true);
    }

    /**
     * Instantiates a new Board.
     *
     * @param gameContext   the game context
     * @param updateHistory the update history
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
     * Execute the move on the board using SAN notation
     *
     * @param move the move in SAN (e.g.: Nc3)
     * @return true if operation was successful
     */
    public boolean doMove(final String move) {

        MoveList moves = new MoveList(this.getFen());
        moves.addSanMove(move, true, true);
        return doMove(moves.removeLast(), true);
    }

    /**
     * Execute the move on the board
     *
     * @param move the move
     * @return true if operation was successful
     */
    public boolean doMove(final Move move) {
        return doMove(move, false);
    }

    /**
     * Execute the move on the board
     *
     * @param move           the move
     * @param fullValidation perform full validation
     * @return true if operation was successful
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
        //call listeners
        if (isEnableEvents() && eventListener.get(BoardEventType.ON_MOVE).size() > 0) {
            for (BoardEventListener evl : eventListener.get(BoardEventType.ON_MOVE)) {
                evl.onEvent(move);
            }
        }
        return true;
    }

    /**
     * Execute a null move on the board -
     *
     * @return true if operation was successful
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
     * Undo the last move executed on the board
     *
     * @return the move
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
        //call listeners
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
     * Move piece piece.
     *
     * @param move   the move
     * @param backup the backup
     * @return the piece
     */
    /*
     * Move a piece
     * @param move
     * @return
     */
    protected Piece movePiece(Move move, MoveBackup backup) {
        return movePiece(move.getFrom(), move.getTo(), move.getPromotion(), backup);
    }

    /**
     * Move piece piece.
     *
     * @param from      the from
     * @param to        the to
     * @param promotion the promotion
     * @param backup    the backup
     * @return the piece
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
     * Undo move piece.
     *
     * @param move the move
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
     * Returns true if finds the specific piece in the given square locations
     *
     * @param piece    the piece
     * @param location the location
     * @return boolean
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
     * Get the piece on the given square
     *
     * @param sq the sq
     * @return piece
     */
    public Piece getPiece(Square sq) {

        return occupation[sq.ordinal()];
    }

    /**
     * Gets bitboard.
     *
     * @return the bitboard
     */
    public long getBitboard() {
        return bbSide[0] | bbSide[1];
    }

    /**
     * Gets bitboard.
     *
     * @param piece the piece
     * @return the bitboard of a given piece
     */
    public long getBitboard(Piece piece) {
        return bitboard[piece.ordinal()];
    }

    /**
     * Gets bitboard.
     *
     * @param side the side
     * @return the bitboard of a given side
     */
    public long getBitboard(Side side) {
        return bbSide[side.ordinal()];
    }

    /**
     * Get bb side long [ ].
     *
     * @return the bbSide
     */
    public long[] getBbSide() {
        return bbSide;
    }

    /**
     * Get the square(s) location of the given piece
     *
     * @param piece the piece
     * @return piece location
     */
    public List<Square> getPieceLocation(Piece piece) {
        if (getBitboard(piece) != 0L) {
            return Bitboard.bbToSquareList(getBitboard(piece));
        }
        return Collections.emptyList();

    }

    /**
     * Get the square first location of the given piece
     *
     * @param piece the piece
     * @return piece location
     */
    public Square getFistPieceLocation(Piece piece) {
        if (getBitboard(piece) != 0L) {
            return Square.squareAt(Bitboard.bitScanForward(getBitboard(piece)));
        }
        return Square.NONE;

    }

    /**
     * Gets side to move.
     *
     * @return the sideToMove
     */
    public Side getSideToMove() {
        return sideToMove;
    }

    /**
     * Sets side to move.
     *
     * @param sideToMove the sideToMove to set
     */
    public void setSideToMove(Side sideToMove) {
        this.sideToMove = sideToMove;
    }

    /**
     * Gets en passant target.
     *
     * @return the enPassantTarget
     */
    public Square getEnPassantTarget() {
        return enPassantTarget;
    }

    /**
     * Sets en passant target.
     *
     * @param enPassant the enPassantTarget to set
     */
    public void setEnPassantTarget(Square enPassant) {
        this.enPassantTarget = enPassant;
    }

    /**
     * Gets en passant.
     *
     * @return the enPassant
     */
    public Square getEnPassant() {
        return enPassant;
    }

    /**
     * Sets en passant.
     *
     * @param enPassant the enPassant to set
     */
    public void setEnPassant(Square enPassant) {
        this.enPassant = enPassant;
    }

    /**
     * Gets move counter.
     *
     * @return the moveCounter
     */
    public Integer getMoveCounter() {
        return moveCounter;
    }

    /**
     * Sets move counter.
     *
     * @param moveCounter the moveCounter to set
     */
    public void setMoveCounter(Integer moveCounter) {
        this.moveCounter = moveCounter;
    }

    /**
     * Gets half move counter.
     *
     * @return the halfMoveCounter
     */
    public Integer getHalfMoveCounter() {
        return halfMoveCounter;
    }

    /**
     * Sets half move counter.
     *
     * @param halfMoveCounter the halfMoveCounter to set
     */
    public void setHalfMoveCounter(Integer halfMoveCounter) {
        this.halfMoveCounter = halfMoveCounter;
    }

    /**
     * Gets castle right.
     *
     * @param side the side
     * @return the castleRight
     */
    public CastleRight getCastleRight(Side side) {
        return castleRight.get(side);
    }

    /**
     * Gets castle right.
     *
     * @return the castleRight
     */
    public EnumMap<Side, CastleRight> getCastleRight() {
        return castleRight;
    }

    /**
     * Gets context.
     *
     * @return the context
     */
    public GameContext getContext() {
        return context;
    }

    /**
     * Sets context.
     *
     * @param context the context to set
     */
    public void setContext(GameContext context) {
        this.context = context;
    }

    /**
     * Gets backup.
     *
     * @return the backup
     */
    public LinkedList<MoveBackup> getBackup() {
        return backup;
    }

    /**
     * Clear the entire board
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
     * add a piece into a given square
     *
     * @param piece the piece
     * @param sq    the sq
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
     * remove a piece from a given square
     *
     * @param piece the piece
     * @param sq    the sq
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
     * Load an specific chess position using FEN notation
     * ex.: rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1
     *
     * @param fen the fen
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
        //call listeners
        if (isEnableEvents() &&
                eventListener.get(BoardEventType.ON_LOAD).size() > 0) {
            for (BoardEventListener evl :
                    eventListener.get(BoardEventType.ON_LOAD)) {
                evl.onEvent(Board.this);
            }
        }
    }

    /**
     * Generates the current board FEN representation
     *
     * @return board fen
     */
    public String getFen() {
        return getFen(true);
    }

    /**
     * Generates the current board FEN representation
     *
     * @param includeCounters if true include halfMove and fullMove counters
     * @return board fen
     */
    public String getFen(boolean includeCounters) {
        return getFen(includeCounters, false);
    }

    /**
     * Generates the current board FEN representation
     *
     * @param includeCounters if true include halfMove and fullMove counters
     * @param onlyOutputEnPassantIfCapturable if true, only output the en passant 
     *   square if the pawn that just moved is able to be captured. If false, always
     *   output the en passant square if a pawn just moved 2 squares.
     * @return board fen
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
     * Board to array piece [ ].
     *
     * @return the piece [ ]
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

    public BoardEventType getType() {
        return BoardEventType.ON_LOAD;
    }

    /**
     * Gets event listener.
     *
     * @return the event listener
     */
    public EnumMap<BoardEventType, List<BoardEventListener>> getEventListener() {
        return eventListener;
    }

    /**
     * Adds a Board Event Listener
     *
     * @param eventType the event type
     * @param listener  the listener
     * @return Board board
     */
    public Board addEventListener(BoardEventType eventType, BoardEventListener listener) {
        getEventListener().get(eventType).add(listener);
        return this;
    }

    /**
     * Removes a Board Event Listener
     *
     * @param eventType the event type
     * @param listener  the listener
     * @return Board board
     */
    public Board removeEventListener(BoardEventType eventType, BoardEventListener listener) {
        if (getEventListener() != null && getEventListener().get(eventType) != null) {
            getEventListener().get(eventType).remove(listener);
        }
        return this;
    }

    /**
     * Returns if the the bitboard with pieces which can attack the given square
     *
     * @param square the square
     * @param side   the side
     * @return true if the square is attacked
     */
    public long squareAttackedBy(Square square, Side side) {
        return squareAttackedBy(square, side, getBitboard());
    }

    /**
     * Returns if the the bitboard with pieces which can attack the given square
     *
     * @param square the square
     * @param side   the side
     * @param occ    occupied squares
     * @return true if the square is attacked
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
     * Square attacked by a given piece type and side
     *
     * @param square the square
     * @param side   the side
     * @param type   the type
     * @return long
     */
    public long squareAttackedByPieceType(Square square,
                                          Side side, PieceType type) {
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
     * Get king Square
     *
     * @param side the side
     * @return king square
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
     * Is King Attacked
     *
     * @return boolean
     */
    public boolean isKingAttacked() {
        return squareAttackedBy(getKingSquare(getSideToMove()), getSideToMove().flip()) != 0;
    }

    /**
     * set of squares attacked by
     *
     * @param squares the squares
     * @param side    the side
     * @return boolean
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
     * Verify if the move to be played leaves the resulting board in a legal position
     *
     * @param move           the move
     * @param fullValidation performs a full validation on the move, not only if it leaves own king on check, but also if
     *                       castling is legal, based on attacked pieces and squares, if board is in a consistent state:
     *                       e.g.: Occupancy of source square by a piece that belongs to playing side, etc.
     * @return the boolean
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
     * Is attacked by boolean.
     *
     * @param move the move
     * @return the boolean
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
     * Gets history.
     *
     * @return the history
     */
    public LinkedList<Long> getHistory() {
        return history;
    }

    /**
     * Is side mated
     *
     * @return boolean
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
     * verify draw by 3 fold rep, insufficient material, 50th move rule and stale mate
     *
     * @return the boolean
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
     * Verify threefold repetition
     *
     * @param count the number of repetitions
     * @return boolean
     */
    public boolean isRepetition(int count) {

        final int i = Math.min(getHistory().size() - 1, getHalfMoveCounter());
        if (getHistory().size() >= 4) {
            long lastKey = getHistory().get(getHistory().size() - 1);
            int rep = 0;
            for (int x = 4; x <= i; x += 2) {
                final long k = getHistory().get(getHistory().size() - x - 1);
                if (k == lastKey && ++rep >= count - 1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Verify threefold repetition
     *
     * @return boolean
     */
    public boolean isRepetition() {

        return isRepetition(3);
    }

    /**
     * Verify if there is enough material left in the board
     *
     * @return boolean
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
     * Is stale mate
     *
     * @return boolean
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
     * Is enable events boolean.
     *
     * @return the enableEvents
     */
    public boolean isEnableEvents() {
        return enableEvents;
    }

    /**
     * Sets enable events.
     *
     * @param enableEvents the enableEvents to set
     */
    public void setEnableEvents(boolean enableEvents) {
        this.enableEvents = enableEvents;
    }

    /**
     * Get the unique position ID for the board state,
     * which is the actual FEN representation of the board without counters
     * <p>
     * This is a reliable way of identifying a unique position, although much more slower
     * than using the {@code Board#hashcode()}, or {@code Board#getZobristKey()}
     *
     * @return position id
     */
    public String getPositionId() {
        String[] parts = this.getFen(false).split(" ");
        return parts[0] + " " + parts[1] + " " + parts[2] +
                (this.getEnPassantTarget() != Square.NONE ? parts[3] : "-");
    }

    /**
     * The legal moves for the current board
     *
     * @return list of only legal moves
     */
    public List<Move> legalMoves() {

        return MoveGenerator.generateLegalMoves(this);
    }

    /**
     * The pseudo-legal moves for the current board
     *
     * @return list of pseud-legal moves
     */
    public List<Move> pseudoLegalMoves() {

        return MoveGenerator.generatePseudoLegalMoves(this);
    }

    /**
     * The pseudo-legal captures for the current board
     *
     * @return list of pseud-legal captures
     */
    public List<Move> pseudoLegalCaptures() {

        return MoveGenerator.generatePseudoLegalCaptures(this);
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#equals(Object)
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
     * Strict equals is equivalent to the {@code Board#equals()} function plus it also compares if history
     * on both boards are the same
     *
     * @param obj board object
     * @return if board compared is strictly equal including history
     */
    public boolean strictEquals(Object obj) {
        if (obj instanceof Board) {
            Board board = (Board) obj;
            return equals(board) && board.getHistory().equals(this.getHistory());
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (int) incrementalHashKey;
    }

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
     * Returns a string representation of the board with the 1st rank at the bottom,
     * similarly to the {@link #toString()} method but without the info on whose turn it is.
     *
     * @return A string representation of the board from the White player's point of view.
     * @since 1.4.0
     */
    public String toStringFromWhiteViewPoint() {
        return toStringFromViewPoint(Side.WHITE);
    }

    /**
     * Returns a string representation of the board with the 8th rank at the bottom
     *
     * @return A string representation of the board from the Black player's point of view.
     * @since 1.4.0
     */
    public String toStringFromBlackViewPoint() {
        return toStringFromViewPoint(Side.BLACK);
    }

    /**
     * Returns a string representation of the board from the given player's point of view.
     *
     * @param side The side whose home rank should be at the bottom of the resulting representation.
     * @return A string representation of the board from the given player's point of view.
     * @since 1.4.0
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

    @Override
    public String toString() {
        return toStringFromWhiteViewPoint() + "Side: " + getSideToMove();
    }

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

    public long getIncrementalHashKey() {
        return incrementalHashKey;
    }

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

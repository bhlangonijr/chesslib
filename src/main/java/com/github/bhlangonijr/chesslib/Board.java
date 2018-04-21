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

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Chessboard data structure
 */
public class Board implements Cloneable, BoardEvent {

    private final LinkedList<MoveBackup> backup;
    private final EnumMap<BoardEventType, List<BoardEventListener>> eventListener;
    private final long bitboard[];
    private final long bbSide[];
    private final EnumMap<Side, CastleRight> castleRight;
    private final LinkedList<Integer> history = new LinkedList<Integer>();
    private Side sideToMove;
    private Square enPassantTarget;
    private Square enPassant;
    private Integer moveCounter;
    private Integer halfMoveCounter;
    private GameContext context;
    private boolean enableEvents;
    private boolean updateHistory;

    public Board() {
        this(new GameContext(), false);
    }

    public Board(GameContext gameContext, boolean updateHistory) {
        bitboard = new long[Piece.values().length];
        bbSide = new long[Side.values().length];
        castleRight = new EnumMap<Side, CastleRight>(Side.class);
        backup = new LinkedList<MoveBackup>();
        context = gameContext;
        eventListener = new EnumMap<BoardEventType, List<BoardEventListener>>(BoardEventType.class);
        this.updateHistory = updateHistory;
        setSideToMove(Side.WHITE);
        setEnPassantTarget(Square.NONE);
        setEnPassant(Square.NONE);
        setMoveCounter(0);
        setHalfMoveCounter(0);
        for (BoardEventType evt : BoardEventType.values()) {
            eventListener.put(evt, new CopyOnWriteArrayList<BoardEventListener>());
        }
        loadFromFen(gameContext.getStartFEN());
        setEnableEvents(true);
    }

    private static Square findEnPassantTarget(Square sq, Side side) {
        Square ep = Square.NONE;
        if (!Square.NONE.equals(sq)) {
            ep = Side.WHITE.equals(side) ?
                    Square.encode(Rank.RANK_4, sq.getFile()) :
                    Square.encode(Rank.RANK_5, sq.getFile());
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

    public static boolean isPromoRank(Side side, Move move) {
        if (side.equals(Side.WHITE) &&
                move.getTo().getRank().equals(Rank.RANK_8)) {
            return true;
        } else if (side.equals(Side.BLACK) &&
                move.getTo().getRank().equals(Rank.RANK_1)) {
            return true;
        }

        return false;
    }

    /**
     * Execute the move in the board
     *
     * @param move
     */
    public boolean doMove(final Move move) {
        return doMove(move, false);
    }

    /**
     * Execute the move on the board
     *
     * @param move
     * @param fullValidation
     */
    public boolean doMove(final Move move, boolean fullValidation) {

        if (!isMoveLegal(move, fullValidation)) {
            return false;
        }

        Piece movingPiece = getPiece(move.getFrom());
        Side side = getSideToMove();

        MoveBackup backupMove = new MoveBackup(this, move);
        final boolean isCastle = context.isCastleMove(move);

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
            getCastleRight().put(side, CastleRight.NONE);
        } else if (PieceType.ROOK.equals(movingPiece.getPieceType())
                && !CastleRight.NONE.equals(getCastleRight(side))) {
            final Move oo = context.getRookoo(side);
            final Move ooo = context.getRookooo(side);

            if (move.getFrom().equals(oo.getFrom())) {
                if (CastleRight.KING_AND_QUEEN_SIDE.equals(getCastleRight(side))) {
                    getCastleRight().put(side, CastleRight.QUEEN_SIDE);
                } else {
                    getCastleRight().put(side, CastleRight.NONE);
                }
            } else if (move.getFrom().equals(ooo.getFrom())) {
                if (CastleRight.KING_AND_QUEEN_SIDE.equals(getCastleRight(side))) {
                    getCastleRight().put(side, CastleRight.KING_SIDE);
                } else {
                    getCastleRight().put(side, CastleRight.NONE);
                }
            }
        }

        Piece capturedPiece = movePiece(move, backupMove);

        if (PieceType.ROOK.equals(capturedPiece.getPieceType())) {
            final Move oo = context.getRookoo(side.flip());
            final Move ooo = context.getRookooo(side.flip());
            if (move.getTo().equals(oo.getFrom())) {
                if (CastleRight.KING_AND_QUEEN_SIDE.equals(getCastleRight(side.flip()))) {
                    getCastleRight().put(side.flip(), CastleRight.QUEEN_SIDE);
                } else {
                    getCastleRight().put(side.flip(), CastleRight.NONE);
                }
            } else if (move.getTo().equals(ooo.getFrom())) {
                if (CastleRight.KING_AND_QUEEN_SIDE.equals(getCastleRight(side.flip()))) {
                    getCastleRight().put(side.flip(), CastleRight.KING_SIDE);
                } else {
                    getCastleRight().put(side.flip(), CastleRight.NONE);
                }
            }
        }

        if (Piece.NONE.equals(capturedPiece)) {
            setHalfMoveCounter(getHalfMoveCounter() + 1);
        } else {
            setHalfMoveCounter(0);
        }

        setEnPassantTarget(Square.NONE);
        setEnPassant(Square.NONE);

        if (PieceType.PAWN.equals(movingPiece.getPieceType())) {
            if (Math.abs(move.getTo().getRank().ordinal() -
                    move.getFrom().getRank().ordinal()) == 2) {
                Piece otherPawn = Side.WHITE.equals(side) ?
                        Piece.BLACK_PAWN : Piece.WHITE_PAWN;
                if (hasPiece(otherPawn, move.getTo().getSideSquares())) {
                    setEnPassantTarget(move.getTo());
                }
                setEnPassant(findEnPassant(move.getTo(), side));
            }
            setHalfMoveCounter(0);
        }

        setSideToMove(side.flip());
        if (updateHistory) {
            getHistory().addLast(this.hashCode());
        }
        setMoveCounter(getMoveCounter() + 1);
        backup.add(backupMove);
        //call listeners
        if (isEnableEvents() &&
                eventListener.get(BoardEventType.ON_MOVE).size() > 0) {
            for (BoardEventListener evl :
                    eventListener.get(BoardEventType.ON_MOVE)) {
                evl.onEvent(move);
            }
        }
        return true;
    }

    /**
     * Undo the last move executed on the board
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

    /*
     * Move a piece
     * @param move
     * @return
     */
    protected Piece movePiece(Move move, MoveBackup backup) {
        return movePiece(move.getFrom(), move.getTo(), move.getPromotion(), backup);
    }

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
     * @param piece
     * @param location
     * @return
     */
    public boolean hasPiece(Piece piece, Square location[]) {
        for (Square sq : location) {
            if (piece.equals(getPiece(sq))) {
                return true;
            }
        }
        return false;
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

        for (int i = 0; i < bitboard.length; i++) {
            bitboard[i] = 0L;
        }
        for (int i = 0; i < bbSide.length; i++) {
            bbSide[i] = 0L;
        }
        backup.clear();
    }

    /**
     * add a piece into a given square
     *
     * @param piece
     * @param sq
     */
    public void setPiece(Piece piece, Square sq) {
        bitboard[piece.ordinal()] |= sq.getBitboard();
        bbSide[piece.getPieceSide().ordinal()] |= sq.getBitboard();
    }

    /**
     * remove a piece from a given square
     *
     * @param piece
     * @param sq
     */
    public void unsetPiece(Piece piece, Square sq) {
        bitboard[piece.ordinal()] ^= sq.getBitboard();
        bbSide[piece.getPieceSide().ordinal()] ^= sq.getBitboard();
    }

    /**
     * Load an specific chess position using FEN notation
     * ex.: rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1
     *
     * @param fen
     */
    public void loadFromFen(String fen) {
        clear();
        String squares = fen.substring(0, fen.indexOf(' '));
        String state = fen.substring(fen.indexOf(' ') + 1);

        String ranks[] = squares.split("/");
        int file;
        int rank = 7;
        for (String r : ranks) {
            file = 0;
            for (int i = 0; i < r.length(); i++) {
                char c = r.charAt(i);
                if (Character.isDigit(c)) {
                    file += Integer.parseInt(c + "");
                } else {
                    Square sq = Square.encode(Rank.values()[rank], File.values()[file]);
                    setPiece(Constants.getPieceByNotation(c + ""), sq);
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

        String flags[] = state.split(" ");

        if (flags.length >= 3) {
            String s = flags[2].toUpperCase().trim();
            if (!s.equals("-")) {
                Square ep = Square.valueOf(s);
                setEnPassant(ep);
                setEnPassantTarget(findEnPassantTarget(ep, sideToMove));
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

        StringBuffer fen = new StringBuffer();
        int count = 0;
        int rankCounter = 1;
        int sqCount = 0;
        for (int i = 7; i >= 0; i--) {
            Rank r = Rank.values()[i];
            for (int n = 0; n <= 7; n++) {
                File f = File.values()[n];
                if (!File.NONE.equals(f) && !Rank.NONE.equals(r)) {
                    Square sq = Square.encode(r, f);
                    Piece piece = getPiece(sq);
                    if (!Piece.NONE.equals(piece)) {
                        if (count > 0) {
                            fen.append(count);
                        }
                        fen.append(Constants.getPieceNotation(piece));
                        count = 0;
                    } else {
                        count++;
                    }
                    if ((sqCount + 1) % 8 == 0) {
                        if (count > 0) {
                            fen.append(count);
                            count = 0;
                        }
                        if (rankCounter < 8) {
                            fen.append("/");
                        }
                        rankCounter++;
                    }
                    sqCount++;
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

        if (Square.NONE.equals(getEnPassant())) {
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
     * Get the piece on the given square
     *
     * @param sq
     * @return
     */
    public Piece getPiece(Square sq) {
        for (int i = 0; i < bitboard.length - 1; i++) {
            if ((sq.getBitboard() & bitboard[i]) != 0L) {
                return Piece.values()[i];
            }
        }
        return Piece.NONE;
    }

    /**
     * @return the bitboard
     */
    public long getBitboard() {
        return bbSide[0] | bbSide[1];
    }

    /**
     * @return the bitboard of a given piece
     */
    public long getBitboard(Piece piece) {
        return bitboard[piece.ordinal()];
    }

    /**
     * @param side
     * @return the bitboard of a given side
     */
    public long getBitboard(Side side) {
        return bbSide[side.ordinal()];
    }

    /**
     * @return the bbSide
     */
    public long[] getBbSide() {
        return bbSide;
    }

    /**
     * Get the square(s) location of the given piece
     *
     * @param piece
     * @return
     */
    public List<Square> getPieceLocation(Piece piece) {
        if (getBitboard(piece) != 0L) {
            return Bitboard.bbToSquareList(getBitboard(piece));
        }
        return Collections.emptyList();

    }

    /**
     * @return the sideToMove
     */
    public Side getSideToMove() {
        return sideToMove;
    }

    /**
     * @param sideToMove the sideToMove to set
     */
    public void setSideToMove(Side sideToMove) {
        this.sideToMove = sideToMove;
    }

    /**
     * @return the enPassantTarget
     */
    public Square getEnPassantTarget() {
        return enPassantTarget;
    }

    /**
     * @param enPassant the enPassantTarget to set
     */
    public void setEnPassantTarget(Square enPassant) {
        this.enPassantTarget = enPassant;
    }

    /**
     * @return the enPassant
     */
    public Square getEnPassant() {
        return enPassant;
    }

    /**
     * @param enPassant the enPassant to set
     */
    public void setEnPassant(Square enPassant) {
        this.enPassant = enPassant;
    }

    /**
     * @return the moveCounter
     */
    public Integer getMoveCounter() {
        return moveCounter;
    }

    /**
     * @param moveCounter the moveCounter to set
     */
    public void setMoveCounter(Integer moveCounter) {
        this.moveCounter = moveCounter;
    }

    /**
     * @return the halfMoveCounter
     */
    public Integer getHalfMoveCounter() {
        return halfMoveCounter;
    }

    /**
     * @param halfMoveCounter the halfMoveCounter to set
     */
    public void setHalfMoveCounter(Integer halfMoveCounter) {
        this.halfMoveCounter = halfMoveCounter;
    }

    /**
     * @return the castleRight
     */
    public CastleRight getCastleRight(Side side) {
        return castleRight.get(side);
    }

    /**
     * @return the castleRight
     */
    public EnumMap<Side, CastleRight> getCastleRight() {
        return castleRight;
    }

    /**
     * @return the context
     */
    public GameContext getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(GameContext context) {
        this.context = context;
    }

    public LinkedList<MoveBackup> getBackup() {
        return backup;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 7; i >= 0; i--) {
            Rank r = Rank.values()[i];
            for (int n = 0; n <= 7; n++) {
                File f = File.values()[n];
                if (!File.NONE.equals(f) && !Rank.NONE.equals(r)) {
                    Square sq = Square.encode(r, f);
                    Piece piece = getPiece(sq);
                    if (Piece.NONE.equals(piece)) {
                        sb.append(" ");
                    } else {
                        sb.append(Constants.getPieceNotation(piece));
                    }
                }
            }
            sb.append("\n");
        }
        sb.append("Side: " + getSideToMove());

        return sb.toString();
    }

    public Piece[] boardToArray() {

        final Piece pieces[] = new Piece[65];
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

    public EnumMap<BoardEventType, List<BoardEventListener>> getEventListener() {
        return eventListener;
    }

    /**
     * Adds a Board Event Listener
     *
     * @param listener
     * @return Board
     */
    public Board addEventListener(BoardEventType eventType, BoardEventListener listener) {
        getEventListener().get(eventType).add(listener);
        return this;
    }

    /**
     * Removes a Board Event Listener
     *
     * @param listener
     * @return Board
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
     * @param side
     * @return true if the square is attacked
     */
    public long squareAttackedBy(Square square, Side side) {
        long result;
        long occ = getBitboard();
        result = Bitboard.getPawnAttacks(side.flip(), square) &
                getBitboard(Piece.make(side, PieceType.PAWN));
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
     * Square attacked by a given piece type & side
     *
     * @param square
     * @param side
     * @param type
     * @return
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
     * @param side
     * @return
     */
    public Square getKingSquare(Side side) {
        Square result = Square.NONE;
        List<Square> sq = getPieceLocation(Piece.make(side, PieceType.KING));
        if (sq != null && sq.size() > 0) {
            result = sq.get(0);
        }
        return result;
    }

    /**
     * Is King Attacked
     *
     * @return
     */
    public boolean isKingAttacked() {
        return squareAttackedBy(getKingSquare(getSideToMove()), getSideToMove().flip()) != 0;
    }

    /**
     * set of squares attacked by
     *
     * @param squares
     * @param side
     * @return
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
     * Verify if a move is legal within this board context
     */
    public boolean isMoveLegal(Move move, boolean fullValidation) {

        final Piece fromPiece = getPiece(move.getFrom());
        final Piece toPiece = getPiece(move.getTo());
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
        long allPieces = (getBitboard() ^ moveFrom) | moveTo;

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

        long pawns = (getBitboard(Piece.make(other, PieceType.PAWN))) & ~moveTo;

        if (fromPiece.getPieceType().equals(PieceType.PAWN) &&
                !Square.NONE.equals(getEnPassantTarget()) &&
                toPiece.equals(Piece.NONE)) {
            pawns &= ~getEnPassantTarget().getBitboard();
        }

        if (pawns != 0L &&
                (Bitboard.getPawnAttacks(side, kingSq) & pawns) != 0L) {
            return false;
        }

        return true;
    }

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
     * @return the history
     */
    public LinkedList<Integer> getHistory() {
        return history;
    }

    /**
     * Is side mated
     *
     * @return
     */
    public boolean isMated() {
        try {
            if (isKingAttacked()) {
                final MoveList l = MoveGenerator.generateLegalMoves(this);
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
     * verify draw by 50th move rule, 3 fold rep and insuficient material
     */
    public boolean isDraw() {
        final int i = Math.min(getHistory().size() - 1, getHalfMoveCounter());
        int rept = 0;
        if (getHistory().size() >= 4) {
            final int lastKey = getHistory().get(getHistory().size() - 1);
            for (int x = 2; x <= i; x += 2) {
                final int k = getHistory().get(getHistory().size() - x - 1);
                if (k == lastKey) {
                    rept++;
                    if (rept >= 2) {
                        return true;
                    }
                }
            }
        }
        if (isInsufficientMaterial()) {
            return true;
        }
        if (isStaleMate()) {
            return true;
        }
        return getHalfMoveCounter() >= 100;

    }

    /**
     * Verify if there is enough material left in the board
     *
     * @return
     */
    public boolean isInsufficientMaterial() {
        boolean result = false;
        final long pawns = getBitboard(Piece.WHITE_PAWN) |
                getBitboard(Piece.BLACK_PAWN);
        if (pawns == 0L) {
            if ((getBitboard(Piece.WHITE_QUEEN) +
                    getBitboard(Piece.BLACK_QUEEN) +
                    getBitboard(Piece.WHITE_ROOK) +
                    getBitboard(Piece.BLACK_ROOK)) != 0L) {
                result = false;
            } else {
                long count = Long.bitCount(getBitboard());
                if (count == 4) {
                    if (Long.bitCount(getBitboard(Side.WHITE)) > 1 &&
                            Long.bitCount(getBitboard(Side.BLACK)) > 1) {
                        result = true;
                    } else if (Long.bitCount(getBitboard(Piece.WHITE_KNIGHT)) == 2 ||
                            Long.bitCount(getBitboard(Piece.BLACK_KNIGHT)) == 2) {
                        result = true;
                    }
                } else if (count < 4) {
                    return true;
                }
            }
        }

        return result;
    }

    /**
     * Is stale mate
     *
     * @return
     */
    public boolean isStaleMate() {
        try {
            if (!isKingAttacked()) {
                MoveList l = MoveGenerator.generateLegalMoves(this);
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
     * @return the enableEvents
     */
    public boolean isEnableEvents() {
        return enableEvents;
    }

    /**
     * @param enableEvents the enableEvents to set
     */
    public void setEnableEvents(boolean enableEvents) {
        this.enableEvents = enableEvents;
    }

    /**
     * Get the unique position ID for the board state,
     * which is the actual FEN representation of the board without counters
     *
     * @return
     */
    public String getPositionId() {
        return getFen(false);
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj != null && obj instanceof Board) {

            Board board = (Board) obj;
            result = true;
            for (Square sq : Square.values()) {
                if (!getPiece(sq).equals(board.getPiece(sq))) {
                    result = false;
                    break;
                }
            }
            if (result) {
                result = getSideToMove().equals(board.getSideToMove());
                result = result || getCastleRight(Side.WHITE).equals(board.getCastleRight(Side.WHITE));
                result = result || getCastleRight(Side.BLACK).equals(board.getCastleRight(Side.BLACK));
                result = result || getEnPassant().equals(board.getEnPassant());
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 0;
        for (Square sq : Square.values()) {
            Piece piece = getPiece(sq);
            if (!Piece.NONE.equals(piece) &&
                    !Square.NONE.equals(sq)) {
                hash ^= sq.hashCode() ^ piece.hashCode();
            }
        }
        hash ^= getSideToMove().hashCode();
        hash ^= getCastleRight(Side.WHITE).hashCode();
        hash ^= getCastleRight(Side.BLACK).hashCode();
        if (!Square.NONE.equals(getEnPassant())) {
            hash ^= getEnPassant().hashCode();
        }
        //TODO use increamental zobrist's hashing
        return hash;
    }

    @Override
    public Board clone() {
        Board copy = new Board(getContext(), this.updateHistory);
        copy.loadFromFen(this.getFen());
        return copy;
    }


}

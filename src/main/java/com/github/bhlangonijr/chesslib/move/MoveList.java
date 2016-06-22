/*
 * Copyright 2016 Ben-Hur Carlos Vieira Langoni Junior
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

package com.github.bhlangonijr.chesslib.move;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.util.StringUtil;

import java.util.*;

public class MoveList extends ArrayList<Move> implements List<Move> {

    private static final long serialVersionUID = -6204280556340150806L;
    private static final ThreadLocal<Board> boardHolder = new ThreadLocal<Board>() {
        @Override
        protected Board initialValue() {
            return new Board();
        }
    };

    private static EnumMap<Piece, String> sanNotation =
            new EnumMap<Piece, String>(Piece.class);

    private static EnumMap<Piece, String> fanNotation =
            new EnumMap<Piece, String>(Piece.class);

    private static Map<String, PieceType> sanNotationR =
            new HashMap<String, PieceType>(7);


    static {
        sanNotation.put(Piece.WHITE_PAWN, "");
        sanNotation.put(Piece.BLACK_PAWN, "");
        sanNotation.put(Piece.WHITE_KNIGHT, "N");
        sanNotation.put(Piece.BLACK_KNIGHT, "N");
        sanNotation.put(Piece.WHITE_BISHOP, "B");
        sanNotation.put(Piece.BLACK_BISHOP, "B");
        sanNotation.put(Piece.WHITE_ROOK, "R");
        sanNotation.put(Piece.BLACK_ROOK, "R");
        sanNotation.put(Piece.WHITE_QUEEN, "Q");
        sanNotation.put(Piece.BLACK_QUEEN, "Q");
        sanNotation.put(Piece.WHITE_KING, "K");
        sanNotation.put(Piece.BLACK_KING, "K");
        sanNotation.put(Piece.NONE, "NONE");

        fanNotation.put(Piece.WHITE_PAWN, "♙");
        fanNotation.put(Piece.BLACK_PAWN, "♟");
        fanNotation.put(Piece.WHITE_KNIGHT, "♘");
        fanNotation.put(Piece.BLACK_KNIGHT, "♞");
        fanNotation.put(Piece.WHITE_BISHOP, "♗");
        fanNotation.put(Piece.BLACK_BISHOP, "♝");
        fanNotation.put(Piece.WHITE_ROOK, "♖");
        fanNotation.put(Piece.BLACK_ROOK, "♜");
        fanNotation.put(Piece.WHITE_QUEEN, "♕");
        fanNotation.put(Piece.BLACK_QUEEN, "♛");
        fanNotation.put(Piece.WHITE_KING, "♔");
        fanNotation.put(Piece.BLACK_KING, "♚");
        fanNotation.put(Piece.NONE, "NONE");

        sanNotationR.put("", PieceType.PAWN);
        sanNotationR.put("N", PieceType.KNIGHT);
        sanNotationR.put("B", PieceType.BISHOP);
        sanNotationR.put("R", PieceType.ROOK);
        sanNotationR.put("Q", PieceType.QUEEN);
        sanNotationR.put("K", PieceType.KING);
        sanNotationR.put("NONE", PieceType.NONE);
    }

    private final String startFEN;
    private boolean dirty = true;

    private String sanArray[];
    private String fanArray[];
    private int parent;
    private int index;

    public MoveList() {
        this(Constants.startStandardFENPosition);
    }

    /**
     * Initialize the move list with the initial FEN
     *
     * @param initialFEN
     */
    public MoveList(String initialFEN) {
        this.startFEN = initialFEN;
    }

    /**
     * Intialize a MoveList based on another
     *
     * @param halfMoves
     */
    public MoveList(MoveList halfMoves) {
        this(halfMoves.getStartFEN());
        super.addAll(halfMoves);
    }

    protected static Board getBoard() {
        return boardHolder.get();
    }

    // encode the move to SAN move and update thread local board
    protected static String encodeToSAN(final Board board, Move move) throws MoveConversionException {
        return encode(board, move, sanNotation);
    }

    // encode the move to SAN move and update thread local board
    protected static String encodeToFAN(final Board board, Move move) throws MoveConversionException {
        return encode(board, move, fanNotation);
    }

    // encode the move to SAN/FAN move and update thread local board
    protected static String encode(final Board board, Move move, EnumMap<Piece, String> notation)
            throws MoveConversionException {
        StringBuilder san = new StringBuilder();

        Piece piece = board.getPiece(move.getFrom());
        if (piece.getPieceType().equals(PieceType.KING)) {
            int delta = move.getTo().getFile().ordinal() -
                    move.getFrom().getFile().ordinal();
            if (Math.abs(delta) >= 2) { // is castle
                if (!board.doMove(move, true)) {
                    throw new MoveConversionException("Invalid move [" +
                            move.toString() + "] for current setup: " + board.getFEN());
                }
                if (delta > 0) {
                    return "O-O";
                } else {
                    return "O-O-O";
                }
            }
        }
        boolean pawnMove = piece.getPieceType().equals(PieceType.PAWN) &&
                move.getFrom().getFile().equals(move.getTo().getFile());
        boolean ambResolved = false;
        san.append(notation.get(piece));
        if (!pawnMove) {
            //resolving ambiguous move
            long amb = board.squareAttackedByPieceType(move.getTo(),
                    board.getSideToMove(), piece.getPieceType());
            amb &= ~move.getFrom().getBitboard();
            if (amb != 0L) {
                if ((Bitboard.getFilebb(move.getFrom()) & amb) == 0L) {
                    san.append(move.getFrom().getFile().getNotation().toLowerCase());
                } else if ((Bitboard.getRankbb(move.getFrom()) & amb) == 0L) {
                    san.append(move.getFrom().getRank().getNotation().toLowerCase());
                } else {
                    san.append(move.getFrom().toString().toLowerCase());
                }
                ambResolved = true;
            }
        }

        if (!board.doMove(move, true)) {
            throw new MoveConversionException("Invalid move [" +
                    move.toString() + "] for current setup: " + board.getFEN());
        }

        Piece captured = board.getBackup().peekLast().getCapturedPiece();
        boolean isCapture = !captured.equals(Piece.NONE);
        if (isCapture) {
            if (!ambResolved &&
                    piece.getPieceType().equals(PieceType.PAWN)) {
                san.append(move.getFrom().getFile().getNotation().toLowerCase());
            }
            san.append("x");
        }
        san.append(move.getTo().toString().toLowerCase());
        if (!move.getPromotion().equals(Piece.NONE)) {
            san.append("=" + notation.get(move.getPromotion()));
        }
        if (board.isKingAttacked()) {
            if (board.isMated()) {
                san.append("#");
            } else {
                san.append("+");
            }
        }
        return san.toString();
    }

    private static long findLegalSquares(Board board, Square to, Piece promotion, long pieces) {
        long result = 0L;

        if (pieces != 0L) {
            for (Square sqSource : Bitboard.bbToSquareList(pieces)) {
                Move move = new Move(sqSource, to, promotion);
                if (board.isMoveLegal(move, true)) {
                    result |= sqSource.getBitboard();
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Create a MoveList with a given startposition
     *
     * @param startMoves
     * @param finalIndex
     * @return
     * @throws MoveConversionException
     */
    public static MoveList createMoveListFrom(MoveList startMoves, int finalIndex) throws MoveConversionException {
        String fen = null;
        final Board b = getBoard();
        if (!b.getFEN().equals(startMoves.getStartFEN())) {
            b.loadFromFEN(startMoves.getStartFEN());
        }
        int i = 0;
        for (Move move : startMoves) {
            i++;
            if (!b.doMove(move, false)) {
                throw new MoveConversionException("Couldn't parse SAN to MoveList: Illegal move: " +
                        move + " [" + move.toString() + "] on " + b.getFEN());
            }
            if (i >= finalIndex) {
                fen = b.getFEN();
                break;
            }
        }
        if (fen == null) {
            fen = b.getFEN();
        }
        MoveList moves = new MoveList(fen);
        return moves;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /* (non-Javadoc)
     * @see java.util.ArrayList#add(int, java.lang.Object)
     */
    @Override
    public void add(int arg0, Move arg1) {
        dirty = true;
        super.add(arg0, arg1);
    }

    /* (non-Javadoc)
     * @see java.util.ArrayList#add(java.lang.Object)
     */
    @Override
    public boolean add(Move arg0) {
        dirty = true;
        return super.add(arg0);
    }

    /* (non-Javadoc)
     * @see java.util.ArrayList#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends Move> arg0) {
        dirty = true;
        return super.addAll(arg0);
    }

    /* (non-Javadoc)
     * @see java.util.ArrayList#addAll(int, java.util.Collection)
     */
    @Override
    public boolean addAll(int arg0, Collection<? extends Move> arg1) {
        dirty = true;
        return super.addAll(arg0, arg1);
    }

    /* (non-Javadoc)
     * @see java.util.ArrayList#clear()
     */
    @Override
    public void clear() {
        dirty = true;
        sanArray = null;
        fanArray = null;
        super.clear();
    }

    /**
     * Converts the MoveList into SAN representation
     *
     * @return
     */
    public String toSAN() throws MoveConversionException {
        StringBuilder sb = new StringBuilder();
        for (String sanMove : this.toSANArray()) {
            sb.append(sanMove);
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Converts the MoveList into FAN representation
     *
     * @return
     */
    public String toFAN() throws MoveConversionException {
        StringBuilder sb = new StringBuilder();
        for (String fanMove : this.toFANArray()) {
            sb.append(fanMove);
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Converts the MoveList into SAN Array representation
     *
     * @return
     */
    public String[] toSANArray() throws MoveConversionException {
        if (!dirty && sanArray != null) {
            return sanArray;
        }
        sanArray = new String[this.size()];
        final Board b = getBoard();
        if (!b.getFEN().equals(getStartFEN())) {
            b.loadFromFEN(getStartFEN());
        }
        int i = 0;
        for (Move move : this) {
            String sanMove = encodeToSAN(b, move);
            sanArray[i++] = sanMove;
        }
        dirty = false;
        return sanArray;
    }

    /**
     * Converts the MoveList into FAN Array representation
     *
     * @return
     */
    public String[] toFANArray() throws MoveConversionException {
        if (!dirty && fanArray != null) {
            return fanArray;
        }
        fanArray = new String[this.size()];
        final Board b = getBoard();
        if (!b.getFEN().equals(getStartFEN())) {
            b.loadFromFEN(getStartFEN());
        }
        int i = 0;
        for (Move move : this) {
            String sanMove = encodeToFAN(b, move);
            fanArray[i++] = sanMove;
        }
        dirty = false;
        return fanArray;
    }

    /**
     * @return the startFEN
     */
    public String getStartFEN() {
        return startFEN;
    }

    /**
     * load from long algebraic text
     *
     * @param text
     */
    public synchronized void loadFromText(String text) throws MoveConversionException {
        final Board b = getBoard();
        if (!b.getFEN().equals(getStartFEN())) {
            b.loadFromFEN(getStartFEN());
        }
        try {
            Side side = b.getSideToMove();
            text = StringUtil.normalize(text);
            String m[] = text.split(" ");
            int i = 0;
            for (String strMove : m) {
                Move move = new Move(strMove, side);
                add(i++, move);
                side = side.flip();
            }
        } catch (Exception e) {
            throw new MoveConversionException("Couldn't parse text to MoveList: " + e.getMessage());
        }

    }

    /**
     * Add a move in the SAN format
     *
     * @param san
     * @throws MoveConversionException
     */
    public void addSanMove(String san) throws MoveConversionException {
        addSanMove(san, false, true);
    }

    /**
     * Add a move in the SAN format
     *
     * @param san
     * @throws MoveConversionException
     */
    public void addSanMove(String san, boolean replay, boolean fullValidation) throws MoveConversionException {
        final Board b = getBoard();
        if (replay) {
            if (!b.getFEN().equals(getStartFEN())) {
                b.loadFromFEN(getStartFEN());
            }
            for (Move move : this) {
                if (!b.doMove(move, false)) {
                    throw new MoveConversionException("Couldn't parse SAN to MoveList: Illegal move: " +
                            move + " [" + move.toString() + "] on " + b.getFEN());
                }
            }
        }
        Move move = encodeSANToMove(b, san, b.getSideToMove());
        move.setSan(san);

        if (!b.doMove(move, fullValidation)) {
            throw new MoveConversionException("Couldn't parse SAN to MoveList: Illegal move: " +
                    move + " [" + san + "] on " + b.getFEN());
        }
        add(this.size(), move);
    }

    /**
     * load from SAN text
     *
     * @param text
     */
    public void loadFromSAN(String text) throws MoveConversionException {
        final Board b = getBoard();
        if (!b.getFEN().equals(getStartFEN())) {
            b.loadFromFEN(getStartFEN());
        }
        try {
            text = StringUtil.normalize(text);
            String m[] = text.split(" ");
            for (String strMove : m) {
                if (strMove.startsWith("$")) {
                    continue;
                }
                if (strMove.indexOf("...") > -1) {
                    continue;
                }
                if (strMove.indexOf(".") > -1) {
                    strMove = StringUtil.afterSequence(strMove, ".");
                }
                if (strMove == null || strMove.trim().equals("")) {
                    continue;
                }
                addSanMove(strMove);
            }
        } catch (MoveConversionException e1) {
            throw e1;
        } catch (Exception e2) {
            e2.printStackTrace();
            throw new MoveConversionException("Couldn't parse SAN to MoveList: " + e2.getMessage());
        }
    }

    /*
     * encode san to move
     */
    protected Move encodeSANToMove(Board board, String san, Side side) throws MoveConversionException {

        san = san.replace("+", "");
        san = san.replace("#", "");
        san = san.replace("!", "");
        san = san.replace("?", "");
        san = san.replace("\n", " ");
        String strPromotion = StringUtil.afterSequence(san, "=", 1);
        san = StringUtil.beforeSequence(san, "=");

        if (san.equals("O-O") || san.equals("O-O-O")) { // is castle
            if (san.equals("O-O")) {
                return board.getContext().getoo(side);
            } else {
                return board.getContext().getooo(side);
            }
        }

        if (san.length() == 3 &&
                Character.isUpperCase(san.charAt(2))) {
            strPromotion = san.substring(2, 3);
            san = san.substring(0, 2);
        }

        Square from = Square.NONE;
        Square to = Square.NONE;
        try {
            to = Square.valueOf(StringUtil.lastSequence(san.toUpperCase(), 2));
        } catch (Exception e) {
            throw new MoveConversionException("Coudn't parse destination square[" + san + "]: " +
                    san.toUpperCase());
        }
        Piece promotion = strPromotion.equals("") ? Piece.NONE :
                Constants.getPieceByNotation(side.equals(Side.WHITE) ?
                        strPromotion.toUpperCase() : strPromotion.toLowerCase());

        if (san.length() == 2) { //is pawn move
            long mask = Bitboard.getBbtable(to) - 1L;
            long xfrom = (side.equals(Side.WHITE) ? mask : ~mask) & Bitboard.getFilebb(to) &
                    board.getBitboard(Piece.make(side, PieceType.PAWN));
            int f = side.equals(Side.BLACK) ? Bitboard.bitScanForward(xfrom) :
                    Bitboard.bitScanReverse(xfrom);
            if (f >= 0 && f <= 63) {
                from = Square.squareAt(f);
            }
        } else {

            String strFrom = (san.indexOf("x") >= 0 ?
                    StringUtil.beforeSequence(san, "x") :
                    san.substring(0, san.length() - 2));

            if (strFrom == null ||
                    strFrom.length() == 0 || strFrom.length() > 3) {
                throw new MoveConversionException("Couldn't parse 'from' square " + san + ": Too many/few characters.");
            }

            PieceType fromPiece = PieceType.PAWN;

            if (Character.isUpperCase(strFrom.charAt(0))) {
                fromPiece = sanNotationR.get(strFrom.charAt(0) + "");
            }

            if (strFrom.length() == 3) {
                from = Square.valueOf(strFrom.substring(1, 3).toUpperCase());
            } else {
                String location = "";
                if (strFrom.length() == 2) {
                    if (Character.isUpperCase(strFrom.charAt(0))) {
                        location = strFrom.substring(1, 2);
                    } else {
                        location = strFrom.substring(0, 2);
                        from = Square.valueOf(location.toUpperCase());
                    }
                } else {
                    if (Character.isLowerCase(strFrom.charAt(0))) {
                        location = strFrom;
                    }
                }
                if (location.length() < 2) {
                    //resolving ambiguous from
                    long xfrom = board.squareAttackedByPieceType(to,
                            board.getSideToMove(), fromPiece);
                    if (location.length() > 0) {
                        if (Character.isDigit(location.charAt(0))) {
                            int irank = Integer.parseInt(location);
                            if (!(irank >= 1 && irank <= 8)) {
                                throw new MoveConversionException("Couldn't parse rank: " + location);
                            }
                            Rank rank = Rank.values()[irank - 1];
                            xfrom &= Bitboard.getRankbb(rank);
                        } else {
                            try {
                                File file = File.valueOf("FILE_" + location.toUpperCase());
                                xfrom &= Bitboard.getFilebb(file);
                            } catch (Exception e) {
                                throw new MoveConversionException("Couldn't parse file: " + location);
                            }
                        }
                    }
                    if (xfrom != 0L) {
                        if (!Bitboard.hasOnly1Bit(xfrom)) {
                            xfrom = findLegalSquares(board, to, promotion, xfrom);
                        }
                        int f = Bitboard.bitScanForward(xfrom);
                        if (f >= 0 && f <= 63) {
                            from = Square.squareAt(f);
                        }
                    }
                }
            }

        }
        if (from.equals(Square.NONE)) {
            throw new MoveConversionException("Couldn't parse 'from' square " + san + " to setup: " + board.getFEN());
        }
        return new Move(from, to, promotion);
    }

    /**
     * Get the FEN representation of the movelist applied
     * into a standard board at the start position
     *
     * @param atMoveIndex
     * @return
     */
    public String getFEN(int atMoveIndex) {
        return getFEN(atMoveIndex, true);
    }

    /**
     * Get the FEN representation of the movelist applied
     * into a standard board at the start position
     *
     * @param atMoveIndex
     * @param includeCounters
     * @return
     */
    public String getFEN(int atMoveIndex, boolean includeCounters) {
        final Board b = getBoard();
        if (!b.getFEN().equals(getStartFEN())) {
            b.loadFromFEN(getStartFEN());
        }
        int i = 0;
        for (Move move : this) {
            i++;
            if (!b.doMove(move, false)) {
                throw new IllegalArgumentException("Couldn't parse SAN to MoveList: Illegal move: " +
                        move + " [" + move.toString() + "] on " + b.getFEN(includeCounters));
            }
            if (i >= atMoveIndex) {
                return b.getFEN(includeCounters);
            }
        }
        return null;
    }

    /**
     * Get the FEN representation of the movelist applied
     * into a standard board at the start position
     *
     * @return
     */
    public String getFEN() {
        return getFEN(this.size() - 1);
    }

    /**
     * @return the parent
     */
    public int getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(int parent) {
        this.parent = parent;
    }

    /* (non-Javadoc)
     * @see java.util.AbstractCollection#toString()
     */
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (Move move : this) {
            b.append(move.toString());
            b.append(" ");
        }
        return b.toString().trim();
    }

    /* (non-Javadoc)
     * @see java.util.AbstractList#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + toString().hashCode();
        return result;
    }

    /* (non-Javadoc)
     * @see java.util.AbstractList#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MoveList) {
            MoveList l = (MoveList) obj;
            if (l.size() != this.size()) {
                return false;
            }
            for (int i = 0; i < l.size(); i++) {
                if (!l.get(i).equals(this.get(i))) {
                    return false;
                }
            }
        }
        return false;
    }


}

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

package com.github.bhlangonijr.chesslib.move;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.util.StringUtil;

import java.util.*;

/**
 * The type Move list.
 */
public class MoveList extends LinkedList<Move> implements List<Move> {

    private static final long serialVersionUID = -6204280556340150806L;
    private static final ThreadLocal<Board> boardHolder = new ThreadLocal<Board>() {
        @Override
        protected Board initialValue() {
            return new Board();
        }
    };
    private static final Move nullMove = new Move(Square.NONE, Square.NONE);

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

    /**
     * Instantiates a new Move list.
     */
    public MoveList() {
        this(Constants.startStandardFENPosition);
    }

    /**
     * Initialize the move list with the initial FEN
     *
     * @param initialFEN the initial fen
     */
    public MoveList(String initialFEN) {
        this.startFEN = initialFEN;
    }

    /**
     * Intialize a MoveList based on another
     *
     * @param halfMoves the half moves
     */
    public MoveList(MoveList halfMoves) {
        this(halfMoves.getStartFen());
        super.addAll(halfMoves);
    }

    /**
     * Gets board.
     *
     * @return the board
     */
    protected static Board getBoard() {
        return boardHolder.get();
    }

    /**
     * Encode to san string.
     *
     * @param board the board
     * @param move  the move
     * @return the string
     * @throws MoveConversionException the move conversion exception
     */
// encode the move to SAN move and update thread local board
    protected static String encodeToSan(final Board board, Move move) throws MoveConversionException {
        return encode(board, move, sanNotation);
    }

    /**
     * Encode to fan string.
     *
     * @param board the board
     * @param move  the move
     * @return the string
     * @throws MoveConversionException the move conversion exception
     */
// encode the move to SAN move and update thread local board
    protected static String encodeToFan(final Board board, Move move) throws MoveConversionException {
        return encode(board, move, fanNotation);
    }

    /**
     * Encode string.
     *
     * @param board    the board
     * @param move     the move
     * @param notation the notation
     * @return the string
     * @throws MoveConversionException the move conversion exception
     */
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
                            move.toString() + "] for current setup: " + board.getFen());
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
                    move.toString() + "] for current setup: " + board.getFen());
        }

        Piece captured = board.getBackup().removeLast().getCapturedPiece();
        boolean isCapture = !captured.equals(Piece.NONE);
        if (isCapture) {
            if (!ambResolved &&
                    piece.getPieceType().equals(PieceType.PAWN)) {
                san.append(move.getFrom().getFile().getNotation().toLowerCase());
            }
            san.append("x");
        }
        san.append(move.getTo().toString().toLowerCase());
        if (isCapture) {

        }
        if (!move.getPromotion().equals(Piece.NONE)) {
            san.append("=");
            san.append(notation.get(move.getPromotion()));
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
     * @param startMoves the start moves
     * @param finalIndex the final index
     * @return move list
     * @throws MoveConversionException the move conversion exception
     */
    public static MoveList createMoveListFrom(MoveList startMoves, int finalIndex) throws MoveConversionException {
        String fen = null;
        final Board b = getBoard();
        if (!b.getFen().equals(startMoves.getStartFen())) {
            b.loadFromFen(startMoves.getStartFen());
        }
        int i = 0;
        for (Move move : startMoves) {
            i++;
            if (!b.doMove(move, false)) {
                throw new MoveConversionException("Couldn't parse SAN to MoveList: Illegal move: " +
                        move + " [" + move.toString() + "] on " + b.getFen());
            }
            if (i >= finalIndex) {
                fen = b.getFen();
                break;
            }
        }
        if (fen == null) {
            fen = b.getFen();
        }
        MoveList moves = new MoveList(fen);
        return moves;
    }

    /**
     * Gets index.
     *
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets index.
     *
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
     * @return string
     * @throws MoveConversionException the move conversion exception
     */
    public String toSan() throws MoveConversionException {
        StringBuilder sb = new StringBuilder();
        for (String sanMove : this.toSanArray()) {
            sb.append(sanMove);
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Converts the MoveList into FAN representation
     *
     * @return string
     * @throws MoveConversionException the move conversion exception
     */
    public String toFan() throws MoveConversionException {
        StringBuilder sb = new StringBuilder();
        for (String fanMove : this.toFanArray()) {
            sb.append(fanMove);
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Converts the MoveList into SAN Array representation
     *
     * @return string [ ]
     * @throws MoveConversionException the move conversion exception
     */
    public String[] toSanArray() throws MoveConversionException {
        if (!dirty && sanArray != null) {
            return sanArray;
        }
        sanArray = new String[this.size()];
        final Board b = getBoard();
        if (!b.getFen().equals(getStartFen())) {
            b.loadFromFen(getStartFen());
        }
        int i = 0;
        for (Move move : this) {
            String sanMove = encodeToSan(b, move);
            sanArray[i++] = sanMove;
        }
        dirty = false;
        return sanArray;
    }

    /**
     * Converts the MoveList into FAN Array representation
     *
     * @return string [ ]
     * @throws MoveConversionException the move conversion exception
     */
    public String[] toFanArray() throws MoveConversionException {
        if (!dirty && fanArray != null) {
            return fanArray;
        }
        fanArray = new String[this.size()];
        final Board b = getBoard();
        if (!b.getFen().equals(getStartFen())) {
            b.loadFromFen(getStartFen());
        }
        int i = 0;
        for (Move move : this) {
            String sanMove = encodeToFan(b, move);
            fanArray[i++] = sanMove;
        }
        dirty = false;
        return fanArray;
    }

    /**
     * Gets start fen.
     *
     * @return the startFEN
     */
    public String getStartFen() {
        return startFEN;
    }

    /**
     * load from long algebraic text
     *
     * @param text the text
     * @throws MoveConversionException the move conversion exception
     */
    public synchronized void loadFromText(String text) throws MoveConversionException {
        final Board b = getBoard();
        if (!b.getFen().equals(getStartFen())) {
            b.loadFromFen(getStartFen());
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
     * @param san the san
     * @throws MoveConversionException the move conversion exception
     */
    public void addSanMove(String san) throws MoveConversionException {
        addSanMove(san, false, true);
    }

    /**
     * Add a move in the SAN format
     *
     * @param san            the san
     * @param replay         the replay
     * @param fullValidation the full validation
     * @throws MoveConversionException the move conversion exception
     */
    public void addSanMove(String san, boolean replay, boolean fullValidation) throws MoveConversionException {
        final Board b = getBoard();
        if (replay) {
            if (!b.getFen().equals(getStartFen())) {
                b.loadFromFen(getStartFen());
            }
            for (Move move : this) {
                if (!b.doMove(move, false)) {
                    throw new MoveConversionException("Couldn't parse SAN to MoveList: Illegal move: " +
                            move + " [" + move.toString() + "] on " + b.getFen());
                }
            }
        }
        Move move = encodeSanToMove(b, san, b.getSideToMove());
        if (move == nullMove) {
            return;
        }
        move.setSan(san);
        if (!b.doMove(move, fullValidation)) {
            throw new MoveConversionException("Couldn't parse SAN to MoveList: Illegal move: " +
                    move + " [" + san + "] on " + b.getFen());
        }
        add(this.size(), move);
    }

    /**
     * load from SAN text
     *
     * @param text the text
     * @throws MoveConversionException the move conversion exception
     */
    public void loadFromSan(String text) throws MoveConversionException {
        final Board b = getBoard();
        if (!b.getFen().equals(getStartFen())) {
            b.loadFromFen(getStartFen());
        }
        try {
            text = StringUtil.normalize(text);
            String m[] = text.split(" ");
            for (String strMove : m) {
                if (strMove.startsWith("$")) {
                    continue;
                }
                if (strMove.contains("...")) {
                    continue;
                }
                if (strMove.contains(".")) {
                    strMove = StringUtil.afterSequence(strMove, ".");
                }
                if (strMove.trim().equals("")) {
                    continue;
                }
                addSanMove(strMove);
            }
        } catch (MoveConversionException e1) {
            throw e1;
        } catch (Exception e2) {
            throw new MoveConversionException("Couldn't parse SAN to MoveList: " + e2.getMessage());
        }
    }

    /**
     * Encode san to move move.
     *
     * @param board the board
     * @param san   the san
     * @param side  the side
     * @return the move
     * @throws MoveConversionException the move conversion exception
     */
    /*
     * encode san to move
     */
    protected Move encodeSanToMove(Board board, String san, Side side) throws MoveConversionException {

        if (san.equalsIgnoreCase("Z0")) {
            return nullMove;
        }
        san = san.replace("+", "");
        san = san.replace("#", "");
        san = san.replace("!", "");
        san = san.replace("?", "");
        san = san.replace("ep", "");
        san = san.replace("\n", " ");

        String strPromotion = StringUtil.afterSequence(san, "=", 1);
        san = StringUtil.beforeSequence(san, "=");

        char lastChar = san.charAt(san.length() - 1);
        //FIX missing equal sign for pawn promotions
        if (Character.isLetter(lastChar) && Character.toUpperCase(lastChar) != 'O') {
            san = san.substring(0, san.length() - 1);
            strPromotion = lastChar + "";
        }

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
        Square to;
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

            String strFrom = (san.contains("x") ?
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
            throw new MoveConversionException("Couldn't parse 'from' square " + san + " to setup: " + board.getFen());
        }
        return new Move(from, to, promotion);
    }

    /**
     * Get the FEN representation of the movelist applied
     * into a standard board at the start position
     *
     * @param atMoveIndex the at move index
     * @return fen
     */
    public String getFen(int atMoveIndex) {
        return getFen(atMoveIndex, true);
    }

    /**
     * Get the FEN representation of the movelist applied
     * into a standard board at the start position
     *
     * @param atMoveIndex     the at move index
     * @param includeCounters the include counters
     * @return fen
     */
    public String getFen(int atMoveIndex, boolean includeCounters) {
        final Board b = getBoard();
        if (!b.getFen().equals(getStartFen())) {
            b.loadFromFen(getStartFen());
        }
        int i = 0;
        for (Move move : this) {
            i++;
            if (!b.doMove(move, false)) {
                throw new IllegalArgumentException("Couldn't parse SAN to MoveList: Illegal move: " +
                        move + " [" + move.toString() + "] on " + b.getFen(includeCounters));
            }
            if (i >= atMoveIndex) {
                return b.getFen(includeCounters);
            }
        }
        return null;
    }

    /**
     * Get the FEN representation of the movelist applied
     * into a standard board at the start position
     *
     * @return fen
     */
    public String getFen() {
        return getFen(this.size());
    }

    /**
     * Gets parent.
     *
     * @return the parent
     */
    public int getParent() {
        return parent;
    }

    /**
     * Sets parent.
     *
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

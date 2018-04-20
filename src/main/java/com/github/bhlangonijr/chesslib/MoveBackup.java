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

package com.github.bhlangonijr.chesslib;

import com.github.bhlangonijr.chesslib.move.Move;

import java.util.EnumMap;

/**
 * Move Backup structure
 */
public class MoveBackup implements BoardEvent {

    private final EnumMap<Side, CastleRight> castleRight;
    private Side sideToMove;
    private Square enPassantTarget;
    private Square enPassant;
    private Integer moveCounter;
    private Integer halfMoveCounter;
    private Move move;
    private Move rookCastleMove;
    private Piece capturedPiece;
    private Square capturedSquare;
    private Piece movingPiece;
    private boolean castleMove;
    private boolean enPassantMove;

    public MoveBackup() {
        castleRight = new EnumMap<Side, CastleRight>(Side.class);
    }

    public MoveBackup(Board board, Move move) {
        this();
        makeBackup(board, move);
    }

    /**
     * make the board backup
     *
     * @param board
     * @param move
     */
    public void makeBackup(Board board, Move move) {

        setSideToMove(board.getSideToMove());
        setEnPassantTarget(board.getEnPassantTarget());
        setEnPassant(board.getEnPassant());
        setMoveCounter(board.getMoveCounter());
        setHalfMoveCounter(board.getHalfMoveCounter());
        setMove(move);
        getCastleRight().put(Side.WHITE, board.getCastleRight(Side.WHITE));
        getCastleRight().put(Side.BLACK, board.getCastleRight(Side.BLACK));
        setCapturedPiece(board.getPiece(move.getTo()));
        setCapturedSquare(move.getTo());
        Piece moving = move.getPromotion() == Piece.NONE ? board.getPiece(move.getFrom()) : move.getPromotion();
        setMovingPiece(moving);
        if (board.getContext().isCastleMove(move)) {
            CastleRight c = board.getContext().isKingSideCastle(move) ? CastleRight.KING_SIDE :
                    CastleRight.QUEEN_SIDE;
            Move rookMove = board.getContext().getRookCastleMove(board.getSideToMove(), c);
            setRookCastleMove(rookMove);
            setCastleMove(true);
        } else {
            setRookCastleMove(null);
            setCastleMove(false);
        }
    }

    /**
     * restore the board
     *
     * @param board
     */
    public void restore(Board board) {
        board.setSideToMove(getSideToMove());
        board.setEnPassantTarget(getEnPassantTarget());
        board.setEnPassant(getEnPassant());
        board.setMoveCounter(getMoveCounter());
        board.setHalfMoveCounter(getHalfMoveCounter());
        Piece movingPiece = getMovingPiece();
        board.getCastleRight().put(Side.WHITE, getCastleRight().get(Side.WHITE));
        board.getCastleRight().put(Side.BLACK, getCastleRight().get(Side.BLACK));

        final boolean isCastle = board.getContext().isCastleMove(getMove());

        if (PieceType.KING.equals(movingPiece.getPieceType()) && isCastle) {
            board.undoMovePiece(getRookCastleMove());
        }
        board.unsetPiece(movingPiece, getMove().getTo());
        if (Piece.NONE.equals(getMove().getPromotion())) {
            board.setPiece(movingPiece, getMove().getFrom());
        } else {
            board.setPiece(Piece.make(getSideToMove(), PieceType.PAWN), getMove().getFrom());
        }
        if (!Piece.NONE.equals(getCapturedPiece())) {
            board.setPiece(getCapturedPiece(), getCapturedSquare());
        }
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
     * @return the move
     */
    public Move getMove() {
        return move;
    }

    /**
     * @param move the move to set
     */
    public void setMove(Move move) {
        this.move = move;
    }

    /**
     * @return the rookCastleMove
     */
    public Move getRookCastleMove() {
        return rookCastleMove;
    }

    /**
     * @param rookCastleMove the rookCastleMove to set
     */
    public void setRookCastleMove(Move rookCastleMove) {
        this.rookCastleMove = rookCastleMove;
    }

    /**
     * @return the castleRight
     */
    public EnumMap<Side, CastleRight> getCastleRight() {
        return castleRight;
    }

    /**
     * @return the capturedPiece
     */
    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    /**
     * @param capturedPiece the capturedPiece to set
     */
    public void setCapturedPiece(Piece capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    /**
     * @return the capturedSquare
     */
    public Square getCapturedSquare() {
        return capturedSquare;
    }

    /**
     * @param capturedSquare the capturedSquare to set
     */
    public void setCapturedSquare(Square capturedSquare) {
        this.capturedSquare = capturedSquare;
    }

    public BoardEventType getType() {
        return BoardEventType.ON_UNDO_MOVE;
    }

    /**
     * @return the movingPiece
     */
    public Piece getMovingPiece() {
        return movingPiece;
    }

    /**
     * @param movingPiece the movingPiece to set
     */
    public void setMovingPiece(Piece movingPiece) {
        this.movingPiece = movingPiece;
    }

    /**
     * @return the castleMove
     */
    public boolean isCastleMove() {
        return castleMove;
    }

    /**
     * @param castleMove the castleMove to set
     */
    public void setCastleMove(boolean castleMove) {
        this.castleMove = castleMove;
    }

    /**
     * @return the enPassantMove
     */
    public boolean isEnPassantMove() {
        return enPassantMove;
    }

    /**
     * @param enPassantMove the enPassantMove to set
     */
    public void setEnPassantMove(boolean enPassantMove) {
        this.enPassantMove = enPassantMove;
    }

}

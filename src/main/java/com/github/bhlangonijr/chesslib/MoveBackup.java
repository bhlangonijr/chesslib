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

import static com.github.bhlangonijr.chesslib.Constants.emptyMove;

import java.util.EnumMap;

import com.github.bhlangonijr.chesslib.move.Move;

/**
 * A structure that can be used to cancel the effects of a move and to restore the board to a previous status. The
 * board context is memorized at the <i>backup</i> is created, and it could be subsequently re-applied to the sourcing
 * board.
 * <p>
 * The move backup is also a {@link BoardEvent}, and hence it can be passed to the observers of the
 * {@link BoardEventType#ON_UNDO_MOVE} events, emitted when a move is reverted on a board.
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
    private long incrementalHashKey;

    /**
     * Constructs a new empty move backup.
     */
    public MoveBackup() {
        castleRight = new EnumMap<>(Side.class);
    }

    /**
     * Constructs a new move backup taking a board and a move. At the same time, it both instantiates the data structure
     * and takes a snapshot of the board status for a later restore.
     *
     * @param board the board that describes the status at the time of the move
     * @param move  the move which could be potentially restored later in time
     */
    public MoveBackup(Board board, Move move) {
        this();
        makeBackup(board, move);
    }

    /**
     * Initiates a new move backup, possibly overwriting any previously existing backup.
     *
     * @param board the board that describes the status at the time of the move
     * @param move  the move which could be potentially restored later in time
     */
    public void makeBackup(Board board, Move move) {

        setIncrementalHashKey(board.getIncrementalHashKey());
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
        Piece moving = board.getPiece(move.getFrom());
        setMovingPiece(moving);
        if (board.getContext().isCastleMove(move) && movingPiece == Piece.make(board.getSideToMove(), PieceType.KING)) {
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
     * Restores the previously stored status backup to the board passed as an argument, effectively cancelling the
     * consequences of the move memorized at the time the backup was created, as well as any potential move executed on
     * the board after that moment.
     * <p>
     * It is a responsibility of the caller to make sure the board used for creating the backup is also the board passed
     * in input to this method. No check is performed to prevent another board is used instead.
     *
     * @param board the board to be restored to a previous status
     */
    public void restore(Board board) {
        board.setSideToMove(getSideToMove());
        board.setEnPassantTarget(getEnPassantTarget());
        board.setEnPassant(getEnPassant());
        board.setMoveCounter(getMoveCounter());
        board.setHalfMoveCounter(getHalfMoveCounter());
        Piece movingPiece = move.getPromotion() == Piece.NONE ? getMovingPiece() : move.getPromotion();
        board.getCastleRight().put(Side.WHITE, getCastleRight().get(Side.WHITE));
        board.getCastleRight().put(Side.BLACK, getCastleRight().get(Side.BLACK));

        if (move != emptyMove) {
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
        board.setIncrementalHashKey(getIncrementalHashKey());
    }

    /**
     * Returns the next side to move used for restoring the board.
     *
     * @return the next side to move
     */
    public Side getSideToMove() {
        return sideToMove;
    }

    /**
     * Sets the next side to move used for restoring the board.
     *
     * @param sideToMove the next side to move
     */
    public void setSideToMove(Side sideToMove) {
        this.sideToMove = sideToMove;
    }

    /**
     * Returns the target square of an en passant capture used for restoring the board.
     *
     * @return the en passant target square, or {@link Square#NONE} if en passant was not possible at the time the
     * backup was created
     */
    public Square getEnPassantTarget() {
        return enPassantTarget;
    }

    /**
     * Sets the target square of an en passant capture used for restoring the board.
     *
     * @param enPassant the en passant target square
     */
    public void setEnPassantTarget(Square enPassant) {
        this.enPassantTarget = enPassant;
    }

    /**
     * Returns the destination square of an en passant capture used for restoring the board.
     *
     * @return the en passant destination square, or {@link Square#NONE} if en passant was not possible at the time the
     * backup was created
     */
    public Square getEnPassant() {
        return enPassant;
    }

    /**
     * Sets the destination square of an en passant capture used for restoring the board.
     *
     * @param enPassant the en passant destination square
     */
    public void setEnPassant(Square enPassant) {
        this.enPassant = enPassant;
    }

    /**
     * Returns the counter of full moves used for restoring the board.
     *
     * @return the counter of full moves
     */
    public Integer getMoveCounter() {
        return moveCounter;
    }

    /**
     * Sets the counter of full moves used for restoring the board.
     *
     * @param moveCounter the counter of full moves
     */
    public void setMoveCounter(Integer moveCounter) {
        this.moveCounter = moveCounter;
    }

    /**
     * Returns the counter of half moves used for restoring the board.
     *
     * @return the counter of half moves
     */
    public Integer getHalfMoveCounter() {
        return halfMoveCounter;
    }

    /**
     * Sets the counter of half moves used for restoring the board.
     *
     * @param halfMoveCounter the counter of half moves
     */
    public void setHalfMoveCounter(Integer halfMoveCounter) {
        this.halfMoveCounter = halfMoveCounter;
    }

    /**
     * Returns the move to revert in the case a board has to be restored.
     *
     * @return the move to revert
     */
    public Move getMove() {
        return move;
    }

    /**
     * Sets the move to revert in the case a board has to be restored.
     *
     * @param move the move to revert
     */
    public void setMove(Move move) {
        this.move = move;
    }

    /**
     * Returns the rook move to apply in order to revert a castle move in the case a board has to be restored.
     *
     * @return the rook move to apply to revert a castle move, or null if the move to revert is not a castle move
     */
    public Move getRookCastleMove() {
        return rookCastleMove;
    }

    /**
     * Sets the rook move to apply in order to revert a castle move in the case a board has to be restored.
     *
     * @param rookCastleMove the rook move to apply to revert a castle move
     */
    public void setRookCastleMove(Move rookCastleMove) {
        this.rookCastleMove = rookCastleMove;
    }

    /**
     * Returns the castle rights used for restoring the board.
     *
     * @return the castle rights
     */
    public EnumMap<Side, CastleRight> getCastleRight() {
        return castleRight;
    }

    /**
     * Returns the piece captured with the move to revert in the case a board has to be restored.
     *
     * @return the captured piece, or {@link Piece#NONE} if no piece was captured at the time the backup was created
     */
    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    /**
     * Sets the captured piece used for restoring the board.
     *
     * @param capturedPiece the captured piece
     */
    public void setCapturedPiece(Piece capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    /**
     * Returns the square of the piece captured with the move to revert in the case a board has to be restored.
     *
     * @return the square of the captured piece, or {@link Square#NONE} if no piece was captured at the time the backup
     * was created
     */
    public Square getCapturedSquare() {
        return capturedSquare;
    }

    /**
     * Sets the square of the captured piece used for restoring the board.
     *
     * @param capturedSquare the square of the captured piece
     */
    public void setCapturedSquare(Square capturedSquare) {
        this.capturedSquare = capturedSquare;
    }

    /**
     * The type of board events this data structure represents when notified to its observers.
     *
     * @return the board event type {@link BoardEventType#ON_UNDO_MOVE}
     */
    @Override
    public BoardEventType getType() {
        return BoardEventType.ON_UNDO_MOVE;
    }

    /**
     * Returns the piece moved in the move to revert in the case a board has to be restored.
     *
     * @return the moved piece
     */
    public Piece getMovingPiece() {
        return movingPiece;
    }

    /**
     * Sets the moving piece used for restoring the board.
     *
     * @param movingPiece the moving piece
     */
    public void setMovingPiece(Piece movingPiece) {
        this.movingPiece = movingPiece;
    }

    /**
     * Checks if the move to revert in the case a board has to be restored is a castle move.
     *
     * @return {@code true} if the move is a castle move
     */
    public boolean isCastleMove() {
        return castleMove;
    }

    /**
     * Sets whether the move to revert in the case a board has to be restored is a castle move or not.
     *
     * @param castleMove whether the move to restore is a castle move or not
     */
    public void setCastleMove(boolean castleMove) {
        this.castleMove = castleMove;
    }

    /**
     * Checks if the move to revert in the case a board has to be restored is an en passant move.
     *
     * @return {@code true} if the move is an en passant move
     */
    public boolean isEnPassantMove() {
        return enPassantMove;
    }

    /**
     * Sets whether the move to revert in the case a board has to be restored is an en passant move or not.
     *
     * @param enPassantMove whether the move to restore is an en passant move or not
     */
    public void setEnPassantMove(boolean enPassantMove) {
        this.enPassantMove = enPassantMove;
    }

    /**
     * Returns the incremental hash key used for restoring the board.
     *
     * @return the incremental hash key
     */
    public long getIncrementalHashKey() {
        return incrementalHashKey;
    }

    /**
     * Sets the incremental hash key used for restoring the board.
     *
     * @param incrementalHashKey the incremental hash key
     */
    public void setIncrementalHashKey(long incrementalHashKey) {
        this.incrementalHashKey = incrementalHashKey;
    }
}

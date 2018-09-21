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

import java.util.List;

/**
 * The Move generator.
 */
public class MoveGenerator {

    private MoveGenerator() {
    }

    /**
     * Generate All pawn moves
     *
     * @param board the board
     * @param moves the moves
     */
    public static void generatePawnCaptures(Board board, MoveList moves) {
        Side side = board.getSideToMove();
        long pieces = board.getBitboard(Piece.make(side, PieceType.PAWN));
        if (pieces != 0L) {
            List<Square> source = Bitboard.bbToSquareList(pieces);
            for (Square sqSource : source) {
                List<Square> target = Bitboard.bbToSquareList(
                        Bitboard.getPawnCaptures(side, sqSource,
                                board.getBitboard(), board.getEnPassantTarget())
                                & ~board.getBitboard(side));
                for (Square sqTarget : target) {
                    if (Side.WHITE.equals(side) &&
                            Rank.RANK_8.equals(sqTarget.getRank())) {
                        moves.add(new Move(sqSource, sqTarget, Piece.WHITE_QUEEN));
                        moves.add(new Move(sqSource, sqTarget, Piece.WHITE_ROOK));
                        moves.add(new Move(sqSource, sqTarget, Piece.WHITE_BISHOP));
                        moves.add(new Move(sqSource, sqTarget, Piece.WHITE_KNIGHT));
                    } else if (Side.BLACK.equals(side) &&
                            Rank.RANK_1.equals(sqTarget.getRank())) {
                        moves.add(new Move(sqSource, sqTarget, Piece.BLACK_QUEEN));
                        moves.add(new Move(sqSource, sqTarget, Piece.BLACK_ROOK));
                        moves.add(new Move(sqSource, sqTarget, Piece.BLACK_BISHOP));
                        moves.add(new Move(sqSource, sqTarget, Piece.BLACK_KNIGHT));
                    } else {
                        moves.add(new Move(sqSource, sqTarget, Piece.NONE));
                    }
                }
            }
        }
    }

    /**
     * Generate All pawn captures
     *
     * @param board the board
     * @param moves the moves
     */
    public static void generatePawnMoves(Board board, MoveList moves) {
        Side side = board.getSideToMove();
        long pieces = board.getBitboard(Piece.make(side, PieceType.PAWN));
        if (pieces != 0L) {
            List<Square> source = Bitboard.bbToSquareList(pieces);
            for (Square sqSource : source) {
                List<Square> target = Bitboard.bbToSquareList(
                        Bitboard.getPawnMoves(side, sqSource, board.getBitboard()));
                for (Square sqTarget : target) {
                    if (Side.WHITE.equals(side) &&
                            Rank.RANK_8.equals(sqTarget.getRank())) {
                        moves.add(new Move(sqSource, sqTarget, Piece.WHITE_QUEEN));
                        moves.add(new Move(sqSource, sqTarget, Piece.WHITE_ROOK));
                        moves.add(new Move(sqSource, sqTarget, Piece.WHITE_BISHOP));
                        moves.add(new Move(sqSource, sqTarget, Piece.WHITE_KNIGHT));
                    } else if (Side.BLACK.equals(side) &&
                            Rank.RANK_1.equals(sqTarget.getRank())) {
                        moves.add(new Move(sqSource, sqTarget, Piece.BLACK_QUEEN));
                        moves.add(new Move(sqSource, sqTarget, Piece.BLACK_ROOK));
                        moves.add(new Move(sqSource, sqTarget, Piece.BLACK_BISHOP));
                        moves.add(new Move(sqSource, sqTarget, Piece.BLACK_KNIGHT));
                    } else {
                        moves.add(new Move(sqSource, sqTarget, Piece.NONE));
                    }
                }
            }
        }
    }

    /**
     * Get knight moves
     *
     * @param board the board
     * @param moves the moves
     */
    public static void generateKnightMoves(Board board, MoveList moves) {
        Side side = board.getSideToMove();
        long pieces = board.getBitboard(Piece.make(side, PieceType.KNIGHT));
        if (pieces != 0L) {
            List<Square> source = Bitboard.bbToSquareList(pieces);
            for (Square sqSource : source) {
                List<Square> target = Bitboard.bbToSquareList(
                        Bitboard.getKnightAttacks(sqSource, ~board.getBitboard(side)));
                for (Square sqTarget : target) {
                    moves.add(new Move(sqSource, sqTarget, Piece.NONE));
                }
            }
        }
    }

    /**
     * Get Bishop moves
     *
     * @param board the board
     * @param moves the moves
     */
    public static void generateBishopMoves(Board board, MoveList moves) {
        Side side = board.getSideToMove();
        long pieces = board.getBitboard(Piece.make(side, PieceType.BISHOP));
        if (pieces != 0L) {
            List<Square> source = Bitboard.bbToSquareList(pieces);
            for (Square sqSource : source) {
                List<Square> target = Bitboard.bbToSquareList(
                        Bitboard.getBishopAttacks(board.getBitboard(), sqSource)
                                & ~board.getBitboard(side));
                for (Square sqTarget : target) {
                    moves.add(new Move(sqSource, sqTarget, Piece.NONE));
                }
            }
        }
    }

    /**
     * Get Rook moves
     *
     * @param board the board
     * @param moves the moves
     */
    public static void generateRookMoves(Board board, MoveList moves) {
        Side side = board.getSideToMove();
        long pieces = board.getBitboard(Piece.make(side, PieceType.ROOK));
        if (pieces != 0L) {
            List<Square> source = Bitboard.bbToSquareList(pieces);
            for (Square sqSource : source) {
                List<Square> target = Bitboard.bbToSquareList(
                        Bitboard.getRookAttacks(board.getBitboard(), sqSource)
                                & ~board.getBitboard(side));
                for (Square sqTarget : target) {
                    moves.add(new Move(sqSource, sqTarget, Piece.NONE));
                }
            }
        }
    }

    /**
     * Get Queen moves
     *
     * @param board the board
     * @param moves the moves
     */
    public static void generateQueenMoves(Board board, MoveList moves) {
        Side side = board.getSideToMove();
        long pieces = board.getBitboard(Piece.make(side, PieceType.QUEEN));
        if (pieces != 0L) {
            List<Square> source = Bitboard.bbToSquareList(pieces);
            for (Square sqSource : source) {
                List<Square> target = Bitboard.bbToSquareList(
                        Bitboard.getQueenAttacks(board.getBitboard(), sqSource)
                                & ~board.getBitboard(side));
                for (Square sqTarget : target) {
                    moves.add(new Move(sqSource, sqTarget, Piece.NONE));
                }
            }
        }
    }

    /**
     * Get King moves
     *
     * @param board the board
     * @param moves the moves
     */
    public static void generateKingMoves(Board board, MoveList moves) {
        Side side = board.getSideToMove();
        long pieces = board.getBitboard(Piece.make(side, PieceType.KING));
        if (pieces != 0L) {
            List<Square> source = Bitboard.bbToSquareList(pieces);
            for (Square sqSource : source) {
                List<Square> target = Bitboard.bbToSquareList(
                        Bitboard.getKingAttacks(
                                sqSource, ~board.getBitboard(side)));
                for (Square sqTarget : target) {
                    moves.add(new Move(sqSource, sqTarget, Piece.NONE));
                }
            }
        }
    }

    /**
     * Generate all castle moves - always legal moves
     *
     * @param board the board
     * @param moves the moves
     */
    public static void generateCastleMoves(Board board, MoveList moves) {
        Side side = board.getSideToMove();
        if (board.isKingAttacked()) {
            return;
        }
        if (board.getCastleRight(side).equals(CastleRight.KING_AND_QUEEN_SIDE) ||
                (board.getCastleRight(side).equals(CastleRight.KING_SIDE))) {
            if ((board.getBitboard() & board.getContext().getooAllSquaresBb(side)) == 0L) {
                if (!board.isSquareAttackedBy(board.getContext().getooSquares(side), side.flip())) {
                    moves.add(board.getContext().getoo(side));
                }
            }
        }
        if (board.getCastleRight(side).equals(CastleRight.KING_AND_QUEEN_SIDE) ||
                (board.getCastleRight(side).equals(CastleRight.QUEEN_SIDE))) {
            if ((board.getBitboard() & board.getContext().getoooAllSquaresBb(side)) == 0L) {
                if (!board.isSquareAttackedBy(board.getContext().getoooSquares(side), side.flip())) {
                    moves.add(board.getContext().getooo(side));
                }
            }
        }
    }

    /**
     * Generate all pseudo-legal moves
     *
     * @param board the board
     * @return move list
     */
    public static MoveList generatePseudoLegalMoves(Board board) {
        MoveList moves = new MoveList();
        generatePawnCaptures(board, moves);
        generatePawnMoves(board, moves);
        generateKnightMoves(board, moves);
        generateBishopMoves(board, moves);
        generateRookMoves(board, moves);
        generateQueenMoves(board, moves);
        generateKingMoves(board, moves);
        generateCastleMoves(board, moves);
        return moves;
    }

    /**
     * Generate Legal Moves
     *
     * @param board the board
     * @return move list
     * @throws MoveGeneratorException the move generator exception
     */
    public static MoveList generateLegalMoves(Board board) throws MoveGeneratorException {
        MoveList legalMoves = new MoveList();
        try {
            MoveList moves = generatePseudoLegalMoves(board);
            for (Move move : moves) {
                if (board.isMoveLegal(move, false)) {
                    legalMoves.add(move);
                }
            }
        } catch (Exception e) {
            throw new MoveGeneratorException("Couldn't generate Legal moves: ", e);
        }

        return legalMoves;
    }

}

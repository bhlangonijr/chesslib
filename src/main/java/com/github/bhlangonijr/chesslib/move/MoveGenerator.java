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

import java.util.LinkedList;
import java.util.List;

import static com.github.bhlangonijr.chesslib.Bitboard.bitScanForward;
import static com.github.bhlangonijr.chesslib.Bitboard.extractLsb;

/**
 * A handy collection of static utility methods for generating moves from a chess position.
 */
public class MoveGenerator {

    private MoveGenerator() {
    }

    /**
     * Generates all pawn captures for the playing side in the given position, and appends them to the list passed as an
     * argument. That implies the list must be mutable in order for this method to work.
     * <p>
     * All moves have to be considered pseudo-legal: although the captures are legal according to the standard rules of
     * pawn movements, the resulting position might not be considered legal after they are played on the board.
     *
     * @param board the board from which to generate the pawn captures
     * @param moves a mutable list in which to append the generated pawn captures
     */
    public static void generatePawnCaptures(Board board, List<Move> moves) {
        Side side = board.getSideToMove();
        long pieces = board.getBitboard(Piece.make(side, PieceType.PAWN));
        while (pieces != 0L) {
            int sourceIndex = bitScanForward(pieces);
            pieces = extractLsb(pieces);
            Square sqSource = Square.squareAt(sourceIndex);
            long attacks = Bitboard.getPawnCaptures(side, sqSource,
                    board.getBitboard(), board.getEnPassantTarget()) & ~board.getBitboard(side);
            while (attacks != 0L) {
                int targetIndex = bitScanForward(attacks);
                attacks = extractLsb(attacks);
                Square sqTarget = Square.squareAt(targetIndex);
                addPromotions(moves, side, sqTarget, sqSource);
            }
        }
    }

    /**
     * Generates all pawn moves, excluding captures, for the playing side in the given position, and appends them to the
     * list passed as an argument. That implies the list must be mutable in order for this method to work.
     * <p>
     * All moves have to be considered pseudo-legal: although the moves are legal according to the standard rules of
     * pawn movements, the resulting position might not be considered legal after they are played on the board.
     *
     * @param board the board from which to generate the pawn moves
     * @param moves a mutable list in which to append the generated pawn moves
     */
    public static void generatePawnMoves(Board board, List<Move> moves) {
        Side side = board.getSideToMove();
        long pieces = board.getBitboard(Piece.make(side, PieceType.PAWN));
        while (pieces != 0L) {
            int sourceIndex = bitScanForward(pieces);
            pieces = extractLsb(pieces);
            Square sqSource = Square.squareAt(sourceIndex);
            long attacks = Bitboard.getPawnMoves(side, sqSource, board.getBitboard());
            while (attacks != 0L) {
                int targetIndex = bitScanForward(attacks);
                attacks = extractLsb(attacks);
                Square sqTarget = Square.squareAt(targetIndex);
                addPromotions(moves, side, sqTarget, sqSource);
            }
        }
    }

    private static void addPromotions(List<Move> moves, Side side, Square sqTarget, Square sqSource) {

        if (Side.WHITE.equals(side) && Rank.RANK_8.equals(sqTarget.getRank())) {
            moves.add(new Move(sqSource, sqTarget, Piece.WHITE_QUEEN));
            moves.add(new Move(sqSource, sqTarget, Piece.WHITE_ROOK));
            moves.add(new Move(sqSource, sqTarget, Piece.WHITE_BISHOP));
            moves.add(new Move(sqSource, sqTarget, Piece.WHITE_KNIGHT));
        } else if (Side.BLACK.equals(side) && Rank.RANK_1.equals(sqTarget.getRank())) {
            moves.add(new Move(sqSource, sqTarget, Piece.BLACK_QUEEN));
            moves.add(new Move(sqSource, sqTarget, Piece.BLACK_ROOK));
            moves.add(new Move(sqSource, sqTarget, Piece.BLACK_BISHOP));
            moves.add(new Move(sqSource, sqTarget, Piece.BLACK_KNIGHT));
        } else {
            moves.add(new Move(sqSource, sqTarget, Piece.NONE));
        }
    }

    /**
     * Generates all knight moves for the playing side in the given position, according to a bitboard mask used to
     * specify the allowed target squares on the board. The generated moves are appended to the list passed as an
     * argument, which must be mutable in order for this method to work.
     * <p>
     * All moves have to be considered pseudo-legal: although the moves are legal according to the standard rules of
     * knight movements, the resulting position might not be considered legal after they are played on the board.
     *
     * @param board the board from which to generate the knight moves
     * @param moves a mutable list in which to append the generated knight moves
     * @param mask  bitboard mask of allowed targets
     */
    public static void generateKnightMoves(Board board, List<Move> moves, long mask) {
        Side side = board.getSideToMove();
        long pieces = board.getBitboard(Piece.make(side, PieceType.KNIGHT));
        while (pieces != 0L) {
            int knightIndex = bitScanForward(pieces);
            pieces = extractLsb(pieces);
            Square sqSource = Square.squareAt(knightIndex);
            long attacks = Bitboard.getKnightAttacks(sqSource, mask);
            while (attacks != 0L) {
                int attackIndex = bitScanForward(attacks);
                attacks = extractLsb(attacks);
                Square sqTarget = Square.squareAt(attackIndex);
                moves.add(new Move(sqSource, sqTarget, Piece.NONE));
            }
        }
    }

    /**
     * Generates all knight moves for the playing side in the given position, and appends them to the list passed as an
     * argument. That implies the list must be mutable in order for this method to work.
     * <p>
     * All moves have to be considered pseudo-legal: although the moves are legal according to the standard rules of
     * knight movements, the resulting position might not be considered legal after they are played on the board.
     *
     * @param board the board from which to generate the knight moves
     * @param moves a mutable list in which to append the generated knight moves
     * @see MoveGenerator#generateKnightMoves(Board, List, long)
     */
    public static void generateKnightMoves(Board board, List<Move> moves) {

        generateKnightMoves(board, moves, ~board.getBitboard(board.getSideToMove()));
    }

    /**
     * Generates all bishop moves for the playing side in the given position, according to a bitboard mask used to
     * specify the allowed target squares on the board. The generated moves are appended to the list passed as an
     * argument, which must be mutable in order for this method to work.
     * <p>
     * All moves have to be considered pseudo-legal: although the moves are legal according to the standard rules of
     * bishop movements, the resulting position might not be considered legal after they are played on the board.
     *
     * @param board the board from which to generate the bishop moves
     * @param moves a mutable list in which to append the generated bishop moves
     * @param mask  bitboard mask of allowed targets
     */
    public static void generateBishopMoves(Board board, List<Move> moves, long mask) {
        Side side = board.getSideToMove();
        long pieces = board.getBitboard(Piece.make(side, PieceType.BISHOP));
        while (pieces != 0L) {
            int sourceIndex = bitScanForward(pieces);
            pieces = extractLsb(pieces);
            Square sqSource = Square.squareAt(sourceIndex);
            long attacks = Bitboard.getBishopAttacks(board.getBitboard(), sqSource) & mask;
            while (attacks != 0L) {
                int attackIndex = bitScanForward(attacks);
                attacks = extractLsb(attacks);
                Square sqTarget = Square.squareAt(attackIndex);
                moves.add(new Move(sqSource, sqTarget, Piece.NONE));
            }
        }
    }

    /**
     * Generates all bishop moves for the playing side in the given position, and appends them to the list passed as an
     * argument. That implies the list must be mutable in order for this method to work.
     * <p>
     * All moves have to be considered pseudo-legal: although the moves are legal according to the standard rules of
     * bishop movements, the resulting position might not be considered legal after they are played on the board.
     *
     * @param board the board from which to generate the bishop moves
     * @param moves a mutable list in which to append the generated bishop moves
     * @see MoveGenerator#generateBishopMoves(Board, List, long)
     */
    public static void generateBishopMoves(Board board, List<Move> moves) {

        generateBishopMoves(board, moves, ~board.getBitboard(board.getSideToMove()));
    }

    /**
     * Generates all rook moves for the playing side in the given position, according to a bitboard mask used to specify
     * the allowed target squares on the board. The generated moves are appended to the list passed as an argument,
     * which must be mutable in order for this method to work.
     * <p>
     * All moves have to be considered pseudo-legal: although the moves are legal according to the standard rules of
     * rook movements, the resulting position might not be considered legal after they are played on the board.
     *
     * @param board the board from which to generate the rook moves
     * @param moves a mutable list in which to append the generated rook moves
     * @param mask  bitboard mask of allowed targets
     */
    public static void generateRookMoves(Board board, List<Move> moves, long mask) {
        Side side = board.getSideToMove();
        long pieces = board.getBitboard(Piece.make(side, PieceType.ROOK));
        while (pieces != 0L) {
            int sourceIndex = bitScanForward(pieces);
            pieces = extractLsb(pieces);
            Square sqSource = Square.squareAt(sourceIndex);
            long attacks = Bitboard.getRookAttacks(board.getBitboard(), sqSource) & mask;
            while (attacks != 0L) {
                int attackIndex = bitScanForward(attacks);
                attacks = extractLsb(attacks);
                Square sqTarget = Square.squareAt(attackIndex);
                moves.add(new Move(sqSource, sqTarget, Piece.NONE));
            }
        }
    }

    /**
     * Generates all rook moves for the playing side in the given position, and appends them to the list passed as an
     * argument. That implies the list must be mutable in order for this method to work.
     * <p>
     * All moves have to be considered pseudo-legal: although the moves are legal according to the standard rules of
     * rook movements, the resulting position might not be considered legal after they are played on the board.
     *
     * @param board the board from which to generate the rook moves
     * @param moves a mutable list in which to append the generated rook moves
     * @see MoveGenerator#generateRookMoves(Board, List, long)
     */
    public static void generateRookMoves(Board board, List<Move> moves) {

        generateRookMoves(board, moves, ~board.getBitboard(board.getSideToMove()));
    }

    /**
     * Generates all queen moves for the playing side in the given position, according to a bitboard mask used to
     * specify the allowed target squares on the board. The generated moves are appended to the list passed as an
     * argument, which must be mutable in order for this method to work.
     * <p>
     * All moves have to be considered pseudo-legal: although the moves are legal according to the standard rules of
     * queen movements, the resulting position might not be considered legal after they are played on the board.
     *
     * @param board the board from which to generate the queen moves
     * @param moves a mutable list in which to append the generated queen moves
     * @param mask  bitboard mask of allowed targets
     */
    public static void generateQueenMoves(Board board, List<Move> moves, long mask) {
        Side side = board.getSideToMove();
        long pieces = board.getBitboard(Piece.make(side, PieceType.QUEEN));
        while (pieces != 0L) {
            int sourceIndex = bitScanForward(pieces);
            pieces = extractLsb(pieces);
            Square sqSource = Square.squareAt(sourceIndex);
            long attacks = Bitboard.getQueenAttacks(board.getBitboard(), sqSource) & mask;
            while (attacks != 0L) {
                int attackIndex = bitScanForward(attacks);
                attacks = extractLsb(attacks);
                Square sqTarget = Square.squareAt(attackIndex);
                moves.add(new Move(sqSource, sqTarget, Piece.NONE));
            }
        }
    }

    /**
     * Generates all queen moves for the playing side in the given position, and appends them to the list passed as an
     * argument. That implies the list must be mutable in order for this method to work.
     * <p>
     * All moves have to be considered pseudo-legal: although the moves are legal according to the standard rules of
     * queen movements, the resulting position might not be considered legal after they are played on the board.
     *
     * @param board the board from which to generate the queen moves
     * @param moves a mutable list in which to append the generated queen moves
     * @see MoveGenerator#generateQueenMoves(Board, List, long)
     */
    public static void generateQueenMoves(Board board, List<Move> moves) {

        generateQueenMoves(board, moves, ~board.getBitboard(board.getSideToMove()));
    }

    /**
     * Generates all king moves for the playing side in the given position, according to a bitboard mask used to specify
     * the allowed target squares on the board. The generated moves are appended to the list passed as an argument,
     * which must be mutable in order for this method to work.
     * <p>
     * All moves have to be considered pseudo-legal: although the moves are legal according to the standard rules of
     * king movements, the resulting position might not be considered legal after they are played on the board.
     *
     * @param board the board from which to generate the king moves
     * @param moves a mutable list in which to append the generated king moves
     * @param mask  bitboard mask of allowed targets
     */
    public static void generateKingMoves(Board board, List<Move> moves, long mask) {
        Side side = board.getSideToMove();
        long pieces = board.getBitboard(Piece.make(side, PieceType.KING));
        while (pieces != 0L) {
            int sourceIndex = bitScanForward(pieces);
            pieces = extractLsb(pieces);
            Square sqSource = Square.squareAt(sourceIndex);
            long attacks = Bitboard.getKingAttacks(sqSource, mask);
            while (attacks != 0L) {
                int attackIndex = bitScanForward(attacks);
                attacks = extractLsb(attacks);
                Square sqTarget = Square.squareAt(attackIndex);
                moves.add(new Move(sqSource, sqTarget, Piece.NONE));
            }
        }
    }

    /**
     * Generates all king moves for the playing side in the given position, and appends them to the list passed as an
     * argument. That implies the list must be mutable in order for this method to work.
     * <p>
     * All moves have to be considered pseudo-legal: although the moves are legal according to the standard rules of
     * rook movements, the resulting position might not be considered legal after they are played on the board.
     *
     * @param board the board from which to generate the king moves
     * @param moves a mutable list in which to append the generated king moves
     * @see MoveGenerator#generateKingMoves(Board, List, long)
     */
    public static void generateKingMoves(Board board, List<Move> moves) {
        generateKingMoves(board, moves, ~board.getBitboard(board.getSideToMove()));
    }

    /**
     * Generates all castle moves for the playing side in the given position, and appends them to the list passed as an
     * argument. That implies the list must be mutable in order for this method to work.
     *
     * @param board the board from which to generate the castle moves
     * @param moves a mutable list in which to append the generated castle moves
     */
    public static void generateCastleMoves(Board board, List<Move> moves) {
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
     * Returns the list of all possible pseudo-legal moves for the given position.
     * <p>
     * A move is considered pseudo-legal when it is legal according to the standard rules of chess piece movements, but
     * the resulting position might not be legal because of other rules (e.g. checks to the king).
     *
     * @param board the board from which to generate the pseudo-legal moves
     * @return the list of pseudo-legal moves available in the position
     */
    public static List<Move> generatePseudoLegalMoves(Board board) {
        List<Move> moves = new LinkedList<>();
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
     * Returns the list of all possible pseudo-legal captures for the given position.
     * <p>
     * A move is considered a pseudo-legal capture when it takes an enemy piece and it is legal according to the
     * standard rules of chess piece movements, but the resulting position might not be legal because of other rules
     * (e.g. checks to the king).
     *
     * @param board the board from which to generate the pseudo-legal captures
     * @return the list of pseudo-legal captures available in the position
     */
    public static List<Move> generatePseudoLegalCaptures(Board board) {
        List<Move> moves = new LinkedList<>();
        Side other = board.getSideToMove().flip();
        generatePawnCaptures(board, moves);
        generateKnightMoves(board, moves, board.getBitboard(other));
        generateBishopMoves(board, moves, board.getBitboard(other));
        generateRookMoves(board, moves, board.getBitboard(other));
        generateQueenMoves(board, moves, board.getBitboard(other));
        generateKingMoves(board, moves, board.getBitboard(other));
        return moves;
    }

    /**
     * Returns the list of all possible legal moves for the position according to the standard rules of chess.
     *
     * @param board the board from which to generate the legal moves
     * @return the list of legal moves available in the position
     * @throws MoveGeneratorException if it is not possible to generate the moves
     */
    public static List<Move> generateLegalMoves(Board board) throws MoveGeneratorException {
        try {
            List<Move> moves = generatePseudoLegalMoves(board);
            moves.removeIf(move -> !board.isMoveLegal(move, false));
            return moves;
        } catch (Exception e) {
            throw new MoveGeneratorException("Couldn't generate Legal moves: ", e);
        }
    }

}

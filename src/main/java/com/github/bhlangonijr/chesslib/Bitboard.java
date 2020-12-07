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

import java.util.LinkedList;
import java.util.List;

import static com.github.bhlangonijr.chesslib.DiagonalA1H8.*;
import static com.github.bhlangonijr.chesslib.DiagonalH1A8.*;
import static com.github.bhlangonijr.chesslib.Square.*;

/**
 * Bitboard functions
 */
public class Bitboard {

    /**
     * The constant lightSquares.
     */
    public static final long lightSquares = 0x55AA55AA55AA55AAL;
    /**
     * The constant darkSquares.
     */
    public static final long darkSquares = 0xAA55AA55AA55AA55L;

    /**
     * The constant rankBB.
     */
// bitboard for all ranks
    final static long[] rankBB = {
            0x00000000000000FFL, 0x000000000000FF00L, 0x0000000000FF0000L, 0x00000000FF000000L,
            0x000000FF00000000L, 0x0000FF0000000000L, 0x00FF000000000000L, 0xFF00000000000000L
    };
    /**
     * The constant fileBB.
     */
// bitboard for all files
    final static long[] fileBB = {
            0x0101010101010101L, 0x0202020202020202L, 0x0404040404040404L, 0x0808080808080808L,
            0x1010101010101010L, 0x2020202020202020L, 0x4040404040404040L, 0x8080808080808080L
    };

    /**
     * The constant bits between Table.
     */
    final static long[][] bbTable = new long[64][64];
    /**
     * The constant squareToDiagonalA1H8.
     */
// square to enum diagonal A1..H8
    static final DiagonalA1H8[] squareToDiagonalA1H8 = {
            H8_A1, B1_H7, C1_H6, D1_H5, E1_H4, F1_H3, G1_H2, H1_H1,
            G8_A2, H8_A1, B1_H7, C1_H6, D1_H5, E1_H4, F1_H3, G1_H2,
            F8_A3, G8_A2, H8_A1, B1_H7, C1_H6, D1_H5, E1_H4, F1_H3,
            E8_A4, F8_A3, G8_A2, H8_A1, B1_H7, C1_H6, D1_H5, E1_H4,
            D8_A5, E8_A4, F8_A3, G8_A2, H8_A1, B1_H7, C1_H6, D1_H5,
            C8_A6, D8_A5, E8_A4, F8_A3, G8_A2, H8_A1, B1_H7, C1_H6,
            B8_A7, C8_A6, D8_A5, E8_A4, F8_A3, G8_A2, H8_A1, B1_H7,
            A8_A8, B8_A7, C8_A6, D8_A5, E8_A4, F8_A3, G8_A2, H8_A1
    };
    /**
     * The constant squareToDiagonalH1A8.
     */
// square to enum diagonal A1..H8
    static final DiagonalH1A8[] squareToDiagonalH1A8 = {
            A1_A1, B1_A2, C1_A3, D1_A4, E1_A5, F1_A6, G1_A7, H1_A8,
            B1_A2, C1_A3, D1_A4, E1_A5, F1_A6, G1_A7, H1_A8, B8_H2,
            C1_A3, D1_A4, E1_A5, F1_A6, G1_A7, H1_A8, B8_H2, C8_H3,
            D1_A4, E1_A5, F1_A6, G1_A7, H1_A8, B8_H2, C8_H3, D8_H4,
            E1_A5, F1_A6, G1_A7, H1_A8, B8_H2, C8_H3, D8_H4, E8_H5,
            F1_A6, G1_A7, H1_A8, B8_H2, C8_H3, D8_H4, E8_H5, F8_H6,
            G1_A7, H1_A8, B8_H2, C8_H3, D8_H4, E8_H5, F8_H6, G8_H7,
            H1_A8, B8_H2, C8_H3, D8_H4, E8_H5, F8_H6, G8_H7, H8_H8
    };
    /**
     * The constant diagonalH1A8BB.
     */
// bitboard for all diagonal H1..A8
    final static long[] diagonalH1A8BB = {
            sq2Bb(A1),
            sq2Bb(B1) | sq2Bb(A2),
            sq2Bb(C1) | sq2Bb(B2) | sq2Bb(A3),
            sq2Bb(D1) | sq2Bb(C2) | sq2Bb(B3) | sq2Bb(A4),
            sq2Bb(E1) | sq2Bb(D2) | sq2Bb(C3) | sq2Bb(B4) | sq2Bb(A5),
            sq2Bb(F1) | sq2Bb(E2) | sq2Bb(D3) | sq2Bb(C4) | sq2Bb(B5) | sq2Bb(A6),
            sq2Bb(G1) | sq2Bb(F2) | sq2Bb(E3) | sq2Bb(D4) | sq2Bb(C5) | sq2Bb(B6) | sq2Bb(A7),
            sq2Bb(H1) | sq2Bb(G2) | sq2Bb(F3) | sq2Bb(E4) | sq2Bb(D5) | sq2Bb(C6) | sq2Bb(B7) | sq2Bb(A8),
            sq2Bb(B8) | sq2Bb(C7) | sq2Bb(D6) | sq2Bb(E5) | sq2Bb(F4) | sq2Bb(G3) | sq2Bb(H2),
            sq2Bb(C8) | sq2Bb(D7) | sq2Bb(E6) | sq2Bb(F5) | sq2Bb(G4) | sq2Bb(H3),
            sq2Bb(D8) | sq2Bb(E7) | sq2Bb(F6) | sq2Bb(G5) | sq2Bb(H4),
            sq2Bb(E8) | sq2Bb(F7) | sq2Bb(G6) | sq2Bb(H5),
            sq2Bb(F8) | sq2Bb(G7) | sq2Bb(H6),
            sq2Bb(G8) | sq2Bb(H7),
            sq2Bb(H8)
    };
    /**
     * The constant diagonalA1H8BB.
     */
// bitboard for all diagonal A1..H8
    final static long[] diagonalA1H8BB = {
            sq2Bb(A8),
            sq2Bb(B8) | sq2Bb(A7),
            sq2Bb(C8) | sq2Bb(B7) | sq2Bb(A6),
            sq2Bb(D8) | sq2Bb(C7) | sq2Bb(B6) | sq2Bb(A5),
            sq2Bb(E8) | sq2Bb(D7) | sq2Bb(C6) | sq2Bb(B5) | sq2Bb(A4),
            sq2Bb(F8) | sq2Bb(E7) | sq2Bb(D6) | sq2Bb(C5) | sq2Bb(B4) | sq2Bb(A3),
            sq2Bb(G8) | sq2Bb(F7) | sq2Bb(E6) | sq2Bb(D5) | sq2Bb(C4) | sq2Bb(B3) | sq2Bb(A2),
            sq2Bb(H8) | sq2Bb(G7) | sq2Bb(F6) | sq2Bb(E5) | sq2Bb(D4) | sq2Bb(C3) | sq2Bb(B2) | sq2Bb(A1),
            sq2Bb(B1) | sq2Bb(C2) | sq2Bb(D3) | sq2Bb(E4) | sq2Bb(F5) | sq2Bb(G6) | sq2Bb(H7),
            sq2Bb(C1) | sq2Bb(D2) | sq2Bb(E3) | sq2Bb(F4) | sq2Bb(G5) | sq2Bb(H6),
            sq2Bb(D1) | sq2Bb(E2) | sq2Bb(F3) | sq2Bb(G4) | sq2Bb(H5),
            sq2Bb(E1) | sq2Bb(F2) | sq2Bb(G3) | sq2Bb(H4),
            sq2Bb(F1) | sq2Bb(G2) | sq2Bb(H3),
            sq2Bb(G1) | sq2Bb(H2),
            sq2Bb(H1)
    };
    /**
     * The constant knightAttacks.
     */
// bitboard for all knight attacks
    final static long[] knightAttacks = {
            0x0000000000020400L, 0x0000000000050800L, 0x00000000000a1100L, 0x0000000000142200L, 0x0000000000284400L, 0x0000000000508800L, 0x0000000000a01000L, 0x0000000000402000L,
            0x0000000002040004L, 0x0000000005080008L, 0x000000000a110011L, 0x0000000014220022L, 0x0000000028440044L, 0x0000000050880088L, 0x00000000a0100010L, 0x0000000040200020L,
            0x0000000204000402L, 0x0000000508000805L, 0x0000000a1100110aL, 0x0000001422002214L, 0x0000002844004428L, 0x0000005088008850L, 0x000000a0100010a0L, 0x0000004020002040L,
            0x0000020400040200L, 0x0000050800080500L, 0x00000a1100110a00L, 0x0000142200221400L, 0x0000284400442800L, 0x0000508800885000L, 0x0000a0100010a000L, 0x0000402000204000L,
            0x0002040004020000L, 0x0005080008050000L, 0x000a1100110a0000L, 0x0014220022140000L, 0x0028440044280000L, 0x0050880088500000L, 0x00a0100010a00000L, 0x0040200020400000L,
            0x0204000402000000L, 0x0508000805000000L, 0x0a1100110a000000L, 0x1422002214000000L, 0x2844004428000000L, 0x5088008850000000L, 0xa0100010a0000000L, 0x4020002040000000L,
            0x0400040200000000L, 0x0800080500000000L, 0x1100110a00000000L, 0x2200221400000000L, 0x4400442800000000L, 0x8800885000000000L, 0x100010a000000000L, 0x2000204000000000L,
            0x0004020000000000L, 0x0008050000000000L, 0x00110a0000000000L, 0x0022140000000000L, 0x0044280000000000L, 0x0088500000000000L, 0x0010a00000000000L, 0x0020400000000000L
    };
    /**
     * The constant whitePawnAttacks.
     */
// bitboard for all white pawn attacks
    final static long[] whitePawnAttacks = {
            0x0000000000000200L, 0x0000000000000500L, 0x0000000000000a00L, 0x0000000000001400L, 0x0000000000002800L, 0x0000000000005000L, 0x000000000000a000L, 0x0000000000004000L,
            0x0000000000020000L, 0x0000000000050000L, 0x00000000000a0000L, 0x0000000000140000L, 0x0000000000280000L, 0x0000000000500000L, 0x0000000000a00000L, 0x0000000000400000L,
            0x0000000002000000L, 0x0000000005000000L, 0x000000000a000000L, 0x0000000014000000L, 0x0000000028000000L, 0x0000000050000000L, 0x00000000a0000000L, 0x0000000040000000L,
            0x0000000200000000L, 0x0000000500000000L, 0x0000000a00000000L, 0x0000001400000000L, 0x0000002800000000L, 0x0000005000000000L, 0x000000a000000000L, 0x0000004000000000L,
            0x0000020000000000L, 0x0000050000000000L, 0x00000a0000000000L, 0x0000140000000000L, 0x0000280000000000L, 0x0000500000000000L, 0x0000a00000000000L, 0x0000400000000000L,
            0x0002000000000000L, 0x0005000000000000L, 0x000a000000000000L, 0x0014000000000000L, 0x0028000000000000L, 0x0050000000000000L, 0x00a0000000000000L, 0x0040000000000000L,
            0x0200000000000000L, 0x0500000000000000L, 0x0a00000000000000L, 0x1400000000000000L, 0x2800000000000000L, 0x5000000000000000L, 0xa000000000000000L, 0x4000000000000000L,
            0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L
    };
    /**
     * The constant blackPawnAttacks.
     */
// bitboard for all black pawn attacks
    final static long[] blackPawnAttacks = {
            0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
            0x0000000000000002L, 0x0000000000000005L, 0x000000000000000aL, 0x0000000000000014L, 0x0000000000000028L, 0x0000000000000050L, 0x00000000000000a0L, 0x0000000000000040L,
            0x0000000000000200L, 0x0000000000000500L, 0x0000000000000a00L, 0x0000000000001400L, 0x0000000000002800L, 0x0000000000005000L, 0x000000000000a000L, 0x0000000000004000L,
            0x0000000000020000L, 0x0000000000050000L, 0x00000000000a0000L, 0x0000000000140000L, 0x0000000000280000L, 0x0000000000500000L, 0x0000000000a00000L, 0x0000000000400000L,
            0x0000000002000000L, 0x0000000005000000L, 0x000000000a000000L, 0x0000000014000000L, 0x0000000028000000L, 0x0000000050000000L, 0x00000000a0000000L, 0x0000000040000000L,
            0x0000000200000000L, 0x0000000500000000L, 0x0000000a00000000L, 0x0000001400000000L, 0x0000002800000000L, 0x0000005000000000L, 0x000000a000000000L, 0x0000004000000000L,
            0x0000020000000000L, 0x0000050000000000L, 0x00000a0000000000L, 0x0000140000000000L, 0x0000280000000000L, 0x0000500000000000L, 0x0000a00000000000L, 0x0000400000000000L,
            0x0002000000000000L, 0x0005000000000000L, 0x000a000000000000L, 0x0014000000000000L, 0x0028000000000000L, 0x0050000000000000L, 0x00a0000000000000L, 0x0040000000000000L
    };
    /**
     * The constant whitePawnMoves.
     */
// bitboard for all white pawn moves
    final static long[] whitePawnMoves = {
            0x0000000000000100L, 0x0000000000000200L, 0x0000000000000400L, 0x0000000000000800L, 0x0000000000001000L, 0x0000000000002000L, 0x0000000000004000L, 0x0000000000008000L,
            0x0000000001010000L, 0x0000000002020000L, 0x0000000004040000L, 0x0000000008080000L, 0x0000000010100000L, 0x0000000020200000L, 0x0000000040400000L, 0x0000000080800000L,
            0x0000000001000000L, 0x0000000002000000L, 0x0000000004000000L, 0x0000000008000000L, 0x0000000010000000L, 0x0000000020000000L, 0x0000000040000000L, 0x0000000080000000L,
            0x0000000100000000L, 0x0000000200000000L, 0x0000000400000000L, 0x0000000800000000L, 0x0000001000000000L, 0x0000002000000000L, 0x0000004000000000L, 0x0000008000000000L,
            0x0000010000000000L, 0x0000020000000000L, 0x0000040000000000L, 0x0000080000000000L, 0x0000100000000000L, 0x0000200000000000L, 0x0000400000000000L, 0x0000800000000000L,
            0x0001000000000000L, 0x0002000000000000L, 0x0004000000000000L, 0x0008000000000000L, 0x0010000000000000L, 0x0020000000000000L, 0x0040000000000000L, 0x0080000000000000L,
            0x0100000000000000L, 0x0200000000000000L, 0x0400000000000000L, 0x0800000000000000L, 0x1000000000000000L, 0x2000000000000000L, 0x4000000000000000L, 0x8000000000000000L,
            0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L
    };
    /**
     * The constant blackPawnMoves.
     */
// bitboard for all black pawn moves
    final static long[] blackPawnMoves = {
            0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
            0x0000000000000001L, 0x0000000000000002L, 0x0000000000000004L, 0x0000000000000008L, 0x0000000000000010L, 0x0000000000000020L, 0x0000000000000040L, 0x0000000000000080L,
            0x0000000000000100L, 0x0000000000000200L, 0x0000000000000400L, 0x0000000000000800L, 0x0000000000001000L, 0x0000000000002000L, 0x0000000000004000L, 0x0000000000008000L,
            0x0000000000010000L, 0x0000000000020000L, 0x0000000000040000L, 0x0000000000080000L, 0x0000000000100000L, 0x0000000000200000L, 0x0000000000400000L, 0x0000000000800000L,
            0x0000000001000000L, 0x0000000002000000L, 0x0000000004000000L, 0x0000000008000000L, 0x0000000010000000L, 0x0000000020000000L, 0x0000000040000000L, 0x0000000080000000L,
            0x0000000100000000L, 0x0000000200000000L, 0x0000000400000000L, 0x0000000800000000L, 0x0000001000000000L, 0x0000002000000000L, 0x0000004000000000L, 0x0000008000000000L,
            0x0000010100000000L, 0x0000020200000000L, 0x0000040400000000L, 0x0000080800000000L, 0x0000101000000000L, 0x0000202000000000L, 0x0000404000000000L, 0x0000808000000000L,
            0x0001000000000000L, 0x0002000000000000L, 0x0004000000000000L, 0x0008000000000000L, 0x0010000000000000L, 0x0020000000000000L, 0x0040000000000000L, 0x0080000000000000L
    };
    /**
     * The constant adjacentSquares.
     */
// bitboard for all adjacent squares
    final static long[] adjacentSquares = {
            0x0000000000000302L, 0x0000000000000705L, 0x0000000000000e0aL, 0x0000000000001c14L, 0x0000000000003828L, 0x0000000000007050L, 0x000000000000e0a0L, 0x000000000000c040L,
            0x0000000000030203L, 0x0000000000070507L, 0x00000000000e0a0eL, 0x00000000001c141cL, 0x0000000000382838L, 0x0000000000705070L, 0x0000000000e0a0e0L, 0x0000000000c040c0L,
            0x0000000003020300L, 0x0000000007050700L, 0x000000000e0a0e00L, 0x000000001c141c00L, 0x0000000038283800L, 0x0000000070507000L, 0x00000000e0a0e000L, 0x00000000c040c000L,
            0x0000000302030000L, 0x0000000705070000L, 0x0000000e0a0e0000L, 0x0000001c141c0000L, 0x0000003828380000L, 0x0000007050700000L, 0x000000e0a0e00000L, 0x000000c040c00000L,
            0x0000030203000000L, 0x0000070507000000L, 0x00000e0a0e000000L, 0x00001c141c000000L, 0x0000382838000000L, 0x0000705070000000L, 0x0000e0a0e0000000L, 0x0000c040c0000000L,
            0x0003020300000000L, 0x0007050700000000L, 0x000e0a0e00000000L, 0x001c141c00000000L, 0x0038283800000000L, 0x0070507000000000L, 0x00e0a0e000000000L, 0x00c040c000000000L,
            0x0302030000000000L, 0x0705070000000000L, 0x0e0a0e0000000000L, 0x1c141c0000000000L, 0x3828380000000000L, 0x7050700000000000L, 0xe0a0e00000000000L, 0xc040c00000000000L,
            0x0203000000000000L, 0x0507000000000000L, 0x0a0e000000000000L, 0x141c000000000000L, 0x2838000000000000L, 0x5070000000000000L, 0xa0e0000000000000L, 0x40c0000000000000L
    };
    /**
     * The constant rankAttacks.
     */
// bitboard for rank attacks
    final static long[] rankAttacks = {
            sq2RA(A1), sq2RA(B1), sq2RA(C1), sq2RA(D1), sq2RA(E1), sq2RA(F1), sq2RA(G1), sq2RA(H1),
            sq2RA(A2), sq2RA(B2), sq2RA(C2), sq2RA(D2), sq2RA(E2), sq2RA(F2), sq2RA(G2), sq2RA(H2),
            sq2RA(A3), sq2RA(B3), sq2RA(C3), sq2RA(D3), sq2RA(E3), sq2RA(F3), sq2RA(G3), sq2RA(H3),
            sq2RA(A4), sq2RA(B4), sq2RA(C4), sq2RA(D4), sq2RA(E4), sq2RA(F4), sq2RA(G4), sq2RA(H4),
            sq2RA(A5), sq2RA(B5), sq2RA(C5), sq2RA(D5), sq2RA(E5), sq2RA(F5), sq2RA(G5), sq2RA(H5),
            sq2RA(A6), sq2RA(B6), sq2RA(C6), sq2RA(D6), sq2RA(E6), sq2RA(F6), sq2RA(G6), sq2RA(H6),
            sq2RA(A7), sq2RA(B7), sq2RA(C7), sq2RA(D7), sq2RA(E7), sq2RA(F7), sq2RA(G7), sq2RA(H7),
            sq2RA(A8), sq2RA(B8), sq2RA(C8), sq2RA(D8), sq2RA(E8), sq2RA(F8), sq2RA(G8), sq2RA(H8)
    };
    /**
     * The constant fileAttacks.
     */
// bitboard for file attacks
    final static long[] fileAttacks = {
            sq2FA(A1), sq2FA(B1), sq2FA(C1), sq2FA(D1), sq2FA(E1), sq2FA(F1), sq2FA(G1), sq2FA(H1),
            sq2FA(A2), sq2FA(B2), sq2FA(C2), sq2FA(D2), sq2FA(E2), sq2FA(F2), sq2FA(G2), sq2FA(H2),
            sq2FA(A3), sq2FA(B3), sq2FA(C3), sq2FA(D3), sq2FA(E3), sq2FA(F3), sq2FA(G3), sq2FA(H3),
            sq2FA(A4), sq2FA(B4), sq2FA(C4), sq2FA(D4), sq2FA(E4), sq2FA(F4), sq2FA(G4), sq2FA(H4),
            sq2FA(A5), sq2FA(B5), sq2FA(C5), sq2FA(D5), sq2FA(E5), sq2FA(F5), sq2FA(G5), sq2FA(H5),
            sq2FA(A6), sq2FA(B6), sq2FA(C6), sq2FA(D6), sq2FA(E6), sq2FA(F6), sq2FA(G6), sq2FA(H6),
            sq2FA(A7), sq2FA(B7), sq2FA(C7), sq2FA(D7), sq2FA(E7), sq2FA(F7), sq2FA(G7), sq2FA(H7),
            sq2FA(A8), sq2FA(B8), sq2FA(C8), sq2FA(D8), sq2FA(E8), sq2FA(F8), sq2FA(G8), sq2FA(H8)
    };
    /**
     * The constant diagA1H8Attacks.
     */
// bitboard for diagonal attacks
    final static long[] diagA1H8Attacks = {
            sq2A1(A1), sq2A1(B1), sq2A1(C1), sq2A1(D1), sq2A1(E1), sq2A1(F1), sq2A1(G1), sq2A1(H1),
            sq2A1(A2), sq2A1(B2), sq2A1(C2), sq2A1(D2), sq2A1(E2), sq2A1(F2), sq2A1(G2), sq2A1(H2),
            sq2A1(A3), sq2A1(B3), sq2A1(C3), sq2A1(D3), sq2A1(E3), sq2A1(F3), sq2A1(G3), sq2A1(H3),
            sq2A1(A4), sq2A1(B4), sq2A1(C4), sq2A1(D4), sq2A1(E4), sq2A1(F4), sq2A1(G4), sq2A1(H4),
            sq2A1(A5), sq2A1(B5), sq2A1(C5), sq2A1(D5), sq2A1(E5), sq2A1(F5), sq2A1(G5), sq2A1(H5),
            sq2A1(A6), sq2A1(B6), sq2A1(C6), sq2A1(D6), sq2A1(E6), sq2A1(F6), sq2A1(G6), sq2A1(H6),
            sq2A1(A7), sq2A1(B7), sq2A1(C7), sq2A1(D7), sq2A1(E7), sq2A1(F7), sq2A1(G7), sq2A1(H7),
            sq2A1(A8), sq2A1(B8), sq2A1(C8), sq2A1(D8), sq2A1(E8), sq2A1(F8), sq2A1(G8), sq2A1(H8)
    };
    /**
     * The constant diagH1A8Attacks.
     */
// bitboard for diagonal attacks
    final static long[] diagH1A8Attacks = {
            sq2H1(A1), sq2H1(B1), sq2H1(C1), sq2H1(D1), sq2H1(E1), sq2H1(F1), sq2H1(G1), sq2H1(H1),
            sq2H1(A2), sq2H1(B2), sq2H1(C2), sq2H1(D2), sq2H1(E2), sq2H1(F2), sq2H1(G2), sq2H1(H2),
            sq2H1(A3), sq2H1(B3), sq2H1(C3), sq2H1(D3), sq2H1(E3), sq2H1(F3), sq2H1(G3), sq2H1(H3),
            sq2H1(A4), sq2H1(B4), sq2H1(C4), sq2H1(D4), sq2H1(E4), sq2H1(F4), sq2H1(G4), sq2H1(H4),
            sq2H1(A5), sq2H1(B5), sq2H1(C5), sq2H1(D5), sq2H1(E5), sq2H1(F5), sq2H1(G5), sq2H1(H5),
            sq2H1(A6), sq2H1(B6), sq2H1(C6), sq2H1(D6), sq2H1(E6), sq2H1(F6), sq2H1(G6), sq2H1(H6),
            sq2H1(A7), sq2H1(B7), sq2H1(C7), sq2H1(D7), sq2H1(E7), sq2H1(F7), sq2H1(G7), sq2H1(H7),
            sq2H1(A8), sq2H1(B8), sq2H1(C8), sq2H1(D8), sq2H1(E8), sq2H1(F8), sq2H1(G8), sq2H1(H8)
    };

    static {
        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                bbTable[x][y] = ((1L << y) | ((1L << y) - (1L << x)));
            }
        }
    }

    /**
     * Sq 2 bb long.
     *
     * @param sq the square
     * @return the long
     */
    final static long sq2Bb(Square sq) {
        return sq.getBitboard();
    }

    /**
     * Sq 2 ra long.
     *
     * @param x the x
     * @return the long
     */
    final static long sq2RA(Square x) {
        return (rankBB[x.getRank().ordinal()] ^ sq2Bb(x));
    }

    /**
     * Sq 2 fa long.
     *
     * @param x the x
     * @return the long
     */
    final static long sq2FA(Square x) {
        return (fileBB[x.getFile().ordinal()] ^ x.getBitboard());
    }

    /**
     * Sq 2 a 1 long.
     *
     * @param x the x
     * @return the long
     */
    final static long sq2A1(Square x) {
        return (diagonalA1H8BB[squareToDiagonalA1H8[x.ordinal()].ordinal()] ^ sq2Bb(x));
    }

    /**
     * Sq 2 h 1 long.
     *
     * @param x the x
     * @return the long
     */
    final static long sq2H1(Square x) {
        return (diagonalH1A8BB[squareToDiagonalH1A8[x.ordinal()].ordinal()] ^ sq2Bb(x));
    }

    /**
     * Bit Scan Forward - LS1B
     *
     * @param bb the bb
     * @return int
     */
    public static int bitScanForward(long bb) {
        return Long.numberOfTrailingZeros(bb);
    }

    /**
     * Bit Scan Reverse - MS1B
     *
     * @param bb the bb
     * @return int
     */
    public static int bitScanReverse(long bb) {
        return 63 - Long.numberOfLeadingZeros(bb);
    }

    /**
     * Bits between
     *
     * @param bb  the bb
     * @param sq1 the sq 1
     * @param sq2 the sq 2
     * @return long
     */
    public static long bitsBetween(long bb, int sq1, int sq2) {
        return bbTable[sq1][sq2] & bb;
    }

    /**
     * extract least significant bit of a bitboard
     *
     * @param bb the bb
     * @return long
     */
    public static long extractLsb(Long bb) {
        return bb & (bb - 1);
    }

    /**
     * Has only 1 bit boolean.
     *
     * @param bb the bb
     * @return the boolean
     */
    public static boolean hasOnly1Bit(Long bb) {
        return bb > 0L && extractLsb(bb) == 0L;
    }

    /**
     * Gets bbtable.
     *
     * @param sq the sq
     * @return the bbtable
     */
    public static long getBbtable(Square sq) {
        return 1L << sq.ordinal();
    }

    /**
     * get slider attacks based on the attacks mask and occupance
     *
     * @param attacks
     * @param mask
     * @param index
     * @return
     */
    private static long getSliderAttacks(long attacks, long mask, int index) {

        long occ = mask & attacks;
        if (occ == 0L) {
            return attacks;
        }
        long m = (1L << index) - 1L;
        long lowerMask = occ & m;
        long upperMask = occ & ~m;
        int minor = lowerMask == 0L ? 0 : bitScanReverse(lowerMask);
        int major = upperMask == 0L ? 63 : bitScanForward(upperMask);
        return bitsBetween(attacks, minor, major);
    }

    /**
     * Get the bishop attacks
     *
     * @param mask   the mask
     * @param square the square
     * @return bishop attacks
     */
    public static long getBishopAttacks(long mask, Square square) {
        return getSliderAttacks(diagA1H8Attacks[square.ordinal()], mask, square.ordinal()) |
                getSliderAttacks(diagH1A8Attacks[square.ordinal()], mask, square.ordinal());
    }

    /**
     * Get the rook attacks
     *
     * @param mask   the mask
     * @param square the square
     * @return rook attacks
     */
    public static long getRookAttacks(long mask, Square square) {
        return getSliderAttacks(fileAttacks[square.ordinal()], mask, square.ordinal()) |
                getSliderAttacks(rankAttacks[square.ordinal()], mask, square.ordinal());
    }

    /**
     * Get the queen attacks
     *
     * @param mask   the mask
     * @param square the square
     * @return queen attacks
     */
    public static long getQueenAttacks(long mask, Square square) {
        return getRookAttacks(mask, square) |
                getBishopAttacks(mask, square);
    }

    /**
     * return a bitboard with attacked squares by the pawn in the given square
     *
     * @param square   the square
     * @param occupied the occupied
     * @return knight attacks
     */
    public static long getKnightAttacks(Square square, long occupied) {
        return knightAttacks[square.ordinal()] & occupied;
    }

    /**
     * return a bitboard with move squares by the pawn in the given square
     *
     * @param side   the side
     * @param square the square
     * @return the pawn attacks
     */
    public static long getPawnAttacks(Side side, Square square) {
        return (side.equals(Side.WHITE) ?
                whitePawnAttacks[square.ordinal()] :
                blackPawnAttacks[square.ordinal()]);
    }

    /**
     * return a bitboard with move squares by the pawn in the given square
     *
     * @param side      the side
     * @param square    the square
     * @param occupied  the occupied
     * @param enPassant the en passant
     * @return the pawn captures
     */
    public static long getPawnCaptures(Side side, Square square,
                                       long occupied, Square enPassant) {
        long pawnAttacks = (side.equals(Side.WHITE) ?
                whitePawnAttacks[square.ordinal()] :
                blackPawnAttacks[square.ordinal()]);
        if (!enPassant.equals(Square.NONE)) {
            long ep = enPassant.getBitboard();
            occupied |= side.equals(Side.WHITE) ? ep << 8L : ep >> 8L;
        }
        return pawnAttacks & occupied;
    }


    /**
     * return a bitboard with move squares by the pawn in the given square
     *
     * @param side     the side
     * @param square   the square
     * @param occupied the occupied
     * @return the pawn moves
     */
    public static long getPawnMoves(Side side, Square square, long occupied) {

        long pawnMoves = (side.equals(Side.WHITE) ?
                whitePawnMoves[square.ordinal()] : blackPawnMoves[square.ordinal()]);
        long occ = occupied;

        if (square.getRank().equals(Rank.RANK_2) &&
                side.equals(Side.WHITE)) {
            if ((square.getBitboard() << 8 & occ) != 0L) {
                occ |= square.getBitboard() << 16; // double move
            }
        } else if (square.getRank().equals(Rank.RANK_7) &&
                side.equals(Side.BLACK)) {
            if ((square.getBitboard() >> 8 & occ) != 0L) {
                occ |= square.getBitboard() >> 16; // double move
            }
        }
        return pawnMoves & ~occ;
    }

    /**
     * return a bitboard with attacked squares by the King in the given square
     *
     * @param square   the square
     * @param occupied the occupied
     * @return the king attacks
     */
    public static long getKingAttacks(Square square, long occupied) {
        return adjacentSquares[square.ordinal()] & occupied;
    }

    /**
     * Converts a bitboard to a list of squares
     *
     * @param pieces the pieces
     * @return List of Square
     */
    public static List<Square> bbToSquareList(long pieces) {
        List<Square> squares = new LinkedList<Square>();
        while (pieces != 0L) {
            int sq = Bitboard.bitScanForward(pieces);
            pieces = Bitboard.extractLsb(pieces);
            squares.add(Square.squareAt(sq));
        }
        return squares;
    }

    /**
     * Converts bitboard to array of squares
     *
     * @param pieces the pieces
     * @return array of squares
     */
    public static Square[] bbToSquareArray(long pieces) {
        Square[] squares = new Square[Long.bitCount(pieces)];
        int index = 0;
        while (pieces != 0L) {
            int sq = bitScanForward(pieces);
            pieces = extractLsb(pieces);
            squares[index++] = Square.squareAt(sq);
        }
        return squares;
    }

    /**
     * Get rankbb long [ ].
     *
     * @return the rankbb
     */
    public static long[] getRankbb() {
        return rankBB;
    }

    /**
     * Get filebb long [ ].
     *
     * @return the filebb
     */
    public static long[] getFilebb() {
        return fileBB;
    }

    /**
     * Gets rankbb.
     *
     * @param sq the sq
     * @return the rankbb
     */
    public static long getRankbb(Square sq) {
        return rankBB[sq.getRank().ordinal()];
    }

    /**
     * Gets filebb.
     *
     * @param sq the sq
     * @return the filebb
     */
    public static long getFilebb(Square sq) {
        return fileBB[sq.getFile().ordinal()];
    }

    /**
     * Gets rankbb.
     *
     * @param rank the rank
     * @return the rankbb
     */
    public static long getRankbb(Rank rank) {
        return rankBB[rank.ordinal()];
    }

    /**
     * Gets filebb.
     *
     * @param file the file
     * @return the filebb
     */
    public static long getFilebb(File file) {
        return fileBB[file.ordinal()];
    }

    /**
     * print a bitboard in a readble form
     *
     * @param bb the bb
     * @return the string
     */
    public static String bitboardToString(long bb) {
        StringBuilder b = new StringBuilder();
        for (int x = 0; x < 64; x++) {
            if (((1L << x) & bb) != 0L) {
                b.append("1");
            } else {
                b.append("0");
            }
            if ((x + 1) % 8 == 0) {
                b.append("\n");
            }
        }
        return b.toString();
    }
}

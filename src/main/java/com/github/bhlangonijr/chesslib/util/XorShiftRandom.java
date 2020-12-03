package com.github.bhlangonijr.chesslib.util;

public class XorShiftRandom {

    private long seed;

    public XorShiftRandom(long seed) {
        this.seed = seed;
    }

    public XorShiftRandom() {
        this(System.nanoTime());
    }

    public long nextLong() {
        seed ^= seed >>> 12;
        seed ^= seed << 25;
        seed ^= seed >>> 27;
        return seed * 0x2545F4914F6CDD1DL;
    }
}

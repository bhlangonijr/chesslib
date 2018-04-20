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

package com.github.bhlangonijr.chesslib.game;

import com.github.bhlangonijr.chesslib.CastleRight;
import com.github.bhlangonijr.chesslib.Constants;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

import java.util.List;

public class GameContext {

    protected Move whiteoo;
    protected Move whiteooo;
    protected Move blackoo;
    protected Move blackooo;

    protected Move whiteRookoo;
    protected Move whiteRookooo;
    protected Move blackRookoo;
    protected Move blackRookooo;

    protected List<Square> whiteooSquares;
    protected List<Square> whiteoooSquares;
    protected List<Square> blackooSquares;
    protected List<Square> blackoooSquares;

    protected long whiteooSquaresBb;
    protected long whiteoooSquaresBb;
    protected long blackooSquaresBb;
    protected long blackoooSquaresBb;

    protected long whiteooAllSquaresBb;
    protected long whiteoooAllSquaresBb;
    protected long blackooAllSquaresBb;
    protected long blackoooAllSquaresBb;

    protected String startFEN;

    protected GameMode gameMode;
    protected VariationType variationType;
    protected Event event;

    public GameContext() {
        this(GameMode.HUMAN_VS_HUMAN, VariationType.NORMAL);
    }

    public GameContext(GameMode gameMode, VariationType variationType) {
        setGameMode(gameMode);
        setVariationType(variationType);
        if (variationType.equals(VariationType.NORMAL)) {
            loadDefaults();
        }
    }

    private void loadDefaults() {
        //load standard values
        setWhiteoo(Constants.DEFAULT_WHITE_OO);
        setWhiteooo(Constants.DEFAULT_WHITE_OOO);
        setBlackoo(Constants.DEFAULT_BLACK_OO);
        setBlackooo(Constants.DEFAULT_BLACK_OOO);

        setWhiteRookoo(Constants.DEFAULT_WHITE_ROOK_OO);
        setWhiteRookooo(Constants.DEFAULT_WHITE_ROOK_OOO);
        setBlackRookoo(Constants.DEFAULT_BLACK_ROOK_OO);
        setBlackRookooo(Constants.DEFAULT_BLACK_ROOK_OOO);

        setWhiteooSquares(Constants.DEFAULT_WHITE_OO_SQUARES);
        setWhiteoooSquares(Constants.DEFAULT_WHITE_OOO_SQUARES);
        setBlackooSquares(Constants.DEFAULT_BLACK_OO_SQUARES);
        setBlackoooSquares(Constants.DEFAULT_BLACK_OOO_SQUARES);

        setWhiteooSquaresBb(squareListToBb(Constants.DEFAULT_WHITE_OO_SQUARES));
        setWhiteoooSquaresBb(squareListToBb(Constants.DEFAULT_WHITE_OOO_SQUARES));
        setBlackooSquaresBb(squareListToBb(Constants.DEFAULT_BLACK_OO_SQUARES));
        setBlackoooSquaresBb(squareListToBb(Constants.DEFAULT_BLACK_OOO_SQUARES));

        setWhiteooAllSquaresBb(squareListToBb(Constants.DEFAULT_WHITE_OO_ALL_SQUARES));
        setWhiteoooAllSquaresBb(squareListToBb(Constants.DEFAULT_WHITE_OOO_ALL_SQUARES));
        setBlackooAllSquaresBb(squareListToBb(Constants.DEFAULT_BLACK_OO_ALL_SQUARES));
        setBlackoooAllSquaresBb(squareListToBb(Constants.DEFAULT_BLACK_OOO_ALL_SQUARES));

        setStartFEN(Constants.startStandardFENPosition);
    }

    private static long squareListToBb(List<Square> list) {
        long r = 0L;
        for (Square s : list) {
            r |= s.getBitboard();
        }
        return r;
    }

    /**
     * gets the king castle move
     *
     * @param side
     * @param castleRight
     * @return
     */
    public Move getKingCastleMove(Side side, CastleRight castleRight) {
        Move move = null;
        if (Side.WHITE.equals(side)) {
            if (CastleRight.KING_SIDE.equals(castleRight)) {
                move = getWhiteoo();
            } else if (CastleRight.QUEEN_SIDE.equals(castleRight)) {
                move = getWhiteooo();
            }
        } else {
            if (CastleRight.KING_SIDE.equals(castleRight)) {
                move = getBlackoo();
            } else if (CastleRight.QUEEN_SIDE.equals(castleRight)) {
                move = getBlackooo();
            }
        }
        return move;
    }

    /**
     * gets the rook castle move
     *
     * @param side
     * @param castleRight
     * @return
     */
    public Move getRookCastleMove(Side side, CastleRight castleRight) {
        Move move = null;
        if (Side.WHITE.equals(side)) {
            if (CastleRight.KING_SIDE.equals(castleRight)) {
                move = getWhiteRookoo();
            } else if (CastleRight.QUEEN_SIDE.equals(castleRight)) {
                move = getWhiteRookooo();
            }
        } else {
            if (CastleRight.KING_SIDE.equals(castleRight)) {
                move = getBlackRookoo();
            } else if (CastleRight.QUEEN_SIDE.equals(castleRight)) {
                move = getBlackRookooo();
            }
        }
        return move;
    }

    /**
     * @param move
     * @return true if move is a castle one
     */
    public boolean isCastleMove(final Move move) {
        return move.equals(getWhiteoo()) ||
                move.equals(getWhiteooo()) ||
                move.equals(getBlackoo()) ||
                move.equals(getBlackooo());
    }

    /**
     * If castle move is a valid one
     *
     * @param move
     * @return
     */
    public boolean hasCastleRight(final Move move, final CastleRight castleRight) {

        final CastleRight r = castleRight;

        return (CastleRight.KING_AND_QUEEN_SIDE.equals(r)) ||
                (move.equals(getWhiteoo()) && CastleRight.KING_SIDE.equals(r)) ||
                (move.equals(getBlackoo()) && CastleRight.KING_SIDE.equals(r)) ||
                (move.equals(getWhiteooo()) && CastleRight.QUEEN_SIDE.equals(r)) ||
                (move.equals(getBlackooo()) && CastleRight.QUEEN_SIDE.equals(r));
    }

    /**
     * is King side castle
     *
     * @param move
     * @return
     */
    public boolean isKingSideCastle(Move move) {
        return move.equals(getWhiteoo()) ||
                move.equals(getBlackoo());
    }

    /**
     * is queen side castle
     *
     * @param move
     * @return
     */
    public boolean isQueenSideCastle(Move move) {
        return move.equals(getWhiteooo()) ||
                move.equals(getBlackooo());
    }

    public Move getWhiteoo() {
        return whiteoo;
    }

    public void setWhiteoo(Move whiteoo) {
        this.whiteoo = whiteoo;
    }

    public Move getWhiteooo() {
        return whiteooo;
    }

    public void setWhiteooo(Move whiteooo) {
        this.whiteooo = whiteooo;
    }

    public Move getBlackoo() {
        return blackoo;
    }

    public void setBlackoo(Move blackoo) {
        this.blackoo = blackoo;
    }

    public Move getBlackooo() {
        return blackooo;
    }

    public void setBlackooo(Move blackooo) {
        this.blackooo = blackooo;
    }

    public Move getWhiteRookoo() {
        return whiteRookoo;
    }

    public void setWhiteRookoo(Move whiteRookoo) {
        this.whiteRookoo = whiteRookoo;
    }

    public Move getWhiteRookooo() {
        return whiteRookooo;
    }

    public void setWhiteRookooo(Move whiteRookooo) {
        this.whiteRookooo = whiteRookooo;
    }

    public Move getBlackRookoo() {
        return blackRookoo;
    }

    public void setBlackRookoo(Move blackRookoo) {
        this.blackRookoo = blackRookoo;
    }

    public Move getBlackRookooo() {
        return blackRookooo;
    }

    public void setBlackRookooo(Move blackRookooo) {
        this.blackRookooo = blackRookooo;
    }

    public String getStartFEN() {
        return startFEN;
    }

    public void setStartFEN(String startFEN) {
        this.startFEN = startFEN;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * @return the variationType
     */
    public VariationType getVariationType() {
        return variationType;
    }

    /**
     * @param variationType the variationType to set
     */
    public void setVariationType(VariationType variationType) {
        this.variationType = variationType;
    }

    public List<Square> getWhiteooSquares() {
        return whiteooSquares;
    }

    public void setWhiteooSquares(List<Square> whiteooSquares) {
        this.whiteooSquares = whiteooSquares;
    }

    public List<Square> getWhiteoooSquares() {
        return whiteoooSquares;
    }

    public void setWhiteoooSquares(List<Square> whiteoooSquares) {
        this.whiteoooSquares = whiteoooSquares;
    }

    public List<Square> getBlackooSquares() {
        return blackooSquares;
    }

    public void setBlackooSquares(List<Square> blackooSquares) {
        this.blackooSquares = blackooSquares;
    }

    public List<Square> getBlackoooSquares() {
        return blackoooSquares;
    }

    public void setBlackoooSquares(List<Square> blackoooSquares) {
        this.blackoooSquares = blackoooSquares;
    }

    public long getWhiteooSquaresBb() {
        return whiteooSquaresBb;
    }

    public void setWhiteooSquaresBb(long whiteooSquaresBb) {
        this.whiteooSquaresBb = whiteooSquaresBb;
    }

    public long getWhiteoooSquaresBb() {
        return whiteoooSquaresBb;
    }

    public void setWhiteoooSquaresBb(long whiteoooSquaresBb) {
        this.whiteoooSquaresBb = whiteoooSquaresBb;
    }

    public long getBlackooSquaresBb() {
        return blackooSquaresBb;
    }

    public void setBlackooSquaresBb(long blackooSquaresBb) {
        this.blackooSquaresBb = blackooSquaresBb;
    }

    public long getBlackoooSquaresBb() {
        return blackoooSquaresBb;
    }

    public void setBlackoooSquaresBb(long blackoooSquaresBb) {
        this.blackoooSquaresBb = blackoooSquaresBb;
    }

    public long getWhiteooAllSquaresBb() {
        return whiteooAllSquaresBb;
    }

    public GameContext setWhiteooAllSquaresBb(long whiteooAllSquaresBb) {
        this.whiteooAllSquaresBb = whiteooAllSquaresBb;
        return this;
    }

    public long getWhiteoooAllSquaresBb() {
        return whiteoooAllSquaresBb;
    }

    public GameContext setWhiteoooAllSquaresBb(long whiteoooAllSquaresBb) {
        this.whiteoooAllSquaresBb = whiteoooAllSquaresBb;
        return this;
    }

    public long getBlackooAllSquaresBb() {
        return blackooAllSquaresBb;
    }

    public GameContext setBlackooAllSquaresBb(long blackooAllSquaresBb) {
        this.blackooAllSquaresBb = blackooAllSquaresBb;
        return this;
    }

    public long getBlackoooAllSquaresBb() {
        return blackoooAllSquaresBb;
    }

    public GameContext setBlackoooAllSquaresBb(long blackoooAllSquaresBb) {
        this.blackoooAllSquaresBb = blackoooAllSquaresBb;
        return this;
    }

    public Move getoo(Side side) {
        return Side.WHITE.equals(side) ? getWhiteoo() : getBlackoo();
    }

    public Move getooo(Side side) {
        return Side.WHITE.equals(side) ? getWhiteooo() : getBlackooo();
    }

    public Move getRookoo(Side side) {
        return Side.WHITE.equals(side) ? getWhiteRookoo() : getBlackRookoo();
    }

    public Move getRookooo(Side side) {
        return Side.WHITE.equals(side) ? getWhiteRookooo() : getBlackRookooo();
    }

    public List<Square> getooSquares(Side side) {
        return Side.WHITE.equals(side) ?
                getWhiteooSquares() : getBlackooSquares();
    }

    public List<Square> getoooSquares(Side side) {
        return Side.WHITE.equals(side) ?
                getWhiteoooSquares() : getBlackoooSquares();
    }

    public long getooSquaresBb(Side side) {
        return Side.WHITE.equals(side) ?
                getWhiteooSquaresBb() : getBlackooSquaresBb();
    }

    public long getoooSquaresBb(Side side) {
        return Side.WHITE.equals(side) ?
                getWhiteoooSquaresBb() : getBlackoooSquaresBb();
    }

    public long getooAllSquaresBb(Side side) {
        return Side.WHITE.equals(side) ?
                getWhiteooAllSquaresBb() : getBlackooAllSquaresBb();
    }

    public long getoooAllSquaresBb(Side side) {
        return Side.WHITE.equals(side) ?
                getWhiteoooAllSquaresBb() : getBlackoooAllSquaresBb();
    }

    /**
     * @return the event
     */

    public Event getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }
}

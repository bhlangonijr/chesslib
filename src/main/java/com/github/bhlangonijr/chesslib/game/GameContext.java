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

package com.github.bhlangonijr.chesslib.game;

import com.github.bhlangonijr.chesslib.CastleRight;
import com.github.bhlangonijr.chesslib.Constants;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

import java.util.List;

/**
 * The type Game context.
 */
public class GameContext {

    /**
     * The Whiteoo.
     */
    protected Move whiteoo;
    /**
     * The Whiteooo.
     */
    protected Move whiteooo;
    /**
     * The Blackoo.
     */
    protected Move blackoo;
    /**
     * The Blackooo.
     */
    protected Move blackooo;

    /**
     * The White rookoo.
     */
    protected Move whiteRookoo;
    /**
     * The White rookooo.
     */
    protected Move whiteRookooo;
    /**
     * The Black rookoo.
     */
    protected Move blackRookoo;
    /**
     * The Black rookooo.
     */
    protected Move blackRookooo;

    /**
     * The Whiteoo squares.
     */
    protected List<Square> whiteooSquares;
    /**
     * The Whiteooo squares.
     */
    protected List<Square> whiteoooSquares;
    /**
     * The Blackoo squares.
     */
    protected List<Square> blackooSquares;
    /**
     * The Blackooo squares.
     */
    protected List<Square> blackoooSquares;

    /**
     * The Whiteoo squares bb.
     */
    protected long whiteooSquaresBb;
    /**
     * The Whiteooo squares bb.
     */
    protected long whiteoooSquaresBb;
    /**
     * The Blackoo squares bb.
     */
    protected long blackooSquaresBb;
    /**
     * The Blackooo squares bb.
     */
    protected long blackoooSquaresBb;

    /**
     * The Whiteoo all squares bb.
     */
    protected long whiteooAllSquaresBb;
    /**
     * The Whiteooo all squares bb.
     */
    protected long whiteoooAllSquaresBb;
    /**
     * The Blackoo all squares bb.
     */
    protected long blackooAllSquaresBb;
    /**
     * The Blackooo all squares bb.
     */
    protected long blackoooAllSquaresBb;

    /**
     * The Start fen.
     */
    protected String startFEN;

    /**
     * The Game mode.
     */
    protected GameMode gameMode;
    /**
     * The Variation type.
     */
    protected VariationType variationType;
    /**
     * The Event.
     */
    protected Event event;

    /**
     * Instantiates a new Game context.
     */
    public GameContext() {
        this(GameMode.HUMAN_VS_HUMAN, VariationType.NORMAL);
    }

    /**
     * Instantiates a new Game context.
     *
     * @param gameMode      the game mode
     * @param variationType the variation type
     */
    public GameContext(GameMode gameMode, VariationType variationType) {
        setGameMode(gameMode);
        setVariationType(variationType);
        if (variationType.equals(VariationType.NORMAL)) {
            loadDefaults();
        }
    }

    private static long squareListToBb(List<Square> list) {
        long r = 0L;
        for (Square s : list) {
            r |= s.getBitboard();
        }
        return r;
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

    /**
     * gets the king castle move
     *
     * @param side        the side
     * @param castleRight the castle right
     * @return king castle move
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
     * @param side        the side
     * @param castleRight the castle right
     * @return rook castle move
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
     * Is castle move boolean.
     *
     * @param move the move
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
     * @param move        the move
     * @param castleRight the castle right
     * @return boolean
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
     * @param move the move
     * @return boolean
     */
    public boolean isKingSideCastle(Move move) {
        return move.equals(getWhiteoo()) ||
                move.equals(getBlackoo());
    }

    /**
     * is queen side castle
     *
     * @param move the move
     * @return boolean
     */
    public boolean isQueenSideCastle(Move move) {
        return move.equals(getWhiteooo()) ||
                move.equals(getBlackooo());
    }

    /**
     * Gets whiteoo.
     *
     * @return the whiteoo
     */
    public Move getWhiteoo() {
        return whiteoo;
    }

    /**
     * Sets whiteoo.
     *
     * @param whiteoo the whiteoo
     */
    public void setWhiteoo(Move whiteoo) {
        this.whiteoo = whiteoo;
    }

    /**
     * Gets whiteooo.
     *
     * @return the whiteooo
     */
    public Move getWhiteooo() {
        return whiteooo;
    }

    /**
     * Sets whiteooo.
     *
     * @param whiteooo the whiteooo
     */
    public void setWhiteooo(Move whiteooo) {
        this.whiteooo = whiteooo;
    }

    /**
     * Gets blackoo.
     *
     * @return the blackoo
     */
    public Move getBlackoo() {
        return blackoo;
    }

    /**
     * Sets blackoo.
     *
     * @param blackoo the blackoo
     */
    public void setBlackoo(Move blackoo) {
        this.blackoo = blackoo;
    }

    /**
     * Gets blackooo.
     *
     * @return the blackooo
     */
    public Move getBlackooo() {
        return blackooo;
    }

    /**
     * Sets blackooo.
     *
     * @param blackooo the blackooo
     */
    public void setBlackooo(Move blackooo) {
        this.blackooo = blackooo;
    }

    /**
     * Gets white rookoo.
     *
     * @return the white rookoo
     */
    public Move getWhiteRookoo() {
        return whiteRookoo;
    }

    /**
     * Sets white rookoo.
     *
     * @param whiteRookoo the white rookoo
     */
    public void setWhiteRookoo(Move whiteRookoo) {
        this.whiteRookoo = whiteRookoo;
    }

    /**
     * Gets white rookooo.
     *
     * @return the white rookooo
     */
    public Move getWhiteRookooo() {
        return whiteRookooo;
    }

    /**
     * Sets white rookooo.
     *
     * @param whiteRookooo the white rookooo
     */
    public void setWhiteRookooo(Move whiteRookooo) {
        this.whiteRookooo = whiteRookooo;
    }

    /**
     * Gets black rookoo.
     *
     * @return the black rookoo
     */
    public Move getBlackRookoo() {
        return blackRookoo;
    }

    /**
     * Sets black rookoo.
     *
     * @param blackRookoo the black rookoo
     */
    public void setBlackRookoo(Move blackRookoo) {
        this.blackRookoo = blackRookoo;
    }

    /**
     * Gets black rookooo.
     *
     * @return the black rookooo
     */
    public Move getBlackRookooo() {
        return blackRookooo;
    }

    /**
     * Sets black rookooo.
     *
     * @param blackRookooo the black rookooo
     */
    public void setBlackRookooo(Move blackRookooo) {
        this.blackRookooo = blackRookooo;
    }

    /**
     * Gets start fen.
     *
     * @return the start fen
     */
    public String getStartFEN() {
        return startFEN;
    }

    /**
     * Sets start fen.
     *
     * @param startFEN the start fen
     */
    public void setStartFEN(String startFEN) {
        this.startFEN = startFEN;
    }

    /**
     * Gets game mode.
     *
     * @return the game mode
     */
    public GameMode getGameMode() {
        return gameMode;
    }

    /**
     * Sets game mode.
     *
     * @param gameMode the game mode
     */
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * Gets variation type.
     *
     * @return the variationType
     */
    public VariationType getVariationType() {
        return variationType;
    }

    /**
     * Sets variation type.
     *
     * @param variationType the variationType to set
     */
    public void setVariationType(VariationType variationType) {
        this.variationType = variationType;
    }

    /**
     * Gets whiteoo squares.
     *
     * @return the whiteoo squares
     */
    public List<Square> getWhiteooSquares() {
        return whiteooSquares;
    }

    /**
     * Sets whiteoo squares.
     *
     * @param whiteooSquares the whiteoo squares
     */
    public void setWhiteooSquares(List<Square> whiteooSquares) {
        this.whiteooSquares = whiteooSquares;
    }

    /**
     * Gets whiteooo squares.
     *
     * @return the whiteooo squares
     */
    public List<Square> getWhiteoooSquares() {
        return whiteoooSquares;
    }

    /**
     * Sets whiteooo squares.
     *
     * @param whiteoooSquares the whiteooo squares
     */
    public void setWhiteoooSquares(List<Square> whiteoooSquares) {
        this.whiteoooSquares = whiteoooSquares;
    }

    /**
     * Gets blackoo squares.
     *
     * @return the blackoo squares
     */
    public List<Square> getBlackooSquares() {
        return blackooSquares;
    }

    /**
     * Sets blackoo squares.
     *
     * @param blackooSquares the blackoo squares
     */
    public void setBlackooSquares(List<Square> blackooSquares) {
        this.blackooSquares = blackooSquares;
    }

    /**
     * Gets blackooo squares.
     *
     * @return the blackooo squares
     */
    public List<Square> getBlackoooSquares() {
        return blackoooSquares;
    }

    /**
     * Sets blackooo squares.
     *
     * @param blackoooSquares the blackooo squares
     */
    public void setBlackoooSquares(List<Square> blackoooSquares) {
        this.blackoooSquares = blackoooSquares;
    }

    /**
     * Gets whiteoo squares bb.
     *
     * @return the whiteoo squares bb
     */
    public long getWhiteooSquaresBb() {
        return whiteooSquaresBb;
    }

    /**
     * Sets whiteoo squares bb.
     *
     * @param whiteooSquaresBb the whiteoo squares bb
     */
    public void setWhiteooSquaresBb(long whiteooSquaresBb) {
        this.whiteooSquaresBb = whiteooSquaresBb;
    }

    /**
     * Gets whiteooo squares bb.
     *
     * @return the whiteooo squares bb
     */
    public long getWhiteoooSquaresBb() {
        return whiteoooSquaresBb;
    }

    /**
     * Sets whiteooo squares bb.
     *
     * @param whiteoooSquaresBb the whiteooo squares bb
     */
    public void setWhiteoooSquaresBb(long whiteoooSquaresBb) {
        this.whiteoooSquaresBb = whiteoooSquaresBb;
    }

    /**
     * Gets blackoo squares bb.
     *
     * @return the blackoo squares bb
     */
    public long getBlackooSquaresBb() {
        return blackooSquaresBb;
    }

    /**
     * Sets blackoo squares bb.
     *
     * @param blackooSquaresBb the blackoo squares bb
     */
    public void setBlackooSquaresBb(long blackooSquaresBb) {
        this.blackooSquaresBb = blackooSquaresBb;
    }

    /**
     * Gets blackooo squares bb.
     *
     * @return the blackooo squares bb
     */
    public long getBlackoooSquaresBb() {
        return blackoooSquaresBb;
    }

    /**
     * Sets blackooo squares bb.
     *
     * @param blackoooSquaresBb the blackooo squares bb
     */
    public void setBlackoooSquaresBb(long blackoooSquaresBb) {
        this.blackoooSquaresBb = blackoooSquaresBb;
    }

    /**
     * Gets whiteoo all squares bb.
     *
     * @return the whiteoo all squares bb
     */
    public long getWhiteooAllSquaresBb() {
        return whiteooAllSquaresBb;
    }

    /**
     * Sets whiteoo all squares bb.
     *
     * @param whiteooAllSquaresBb the whiteoo all squares bb
     * @return the whiteoo all squares bb
     */
    public GameContext setWhiteooAllSquaresBb(long whiteooAllSquaresBb) {
        this.whiteooAllSquaresBb = whiteooAllSquaresBb;
        return this;
    }

    /**
     * Gets whiteooo all squares bb.
     *
     * @return the whiteooo all squares bb
     */
    public long getWhiteoooAllSquaresBb() {
        return whiteoooAllSquaresBb;
    }

    /**
     * Sets whiteooo all squares bb.
     *
     * @param whiteoooAllSquaresBb the whiteooo all squares bb
     * @return the whiteooo all squares bb
     */
    public GameContext setWhiteoooAllSquaresBb(long whiteoooAllSquaresBb) {
        this.whiteoooAllSquaresBb = whiteoooAllSquaresBb;
        return this;
    }

    /**
     * Gets blackoo all squares bb.
     *
     * @return the blackoo all squares bb
     */
    public long getBlackooAllSquaresBb() {
        return blackooAllSquaresBb;
    }

    /**
     * Sets blackoo all squares bb.
     *
     * @param blackooAllSquaresBb the blackoo all squares bb
     * @return the blackoo all squares bb
     */
    public GameContext setBlackooAllSquaresBb(long blackooAllSquaresBb) {
        this.blackooAllSquaresBb = blackooAllSquaresBb;
        return this;
    }

    /**
     * Gets blackooo all squares bb.
     *
     * @return the blackooo all squares bb
     */
    public long getBlackoooAllSquaresBb() {
        return blackoooAllSquaresBb;
    }

    /**
     * Sets blackooo all squares bb.
     *
     * @param blackoooAllSquaresBb the blackooo all squares bb
     * @return the blackooo all squares bb
     */
    public GameContext setBlackoooAllSquaresBb(long blackoooAllSquaresBb) {
        this.blackoooAllSquaresBb = blackoooAllSquaresBb;
        return this;
    }

    /**
     * Gets .
     *
     * @param side the side
     * @return the
     */
    public Move getoo(Side side) {
        return Side.WHITE.equals(side) ? getWhiteoo() : getBlackoo();
    }

    /**
     * Gets .
     *
     * @param side the side
     * @return the
     */
    public Move getooo(Side side) {
        return Side.WHITE.equals(side) ? getWhiteooo() : getBlackooo();
    }

    /**
     * Gets rookoo.
     *
     * @param side the side
     * @return the rookoo
     */
    public Move getRookoo(Side side) {
        return Side.WHITE.equals(side) ? getWhiteRookoo() : getBlackRookoo();
    }

    /**
     * Gets rookooo.
     *
     * @param side the side
     * @return the rookooo
     */
    public Move getRookooo(Side side) {
        return Side.WHITE.equals(side) ? getWhiteRookooo() : getBlackRookooo();
    }

    /**
     * Gets squares.
     *
     * @param side the side
     * @return the squares
     */
    public List<Square> getooSquares(Side side) {
        return Side.WHITE.equals(side) ?
                getWhiteooSquares() : getBlackooSquares();
    }

    /**
     * Gets squares.
     *
     * @param side the side
     * @return the squares
     */
    public List<Square> getoooSquares(Side side) {
        return Side.WHITE.equals(side) ?
                getWhiteoooSquares() : getBlackoooSquares();
    }

    /**
     * Gets squares bb.
     *
     * @param side the side
     * @return the squares bb
     */
    public long getooSquaresBb(Side side) {
        return Side.WHITE.equals(side) ?
                getWhiteooSquaresBb() : getBlackooSquaresBb();
    }

    /**
     * Gets squares bb.
     *
     * @param side the side
     * @return the squares bb
     */
    public long getoooSquaresBb(Side side) {
        return Side.WHITE.equals(side) ?
                getWhiteoooSquaresBb() : getBlackoooSquaresBb();
    }

    /**
     * Gets all squares bb.
     *
     * @param side the side
     * @return the all squares bb
     */
    public long getooAllSquaresBb(Side side) {
        return Side.WHITE.equals(side) ?
                getWhiteooAllSquaresBb() : getBlackooAllSquaresBb();
    }

    /**
     * Gets all squares bb.
     *
     * @param side the side
     * @return the all squares bb
     */
    public long getoooAllSquaresBb(Side side) {
        return Side.WHITE.equals(side) ?
                getWhiteoooAllSquaresBb() : getBlackoooAllSquaresBb();
    }

    /**
     * Gets event.
     *
     * @return the event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Sets event.
     *
     * @param event the event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }
}

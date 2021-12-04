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

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.pgn.PgnException;
import com.github.bhlangonijr.chesslib.util.StringUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Game Data Holder
 */
public class Game {

    private final Round round;
    private String gameId;
    private String date;
    private String time;
    private Termination termination;
    private Player whitePlayer;
    private Player blackPlayer;
    private String annotator;
    private String plyCount;
    private GameResult result;
    private MoveList halfMoves = new MoveList();
    private Map<Integer, MoveList> variations;
    private Map<Integer, String> comments;
    private Map<Integer, String> nag;
    private Map<String, String> property;

    private String fen;
    private Board board;
    private int position;
    private int initialPosition; // when loaded from FEN
    private MoveList currentMoveList;
    private String eco;
    private StringBuilder moveText;
    private String opening;
    private String variation;

    /**
     * Instantiates a new Game.
     *
     * @param gameId the game id
     * @param round  the round
     */
    public Game(String gameId, Round round) {
        this.gameId = gameId;
        this.round = round;
        this.result = GameResult.ONGOING;
        this.initialPosition = 0;
        this.setPosition(0);
    }

    private static String makeProp(String name, String value) {
        return "[" + name + " \"" + value + "\"]\n";
    }

    private static String getMovesAt(String moves, int index) {
        StringBuilder b = new StringBuilder();
        int count = 0;
        for (String m : moves.split(" ")) {
            count++;
            if (count >= index) {
                break;
            }
            b.append(m);
            b.append(' ');
        }
        return b.toString();
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets time.
     *
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets time.
     *
     * @param time the time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Gets termination.
     *
     * @return the termination
     */
    public Termination getTermination() {
        return termination;
    }

    /**
     * Sets termination.
     *
     * @param termination the termination
     */
    public void setTermination(Termination termination) {
        this.termination = termination;
    }

    /**
     * Gets white player.
     *
     * @return the white player
     */
    public Player getWhitePlayer() {
        return whitePlayer;
    }

    /**
     * Sets white player.
     *
     * @param whitePlayer the white player
     */
    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    /**
     * Gets black player.
     *
     * @return the black player
     */
    public Player getBlackPlayer() {
        return blackPlayer;
    }

    /**
     * Sets black player.
     *
     * @param blackPlayer the black player
     */
    public void setBlackPlayer(Player blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    /**
     * Gets annotator.
     *
     * @return the annotator
     */
    public String getAnnotator() {
        return annotator;
    }

    /**
     * Sets annotator.
     *
     * @param annotator the annotator
     */
    public void setAnnotator(String annotator) {
        this.annotator = annotator;
    }

    /**
     * Gets ply count.
     *
     * @return the ply count
     */
    public String getPlyCount() {
        return plyCount;
    }

    /**
     * Sets ply count.
     *
     * @param plyCount the ply count
     */
    public void setPlyCount(String plyCount) {
        this.plyCount = plyCount;
    }

    /**
     * Gets result.
     *
     * @return the result
     */
    public GameResult getResult() {
        return result;
    }

    /**
     * Sets result.
     *
     * @param result the result
     */
    public void setResult(GameResult result) {
        this.result = result;
    }

    /**
     * Gets variations.
     *
     * @return the variations
     */
    public Map<Integer, MoveList> getVariations() {
        return variations;
    }

    /**
     * Sets variations.
     *
     * @param variations the variations
     */
    public void setVariations(Map<Integer, MoveList> variations) {
        this.variations = variations;
    }

    /**
     * Gets commentary.
     *
     * @return the commentary
     */
    public Map<Integer, String> getComments() {
        return comments;
    }

    /**
     * Sets commentary.
     *
     * @param comments the commentary
     */
    public void setComments(Map<Integer, String> comments) {
        this.comments = comments;
    }

    /**
     * Gets nag.
     *
     * @return the nag
     */
    public Map<Integer, String> getNag() {
        return nag;
    }

    /**
     * Sets nag.
     *
     * @param nag the nag
     */
    public void setNag(Map<Integer, String> nag) {
        this.nag = nag;
    }

    /**
     * Gets half moves.
     *
     * @return the halfMoves
     */
    public MoveList getHalfMoves() {
        if (halfMoves == null) {
            if (getFen() != null && !getFen().trim().equals("")) {
                halfMoves = new MoveList(getFen());
            } else {
                halfMoves = new MoveList();
            }
        }
        return halfMoves;
    }

    /**
     * Sets half moves.
     *
     * @param halfMoves the half moves
     */
    public void setHalfMoves(MoveList halfMoves) {
        this.halfMoves = halfMoves;
        setCurrentMoveList(halfMoves);
    }

    /**
     * Gets fen.
     *
     * @return the fen
     */
    public String getFen() {
        return fen;
    }

    /**
     * Sets fen.
     *
     * @param fen the fen to set
     */
    public void setFen(String fen) {
        this.fen = fen;
    }

    /**
     * Gets board.
     *
     * @return the board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Sets board.
     *
     * @param board the board to set
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Gets round.
     *
     * @return the round
     */
    public Round getRound() {
        return round;
    }

    /**
     * Convert the Game object to the PGN format
     *
     * @param includeVariations the include variations
     * @param includeComments   the include comments
     * @return string
     * @throws MoveConversionException the move conversion exception
     */
    public String toPgn(boolean includeVariations, boolean includeComments) throws MoveConversionException {
        StringBuilder sb = new StringBuilder();

        sb.append(makeProp("Event", getRound().getEvent().getName()));
        sb.append(makeProp("Site", getRound().getEvent().getSite()));
        sb.append(makeProp("Date", getRound().getEvent().getStartDate()));
        sb.append(makeProp("Round", getRound().getNumber() + ""));
        sb.append(makeProp("White", getWhitePlayer().getName()));
        sb.append(makeProp("Black", getBlackPlayer().getName()));
        sb.append(makeProp("Result", getResult().getDescription()));
        sb.append(makeProp("PlyCount", getPlyCount()));
        if (getTermination() != null) {
            sb.append(makeProp("Termination", getTermination().toString().toLowerCase()));
        }
        if (getRound().getEvent().getTimeControl() != null) {
            sb.append(makeProp("TimeControl", getRound().getEvent().getTimeControl().toPGNString()));
        } else {
            sb.append(makeProp("TimeControl", "-"));
        }
        if (getAnnotator() != null && !getAnnotator().equals("")) {
            sb.append(makeProp("Annotator", getAnnotator()));
        }
        if (getFen() != null && !getFen().equals("")) {
            sb.append(makeProp("FEN", getFen()));
        }
        if (getEco() != null && !getEco().equals("")) {
            sb.append(makeProp("ECO", getEco()));
        }
        if (getOpening() != null && !getOpening().equals("")) {
            sb.append(makeProp("Opening", getOpening()));
        }
        if (getWhitePlayer().getElo() > 0) {
            sb.append(makeProp("WhiteElo", getWhitePlayer().getElo() + ""));
        }
        if (getBlackPlayer().getElo() > 0) {
            sb.append(makeProp("BlackElo", getBlackPlayer().getElo() + ""));
        }
        if (getProperty() != null) {
            for (Entry<String, String> entry : getProperty().entrySet()) {
                sb.append(makeProp(entry.getKey(), entry.getValue()));
            }
        }

        sb.append('\n');
        int index = 0;
        int moveCounter = getInitialPosition() + 1;
        int variantIndex = 0;
        int lastSize = sb.length();

        if (getHalfMoves().size() == 0) {
            sb.append(getMoveText().toString());
        } else {
            sb.append(moveCounter);
            if (moveCounter % 2 == 0) {
                sb.append(".. ");
            } else {
                sb.append(". ");
            }
            final String[] sanArray = getHalfMoves().toSanArray();
            for (int i = 0; i < sanArray.length; i++) {
                String san = sanArray[i];
                index++;
                variantIndex++;

                sb.append(san);
                sb.append(' ');

                if (sb.length() - lastSize > 80) {
                    sb.append("\n");
                    lastSize = sb.length();
                }
                if (getNag() != null) {
                    String nag = getNag().get(variantIndex);
                    if (nag != null) {
                        sb.append(nag);
                        sb.append(' ');
                    }
                }

                if (getComments() != null) {
                    String comment = getComments().get(variantIndex);
                    if (comment != null) {
                        sb.append("{");
                        sb.append(comment);
                        sb.append("}");
                    }
                }
                if (getVariations() != null) {
                    MoveList var = getVariations().get(variantIndex);
                    if (var != null) {
                        variantIndex = translateVariation(sb, var, -1,
                                variantIndex, index, moveCounter, lastSize);
                        if (index % 2 != 0) {
                            sb.append(moveCounter);
                            sb.append("... ");
                        }
                    }
                }
                if (i < sanArray.length - 1 &&
                        index % 2 == 0 && index >= 2) {
                    moveCounter++;

                    sb.append(moveCounter);
                    sb.append(". ");
                }
            }
        }
        sb.append(getResult().getDescription());
        return sb.toString();


    }

    private int translateVariation(StringBuilder sb, MoveList variation, int parent,
                                   int variantIndex, int index, int moveCounter, int lastSize) throws MoveConversionException {
        final int variantIndexOld = variantIndex;
        if (variation != null) {
            boolean terminated = false;
            sb.append("(");
            int i = 0;
            int mc = moveCounter;
            int idx = index;
            String[] sanArray = variation.toSanArray();
            for (i = 0; i < sanArray.length; i++) {
                String sanMove = sanArray[i];
                if (i == 0) {
                    sb.append(mc);
                    if (idx % 2 == 0) {
                        sb.append("... ");
                    } else {
                        sb.append(". ");
                    }
                }

                variantIndex++;

                sb.append(sanMove);
                sb.append(' ');
                final MoveList child = getVariations().get(variantIndex);
                if (child != null) {
                    if (i == sanArray.length - 1 &&
                            variantIndexOld != child.getParent()) {
                        terminated = true;
                        sb.append(") ");
                    }
                    variantIndex = translateVariation(sb, child, variantIndexOld,
                            variantIndex, idx, mc, lastSize);
                }
                if (idx % 2 == 0 && idx >= 2
                        && i < sanArray.length - 1) {
                    mc++;

                    sb.append(mc);
                    sb.append(". ");
                }
                idx++;

            }
            if (!terminated) {
                sb.append(") ");
            }

        }
        return variantIndex;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        try {
            return toPgn(true, true);
        } catch (MoveConversionException e) {
            return null;
        }
    }

    /**
     * Gets game id.
     *
     * @return the gameId
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * Sets game id.
     *
     * @param gameId the gameId to set
     */
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    /**
     * Gets position.
     *
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets position.
     *
     * @param position the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Gets initial position.
     *
     * @return the initial position
     */
    public int getInitialPosition() {
        return initialPosition;
    }

    /**
     * Sets initial position.
     *
     * @param initialPosition the initial position
     */
    public void setInitialPosition(int initialPosition) {
        this.initialPosition = initialPosition;
    }

    /**
     * Gets current move list.
     *
     * @return the currentMoveList
     */
    public MoveList getCurrentMoveList() {
        return currentMoveList;
    }

    /**
     * Sets current move list.
     *
     * @param currentMoveList the currentMoveList to set
     */
    public void setCurrentMoveList(MoveList currentMoveList) {
        this.currentMoveList = currentMoveList;
    }

    /**
     * Gets eco.
     *
     * @return the eco
     */
    public String getEco() {
        return eco;
    }

    /**
     * Sets eco.
     *
     * @param eco the eco to set
     */
    public void setEco(String eco) {
        this.eco = eco;
    }

    /**
     * Gets opening.
     *
     * @return the opening
     */
    public String getOpening() {
        return opening;
    }

    /**
     * Sets opening.
     *
     * @param opening the opening
     */
    public void setOpening(String opening) {
        this.opening = opening;
    }

    /**
     * Gets variation.
     *
     * @return the variation
     */
    public String getVariation() {
        return variation;
    }

    /**
     * Sets variation.
     *
     * @param variation the variation
     */
    public void setVariation(String variation) {
        this.variation = variation;
    }

    /**
     * Gets move text.
     *
     * @return the moveText
     */
    public StringBuilder getMoveText() {
        return moveText;
    }

    /**
     * Sets move text.
     *
     * @param moveText the moveText to set
     */
    public void setMoveText(StringBuilder moveText) {
        this.moveText = moveText;
    }

    /**
     * Load a MoveText from a PGN file into the Game object
     *
     * @throws Exception the exception
     */
    public void loadMoveText() throws Exception {
        if (getMoveText() != null) {
            loadMoveText(getMoveText());
        }
    }

    /**
     * Load a MoveText from a PGN file into the Game object
     *
     * @param moveText the move text
     * @throws Exception the exception
     */
    public void loadMoveText(StringBuilder moveText) throws Exception {

        if (getVariations() != null) {
            getVariations().clear();
        }
        if (getComments() != null) {
            getComments().clear();
        }
        if (getNag() != null) {
            getNag().clear();
        }

        StringUtil.replaceAll(moveText, "\n", " \n ");
        StringUtil.replaceAll(moveText, "{", " { ");
        StringUtil.replaceAll(moveText, "}", " } ");
        StringUtil.replaceAll(moveText, "(", " ( ");
        StringUtil.replaceAll(moveText, ")", " ) ");

        String text = moveText.toString();

        if (getFen() != null && !getFen().trim().equals("")) {
            setHalfMoves(new MoveList(getFen()));
        } else {
            setHalfMoves(new MoveList());
        }

        StringBuilder moves = new StringBuilder();
        StringBuilder comment = null;
        LinkedList<RTextEntry> variation =
                new LinkedList<RTextEntry>();

        int halfMove = 0;
        int variantIndex = 0;

        boolean onCommentBlock = false;
        boolean onVariationBlock = false;
        boolean onLineCommentBlock = false;
        for (String token : text.split(" ")) {
            if (token == null || token.trim().equals("")) {
                continue;
            }
            if (!(onLineCommentBlock || onCommentBlock) &&
                    token.contains("...")) {
                token = StringUtil.afterSequence(token, "...");
                if (token.trim().length() == 0) {
                    continue;
                }
            }
            if (!(onLineCommentBlock || onCommentBlock) &&
                    token.contains(".")) {
                token = StringUtil.afterSequence(token, ".");
                if (token.trim().length() == 0) {
                    continue;
                }
            }
            if (!(onLineCommentBlock || onCommentBlock) &&
                    token.startsWith("$")) {
                if (getNag() == null) {
                    setNag(new HashMap<Integer, String>());
                }
                getNag().put(variantIndex, token);
                continue;
            }
            if (token.equals("{") &&
                    !(onLineCommentBlock || onCommentBlock)) {
                onCommentBlock = true;
                comment = new StringBuilder();
                continue;
            } else if (token.equals("}") && !onLineCommentBlock) {
                onCommentBlock = false;
                if (comment != null) {
                    if (getComments() == null) {
                        setComments(new HashMap<Integer, String>());
                    }
                    getComments().put(variantIndex, comment.toString());
                }
                comment = null;
                continue;
            } else if (token.equals(";") && !onCommentBlock) {
                onLineCommentBlock = true;
                comment = new StringBuilder();
                continue;
            } else if (token.equals("\n") && onLineCommentBlock) {
                onLineCommentBlock = false;
                if (comment != null) {
                    getComments().put(variantIndex, comment.toString());
                }
                comment = null;
                continue;
            } else if (token.equals("(") &&
                    !(onCommentBlock) || onLineCommentBlock) {
                onVariationBlock = true;
                variation.add(new RTextEntry(variantIndex));
                continue;
            } else if (token.equals(")") && onVariationBlock &&
                    !(onCommentBlock) || onLineCommentBlock) {
                onVariationBlock = false;
                if (variation != null) {
                    final RTextEntry last = variation.pollLast();
                    StringBuilder currentLine =
                            new StringBuilder(getMovesAt(moves.toString(), halfMove));
                    try {

                        onVariationBlock = variation.size() > 0;

                        for (RTextEntry entry : variation) {
                            currentLine.append(getMovesAt(entry.text.toString(), entry.size));
                        }

                        MoveList tmp = new MoveList();
                        tmp.loadFromSan(getMovesAt(currentLine.toString(), last.index));
                        MoveList var = MoveList.createMoveListFrom(tmp, tmp.size());
                        var.loadFromSan(last.text.toString());
                        final RTextEntry parent = variation.peekLast();
                        if (onVariationBlock && parent != null) {
                            var.setParent(parent.index);
                        } else {
                            var.setParent(-1);
                        }
                        if (getVariations() == null) {
                            setVariations(new HashMap<Integer, MoveList>());
                        }
                        getVariations().put(last.index, var);
                    } catch (Exception e) {
                        if (last != null) {
                            throw new PgnException("Error while reading variation: " +
                                    getMovesAt(currentLine.toString(), last.index) + " - " +
                                    last.text.toString(), e);
                        } else {
                            throw new PgnException("Error while reading variation: ", e);
                        }
                    }
                }
                continue;
            }

            if (onCommentBlock || onLineCommentBlock) {
                if (comment != null) {
                    comment.append(token);
                    comment.append(" ");
                }
                continue;
            }

            if (onVariationBlock) {
                if (variation != null) {
                    variation.getLast().text.append(token);
                    variation.getLast().text.append(" ");
                    variation.getLast().size++;
                    variantIndex++;
                }
                continue;
            }
            variantIndex++;
            halfMove++;
            moves.append(token);
            moves.append(" ");
        }

        StringUtil.replaceAll(moves, "\n", " ");
        getHalfMoves().loadFromSan(moves.toString());
    }

    /**
     * Goto move with the specified index
     *
     * @param moves the moves
     * @param index the index
     * @throws MoveException the move exception
     */
    public void gotoMove(final MoveList moves, int index) throws MoveException {
        setCurrentMoveList(moves);
        if (getBoard() != null &&
                index >= 0 && index < moves.size()) {
            getBoard().loadFromFen(moves.getStartFen());

            int i = 0;
            for (Move move : moves) {
                if (!getBoard().doMove(move, true)) {
                    throw new MoveException("Couldn't load board state. Reason: Illegal move in PGN MoveText.");
                }
                i++;
                if (i - 1 == index) {
                    break;
                }
            }
            setPosition(i - 1);

        }

    }

    /**
     * Goto first.
     *
     * @param moves the moves
     * @throws MoveException the move exception
     */
    public void gotoFirst(final MoveList moves) throws MoveException {
        gotoMove(moves, 0);
    }

    /**
     * Goto last.
     *
     * @param moves the moves
     * @throws MoveException the move exception
     */
    public void gotoLast(final MoveList moves) throws MoveException {
        gotoMove(moves, getHalfMoves().size() - 1);
    }

    /**
     * Goto next.
     *
     * @param moves the moves
     * @throws MoveException the move exception
     */
    public void gotoNext(final MoveList moves) throws MoveException {
        gotoMove(moves, getPosition() + 1);
    }

    /**
     * Goto prior.
     *
     * @param moves the moves
     * @throws MoveException the move exception
     */
    public void gotoPrior(final MoveList moves) throws MoveException {
        gotoMove(moves, getPosition() - 1);
    }

    /**
     * Goto first.
     *
     * @throws MoveException the move exception
     */
    public void gotoFirst() throws MoveException {
        gotoFirst(getCurrentMoveList());
    }

    /**
     * Goto last.
     *
     * @throws MoveException the move exception
     */
    public void gotoLast() throws MoveException {
        gotoLast(getCurrentMoveList());
    }

    /**
     * Goto next.
     *
     * @throws MoveException the move exception
     */
    public void gotoNext() throws MoveException {
        gotoNext(getCurrentMoveList());
    }

    /**
     * Goto prior.
     *
     * @throws MoveException the move exception
     */
    public void gotoPrior() throws MoveException {
        gotoPrior(getCurrentMoveList());
    }

    /**
     * Is end of move list boolean.
     *
     * @return the boolean
     */
    public boolean isEndOfMoveList() {
        return getCurrentMoveList() == null || getPosition() >= getCurrentMoveList().size() - 1;
    }

    /**
     * Is start of move list boolean.
     *
     * @return the boolean
     */
    public boolean isStartOfMoveList() {
        return getCurrentMoveList() == null && getPosition() == 0;
    }

    /**
     * Gets property.
     *
     * @return the property
     */
    public Map<String, String> getProperty() {
        return property;
    }

    /**
     * Sets property.
     *
     * @param property the property
     */
    public void setProperty(Map<String, String> property) {
        this.property = property;
    }

    /**
     * The type R text entry.
     */
    class RTextEntry {
        /**
         * The Index.
         */
        int index;
        /**
         * The Size.
         */
        int size;
        /**
         * The Text.
         */
        StringBuilder text = new StringBuilder();

        /**
         * Instantiates a new R text entry.
         *
         * @param index the index
         */
        public RTextEntry(int index) {
            this.index = index;
            this.size = 0;
        }
    }

}

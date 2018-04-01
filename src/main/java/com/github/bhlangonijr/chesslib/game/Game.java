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
    private Map<Integer, String> commentary;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Termination getTermination() {
        return termination;
    }

    public void setTermination(Termination termination) {
        this.termination = termination;
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(Player blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public String getAnnotator() {
        return annotator;
    }

    public void setAnnotator(String annotator) {
        this.annotator = annotator;
    }

    public String getPlyCount() {
        return plyCount;
    }

    public void setPlyCount(String plyCount) {
        this.plyCount = plyCount;
    }

    public GameResult getResult() {
        return result;
    }

    public void setResult(GameResult result) {
        this.result = result;
    }

    public Map<Integer, MoveList> getVariations() {
        return variations;
    }

    public void setVariations(Map<Integer, MoveList> variations) {
        this.variations = variations;
    }

    public Map<Integer, String> getCommentary() {
        return commentary;
    }

    public void setCommentary(Map<Integer, String> commentary) {
        this.commentary = commentary;
    }

    public Map<Integer, String> getNag() {
        return nag;
    }

    public void setNag(Map<Integer, String> nag) {
        this.nag = nag;
    }

    /**
     * @return the halfMoves
     */
    public MoveList getHalfMoves() {
        if (halfMoves == null) {
            setHalfMoves(new MoveList());
        }
        return halfMoves;
    }

    /**
     * @param halfMoves
     */
    public void setHalfMoves(MoveList halfMoves) {
        this.halfMoves = halfMoves;
        setCurrentMoveList(halfMoves);
    }

    /**
     * @return the fen
     */
    public String getFen() {
        return fen;
    }

    /**
     * @param fen the fen to set
     */
    public void setFen(String fen) {
        this.fen = fen;
    }

    /**
     * @return the board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * @param board the board to set
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * @return the round
     */
    public Round getRound() {
        return round;
    }

    /**
     * Convert the Game object to the PGN format
     *
     * @param includeVariations
     * @param includeComments
     * @return
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
            final String sanArray[] = getHalfMoves().toSANArray();
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

                if (getCommentary() != null) {
                    String comment = getCommentary().get(variantIndex);
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
            String sanArray[] = variation.toSANArray();
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
     * @return the gameId
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * @param gameId the gameId to set
     */
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    public int getInitialPosition() {
        return initialPosition;
    }

    public void setInitialPosition(int initialPosition) {
        this.initialPosition = initialPosition;
    }

    /**
     * @return the currentMoveList
     */
    public MoveList getCurrentMoveList() {
        return currentMoveList;
    }

    /**
     * @param currentMoveList the currentMoveList to set
     */
    public void setCurrentMoveList(MoveList currentMoveList) {
        this.currentMoveList = currentMoveList;
    }

    /**
     * @return the eco
     */
    public String getEco() {
        return eco;
    }

    /**
     * @param eco the eco to set
     */
    public void setEco(String eco) {
        this.eco = eco;
    }

    public String getOpening() {
        return opening;
    }

    public void setOpening(String opening) {
        this.opening = opening;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    /**
     * @return the moveText
     */
    public StringBuilder getMoveText() {
        return moveText;
    }

    /**
     * @param moveText the moveText to set
     */
    public void setMoveText(StringBuilder moveText) {
        this.moveText = moveText;
    }

    /**
     * Load a MoveText from a PGN file into the Game object
     *
     * @throws Exception
     */
    public void loadMoveText() throws Exception {
        if (getMoveText() != null) {
            loadMoveText(getMoveText());
        }
    }

    /**
     * Load a MoveText from a PGN file into the Game object
     *
     * @param moveText
     * @throws Exception
     */
    public void loadMoveText(StringBuilder moveText) throws Exception {

        getHalfMoves().clear();
        if (getVariations() != null) {
            getVariations().clear();
        }
        if (getCommentary() != null) {
            getCommentary().clear();
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
        if (text == null) {
            return;
        }
        if (getHalfMoves() == null) {
            if (getFen() != null && !getFen().trim().equals("")) {
                setHalfMoves(new MoveList(getFen()));
            } else {
                setHalfMoves(new MoveList());
            }
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
                    token.indexOf("...") > -1) {
                token = StringUtil.afterSequence(token, "...");
                if (token == null ||
                        token.trim().length() == 0) {
                    continue;
                }
            }
            if (!(onLineCommentBlock || onCommentBlock) &&
                    token.indexOf(".") > -1) {
                token = StringUtil.afterSequence(token, ".");
                if (token == null ||
                        token.trim().length() == 0) {
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
                    if (getCommentary() == null) {
                        setCommentary(new HashMap<Integer, String>());
                    }
                    getCommentary().put(variantIndex, comment.toString());
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
                    getCommentary().put(variantIndex, comment.toString());
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
                        tmp.loadFromSAN(getMovesAt(currentLine.toString(), last.index));
                        MoveList var = MoveList.createMoveListFrom(tmp, tmp.size());
                        var.loadFromSAN(last.text.toString());
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
                        if (last != null && currentLine != null) {
                            throw new PgnException("Error while reading variation: " +
                                    getMovesAt(currentLine.toString(), last.index) + " - " +
                                    last.text.toString(), e);
                        } else {
                            throw new PgnException("Error while reading variation: ", e);
                        }
                    }
                    currentLine = null;
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
        getHalfMoves().clear();
        getHalfMoves().loadFromSAN(moves.toString());

    }

    /**
     * Goto move with the specified index
     *
     * @param index
     */
    public void gotoMove(final MoveList moves, int index) throws MoveException {
        setCurrentMoveList(moves);
        if (getBoard() != null &&
                index >= 0 && index < moves.size()) {
            getBoard().loadFromFEN(moves.getStartFEN());

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

    public void gotoFirst(final MoveList moves) throws MoveException {
        gotoMove(moves, 0);
    }

    public void gotoLast(final MoveList moves) throws MoveException {
        gotoMove(moves, getHalfMoves().size() - 1);
    }

    public void gotoNext(final MoveList moves) throws MoveException {
        gotoMove(moves, getPosition() + 1);
    }

    public void gotoPrior(final MoveList moves) throws MoveException {
        gotoMove(moves, getPosition() - 1);
    }

    public void gotoFirst() throws MoveException {
        gotoFirst(getCurrentMoveList());
    }

    public void gotoLast() throws MoveException {
        gotoLast(getCurrentMoveList());
    }

    public void gotoNext() throws MoveException {
        gotoNext(getCurrentMoveList());
    }

    public void gotoPrior() throws MoveException {
        gotoPrior(getCurrentMoveList());
    }

    public boolean isEndOfMoveList() {
        return getCurrentMoveList() == null || getPosition() >= getCurrentMoveList().size() - 1;
    }

    public boolean isStartOfMoveList() {
        return getCurrentMoveList() == null && getPosition() == 0;
    }

    public Map<String, String> getProperty() {
        return property;
    }

    public void setProperty(Map<String, String> property) {
        this.property = property;
    }

    class RTextEntry {
        int index;
        int size;
        StringBuilder text = new StringBuilder();

        public RTextEntry(int index) {
            this.index = index;
            this.size = 0;
        }
    }

}

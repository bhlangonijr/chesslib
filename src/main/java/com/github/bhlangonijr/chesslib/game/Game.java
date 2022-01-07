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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.pgn.PgnException;
import com.github.bhlangonijr.chesslib.util.StringUtil;

/**
 * A chess game, as defined by the specifications of the Portable Game Notation (PGN) format.
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
     * Constructs a new chess game.
     *
     * @param gameId the game ID
     * @param round  the round the game belongs to
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
     * Returns the date of the game, when the game was played.
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of the game.
     *
     * @param date the date to set
     * @see Game#getDate()
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Returns the time of the game, at what time the game was played.
     *
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets the time of the game.
     *
     * @param time the time to set
     * @see Game#getTime()
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Returns the termination mode of the game.
     *
     * @return the termination mode
     */
    public Termination getTermination() {
        return termination;
    }

    /**
     * Sets the termination of the game.
     *
     * @param termination the termination to set
     */
    public void setTermination(Termination termination) {
        this.termination = termination;
    }

    /**
     * Returns the white player.
     *
     * @return the white player
     */
    public Player getWhitePlayer() {
        return whitePlayer;
    }

    /**
     * Sets the white player.
     *
     * @param whitePlayer the white player to set
     */
    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    /**
     * Returns the black player.
     *
     * @return the black player
     */
    public Player getBlackPlayer() {
        return blackPlayer;
    }

    /**
     * Sets the black player.
     *
     * @param blackPlayer the black player to set
     */
    public void setBlackPlayer(Player blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    /**
     * Returns the annotator.
     *
     * @return the annotator
     */
    public String getAnnotator() {
        return annotator;
    }

    /**
     * Sets the annotator.
     *
     * @param annotator the annotator to set
     */
    public void setAnnotator(String annotator) {
        this.annotator = annotator;
    }

    /**
     * Returns the ply count, that is, the number of moves played in the game.
     *
     * @return the ply count
     */
    public String getPlyCount() {
        return plyCount;
    }

    /**
     * Sets the ply count.
     *
     * @param plyCount the ply count to set
     * @see Game#getPlyCount()
     */
    public void setPlyCount(String plyCount) {
        this.plyCount = plyCount;
    }

    /**
     * Returns the game result.
     *
     * @return the game result
     */
    public GameResult getResult() {
        return result;
    }

    /**
     * Sets the game result.
     *
     * @param result the result to set
     */
    public void setResult(GameResult result) {
        this.result = result;
    }

    /**
     * Returns the variations present in the game. Each variation is defined by a {@link MoveList} indexed by an
     * identifier of the move from which it branches out.
     *
     * @return the variations
     */
    public Map<Integer, MoveList> getVariations() {
        return variations;
    }

    /**
     * Sets the variations of the game.
     *
     * @param variations the variations to set
     * @see Game#getVariations()
     */
    public void setVariations(Map<Integer, MoveList> variations) {
        this.variations = variations;
    }

    /**
     * Returns the commentary of the game. Each comment is indexed by an identifier of the move it refers to.
     *
     * @return the commentary
     */
    public Map<Integer, String> getComments() {
        return comments;
    }

    /**
     * Sets the commentary of the game.
     *
     * @param comments the commentary to set
     * @see Game#getComments()
     */
    public void setComments(Map<Integer, String> comments) {
        this.comments = comments;
    }

    /**
     * Returns the Numeric Annotation Glyphs (NAG) values present in the game, indexed by the identifiers of the moves
     * they refer to.
     *
     * @return the nag values
     */
    public Map<Integer, String> getNag() {
        return nag;
    }

    /**
     * Sets the Numeric Annotation Glyphs (NAG) values of the game.
     *
     * @param nag the nag values to set
     * @see Game#getNag()
     */
    public void setNag(Map<Integer, String> nag) {
        this.nag = nag;
    }

    /**
     * Returns a reference to the {@link MoveList} that holds the moves of the game, its main variation.
     *
     * @return the move list of the game
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
     * Sets the moves of the game.
     *
     * @param halfMoves the moves to set
     */
    public void setHalfMoves(MoveList halfMoves) {
        this.halfMoves = halfMoves;
        setCurrentMoveList(halfMoves);
    }

    /**
     * Returns the initial position of the game as a Forsyth-Edwards Notation (FEN) string.
     *
     * @return the initial position of the game in FEN notation
     */
    public String getFen() {
        return fen;
    }

    /**
     * Sets the initial position of the game, provided as a Forsyth-Edwards Notation (FEN) string.
     *
     * @param fen the initial position to set
     */
    public void setFen(String fen) {
        this.fen = fen;
    }

    /**
     * Returns the board representing the updated position of the game.
     *
     * @return the updated position of the game
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Sets the board used to represent the updated position of the game.
     *
     * @param board the board to set
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Returns the round of the event this game belongs to.
     *
     * @return the round of the game
     */
    public Round getRound() {
        return round;
    }

    /**
     * Generates the Portable Game Notation (PGN) representation of this game and its data. Variations and comments are
     * included by default.
     *
     * @param includeVariations currently ignored: variations are included regardless the values of this flag
     * @param includeComments   currently ignored: comments are included regardless the values of this flag
     * @return the PGN representation of this game
     * @throws MoveConversionException if the move conversion fails
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

    /**
     * Returns a string representation of this chess game.
     * <p/>
     * The result of {@link Game#toPgn(boolean, boolean)} is used to represent this game.
     *
     * @return a string representation of this game
     * @see Game#toPgn(boolean, boolean)
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
     * Returns the game ID.
     *
     * @return the game ID
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * Sets the game ID.
     *
     * @param gameId the game ID to set
     */
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    /**
     * Returns the index of the current move.
     *
     * @return the index of the current move
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the index of the current move.
     *
     * @param position the index of the current move to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Returns the index of the initial position.
     *
     * @return the index of the initial position
     */
    public int getInitialPosition() {
        return initialPosition;
    }

    /**
     * Sets the index of the initial position.
     *
     * @param initialPosition the index of the initial position to set
     */
    public void setInitialPosition(int initialPosition) {
        this.initialPosition = initialPosition;
    }

    /**
     * Returns the current list of moves, used to navigate the game.
     *
     * @return the current list of moves
     */
    public MoveList getCurrentMoveList() {
        return currentMoveList;
    }

    /**
     * Sets the current list of moves.
     *
     * @param currentMoveList the current list of moves to set
     */
    public void setCurrentMoveList(MoveList currentMoveList) {
        this.currentMoveList = currentMoveList;
    }

    /**
     * Returns the Encyclopedia of Chess Openings (ECO) code of the game.
     *
     * @return the ECO code
     */
    public String getEco() {
        return eco;
    }

    /**
     * Sets the Encyclopedia of Chess Openings (ECO) code of the game.
     *
     * @param eco the ECO code to set
     */
    public void setEco(String eco) {
        this.eco = eco;
    }

    /**
     * Returns the name of the opening.
     *
     * @return the opening name
     */
    public String getOpening() {
        return opening;
    }

    /**
     * Sets the name of the opening.
     *
     * @param opening the opening name to set
     */
    public void setOpening(String opening) {
        this.opening = opening;
    }

    /**
     * Returns the name of the variation.
     *
     * @return the variation name
     */
    public String getVariation() {
        return variation;
    }

    /**
     * Sets the name of the variation.
     *
     * @param variation the variation name to set
     */
    public void setVariation(String variation) {
        this.variation = variation;
    }

    /**
     * Returns the PGN textual representation of the moves of the game.
     *
     * @return the moves of the game
     */
    public StringBuilder getMoveText() {
        return moveText;
    }

    /**
     * Sets the moves of the game as a PGN textual representation.
     *
     * @param moveText the moves to set
     */
    public void setMoveText(StringBuilder moveText) {
        this.moveText = moveText;
    }

    /**
     * Loads an already existing PGN textual representation of moves into this game data structure. The internal status
     * of this instance is updated to reflect the loaded moves.
     * <p/>
     * It is possible to load a list of moves in their PGN textual representation without setting them in advance in the
     * game invoking {@link Game#loadMoveText(StringBuilder)}.
     *
     * @throws Exception if it is not possible to load the moves
     * @see Game#setMoveText(StringBuilder)
     * @see Game#loadMoveText(StringBuilder)
     */
    public void loadMoveText() throws Exception {
        if (getMoveText() != null) {
            loadMoveText(getMoveText());
        }
    }

    /**
     * Loads a PGN textual representation of moves into this game data structure. The internal status of this instance
     * is updated to reflect the loaded moves.
     *
     * @param moveText the moves to load
     * @throws Exception if it is not possible to load the moves
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
     * Navigates the list of moves from the initial position of the game until a given position, defined by the provided
     * index passed in input. In other words, updates the status of the board to reflect the game up to move
     * {@code index} (included). The provided list of moves becomes active and the pointer to the current position is
     * updated.
     *
     * @param moves the moves to navigate
     * @param index the index of the move to reach
     * @throws MoveException if any error occurs browsing the list of moves, for instance if a move is illegal for the
     * position
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
     * Navigates the list of moves from the initial position of the game to the first move in the list. In other words,
     * updates the status of the board to reflect the game up to the first move.
     * <p/>
     * Same as invoking {@code gotoMove(moves, 0)}.
     *
     * @param moves the moves to navigate
     * @throws MoveException if any error occurs browsing the list of moves, for instance if a move is illegal for the
     * position
     * @see Game#gotoMove(MoveList, int)
     */
    public void gotoFirst(final MoveList moves) throws MoveException {
        gotoMove(moves, 0);
    }

    /**
     * Navigates the list of moves from the initial position of the game to the last move in the list. In other words,
     * updates the status of the board to reflect the game up to the last move.
     * <p/>
     * Same as invoking {@code gotoMove(moves, moves.size() - 1)}.
     *
     * @param moves the moves to navigate
     * @throws MoveException if any error occurs browsing the list of moves, for instance if a move is illegal for the
     * position
     * @see Game#gotoMove(MoveList, int)
     */
    public void gotoLast(final MoveList moves) throws MoveException {
        gotoMove(moves, getHalfMoves().size() - 1);
    }

    /**
     * Navigates the list of moves to the next one in the position.
     * <p/>
     * Same as invoking {@code gotoMove(moves, getPosition() + 1)}.
     *
     * @param moves the moves to navigate
     * @throws MoveException if any error occurs browsing the list of moves, for instance if a move is illegal for the
     * position
     * @see Game#gotoMove(MoveList, int)
     */
    public void gotoNext(final MoveList moves) throws MoveException {
        gotoMove(moves, getPosition() + 1);
    }

    /**
     * Navigates the list of moves to the previous one in the position.
     * <p/>
     * Same as invoking {@code gotoMove(moves, getPosition() - 1)}.
     *
     * @param moves the moves to navigate
     * @throws MoveException if any error occurs browsing the list of moves, for instance if a move is illegal for the
     * position
     * @see Game#gotoMove(MoveList, int)
     */
    public void gotoPrior(final MoveList moves) throws MoveException {
        gotoMove(moves, getPosition() - 1);
    }

    /**
     * Navigates the current list of moves to the first one.
     * <p/>
     * Same as invoking {@code gotoMove(getCurrentMoveList(), 0)}.
     *
     * @throws MoveException if any error occurs browsing the list of moves, for instance if a move is illegal for the
     * position
     * @see Game#gotoMove(MoveList, int)
     */
    public void gotoFirst() throws MoveException {
        gotoFirst(getCurrentMoveList());
    }

    /**
     * Navigates the current list of moves to the last one.
     * <p/>
     * Same as invoking {@code gotoMove(getCurrentMoveList(), getCurrentMoveList().size() - 1)}.
     *
     * @throws MoveException if any error occurs browsing the list of moves, for instance if a move is illegal for the
     * position
     * @see Game#gotoMove(MoveList, int)
     */
    public void gotoLast() throws MoveException {
        gotoLast(getCurrentMoveList());
    }

    /**
     * Navigates the current list of moves to the next one in the position.
     * <p/>
     * Same as invoking {@code gotoMove(getCurrentMoveList(), getPosition() + 1)}.
     *
     * @throws MoveException if any error occurs browsing the list of moves, for instance if a move is illegal for the
     * position
     * @see Game#gotoMove(MoveList, int)
     */
    public void gotoNext() throws MoveException {
        gotoNext(getCurrentMoveList());
    }

    /**
     * Navigates the current list of moves to the previous one in the position.
     * <p/>
     * Same as invoking {@code gotoMove(getCurrentMoveList(), getPosition() - 1)}.
     *
     * @throws MoveException if any error occurs browsing the list of moves, for instance if a move is illegal for the
     * position
     * @see Game#gotoMove(MoveList, int)
     */
    public void gotoPrior() throws MoveException {
        gotoPrior(getCurrentMoveList());
    }

    /**
     * Checks if the current move in the position has reached the end of the active list of moves.
     *
     * @return {@code true} if the position has reached the last move in the active list of moves, or the latter is
     * empty
     */
    public boolean isEndOfMoveList() {
        return getCurrentMoveList() == null || getPosition() >= getCurrentMoveList().size() - 1;
    }

    /**
     * Checks if the current move in the position points to the fist one in the active list of moves.
     *
     * @return {@code true} if the current move is the first one in the active list of moves, or the latter is empty
     */
    public boolean isStartOfMoveList() {
        return getCurrentMoveList() == null && getPosition() == 0;
    }

    /**
     * Returns the PGN properties of the game.
     *
     * @return the PGN properties
     */
    public Map<String, String> getProperty() {
        return property;
    }

    /**
     * Sets the PGN properties of the game.
     *
     * @param property the properties to set
     */
    public void setProperty(Map<String, String> property) {
        this.property = property;
    }

    /**
     * Internal game structure used to define text moves variations.
     */
    static class RTextEntry {
        /**
         * The index in the parent line from which this variation branches out.
         */
        int index;
        /**
         * The size of this variation.
         */
        int size;
        /**
         * The textual representation of moves of this variation.
         */
        StringBuilder text = new StringBuilder();

        /**
         * Constructs a new variation.
         *
         * @param index the move index in the parent line from which the variation branches out
         */
        public RTextEntry(int index) {
            this.index = index;
            this.size = 0;
        }
    }

}

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

import java.util.ArrayList;
import java.util.List;

/**
 * A generic chess time control used in a game or a chess event. Depending on its type, the time control can be defined
 * by different components. It is also possible to convert the time control to and from its PGN representation.
 */
public class TimeControl {

    private final List<MovePerTime> movePerTime = new ArrayList<MovePerTime>();
    private TimeControlType timeControlType;
    private int halfMoves = 0;
    private long milliseconds = 0;
    private long increment = 0;
    private int depth = 0;
    private long nodes;

    /**
     * Parses a string that defines a time control and returns its representation.
     * <p>
     * Different formats are supported, as specified in {@link TimeControlType}.
     *
     * @param s the string to parse
     * @return the parsed time control
     */
    public static TimeControl parseFromString(String s) {
        TimeControl tc = new TimeControl();
        s = s.replace("|", "+");
        if (s.equals("?") || s.equals("-")) {
            tc.setTimeControlType(TimeControlType.UNKNOW);
            return tc;
        }

        if (s.indexOf(":") >= 0) {
            for (String field : s.split(":")) {
                parseTC(field, tc);
            }
        } else {
            parseTC(s, tc);
        }

        return tc;
    }

    private static void parseTC(String s, TimeControl tc) {

        if (s.indexOf("/") >= 0) {
            tc.setTimeControlType(TimeControlType.MOVES_PER_TIME);
            parseMT(s, tc);
        } else if (s.indexOf("+") >= 0) {
            tc.setTimeControlType(TimeControlType.TIME_BONUS);
            parseTM(s, tc);
        } else {
            tc.setTimeControlType(TimeControlType.TIME_BONUS);
            tc.milliseconds = Integer.parseInt(s) * 1000;
        }
    }

    private static void parseTM(String s, TimeControl tc) {
        String[] tm = s.split("\\+");
        tc.setIncrement(Integer.parseInt(tm[1]) * 1000);
        if (tm[0].indexOf("/") >= 0) {
            parseMT(tm[0], tc);
        } else {
            tc.setMilliseconds(Integer.parseInt(tm[0]) * 1000);
        }

    }

    private static void parseMT(String s, TimeControl tc) {
        String[] tm = s.split("/");
        int moves = Integer.parseInt(tm[0]);
        int milliseconds = Integer.parseInt(tm[1]) * 1000;
        if (tc.getHalfMoves() == 0) {
            tc.setHalfMoves(moves);
            if (tm[1].indexOf("+") >= 0) {
                parseTM(tm[1], tc);
            } else {
                tc.setMilliseconds(milliseconds);
            }
        } else {
            tc.getMovePerTime().add(new MovePerTime(moves, milliseconds));
        }
    }

    /**
     * Returns the type of the time control.
     *
     * @return the time control type
     */
    public TimeControlType getTimeControlType() {
        return timeControlType;
    }

    /**
     * Sets the type of the time control.
     *
     * @param timeControlType the time control type to set
     */
    public void setTimeControlType(TimeControlType timeControlType) {
        this.timeControlType = timeControlType;
    }

    /**
     * Returns the half moves component of the time control.
     *
     * @return the number of half moves
     */
    public int getHalfMoves() {
        return halfMoves;
    }

    /**
     * Sets the half moves component of the time control.
     *
     * @param halfMoves the number of half moves to set
     */
    public void setHalfMoves(int halfMoves) {
        this.halfMoves = halfMoves;
    }

    /**
     * Returns the time component of the time control, expressed in milliseconds.
     *
     * @return the amount of time, in milliseconds
     */
    public long getMilliseconds() {
        return milliseconds;
    }

    /**
     * Sets the time component of the time control, in milliseconds.
     *
     * @param milliseconds the amount of time to set
     */
    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    /**
     * Returns the time increment component of the time control, expressed in milliseconds.
     *
     * @return the time increment, in milliseconds
     */
    public long getIncrement() {
        return increment;
    }

    /**
     * Sets the time increment component of the time control, in milliseconds.
     *
     * @param increment the time increment to set
     */
    public void setIncrement(long increment) {
        this.increment = increment;
    }

    /**
     * Returns the nodes component of the time control.
     *
     * @return the nodes
     */
    public long getNodes() {
        return nodes;
    }

    /**
     * Sets the nodes component of the time control.
     *
     * @param nodes the nodes to set
     */
    public void setNodes(long nodes) {
        this.nodes = nodes;
    }

    /**
     * Returns the moves-per-time sub-time controls of this time controls.
     *
     * @return the moves-per-time time controls
     */
    public List<MovePerTime> getMovePerTime() {
        return movePerTime;
    }

    /**
     * Returns the depth component of the time control.
     *
     * @return the depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Sets the depth component of the time control.
     *
     * @param depth the depth to set
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * Returns the PGN notation that represents this time control. The format of the returned value depends on its type.
     *
     * @return the PGN representation of this time control
     */
    public String toPGNString() {
        if (getTimeControlType().equals(TimeControlType.UNKNOW)) {
            return "?";
        }
        StringBuilder s = new StringBuilder();
        if (getHalfMoves() > 0) {
            s.append(getHalfMoves());
            s.append("/");
            s.append(getMilliseconds() / 1000);
        } else if (getMilliseconds() >= 0) {
            s.append(getMilliseconds() / 1000);
        }
        if (getIncrement() > 0) {
            s.append("+");
            s.append(getIncrement() / 1000);
        }
        if (getMovePerTime().size() > 0) {
            for (MovePerTime mt : getMovePerTime()) {
                s.append(":");
                s.append(mt.toPGNString());
            }
        }
        return s.toString();
    }

    /**
     * Returns a string representation of this time control.
     *
     * @return a string representation of this time control
     */
    @Override
    public String toString() {
        if (getTimeControlType().equals(TimeControlType.UNKNOW)) {
            return "Custom...";
        }
        StringBuilder s = new StringBuilder();
        if (getHalfMoves() > 0) {
            s.append(getHalfMoves());
            s.append(" Moves / ");
            s.append(getMilliseconds() / 1000);
            s.append(" Sec");
        } else if (getMilliseconds() >= 0) {
            s.append(getMilliseconds() / 1000 / 60);
            s.append(" Min");
        }
        if (getIncrement() > 0) {
            s.append(" + ");
            s.append(getIncrement() / 1000);
            s.append(" Sec");
        }
        if (getMovePerTime().size() > 0) {
            for (MovePerTime mt : getMovePerTime()) {
                s.append(" : ");
                s.append(mt.toString());
            }
        }
        return s.toString();
    }
}

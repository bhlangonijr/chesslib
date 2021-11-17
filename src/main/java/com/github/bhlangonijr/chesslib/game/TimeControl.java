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
 * The type Time control.
 */
public class TimeControl {

    private final List<MovePerTime> movePerTime =
            new ArrayList<MovePerTime>();
    private TimeControlType timeControlType;
    private int halfMoves = 0;
    private long milliseconds = 0;
    private long increment = 0;
    private int depth = 0;
    private long nodes;

    /**
     * Parse from string time control.
     *
     * @param s the s
     * @return the time control
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
     * Gets time control type.
     *
     * @return the time control type
     */
    public TimeControlType getTimeControlType() {
        return timeControlType;
    }

    /**
     * Sets time control type.
     *
     * @param timeControlType the time control type
     */
    public void setTimeControlType(TimeControlType timeControlType) {
        this.timeControlType = timeControlType;
    }

    /**
     * Gets half moves.
     *
     * @return the half moves
     */
    public int getHalfMoves() {
        return halfMoves;
    }

    /**
     * Sets half moves.
     *
     * @param halfMoves the half moves
     */
    public void setHalfMoves(int halfMoves) {
        this.halfMoves = halfMoves;
    }

    /**
     * Gets milliseconds.
     *
     * @return the milliseconds
     */
    public long getMilliseconds() {
        return milliseconds;
    }

    /**
     * Sets milliseconds.
     *
     * @param milliseconds the milliseconds
     */
    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    /**
     * Gets increment.
     *
     * @return the increment
     */
    public long getIncrement() {
        return increment;
    }

    /**
     * Sets increment.
     *
     * @param increment the increment
     */
    public void setIncrement(long increment) {
        this.increment = increment;
    }

    /**
     * Gets nodes.
     *
     * @return the nodes
     */
    public long getNodes() {
        return nodes;
    }

    /**
     * Sets nodes.
     *
     * @param nodes the nodes
     */
    public void setNodes(long nodes) {
        this.nodes = nodes;
    }

    /**
     * Gets move per time.
     *
     * @return the move per time
     */
    public List<MovePerTime> getMovePerTime() {
        return movePerTime;
    }

    /**
     * Gets depth.
     *
     * @return the depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Sets depth.
     *
     * @param depth the depth
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * TimeControl to PGN String Format
     *
     * @return the string
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

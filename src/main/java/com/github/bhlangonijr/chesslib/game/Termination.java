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

/**
 * The enum Termination.
 */
public enum Termination {
    /**
     * Abandoned termination.
     */
    ABANDONED("ABANDONED"),
    /**
     * Adjudication termination.
     */
    ADJUDICATION("ADJUDICATION"),
    /**
     * Death termination.
     */
    DEATH("DEATH"),
    /**
     * Emergency termination.
     */
    EMERGENCY("EMERGENCY"),
    /**
     * Normal termination.
     */
    NORMAL("NORMAL"),
    /**
     * The Rules infraction.
     */
    RULES_INFRACTION("RULES INFRACTION"),
    /**
     * The Time forfeit.
     */
    TIME_FORFEIT("TIME FORFEIT"),
    /**
     * Unterminated termination.
     */
    UNTERMINATED("UNTERMINATED"),
    /**
     * Time termination.
     */
    TIME("TIME");

    /**
     * The Description.
     */
    String description;

    Termination(String description) {
        this.description = description;
    }

    /**
     * From value termination.
     *
     * @param v the v
     * @return the termination
     */
    public static Termination fromValue(String v) {
        return valueOf(v.replace(' ', '_'));
    }

    /**
     * Value string.
     *
     * @return the string
     */
    public String value() {
        return name();
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return description;
    }


}

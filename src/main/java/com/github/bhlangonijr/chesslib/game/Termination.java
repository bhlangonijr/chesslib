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

public enum Termination {
    ABANDONED("ABANDONED"),
    ADJUDICATION("ADJUDICATION"),
    DEATH("DEATH"),
    EMERGENCY("EMERGENCY"),
    NORMAL("NORMAL"),
    RULES_INFRACTION("RULES INFRACTION"),
    TIME_FORFEIT("TIME FORFEIT"),
    UNTERMINATED("UNTERMINATED"),
    TIME("TIME");

    String description;

    Termination(String description) {
        this.description = description;
    }

    public static Termination fromValue(String v) {
        return valueOf(v.replace(' ', '_'));
    }

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

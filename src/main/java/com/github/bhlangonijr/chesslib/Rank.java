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

package com.github.bhlangonijr.chesslib;

public enum Rank {

    RANK_1("1"), RANK_2("2"), RANK_3("3"), RANK_4("4"), RANK_5("5"), RANK_6("6"), RANK_7("7"), RANK_8("8"), NONE("");

    String notation;

    Rank(String notation) {
        this.notation = notation;
    }

    public static Rank fromValue(String v) {
        return valueOf(v);
    }

    public String getNotation() {
        return notation;
    }

    public String value() {
        return name();
    }
}

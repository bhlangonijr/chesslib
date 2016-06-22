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

public enum File {

    FILE_A("A"), FILE_B("B"), FILE_C("C"), FILE_D("D"), FILE_E("E"), FILE_F("F"), FILE_G("G"), FILE_H("H"), NONE("");

    String notation;

    File(String notation) {
        this.notation = notation;
    }

    public static File fromValue(String v) {
        return valueOf(v);
    }

    public String getNotation() {
        return notation;
    }

    public String value() {
        return name();
    }
}

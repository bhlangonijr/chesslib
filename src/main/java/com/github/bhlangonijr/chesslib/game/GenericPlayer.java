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
 * A generic player of a chess game.
 */
public class GenericPlayer implements Player {

    private String id;
    private int elo;
    private String name;
    private PlayerType type;
    private String description;

    /**
     * Constructs a new chess player.
     */
    public GenericPlayer() {

    }

    /**
     * Constructs a new chess player using their basic information.
     *
     * @param id   the ID of the player
     * @param name the name of the player
     */
    public GenericPlayer(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int getElo() {
        return elo;
    }

    @Override
    public void setElo(int elo) {
        this.elo = elo;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public PlayerType getType() {
        return type;
    }

    @Override
    public void setType(PlayerType type) {
        this.type = type;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getLongDescription() {
        String desc = getName();
        if (getElo() > 0) {
            desc += " (" + getElo() + ")";
        }
        return desc;
    }

    /**
     * Returns a string representation of this player.
     *
     * @return a string representation of this player
     */
    @Override
    public String toString() {
        return getId();
    }

}

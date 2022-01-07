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
 * A player of a chess game.
 */
public interface Player {

    /**
     * Returns the ID of the player.
     *
     * @return the ID
     */
    String getId();

    /**
     * Sets the ID of the player.
     *
     * @param id the ID to set
     */
    void setId(String id);

    /**
     * Returns the ELO rating of the player.
     *
     * @return the ELO rating
     */
    int getElo();

    /**
     * Sets the ELO rating of the player.
     *
     * @param elo the ELO rating to set
     */
    void setElo(int elo);

    /**
     * Returns the name of the player.
     *
     * @return the name
     */
    String getName();

    /**
     * Sets the name of the player.
     *
     * @param name the name to set
     */
    void setName(String name);

    /**
     * Returns the type of the player.
     *
     * @return the type
     */
    PlayerType getType();

    /**
     * Sets the type of the player.
     *
     * @param type the type to set
     */
    void setType(PlayerType type);

    /**
     * Returns the description of the player.
     *
     * @return the description
     */
    String getDescription();

    /**
     * Sets the description of the player.
     *
     * @param description the description to set
     */
    void setDescription(String description);

    /**
     * Returns the long description of the player.
     *
     * @return the long description
     */
    String getLongDescription();

}
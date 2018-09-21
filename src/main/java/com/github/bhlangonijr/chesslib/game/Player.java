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
 * The interface Player.
 */
public interface Player {

    /**
     * Gets id.
     *
     * @return the id
     */
    String getId();

    /**
     * Sets id.
     *
     * @param id the id
     */
    void setId(String id);

    /**
     * Gets elo.
     *
     * @return the elo
     */
    int getElo();

    /**
     * Sets elo.
     *
     * @param elo the elo
     */
    void setElo(int elo);

    /**
     * Gets name.
     *
     * @return the name
     */
    String getName();

    /**
     * Sets name.
     *
     * @param name the name
     */
    void setName(String name);

    /**
     * Gets type.
     *
     * @return the type
     */
    PlayerType getType();

    /**
     * Sets type.
     *
     * @param type the type
     */
    void setType(PlayerType type);

    /**
     * Gets description.
     *
     * @return the description
     */
    String getDescription();

    /**
     * Sets description.
     *
     * @param description the description
     */
    void setDescription(String description);

    /**
     * Gets long description.
     *
     * @return the long description
     */
    String getLongDescription();

}
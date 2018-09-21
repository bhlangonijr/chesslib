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
 * The type Game factory.
 */
public class GameFactory {

    /**
     * Create a new Event
     *
     * @param name the name
     * @return event
     */
    public static Event newEvent(String name) {

        Event event = new Event();
        event.setName(name);
        event.setId(name);

        return event;
    }

    /**
     * Create a new Round
     *
     * @param event  the event
     * @param number the number
     * @return round
     */
    public static Round newRound(Event event, int number) {

        Round round = new Round(event);
        round.setNumber(number);

        return round;
    }

    /**
     * Create a new Game
     *
     * @param gameId the game id
     * @param round  the round
     * @return game
     */
    public static Game newGame(String gameId, Round round) {

        Game game = new Game(gameId, round);

        return game;
    }

    /**
     * Creates a new player
     *
     * @param type the type
     * @param name the name
     * @return player
     */
    public static Player newPlayer(PlayerType type, String name) {
        return new GenericPlayer(name, name);
    }


}

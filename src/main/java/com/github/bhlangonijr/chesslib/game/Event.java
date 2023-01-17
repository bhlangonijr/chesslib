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

import com.github.bhlangonijr.chesslib.pgn.PgnHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * A chess event.
 */
public class Event {

    private final Map<Integer, Round> round = new HashMap<>();
    private String id;
    private String name;
    private EventType eventType;
    private String startDate;
    private String endDate;
    private String site;
    private long timestamp;
    private int rounds;
    private TimeControl timeControl;
    private TimeControl timeControl2;
    private PgnHolder pgnHolder;

    /**
     * Returns the ID of the event.
     *
     * @return the ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the event.
     *
     * @param id the ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the name of the event.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the event.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the type of the event.
     *
     * @return the type
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Sets the type of the player.
     *
     * @param eventType the type to set
     */
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    /**
     * Returns the start date of the event.
     *
     * @return the start date
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date of the event.
     *
     * @param startDate the start date to set
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Returns the end date of the event.
     *
     * @return the end date
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date of the event.
     *
     * @param endDate the end date to set
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * Returns the site (location) of the event.
     *
     * @return the site
     */
    public String getSite() {
        return site;
    }

    /**
     * Sets the site (location) of the event.
     *
     * @param site the site to set
     */
    public void setSite(String site) {
        this.site = site;
    }

    /**
     * Returns the specific timestamp of the event.
     *
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the specific timestamp of the event.
     *
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the number of rounds of the event.
     *
     * @return the number of rounds
     */
    public int getRounds() {
        return rounds;
    }

    /**
     * Sets the number of rounds of the event.
     *
     * @param rounds the number of rounds to set
     */
    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    /**
     * Returns the main time control of the event.
     *
     * @return the main time control
     */
    public TimeControl getTimeControl() {
        return timeControl;
    }

    /**
     * Sets the main time control of the event.
     *
     * @param timeControl the main time control to set
     */
    public void setTimeControl(TimeControl timeControl) {
        this.timeControl = timeControl;
    }

    /**
     * Returns the secondary time control of the event.
     *
     * @return the secondary time control
     */
    public TimeControl getTimeControl2() {
        return timeControl2;
    }

    /**
     * Sets the secondary time control of the event.
     *
     * @param timeControl2 the secondary time control to set
     */
    public void setTimeControl2(TimeControl timeControl2) {
        this.timeControl2 = timeControl2;
    }

    /**
     * Returns the rounds of the event.
     *
     * @return the rounds
     */
    public Map<Integer, Round> getRound() {
        return round;
    }

    /**
     * Returns the PGN holder used to access the PGN file used for the event.
     *
     * @return the PGN holder reference
     */
    public PgnHolder getPgnHolder() {
        return pgnHolder;
    }

    /**
     * Sets the PGN holder used to access the PGN file used for the event.
     *
     * @param pgnHolder the PGN holder to use
     */
    public void setPgnHolder(PgnHolder pgnHolder) {
        this.pgnHolder = pgnHolder;
    }

}

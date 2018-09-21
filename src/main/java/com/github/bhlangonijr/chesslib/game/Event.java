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
 * The type Event.
 */
public class Event {

    private final Map<Integer, Round> round = new HashMap<Integer, Round>();
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
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets event type.
     *
     * @return the event type
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Sets event type.
     *
     * @param eventType the event type
     */
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    /**
     * Gets start date.
     *
     * @return the start date
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets start date.
     *
     * @param startDate the start date
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets end date.
     *
     * @return the end date
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Sets end date.
     *
     * @param endDate the end date
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets site.
     *
     * @return the site
     */
    public String getSite() {
        return site;
    }

    /**
     * Sets site.
     *
     * @param site the site
     */
    public void setSite(String site) {
        this.site = site;
    }

    /**
     * Gets timestamp.
     *
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets timestamp.
     *
     * @param timestamp the timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets rounds.
     *
     * @return the rounds
     */
    public int getRounds() {
        return rounds;
    }

    /**
     * Sets rounds.
     *
     * @param rounds the rounds
     */
    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    /**
     * Gets time control.
     *
     * @return the time control
     */
    public TimeControl getTimeControl() {
        return timeControl;
    }

    /**
     * Sets time control.
     *
     * @param timeControl the time control
     */
    public void setTimeControl(TimeControl timeControl) {
        this.timeControl = timeControl;
    }

    /**
     * Gets time control 2.
     *
     * @return the time control 2
     */
    public TimeControl getTimeControl2() {
        return timeControl2;
    }

    /**
     * Sets time control 2.
     *
     * @param timeControl2 the time control 2
     */
    public void setTimeControl2(TimeControl timeControl2) {
        this.timeControl2 = timeControl2;
    }

    /**
     * Gets round.
     *
     * @return the round
     */
    public Map<Integer, Round> getRound() {
        return round;
    }

    /**
     * Gets pgn holder.
     *
     * @return the pgn holder
     */
    public PgnHolder getPgnHolder() {
        return pgnHolder;
    }

    /**
     * Sets pgn holder.
     *
     * @param pgnHolder the pgn holder
     */
    public void setPgnHolder(PgnHolder pgnHolder) {
        this.pgnHolder = pgnHolder;
    }

}

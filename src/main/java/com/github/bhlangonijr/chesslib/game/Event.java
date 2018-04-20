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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public TimeControl getTimeControl() {
        return timeControl;
    }

    public void setTimeControl(TimeControl timeControl) {
        this.timeControl = timeControl;
    }

    public TimeControl getTimeControl2() {
        return timeControl2;
    }

    public void setTimeControl2(TimeControl timeControl2) {
        this.timeControl2 = timeControl2;
    }

    public Map<Integer, Round> getRound() {
        return round;
    }

    public PgnHolder getPgnHolder() {
        return pgnHolder;
    }

    public void setPgnHolder(PgnHolder pgnHolder) {
        this.pgnHolder = pgnHolder;
    }

}

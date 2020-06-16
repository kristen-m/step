// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> possibleTimes = new ArrayList<TimeRange>();
    Set<String> attendees =  new HashSet<String>();
    int meetingDur = (int) request.getDuration();
    attendees.addAll(request.getAttendees());
    if (meetingDur > TimeRange.WHOLE_DAY.duration()){
      return possibleTimes;
    }
    if (events.isEmpty() || attendees.isEmpty()){
      possibleTimes.add(TimeRange.WHOLE_DAY);
      return possibleTimes;
    }
    ArrayList<TimeRange> busyWithOverlap = getBusyTimes(events, attendees);
    possibleTimes = findFreeTimes(busyWithOverlap, request.getDuration());
    return possibleTimes;
  }

/**
  * Returns an ArrayList of meeting times that are unavailable due to schedule
  * conflicts and overlapping meeting times within the given group of attendees. 
  *
  * @param  events the Collection of Event objects
  * @param  requestAttendees the Set of attendees
  * @return    the ArrayList of all meeting times that do not work with the attendees schedules
  */
  private ArrayList<TimeRange> getBusyTimes(Collection<Event> events, Set<String> requestAttendees) {
    ArrayList<TimeRange> busyTimes = new ArrayList<TimeRange>();
    for(Event event : events) {
      Set<String> eventAttendees = event.getAttendees();
      Set<String> overlap = new HashSet<>(eventAttendees);
      overlap.retainAll(requestAttendees);
      if (!overlap.isEmpty()){
        busyTimes.add(event.getWhen());
      }
    }
    Collections.sort(busyTimes, TimeRange.ORDER_BY_START);
    ArrayList<TimeRange> busyWithOverlap = new ArrayList<TimeRange>();
    if(busyTimes.size() > 0) {
      busyWithOverlap.add(busyTimes.get(0));
    }
    int overlapIndex = 0;
    for (int busyIndex=0; busyIndex < busyTimes.size(); busyIndex++){
      if (busyTimes.get(busyIndex).overlaps(busyWithOverlap.get(overlapIndex))){
        int meetingStart = Math.min(busyWithOverlap.get(overlapIndex).start(), busyTimes.get(busyIndex).start());
        int meetingEnd = Math.max(busyWithOverlap.get(overlapIndex).end(), busyTimes.get(busyIndex).end()) - 1;
        busyWithOverlap.set(overlapIndex, TimeRange.fromStartEnd(meetingStart, meetingEnd, true));
      }
      else {
        busyWithOverlap.add(busyTimes.get(busyIndex));
        overlapIndex++;
      }
    }
    return busyWithOverlap;
  }

  /**
  * Returns an ArrayList of possible meeting times that are available for all attendees.
  *
  * @param  busyTimes an ArrayList that contains all of the unavailable meeting times
  * @param  duration the length of the meeting being planned
  * @return    the ArrayList of possible meeting times
  */
  private ArrayList<TimeRange> findFreeTimes(ArrayList<TimeRange> busyTimes, long duration) {
    ArrayList<TimeRange> freeTimes = new ArrayList<>();
    int start = TimeRange.START_OF_DAY;
    int end = TimeRange.END_OF_DAY;
    for (int i = 0; i < busyTimes.size(); i++) {
      TimeRange curr = busyTimes.get(i);
      int thisStart = curr.start();
      int thisEnd = curr.end();
      TimeRange newFree = TimeRange.fromStartEnd(start, thisStart, false);
      if (newFree.duration() >= duration) {
        freeTimes.add(newFree);
      }
      start = thisEnd;
    }
    TimeRange newFree = TimeRange.fromStartEnd(start, end, true);
    if (newFree.duration() >= duration) {
      freeTimes.add(newFree);
    }
    return freeTimes;
  }
}
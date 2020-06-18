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
    Set<String> optionalAttendees =  new HashSet<String>();
    int meetingDur = (int) request.getDuration();
    attendees.addAll(request.getAttendees());
    optionalAttendees.addAll(request.getOptionalAttendees());
    if (meetingDur > TimeRange.WHOLE_DAY.duration()){
      return possibleTimes;
    }
    if (events.isEmpty()){
      possibleTimes.add(TimeRange.WHOLE_DAY);
      return possibleTimes;
    }
    possibleTimes = getPossibleTimes(events, attendees, optionalAttendees, request.getDuration());
    return possibleTimes;
  }

/**
  * Returns an ArrayList of possible meeting times that take into account mandatory
  * attendees & optional attendees schedules.
  *
  * @param  events the Collection of Event objects
  * @param  requestAttendees the Set of mandatory attendees
  * @param  optAttendees the Set of optional attendees
  * @param  duration the length of the requested meeting
  * @return    the ArrayList of all meeting times that work with the attendees schedules
  */
  private ArrayList<TimeRange> getPossibleTimes(Collection<Event> events, Set<String> mandatoryAttendees,
          Set<String> optAttendees, long duration) {
    ArrayList<TimeRange> mandatoryBusyTimes = new ArrayList<TimeRange>();
    ArrayList<TimeRange> optionalBusyTimes = new ArrayList<TimeRange>();
    mandatoryBusyTimes = getBusyTimes(events, mandatoryAttendees);
    optionalBusyTimes = getBusyTimes(events, optAttendees);

    
    if (mandatoryBusyTimes.isEmpty() && !optionalBusyTimes.isEmpty()){
      return findFreeTimes(getOverlap(optionalBusyTimes), duration);
    }
    if (!mandatoryBusyTimes.isEmpty() && optionalBusyTimes.isEmpty()){
      return findFreeTimes(getOverlap(busyTimes), duration);
    }
    ArrayList<TimeRange> allBusyTimes = new ArrayList<TimeRange>();
    allBusyTimes.addAll(mandatoryBusyTimes);
    allBusyTimes.addAll(optionalBusyTimes);
    ArrayList<TimeRange> overlappingBusyTimes = getOverlap(allBusyTimes);
    ArrayList<TimeRange> bothFree = findFreeTimes(overlappingBusyTimes, duration);
    if (bothFree.isEmpty()){
        return findFreeTimes(getOverlap(mandatoryBusyTimes), duration);
    }
    return bothFree;
  }   
  /*
  * Returns an ArrayList of TimeRanges of all of the times throughout the day
  * that the given set of meeting attendees are busy and cannot be scheduled.
  *
  * @param  events the Collection of Event objects
  * @param  attendees the Set of meeting attendees
  * @return   The ArrayList of TimeRanges where the given attendees are busy that day
  */

  private ArrayList<TimeRange> getBusyTimes(Collection<Event> events, Set<String> attendees) {
    ArrayList<TimeRange> unavailableTimes = new ArrayList<TimeRange>();
    for(Event event : events) {
      Set<String> eventAttendees = event.getAttendees();
      Set<String> attendeeOverlap = new HashSet<>(eventAttendees);
      attendeeOverlap.retainAll(attendees);
      if (!attendeeOverlap.isEmpty()) {
        unavailableTimes.add(event.getWhen());
      }
    }
    return unavailableTimes;
  }

  /**
  * Returns an ArrayList of meeting times that are not availale
  * and take into account overlapping meetings.
  *
  * @param  busyTimes an ArrayList that contains all of the unavailable meeting times
  * @return    the ArrayList of all meeting times that don't work with overlapping schedules.
  */
  private ArrayList<TimeRange> getOverlap(ArrayList<TimeRange> busyTimes) {
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
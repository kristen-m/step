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
import static com.google.sps.TimeRange.fromStartEnd;
import static com.google.sps.TimeRange.WHOLE_DAY;
import static com.google.sps.TimeRange.START_OF_DAY;
import static com.google.sps.TimeRange.END_OF_DAY;
import static com.google.sps.TimeRange.ORDER_BY_START;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    long meetingDuration = request.getDuration();
    Set<String> mandatoryAttendees =  new HashSet<String>(request.getAttendees());
    Set<String> optionalAttendees =  new HashSet<String>(request.getOptionalAttendees());
    return getPossibleTimes(getBusyTimes(events, mandatoryAttendees), 
        getBusyTimes(events, optionalAttendees), meetingDuration);
  }

  /**
  * Returns an ArrayList of all possible meeting times. The schedules of mandatory attendees
  * are considered first with top priority. The schedule of optional attendees are considered
  * when possible.
  *
  * @param  mandatory the ArrayList of timeranges where mandatory attendees are busy
  * @param  optional the ArrayList of timeranges where optional attendees are busy
  * @param  duration the length of the requested meeting
  * @return    the ArrayList of all open meeting times for mandatory attendees & if possible, optional attendees
  */
  private ArrayList<TimeRange> getPossibleTimes(ArrayList<TimeRange> mandatoryTimes,
          ArrayList<TimeRange> optionalTimes, long duration) {
    if (mandatoryTimes.isEmpty()) {
      return findFreeTimes(getOverlap(optionalTimes), duration);
    }
    
    if (optionalTimes.isEmpty() ) {
      return findFreeTimes(getOverlap(mandatoryTimes), duration);
    }

    ArrayList<TimeRange> allBusyTimes = new ArrayList<TimeRange>();
    allBusyTimes.addAll(mandatoryTimes);
    allBusyTimes.addAll(optionalTimes);
    ArrayList<TimeRange> bothFree = findFreeTimes(getOverlap(allBusyTimes), duration);
    if (bothFree.isEmpty()) {
      return findFreeTimes(getOverlap(mandatoryTimes), duration);
    }
    return bothFree;
  }

  /**
  * Returns an ArrayList of TimeRanges of all of the unavailable times throughout the day.
  * A time is considered unavailable when meeting attendees are in another meeting during that slot.
  * As a result, that attendee cannot be scheduled and that time is marked as busy. 
  *
  * @param  events the Collection of Event objects
  * @param  attendees the Set of meeting attendees
  * @return   The ArrayList of TimeRanges where the given attendees are busy that day
  */
  private ArrayList<TimeRange> getBusyTimes(Collection<Event> events, Set<String> attendees) {
    ArrayList<TimeRange> unavailableTimes = new ArrayList<TimeRange>();
    for (Event event : events) {
      Set<String> attendeeOverlap = new HashSet<>(event.getAttendees());
      attendeeOverlap.retainAll(attendees);
      if (!attendeeOverlap.isEmpty()) {
        //Mark the time as unavailable if the requested attendees have a conflicting event
        unavailableTimes.add(event.getWhen());
      }
    }
    return unavailableTimes;
  }

  /**
  * Takes in an ArrayList of unavailable times and combines them to account for
  * unavailable chunks that overlap with each other. 
  *
  * @param  busyTimes an ArrayList that contains all of the unavailable meeting times
  * @return    the ArrayList of all chunks of meeting times that are unavailable.
  */
  private ArrayList<TimeRange> getOverlap(ArrayList<TimeRange> busyTimes) {
    Collections.sort(busyTimes, ORDER_BY_START);
    ArrayList<TimeRange> busyWithOverlap = new ArrayList<TimeRange>();
    if (busyTimes.size() > 0) {
      busyWithOverlap.add(busyTimes.get(0));
    }
    int overlapIndex = 0;
    for (int busyIndex=0; busyIndex < busyTimes.size(); busyIndex++) {

      if (busyTimes.get(busyIndex).overlaps(busyWithOverlap.get(overlapIndex))) {
        int meetingStart = Math.min(busyWithOverlap.get(overlapIndex).start(), busyTimes.get(busyIndex).start());
        int meetingEnd = Math.max(busyWithOverlap.get(overlapIndex).end(), busyTimes.get(busyIndex).end()) - 1;
        busyWithOverlap.set(overlapIndex, fromStartEnd(meetingStart, meetingEnd, true));
      }
      else {
        busyWithOverlap.add(busyTimes.get(busyIndex));
        overlapIndex++;
      }
    }
    return busyWithOverlap;
  }

  /**
  * Returns an ArrayList of open meeting times of a duration that can be scheduled
  * in between the given list of unavailable times.
  *
  * @param  busyTimes an ArrayList that contains all of the unavailable meeting times
  * @param  duration the length of the meeting being planned
  * @return    the ArrayList of free meeting times
  */
  private ArrayList<TimeRange> findFreeTimes(ArrayList<TimeRange> busyTimes, long meetingDuration) {
    ArrayList<TimeRange> freeTimes = new ArrayList<>();
    int start = START_OF_DAY;
    int end = END_OF_DAY;
    for (int i = 0; i < busyTimes.size(); i++) {
      TimeRange curr = busyTimes.get(i);
      int thisStart = curr.start();
      int thisEnd = curr.end();
      TimeRange newFree = fromStartEnd(start, thisStart, false);
      if (newFree.duration() >= meetingDuration) {
        freeTimes.add(newFree);
      }
      start = thisEnd;
    }
    TimeRange newFree = fromStartEnd(start, end, true);
    if (newFree.duration() >= meetingDuration) {
      freeTimes.add(newFree);
    }
    return freeTimes;
  }
}
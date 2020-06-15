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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public final class FindMeetingQuery {
  private static int DAY_LENGTH = 24*60;
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> possibleTimes = new ArrayList<TimeRange>();
    Set<String> attendees =  new HashSet<String>();
    int meetingDur = (int)request.getDuration();

    attendees.addAll(request.getAttendees());

    //If the meeting duration is greater than 24 hours return no options
    if (meetingDur > DAY_LENGTH){
      return possibleTimes;
    }

    //if there are no scheduled events or attendees, the entire day is free
    if (events.isEmpty() || attendees.isEmpty()){
      possibleTimes.add(TimeRange.WHOLE_DAY);
      return possibleTimes;
    }

    List<TimeRange> busyTimes = new ArrayList<TimeRange>();
    for(Event event : events) {
      Set<String> eventAttendees = event.getAttendees();
      Set<String> overlap = new HashSet<>(eventAttendees);
      overlap.retainAll(attendees);
      if (!overlap.isEmpty()){
        busyTimes.add(event.getWhen());
      }
    }
    Collections.sort(busyTimes, TimeRange.ORDER_BY_START);
    System.out.println(busyTimes);  
    TimeRange[] unavailable = ((List<TimeRange>) busyTimes).toArray(new TimeRange[busyTimes.size()]); 

    int index = 0;
    for (int i=0; i < unavailable.length; i++) {
      int startTime = 0;
      int endTime = 0;
      if(i == 0) {
        startTime = 0;
        endTime = unavailable[i].start();
      }
      else{
        startTime = unavailable[index].start();
        endTime = unavailable[i].end();
      }
      TimeRange freeTime = TimeRange.fromStartEnd(startTime,endTime,false);
      possibleTimes.add(freeTime);
      index++;
    }
    System.out.println(possibleTimes);
    return possibleTimes;
  }
}


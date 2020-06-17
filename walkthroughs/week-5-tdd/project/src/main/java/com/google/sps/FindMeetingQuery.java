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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class FindMeetingQuery {
  /**
   * Method that finds the available meeting times given events for the days, and request
   * for the new meeting.
   *
   * @param events: Collection of events already scheduled in the day
   * @param request: Details of the new meeting which we try to find time for
   * @return list of potential TimeRanges to schedule the new event
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // Return empty list if duration exceeds length of day.
    if (request.getDuration() >= TimeRange.END_OF_DAY) {
      return new ArrayList<>();
    }

    // Modifiable lists that we populate/sort in this function:
    ArrayList<TimeRange> meetingTimes = new ArrayList<TimeRange>();
    ArrayList<TimeRange> meetingTimesWithOptionals = new ArrayList<TimeRange>();

    // Lists that are populated; made unmodifiable before returning
    List<TimeRange> requiredSlots = new ArrayList<TimeRange>();
    List<TimeRange> withOptionalSlots = new ArrayList<TimeRange>();

    for (Event event : events) {
      TimeRange eventTime = event.getWhen();
      for (String person : request.getAttendees()) {
        if (event.getAttendees().contains(person) && !meetingTimes.contains(eventTime)) {
          meetingTimes.add(eventTime);
          meetingTimesWithOptionals.add(eventTime);
        }
      }

      for (String person : request.getOptionalAttendees()) {
        if (event.getAttendees().contains(person)
            && !meetingTimesWithOptionals.contains(eventTime)) {
          meetingTimesWithOptionals.add(eventTime);
        }
      }
    }
    if (meetingTimes.size() == 0 && meetingTimesWithOptionals.size() == 0) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    Collections.sort(meetingTimesWithOptionals, TimeRange.ORDER_BY_START);
    Collections.sort(meetingTimes, TimeRange.ORDER_BY_START);

    if (meetingTimesWithOptionals.size() > 0) {
      withOptionalSlots = getOpenTimes(meetingTimesWithOptionals, request);
      if (withOptionalSlots.size() > 0) {
        return Collections.unmodifiableList(withOptionalSlots);
      } else if (meetingTimes.size() > 0){
          requiredSlots = getOpenTimes(meetingTimes, request);
      }
    }
    return Collections.unmodifiableList(requiredSlots);
  }
  
  /**
  * Helper function that returns open times based on busy times and new meeting request. 
  *
  * @param busyTimes: list of TimeRanges during which the meeting cannot be held
  * @param request: Details of the new meeting which we try to find time for
  * @return unmodifiable list of open times of given duration
  */
  private List<TimeRange> getOpenTimes(List<TimeRange> busyTimes, MeetingRequest request) {
    List<TimeRange> openTimes = new ArrayList<TimeRange>();

    // Add beginning of day if possible.
    int firstMeetingTime = busyTimes.get(0).start();
    if (firstMeetingTime > request.getDuration()) {
      openTimes.add(TimeRange.fromStartDuration(0, firstMeetingTime));
    }

    // Add open times between meetings
    int lastMeetingEnd = busyTimes.get(0).end();
    boolean meetingWithinMeeting = false;
    for (int i = 0; i < busyTimes.size() - 1; i++) {
      TimeRange currentMeeting = meetingWithinMeeting ? busyTimes.get(i - 1) : busyTimes.get(i);
      TimeRange nextMeeting = busyTimes.get(i + 1);
      int availableTime = nextMeeting.start() - currentMeeting.end();

      if (availableTime >= request.getDuration()) {
        openTimes.add(TimeRange.fromStartDuration(currentMeeting.end(), availableTime));
      }

      if (nextMeeting.end() > lastMeetingEnd) {
        lastMeetingEnd = nextMeeting.end();
      }
      // This variable is True when the current meeting ends AFTER the next meeting ends.
      meetingWithinMeeting = currentMeeting.end() > nextMeeting.end();
    }

    // Add end of day if possible.
    if ((lastMeetingEnd + request.getDuration()) < TimeRange.END_OF_DAY) {
      openTimes.add(TimeRange.fromStartEnd(lastMeetingEnd, TimeRange.END_OF_DAY, true));
    }
    return Collections.unmodifiableList(openTimes);
  }
}

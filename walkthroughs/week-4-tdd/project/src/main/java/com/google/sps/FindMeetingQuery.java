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
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections; 
import java.util.Collection;
import java.util.Comparator;


public final class FindMeetingQuery {
  int startOfDay = TimeRange.START_OF_DAY;
  int endOfDay = TimeRange.END_OF_DAY;
  List<TimeRange> ansArray = new ArrayList<TimeRange>();
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> timesAvailableForMeeting = new ArrayList<TimeRange>();

    if(request.getDuration() > TimeRange.WHOLE_DAY.duration()){
        return Arrays.asList();
    }

    // check for no attendees in request then return the whole day
    if(request.getAttendees().isEmpty() || events.isEmpty()){
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    List<Event> subEvents = new ArrayList<Event>();
    for(Event event: events){
        for(String name: event.getAttendees()){
            if(request.getAttendees().contains(name)){
                subEvents.add(event);
            }            
        }
    }

    if(subEvents.isEmpty()){
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    List<TimeRange> newTimeRangeListFromEvents = helperToMergeContiguoudEvents(events);

    TimeRange edge = TimeRange.fromStartEnd(0, 0, false);
    for(TimeRange timeRange: newTimeRangeListFromEvents){
        TimeRange leading_availability = TimeRange.fromStartEnd(startOfDay, timeRange.start(), false);
        TimeRange trailing_availability = TimeRange.fromStartEnd(timeRange.end(), endOfDay, true);
        edge = trailing_availability;
        if(request.getDuration() <= leading_availability.duration()) timesAvailableForMeeting.add(leading_availability);
        if(request.getDuration() <= trailing_availability.duration()) timesAvailableForMeeting.add(trailing_availability);
    }
    
    List<TimeRange> newResultTimes = helperToMergeTimeAvailabilities(timesAvailableForMeeting);
    //newResultTimes.add(edge);
    for(TimeRange item: newResultTimes){
        //newResultTimes.add(edge);
        System.out.println(item);
        //System.out.println(request.getDuration());
        if(request.getDuration() <= item.duration())ansArray.add(item);
    }
    // timesAvailableForMeeting = newResultTimes;
    // List<TimeRange> alreadyTouched = new ArrayList<TimeRange>();
    // TimeRange begin = timesAvailableForMeeting.get(0);
    // for(int i= 0; i < timesAvailableForMeeting.size()-1; i++){
    //     for(int j = 1; j < timesAvailableForMeeting.size(); j++){
    //         if(timesAvailableForMeeting.get(i).overlaps(timesAvailableForMeeting.get(j))){
    //             if(!alreadyTouched.contains(timesAvailableForMeeting.get(i))){
    //                 ansArray.add(timesAvailableForMeeting.get(i));
    //             }
    //             //alreadyTouched.add(timesAvailableForMeeting.get(i));
    //             alreadyTouched.add(timesAvailableForMeeting.get(j));
    //         }
    //     }
    // }

    return ansArray;
  }

  private List<TimeRange> helperToMergeTimeAvailabilities(List<TimeRange> timeStamps){
      List<TimeRange> resultArray = new ArrayList<TimeRange>();

      Collections.sort(timeStamps, TimeRange.ORDER_BY_START);
      int index = 0;
      for (int i=1; i<timeStamps.size(); i++)  
        {   
            if(timeStamps.get(index).overlaps(timeStamps.get(i)))
            //if (eventTimeRange.get(index).end() >=  eventTimeRange.get(i).start())  
            {  
                int end = Math.min(timeStamps.get(index).end(), timeStamps.get(i).end());
                int start = Math.max(timeStamps.get(index).start(), timeStamps.get(i).start());
                timeStamps.set(index, TimeRange.fromStartEnd(start, end, false));  
            }  
            else { 
                //timeStamps.set(index, timeStamps.get(i));  
                index++; 
            }     
        }
        for(int i=0; i<= index; i++){
            resultArray.add(timeStamps.get(i));
           // System.out.println(timeStamps.get(i));
        }

        return resultArray;

  }

  private List<TimeRange> helperToMergeContiguoudEvents(Collection<Event> inputArray){
        List<TimeRange> eventTimeRange = new ArrayList<TimeRange>();
        List<TimeRange> mergedEventAvailabilityTimes = new ArrayList<TimeRange>();

        for(Event i:inputArray){
            eventTimeRange.add(i.getWhen());
        }

        Collections.sort(eventTimeRange, TimeRange.ORDER_BY_START);

        int index = 0; 

        for (int i=1; i<eventTimeRange.size(); i++)  
        {   
            if(eventTimeRange.get(index).overlaps(eventTimeRange.get(i)))
            //if (eventTimeRange.get(index).end() >=  eventTimeRange.get(i).start())  
            {  
                int end = Math.max(eventTimeRange.get(index).end(), eventTimeRange.get(i).end());
                int start = Math.min(eventTimeRange.get(index).start(), eventTimeRange.get(i).start());
                eventTimeRange.set(index, TimeRange.fromStartEnd(start, end, false));  
            }  
            else { 
                //eventTimeRange.set(index, eventTimeRange.get(i));  
                index++; 
            }     
        }
        for(int i=0; i<= index; i++){
            mergedEventAvailabilityTimes.add(eventTimeRange.get(i));
            //System.out.println(eventTimeRange.get(i));
        }
        
       return mergedEventAvailabilityTimes;   
  }


}

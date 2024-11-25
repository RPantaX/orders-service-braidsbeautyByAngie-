package com.braidsbeautyByAngie.aggregates.response.rest.reservations;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ScheduleDTO {
    private Long scheduleId;
    private LocalDate scheduleDate;
    private LocalTime scheduleHourStart;
    private LocalTime scheduleHourEnd;
    private String scheduleState;
    private Long employeeId;
}

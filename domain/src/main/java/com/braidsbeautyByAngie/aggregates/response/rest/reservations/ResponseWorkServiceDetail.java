package com.braidsbeautyByAngie.aggregates.response.rest.reservations;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseWorkServiceDetail {
    private ServiceDTO serviceDTO;
    private ScheduleDTO scheduleDTO;
}

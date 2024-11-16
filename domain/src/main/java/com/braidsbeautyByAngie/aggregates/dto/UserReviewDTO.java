package com.braidsbeautyByAngie.aggregates.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserReviewDTO {
    private Long userReviewId;
    private String reviewRatingValue;
    private String reviewComment;
    private Long userId;

}

package com.reactivespring.domain;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Review {

    @Id
    private String reviewId;

    @NotNull(message = "rating.movieInfoId: must not be null")
    private Long movieInfoId;
    private String comment;
    //@Min(value = 0L, message = "rating.negative : please pass a non-negative value")

    @Min(value = 0L, message = "rating.negative: please pass a none negative value")
    private Double rating;
}

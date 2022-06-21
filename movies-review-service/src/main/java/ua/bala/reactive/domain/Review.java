package ua.bala.reactive.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Review {

    @Id
    private String reviewId;
    @NotNull(message = "Review.movieInfoId : must not be a null")
    private Long movieInfoId;
    private String comment;
    @Min(value = 0L, message = "Review.rating : rating is negative and please pass a non-negative value")
    private Double rating;
}

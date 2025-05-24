package pl.edu.pwr.commandservice.entity.review;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ReviewId implements Serializable {
    private Integer userId;
    private Integer recipeId;
}

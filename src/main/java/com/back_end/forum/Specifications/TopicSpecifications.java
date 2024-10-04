package com.back_end.forum.Specifications;

import com.back_end.forum.model.Tag;
import com.back_end.forum.model.Topic;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TopicSpecifications {

    public static Specification<Topic> withTitle(String title){
        return (root, query, criteriaBuilder) ->
                title == null ? criteriaBuilder.conjunction() :
                criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    public static Specification<Topic> withCategory(String category){
        return (root, query, criteriaBuilder) ->
                category == null ? criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("category").get("name"), category);
    }

    public static Specification<Topic> withTags(List<String> tags) {
        return (root, query, criteriaBuilder) -> {
            if (tags == null || tags.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            Join<Topic, Tag> tagJoin = root.join("tags");

            for (String tag : tags) {
                predicates.add(criteriaBuilder.equal(tagJoin.get("name"), tag.trim()));
            }

            query.distinct(true);
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static  Specification<Topic> withRatingBetween(Integer minRating, Integer maxRating){
        return (root, query, criteriaBuilder) -> {
            if(minRating == null && maxRating == null) {
                return criteriaBuilder.conjunction();
            } else if (minRating == null) {
                return criteriaBuilder.le(root.get("rating"), maxRating);
            } else if (maxRating == null) {
                return criteriaBuilder.ge(root.get("rating"), minRating);
            } else {
                return criteriaBuilder.between(root.get("rating"), minRating, maxRating);
            }
        };
    }

}

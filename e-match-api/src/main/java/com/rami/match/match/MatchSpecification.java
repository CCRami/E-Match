package com.rami.match.match;

import org.springframework.data.jpa.domain.Specification;

public class MatchSpecification {

    public static Specification<Match> withOwnerId(Integer ownerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
    }
}
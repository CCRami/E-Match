package com.rami.match.match;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface MatchRepository extends JpaRepository<Match, Integer>, JpaSpecificationExecutor<Match> {
    @Query("""
            SELECT match
            FROM Match match
            WHERE match.archived = false
            AND match.shareable = true
            AND match.owner.id != :userId
            """)
    Page<Match> findAllDisplayableMatches(Pageable pageable, Integer userId);
}
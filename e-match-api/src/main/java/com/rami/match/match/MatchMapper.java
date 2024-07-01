package com.rami.match.match;

import com.rami.match.file.FileUtils;
import org.springframework.stereotype.Service;

@Service
public class MatchMapper {
    public Match toMatch(MatchRequest request) {
        return Match.builder()
                .id(request.id())
                .HomeTeam(request.HomeTeam())
                .Stadium(request.Stadium())
                .League(request.League())
                .archived(false)
                .shareable(request.shareable())
                .build();
    }

    public MatchResponse toMatchResponse(Match match) {
        return MatchResponse.builder()
                .id(match.getId())
                .HomeTeam(match.getHomeTeam())
                .Stadium(match.getStadium())
                .League(match.getLeague())
                .rate(match.getRate())
                .archived(match.isArchived())
                .shareable(match.isShareable())
                .owner(match.getOwner().fullName())
                .cover(FileUtils.readFileFromLocation(match.getMatchCover()))
                .build();
    }

}
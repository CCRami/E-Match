package com.rami.match.match;

import com.rami.match.user.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record MatchRequest(
        Integer id,
        @NotNull(message = "100")
        @NotEmpty(message = "100")
        String HomeTeam,
        @NotNull(message = "101")
        @NotEmpty(message = "101")
        User owner,
        @NotNull(message = "102")
        @NotEmpty(message = "102")
        String Stadium,
        @NotNull(message = "103")
        @NotEmpty(message = "103")
        String League,
        boolean shareable
) {
}
package com.rami.match.feedback;

import com.rami.match.match.Match;
import com.rami.match.match.MatchRepository;
import com.rami.match.common.PageResponse;
import com.rami.match.exception.OperationNotPermittedException;
import com.rami.match.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedBackRepository feedBackRepository;
    private final MatchRepository matchRepository;
    private final FeedbackMapper feedbackMapper;

    public Integer save(FeedbackRequest request, Authentication connectedUser) {
        Match match = matchRepository.findById(request.matchId())
                .orElseThrow(() -> new EntityNotFoundException("No match found with ID:: " + request.matchId()));
        if (match.isArchived() || !match.isShareable()) {
            throw new OperationNotPermittedException("You cannot give a feedback for and archived or not shareable match");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(match.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot give feedback to your own match");
        }
        Feedback feedback = feedbackMapper.toFeedback(request);
        return feedBackRepository.save(feedback).getId();
    }

    @Transactional
    public PageResponse<FeedbackResponse> findAllFeedbacksByMatch(Integer matchId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size);
        User user = ((User) connectedUser.getPrincipal());
        Page<Feedback> feedbacks = feedBackRepository.findAllByMatchId(matchId, pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
                .toList();
        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );

    }
}
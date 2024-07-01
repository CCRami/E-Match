package com.rami.match.match;

import com.rami.match.common.PageResponse;

import com.rami.match.exception.OperationNotPermittedException;

import com.rami.match.file.FileStorageService;

import com.rami.match.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static com.rami.match.match.MatchSpecification.withOwnerId;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;
    private final FileStorageService fileStorageService;

    public Integer save(MatchRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Match match = matchMapper.toMatch(request);
        match.setOwner(user);
        return matchRepository.save(match).getId();
    }

    public MatchResponse findById(Integer matchId) {
        return matchRepository.findById(matchId)
                .map(matchMapper::toMatchResponse)
                .orElseThrow(() -> new EntityNotFoundException("No match found with ID:: " + matchId));
    }

    public PageResponse<MatchResponse> findAllMatches(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Match> matches = matchRepository.findAllDisplayableMatches(pageable, user.getId());
        List<MatchResponse> matchesResponse = matches.stream()
                .map(matchMapper::toMatchResponse)
                .toList();
        return new PageResponse<>(
                matchesResponse,
                matches.getNumber(),
                matches.getSize(),
                matches.getTotalElements(),
                matches.getTotalPages(),
                matches.isFirst(),
                matches.isLast()
        );
    }

    public PageResponse<MatchResponse> findAllMatchesByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Match> matches = matchRepository.findAll(withOwnerId(user.getId()), pageable);
        List<MatchResponse> matchesResponse = matches.stream()
                .map(matchMapper::toMatchResponse)
                .toList();
        return new PageResponse<>(
                matchesResponse,
                matches.getNumber(),
                matches.getSize(),
                matches.getTotalElements(),
                matches.getTotalPages(),
                matches.isFirst(),
                matches.isLast()
        );
    }

    public Integer updateShareableStatus(Integer matchId, Authentication connectedUser) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("No match found with ID:: " + matchId));
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(match.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update others matches shareable status");
        }
        match.setShareable(!match.isShareable());
        matchRepository.save(match);
        return matchId;
    }

    public Integer updateArchivedStatus(Integer matchId, Authentication connectedUser) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("No match found with ID:: " + matchId));
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(match.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update others matches archived status");
        }
        match.setArchived(!match.isArchived());
        matchRepository.save(match);
        return matchId;
    }





    public void uploadMatchCoverPicture(MultipartFile file, Authentication connectedUser, Integer matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("No match found with ID:: " + matchId));
        User user = ((User) connectedUser.getPrincipal());
        var profilePicture = fileStorageService.saveFile(file, matchId, user.getId());
        match.setMatchCover(profilePicture);
        matchRepository.save(match);
    }



}
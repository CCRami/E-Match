package com.rami.match.match;

import com.rami.match.common.PageResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Match")
public class MatchController {

    private final MatchService service;

    @PostMapping
    public ResponseEntity<Integer> saveMatch(
            @Valid @RequestBody MatchRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.save(request, connectedUser));
    }

    @GetMapping("/{match-id}")
    public ResponseEntity<MatchResponse> findMatchById(
            @PathVariable("match-id") Integer matchId
    ) {
        return ResponseEntity.ok(service.findById(matchId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<MatchResponse>> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllMatches(page, size, connectedUser));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<MatchResponse>> findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllMatchesByOwner(page, size, connectedUser));
    }


    @PatchMapping("/shareable/{match-id}")
    public ResponseEntity<Integer> updateShareableStatus(
            @PathVariable("match-id") Integer matchId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.updateShareableStatus(matchId, connectedUser));
    }

    @PatchMapping("/archived/{match-id}")
    public ResponseEntity<Integer> updateArchivedStatus(
            @PathVariable("match-id") Integer matchId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.updateArchivedStatus(matchId, connectedUser));
    }



    @PostMapping(value = "/cover/{match-id}", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadMatchCoverPicture(
            @PathVariable("match-id") Integer matchId,
            @Parameter()
            @RequestPart("file") MultipartFile file,
            Authentication connectedUser
    ) {
        service.uploadMatchCoverPicture(file, connectedUser, matchId);
        return ResponseEntity.accepted().build();
    }
}
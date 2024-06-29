package com.utkarsh.stack.book.feedback;

import com.utkarsh.stack.book.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;
    @PostMapping
    public ResponseEntity<Integer> saveFeedback(
            @RequestBody @Valid FeedbackRequest feedbackRequest,
            Authentication connectedUser
    ){
        return new ResponseEntity<>(feedbackService.save(feedbackRequest, connectedUser), HttpStatus.CREATED);
    }

    @GetMapping("/boook/{book-id}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findFeedbacksOfBook(
            @PathVariable("book-id") Integer bookId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ){
        return new ResponseEntity<>(feedbackService.findFeedbackOfBook(bookId, page, size, connectedUser), HttpStatus.OK);
    }
}

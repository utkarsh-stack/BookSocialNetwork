package com.utkarsh.stack.book.feedback;

import com.utkarsh.stack.book.book.Book;
import com.utkarsh.stack.book.book.BookRepository;
import com.utkarsh.stack.book.common.PageResponse;
import com.utkarsh.stack.book.exception.OperationNotPermittedException;
import com.utkarsh.stack.book.user.User;
import com.utkarsh.stack.book.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    public Integer save(FeedbackRequest feedbackRequest, Authentication connectedUser) {
        Book book = bookRepository.findById(feedbackRequest.bookId())
                .orElseThrow(()->new EntityNotFoundException("Invalid Book Id"));
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("Book not eligible for feedback");
        }
        User user = (User) connectedUser.getPrincipal();
        if(Objects.equals(user.getId(), book.getOwner().getId())){
            throw new OperationNotPermittedException("You can not provide feedback to your own book");
        }
        Feedback feedback = Feedback.builder()
                .note(feedbackRequest.note())
                .comment(feedbackRequest.comment())
                .book(book).build();
        return feedbackRepository.save(feedback).getId();

    }

    public PageResponse<FeedbackResponse> findFeedbackOfBook(Integer bookId, int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
//        User user = userRepository.findByEmail(connectedUser.getPrincipal().toString()).get();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Feedback> feedbacks = feedbackRepository.findFeebackByBookId(pageable, bookId);
        List<FeedbackResponse> feedbackResponseList = feedbacks.stream()
                .map(feedback -> FeedbackMapper.toFeedbackResponse(feedback,user.getId() ))
                .toList();
        return new PageResponse<>(
                feedbackResponseList,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}

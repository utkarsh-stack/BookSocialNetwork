package com.utkarsh.stack.book.feedback;

import com.utkarsh.stack.book.book.Book;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackResponse {
    private Double note;
    private String comment;
    private boolean ownFeedback;
}

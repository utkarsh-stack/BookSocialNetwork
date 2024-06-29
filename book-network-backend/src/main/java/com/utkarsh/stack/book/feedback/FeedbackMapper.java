package com.utkarsh.stack.book.feedback;

import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {

    public static FeedbackResponse toFeedbackResponse(Feedback feedback, Integer userId){
        return FeedbackResponse.builder()
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(userId, feedback.getCreatedBy()))
                .build();
    }
}

package com.utkarsh.stack.book.feedback;

import jakarta.validation.constraints.*;

public record FeedbackRequest (
    @NotNull(message = "200")
    @NotEmpty(message = "200")
    Integer bookId,
    @Positive(message = "201")
    @Min(value = 0,message = "201")
    @Max(value = 5, message = "201")
    Double note,
    @NotNull(message = "202")
    @NotEmpty(message = "202")
    @NotBlank(message = "202")
    String comment
){}

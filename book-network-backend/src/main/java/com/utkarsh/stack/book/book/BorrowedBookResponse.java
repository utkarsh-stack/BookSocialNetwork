package com.utkarsh.stack.book.book;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BorrowedBookResponse {
    private Integer id;
    private String title;
    private String isbn;
    private String authorName;
    private boolean returned;
    private boolean returnApproved;
    private Double rate;
}

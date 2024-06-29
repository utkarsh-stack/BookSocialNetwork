package com.utkarsh.stack.book.book;

import com.utkarsh.stack.book.user.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {
    private Integer id;
    private String title;
    private String isbn;
    private String synopsis;
    private String authorName;
    private String owner;
    private boolean archived;
    private boolean shareable;
    private Double rate;
    private byte[] cover;
}

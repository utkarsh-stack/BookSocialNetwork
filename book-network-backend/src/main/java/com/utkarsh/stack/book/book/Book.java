package com.utkarsh.stack.book.book;

import com.utkarsh.stack.book.common.BaseEntity;
import com.utkarsh.stack.book.feedback.Feedback;
import com.utkarsh.stack.book.history.BookTransactionHistory;
import com.utkarsh.stack.book.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Book extends BaseEntity {
    private String title;
    private String bookCover;
    private String isbn;
    private String synopsis;
    private String authorName;
    private boolean archived;
    private boolean shareable;

    @ManyToOne
    @JoinColumn(name="owner_id")
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> histories;

    @Transient
    public Double getRate(){
        if(feedbacks == null || feedbacks.isEmpty())
            return 0.0;
        double rate = this.feedbacks.stream().mapToDouble(Feedback::getNote).average().orElse(0.0);
        return Math.round(rate*10.0)/10.0;
    }
}

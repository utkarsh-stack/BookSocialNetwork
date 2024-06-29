package com.utkarsh.stack.book.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    @Query("""
            SELECT feedback
            FROM Feedback feedback
            WHERE
            feedback.book.id=:bookId
            """)
    Page<Feedback> findFeebackByBookId(Pageable pageable, @Param("bookId") Integer bookId);
}

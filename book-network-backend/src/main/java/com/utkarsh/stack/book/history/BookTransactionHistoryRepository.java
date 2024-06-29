package com.utkarsh.stack.book.history;

import com.utkarsh.stack.book.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {
    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.user.id= :userId
            """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);

    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.book.owner.id= :userId
            """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer userId);

    @Query("""
            SELECT 
            (COUNT (*) > 0) AS isBorrowed
            FROM BookTransactionHistory history
            WHERE history.book.id = :bookId
            AND history.user.id = :userId
            """)
    boolean isAlreadyBorrowed(Integer bookId, Integer userId);

    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.user.id= :userId
            AND history.book.id = :bookId
            AND history.returned = false
            AND history.returnApproved = false
            """)
    Optional<BookTransactionHistory> findByBookIdAndUserId(Integer bookId, Integer userId);

    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.book.owner.id= :userId
            AND history.book.id = :bookId
            AND history.returned = true
            AND history.returnApproved = false
            """)
    Optional<BookTransactionHistory> findPendingReturnApproval(Integer bookId, Integer userId);
}

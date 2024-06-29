package com.utkarsh.stack.book.book;

import com.utkarsh.stack.book.history.BookTransactionHistory;
import com.utkarsh.stack.book.file.FileUtils;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {
    public Book toBook(BookRequest bookRequest) {
        Book book = Book.builder()
                .id(bookRequest.id())
                .title(bookRequest.title())
                .synopsis(bookRequest.synopsis())
                .shareable(bookRequest.shareable())
                .authorName(bookRequest.authorName())
                .isbn(bookRequest.isbn())
                .archived(false)
                .build();
        return book;
    }
    public static BookResponse toBookResponse(Book book){
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .rate(book.getRate())
                .owner(book.getOwner().getFullName())
                .isbn(book.getIsbn())
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .cover(FileUtils.readFileFromLocation(book.getBookCover()))
                .synopsis(book.getSynopsis()).build();
    }

    public static BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history){
        return BorrowedBookResponse.builder()
                .id(history.getBook().getId())
                .title(history.getBook().getTitle())
                .authorName(history.getBook().getAuthorName())
                .rate(history.getBook().getRate())
                .isbn(history.getBook().getIsbn())
                .returned(history.isReturned())
                .returnApproved(history.isReturnApproved()).build();
    }
}

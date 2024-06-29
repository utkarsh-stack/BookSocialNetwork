package com.utkarsh.stack.book.book;

import com.utkarsh.stack.book.common.PageResponse;
import com.utkarsh.stack.book.exception.OperationNotPermittedException;
import com.utkarsh.stack.book.file.FileUploadService;
import com.utkarsh.stack.book.history.BookTransactionHistory;
import com.utkarsh.stack.book.history.BookTransactionHistoryRepository;
import com.utkarsh.stack.book.user.User;
import com.utkarsh.stack.book.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookMapper bookMapper;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final FileUploadService fileUploadService;
    public int save(BookRequest bookRequest, Authentication auth) {
        log.info("Authentication: "+auth.getName());
        log.info("Authentication: "+auth);
        log.info("Authentication: "+auth);
//        User connectedUser = userRepository.findByEmail(auth.getPrincipal().toString()).get();
        User connectedUser = ((User) auth.getPrincipal());
        Book book = bookMapper.toBook(bookRequest);
        book.setOwner(connectedUser);
        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId).map(BookMapper::toBookResponse)
                .orElseThrow(()->new EntityNotFoundException("Book not found with Id: "+ bookId));

    }

    public PageResponse<BookResponse> findAll(int page, int size, Authentication connectedUser){
        User user = (User) connectedUser.getPrincipal();
//        User user = userRepository.findByEmail(connectedUser.getPrincipal().toString()).get();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponses = books.stream().map(BookMapper::toBookResponse).toList();
        return new PageResponse<>(
          bookResponses,
          books.getNumber(),
          books.getSize(),
          books.getTotalElements(),
          books.getTotalPages(),
          books.isFirst(),
          books.isLast()
        );
    }
    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser){
        User user = (User) connectedUser.getPrincipal();
//        User user = userRepository.findByEmail(connectedUser.getPrincipal().toString()).get();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponses = books.stream().map(BookMapper::toBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
//        User user = userRepository.findByEmail(connectedUser.getPrincipal().toString()).get();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> borrowedBooksTransaction = transactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> borrowedBookResponses = borrowedBooksTransaction.stream().map(BookMapper::toBorrowedBookResponse).toList();
        return new PageResponse<>(
                borrowedBookResponses,
                borrowedBooksTransaction.getNumber(),
                borrowedBooksTransaction.getSize(),
                borrowedBooksTransaction.getTotalElements(),
                borrowedBooksTransaction.getTotalPages(),
                borrowedBooksTransaction.isFirst(),
                borrowedBooksTransaction.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
//        User user = userRepository.findByEmail(connectedUser.getPrincipal().toString()).get();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> borrowedBooksTransaction = transactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> borrowedBookResponses = borrowedBooksTransaction.stream().map(BookMapper::toBorrowedBookResponse).toList();
        return new PageResponse<>(
                borrowedBookResponses,
                borrowedBooksTransaction.getNumber(),
                borrowedBooksTransaction.getSize(),
                borrowedBooksTransaction.getTotalElements(),
                borrowedBooksTransaction.getTotalPages(),
                borrowedBooksTransaction.isFirst(),
                borrowedBooksTransaction.isLast()
        );
    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No book found with id: "+ bookId));
        User user = (User) connectedUser.getPrincipal();
//        User user = userRepository.findByEmail(connectedUser.getPrincipal().toString()).get();
        if(!Objects.equals(book.getOwner().getId(), user.getId())){
            throw new OperationNotPermittedException("You are not authorized to update this book");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }
    public Integer updateArchiveStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No book found with id: "+ bookId));
        User user = (User) connectedUser.getPrincipal();
//        User user = userRepository.findByEmail(connectedUser.getPrincipal().toString()).get();
        if(!Objects.equals(book.getOwner().getId(), user.getId())){
            throw new OperationNotPermittedException("You are not authorized to update this book");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No book found with id: "+ bookId));
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("Book is not available for borrowing");
        }
        User user = (User) connectedUser.getPrincipal();
//        User user = userRepository.findByEmail(connectedUser.getPrincipal().toString()).get();
        if(Objects.equals(book.getOwner().getId(), user.getId())){
            throw new OperationNotPermittedException("You can not borrow your own book");
        }
        boolean isAlreadyBorrowed = transactionHistoryRepository.isAlreadyBorrowed(bookId, user.getId());
        if(isAlreadyBorrowed){
            throw new OperationNotPermittedException("You have already borrowed this book");
        }
        BookTransactionHistory transactionHistory = BookTransactionHistory.builder()
                .book(book)
                .user(user)
                .returned(false)
                .returnApproved(false).build();
        return transactionHistoryRepository.save(transactionHistory).getId();
    }

    public Integer returnBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No book found with id: "+ bookId));
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("Book is not available for borrowing or return");
        }
        User user = (User) connectedUser.getPrincipal();
//        User user = userRepository.findByEmail(connectedUser.getPrincipal().toString()).get();
        if(Objects.equals(book.getOwner().getId(), user.getId())){
            throw new OperationNotPermittedException("You can not borrow your own book");
        }
        BookTransactionHistory history = transactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(()->new EntityNotFoundException("You have either not borrowed this book or have already returned"));
        history.setReturned(true);
        return transactionHistoryRepository.save(history).getId();
    }

    public Integer approveReturn(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No book found with id: "+ bookId));
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("Book is not available for borrowing or return");
        }
        User user = (User) connectedUser.getPrincipal();
//        User user = userRepository.findByEmail(connectedUser.getPrincipal().toString()).get();
        if(!Objects.equals(book.getOwner().getId(), user.getId())){
            throw new OperationNotPermittedException("You are not the owner of bookID: "+ bookId);
        }
        BookTransactionHistory history = transactionHistoryRepository.findPendingReturnApproval(bookId, user.getId())
                .orElseThrow(()->new EntityNotFoundException("This book was either never borrowed or not returned, hence no pending approvals are there for book id: "+ bookId));
        history.setReturnApproved(true);
        return transactionHistoryRepository.save(history).getId();
    }

    public String uploadCover(MultipartFile file, Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No book found with id: "+ bookId));
        User user = (User) connectedUser.getPrincipal();
//        User user = userRepository.findByEmail(connectedUser.getPrincipal().toString()).get();
        if(!Objects.equals(book.getOwner().getId(), user.getId())){
            throw new OperationNotPermittedException("You are not the owner of bookID: "+ bookId);
        }
        String bookCoverPath = fileUploadService.saveFile(file, user.getId());
        book.setBookCover(bookCoverPath);
        bookRepository.save(book);
        return bookCoverPath;
    }
}

package com.utkarsh.stack.book.book;

import com.utkarsh.stack.book.common.PageResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.Multipart;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Books")
public class BookController {

    private final BookService bookService;

    @PostMapping("save")
    public ResponseEntity<Integer> saveBook(
            @RequestBody @Valid BookRequest bookRequest,
            Authentication connectedUser
    ){
        return new ResponseEntity<>(
                bookService.save(bookRequest, connectedUser),
                HttpStatus.CREATED);
    }

    @GetMapping("{book-id}")
    public ResponseEntity<BookResponse> getBook(
            @PathVariable("book-id") Integer bookId
    ){
        return new ResponseEntity<>(bookService.findById(bookId), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ){
        return new ResponseEntity<>(bookService.findAll(page, size, connectedUser), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ){
        return new ResponseEntity<>(bookService.findAllBooksByOwner(page, size, connectedUser), HttpStatus.OK);
    }
    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ){
        return new ResponseEntity<>(bookService.findAllBorrowedBooks(page, size, connectedUser), HttpStatus.OK);
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ){
        return new ResponseEntity<>(bookService.findAllReturnedBooks(page, size, connectedUser), HttpStatus.OK);
    }

    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateShareableStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ){
        return new ResponseEntity<>(bookService.updateShareableStatus(bookId, connectedUser), HttpStatus.OK);
    }

    @PatchMapping("/archive/{book-id}")
    public ResponseEntity<Integer> updateArchiveStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ){
        return new ResponseEntity<>(bookService.updateArchiveStatus(bookId, connectedUser), HttpStatus.OK);
    }

    @PostMapping("/borrow/{book-id}")
    public ResponseEntity<Integer> borrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ){
        return new ResponseEntity<>(bookService.borrowBook(bookId, connectedUser), HttpStatus.OK);
    }
    @PatchMapping("/return/{book-id}")
    public ResponseEntity<Integer> returnBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ){
        return new ResponseEntity<>(bookService.returnBook(bookId, connectedUser), HttpStatus.OK);
    }
    @PatchMapping("/return/approve/{book-id}")
    public ResponseEntity<Integer> approveReturn(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ){
        return new ResponseEntity<>(bookService.approveReturn(bookId, connectedUser), HttpStatus.OK);
    }

    @PostMapping(value = "/cover/{book-id}", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadCover(
            @PathVariable("book-id") Integer bookId,
            @Parameter()
            @RequestPart("file") MultipartFile file,
            Authentication connectedUser
            ){
        bookService.uploadCover(file, bookId, connectedUser);
        return new ResponseEntity<>(bookService.uploadCover(file, bookId, connectedUser), HttpStatus.OK);
    }

}

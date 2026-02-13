package auca.ac.rw.question1_library_api.controller;

import auca.ac.rw.question1_library_api.model.Book;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

//REST Controller for managing library books
//Provides CRUD operations and search functionality

@RestController
@RequestMapping("/api/books")
public class BookController {

    // In-memory storage for books using ArrayList
    public final List<Book> books = new ArrayList<>();

    // AtomicLong for generating unique IDs in a thread-safe manner
    public final AtomicLong idGenerator = new AtomicLong(3);

    /**
     * Initialize sample data when controller is created
     */
    public BookController() {
        initializeSampleBooks();
    }

    /**
     * Creates three sample books for testing purposes
     */
    public void initializeSampleBooks() {
        books.add(new Book(1L, "Happy Place", "Emily Henry",
                "978-0743273565", 2020));
        books.add(new Book(2L, "Ngunda", "Edition BAKAME",
                "978-0446310789", 1999));
        books.add(new Book(3L, "PETERO NZUKIRA", "Edi",
                "978-0451524935", 1980));
    }

    /**
     * GET /api/books
     * Retrieves all books in the library
     * 
     * @return ResponseEntity containing list of all books with HTTP status 200 OK
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    /**
     * GET /api/books/{id}
     * Retrieves a specific book by its ID
     * 
     * @param id - Book ID to search for
     * @return ResponseEntity containing the book if found with HTTP status 200 OK,
     *         otherwise HTTP status 404 NOT FOUND
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return books.stream()
                .filter(book -> book.getId().equals(id))
                .findFirst()
                .map(book -> new ResponseEntity<>(book, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET /api/books/search?title={title}
     * Searches for books by title (case-insensitive partial match)
     * 
     * @param title - Title to search for
     * @return ResponseEntity containing list of matching books with HTTP status 200
     *         OK
     */
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooksByTitle(@RequestParam String title) {
        List<Book> matchingBooks = books.stream()
                .filter(book -> book.getTitle().toLowerCase()
                        .contains(title.toLowerCase()))
                .collect(Collectors.toList());

        return new ResponseEntity<>(matchingBooks, HttpStatus.OK);
    }

    /**
     * POST /api/books
     * Adds a new book to the library
     * 
     * @param book - Book object from request body (without ID)
     * @return ResponseEntity containing the created book with generated ID
     *         and HTTP status 201 CREATED
     */
    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        // Generate new ID for the book
        book.setId(idGenerator.incrementAndGet());
        books.add(book);
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    /**
     * DELETE /api/books/{id}
     * Deletes a book by its ID
     * 
     * @param id - ID of the book to delete
     * @return ResponseEntity with HTTP status 204 NO CONTENT if successful,
     *         HTTP status 404 NOT FOUND if book doesn't exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        boolean removed = books.removeIf(book -> book.getId().equals(id));

        if (removed) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
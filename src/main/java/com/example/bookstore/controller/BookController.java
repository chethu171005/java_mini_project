package com.example.bookstore.controller;

import com.example.bookstore.dto.BookDTO;
import com.example.bookstore.model.Author;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.AuthorRepository;
import com.example.bookstore.repository.BookRepository;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookController(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @GetMapping
    public Page<BookDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Book> result;
        if (title != null && !title.isBlank()) {
            result = bookRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else if (author != null && !author.isBlank()) {
            result = bookRepository.findByAuthorNameContaining(author, pageable);
        } else {
            result = bookRepository.findAll(pageable);
        }

        return result.map(b -> BookDTO.builder()
                .id(b.getId())
                .title(b.getTitle())
                .isbn(b.getIsbn())
                .pages(b.getPages())
                .authorId(b.getAuthor() != null ? b.getAuthor().getId() : null)
                .authorName(b.getAuthor() != null ? b.getAuthor().getName() : null)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> get(@PathVariable Long id) {
        return bookRepository.findById(id).map(b -> BookDTO.builder()
                        .id(b.getId())
                        .title(b.getTitle())
                        .isbn(b.getIsbn())
                        .pages(b.getPages())
                        .authorId(b.getAuthor() != null ? b.getAuthor().getId() : null)
                        .authorName(b.getAuthor() != null ? b.getAuthor().getName() : null)
                        .build())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BookDTO> create(@RequestBody BookDTO dto) {
        Author author = null;
        if (dto.getAuthorId() != null) {
            author = authorRepository.findById(dto.getAuthorId()).orElse(null);
        } else if (dto.getAuthorName() != null) {
            author = authorRepository.findByName(dto.getAuthorName()).orElse(null);
            if (author == null) {
                author = authorRepository.save(Author.builder().name(dto.getAuthorName()).build());
            }
        }

        Book book = Book.builder()
                .title(dto.getTitle())
                .isbn(dto.getIsbn())
                .pages(dto.getPages())
                .author(author)
                .build();

        Book saved = bookRepository.save(book);

        BookDTO resp = BookDTO.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .isbn(saved.getIsbn())
                .pages(saved.getPages())
                .authorId(saved.getAuthor() != null ? saved.getAuthor().getId() : null)
                .authorName(saved.getAuthor() != null ? saved.getAuthor().getName() : null)
                .build();

        return ResponseEntity.created(URI.create("/api/books/" + resp.getId())).body(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> update(@PathVariable Long id, @RequestBody BookDTO dto) {
        Optional<Book> opt = bookRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Book book = opt.get();
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setPages(dto.getPages());

        if (dto.getAuthorId() != null) {
            authorRepository.findById(dto.getAuthorId()).ifPresent(book::setAuthor);
        } else if (dto.getAuthorName() != null) {
            Author a = authorRepository.findByName(dto.getAuthorName()).orElseGet(() ->
                    authorRepository.save(Author.builder().name(dto.getAuthorName()).build()));
            book.setAuthor(a);
        }

        Book saved = bookRepository.save(book);

        BookDTO resp = BookDTO.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .isbn(saved.getIsbn())
                .pages(saved.getPages())
                .authorId(saved.getAuthor() != null ? saved.getAuthor().getId() : null)
                .authorName(saved.getAuthor() != null ? saved.getAuthor().getName() : null)
                .build();

        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) return ResponseEntity.notFound().build();
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

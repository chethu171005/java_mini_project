package com.example.bookstore.controller;

import com.example.bookstore.model.Author;
import com.example.bookstore.repository.AuthorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {
    private final AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @GetMapping
    public List<Author> all() { return authorRepository.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Author> get(@PathVariable Long id) {
        return authorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Author> create(@RequestBody Author author) {
        Author saved = authorRepository.save(author);
        return ResponseEntity.created(URI.create("/api/authors/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Author> update(@PathVariable Long id, @RequestBody Author updated) {
        return authorRepository.findById(id).map(a -> {
            a.setName(updated.getName());
            authorRepository.save(a);
            return ResponseEntity.ok(a);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!authorRepository.existsById(id)) return ResponseEntity.notFound().build();
        authorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

package com.example.bookstore.config;

import com.example.bookstore.model.Author;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.AuthorRepository;
import com.example.bookstore.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {
    @Bean
    CommandLineRunner init(AuthorRepository authorRepository, BookRepository bookRepository) {
        return args -> {
            Author a1 = authorRepository.save(Author.builder().name("George Orwell").build());
            Author a2 = authorRepository.save(Author.builder().name("J. K. Rowling").build());

            bookRepository.save(Book.builder().title("1984").isbn("978-0451524935").pages(328).author(a1).build());
            bookRepository.save(Book.builder().title("Animal Farm").isbn("978-0451526342").pages(112).author(a1).build());
            bookRepository.save(Book.builder().title("Harry Potter and the Sorcerer's Stone").isbn("978-0590353427").pages(309).author(a2).build());
        };
    }
}

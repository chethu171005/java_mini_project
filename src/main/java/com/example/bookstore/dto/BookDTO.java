package com.example.bookstore.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {
    private Long id;
    private String title;
    private String isbn;
    private Integer pages;
    private Long authorId;
    private String authorName;
}

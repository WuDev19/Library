package com.example.library.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "book_import_items",
    uniqueConstraints = @UniqueConstraint(columnNames = {"book_import_id", "book_copy_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PUBLIC)
public class BookImportItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_import_item_id")
    private Long bookImportItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_import_id", nullable = false)
    private BookImport bookImport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_id", nullable = false)
    private BookCopy bookCopy;
}

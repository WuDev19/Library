package com.example.bookborrowservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "book_imports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookImport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_import_id")
    private Long bookImportId;

    @Column(name = "import_code", nullable = false, unique = true, length = 30)
    private String importCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "imported_by", nullable = false)
    private Long importedBy; // User ID của thủ thư thực hiện nhập sách

    @Column(name = "import_date", nullable = false)
    private LocalDate importDate;

    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        if (importDate == null) {
            importDate = LocalDate.now();
        }
    }
}

import React, { useState } from 'react';
import type { Book, Category } from '../../types';

interface BookCatalogProps {
  books: Book[];
  categories: Category[];
  page?: number;
  pageSize?: number;
  sizeOfPage?: number;
  onPageChange?: (newPage: number) => void;
  onQuickBorrow: (book: Book) => void;
}

export const BookCatalog: React.FC<BookCatalogProps> = ({
  books,
  categories,
  page = 0,
  pageSize = 10,
  sizeOfPage = 0,
  onPageChange,
  onQuickBorrow,
}) => {
  const [searchKeyword, setSearchKeyword] = useState<string>('');
  const [selectedCategoryFilter, setSelectedCategoryFilter] = useState<number>(0);

  const filteredBooks = books.filter((b) => {
    const matchesSearch =
      b.title.toLowerCase().includes(searchKeyword.toLowerCase()) ||
      b.author.toLowerCase().includes(searchKeyword.toLowerCase()) ||
      (b.isbn && b.isbn.includes(searchKeyword));
    const matchesCat = selectedCategoryFilter === 0 || b.categoryId === selectedCategoryFilter;
    return matchesSearch && matchesCat;
  });

  return (
    <div>
      <div style={{ display: 'flex', gap: '16px', marginBottom: '24px' }}>
        <input
          type="text"
          placeholder="Nhập tên sách, tác giả hoặc từ khóa tra cứu..."
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
          style={{ flex: 1 }}
        />
        <select
          value={selectedCategoryFilter}
          onChange={(e) => setSelectedCategoryFilter(Number(e.target.value))}
          style={{ width: '220px' }}
        >
          <option value={0}>-- Tất cả danh mục --</option>
          {categories.map((c) => (
            <option key={c.id} value={c.id}>
              {c.name}
            </option>
          ))}
        </select>
      </div>

      {filteredBooks.length === 0 ? (
        <div className="glass-card text-center" style={{ padding: '48px' }}>
          <i
            className="fa-solid fa-magnifying-glass"
            style={{ fontSize: '3rem', color: 'var(--primary)', marginBottom: '14px' }}
          ></i>
          <h3>Không có sách nào khớp với tìm kiếm</h3>
          <p className="text-muted" style={{ marginTop: '6px' }}>
            Database chưa có dữ liệu hoặc không tìm thấy sách phù hợp.
          </p>
        </div>
      ) : (
        <>
          <div className="books-grid">
            {filteredBooks.map((book) => {
              const avail = book.availableCopies ?? book.availableQuantity ?? 0;
              const total = book.totalCopies ?? book.totalQuantity ?? 0;
              return (
                <div key={book.id} className="book-card">
                  <div className="book-cover-placeholder">
                    <i className="fa-solid fa-book"></i>
                  </div>
                  <div className="book-card-body">
                    <div>
                      <div className="badge badge-jade" style={{ marginBottom: '6px' }}>
                        {book.categoryName || 'Sách'}
                      </div>
                      <div className="book-title">{book.title}</div>
                      <div className="book-author">Tác giả: {book.author}</div>
                      <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '12px' }}>
                        {book.description || 'Chưa có thông tin mô tả chi tiết.'}
                      </p>
                    </div>
                    <div>
                      <div
                        style={{
                          fontSize: '0.88rem',
                          fontWeight: 700,
                          color: avail > 0 ? 'var(--primary-hover)' : 'var(--danger)',
                          marginBottom: '10px',
                        }}
                      >
                        Số lượng khả dụng: {avail} / {total} bản
                      </div>
                      <button
                        className="btn btn-primary btn-block"
                        disabled={avail <= 0}
                        onClick={() => onQuickBorrow(book)}
                      >
                        {avail > 0 ? 'Đăng Ký Mượn Sách' : 'Đã Hết Bản Sao'}
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>

          {/* Pagination Controls */}
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              marginTop: '24px',
              paddingTop: '16px',
              borderTop: '1px solid rgba(255, 255, 255, 0.1)',
            }}
          >
            <span className="text-muted" style={{ fontSize: '0.9rem' }}>
              Số lượng trong trang: <strong>{sizeOfPage || books.length}</strong> (Trang {page + 1})
            </span>
            <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
              <button
                className="btn btn-sm btn-secondary"
                disabled={page <= 0}
                onClick={() => onPageChange && onPageChange(page - 1)}
              >
                <i className="fa-solid fa-chevron-left"></i> Trang trước
              </button>
              <span style={{ fontWeight: 600, padding: '0 8px' }}>Trang {page + 1}</span>
              <button
                className="btn btn-sm btn-secondary"
                disabled={books.length < pageSize}
                onClick={() => onPageChange && onPageChange(page + 1)}
              >
                Trang sau <i className="fa-solid fa-chevron-right"></i>
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

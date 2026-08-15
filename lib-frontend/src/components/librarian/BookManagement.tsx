import React, { useState } from 'react';
import type { Book, Category } from '../../types';

interface BookManagementProps {
  books: Book[];
  categories: Category[];
  onOpenAddBook: () => void;
  onOpenEditBook: (book: Book) => void;
  onDeleteBook: (id: number) => void;
  onOpenAddCopy: (book: Book) => void;
}

export const BookManagement: React.FC<BookManagementProps> = ({
  books,
  categories,
  onOpenAddBook,
  onOpenEditBook,
  onDeleteBook,
  onOpenAddCopy,
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
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '24px',
        }}
      >
        <div style={{ display: 'flex', gap: '12px', flex: 1, maxWidth: '600px' }}>
          <input
            type="text"
            placeholder="Tìm kiếm sách theo tên, tác giả, ISBN..."
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
          />
          <select
            value={selectedCategoryFilter}
            onChange={(e) => setSelectedCategoryFilter(Number(e.target.value))}
          >
            <option value={0}>-- Tất cả danh mục --</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </select>
        </div>
        <button className="btn btn-primary" onClick={onOpenAddBook}>
          <i className="fa-solid fa-plus"></i> Thêm Đầu Sách
        </button>
      </div>

      {filteredBooks.length === 0 ? (
        <div className="glass-card text-center" style={{ padding: '48px' }}>
          <i className="fa-solid fa-book-open" style={{ fontSize: '3rem', color: 'var(--primary)', marginBottom: '14px' }}></i>
          <h3>Không tìm thấy dữ liệu đầu sách</h3>
          <p className="text-muted" style={{ marginTop: '6px' }}>
            Database chưa có sách hoặc không có kết quả phù hợp với bộ lọc tìm kiếm.
          </p>
        </div>
      ) : (
        <div className="books-grid">
          {filteredBooks.map((book) => (
            <div key={book.id} className="book-card">
              <div className="book-cover-placeholder">
                <i className="fa-solid fa-book"></i>
              </div>
              <div className="book-card-body">
                <div>
                  <div className="badge badge-jade" style={{ marginBottom: '8px' }}>
                    {book.categoryName || 'Chưa phân loại'}
                  </div>
                  <div className="book-title">{book.title}</div>
                  <div className="book-author">Tác giả: {book.author}</div>
                  <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                    ISBN: {book.isbn || 'N/A'}
                  </div>
                  <div
                    style={{
                      fontSize: '0.88rem',
                      fontWeight: 600,
                      marginTop: '8px',
                      color: 'var(--primary-hover)',
                    }}
                  >
                    Khả dụng: {book.availableCopies ?? 0} / {book.totalCopies ?? 0} bản
                  </div>
                </div>
                <div style={{ display: 'flex', gap: '8px', marginTop: '16px' }}>
                  <button
                    className="btn btn-sm btn-outline-jade"
                    style={{ flex: 1 }}
                    onClick={() => onOpenAddCopy(book)}
                  >
                    <i className="fa-solid fa-copy"></i> + Bản sao
                  </button>
                  <button
                    className="btn btn-sm btn-secondary"
                    onClick={() => onOpenEditBook(book)}
                  >
                    <i className="fa-solid fa-pen"></i>
                  </button>
                  <button
                    className="btn btn-sm btn-danger"
                    onClick={() => onDeleteBook(book.id)}
                  >
                    <i className="fa-solid fa-trash"></i>
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

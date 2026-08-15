import React, { useState, useEffect } from 'react';
import type { Book, Category } from '../../types';

interface BookModalProps {
  isOpen: boolean;
  editingBook: Book | null;
  categories: Category[];
  onClose: () => void;
  onSave: (bookData: Partial<Book>) => void;
}

export const BookModal: React.FC<BookModalProps> = ({
  isOpen,
  editingBook,
  categories,
  onClose,
  onSave,
}) => {
  const [form, setForm] = useState({
    title: '',
    author: '',
    isbn: '',
    categoryId: categories[0]?.id || 1,
    totalCopies: 1,
    description: '',
  });

  useEffect(() => {
    if (editingBook) {
      setForm({
        title: editingBook.title,
        author: editingBook.author,
        isbn: editingBook.isbn || '',
        categoryId: editingBook.categoryId || categories[0]?.id || 1,
        totalCopies: editingBook.totalCopies || 1,
        description: editingBook.description || '',
      });
    } else {
      setForm({
        title: '',
        author: '',
        isbn: '',
        categoryId: categories[0]?.id || 1,
        totalCopies: 1,
        description: '',
      });
    }
  }, [editingBook, categories]);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSave(form);
  };

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <div className="modal-header">
          <h3>
            <i className="fa-solid fa-book-medical"></i>{' '}
            {editingBook ? 'Cập Nhật Đầu Sách' : 'Thêm Đầu Sách Mới'}
          </h3>
          <button className="modal-close" onClick={onClose}>
            <i className="fa-solid fa-xmark"></i>
          </button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-group">
              <label>Tên sách *</label>
              <input
                type="text"
                value={form.title}
                onChange={(e) => setForm({ ...form, title: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label>Tác giả *</label>
              <input
                type="text"
                value={form.author}
                onChange={(e) => setForm({ ...form, author: e.target.value })}
                required
              />
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
              <div className="form-group">
                <label>Mã ISBN</label>
                <input
                  type="text"
                  value={form.isbn}
                  onChange={(e) => setForm({ ...form, isbn: e.target.value })}
                />
              </div>
              <div className="form-group">
                <label>Danh mục *</label>
                <select
                  value={form.categoryId}
                  onChange={(e) => setForm({ ...form, categoryId: Number(e.target.value) })}
                >
                  {categories.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.name}
                    </option>
                  ))}
                </select>
              </div>
            </div>
            {!editingBook && (
              <div className="form-group">
                <label>Số lượng bản sao tạo ban đầu</label>
                <input
                  type="number"
                  min={1}
                  value={form.totalCopies}
                  onChange={(e) => setForm({ ...form, totalCopies: Number(e.target.value) })}
                />
              </div>
            )}
            <div className="form-group">
              <label>Mô tả nội dung</label>
              <textarea
                rows={3}
                value={form.description}
                onChange={(e) => setForm({ ...form, description: e.target.value })}
              />
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-outline-jade" onClick={onClose}>
              Hủy
            </button>
            <button type="submit" className="btn btn-primary">
              {editingBook ? 'Lưu Cập Nhật' : 'Tạo Đầu Sách'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

import React, { useState, useEffect } from 'react';
import type { Book } from '../../types';

interface CreateBorrowModalProps {
  isOpen: boolean;
  books: Book[];
  onClose: () => void;
  onCreateBorrow: (data: { borrowerUsername: string; bookId: number; days: number }) => void;
}

export const CreateBorrowModal: React.FC<CreateBorrowModalProps> = ({
  isOpen,
  books,
  onClose,
  onCreateBorrow,
}) => {
  const [borrowerUsername, setBorrowerUsername] = useState('');
  const [bookId, setBookId] = useState<number>(0);
  const [days, setDays] = useState<number>(14);

  useEffect(() => {
    const firstAvailable = books.find(
      (b) => (b.availableCopies ?? b.availableQuantity ?? 0) > 0
    );
    if (firstAvailable) {
      setBookId(firstAvailable.id || firstAvailable.bookId || 0);
    } else if (books.length > 0) {
      setBookId(books[0].id || books[0].bookId || 0);
    }
  }, [books, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!borrowerUsername.trim() || !bookId) return;

    const selectedBook = books.find((b) => b.id === bookId || b.bookId === bookId);
    const avail = selectedBook?.availableCopies ?? selectedBook?.availableQuantity ?? 0;
    if (avail <= 0) {
      alert('Sách này đã hết bản sao sẵn có trong kho. Vui lòng chọn đầu sách khác hoặc nhập thêm bản sao!');
      return;
    }

    onCreateBorrow({
      borrowerUsername: borrowerUsername.trim(),
      bookId,
      days,
    });
  };

  const selectedBook = books.find((b) => b.id === bookId || b.bookId === bookId);
  const isSelectedOutOfStock = (selectedBook?.availableCopies ?? selectedBook?.availableQuantity ?? 0) <= 0;

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <div className="modal-header">
          <h3>
            <i className="fa-solid fa-clipboard-check"></i> Tạo Phiếu Mượn Sách Mới
          </h3>
          <button className="modal-close" onClick={onClose}>
            <i className="fa-solid fa-xmark"></i>
          </button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-group">
              <label>Tên đăng nhập Độc giả mượn sách *</label>
              <input
                type="text"
                placeholder="Nhập username người mượn..."
                value={borrowerUsername}
                onChange={(e) => setBorrowerUsername(e.target.value)}
                required
              />
            </div>
            <div className="form-group">
              <label>Chọn sách cần mượn *</label>
              <select
                value={bookId}
                onChange={(e) => setBookId(Number(e.target.value))}
                required
              >
                {books.length === 0 ? (
                  <option value={0}>-- Database chưa có sách --</option>
                ) : (
                  books.map((b) => {
                    const avail = b.availableCopies ?? b.availableQuantity ?? 0;
                    return (
                      <option
                        key={b.id}
                        value={b.id}
                        disabled={avail <= 0}
                        style={{ color: avail <= 0 ? '#9ca3af' : 'inherit' }}
                      >
                        {b.title} ({avail > 0 ? `Còn ${avail} bản` : 'Hết bản sao'})
                      </option>
                    );
                  })
                )}
              </select>
            </div>
            <div className="form-group">
              <label>Thời hạn mượn (Số ngày: 1-30)</label>
              <input
                type="number"
                min={1}
                max={30}
                value={days}
                onChange={(e) => setDays(Math.min(30, Math.max(1, Number(e.target.value))))}
                required
              />
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-outline-jade" onClick={onClose}>
              Hủy
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={books.length === 0 || isSelectedOutOfStock}
            >
              Tạo Phiếu Mượn
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

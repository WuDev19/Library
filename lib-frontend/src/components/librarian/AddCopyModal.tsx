import React, { useState } from 'react';
import type { Book } from '../../types';

interface AddCopyModalProps {
  isOpen: boolean;
  book: Book | null;
  onClose: () => void;
  onAddCopies: (bookId: number, count: number, note: string) => void;
}

export const AddCopyModal: React.FC<AddCopyModalProps> = ({
  isOpen,
  book,
  onClose,
  onAddCopies,
}) => {
  const [count, setCount] = useState<number>(1);
  const [note, setNote] = useState<string>('');

  if (!isOpen || !book) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (count < 1) return;
    onAddCopies(book.id, count, note);
  };

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <div className="modal-header">
          <h3>
            <i className="fa-solid fa-copy"></i> Nhập Thêm Bản Sao Sách
          </h3>
          <button className="modal-close" onClick={onClose}>
            <i className="fa-solid fa-xmark"></i>
          </button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <p style={{ marginBottom: '14px' }}>
              Sách: <strong>{book.title}</strong>
            </p>
            <div className="form-group">
              <label>Số lượng bản sao cần nhập thêm *</label>
              <input
                type="number"
                min={1}
                max={100}
                value={count}
                onChange={(e) => setCount(Number(e.target.value))}
                required
              />
            </div>
            <div className="form-group">
              <label>Ghi chú đợt nhập (Note)</label>
              <input
                type="text"
                placeholder="Nhập ghi chú lô hàng (VD: Nhập đợt bổ sung Q3/2026)..."
                value={note}
                onChange={(e) => setNote(e.target.value)}
              />
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-outline-jade" onClick={onClose}>
              Hủy
            </button>
            <button type="submit" className="btn btn-primary">
              Xác Nhận Nhập Bản Sao
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

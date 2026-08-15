import React, { useState, useEffect } from 'react';
import type { Book } from '../../types';

interface BorrowModalProps {
  isOpen: boolean;
  book: Book | null;
  onClose: () => void;
  onConfirmBorrow: (book: Book, days: number, dueDateStr: string) => void;
}

export const BorrowModal: React.FC<BorrowModalProps> = ({
  isOpen,
  book,
  onClose,
  onConfirmBorrow,
}) => {
  const [days, setDays] = useState<number>(14);

  // Compute min, max and selected date string
  const today = new Date();
  const minDateStr = new Date(today.getTime() + 86400000).toISOString().split('T')[0];
  const maxDateStr = new Date(today.getTime() + 30 * 86400000).toISOString().split('T')[0];

  const computedDueDate = new Date(today.getTime() + days * 86400000).toISOString().split('T')[0];
  const [selectedDueDate, setSelectedDueDate] = useState<string>(computedDueDate);

  useEffect(() => {
    const updatedDueDate = new Date(today.getTime() + days * 86400000).toISOString().split('T')[0];
    setSelectedDueDate(updatedDueDate);
  }, [days]);

  if (!isOpen || !book) return null;

  const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value;
    setSelectedDueDate(val);
    const chosenTime = new Date(val).getTime();
    const todayTime = new Date().setHours(0, 0, 0, 0);
    const diffDays = Math.round((chosenTime - todayTime) / (1000 * 3600 * 24));
    if (diffDays >= 1 && diffDays <= 30) {
      setDays(diffDays);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (days < 1 || days > 30) return;
    onConfirmBorrow(book, days, selectedDueDate);
  };

  const availableCount = book.availableCopies ?? book.availableQuantity ?? 0;

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <div className="modal-header">
          <h3>
            <i className="fa-solid fa-hand-holding-book"></i> Đăng Ký Mượn Sách Trực Tuyến
          </h3>
          <button className="modal-close" onClick={onClose}>
            <i className="fa-solid fa-xmark"></i>
          </button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div
              style={{
                background: 'var(--bg-card)',
                padding: '16px',
                borderRadius: '8px',
                marginBottom: '16px',
                border: '1px solid var(--border-jade)',
              }}
            >
              <h4 className="font-bold" style={{ color: 'var(--primary-hover)', marginBottom: '4px' }}>
                {book.title}
              </h4>
              <p style={{ fontSize: '0.88rem', color: 'var(--text-muted)' }}>Tác giả: {book.author}</p>
              <div style={{ marginTop: '8px' }}>
                <span className={`badge badge-${availableCount > 0 ? 'jade' : 'danger'}`}>
                  Số lượng sẵn có: <strong>{availableCount} bản</strong>
                </span>
              </div>
            </div>

            <div className="form-group">
              <label>Số ngày mượn (Tối đa 30 ngày) *</label>
              <input
                type="number"
                min={1}
                max={30}
                value={days}
                onChange={(e) => {
                  const num = Math.min(30, Math.max(1, Number(e.target.value)));
                  setDays(num);
                }}
                required
              />
            </div>

            <div className="form-group">
              <label>Hoặc chọn Ngày Trả Sách (Hạn tối đa 30 ngày)</label>
              <input
                type="date"
                min={minDateStr}
                max={maxDateStr}
                value={selectedDueDate}
                onChange={handleDateChange}
                required
              />
            </div>

            <div style={{ background: '#f0fdfa', padding: '12px', borderRadius: '6px', fontSize: '0.88rem' }}>
              <div>
                <i className="fa-solid fa-calendar-check text-success"></i> Ngày mượn:{' '}
                <strong>{today.toLocaleDateString('vi-VN')}</strong>
              </div>
              <div style={{ marginTop: '4px' }}>
                <i className="fa-solid fa-calendar-day text-danger"></i> Hạn trả dự kiến:{' '}
                <strong className="text-danger">{selectedDueDate}</strong> ({days} ngày mượn)
              </div>
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-outline-jade" onClick={onClose}>
              Hủy
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={availableCount <= 0}
            >
              Xác Nhận Mượn Sách
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

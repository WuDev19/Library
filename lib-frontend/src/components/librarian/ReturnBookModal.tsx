import React, { useState, useEffect } from 'react';

interface ReturnBookModalProps {
  isOpen: boolean;
  selectedBorrowCode?: string;
  onClose: () => void;
  onReturnBook: (borrowCode: string) => void;
}

export const ReturnBookModal: React.FC<ReturnBookModalProps> = ({
  isOpen,
  selectedBorrowCode,
  onClose,
  onReturnBook,
}) => {
  const [borrowCode, setBorrowCode] = useState('');

  useEffect(() => {
    if (selectedBorrowCode) {
      setBorrowCode(selectedBorrowCode);
    } else {
      setBorrowCode('');
    }
  }, [selectedBorrowCode, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!borrowCode.trim()) return;
    onReturnBook(borrowCode.trim().toUpperCase());
    setBorrowCode('');
  };

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <div className="modal-header">
          <h3>
            <i className="fa-solid fa-rotate-left"></i> Thủ Tục Nhận Trả Sách
          </h3>
          <button className="modal-close" onClick={onClose}>
            <i className="fa-solid fa-xmark"></i>
          </button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-group">
              <label>Mã Phiếu Mượn *</label>
              <input
                type="text"
                placeholder="Nhập mã phiếu mượn (Ví dụ: BRW-882190)..."
                value={borrowCode}
                onChange={(e) => setBorrowCode(e.target.value)}
                required
              />
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-outline-jade" onClick={onClose}>
              Hủy
            </button>
            <button type="submit" className="btn btn-secondary">
              Xác Nhận Nhận Trả Sách
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

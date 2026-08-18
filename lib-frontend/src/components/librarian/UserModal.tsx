import React, { useState, useEffect } from 'react';
import type { User } from '../../types';

interface UserModalProps {
  isOpen: boolean;
  editingUser: User | null;
  onClose: () => void;
  onSave: (data: { userId?: number; fullName: string; phone: string }) => void;
}

export const UserModal: React.FC<UserModalProps> = ({
  isOpen,
  editingUser,
  onClose,
  onSave,
}) => {
  const [fullName, setFullName] = useState('');
  const [phone, setPhone] = useState('');

  useEffect(() => {
    if (editingUser) {
      setFullName(editingUser.fullName || editingUser.username || '');
      setPhone(editingUser.phone || editingUser.phoneNumber || '');
    } else {
      setFullName('');
      setPhone('');
    }
  }, [editingUser]);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!fullName.trim() || !phone.trim()) return;
    onSave({
      userId: editingUser?.userId || editingUser?.id,
      fullName: fullName.trim(),
      phone: phone.trim(),
    });
  };

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <div className="modal-header">
          <h3>
            <i className="fa-solid fa-user-gear"></i> Cập Nhật Thông Tin Người Dùng
          </h3>
          <button className="modal-close" onClick={onClose}>
            <i className="fa-solid fa-xmark"></i>
          </button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            {editingUser?.email && (
              <div className="form-group">
                <label>Email</label>
                <input
                  type="email"
                  value={editingUser.email}
                  disabled
                  style={{ opacity: 0.7, cursor: 'not-allowed' }}
                />
              </div>
            )}
            <div className="form-group">
              <label>Họ và tên *</label>
              <input
                type="text"
                placeholder="Ví dụ: Nguyễn Văn An"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                required
              />
            </div>
            <div className="form-group">
              <label>Số điện thoại *</label>
              <input
                type="text"
                placeholder="0912345678"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                required
              />
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-outline-jade" onClick={onClose}>
              Hủy
            </button>
            <button type="submit" className="btn btn-primary">
              Lưu Thay Đổi
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

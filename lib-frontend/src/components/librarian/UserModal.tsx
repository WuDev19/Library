import React, { useState, useEffect } from 'react';
import type { User } from '../../types';

interface UserModalProps {
  isOpen: boolean;
  editingUser: User | null;
  onClose: () => void;
  onSave: (data: { userId?: number; fullName: string; email: string; phone: string }) => void;
}

export const UserModal: React.FC<UserModalProps> = ({
  isOpen,
  editingUser,
  onClose,
  onSave,
}) => {
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');

  useEffect(() => {
    if (editingUser) {
      setFullName(editingUser.fullName || editingUser.username || '');
      setEmail(editingUser.email || '');
      setPhone(editingUser.phone || editingUser.phoneNumber || '');
    } else {
      setFullName('');
      setEmail('');
      setPhone('0912345678');
    }
  }, [editingUser]);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!fullName.trim() || !phone.trim()) return;
    onSave({
      userId: editingUser?.userId || editingUser?.id,
      fullName: fullName.trim(),
      email: email.trim(),
      phone: phone.trim(),
    });
  };

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <div className="modal-header">
          <h3>
            <i className="fa-solid fa-user-gear"></i>{' '}
            {editingUser ? 'Cập Nhật Thông Tin Người Dùng' : 'Tạo Hồ Sơ Người Dùng Mới'}
          </h3>
          <button className="modal-close" onClick={onClose}>
            <i className="fa-solid fa-xmark"></i>
          </button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
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
              <label>Email *</label>
              <input
                type="email"
                placeholder="example@gmail.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
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
              {editingUser ? 'Lưu Thay Đổi' : 'Tạo Người Dùng'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

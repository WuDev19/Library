import React, { useState, useEffect } from 'react';
import type { Category } from '../../types';

interface CategoryModalProps {
  isOpen: boolean;
  editingCategory: Category | null;
  onClose: () => void;
  onSave: (data: { id?: number; code?: string; name: string; description?: string }) => void;
}

export const CategoryModal: React.FC<CategoryModalProps> = ({
  isOpen,
  editingCategory,
  onClose,
  onSave,
}) => {
  const [code, setCode] = useState('');
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');

  useEffect(() => {
    if (editingCategory) {
      setCode(editingCategory.code || editingCategory.categoryCode || '');
      setName(editingCategory.name || editingCategory.categoryName || '');
      setDescription(editingCategory.description || '');
    } else {
      setCode('');
      setName('');
      setDescription('');
    }
  }, [editingCategory]);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;
    onSave({
      id: editingCategory?.id || editingCategory?.categoryId,
      code: code.trim(),
      name: name.trim(),
      description: description.trim(),
    });
  };

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <div className="modal-header">
          <h3>
            <i className="fa-solid fa-folder-gear"></i>{' '}
            {editingCategory ? 'Chỉnh Sửa Danh Mục Sách' : 'Thêm Danh Mục Sách Mới'}
          </h3>
          <button className="modal-close" onClick={onClose}>
            <i className="fa-solid fa-xmark"></i>
          </button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-group">
              <label>Mã danh mục (Code)</label>
              <input
                type="text"
                placeholder="Tự động tạo hoặc nhập mã (VD: CAT-TECH)"
                value={code}
                onChange={(e) => setCode(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label>Tên danh mục *</label>
              <input
                type="text"
                placeholder="Ví dụ: Khoa Học Máy Tính"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </div>
            <div className="form-group">
              <label>Mô tả danh mục</label>
              <textarea
                placeholder="Mô tả vắn tắt về danh mục này..."
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                rows={3}
              />
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-outline-jade" onClick={onClose}>
              Hủy
            </button>
            <button type="submit" className="btn btn-primary">
              {editingCategory ? 'Lưu Thay Đổi' : 'Tạo Danh Mục'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

import React from 'react';
import type { Category } from '../../types';

interface CategoryManagementProps {
  categories: Category[];
  onOpenAddCategory: () => void;
  onOpenEditCategory: (category: Category) => void;
  onDeleteCategory: (categoryId: number) => void;
}

export const CategoryManagement: React.FC<CategoryManagementProps> = ({
  categories,
  onOpenAddCategory,
  onOpenEditCategory,
  onDeleteCategory,
}) => {
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
        <h3>
          <i className="fa-solid fa-tags"></i> Danh Mục Sách Thư Viện ({categories.length})
        </h3>
        <button className="btn btn-primary" onClick={onOpenAddCategory}>
          <i className="fa-solid fa-folder-plus"></i> Thêm Danh Mục
        </button>
      </div>

      {categories.length === 0 ? (
        <div className="glass-card text-center" style={{ padding: '48px' }}>
          <i
            className="fa-solid fa-tags"
            style={{ fontSize: '3rem', color: 'var(--primary)', marginBottom: '14px' }}
          ></i>
          <h3>Chưa có danh mục nào trong Database</h3>
          <p className="text-muted" style={{ marginTop: '6px' }}>
            Nhấn nút &quot;Thêm Danh Mục&quot; để tạo danh mục sách đầu tiên.
          </p>
        </div>
      ) : (
        <div className="stats-grid">
          {categories.map((c) => {
            const cid = c.id || c.categoryId || 0;
            return (
              <div key={cid} className="glass-card" style={{ padding: '20px', position: 'relative' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '14px', marginBottom: '12px' }}>
                    <div className="stat-icon jade">
                      <i className="fa-solid fa-folder"></i>
                    </div>
                    <div>
                      <h4 className="font-bold">{c.name || c.categoryName}</h4>
                      <span className="badge badge-jade">Mã: {c.code || c.categoryCode || 'CAT'}</span>
                    </div>
                  </div>
                  <div style={{ display: 'flex', gap: '6px' }}>
                    <button
                      className="btn btn-sm btn-secondary"
                      onClick={() => onOpenEditCategory(c)}
                      title="Sửa danh mục"
                    >
                      <i className="fa-solid fa-pen-to-square"></i>
                    </button>
                    <button
                      className="btn btn-sm btn-danger"
                      onClick={() => onDeleteCategory(cid)}
                      title="Xóa danh mục"
                    >
                      <i className="fa-solid fa-trash-can"></i>
                    </button>
                  </div>
                </div>
                <p style={{ fontSize: '0.88rem', color: 'var(--text-muted)', marginTop: '8px' }}>
                  {c.description || 'Không có mô tả'}
                </p>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

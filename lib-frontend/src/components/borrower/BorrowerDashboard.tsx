import React from 'react';
import type { Book, BorrowRecord } from '../../types';
import { useAuth } from '../../context/AuthContext';

interface BorrowerDashboardProps {
  books: Book[];
  borrows: BorrowRecord[];
  onNavigateTab: (tab: string) => void;
  onQuickBorrow: (book: Book) => void;
}

export const BorrowerDashboard: React.FC<BorrowerDashboardProps> = ({
  books,
  borrows,
  onNavigateTab,
  onQuickBorrow,
}) => {
  const { user } = useAuth();
  const overdueCount = borrows.filter((b) => b.status === 'OVERDUE').length;

  return (
    <div>
      <div className="welcome-banner">
        <div>
          <h1>Chào mừng, {user?.fullName || user?.username || 'Độc giả'}!</h1>
          <p>
            Khám phá kho sách phong phú và quản lý lịch sử mượn trả dễ dàng với giao diện Jade Emerald Sáng.
          </p>
        </div>
        <button className="btn btn-secondary" onClick={() => onNavigateTab('catalog')}>
          <i className="fa-solid fa-magnifying-glass"></i> Tra Cứu Sách Ngay
        </button>
      </div>

      {/* Overdue Warning Alert */}
      {overdueCount > 0 && (
        <div
          className="glass-card"
          style={{
            padding: '20px',
            borderColor: 'var(--danger)',
            background: 'rgba(239, 68, 68, 0.05)',
            marginBottom: '24px',
          }}
        >
          <h4 className="text-danger font-bold" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <i className="fa-solid fa-triangle-exclamation"></i> Cảnh Báo Quá Hạn Trả Sách
          </h4>
          <p style={{ marginTop: '4px', fontSize: '0.92rem' }}>
            Bạn đang có {overdueCount} phiếu mượn quá hạn. Vui lòng mang sách tới thư viện để hoàn tất thủ tục trả.
          </p>
        </div>
      )}

      <h3 className="font-bold mb-3">
        <i className="fa-solid fa-book-open"></i> Gợi Ý Sách Hay Nổi Bật
      </h3>
      {books.length === 0 ? (
        <div className="glass-card text-center" style={{ padding: '36px' }}>
          <p className="text-muted">Chưa có dữ liệu sách trong Database.</p>
        </div>
      ) : (
        <div className="books-grid">
          {books.slice(0, 4).map((book) => {
            const avail = book.availableCopies ?? book.availableQuantity ?? 0;
            const total = book.totalCopies ?? book.totalQuantity ?? 0;
            return (
              <div key={book.id} className="book-card">
                <div className="book-cover-placeholder">
                  <i className="fa-solid fa-book-bookmark"></i>
                </div>
                <div className="book-card-body">
                  <div>
                    <div className="badge badge-jade" style={{ marginBottom: '8px' }}>
                      {book.categoryName || 'Sách Hay'}
                    </div>
                    <div className="book-title">{book.title}</div>
                    <div className="book-author">Tác giả: {book.author}</div>
                    <div style={{ fontSize: '0.82rem', fontWeight: 600, color: 'var(--primary-hover)', marginTop: '4px' }}>
                      Khả dụng: {avail} / {total} bản
                    </div>
                  </div>
                  <button
                    className="btn btn-sm btn-primary btn-block"
                    style={{ marginTop: '14px' }}
                    disabled={avail <= 0}
                    onClick={() => onQuickBorrow(book)}
                  >
                    <i className="fa-solid fa-hand-holding-book"></i> {avail > 0 ? 'Đăng Ký Mượn' : 'Hết Bản Sao'}
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

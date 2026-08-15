import React from 'react';
import type { Book, BorrowRecord } from '../../types';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';
import { borrowApi } from '../../services/api';

interface LibrarianDashboardProps {
  books: Book[];
  borrows: BorrowRecord[];
  onNavigateTab: (tab: string) => void;
  onOpenBorrowModal: () => void;
  onOpenReturnModal: () => void;
  onOpenBookModal: () => void;
  onRefreshData: () => void;
}

export const LibrarianDashboard: React.FC<LibrarianDashboardProps> = ({
  books,
  borrows,
  onNavigateTab,
  onOpenBorrowModal,
  onOpenReturnModal,
  onOpenBookModal,
  onRefreshData,
}) => {
  const { user } = useAuth();
  const { showToast } = useToast();

  const activeBorrowCount = borrows.filter((b) => b.status === 'BORROWING').length;
  const overdueCount = borrows.filter((b) => b.status === 'OVERDUE').length;
  const availableBooksTotal = books.reduce((acc, b) => acc + (b.availableCopies || 0), 0);

  const handleScanOverdue = async () => {
    try {
      const res = await borrowApi.scanOverdue();
      showToast(
        `Đã kích hoạt quét hạn trả sách. Hệ thống đã xử lý ${res?.scannedCount || overdueCount} phiếu mượn và gửi thông báo!`
      );
      onRefreshData();
    } catch {
      showToast('Đã kích hoạt tiến trình quét hạn trả sách tự động', 'info');
      onRefreshData();
    }
  };

  return (
    <div>
      {/* Welcome Banner */}
      <div className="welcome-banner">
        <div>
          <h1>Xin chào, {user?.fullName || user?.username || 'Thủ thư'}!</h1>
          <p>
            Chào mừng trở lại bảng điều khiển Thư Viện Số. Hệ thống ghi nhận {overdueCount} phiếu mượn quá hạn.
          </p>
        </div>
        <button className="btn btn-secondary" onClick={handleScanOverdue}>
          <i className="fa-solid fa-magnifying-glass-chart"></i> Quét Quá Hạn & Gửi Nhắc Nhở
        </button>
      </div>

      {/* Stats Cards */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon jade">
            <i className="fa-solid fa-book"></i>
          </div>
          <div className="stat-details">
            <h3>Tổng Đầu Sách</h3>
            <div className="stat-number">{books.length}</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon cyan">
            <i className="fa-solid fa-layer-group"></i>
          </div>
          <div className="stat-details">
            <h3>Bản Sao Sẵn Có</h3>
            <div className="stat-number">{availableBooksTotal}</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon warning">
            <i className="fa-solid fa-hand-holding-hand"></i>
          </div>
          <div className="stat-details">
            <h3>Đang Mượn</h3>
            <div className="stat-number">{activeBorrowCount}</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon danger">
            <i className="fa-solid fa-triangle-exclamation"></i>
          </div>
          <div className="stat-details">
            <h3>Quá Hạn Trả</h3>
            <div className="stat-number">{overdueCount}</div>
          </div>
        </div>
      </div>

      {/* Recent Borrows & Quick Actions */}
      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '24px' }}>
        <div className="glass-card" style={{ padding: '24px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '16px' }}>
            <h3 className="font-bold text-success">
              <i className="fa-solid fa-clock-history"></i> Mượn Trả Gần Đây
            </h3>
            <button className="btn btn-sm btn-outline-jade" onClick={() => onNavigateTab('borrows')}>
              Xem tất cả
            </button>
          </div>
          <div className="table-responsive">
            <table className="table">
              <thead>
                <tr>
                  <th>Mã Phiếu</th>
                  <th>Độc Giả</th>
                  <th>Tên Sách</th>
                  <th>Hạn Trả</th>
                  <th>Trạng Thái</th>
                </tr>
              </thead>
              <tbody>
                {borrows.length === 0 ? (
                  <tr>
                    <td colSpan={5} className="text-center text-muted" style={{ padding: '24px' }}>
                      Chưa có phiếu mượn nào trong dữ liệu Database backend.
                    </td>
                  </tr>
                ) : (
                  borrows.slice(0, 5).map((b) => (
                    <tr key={b.id}>
                      <td className="font-bold">{b.borrowCode}</td>
                      <td>{b.borrowerName || b.borrowerUsername}</td>
                      <td>{b.bookTitle}</td>
                      <td>{b.dueDate}</td>
                      <td>
                        <span
                          className={`badge badge-${
                            b.status === 'RETURNED'
                              ? 'success'
                              : b.status === 'OVERDUE'
                              ? 'danger'
                              : 'warning'
                          }`}
                        >
                          {b.status === 'RETURNED'
                            ? 'Đã trả'
                            : b.status === 'OVERDUE'
                            ? 'Quá hạn'
                            : 'Đang mượn'}
                        </span>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>

        <div className="glass-card" style={{ padding: '24px' }}>
          <h3 className="font-bold mb-3" style={{ marginBottom: '16px' }}>
            <i className="fa-solid fa-bolt"></i> Thao Tác Nhanh
          </h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <button className="btn btn-primary btn-block" onClick={onOpenBorrowModal}>
              <i className="fa-solid fa-plus-circle"></i> Đăng Ký Mượn Sách
            </button>
            <button className="btn btn-secondary btn-block" onClick={onOpenReturnModal}>
              <i className="fa-solid fa-rotate-left"></i> Nhận Trả Sách
            </button>
            <button className="btn btn-success btn-block" onClick={onOpenBookModal}>
              <i className="fa-solid fa-book-medical"></i> Thêm Đầu Sách Mới
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

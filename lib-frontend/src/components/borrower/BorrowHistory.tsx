import React from 'react';
import type { BorrowRecord } from '../../types';

interface BorrowHistoryProps {
  borrows: BorrowRecord[];
  page?: number;
  pageSize?: number;
  sizeOfPage?: number;
  onPageChange?: (newPage: number) => void;
}

export const BorrowHistory: React.FC<BorrowHistoryProps> = ({
  borrows,
  page = 0,
  pageSize = 10,
  sizeOfPage = 0,
  onPageChange,
}) => {
  return (
    <div className="glass-card" style={{ padding: '24px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
        <h3 className="font-bold">
          <i className="fa-solid fa-clock-rotate-left"></i> Lịch Sử Mượn Trả Sách Của Tôi ({borrows.length})
        </h3>
        <span style={{ fontSize: '0.88rem', color: 'var(--text-muted)' }}>
          <i className="fa-solid fa-circle-info"></i> Để trả sách, hãy mang cuốn sách vật lý tới quầy Thủ thư và cung cấp <strong>Mã Phiếu Mượn</strong>.
        </span>
      </div>

      <div className="table-responsive">
        <table className="table">
          <thead>
            <tr>
              <th>Mã Phiếu</th>
              <th>Tên Sách</th>
              <th>Mã Bản Sao</th>
              <th>Ngày Mượn</th>
              <th>Hạn Trả</th>
              <th>Trạng Thái</th>
              <th>Hướng Dẫn Trả Sách</th>
            </tr>
          </thead>
          <tbody>
            {borrows.length === 0 ? (
              <tr>
                <td colSpan={7} className="text-center text-muted" style={{ padding: '32px' }}>
                  Bạn chưa có phiếu mượn sách nào trong hệ thống Database.
                </td>
              </tr>
            ) : (
              borrows.map((b) => (
                <tr key={b.id}>
                  <td className="font-bold">
                    <code>{b.borrowCode}</code>
                  </td>
                  <td>
                    <strong>{b.bookTitle}</strong>
                  </td>
                  <td>
                    <code>{b.copyCode}</code>
                  </td>
                  <td>{b.borrowDate}</td>
                  <td>
                    <span className={b.status === 'OVERDUE' ? 'text-danger font-bold' : ''}>
                      {b.dueDate}
                    </span>
                  </td>
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
                        ? 'Đã hoàn tất trả'
                        : b.status === 'OVERDUE'
                        ? 'Quá hạn mượn'
                        : 'Đang mượn'}
                    </span>
                  </td>
                  <td>
                    {b.status !== 'RETURNED' ? (
                      <span className="badge badge-jade" style={{ fontSize: '0.82rem' }}>
                        <i className="fa-solid fa-store"></i> Đưa mã <strong>{b.borrowCode}</strong> cho Thủ thư tại quầy
                      </span>
                    ) : (
                      <span className="text-success font-bold" style={{ fontSize: '0.88rem' }}>
                        <i className="fa-solid fa-circle-check"></i> Đã trả lại kho
                      </span>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination Controls */}
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginTop: '20px',
          paddingTop: '16px',
          borderTop: '1px solid rgba(255, 255, 255, 0.1)',
        }}
      >
        <span className="text-muted" style={{ fontSize: '0.9rem' }}>
          Số lượng trong trang: <strong>{sizeOfPage || borrows.length}</strong> (Trang {page + 1})
        </span>
        <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
          <button
            className="btn btn-sm btn-secondary"
            disabled={page <= 0}
            onClick={() => onPageChange && onPageChange(page - 1)}
          >
            <i className="fa-solid fa-chevron-left"></i> Trang trước
          </button>
          <span style={{ fontWeight: 600, padding: '0 8px' }}>Trang {page + 1}</span>
          <button
            className="btn btn-sm btn-secondary"
            disabled={borrows.length < pageSize}
            onClick={() => onPageChange && onPageChange(page + 1)}
          >
            Trang sau <i className="fa-solid fa-chevron-right"></i>
          </button>
        </div>
      </div>
    </div>
  );
};

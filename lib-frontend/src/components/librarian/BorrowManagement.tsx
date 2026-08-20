import React, { useState } from 'react';
import type { BorrowRecord } from '../../types';

interface BorrowManagementProps {
  borrows: BorrowRecord[];
  page?: number;
  pageSize?: number;
  sizeOfPage?: number;
  onPageChange?: (newPage: number) => void;
  onOpenCreateBorrow: () => void;
  onOpenReturnBook: (borrowCode?: string) => void;
}

export const BorrowManagement: React.FC<BorrowManagementProps> = ({
  borrows,
  page = 0,
  pageSize = 10,
  sizeOfPage = 0,
  onPageChange,
  onOpenCreateBorrow,
  onOpenReturnBook,
}) => {
  const [statusFilter, setStatusFilter] = useState<string>('ALL');

  const filteredBorrows = borrows.filter((b) => {
    if (statusFilter === 'ALL') return true;
    return b.status === statusFilter;
  });

  return (
    <div>
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '20px',
        }}
      >
        <div style={{ display: 'flex', gap: '10px' }}>
          {['ALL', 'BORROWING', 'OVERDUE', 'RETURNED'].map((st) => (
            <button
              key={st}
              className={`btn btn-sm ${statusFilter === st ? 'btn-primary' : 'btn-outline-jade'}`}
              onClick={() => setStatusFilter(st)}
            >
              {st === 'ALL'
                ? 'Tất cả'
                : st === 'BORROWING'
                ? 'Đang mượn'
                : st === 'OVERDUE'
                ? 'Quá hạn'
                : 'Đã trả'}
            </button>
          ))}
        </div>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button className="btn btn-primary" onClick={onOpenCreateBorrow}>
            <i className="fa-solid fa-plus"></i> Đăng Ký Mượn
          </button>
          <button className="btn btn-secondary" onClick={() => onOpenReturnBook()}>
            <i className="fa-solid fa-rotate-left"></i> Nhận Trả Sách Tại Quầy
          </button>
        </div>
      </div>

      <div className="glass-card" style={{ padding: '20px' }}>
        <div className="table-responsive">
          <table className="table">
            <thead>
              <tr>
                <th>Mã Phiếu</th>
                <th>Độc Giả</th>
                <th>Mã Bản Sao</th>
                <th>Tên Sách</th>
                <th>Ngày Mượn</th>
                <th>Hạn Trả</th>
                <th>Trạng Thái</th>
                <th>Tiền Phạt</th>
                <th>Thao Tác</th>
              </tr>
            </thead>
            <tbody>
              {filteredBorrows.length === 0 ? (
                <tr>
                  <td colSpan={9} className="text-center text-muted" style={{ padding: '32px' }}>
                    Chưa có bản ghi mượn trả nào trong dữ liệu Backend Database.
                  </td>
                </tr>
              ) : (
                filteredBorrows.map((b) => (
                  <tr key={b.id}>
                    <td className="font-bold">
                      <code>{b.borrowCode}</code>
                    </td>
                    <td>{b.borrowerName || b.borrowerUsername}</td>
                    <td>
                      <code>{b.copyCode}</code>
                    </td>
                    <td>{b.bookTitle}</td>
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
                          ? 'Đã trả'
                          : b.status === 'OVERDUE'
                          ? 'Quá hạn'
                          : 'Đang mượn'}
                      </span>
                    </td>
                    <td className="text-danger font-bold">
                      {b.fineAmount ? `${b.fineAmount.toLocaleString()} VNĐ` : '0'}
                    </td>
                    <td>
                      {b.status !== 'RETURNED' ? (
                        <button
                          className="btn btn-sm btn-secondary"
                          onClick={() => onOpenReturnBook(b.borrowCode)}
                          title="Nhận trả sách này"
                        >
                          <i className="fa-solid fa-rotate-left"></i> Trả Sách
                        </button>
                      ) : (
                        <span className="text-muted" style={{ fontSize: '0.85rem' }}>
                          <i className="fa-solid fa-check-double text-success"></i> Hoàn tất
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
    </div>
  );
};

import React, { useState, useMemo } from 'react';
import type { User, BorrowRecord } from '../../types';

interface UserManagementProps {
  users: User[];
  allBorrows?: BorrowRecord[];
  onOpenCreateUser: () => void;
  onOpenEditUser: (user: User) => void;
  onDeleteUser: (userId: number) => void;
  onSearchUsers: (keyword: string) => void;
}

export const UserManagement: React.FC<UserManagementProps> = ({
  users,
  allBorrows = [],
  onOpenCreateUser,
  onOpenEditUser,
  onDeleteUser,
  onSearchUsers,
}) => {
  const [searchKeyword, setSearchKeyword] = useState<string>('');
  const [selectedUserForDetails, setSelectedUserForDetails] = useState<User | null>(null);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSearchUsers(searchKeyword.trim());
  };

  const formatDate = (dateStr?: string) => {
    if (!dateStr) return 'N/A';
    try {
      const d = new Date(dateStr);
      if (isNaN(d.getTime())) return dateStr;
      return d.toLocaleDateString('vi-VN', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
      });
    } catch {
      return dateStr;
    }
  };

  const checkIsOverdue = (dueDateStr?: string) => {
    if (!dueDateStr) return false;
    try {
      const due = new Date(dueDateStr);
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      return due < today;
    } catch {
      return false;
    }
  };

  const activeBorrowedBooks = useMemo(() => {
    if (!selectedUserForDetails) return [];
    
    if (selectedUserForDetails.borrowedBooks && selectedUserForDetails.borrowedBooks.length > 0) {
      return selectedUserForDetails.borrowedBooks.map((b, idx) => ({
        borrowId: b.borrowId || idx + 1,
        bookTitle: b.bookTitle || 'N/A',
        borrowDate: b.borrowDate,
        dueDate: b.dueDate,
        code: `BRW-${b.borrowId || idx + 1}`,
      }));
    }

    const uid = selectedUserForDetails.userId || selectedUserForDetails.id;
    if (allBorrows && uid) {
      return allBorrows
        .filter((b) => b.borrowerId === uid && b.status !== 'RETURNED')
        .map((b) => ({
          borrowId: b.id || b.borrowRecordId,
          bookTitle: b.bookTitle || 'N/A',
          borrowDate: b.borrowDate,
          dueDate: b.dueDate,
          code: b.borrowCode || `BRW-${b.id}`,
        }));
    }

    return [];
  }, [selectedUserForDetails, allBorrows]);

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
        <form onSubmit={handleSearchSubmit} style={{ display: 'flex', gap: '12px', flex: 1, maxWidth: '500px' }}>
          <input
            type="text"
            placeholder="Tìm kiếm độc giả theo tên, email, số điện thoại..."
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
          />
          <button type="submit" className="btn btn-outline-jade">
            <i className="fa-solid fa-magnifying-glass"></i> Tìm kiếm
          </button>
        </form>
        <button className="btn btn-primary" onClick={onOpenCreateUser}>
          <i className="fa-solid fa-user-plus"></i> Thêm Người Dùng
        </button>
      </div>

      <div className="glass-card" style={{ padding: '24px' }}>
        <h3 className="font-bold mb-3">
          <i className="fa-solid fa-users-gear"></i> Danh Sách Người Dùng & Độc Giả ({users.length})
        </h3>
        <div className="table-responsive">
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Họ và Tên</th>
                <th>Tên Đăng Nhập / Email</th>
                <th>Số Điện Thoại</th>
                <th>Sách Đang Mượn</th>
                <th>Vai Trò</th>
                <th>Thao Tác</th>
              </tr>
            </thead>
            <tbody>
              {users.length === 0 ? (
                <tr>
                  <td colSpan={7} className="text-center text-muted" style={{ padding: '32px' }}>
                    Chưa có người dùng nào được tìm thấy trong Database.
                  </td>
                </tr>
              ) : (
                users.map((u) => {
                  const uid = u.userId || u.id || 0;
                  const count = u.borrowingCount || (u.borrowedBooks ? u.borrowedBooks.length : 0);
                  return (
                    <tr key={uid}>
                      <td className="font-bold">#{uid}</td>
                      <td>
                        <strong>{u.fullName || u.username}</strong>
                      </td>
                      <td>{u.email || u.username}</td>
                      <td>{u.phone || u.phoneNumber || 'N/A'}</td>
                      <td>
                        <button
                          className="btn btn-sm btn-outline-jade"
                          onClick={() => setSelectedUserForDetails(u)}
                          title="Click để xem chi tiết danh sách sách đang mượn"
                        >
                          <i className="fa-solid fa-book-bookmark"></i> {count} cuốn sách
                        </button>
                      </td>
                      <td>
                        <span
                          className={`badge badge-${
                            u.role === 'LIBRARIAN' ? 'jade' : 'success'
                          }`}
                        >
                          {u.role === 'LIBRARIAN' ? 'Thủ thư' : 'Độc giả'}
                        </span>
                      </td>
                      <td>
                        <div style={{ display: 'flex', gap: '8px' }}>
                          <button
                            className="btn btn-sm btn-secondary"
                            onClick={() => onOpenEditUser(u)}
                            title="Sửa thông tin"
                          >
                            <i className="fa-solid fa-user-pen"></i>
                          </button>
                          <button
                            className="btn btn-sm btn-danger"
                            onClick={() => onDeleteUser(uid)}
                            title="Xóa người dùng"
                          >
                            <i className="fa-solid fa-user-xmark"></i>
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal Xem Danh Sách Sách Mượn Của Người Dùng */}
      {selectedUserForDetails && (
        <div className="modal-overlay">
          <div className="modal-card" style={{ maxWidth: '680px' }}>
            <div className="modal-header">
              <h3>
                <i className="fa-solid fa-book-open-reader"></i> Chi Tiết Sách Đang Mượn
              </h3>
              <button className="modal-close" onClick={() => setSelectedUserForDetails(null)}>
                <i className="fa-solid fa-xmark"></i>
              </button>
            </div>
            
            <div className="modal-body">
              {/* Profile Summary Card */}
              <div
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '16px',
                  padding: '16px',
                  borderRadius: '12px',
                  backgroundColor: 'rgba(16, 185, 129, 0.08)',
                  border: '1px solid rgba(16, 185, 129, 0.2)',
                  marginBottom: '20px',
                }}
              >
                <div
                  style={{
                    width: '48px',
                    height: '48px',
                    borderRadius: '50%',
                    backgroundColor: 'var(--primary-color)',
                    color: '#fff',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: '1.4rem',
                  }}
                >
                  <i className="fa-solid fa-user"></i>
                </div>
                <div style={{ flex: 1 }}>
                  <h4 style={{ margin: 0, fontWeight: 700 }}>
                    {selectedUserForDetails.fullName || selectedUserForDetails.username}
                  </h4>
                  <p style={{ margin: '2px 0 0', fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                    <i className="fa-solid fa-envelope"></i> {selectedUserForDetails.email || 'N/A'} |{' '}
                    <i className="fa-solid fa-phone"></i> {selectedUserForDetails.phone || selectedUserForDetails.phoneNumber || 'N/A'}
                  </p>
                </div>
                <span className="badge badge-jade" style={{ padding: '6px 12px', fontSize: '0.9rem' }}>
                  <i className="fa-solid fa-book-bookmark"></i> Đang mượn {activeBorrowedBooks.length} cuốn
                </span>
              </div>

              {activeBorrowedBooks.length === 0 ? (
                <div className="text-center text-muted" style={{ padding: '36px 16px' }}>
                  <i
                    className="fa-solid fa-box-open"
                    style={{ fontSize: '3rem', marginBottom: '12px', color: 'var(--primary-color)', opacity: 0.6, display: 'block' }}
                  ></i>
                  <h5 style={{ fontWeight: 600, margin: '0 0 4px' }}>Chưa có sách mượn nào</h5>
                  <p style={{ fontSize: '0.9rem' }}>Độc giả này hiện tại không có sách nào đang mượn trong hệ thống.</p>
                </div>
              ) : (
                <div className="table-responsive">
                  <table className="table">
                    <thead>
                      <tr>
                        <th>#</th>
                        <th>Mã Phiếu</th>
                        <th>Tên Đầu Sách</th>
                        <th>Ngày Mượn</th>
                        <th>Hạn Trả</th>
                        <th>Trạng Thái</th>
                      </tr>
                    </thead>
                    <tbody>
                      {activeBorrowedBooks.map((b, idx) => {
                        const isOverdue = checkIsOverdue(b.dueDate);
                        return (
                          <tr key={b.borrowId || idx}>
                            <td className="font-bold">{idx + 1}</td>
                            <td>
                              <code style={{ fontSize: '0.85rem' }}>{b.code}</code>
                            </td>
                            <td>
                              <strong style={{ color: 'var(--text-primary)' }}>
                                <i className="fa-solid fa-book" style={{ marginRight: '6px', color: 'var(--primary-color)' }}></i>
                                {b.bookTitle}
                              </strong>
                            </td>
                            <td>{formatDate(b.borrowDate)}</td>
                            <td>
                              <span className={isOverdue ? 'text-danger font-bold' : ''}>
                                {formatDate(b.dueDate)}
                              </span>
                            </td>
                            <td>
                              {isOverdue ? (
                                <span className="badge badge-danger">
                                  <i className="fa-solid fa-triangle-exclamation"></i> Quá hạn
                                </span>
                              ) : (
                                <span className="badge badge-jade">
                                  <i className="fa-solid fa-book-bookmark"></i> Đang mượn
                                </span>
                              )}
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              )}
            </div>

            <div className="modal-footer">
              <button
                type="button"
                className="btn btn-outline-jade"
                onClick={() => setSelectedUserForDetails(null)}
              >
                Đóng
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

import React from 'react';
import { useAuth } from '../../context/AuthContext';

interface SidebarProps {
  activeTab: string;
  setActiveTab: (tab: string) => void;
  unreadCount?: number;
  onOpenAuth: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({
  activeTab,
  setActiveTab,
  unreadCount = 0,
  onOpenAuth,
}) => {
  const { user, logout } = useAuth();

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <div className="brand-icon">
          <i className="fa-solid fa-book-open-reader"></i>
        </div>
        <div>
          <div className="brand-title">Thư Viện Số</div>
          <div className="brand-tag">Jade Light Edition</div>
        </div>
      </div>

      <nav className="sidebar-nav">
        {user?.role === 'LIBRARIAN' ? (
          <>
            <div className="nav-section-title">QUẢN TRỊ THỦ THƯ</div>
            <a
              className={`nav-link ${activeTab === 'dashboard' ? 'active' : ''}`}
              onClick={() => setActiveTab('dashboard')}
            >
              <i className="fa-solid fa-chart-pie"></i>
              <span>Tổng quan</span>
            </a>
            <a
              className={`nav-link ${activeTab === 'books' ? 'active' : ''}`}
              onClick={() => setActiveTab('books')}
            >
              <i className="fa-solid fa-book"></i>
              <span>Quản lý Đầu Sách</span>
            </a>
            <a
              className={`nav-link ${activeTab === 'categories' ? 'active' : ''}`}
              onClick={() => setActiveTab('categories')}
            >
              <i className="fa-solid fa-tags"></i>
              <span>Danh mục Sách</span>
            </a>
            <a
              className={`nav-link ${activeTab === 'borrows' ? 'active' : ''}`}
              onClick={() => setActiveTab('borrows')}
            >
              <i className="fa-solid fa-clipboard-list"></i>
              <span>Mượn / Trả Sách</span>
            </a>
            <a
              className={`nav-link ${activeTab === 'users' ? 'active' : ''}`}
              onClick={() => setActiveTab('users')}
            >
              <i className="fa-solid fa-users"></i>
              <span>Quản lý Người Dùng</span>
            </a>
          </>
        ) : (
          <>
            <div className="nav-section-title">KHÔNG GIAN ĐỘC GIẢ</div>
            <a
              className={`nav-link ${activeTab === 'home' ? 'active' : ''}`}
              onClick={() => setActiveTab('home')}
            >
              <i className="fa-solid fa-house"></i>
              <span>Trang chủ Độc giả</span>
            </a>
            <a
              className={`nav-link ${activeTab === 'catalog' ? 'active' : ''}`}
              onClick={() => setActiveTab('catalog')}
            >
              <i className="fa-solid fa-book-open"></i>
              <span>Tra cứu Kho Sách</span>
            </a>
            <a
              className={`nav-link ${activeTab === 'history' ? 'active' : ''}`}
              onClick={() => setActiveTab('history')}
            >
              <i className="fa-solid fa-clock-rotate-left"></i>
              <span>Lịch sử Mượn Trả</span>
            </a>
            <a
              className={`nav-link ${activeTab === 'notifications' ? 'active' : ''}`}
              onClick={() => setActiveTab('notifications')}
            >
              <i className="fa-solid fa-bell"></i>
              <span>Thông báo</span>
              {unreadCount > 0 && (
                <span className="badge badge-danger" style={{ marginLeft: 'auto' }}>
                  {unreadCount}
                </span>
              )}
            </a>
          </>
        )}
      </nav>

      {/* User Footer */}
      <div className="sidebar-user">
        <div className="user-badge-info">
          <i className="fa-solid fa-circle-user user-avatar-icon"></i>
          <div className="user-name-role">
            <h4>{user ? user.fullName || user.username : 'Khách ghé thăm'}</h4>
            <span>{user?.role === 'LIBRARIAN' ? 'Thủ thư Thư viện' : user?.role === 'BORROWER' ? 'Độc giả' : 'Chưa đăng nhập'}</span>
          </div>
        </div>
        {user ? (
          <button className="btn btn-sm btn-outline-jade" onClick={logout} title="Đăng xuất">
            <i className="fa-solid fa-right-from-bracket"></i>
          </button>
        ) : (
          <button className="btn btn-sm btn-primary" onClick={onOpenAuth}>
            Đăng nhập
          </button>
        )}
      </div>
    </aside>
  );
};

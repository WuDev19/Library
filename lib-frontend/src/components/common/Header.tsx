import React from 'react';
import { useAuth } from '../../context/AuthContext';

interface HeaderProps {
  title: string;
  onOpenSettings: () => void;
  onOpenAuth: () => void;
}

export const Header: React.FC<HeaderProps> = ({ title, onOpenSettings, onOpenAuth }) => {
  const { user, token } = useAuth();

  return (
    <header className="top-header">
      <div className="header-title">
        <h2>{title}</h2>
      </div>
      <div className="header-controls">
        {token && (
          <span className="badge badge-jade" title={`JWT Token: ${token}`}>
            <i className="fa-solid fa-key"></i> Đã xác thực JWT
          </span>
        )}

        {user && (
          <span className="badge badge-jade">
            <i className="fa-solid fa-user-shield"></i> Vai trò: {user.role === 'LIBRARIAN' ? 'Thủ thư' : 'Độc giả'}
          </span>
        )}

        {!user && (
          <button className="btn btn-sm btn-primary" onClick={onOpenAuth}>
            <i className="fa-solid fa-right-to-bracket"></i> Đăng nhập
          </button>
        )}

        <button className="btn btn-sm btn-outline-jade" onClick={onOpenSettings} title="Cấu hình kết nối API Gateway">
          <i className="fa-solid fa-gears"></i> Cấu hình API
        </button>
      </div>
    </header>
  );
};

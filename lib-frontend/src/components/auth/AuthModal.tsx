import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';
import type { UserRole } from '../../types';

interface AuthModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const AuthModal: React.FC<AuthModalProps> = ({ isOpen, onClose }) => {
  const { login, register } = useAuth();
  const { showToast } = useToast();
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [loginForm, setLoginForm] = useState({ username: '', password: '' });
  const [regForm, setRegForm] = useState({
    username: '',
    email: '',
    password: '',
    passwordConfirm: '',
    fullName: '',
    phone: '0912345678',
    role: 'BORROWER' as UserRole,
  });

  if (!isOpen) return null;

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!loginForm.username || !loginForm.password) {
      showToast('Vui lòng điền đầy đủ tên đăng nhập và mật khẩu', 'error');
      return;
    }
    try {
      await login(loginForm.username, loginForm.password);
      showToast('Đăng nhập hệ thống thành công!');
      onClose();
    } catch (err: unknown) {
      showToast((err as Error).message || 'Đăng nhập thất bại. Kiểm tra kết nối Backend hoặc thông tin tài khoản!', 'error');
    }
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!regForm.username || !regForm.password || !regForm.passwordConfirm || !regForm.fullName || !regForm.email) {
      showToast('Vui lòng điền đầy đủ các thông tin bắt buộc (*)', 'error');
      return;
    }
    if (regForm.password !== regForm.passwordConfirm) {
      showToast('Mật khẩu xác nhận không trùng khớp với mật khẩu!', 'error');
      return;
    }

    try {
      await register({
        username: regForm.username,
        email: regForm.email,
        password: regForm.password,
        passwordConfirm: regForm.passwordConfirm,
        fullName: regForm.fullName,
        phone: regForm.phone,
        role: regForm.role,
      });
      showToast('Đăng ký tài khoản thành công! Vui lòng đăng nhập.');
      setMode('login');
    } catch (err: unknown) {
      showToast((err as Error).message || 'Đăng ký thất bại. Kiểm tra quy tắc mật khẩu (8+ ký tự, A-z, 0-9, @)', 'error');
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-card" style={{ maxWidth: mode === 'register' ? '560px' : '440px' }}>
        <div className="modal-header">
          <h3>
            <i className="fa-solid fa-user-lock"></i>{' '}
            {mode === 'login' ? 'Đăng Nhập Tài Khoản REST API' : 'Đăng Ký Tài Khoản Mới'}
          </h3>
          <button className="modal-close" onClick={onClose}>
            <i className="fa-solid fa-xmark"></i>
          </button>
        </div>
        <div className="modal-body">
          {mode === 'login' ? (
            <form onSubmit={handleLogin}>
              <div className="form-group">
                <label>
                  <i className="fa-solid fa-user"></i> Tên đăng nhập
                </label>
                <input
                  type="text"
                  placeholder="Nhập tên đăng nhập (VD: admin, reader1)"
                  value={loginForm.username}
                  onChange={(e) => setLoginForm({ ...loginForm, username: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>
                  <i className="fa-solid fa-lock"></i> Mật khẩu
                </label>
                <input
                  type="password"
                  placeholder="Nhập mật khẩu (VD: Admin@123)"
                  value={loginForm.password}
                  onChange={(e) => setLoginForm({ ...loginForm, password: e.target.value })}
                  required
                />
                <span style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>
                  Lưu ý Backend constraint: 8+ ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt (VD: Admin@123).
                </span>
              </div>
              <button type="submit" className="btn btn-primary btn-block" style={{ marginTop: '16px' }}>
                Đăng Nhập
              </button>
              <p style={{ textAlign: 'center', marginTop: '14px', fontSize: '0.9rem' }}>
                Chưa có tài khoản?{' '}
                <a
                  href="#"
                  onClick={() => setMode('register')}
                  style={{ color: 'var(--primary-hover)', fontWeight: 700 }}
                >
                  Đăng ký ngay
                </a>
              </p>
            </form>
          ) : (
            <form onSubmit={handleRegister}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                <div className="form-group">
                  <label>Tên đăng nhập *</label>
                  <input
                    type="text"
                    placeholder="VD: reader_an"
                    value={regForm.username}
                    onChange={(e) => setRegForm({ ...regForm, username: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Họ và tên *</label>
                  <input
                    type="text"
                    placeholder="VD: Nguyễn Văn An"
                    value={regForm.fullName}
                    onChange={(e) => setRegForm({ ...regForm, fullName: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                <div className="form-group">
                  <label>Email *</label>
                  <input
                    type="email"
                    placeholder="example@gmail.com"
                    value={regForm.email}
                    onChange={(e) => setRegForm({ ...regForm, email: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Số điện thoại *</label>
                  <input
                    type="text"
                    placeholder="0912345678"
                    value={regForm.phone}
                    onChange={(e) => setRegForm({ ...regForm, phone: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label>Vai trò hệ thống *</label>
                <select
                  value={regForm.role}
                  onChange={(e) => setRegForm({ ...regForm, role: e.target.value as UserRole })}
                >
                  <option value="BORROWER">Độc giả (BORROWER)</option>
                  <option value="LIBRARIAN">Thủ thư (LIBRARIAN)</option>
                </select>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                <div className="form-group">
                  <label>Mật khẩu *</label>
                  <input
                    type="password"
                    placeholder="Admin@123"
                    value={regForm.password}
                    onChange={(e) => setRegForm({ ...regForm, password: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Xác nhận mật khẩu *</label>
                  <input
                    type="password"
                    placeholder="Admin@123"
                    value={regForm.passwordConfirm}
                    onChange={(e) => setRegForm({ ...regForm, passwordConfirm: e.target.value })}
                    required
                  />
                </div>
              </div>

              <span style={{ fontSize: '0.78rem', color: 'var(--text-muted)', display: 'block', marginBottom: '14px' }}>
                Yêu cầu mật khẩu Backend: Tối thiểu 8 ký tự, gồm chữ HOA, chữ thường, chữ số và ký tự đặc biệt (VD: Admin@123).
              </span>

              <button type="submit" className="btn btn-secondary btn-block">
                Tạo Tài Khoản
              </button>
              <p style={{ textAlign: 'center', marginTop: '14px', fontSize: '0.9rem' }}>
                Đã có tài khoản?{' '}
                <a
                  href="#"
                  onClick={() => setMode('login')}
                  style={{ color: 'var(--primary-hover)', fontWeight: 700 }}
                >
                  Đăng nhập
                </a>
              </p>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};

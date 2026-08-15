import React, { useState } from 'react';
import { getApiBaseUrl, setApiBaseUrl } from '../../services/api';
import { useToast } from '../../context/ToastContext';

interface ApiSettingsModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const ApiSettingsModal: React.FC<ApiSettingsModalProps> = ({ isOpen, onClose }) => {
  const [url, setUrl] = useState<string>(getApiBaseUrl());
  const { showToast } = useToast();

  if (!isOpen) return null;

  const handleSave = () => {
    setApiBaseUrl(url);
    showToast(`Đã lưu URL kết nối API Gateway: ${url}`);
    onClose();
  };

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <div className="modal-header">
          <h3>
            <i className="fa-solid fa-gears"></i> Cấu Hình Kết Nối API Backend
          </h3>
          <button className="modal-close" onClick={onClose}>
            <i className="fa-solid fa-xmark"></i>
          </button>
        </div>
        <div className="modal-body">
          <div className="form-group">
            <label>API Base URL (Spring Boot Gateway)</label>
            <input type="text" value={url} onChange={(e) => setUrl(e.target.value)} />
          </div>
          <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
            Mặc định kết nối tới Spring Boot REST Gateway tại: <code>http://localhost:8080/api/v1</code>.
          </p>
        </div>
        <div className="modal-footer">
          <button className="btn btn-outline-jade" onClick={onClose}>
            Hủy
          </button>
          <button className="btn btn-primary" onClick={handleSave}>
            Lưu Cấu Hình
          </button>
        </div>
      </div>
    </div>
  );
};

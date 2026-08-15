import React from 'react';
import type { NotificationItem } from '../../types';

interface NotificationsInboxProps {
  notifications: NotificationItem[];
  onMarkAsRead?: (id: number) => void;
}

export const NotificationsInbox: React.FC<NotificationsInboxProps> = ({
  notifications,
  onMarkAsRead,
}) => {
  return (
    <div style={{ maxWidth: '750px', margin: '0 auto' }}>
      <h3 className="font-bold mb-3">
        <i className="fa-solid fa-bell"></i> Hộp Thư Thông Báo ({notifications.length})
      </h3>
      {notifications.length === 0 ? (
        <div className="glass-card text-center" style={{ padding: '48px' }}>
          <i
            className="fa-solid fa-envelope-open"
            style={{ fontSize: '3rem', color: 'var(--primary)', marginBottom: '14px' }}
          ></i>
          <h3>Không có thông báo mới</h3>
          <p className="text-muted" style={{ marginTop: '6px' }}>
            Hệ thống Notification Service chưa ghi nhận thông báo nào cho tài khoản này.
          </p>
        </div>
      ) : (
        notifications.map((n) => (
          <div
            key={n.id}
            className="glass-card"
            style={{
              padding: '18px',
              marginBottom: '14px',
              borderLeft: `4px solid ${
                n.type === 'OVERDUE_WARNING' ? 'var(--danger)' : 'var(--primary)'
              }`,
              opacity: n.read ? 0.75 : 1,
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px' }}>
              <h4 className="font-bold">{n.title}</h4>
              <span style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>{n.createdAt}</span>
            </div>
            <p style={{ fontSize: '0.9rem', color: 'var(--text-main)' }}>{n.message}</p>
            {!n.read && onMarkAsRead && (
              <button
                className="btn btn-sm btn-outline-jade"
                style={{ marginTop: '10px' }}
                onClick={() => onMarkAsRead(n.id)}
              >
                Đánh dấu đã đọc
              </button>
            )}
          </div>
        ))
      )}
    </div>
  );
};

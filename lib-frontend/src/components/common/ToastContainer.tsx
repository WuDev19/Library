import React from 'react';
import { useToast } from '../../context/ToastContext';

export const ToastContainer: React.FC = () => {
  const { toasts } = useToast();

  if (toasts.length === 0) return null;

  return (
    <div className="toast-container">
      {toasts.map((t) => (
        <div key={t.id} className={`toast toast-${t.type}`}>
          <i
            className={`fa-solid ${
              t.type === 'error'
                ? 'fa-circle-exclamation'
                : t.type === 'warning'
                ? 'fa-triangle-exclamation'
                : 'fa-circle-check'
            }`}
          ></i>
          <span>{t.message}</span>
        </div>
      ))}
    </div>
  );
};

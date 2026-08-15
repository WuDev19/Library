CREATE TABLE notifications
(
    notification_id  BIGSERIAL PRIMARY KEY,
    user_id          BIGINT      NOT NULL,
    borrow_record_id BIGINT      NOT NULL,
    type             VARCHAR(50) NOT NULL,
    message          TEXT        NOT NULL,
    is_read          BOOLEAN     NOT NULL DEFAULT FALSE,
    sent_at          TIMESTAMPTZ NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notifications_user ON notifications (user_id);

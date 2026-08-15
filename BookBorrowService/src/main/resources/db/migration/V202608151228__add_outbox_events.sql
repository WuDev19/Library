CREATE TABLE outbox_events
(
    outbox_id      BIGSERIAL PRIMARY KEY,
    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id   VARCHAR(50) NOT NULL,
    event_type     VARCHAR(50) NOT NULL,
    payload        TEXT        NOT NULL,
    status         VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    processed_at   TIMESTAMPTZ
);

CREATE INDEX idx_outbox_events_status ON outbox_events (status);
CREATE INDEX idx_outbox_events_aggregate ON outbox_events (aggregate_id, event_type);

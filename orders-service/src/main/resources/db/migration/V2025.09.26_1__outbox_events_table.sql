
CREATE SCHEMA outbox;

CREATE TABLE outbox.outbox_events (
    id bigserial PRIMARY KEY,
    created_at timestamp NOT NULL DEFAULT NOW(),
    type varchar NOT NULL CHECK (type in ('ORDER_CREATED')),
    status varchar NOT NULL CHECK (status in ('PENDING', 'SENT')) DEFAULT 'PENDING',
    updated_at timestamp,
    payload jsonb NOT NULL
);
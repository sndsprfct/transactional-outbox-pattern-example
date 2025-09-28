
-- Adding new outbox event's type - 'ORDER_CANCELLED'

ALTER TABLE outbox.outbox_events
DROP CONSTRAINT outbox_events_type_check;

ALTER TABLE outbox.outbox_events
ADD CONSTRAINT outbox_events_type_check CHECK (type in ('ORDER_CREATED', 'ORDER_CANCELLED'));
CREATE TABLE orders.orders (
	id bigserial PRIMARY KEY,
	customer_id bigint NOT NULL,
	idempotency_key uuid NOT NULL,
	created_at timestamp NOT NULL DEFAULT NOW(),
	delivery_address varchar NOT NULL,
	status varchar NOT NULL CHECK (status IN ('PENDING', 'PROCESSING', 'DELIVERED', 'COMPLETED', 'CANCELED'))
);
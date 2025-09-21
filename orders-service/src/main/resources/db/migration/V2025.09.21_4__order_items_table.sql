CREATE TABLE orders.order_items (
	id bigserial PRIMARY KEY,
	order_id bigint NOT NULL,
	product_id bigint NOT NULL,
	quantity int NOT NULL CHECK (quantity > 0),
	unit_price bigint NOT NULL CHECK (unit_price > 0), -- Unit price at the time of order creation
	FOREIGN KEY (order_id) REFERENCES orders.orders (id),
	FOREIGN KEY (product_id) REFERENCES orders.products (id)
);

COMMENT ON COLUMN orders.order_items.unit_price IS 'Unit price at the time of order creation';
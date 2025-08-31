CREATE TABLE tariffs (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    boost_factor DOUBLE PRECISION NOT NULL,
    active boolean DEFAULT TRUE NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp
);
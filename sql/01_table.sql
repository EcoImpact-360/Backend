-- 1. Escuelas
CREATE TABLE schools (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL
);

-- 2. Tipos de Residuo
CREATE TABLE waste_types (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(20),
    co2factor DOUBLE PRECISION NOT NULL,
    max_kg_per_week DOUBLE PRECISION,
    category VARCHAR(50)
);

-- 3. Aulas
CREATE TABLE classrooms (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    score INTEGER DEFAULT 0,
    school_id INTEGER REFERENCES schools(id)
);

-- 4. Entradas de Residuo
CREATE TABLE waste_entries (
    id SERIAL PRIMARY KEY,
    quantity DOUBLE PRECISION NOT NULL,
    impact_points DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Simplificado aquí
    waste_type_id INTEGER REFERENCES waste_types(id),
    school_id INTEGER REFERENCES schools(id),
    classroom_id INTEGER REFERENCES classrooms(id)
);
-- Insertar Escuela
INSERT INTO schools (name, city) VALUES ('IES EcoImpact', 'Barcelona');

-- Insertar Aula (El ID de escuela será 1)
INSERT INTO classrooms (name, score, school_id) VALUES ('1º ESO A', 0, 1);

-- Insertar Tipos de Residuo
INSERT INTO waste_types (name, color, co2factor, max_kg_per_week, category) 
VALUES ('Plastico', '#3498DB', 2.5, 30.0, 'PLASTIC');
INSERT INTO waste_types (name, color, co2factor, max_kg_per_week, category) 
VALUES ('Papel', '#F39C12', 0.9, 40.0, 'PAPER');
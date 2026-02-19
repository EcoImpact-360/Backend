INSERT INTO schools
    (id, name, city)
VALUES
    (1, 'IES EcoImpact', 'Barcelona')
ON CONFLICT DO NOTHING;

INSERT INTO classrooms
    (id, name, score, school_id)
VALUES
    (1, '1ºA', 4, 1)
ON CONFLICT DO NOTHING;
INSERT INTO classrooms
    (id, name, score, school_id)
VALUES
    (2, '2ºB', 2, 1)
ON CONFLICT DO NOTHING;
INSERT INTO classrooms
    (id, name, score, school_id)
VALUES
    (3, '3ºC', 1, 1)
ON CONFLICT DO NOTHING;
INSERT INTO classrooms
    (id, name, score, school_id)
VALUES
    (4, 'Comedor', 0, 1)
ON CONFLICT DO NOTHING;

INSERT INTO waste_types
    (id, name, color, co2factor, max_kg_per_week, category)
VALUES
    (1, 'Plastico', '#3498DB', 2.5, 30.0, 'PLASTIC')
ON CONFLICT DO NOTHING;
INSERT INTO waste_types
    (id, name, color, co2factor, max_kg_per_week, category)
VALUES
    (2, 'Papel', '#F39C12', 0.9, 40.0, 'PAPER')
ON CONFLICT DO NOTHING;
INSERT INTO waste_types
    (id, name, color, co2factor, max_kg_per_week, category)
VALUES
    (3, 'Organico', '#27AE60', 0.5, 50.0, 'ORGANIC')
ON CONFLICT DO NOTHING;
INSERT INTO waste_types
    (id, name, color, co2factor, max_kg_per_week, category)
VALUES
    (4, 'Vidrio', '#8E44AD', 0.6, 20.0, 'GLASS')
ON CONFLICT DO NOTHING;
INSERT INTO waste_types
    (id, name, color, co2factor, max_kg_per_week, category)
VALUES
    (5, 'General', '#7F8C8D', 2.0, 60.0, 'GENERAL')
ON CONFLICT DO NOTHING;


INSERT INTO waste_entries (co2equivalent, created_at, quantity_kg, status, classroom_id, waste_type_id) VALUES  (9.2, NOW(), 2.1, 'PENDING', 1, 5);
INSERT INTO waste_entries (co2equivalent, created_at, quantity_kg, status, classroom_id, waste_type_id) VALUES  (3.8, NOW(), 5.9, 'PENDING', 3, 4);
INSERT INTO waste_entries (co2equivalent, created_at, quantity_kg, status, classroom_id, waste_type_id) VALUES  (6.9, NOW(), 7.3, 'PENDING', 4, 2);
INSERT INTO waste_entries (co2equivalent, created_at, quantity_kg, status, classroom_id, waste_type_id) VALUES  (7.5, NOW(), 1.1, 'PENDING', 2, 1);

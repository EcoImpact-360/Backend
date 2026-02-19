INSERT INTO schools (id, name, city) 
VALUES (1, 'IES EcoImpact', 'Barcelona') 
ON CONFLICT DO NOTHING;

INSERT INTO classrooms (name, score, school_id) VALUES ('1ºA', 4, 1) ON CONFLICT DO NOTHING;
INSERT INTO classrooms (name, score, school_id) VALUES ('2ºB', 2, 1) ON CONFLICT DO NOTHING;
INSERT INTO classrooms (name, score, school_id) VALUES ('3ºC', 1, 1) ON CONFLICT DO NOTHING;
INSERT INTO classrooms (name, score, school_id) VALUES ('Comedor', 0, 1) ON CONFLICT DO NOTHING;

INSERT INTO waste_types (name, color, co2factor, max_kg_per_week, category)
VALUES ('Plastico', '#3498DB', 2.5, 30.0, 'PLASTIC') ON CONFLICT DO NOTHING;
INSERT INTO waste_types (name, color, co2factor, max_kg_per_week, category)
VALUES ('Papel', '#F39C12', 0.9, 40.0, 'PAPER') ON CONFLICT DO NOTHING;
INSERT INTO waste_types (name, color, co2factor, max_kg_per_week, category)
VALUES ('Organico', '#27AE60', 0.5, 50.0, 'ORGANIC') ON CONFLICT DO NOTHING;
INSERT INTO waste_types (name, color, co2factor, max_kg_per_week, category)
VALUES ('Vidrio', '#8E44AD', 0.6, 20.0, 'GLASS') ON CONFLICT DO NOTHING;
INSERT INTO waste_types (name, color, co2factor, max_kg_per_week, category)
VALUES ('General', '#7F8C8D', 2.0, 60.0, 'GENERAL') ON CONFLICT DO NOTHING;
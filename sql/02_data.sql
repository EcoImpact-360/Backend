-- Ver todos los tipos de residuos y sus factores de CO2
SELECT * FROM waste_types;

-- Ver las escuelas registradas
SELECT * FROM schools;

-- Ver las aulas y a qué escuela pertenecen
SELECT c.name as aula, s.name as escuela, c.score 
FROM classrooms c 
JOIN schools s ON c.school_id = s.id;
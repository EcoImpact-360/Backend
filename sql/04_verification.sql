-- ¿Qué aula pertenece a qué escuela?
SELECT c.name AS aula, s.name AS escuela 
FROM classrooms c
JOIN schools s ON c.school_id = s.id;

-- ¿Cuántos residuos hemos registrado hoy?
SELECT * FROM waste_entries WHERE created_at >= CURRENT_DATE;
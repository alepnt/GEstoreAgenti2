INSERT INTO roles (name)
SELECT 'Amministratore'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'Amministratore');

INSERT INTO roles (name)
SELECT 'Responsabile Team'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'Responsabile Team');

INSERT INTO roles (name)
SELECT 'Back Office'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'Back Office');

INSERT INTO teams (name)
SELECT 'Vendite Nord'
WHERE NOT EXISTS (SELECT 1 FROM teams WHERE name = 'Vendite Nord');

INSERT INTO teams (name)
SELECT 'Vendite Sud'
WHERE NOT EXISTS (SELECT 1 FROM teams WHERE name = 'Vendite Sud');

INSERT INTO teams (name)
SELECT 'Marketing'
WHERE NOT EXISTS (SELECT 1 FROM teams WHERE name = 'Marketing');

INSERT INTO teams (name)
SELECT 'Supporto Clienti'
WHERE NOT EXISTS (SELECT 1 FROM teams WHERE name = 'Supporto Clienti');

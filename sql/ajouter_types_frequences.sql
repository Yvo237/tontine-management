
DELETE FROM typetontine;

-- Insertion des nouveaux types de tontine avec fréquences variées
INSERT INTO typetontine (id_type, nom, description, est_obligatoire, montant_cotisation, frequence) VALUES 
(1, 'Tontine Journalière', 'Tontine obligatoire quotidienne pour les commerçants', true, 500.00, 'journaliere'),
(2, 'Tontie Hebdomadaire', 'Tontine hebdomadaire pour salariés', true, 2000.00, 'hebdomadaire'),
(3, 'Tontine Mensuelle', 'Tontine mensuelle classique', true, 5000.00, 'mensuel'),
(4, 'Tontie Journalière Optionnelle', 'Tontine optionnelle quotidienne', false, 300.00, 'journaliere'),
(5, 'Tontine Hebdomadaire Optionnelle', 'Tontine optionnelle hebdomadaire', false, 1500.00, 'hebdomadaire'),
(6, 'Tontine Mensuelle Optionnelle', 'Tontine optionnelle mensuelle', false, 3000.00, 'mensuel');

-- Vérification
SELECT * FROM typetontine ORDER BY id_type;

-- Mise à jour de la contrainte CHECK pour inclure 'journaliere'
ALTER TABLE typetontine DROP CONSTRAINT IF EXISTS typetontine_frequence_check;
ALTER TABLE typetontine ADD CONSTRAINT typetontine_frequence_check 
CHECK (frequence IN ('journaliere', 'hebdomadaire', 'mensuel', 'bimensuel'));

COMMIT;

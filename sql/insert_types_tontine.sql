-- Script pour insérer les types de tontine de base
-- Résout les problèmes de création de tontine où id_type doit exister

-- Vérifier si la table typetontine existe et insérer les types de base
INSERT INTO typetontine (id_type, nom, description, estobligatoire, montantcotisation, frequence) VALUES 
(1, 'Présence', 'Tontine de présence obligatoire pour les réunions', true, 1000.00, 'mensuelle'),
(2, 'Optionnelle', 'Tontine optionnelle pour les membres volontaires', false, 5000.00, 'mensuelle'),
(3, 'FIAC', 'Tontine FIAC pour les projets financiers', true, 10000.00, 'mensuelle')
ON CONFLICT (id_type) DO UPDATE SET 
    nom = EXCLUDED.nom,
    description = EXCLUDED.description,
    estobligatoire = EXCLUDED.estobligatoire,
    montantcotisation = EXCLUDED.montantcotisation,
    frequence = EXCLUDED.frequence;

-- Vérification
SELECT * FROM typetontine ORDER BY id_type;

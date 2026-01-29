-- =============================================
-- Correction du problème de statut de tontine
-- =============================================

-- Étape 1 : Mettre à jour les données existantes avec "terminée" vers "terminee"
UPDATE tontine 
SET statut = 'terminee' 
WHERE statut = 'terminée';

-- Étape 2 : Mettre à jour les autres variantes possibles
UPDATE tontine 
SET statut = 'suspendue' 
WHERE statut = 'suspendu';

UPDATE tontine 
SET statut = 'active' 
WHERE statut = 'actif';

-- Étape 3 : Vérifier les données après correction
SELECT statut, COUNT(*) as nombre 
FROM tontine 
GROUP BY statut;

-- Étape 4 : (Optionnel) Ajouter une contrainte plus stricte si nécessaire
-- ALTER TABLE tontine DROP CONSTRAINT IF EXISTS tontine_statut_check;
-- ALTER TABLE tontine ADD CONSTRAINT tontine_statut_check 
-- CHECK (statut IN ('active', 'terminee', 'suspendue'));

--new row for relation 'membre' violates check constraint 'membre_statut_check'

-- Supprimer l'ancienne contrainte
ALTER TABLE membre DROP CONSTRAINT IF EXISTS membre_statut_check;

-- Ajouter la nouvelle contrainte avec toutes les valeurs autorisées
ALTER TABLE membre ADD CONSTRAINT membre_statut_check 
CHECK (statut IN ('actif', 'inactif', 'suspendu'));

-- Vérifier les données existantes et corriger si nécessaire
UPDATE membre SET statut = 'actif' WHERE statut NOT IN ('actif', 'inactif', 'suspendu');

-- Afficher les statuts actuels pour vérification
SELECT DISTINCT statut FROM membre ORDER BY statut;

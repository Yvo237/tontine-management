ALTER TABLE membre DROP CONSTRAINT IF EXISTS membre_statut_check;

ALTER TABLE membre ADD CONSTRAINT membre_statut_check 
CHECK (statut IN ('actif', 'inactif', 'suspendu'));

UPDATE membre SET statut = 'actif' WHERE statut NOT IN ('actif', 'inactif', 'suspendu');

SELECT DISTINCT statut FROM membre ORDER BY statut;

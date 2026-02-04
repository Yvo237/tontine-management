UPDATE tontine 
SET statut = 'terminee' 
WHERE statut = 'terminée';

UPDATE tontine 
SET statut = 'suspendue' 
WHERE statut = 'suspendu';

UPDATE tontine 
SET statut = 'active' 
WHERE statut = 'actif';

SELECT statut, COUNT(*) as nombre 
FROM tontine 
GROUP BY statut;



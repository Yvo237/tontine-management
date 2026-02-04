SELECT id_membre, nom, prenom, telephone, email, date_adhesion
FROM membre 
WHERE statut = 'actif'
ORDER BY nom, prenom;

SELECT t.id_tontine, t.nom, tt.nom as type_tontine, t.date_debut, 
       t.nombre_tours, t.tour_actuel, COUNT(p.id_participation) as nb_participants
FROM tontine t
JOIN typetontine tt ON t.id_type = tt.id_type
LEFT JOIN participation p ON t.id_tontine = p.id_tontine AND p.statut = 'active'
WHERE t.statut = 'active'
GROUP BY t.id_tontine, t.nom, tt.nom, t.date_debut, t.nombre_tours, t.tour_actuel
ORDER BY t.date_debut;

SELECT s.id_seance, t.nom as tontine, s.numero_tour, s.date_seance, 
       s.lieu, s.statut, COUNT(c.id_cotisation) as nb_cotisations
FROM seance s
JOIN tontine t ON s.id_tontine = t.id_tontine
LEFT JOIN cotisation c ON s.id_seance = c.id_seance
WHERE EXTRACT(MONTH FROM s.date_seance) = EXTRACT(MONTH FROM CURRENT_DATE()) 
  AND EXTRACT(YEAR FROM s.date_seance) = EXTRACT(YEAR FROM CURRENT_DATE())
GROUP BY s.id_seance, t.nom, s.numero_tour, s.date_seance, s.lieu, s.statut
ORDER BY s.date_seance;


SELECT cr.id_credit, m.nom || ' ' || m.prenom as membre, 
       t.nom as tontine, cr.montant_emprunte, cr.taux_interet,
       cr.montant_emprunte * (1 + cr.taux_interet/100) as montant_total,
       cr.montant_rembourse, cr.date_echeance, cr.statut
FROM credit cr
JOIN membre m ON cr.id_membre = m.id_membre
JOIN tontine t ON cr.id_tontine = t.id_tontine
WHERE cr.statut IN ('en_cours', 'en_retard')
ORDER BY cr.date_echeance;


SELECT p.id_penalite, m.nom || ' ' || m.prenom as membre,
       p.motif, p.montant, p.date_penalite
FROM penalite p
JOIN membre m ON p.id_membre = m.id_membre
WHERE p.payee = FALSE
ORDER BY p.date_penalite DESC;



SELECT id_membre, nom, prenom, telephone, email, statut
FROM membre 
WHERE nom LIKE '%' || $recherche || '%'
   OR prenom LIKE '%' || $recherche || '%'
   OR telephone LIKE '%' || $recherche || '%'
ORDER BY nom, prenom;


SELECT c.id_cotisation, s.numero_tour, s.date_seance, c.montant_du, 
       c.montant, c.statut, c.date_paiement
FROM cotisation c
JOIN participation p ON c.id_membre = p.id_membre
JOIN seance s ON c.id_seance = s.id_seance
WHERE p.id_membre = $id_membre 
  AND p.id_tontine = $id_tontine
ORDER BY s.numero_tour;


SELECT cr.id_credit, t.nom as tontine, cr.montant_emprunte, cr.taux_interet,
       cr.date_emprunt, cr.date_echeance, cr.montant_rembourse,
       cr.montant_emprunte * (1 + cr.taux_interet/100) as montant_total,
       cr.statut
FROM credit cr
JOIN tontine t ON cr.id_tontine = t.id_tontine
WHERE cr.id_membre = $id_membre
ORDER BY cr.date_emprunt DESC;


SELECT t.id_tontine, t.nom, 
       COUNT(DISTINCT p.id_membre) as nb_participants,
       SUM(c.montant_du) as total_du,
       SUM(c.montant) as total_verse,
       ROUND((SUM(c.montant) / SUM(c.montant_du)) * 100, 2) as taux_recouvrement
FROM tontine t
LEFT JOIN participation p ON t.id_tontine = p.id_tontine AND p.statut = 'active'
LEFT JOIN seance s ON t.id_tontine = s.id_tontine
LEFT JOIN cotisation c ON s.id_seance = c.id_seance
GROUP BY t.id_tontine, t.nom
ORDER BY t.nom;

SELECT m.id_membre, m.nom || ' ' || m.prenom as membre,
       COUNT(DISTINCT p.id_tontine) as nb_tontines,
       SUM(p.nombre_parts) as total_parts,
       COALESCE(SUM(c.montant), 0) as total_cotisations,
       COALESCE(SUM(b.montant_gain), 0) as total_gains,
       COALESCE(SUM(cr.montant_emprunte), 0) as total_emprunts,
       m.statut
FROM membre m
LEFT JOIN participation p ON m.id_membre = p.id_membre
LEFT JOIN cotisation c ON p.id_participation = c.id_participation
LEFT JOIN beneficiaire b ON p.id_participation = b.id_participation
LEFT JOIN credit cr ON m.id_membre = cr.id_membre
GROUP BY m.id_membre, m.nom, m.prenom, m.statut
ORDER BY m.nom, m.prenom;

SELECT t.id_tontine, t.nom,
       COUNT(s.id_seance) as nb_seances_planifiees,
       SUM(CASE WHEN s.statut = 'terminee' THEN 1 ELSE 0 END) as nb_seances_terminees,
       SUM(CASE WHEN s.statut = 'en_cours' THEN 1 ELSE 0 END) as nb_seances_en_cours,
       SUM(CASE WHEN s.statut = 'planifiee' THEN 1 ELSE 0 END) as nb_seances_planifiees_en_attente
FROM tontine t
LEFT JOIN seance s ON t.id_tontine = s.id_tontine
GROUP BY t.id_tontine, t.nom
ORDER BY t.nom;

SELECT statut, COUNT(*) as nombre,
       ROUND((COUNT(*) * 100.0) / (SELECT COUNT(*) FROM membre), 2) as pourcentage
FROM membre
GROUP BY statut
ORDER BY nombre DESC;



UPDATE credit 
SET statut = 'en_retard'
WHERE statut = 'en_cours' 
  AND date_echeance < CURRENT_DATE
  AND montant_rembourse < (montant_emprunte * (1 + taux_interet/100));


UPDATE cotisation 
SET statut = 'impaye'
WHERE statut = 'en_attente'
  AND (SELECT date_seance FROM seance WHERE id_seance = cotisation.id_seance) < CURRENT_DATE
  AND montant = 0;

UPDATE tontine 
SET tour_actuel = (
    SELECT COALESCE(MAX(numero_tour), 0) + 1
    FROM seance 
    WHERE id_tontine = tontine.id_tontine 
      AND statut = 'terminee'
)
WHERE statut = 'active'
  AND tour_actuel < nombre_tours;


SELECT s.id_seance, t.nom as tontine, s.numero_tour, s.date_seance,
       STRING_AGG(m.nom || ' ' || m.prenom, ', ') as beneficiaires,
       SUM(b.montant_gain) as total_distribue
FROM seance s
JOIN tontine t ON s.id_tontine = t.id_tontine
LEFT JOIN beneficiaire b ON s.id_seance = b.id_seance
LEFT JOIN participation p ON b.id_participation = p.id_participation
LEFT JOIN membre m ON p.id_membre = m.id_membre
GROUP BY s.id_seance, t.nom, s.numero_tour, s.date_seance
ORDER BY t.nom, s.numero_tour;


SELECT 
    s.id_seance, t.nom as tontine, s.numero_tour, s.date_seance, 
    s.lieu, s.statut, s.observations,
    COUNT(c.id_cotisation) as nb_cotisations,
    SUM(c.montant_du) as total_du,
    SUM(c.montant) as total_verse,
    SUM(c.montant_du) - SUM(c.montant) as reste_a_collecter,
    COUNT(b.id_beneficiaire) as nb_beneficiaires,
    COALESCE(SUM(b.montant_gain), 0) as total_distribue
FROM seance s
JOIN tontine t ON s.id_tontine = t.id_tontine
LEFT JOIN cotisation c ON s.id_seance = c.id_seance
LEFT JOIN beneficiaire b ON s.id_seance = b.id_seance
WHERE s.id_seance = $id_seance
GROUP BY s.id_seance, t.nom, s.numero_tour, s.date_seance, s.lieu, s.statut, s.observations;


SELECT p.id_projet, p.nom_projet, t.nom as tontine,
       p.montant_objectif, p.montant_collecte,
       ROUND((p.montant_collecte / p.montant_objectif) * 100, 2) as pourcentage_avancement,
       p.date_debut, p.date_fin_prevue, p.statut
FROM projetfiac p
JOIN tontine t ON p.id_tontine = t.id_tontine
WHERE p.statut IN ('planifie', 'en_cours')
ORDER BY p.date_debut;


SELECT 
    m.nom || ' ' || m.prenom as membre,
    p.nombre_parts,
    SUM(c.montant) as total_cotise,
    COUNT(c.id_cotisation) as nb_cotisations_payees,
    ROUND((SUM(c.montant) / SUM(c.montant_du)) * 100, 2) as taux_participation
FROM membre m
JOIN participation p ON m.id_membre = p.id_membre
JOIN cotisation c ON p.id_participation = c.id_participation
JOIN seance s ON c.id_seance = s.id_seance
WHERE p.id_tontine = $id_tontine
  AND p.statut = 'active'
GROUP BY m.id_membre, m.nom, m.prenom, p.nombre_parts
ORDER BY total_cotise DESC
LIMIT 5;

SELECT 
    COUNT(DISTINCT m.id_membre) as nb_total_membres,
    SUM(CASE WHEN m.statut = 'actif' THEN 1 ELSE 0 END) as nb_membres_actifs,
    COUNT(DISTINCT t.id_tontine) as nb_tontines,
    SUM(CASE WHEN t.statut = 'active' THEN 1 ELSE 0 END) as nb_tontines_actives,
    COALESCE(SUM(c.montant), 0) as total_cotisations,
    COALESCE(SUM(cr.montant_emprunte), 0) as total_credits_accordes,
    COALESCE(SUM(cr.montant_rembourse), 0) as total_remboursements,
    COALESCE(SUM(p.montant), 0) as total_penalites,
    COUNT(DISTINCT pr.id_projet) as nb_projets_fiac,
    COALESCE(SUM(pr.montant_collecte), 0) as total_fonds_fiac
FROM membre m
CROSS JOIN tontine t
LEFT JOIN participation part ON t.id_tontine = part.id_tontine
LEFT JOIN seance s ON t.id_tontine = s.id_tontine
LEFT JOIN cotisation c ON s.id_seance = c.id_seance
LEFT JOIN credit cr ON t.id_tontine = cr.id_tontine
LEFT JOIN penalite p ON m.id_membre = p.id_membre
LEFT JOIN projetfiac pr ON t.id_tontine = pr.id_tontine;

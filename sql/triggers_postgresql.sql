
CREATE OR REPLACE FUNCTION update_statut_credit()
RETURNS TRIGGER AS $$
DECLARE
    montant_total DECIMAL(10,2);
    total_rembourse DECIMAL(10,2);
BEGIN
    -- Calculer le montant total du crédit
    SELECT montant_emprunte * (1 + taux_interet/100) INTO montant_total
    FROM credit
    WHERE id_credit = NEW.id_credit;
    
    -- Calculer le total remboursé après ce remboursement
    SELECT COALESCE(SUM(montant), 0) INTO total_rembourse
    FROM remboursementcredit
    WHERE id_credit = NEW.id_credit;
    
    -- Mettre à jour le crédit
    UPDATE credit
    SET montant_rembourse = total_rembourse,
        statut = CASE 
            WHEN total_rembourse >= montant_total THEN 'rembourse'
            WHEN date_echeance < CURRENT_DATE AND total_rembourse < montant_total THEN 'en_retard'
            ELSE 'en_cours'
        END
    WHERE id_credit = NEW.id_credit;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_statut_credit
AFTER INSERT ON remboursementcredit
FOR EACH ROW
EXECUTE FUNCTION update_statut_credit();

CREATE OR REPLACE FUNCTION update_statut_cotisation()
RETURNS TRIGGER AS $$
BEGIN
    -- Mettre à jour le statut selon le montant versé
    NEW.statut = CASE 
        WHEN NEW.montant >= NEW.montant_du THEN 'paye'
        WHEN NEW.montant > 0 AND NEW.montant < NEW.montant_du THEN 'partiel'
        WHEN NEW.montant = 0 AND 
             (SELECT date_seance FROM seance WHERE id_seance = NEW.id_seance) < CURRENT_DATE THEN 'impaye'
        ELSE 'en_attente'
    END;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_statut_cotisation
BEFORE UPDATE ON cotisation
FOR EACH ROW
EXECUTE FUNCTION update_statut_cotisation();

CREATE OR REPLACE FUNCTION update_tour_actuel()
RETURNS TRIGGER AS $$
BEGIN
    -- Si la séance est terminée, mettre à jour le tour actuel
    IF NEW.statut = 'terminee' AND OLD.statut != 'terminee' THEN
        UPDATE tontine
        SET tour_actuel = GREATEST(tour_actuel, NEW.numero_tour + 1)
        WHERE id_tontine = NEW.id_tontine;
        
        -- Si c'est le dernier tour, marquer la tontine comme terminée
        IF NEW.numero_tour = (SELECT nombre_tours FROM tontine WHERE id_tontine = NEW.id_tontine) THEN
            UPDATE tontine
            SET statut = 'terminee',
                date_fin = CURRENT_DATE
            WHERE id_tontine = NEW.id_tontine;
        END IF;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_tour_actuel
AFTER UPDATE ON seance
FOR EACH ROW
EXECUTE FUNCTION update_tour_actuel();

CREATE OR REPLACE FUNCTION verifier_suppression_seance()
RETURNS TRIGGER AS $$
DECLARE
    cotisations_payees INTEGER;
BEGIN
    -- Vérifier s'il y a des cotisations payées pour cette séance
    SELECT COUNT(*) INTO cotisations_payees
    FROM cotisation
    WHERE id_seance = OLD.id_seance 
      AND statut IN ('paye', 'partiel');
    
    -- Si oui, empêcher la suppression
    IF cotisations_payees > 0 THEN
        RAISE EXCEPTION 'Impossible de supprimer une séance ayant des cotisations payées';
    END IF;
    
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_verifier_suppression_seance
BEFORE DELETE ON seance
FOR EACH ROW
EXECUTE FUNCTION verifier_suppression_seance();

CREATE OR REPLACE FUNCTION mettre_a_jour_credits_en_retard()
RETURNS VOID AS $$
BEGIN
    UPDATE credit
    SET statut = 'en_retard'
    WHERE statut = 'en_cours' 
      AND date_echeance < CURRENT_DATE
      AND montant_rembourse < (montant_emprunte * (1 + taux_interet/100));
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION marquer_cotisations_impayees()
RETURNS VOID AS $$
BEGIN
    UPDATE cotisation
    SET statut = 'impaye'
    WHERE statut = 'en_attente'
      AND (SELECT date_seance FROM seance WHERE id_seance = cotisation.id_seance) < CURRENT_DATE
      AND montant = 0;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE VIEW vue_situation_financiere_membre AS
SELECT 
    m.id_membre,
    m.nom,
    m.prenom,
    m.telephone,
    COALESCE(SUM(c.montant), 0) as total_cotisations,
    COALESCE(SUM(b.montant_gain), 0) as total_gains,
    COALESCE(SUM(cr.montant_emprunte), 0) as total_emprunts,
    COALESCE(SUM(cr.montant_rembourse), 0) as total_remboursements,
    COALESCE(SUM(p.montant), 0) as total_penalites,
    (COALESCE(SUM(c.montant), 0) + COALESCE(SUM(b.montant_gain), 0) - 
     COALESCE(SUM(cr.montant_emprunte), 0) + COALESCE(SUM(cr.montant_rembourse), 0) - 
     COALESCE(SUM(p.montant), 0)) as solde_net
FROM membre m
LEFT JOIN participation part ON m.id_membre = part.id_membre
LEFT JOIN cotisation c ON part.id_participation = c.id_participation
LEFT JOIN beneficiaire b ON part.id_participation = b.id_participation
LEFT JOIN credit cr ON m.id_membre = cr.id_membre
LEFT JOIN penalite p ON m.id_membre = p.id_membre
GROUP BY m.id_membre, m.nom, m.prenom, m.telephone;

CREATE OR REPLACE VIEW vue_etat_tontines AS
SELECT 
    t.id_tontine,
    t.nom as nom_tontine,
    tt.nom as type_tontine,
    t.date_debut,
    t.date_fin,
    t.nombre_tours,
    t.tour_actuel,
    t.statut,
    COUNT(DISTINCT p.id_participation) as nombre_participants,
    SUM(p.nombre_parts) as nombre_total_parts,
    COALESCE(SUM(c.montant), 0) as total_cotisations_collectees,
    COALESCE(SUM(b.montant_gain), 0) as total_gains_distribues
FROM tontine t
JOIN typetontine tt ON t.id_type = tt.id_type
LEFT JOIN participation p ON t.id_tontine = p.id_tontine AND p.statut = 'active'
LEFT JOIN seance s ON t.id_tontine = s.id_tontine
LEFT JOIN cotisation c ON s.id_seance = c.id_seance
LEFT JOIN beneficiaire b ON s.id_seance = b.id_seance
GROUP BY t.id_tontine, t.nom, tt.nom, t.date_debut, t.date_fin, t.nombre_tours, t.tour_actuel, t.statut;

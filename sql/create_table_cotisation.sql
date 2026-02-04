CREATE TABLE IF NOT EXISTS cotisation (
    id_cotisation SERIAL PRIMARY KEY,
    id_seance INTEGER NOT NULL,
    id_membre INTEGER NOT NULL,
    montant_du DECIMAL(10,2) NOT NULL CHECK (montant_du > 0),
    montant DECIMAL(10,2) DEFAULT 0 CHECK (montant >= 0),
    statut VARCHAR(20) DEFAULT 'en_attente' CHECK (statut IN ('en_attente', 'paye', 'partiel', 'impaye')),
    date_paiement DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_seance) REFERENCES seance(id_seance) ON DELETE CASCADE,
    FOREIGN KEY (id_membre) REFERENCES membre(id_membre) ON DELETE CASCADE,
    UNIQUE (id_seance, id_membre)
);


CREATE INDEX IF NOT EXISTS idx_cotisation_seance ON cotisation(id_seance);
CREATE INDEX IF NOT EXISTS idx_cotisation_membre ON cotisation(id_membre);
CREATE INDEX IF NOT EXISTS idx_cotisation_statut ON cotisation(statut);

CREATE OR REPLACE FUNCTION update_cotisation_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_cotisation_timestamp
BEFORE UPDATE ON cotisation
FOR EACH ROW
EXECUTE FUNCTION update_cotisation_timestamp();

COMMENT ON TABLE cotisation IS 'Table des cotisations des membres pour chaque séance';
COMMENT ON COLUMN cotisation.montant_du IS 'Montant que le membre doit payer pour cette séance';
COMMENT ON COLUMN cotisation.montant IS 'Montant effectivement payé par le membre';
COMMENT ON COLUMN cotisation.statut IS 'Statut du paiement : en_attente, paye, partiel, impaye';

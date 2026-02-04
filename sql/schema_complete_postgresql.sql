CREATE TABLE typetontine (
    id_type SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT,
    est_obligatoire BOOLEAN DEFAULT FALSE,
    montant_cotisation DECIMAL(10,2) NOT NULL CHECK (montant_cotisation > 0),
    frequence VARCHAR(20) DEFAULT 'mensuel' CHECK (frequence IN ('hebdomadaire', 'mensuel', 'bimensuel')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE membre (
    id_membre SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    telephone VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100),
    adresse TEXT,
    date_adhesion DATE NOT NULL DEFAULT CURRENT_DATE,
    statut VARCHAR(20) DEFAULT 'actif' CHECK (statut IN ('actif', 'inactif', 'suspendu')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tontine (
    id_tontine SERIAL PRIMARY KEY,
    id_type INTEGER NOT NULL,
    nom VARCHAR(100) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE,
    nombre_tours INTEGER NOT NULL CHECK (nombre_tours >= 1),
    tour_actuel INTEGER DEFAULT 1 CHECK (tour_actuel <= nombre_tours),
    statut VARCHAR(20) DEFAULT 'active' CHECK (statut IN ('active', 'terminee', 'suspendue')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_type) REFERENCES typetontine(id_type) ON DELETE RESTRICT,
    CHECK (date_fin IS NULL OR date_fin > date_debut)
);

CREATE TABLE participation (
    id_participation SERIAL PRIMARY KEY,
    id_membre INTEGER NOT NULL,
    id_tontine INTEGER NOT NULL,
    nombre_parts INTEGER DEFAULT 1 CHECK (nombre_parts >= 1),
    date_participation DATE NOT NULL DEFAULT CURRENT_DATE,
    statut VARCHAR(20) DEFAULT 'active' CHECK (statut IN ('active', 'retiree')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_membre) REFERENCES membre(id_membre) ON DELETE CASCADE,
    FOREIGN KEY (id_tontine) REFERENCES tontine(id_tontine) ON DELETE CASCADE,
    UNIQUE (id_membre, id_tontine)
);

CREATE TABLE seance (
    id_seance SERIAL PRIMARY KEY,
    id_tontine INTEGER NOT NULL,
    numero_tour INTEGER NOT NULL,
    date_seance DATE NOT NULL,
    lieu VARCHAR(200),
    statut VARCHAR(20) DEFAULT 'planifiee' CHECK (statut IN ('planifiee', 'en_cours', 'terminee')),
    observations TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_tontine) REFERENCES tontine(id_tontine) ON DELETE CASCADE,
    UNIQUE (id_tontine, numero_tour),
    CHECK (numero_tour <= (SELECT nombre_tours FROM tontine WHERE id_tontine = seance.id_tontine))
);

CREATE TABLE cotisation (
    id_cotisation SERIAL PRIMARY KEY,
    id_seance INTEGER NOT NULL,
    id_membre INTEGER NOT NULL,
    montant DECIMAL(10,2) NOT NULL CHECK (montant > 0),
    date_paiement DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_seance) REFERENCES seance(id_seance) ON DELETE CASCADE,
    FOREIGN KEY (id_membre) REFERENCES membre(id_membre) ON DELETE CASCADE
);

CREATE TABLE beneficiaire (
    id_beneficiaire SERIAL PRIMARY KEY,
    id_seance INTEGER NOT NULL,
    id_participation INTEGER NOT NULL,
    montant_gain DECIMAL(10,2) NOT NULL CHECK (montant_gain > 0),
    date_attribution DATE NOT NULL,
    mode_paiement VARCHAR(20) DEFAULT 'especes' CHECK (mode_paiement IN ('especes', 'virement', 'cheque', 'mobile_money')),
    statut VARCHAR(20) DEFAULT 'attribue' CHECK (statut IN ('attribue', 'paye')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_seance) REFERENCES seance(id_seance) ON DELETE CASCADE,
    FOREIGN KEY (id_participation) REFERENCES participation(id_participation) ON DELETE CASCADE
);

CREATE TABLE credit (
    id_credit SERIAL PRIMARY KEY,
    id_membre INTEGER NOT NULL,
    id_tontine INTEGER NOT NULL,
    montant_emprunte DECIMAL(10,2) NOT NULL CHECK (montant_emprunte > 0),
    taux_interet DECIMAL(5,2) DEFAULT 0 CHECK (taux_interet >= 0),
    date_emprunt DATE NOT NULL,
    date_echeance DATE NOT NULL,
    montant_rembourse DECIMAL(10,2) DEFAULT 0 CHECK (montant_rembourse >= 0),
    statut VARCHAR(20) DEFAULT 'en_cours' CHECK (statut IN ('en_cours', 'rembourse', 'en_retard')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_membre) REFERENCES membre(id_membre) ON DELETE CASCADE,
    FOREIGN KEY (id_tontine) REFERENCES tontine(id_tontine) ON DELETE CASCADE,
    CHECK (date_echeance > date_emprunt)
);

CREATE TABLE remboursementcredit (
    id_remboursement SERIAL PRIMARY KEY,
    id_credit INTEGER NOT NULL,
    montant DECIMAL(10,2) NOT NULL CHECK (montant > 0),
    date_remboursement DATE NOT NULL,
    mode_paiement VARCHAR(20) DEFAULT 'especes' CHECK (mode_paiement IN ('especes', 'virement', 'cheque', 'mobile_money')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_credit) REFERENCES credit(id_credit) ON DELETE CASCADE
);


CREATE TABLE penalite (
    id_penalite SERIAL PRIMARY KEY,
    id_membre INTEGER NOT NULL,
    id_seance INTEGER,
    motif VARCHAR(200) NOT NULL,
    montant DECIMAL(10,2) NOT NULL CHECK (montant > 0),
    date_penalite DATE NOT NULL,
    payee BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_membre) REFERENCES membre(id_membre) ON DELETE CASCADE,
    FOREIGN KEY (id_seance) REFERENCES seance(id_seance) ON DELETE SET NULL
);


CREATE TABLE projetfiac (
    id_projet SERIAL PRIMARY KEY,
    id_tontine INTEGER NOT NULL,
    nom_projet VARCHAR(200) NOT NULL,
    description TEXT,
    montant_objectif DECIMAL(10,2) NOT NULL CHECK (montant_objectif > 0),
    montant_collecte DECIMAL(10,2) DEFAULT 0 CHECK (montant_collecte >= 0 AND montant_collecte <= montant_objectif),
    date_debut DATE NOT NULL,
    date_fin_prevue DATE,
    statut VARCHAR(20) DEFAULT 'planifie' CHECK (statut IN ('planifie', 'en_cours', 'termine', 'annule')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_tontine) REFERENCES tontine(id_tontine) ON DELETE CASCADE,
    CHECK (date_fin_prevue IS NULL OR date_fin_prevue > date_debut)
);


CREATE TABLE contributionfiac (
    id_contribution SERIAL PRIMARY KEY,
    id_projet INTEGER NOT NULL,
    id_membre INTEGER NOT NULL,
    montant DECIMAL(10,2) NOT NULL CHECK (montant > 0),
    date_contribution DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_projet) REFERENCES projetfiac(id_projet) ON DELETE CASCADE,
    FOREIGN KEY (id_membre) REFERENCES membre(id_membre) ON DELETE CASCADE
);


CREATE INDEX idx_membre_statut ON membre(statut);
CREATE INDEX idx_participation_membre ON participation(id_membre);
CREATE INDEX idx_participation_tontine ON participation(id_tontine);
CREATE INDEX idx_cotisation_statut ON cotisation(created_at);
CREATE INDEX idx_credit_statut ON credit(statut);
CREATE INDEX idx_seance_date ON seance(date_seance);
CREATE INDEX idx_tontine_statut ON tontine(statut);
CREATE INDEX idx_beneficiaire_seance ON beneficiaire(id_seance);


INSERT INTO typetontine (nom, description, est_obligatoire, montant_cotisation, frequence) VALUES
('Tontine de Présence', 'Tontine obligatoire pour tous les membres', TRUE, 5000.00, 'mensuel'),
('Tontine Épargne', 'Tontine optionnelle à parts multiples', FALSE, 10000.00, 'mensuel'),
('Tontine Solidarité', 'Tontine optionnelle pour projets communautaires', FALSE, 7500.00, 'mensuel');

INSERT INTO membre (nom, prenom, telephone, email, adresse, date_adhesion) VALUES
('Kamga', 'Marie', '237612345678', 'marie.kamga@email.com', 'Yaoundé, Bastos', '2024-01-15'),
('Tchamba', 'Paul', '237698765432', 'paul.tchamba@email.com', 'Yaoundé, Mokolo', '2024-01-20'),
('Fotso', 'Claire', '237655555555', 'claire.fotso@email.com', 'Yaoundé, Briqueterie', '2024-02-01'),
('Mballa', 'Jean', '237677777777', 'jean.mballa@email.com', 'Yaoundé, Efoulan', '2024-02-10'),
('Ngono', 'Alice', '237699999999', 'alice.ngono@email.com', 'Yaoundé, Tsinga', '2024-02-15');

INSERT INTO tontine (id_type, nom, date_debut, nombre_tours) VALUES
(1, 'Tontine Présence 2024', '2024-01-01', 12),
(2, 'Tontine Épargne 2024', '2024-01-01', 12);

INSERT INTO participation (id_membre, id_tontine, nombre_parts, date_participation) VALUES
(1, 1, 1, '2024-01-01'),
(2, 1, 1, '2024-01-01'),
(3, 1, 1, '2024-01-01'),
(4, 1, 1, '2024-01-01'),
(5, 1, 1, '2024-01-01'),
(1, 2, 2, '2024-01-01'),
(2, 2, 1, '2024-01-01'),
(3, 2, 3, '2024-01-01'); 

INSERT INTO seance (id_tontine, numero_tour, date_seance, lieu) VALUES
(1, 1, '2024-01-15', 'Domicile de Marie'),
(2, 1, '2024-01-15', 'Domicile de Marie');

-- Script de correction complète pour la base de données PostgreSQL
-- Projet INF2212 - Université de Yaoundé I

-- Supprimer et recréer la table Membre avec la bonne structure
DROP TABLE IF EXISTS Membre CASCADE;

CREATE TABLE Membre (
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

-- Insérer les données de test
INSERT INTO Membre (nom, prenom, telephone, email, adresse, date_adhesion, statut) VALUES
('Dupont', 'Jean', '690123456', 'jean.dupont@email.com', 'Yaoundé, Bastos', '2024-01-15', 'actif'),
('Martin', 'Marie', '691234567', 'marie.martin@email.com', 'Yaoundé, Mokolo', '2024-01-20', 'actif'),
('Tchamba', 'Pierre', '692345678', 'pierre.tchamba@email.com', 'Yaoundé, Briqueterie', '2024-02-01', 'actif'),
('Ngo', 'Sophie', '693456789', 'sophie.ngo@email.com', 'Yaoundé, Efoulan', '2024-02-10', 'actif'),
('Kamga', 'Paul', '694567890', 'paul.kamga@email.com', 'Yaoundé, Mendong', '2024-02-15', 'actif');

-- Vérifier/créer les autres tables
DROP TABLE IF EXISTS Participation CASCADE;
DROP TABLE IF EXISTS Cotisation CASCADE;
DROP TABLE IF EXISTS Seance CASCADE;
DROP TABLE IF EXISTS Credit CASCADE;
DROP TABLE IF EXISTS Tontine CASCADE;
DROP TABLE IF EXISTS TypeTontine CASCADE;

-- Recréer toutes les tables dans le bon ordre
CREATE TABLE TypeTontine (
    id_type SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT,
    est_obligatoire BOOLEAN DEFAULT FALSE,
    montant_cotisation DECIMAL(10,2) NOT NULL CHECK (montant_cotisation > 0),
    frequence VARCHAR(20) DEFAULT 'mensuel' CHECK (frequence IN ('hebdomadaire', 'mensuel', 'bimensuel')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Tontine (
    id_tontine SERIAL PRIMARY KEY,
    id_type INTEGER NOT NULL REFERENCES TypeTontine(id_type),
    nom VARCHAR(100) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE,
    nombre_tours INTEGER NOT NULL CHECK (nombre_tours >= 1),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Seance (
    id_seance SERIAL PRIMARY KEY,
    id_tontine INTEGER NOT NULL REFERENCES Tontine(id_tontine),
    date_seance DATE NOT NULL,
    montant_total DECIMAL(10,2) DEFAULT 0,
    beneficiaire_id INTEGER REFERENCES Membre(id_membre),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Cotisation (
    id_cotisation SERIAL PRIMARY KEY,
    id_seance INTEGER NOT NULL REFERENCES Seance(id_seance),
    id_membre INTEGER NOT NULL REFERENCES Membre(id_membre),
    montant DECIMAL(10,2) NOT NULL,
    date_paiement DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Credit (
    id_credit SERIAL PRIMARY KEY,
    id_membre INTEGER NOT NULL REFERENCES Membre(id_membre),
    id_tontine INTEGER NOT NULL REFERENCES Tontine(id_tontine),
    montant_emprunte DECIMAL(10,2) NOT NULL,
    taux_interet DECIMAL(5,2) NOT NULL,
    date_emprunt DATE NOT NULL,
    date_echeance DATE NOT NULL,
    montant_rembourse DECIMAL(10,2) DEFAULT 0,
    statut VARCHAR(20) DEFAULT 'en_cours' CHECK (statut IN ('en_cours', 'rembourse', 'en_retard')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Participation (
    id_participation SERIAL PRIMARY KEY,
    id_membre INTEGER NOT NULL REFERENCES Membre(id_membre),
    id_tontine INTEGER NOT NULL REFERENCES Tontine(id_tontine),
    nombre_parts INTEGER NOT NULL DEFAULT 1,
    date_participation DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insérer des données de test pour les autres tables
INSERT INTO TypeTontine (nom, description, est_obligatoire, montant_cotisation, frequence) VALUES
('Tontine Présence', 'Tontine obligatoire de présence', TRUE, 5000.00, 'mensuel'),
('Tontine Sociale', 'Tontine optionnelle sociale', FALSE, 10000.00, 'mensuel'),
('Tontine Projet', 'Tontine pour les projets collectifs', FALSE, 15000.00, 'mensuel');

INSERT INTO Tontine (id_type, nom, date_debut, date_fin, nombre_tours) VALUES
(1, 'Tontine Mensuelle 2024', '2024-01-01', '2024-12-31', 12),
(2, 'Tontine Sociale 2024', '2024-01-01', '2024-12-31', 12);

-- Afficher un résumé
DO $$
BEGIN
    RAISE NOTICE 'Base de données corrigée avec succès!';
    RAISE NOTICE 'Tables créées: Membre, TypeTontine, Tontine, Seance, Cotisation, Credit, Participation';
    RAISE NOTICE 'Nombre de membres: %', (SELECT COUNT(*) FROM Membre);
    RAISE NOTICE 'Nombre de types de tontine: %', (SELECT COUNT(*) FROM TypeTontine);
    RAISE NOTICE 'Nombre de tontines: %', (SELECT COUNT(*) FROM Tontine);
END $$;

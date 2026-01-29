# Dictionnaire des Données - Application de Gestion de Tontine

## 1. Introduction

Ce dictionnaire des données décrit l'ensemble des entités, attributs et relations du système de gestion de tontine. Il sert de référence pour la conception, le développement et la maintenance de l'application.

## 2. Entités Principales

### 2.1 Membre (membre)

**Description**: Représente un adhérent à une ou plusieurs tontines.

| Attribut | Type | Taille | Obligatoire | Valeur par défaut | Contrainte | Description |
|----------|------|--------|-------------|-------------------|------------|-------------|
| id_membre | INTEGER | - | Oui | - | Clé primaire | Identifiant unique du membre |
| nom | VARCHAR | 100 | Oui | - | - | Nom de famille du membre |
| prenom | VARCHAR | 100 | Oui | - | - | Prénom du membre |
| telephone | VARCHAR | 20 | Oui | - | Unique | Numéro de téléphone unique |
| email | VARCHAR | 100 | Non | NULL | - | Adresse email |
| adresse | TEXT | - | Non | NULL | - | Adresse physique |
| date_adhesion | DATE | - | Oui | CURRENT_DATE | - | Date d'adhésion |
| statut | VARCHAR | 20 | Non | 'actif' | CHECK('actif','inactif','suspendu') | Statut du membre |
| created_at | TIMESTAMP | - | Non | CURRENT_TIMESTAMP | - | Date de création |
| updated_at | TIMESTAMP | - | Non | CURRENT_TIMESTAMP | - | Date de mise à jour |

**Relations**:
- 1,N → participation (id_membre)
- 1,N → credit (id_membre)
- 1,N → cotisation (id_membre)
- 1,N → beneficiaire (id_membre)
- 1,N → penalite (id_membre)

---

### 2.2 Type Tontine (typetontine)

**Description**: Définit les types et configurations de tontines disponibles.

| Attribut | Type | Taille | Obligatoire | Valeur par défaut | Contrainte | Description |
|----------|------|--------|-------------|-------------------|------------|-------------|
| id_type | INTEGER | - | Oui | - | Clé primaire | Identifiant unique du type |
| nom | VARCHAR | 100 | Oui | - | - | Nom du type de tontine |
| description | TEXT | - | Non | NULL | - | Description détaillée |
| est_obligatoire | BOOLEAN | - | Non | FALSE | - | Type obligatoire ou optionnel |
| montant_cotisation | DECIMAL | 10,2 | Oui | - | CHECK(>0) | Montant de cotisation |
| frequence | VARCHAR | 20 | Non | 'mensuel' | CHECK('hebdomadaire','mensuel','bimensuel') | Fréquence des cotisations |
| created_at | TIMESTAMP | - | Non | CURRENT_TIMESTAMP | - | Date de création |

**Relations**:
- 1,N → tontine (id_type)

---

### 2.3 Tontine (tontine)

**Description**: Instance spécifique d'une tontine avec ses paramètres.

| Attribut | Type | Taille | Obligatoire | Valeur par défaut | Contrainte | Description |
|----------|------|--------|-------------|-------------------|------------|-------------|
| id_tontine | INTEGER | - | Oui | - | Clé primaire | Identifiant unique de la tontine |
| id_type | INTEGER | - | Oui | - | Clé étrangère | Type de tontine |
| nom | VARCHAR | 100 | Oui | - | - | Nom de la tontine |
| type | VARCHAR | 20 | Oui | - | CHECK('PRESENCE','OPTIONNELLE') | Type normalisé |
| montant_part | DECIMAL | 10,2 | Oui | - | CHECK(>0) | Montant par part |
| frequence | VARCHAR | 20 | Oui | - | CHECK('MENSUELLE','HEBDOMADAIRE','QUOTIDIENNE') | Fréquence des séances |
| date_debut | DATE | - | Oui | - | - | Date de début |
| date_fin | DATE | - | Non | NULL | - | Date de fin prévue |
| date_creation | DATE | - | Non | CURRENT_DATE | - | Date de création |
| nombre_tours | INTEGER | - | Oui | - | CHECK(>=1) | Nombre total de tours |
| tour_actuel | INTEGER | - | Non | 1 | CHECK(<=nombre_tours) | Tour en cours |
| statut | VARCHAR | 20 | Non | 'active' | CHECK('active','terminee','suspendue') | Statut actuel |
| created_at | TIMESTAMP | - | Non | CURRENT_TIMESTAMP | - | Date de création |

**Relations**:
- N,1 → typetontine (id_type)
- 1,N → participation (id_tontine)
- 1,N → seance (id_tontine)
- 1,N → credit (id_tontine)
- 1,N → projetfiac (id_tontine)

---

### 2.4 Participation (participation)

**Description**: Enregistrement d'adhésion d'un membre à une tontine.

| Attribut | Type | Taille | Obligatoire | Valeur par défaut | Contrainte | Description |
|----------|------|--------|-------------|-------------------|------------|-------------|
| id_participation | INTEGER | - | Oui | - | Clé primaire | Identifiant unique |
| id_membre | INTEGER | - | Oui | - | Clé étrangère | Membre participant |
| id_tontine | INTEGER | - | Oui | - | Clé étrangère | Tontine concernée |
| nombre_parts | INTEGER | - | Non | 1 | CHECK(>=1) | Nombre de parts souscrites |
| date_participation | DATE | - | Oui | CURRENT_DATE | - | Date de participation |
| statut | VARCHAR | 20 | Non | 'active' | CHECK('active','retiree') | Statut de la participation |
| created_at | TIMESTAMP | - | Non | CURRENT_TIMESTAMP | - | Date de création |

**Relations**:
- N,1 → membre (id_membre)
- N,1 → tontine (id_tontine)
- 1,N → beneficiaire (id_participation)

**Contrainte**: UNIQUE(id_membre, id_tontine)

---

### 2.5 Séance (seance)

**Description**: Réunion planifiée pour une tontine spécifique.

| Attribut | Type | Taille | Obligatoire | Valeur par défaut | Contrainte | Description |
|----------|------|--------|-------------|-------------------|------------|-------------|
| id_seance | INTEGER | - | Oui | - | Clé primaire | Identifiant unique |
| id_tontine | INTEGER | - | Oui | - | Clé étrangère | Tontine concernée |
| numero_tour | INTEGER | - | Oui | - | - | Numéro du tour |
| date_seance | DATE | - | Oui | - | - | Date de la séance |
| lieu | VARCHAR | 200 | Non | NULL | - | Lieu de la séance |
| statut | VARCHAR | 20 | Non | 'planifiee' | CHECK('planifiee','en_cours','terminee') | Statut de la séance |
| observations | TEXT | - | Non | NULL | - | Observations |
| created_at | TIMESTAMP | - | Non | CURRENT_TIMESTAMP | - | Date de création |

**Relations**:
- N,1 → tontine (id_tontine)
- 1,N → cotisation (id_seance)
- 1,N → beneficiaire (id_seance)
- 1,N → penalite (id_seance)

**Contrainte**: UNIQUE(id_tontine, numero_tour)

---

### 2.6 Cotisation (cotisation)

**Description**: Paiement effectué par un membre lors d'une séance.

| Attribut | Type | Taille | Obligatoire | Valeur par défaut | Contrainte | Description |
|----------|------|--------|-------------|-------------------|------------|-------------|
| id_cotisation | INTEGER | - | Oui | - | Clé primaire | Identifiant unique |
| id_seance | INTEGER | - | Oui | - | Clé étrangère | Séance concernée |
| id_membre | INTEGER | - | Oui | - | Clé étrangère | Membre payeur |
| montant | DECIMAL | 10,2 | Oui | - | CHECK(>0) | Montant payé |
| date_paiement | DATE | - | Oui | - | - | Date du paiement |
| created_at | TIMESTAMP | - | Non | CURRENT_TIMESTAMP | - | Date de création |

**Relations**:
- N,1 → seance (id_seance)
- N,1 → membre (id_membre)

---

### 2.7 Crédit (credit)

**Description**: Prêt accordé à un membre dans le cadre d'une tontine.

| Attribut | Type | Taille | Obligatoire | Valeur par défaut | Contrainte | Description |
|----------|------|--------|-------------|-------------------|------------|-------------|
| id_credit | INTEGER | - | Oui | - | Clé primaire | Identifiant unique |
| id_membre | INTEGER | - | Oui | - | Clé étrangère | Membre emprunteur |
| id_tontine | INTEGER | - | Oui | - | Clé étrangère | Tontine source |
| montant_emprunte | DECIMAL | 10,2 | Oui | - | CHECK(>0) | Montant emprunté |
| taux_interet | DECIMAL | 5,2 | Non | 0 | CHECK(>=0) | Taux d'intérêt |
| date_emprunt | DATE | - | Oui | - | - | Date d'emprunt |
| date_echeance | DATE | - | Oui | - | - | Date d'échéance |
| montant_rembourse | DECIMAL | 10,2 | Non | 0 | CHECK(>=0) | Montant remboursé |
| statut | VARCHAR | 20 | Non | 'en_cours' | CHECK('en_cours','rembourse','en_retard') | Statut du crédit |
| created_at | TIMESTAMP | - | Non | CURRENT_TIMESTAMP | - | Date de création |

**Relations**:
- N,1 → membre (id_membre)
- N,1 → tontine (id_tontine)
- 1,N → remboursementcredit (id_credit)

---

### 2.8 Bénéficiaire (beneficiaire)

**Description**: Membre désigné pour recevoir les gains d'une séance.

| Attribut | Type | Taille | Obligatoire | Valeur par défaut | Contrainte | Description |
|----------|------|--------|-------------|-------------------|------------|-------------|
| id_beneficiaire | INTEGER | - | Oui | - | Clé primaire | Identifiant unique |
| id_seance | INTEGER | - | Oui | - | Clé étrangère | Séance concernée |
| id_participation | INTEGER | - | Oui | - | Clé étrangère | Participation du bénéficiaire |
| montant_gain | DECIMAL | 10,2 | Oui | - | CHECK(>0) | Montant des gains |
| date_attribution | DATE | - | Oui | - | - | Date d'attribution |
| mode_paiement | VARCHAR | 20 | Non | 'especes' | CHECK('especes','virement','cheque','mobile_money') | Mode de paiement |
| statut | VARCHAR | 20 | Non | 'attribue' | CHECK('attribue','paye') | Statut du paiement |
| created_at | TIMESTAMP | - | Non | CURRENT_TIMESTAMP | - | Date de création |

**Relations**:
- N,1 → seance (id_seance)
- N,1 → participation (id_participation)

---

### 2.9 Remboursement Crédit (remboursementcredit)

**Description**: Paiement effectué pour rembourser un crédit.

| Attribut | Type | Taille | Obligatoire | Valeur par défaut | Contrainte | Description |
|----------|------|--------|-------------|-------------------|------------|-------------|
| id_remboursement | INTEGER | - | Oui | - | Clé primaire | Identifiant unique |
| id_credit | INTEGER | - | Oui | - | Clé étrangère | Crédit concerné |
| montant | DECIMAL | 10,2 | Oui | - | CHECK(>0) | Montant remboursé |
| date_remboursement | DATE | - | Oui | - | - | Date du remboursement |
| mode_paiement | VARCHAR | 20 | Non | 'especes' | CHECK('especes','virement','cheque','mobile_money') | Mode de paiement |
| created_at | TIMESTAMP | - | Non | CURRENT_TIMESTAMP | - | Date de création |

**Relations**:
- N,1 → credit (id_credit)

---

### 2.10 Pénalité (penalite)

**Description**: Sanction appliquée à un membre.

| Attribut | Type | Taille | Obligatoire | Valeur par défaut | Contrainte | Description |
|----------|------|--------|-------------|-------------------|------------|-------------|
| id_penalite | INTEGER | - | Oui | - | Clé primaire | Identifiant unique |
| id_membre | INTEGER | - | Oui | - | Clé étrangère | Membre sanctionné |
| id_seance | INTEGER | - | Non | NULL | Clé étrangère | Séance concernée |
| motif | VARCHAR | 200 | Oui | - | - | Motif de la pénalité |
| montant | DECIMAL | 10,2 | Oui | - | CHECK(>0) | Montant de la pénalité |
| date_penalite | DATE | - | Oui | - | - | Date de la pénalité |
| payee | BOOLEAN | - | Non | FALSE | - | Pénalité payée |
| created_at | TIMESTAMP | - | Non | CURRENT_TIMESTAMP | - | Date de création |

**Relations**:
- N,1 → membre (id_membre)
- N,1 → seance (id_seance)

---

### 2.11 Projet FIAC (projetfiac)

**Description**: Projet communautaire financé par la tontine.

| Attribut | Type | Taille | Obligatoire | Valeur par défaut | Contrainte | Description |
|----------|------|--------|-------------|-------------------|------------|-------------|
| id_projet | INTEGER | - | Oui | - | Clé primaire | Identifiant unique |
| id_tontine | INTEGER | - | Oui | - | Clé étrangère | Tontine financière |
| nom_projet | VARCHAR | 200 | Oui | - | - | Nom du projet |
| description | TEXT | - | Non | NULL | - | Description du projet |
| montant_objectif | DECIMAL | 10,2 | Oui | - | CHECK(>0) | Objectif financier |
| montant_collecte | DECIMAL | 10,2 | Non | 0 | CHECK(>=0,<=montant_objectif) | Montant collecté |
| date_debut | DATE | - | Oui | - | - | Date de début |
| date_fin_prevue | DATE | - | Non | NULL | - | Date de fin prévue |
| statut | VARCHAR | 20 | Non | 'planifie' | CHECK('planifie','en_cours','termine','annule') | Statut du projet |
| created_at | TIMESTAMP | - | Non | CURRENT_TIMESTAMP | - | Date de création |

**Relations**:
- N,1 → tontine (id_tontine)
- 1,N → contributionfiac (id_projet)

---

### 2.12 Contribution FIAC (contributionfiac)

**Description**: Contribution financière à un projet FIAC.

| Attribut | Type | Taille | Obligatoire | Valeur par défaut | Contrainte | Description |
|----------|------|--------|-------------|-------------------|------------|-------------|
| id_contribution | INTEGER | - | Oui | - | Clé primaire | Identifiant unique |
| id_projet | INTEGER | - | Oui | - | Clé étrangère | Projet concerné |
| id_membre | INTEGER | - | Oui | - | Clé étrangère | Membre contributeur |
| montant | DECIMAL | 10,2 | Oui | - | CHECK(>0) | Montant contribué |
| date_contribution | DATE | - | Oui | - | - | Date de contribution |
| created_at | TIMESTAMP | - | Non | CURRENT_TIMESTAMP | - | Date de création |

**Relations**:
- N,1 → projetfiac (id_projet)
- N,1 → membre (id_membre)

---

## 3. Relations et Contraintes

### 3.1 Relations Principales

| Relation | Type | Cardinalité | Description |
|----------|------|-------------|-------------|
| typetontine → tontine | 1,N | Un type peut avoir plusieurs tontines |
| membre → participation | 1,N | Un membre peut participer à plusieurs tontines |
| tontine → participation | 1,N | Une tontine peut avoir plusieurs participants |
| tontine → seance | 1,N | Une tontine peut avoir plusieurs séances |
| seance → cotisation | 1,N | Une séance peut avoir plusieurs cotisations |
| membre → credit | 1,N | Un membre peut avoir plusieurs crédits |
| tontine → credit | 1,N | Une tontine peut financer plusieurs crédits |
| participation → beneficiaire | 1,N | Une participation peut générer plusieurs bénéficiaires |
| seance → beneficiaire | 1,N | Une séance peut avoir plusieurs bénéficiaires |

### 3.2 Contraintes d'Intégrité

#### Contraintes CHECK
- `membre.statut` ∈ {'actif', 'inactif', 'suspendu'}
- `typetontine.frequence` ∈ {'hebdomadaire', 'mensuel', 'bimensuel'}
- `tontine.statut` ∈ {'active', 'terminee', 'suspendue'}
- `tontine.type` ∈ {'PRESENCE', 'OPTIONNELLE'}
- `tontine.frequence` ∈ {'MENSUELLE', 'HEBDOMADAIRE', 'QUOTIDIENNE'}
- `participation.statut` ∈ {'active', 'retiree'}
- `seance.statut` ∈ {'planifiee', 'en_cours', 'terminee'}
- `credit.statut` ∈ {'en_cours', 'rembourse', 'en_retard'}
- `beneficiaire.statut` ∈ {'attribue', 'paye'}
- `projetfiac.statut` ∈ {'planifie', 'en_cours', 'termine', 'annule'}

#### Contraintes UNIQUE
- `membre.telephone` : Un numéro de téléphone par membre
- `participation(id_membre, id_tontine)` : Une participation par membre/tontine
- `seance(id_tontine, numero_tour)` : Un tour par tontine

#### Contraintes de Domaine
- Montants > 0 pour tous les champs financiers
- Dates cohérentes (date_fin > date_debut)
- Tours valides (tour_actuel <= nombre_tours)

---

## 4. Index de Performance

| Index | Table | Colonnes | Utilité |
|-------|-------|----------|---------|
| idx_membre_statut | membre | statut | Filtrage rapide par statut |
| idx_participation_membre | participation | id_membre | Recherche des participations d'un membre |
| idx_participation_tontine | participation | id_tontine | Recherche des participants d'une tontine |
| idx_cotisation_statut | cotisation | created_at | Filtrage chronologique |
| idx_credit_statut | credit | statut | Filtrage par statut de crédit |
| idx_seance_date | seance | date_seance | Recherche chronologique |
| idx_tontine_statut | tontine | statut | Filtrage par statut |
| idx_beneficiaire_seance | beneficiaire | id_seance | Recherche des bénéficiaires d'une séance |

---

## 5. Types de Données

### 5.1 Types Numériques
- **INTEGER**: Identifiants et compteurs
- **DECIMAL(10,2)**: Montants financiers
- **DECIMAL(5,2)**: Taux et pourcentages
- **BOOLEAN**: États binaires

### 5.2 Types Chaînes
- **VARCHAR(n)**: Textes de longueur limitée
- **TEXT**: Textes de longueur variable

### 5.3 Types Temporels
- **DATE**: Dates sans heure
- **TIMESTAMP**: Dates avec heure

---

## 6. Règles Métier

### 6.1 Gestion des Tontines
- Une tontine doit avoir au moins 1 tour
- Le tour actuel ne peut pas dépasser le nombre total de tours
- Une tontine ne peut pas être terminée avec des crédits en cours

### 6.2 Gestion des Participations
- Un membre ne peut participer qu'une fois à une tontine
- Le nombre de parts doit être positif
- Une participation ne peut être retirée si des crédits sont associés

### 6.3 Gestion des Crédits
- Le montant remboursé ne peut pas dépasser le montant dû
- La date d'échéance doit être postérieure à la date d'emprunt
- Un crédit ne peut être supprimé s'il a des remboursements

### 6.4 Gestion des Séances
- Le numéro de tour doit être unique pour une tontine
- Une séance ne peut être terminée si des cotisations sont en attente
- Le lieu est obligatoire pour les séances planifiées

---

**Version**: 3.0  
**Date**: 27 Janvier 2026  
**Auteur**: Équipe INF2212 - Université de Yaoundé I

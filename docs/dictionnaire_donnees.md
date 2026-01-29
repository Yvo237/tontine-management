# Dictionnaire de Données
## Application de Gestion de Tontine - INF2212

---

## Table: Membre

Stocke les informations des membres de la tontine.

| Champ | Type | Taille | Contrainte | Description |
|-------|------|--------|------------|-------------|
| **id_membre** | INT | - | PK, AUTO_INCREMENT | Identifiant unique du membre |
| nom | VARCHAR | 100 | NOT NULL | Nom de famille du membre |
| prenom | VARCHAR | 100 | NOT NULL | Prénom du membre |
| telephone | VARCHAR | 20 | UNIQUE | Numéro de téléphone (unique) |
| email | VARCHAR | 100 | - | Adresse email |
| adresse | TEXT | - | - | Adresse physique |
| date_adhesion | DATE | - | NOT NULL | Date d'adhésion à la tontine |
| statut | ENUM | - | DEFAULT 'actif' | Statut: actif, inactif, suspendu |
| created_at | TIMESTAMP | - | DEFAULT CURRENT_TIMESTAMP | Date de création |
| updated_at | TIMESTAMP | - | ON UPDATE | Date de modification |

**Règles métier:**
- Le téléphone doit être unique
- Un membre doit avoir au moins nom, prénom et téléphone
- Le statut par défaut est 'actif'

---

## Table: TypeTontine

Définit les différents types de tontine (présence, optionnelle, etc.).

| Champ | Type | Taille | Contrainte | Description |
|-------|------|--------|------------|-------------|
| **id_type** | INT | - | PK, AUTO_INCREMENT | Identifiant unique du type |
| nom | VARCHAR | 100 | NOT NULL | Nom du type de tontine |
| description | TEXT | - | - | Description détaillée |
| est_obligatoire | BOOLEAN | - | DEFAULT FALSE | Indique si obligatoire |
| montant_cotisation | DECIMAL | 10,2 | NOT NULL | Montant de la cotisation |
| frequence | ENUM | - | DEFAULT 'mensuel' | hebdomadaire, mensuel, bimensuel |

**Règles métier:**
- Une tontine de présence est obligatoire (est_obligatoire = TRUE)
- Le montant doit être positif
- La fréquence détermine le rythme des séances

---

## Table: Tontine

Représente une instance de tontine basée sur un type.

| Champ | Type | Taille | Contrainte | Description |
|-------|------|--------|------------|-------------|
| **id_tontine** | INT | - | PK, AUTO_INCREMENT | Identifiant unique |
| id_type | INT | - | FK → TypeTontine | Type de la tontine |
| nom | VARCHAR | 100 | NOT NULL | Nom de la tontine |
| date_debut | DATE | - | NOT NULL | Date de début |
| date_fin | DATE | - | - | Date de fin (nullable) |
| nombre_tours | INT | - | NOT NULL | Nombre total de tours |
| tour_actuel | INT | - | DEFAULT 1 | Tour en cours |
| statut | ENUM | - | DEFAULT 'active' | active, terminee, suspendue |

**Règles métier:**
- Une tontine doit avoir au moins 1 tour
- tour_actuel ≤ nombre_tours
- Si date_fin existe, elle doit être > date_debut

---

## Table: Participation

Liaison entre membres et tontines (inscription).

| Champ | Type | Taille | Contrainte | Description |
|-------|------|--------|------------|-------------|
| **id_participation** | INT | - | PK, AUTO_INCREMENT | Identifiant unique |
| id_membre | INT | - | FK → Membre | Membre participant |
| id_tontine | INT | - | FK → Tontine | Tontine concernée |
| nombre_parts | INT | - | DEFAULT 1 | Nombre de parts souscrites |
| date_inscription | DATE | - | NOT NULL | Date d'inscription |
| statut | ENUM | - | DEFAULT 'active' | active, retiree |

**Contraintes:**
- UNIQUE(id_membre, id_tontine) - Un membre ne peut participer qu'une fois à une tontine
- nombre_parts ≥ 1

**Règles métier:**
- Pour les tontines obligatoires: nombre_parts = 1
- Pour les tontines optionnelles: nombre_parts ≥ 1
- Un membre peut être bénéficiaire autant de fois qu'il a de parts

---

## Table: Seance

Représente une séance de la tontine.

| Champ | Type | Taille | Contrainte | Description |
|-------|------|--------|------------|-------------|
| **id_seance** | INT | - | PK, AUTO_INCREMENT | Identifiant unique |
| id_tontine | INT | - | FK → Tontine | Tontine concernée |
| numero_tour | INT | - | NOT NULL | Numéro du tour |
| date_seance | DATE | - | NOT NULL | Date de la séance |
| lieu | VARCHAR | 200 | - | Lieu de la séance |
| statut | ENUM | - | DEFAULT 'planifiee' | planifiee, en_cours, terminee |
| observations | TEXT | - | - | Notes et observations |

**Contraintes:**
- UNIQUE(id_tontine, numero_tour) - Un seul tour par numéro

**Règles métier:**
- Lors de la création d'une séance, des cotisations sont automatiquement créées
- numero_tour ≤ nombre_tours de la tontine

---

## Table: Cotisation

Enregistre les cotisations des membres pour chaque séance.

| Champ | Type | Taille | Contrainte | Description |
|-------|------|--------|------------|-------------|
| **id_cotisation** | INT | - | PK, AUTO_INCREMENT | Identifiant unique |
| id_seance | INT | - | FK → Seance | Séance concernée |
| id_participation | INT | - | FK → Participation | Participation concernée |
| montant_du | DECIMAL | 10,2 | NOT NULL | Montant dû |
| montant_verse | DECIMAL | 10,2 | DEFAULT 0 | Montant versé |
| date_versement | DATETIME | - | - | Date du versement |
| statut | ENUM | - | DEFAULT 'en_attente' | en_attente, partiel, paye, impaye |

**Règles métier:**
- montant_du = nombre_parts × montant_cotisation du type
- Si montant_verse = montant_du → statut = 'paye'
- Si 0 < montant_verse < montant_du → statut = 'partiel'
- Si montant_verse = 0 et séance passée → statut = 'impaye'

---

## Table: Beneficiaire

Enregistre les bénéficiaires du gain à chaque tour.

| Champ | Type | Taille | Contrainte | Description |
|-------|------|--------|------------|-------------|
| **id_beneficiaire** | INT | - | PK, AUTO_INCREMENT | Identifiant unique |
| id_seance | INT | - | FK → Seance | Séance du gain |
| id_participation | INT | - | FK → Participation | Participation bénéficiaire |
| montant_gain | DECIMAL | 10,2 | NOT NULL | Montant du gain |
| date_attribution | DATE | - | NOT NULL | Date d'attribution |
| mode_paiement | ENUM | - | DEFAULT 'especes' | especes, virement, cheque, mobile_money |
| statut | ENUM | - | DEFAULT 'attribue' | attribue, paye |

**Règles métier (CONTRAINTE MAJEURE):**
- **TRIGGER**: Vérifie que le total perçu ≤ total à cotiser
- Pour tontines optionnelles uniquement
- Formule: Σ(montant_gain) ≤ (nombre_parts × nombre_tours × montant_cotisation)

---

## Table: Credit

Gère les crédits internes accordés aux membres.

| Champ | Type | Taille | Contrainte | Description |
|-------|------|--------|------------|-------------|
| **id_credit** | INT | - | PK, AUTO_INCREMENT | Identifiant unique |
| id_membre | INT | - | FK → Membre | Membre emprunteur |
| id_tontine | INT | - | FK → Tontine | Tontine source |
| montant_emprunte | DECIMAL | 10,2 | NOT NULL | Montant du crédit |
| taux_interet | DECIMAL | 5,2 | DEFAULT 0 | Taux d'intérêt (%) |
| date_emprunt | DATE | - | NOT NULL | Date de l'emprunt |
| date_echeance | DATE | - | NOT NULL | Date limite |
| montant_rembourse | DECIMAL | 10,2 | DEFAULT 0 | Montant déjà remboursé |
| statut | ENUM | - | DEFAULT 'en_cours' | en_cours, rembourse, en_retard |

**Règles métier:**
- montant_total = montant_emprunte × (1 + taux_interet/100)
- Si montant_rembourse ≥ montant_total → statut = 'rembourse'
- Si date_echeance < aujourd'hui ET non remboursé → statut = 'en_retard'

---

## Table: RemboursementCredit

Enregistre les remboursements partiels ou totaux.

| Champ | Type | Taille | Contrainte | Description |
|-------|------|--------|------------|-------------|
| **id_remboursement** | INT | - | PK, AUTO_INCREMENT | Identifiant unique |
| id_credit | INT | - | FK → Credit | Crédit concerné |
| montant | DECIMAL | 10,2 | NOT NULL | Montant remboursé |
| date_remboursement | DATE | - | NOT NULL | Date du remboursement |
| mode_paiement | ENUM | - | DEFAULT 'especes' | Mode de paiement |

**Règles métier:**
- **TRIGGER**: Met à jour automatiquement le montant_rembourse dans Credit
- **TRIGGER**: Change le statut en 'rembourse' si total atteint

---

## Table: Penalite

Gère les pénalités appliquées aux membres.

| Champ | Type | Taille | Contrainte | Description |
|-------|------|--------|------------|-------------|
| **id_penalite** | INT | - | PK, AUTO_INCREMENT | Identifiant unique |
| id_membre | INT | - | FK → Membre | Membre pénalisé |
| id_seance | INT | - | FK → Seance (NULL) | Séance liée (optionnel) |
| motif | VARCHAR | 200 | NOT NULL | Raison de la pénalité |
| montant | DECIMAL | 10,2 | NOT NULL | Montant de la pénalité |
| date_penalite | DATE | - | NOT NULL | Date d'application |
| payee | BOOLEAN | - | DEFAULT FALSE | Statut de paiement |

**Règles métier:**
- Motifs courants: retard, absence, non-respect règlement
- Une pénalité peut être liée ou non à une séance

---

## Table: ProjetFIAC

Projets collectifs financés par la tontine.

| Champ | Type | Taille | Contrainte | Description |
|-------|------|--------|------------|-------------|
| **id_projet** | INT | - | PK, AUTO_INCREMENT | Identifiant unique |
| id_tontine | INT | - | FK → Tontine | Tontine financeuse |
| nom_projet | VARCHAR | 200 | NOT NULL | Nom du projet |
| description | TEXT | - | - | Description détaillée |
| montant_objectif | DECIMAL | 10,2 | NOT NULL | Montant cible |
| montant_collecte | DECIMAL | 10,2 | DEFAULT 0 | Montant collecté |
| date_debut | DATE | - | NOT NULL | Date de début |
| date_fin_prevue | DATE | - | - | Date de fin prévue |
| statut | ENUM | - | DEFAULT 'planifie' | planifie, en_cours, termine, annule |

**Règles métier:**
- montant_collecte ≤ montant_objectif
- Si montant_collecte = montant_objectif → peut passer à 'termine'

---

## Table: ContributionFIAC

Contributions des membres aux projets.

| Champ | Type | Taille | Contrainte | Description |
|-------|------|--------|------------|-------------|
| **id_contribution** | INT | - | PK, AUTO_INCREMENT | Identifiant unique |
| id_projet | INT | - | FK → ProjetFIAC | Projet concerné |
| id_membre | INT | - | FK → Membre | Membre contributeur |
| montant | DECIMAL | 10,2 | NOT NULL | Montant de la contribution |
| date_contribution | DATE | - | NOT NULL | Date de contribution |

**Règles métier:**
- La somme des contributions met à jour montant_collecte du projet

---

## Relations et Cardinalités

### Relations principales:

1. **Membre → Participation** (1,N)
   - Un membre peut participer à plusieurs tontines

2. **Tontine → Participation** (1,N)
   - Une tontine a plusieurs participants

3. **Tontine → Seance** (1,N)
   - Une tontine a plusieurs séances

4. **Seance → Cotisation** (1,N)
   - Une séance génère plusieurs cotisations

5. **Participation → Cotisation** (1,N)
   - Une participation génère plusieurs cotisations

6. **Membre → Credit** (1,N)
   - Un membre peut avoir plusieurs crédits

7. **Credit → RemboursementCredit** (1,N)
   - Un crédit peut avoir plusieurs remboursements

---

## Index de Performance

```sql
-- Index sur les clés étrangères
CREATE INDEX idx_participation_membre ON Participation(id_membre);
CREATE INDEX idx_participation_tontine ON Participation(id_tontine);
CREATE INDEX idx_cotisation_statut ON Cotisation(statut);
CREATE INDEX idx_credit_statut ON Credit(statut);
CREATE INDEX idx_seance_date ON Seance(date_seance);
CREATE INDEX idx_membre_statut ON Membre(statut);
```

---

## Triggers Implémentés

### 1. verifier_contrainte_beneficiaire
- **Table**: Beneficiaire
- **Événement**: BEFORE INSERT
- **Fonction**: Vérifie la contrainte des tontines optionnelles

### 2. update_statut_credit
- **Table**: RemboursementCredit
- **Événement**: AFTER INSERT
- **Fonction**: Met à jour le statut et montant_rembourse du crédit

---

## Volumétrie Estimée

| Table | Nombre d'enregistrements (estimation) |
|-------|--------------------------------------|
| Membre | 50-100 |
| TypeTontine | 3-10 |
| Tontine | 5-20 |
| Participation | 200-500 |
| Seance | 100-500 |
| Cotisation | 1000-5000 |
| Credit | 20-100 |
| Penalite | 10-50 |
| ProjetFIAC | 1-10 |

---

## Conventions de Nommage

- **Tables**: Nom singulier, PascalCase
- **Colonnes**: snake_case
- **Clés primaires**: id_[nom_table]
- **Clés étrangères**: id_[table_référencée]
- **ENUM**: snake_case
- **Index**: idx_[table]_[colonne]
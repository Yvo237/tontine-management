# Cahier des Charges
## Application de Gestion de Tontine

**Projet**: INF2212 - Implémentation des Bases de Données  
**Institution**: Université de Yaoundé I - Faculté des Sciences  
**Département**: Informatique  
**Date**: Janvier 2026  
**Enseignant**: Etienne Kouokam

---

## 1. Contexte du Projet

### 1.1 Présentation Générale

Ce projet vise à concevoir et développer une application complète de gestion d'une tontine, s'appuyant sur une base de données relationnelle robuste. L'application doit permettre une gestion fiable, traçable et synthétique de toutes les opérations liées à une tontine.

### 1.2 Définition d'une Tontine

Une tontine est une association financière dans laquelle:
- Les membres versent des cotisations périodiques
- Un ou plusieurs membres bénéficient d'un gain lors de chaque tour
- Des crédits internes peuvent être accordés aux membres
- Des pénalités peuvent être appliquées en cas de manquement
- Des projets collectifs (FIAC) peuvent être financés

### 1.3 Objectifs du Projet

**Objectifs Pédagogiques**:
- Appliquer les concepts de modélisation de bases de données
- Maîtriser l'implémentation d'un SGBD relationnel
- Développer des compétences en conception d'interfaces utilisateur
- Intégrer contraintes d'intégrité et automatisations

**Objectifs Fonctionnels**:
- Faciliter la gestion quotidienne d'une tontine
- Assurer la traçabilité de toutes les opérations
- Automatiser les calculs et contrôles
- Fournir des rapports et statistiques pertinents

---

## 2. Périmètre Fonctionnel

### 2.1 Fonctionnalités Principales

#### Module Gestion des Membres
- **CR-001**: Créer un nouveau membre avec informations complètes
- **CR-002**: Modifier les informations d'un membre existant
- **CR-003**: Supprimer un membre (avec vérifications)
- **CR-004**: Rechercher des membres par nom, prénom ou téléphone
- **CR-005**: Gérer le statut des membres (actif, inactif, suspendu)
- **CR-006**: Visualiser l'historique d'un membre

#### Module Gestion des Tontines
- **CR-007**: Créer différents types de tontine (présence, optionnelles)
- **CR-008**: Définir les paramètres d'une tontine (montant, fréquence, durée)
- **CR-009**: Inscrire des membres à une tontine
- **CR-010**: Gérer le nombre de parts pour les tontines optionnelles
- **CR-011**: Modifier les paramètres d'une tontine active
- **CR-012**: Clôturer une tontine terminée

#### Module Gestion des Séances
- **CR-013**: Planifier une nouvelle séance
- **CR-014**: Générer automatiquement les cotisations pour tous les participants
- **CR-015**: Enregistrer les paiements des cotisations
- **CR-016**: Désigner le(s) bénéficiaire(s) du tour
- **CR-017**: Calculer et distribuer le gain
- **CR-018**: Enregistrer les observations de séance

#### Module Gestion des Crédits
- **CR-019**: Accorder un crédit à un membre
- **CR-020**: Définir le taux d'intérêt et l'échéance
- **CR-021**: Enregistrer les remboursements partiels ou totaux
- **CR-022**: Calculer automatiquement les montants dus
- **CR-023**: Gérer les crédits en retard
- **CR-024**: Consulter l'historique des remboursements

#### Module Pénalités
- **CR-025**: Appliquer une pénalité à un membre
- **CR-026**: Définir le motif et le montant
- **CR-027**: Enregistrer le paiement des pénalités
- **CR-028**: Générer un récapitulatif des pénalités

#### Module Projets FIAC
- **CR-029**: Créer un projet collectif
- **CR-030**: Définir l'objectif financier
- **CR-031**: Enregistrer les contributions des membres
- **CR-032**: Suivre l'avancement du projet
- **CR-033**: Clôturer un projet terminé

#### Module Rapports et Statistiques
- **CR-034**: Générer la situation financière par membre
- **CR-035**: Produire l'état détaillé d'une séance
- **CR-036**: Lister les crédits en cours
- **CR-037**: Créer une synthèse pour l'assemblée générale
- **CR-038**: Afficher le tableau de bord avec statistiques clés

### 2.2 Règles de Gestion Spécifiques

#### Tontines de Présence
- **RG-001**: La participation est obligatoire pour tous les membres
- **RG-002**: Chaque membre ne peut avoir qu'une seule part
- **RG-003**: La cotisation est fixe et identique pour tous
- **RG-004**: Chaque membre ne peut être bénéficiaire qu'une seule fois

#### Tontines Optionnelles
- **RG-005**: La participation est volontaire
- **RG-006**: Un membre peut souscrire plusieurs parts (1, 2, 3, ...)
- **RG-007**: Le montant de la cotisation = nombre de parts × montant unitaire
- **RG-008**: Un membre peut être bénéficiaire autant de fois qu'il a de parts
- **RG-009**: **CONTRAINTE MAJEURE**: Le montant cumulé perçu par un membre ne doit jamais excéder le montant total qu'il est censé cotiser sur l'ensemble des tours

**Formule de vérification**:
```
Total à cotiser = Nombre_Parts × Nombre_Tours × Montant_Cotisation
Total perçu = Σ(Montants_Gains_Reçus)

Contrainte: Total_Perçu ≤ Total_à_Cotiser
```

#### Cotisations
- **RG-010**: Une cotisation est créée automatiquement pour chaque participant à chaque séance
- **RG-011**: Le statut change automatiquement selon les paiements:
  - `en_attente`: Aucun paiement
  - `partiel`: Paiement incomplet
  - `paye`: Paiement complet
  - `impaye`: Séance passée sans paiement

#### Crédits
- **RG-012**: Un membre ne peut emprunter que s'il est à jour de ses cotisations
- **RG-013**: Le montant total = montant emprunté × (1 + taux/100)
- **RG-014**: Les remboursements mettent à jour automatiquement le solde
- **RG-015**: Le statut passe à "en retard" si l'échéance est dépassée
- **RG-016**: Le statut passe à "remboursé" quand le total est payé

#### Unicité et Intégrité
- **RG-017**: Un numéro de téléphone ne peut être attribué qu'à un seul membre
- **RG-018**: Un membre ne peut participer qu'une fois à une même tontine
- **RG-019**: Le numéro de tour d'une séance est unique par tontine
- **RG-020**: Une séance ne peut être supprimée si elle a des cotisations payées

---

## 3. Contraintes Techniques

### 3.1 Architecture

**Modèle**: MVC (Modèle-Vue-Contrôleur)
- **Modèle**: Classes Java représentant les entités métier
- **Vue**: Interfaces graphiques Swing
- **Contrôleur**: Classes DAO pour l'accès aux données

### 3.2 Technologies Imposées

- **Langage**: Java 8 ou supérieur
- **Base de données**: MySQL 8.0 ou supérieur
- **Interface graphique**: Java Swing
- **Driver JDBC**: MySQL Connector/J 8.0+

### 3.3 Contraintes de Performance

- **CP-001**: Temps de réponse < 2 secondes pour toute opération CRUD
- **CP-002**: Temps de génération d'un rapport < 5 secondes
- **CP-003**: Support de minimum 100 membres simultanés
- **CP-004**: Gestion d'au moins 10 tontines actives

### 3.4 Contraintes de Sécurité

- **CS-001**: Validation de toutes les saisies utilisateur
- **CS-002**: Protection contre les injections SQL (requêtes préparées)
- **CS-003**: Gestion sécurisée des mots de passe de base de données
- **CS-004**: Logs des opérations critiques

### 3.5 Contraintes d'Ergonomie

- **CE-001**: Interface intuitive et accessible aux non-informaticiens
- **CE-002**: Messages d'erreur clairs et explicites
- **CE-003**: Confirmations pour les opérations irréversibles
- **CE-004**: Navigation cohérente et fluide

---

## 4. Spécifications de la Base de Données

### 4.1 Modèle Conceptuel (MCD)

**Entités Principales**:
1. Membre
2. TypeTontine
3. Tontine
4. Participation
5. Seance
6. Cotisation
7. Beneficiaire
8. Credit
9. RemboursementCredit
10. Penalite
11. ProjetFIAC
12. ContributionFIAC

**Relations**:
- Membre (1,N) ←→ Participation (1,1)
- Tontine (1,N) ←→ Participation (1,1)
- TypeTontine (1,1) ←→ Tontine (1,N)
- Tontine (1,1) ←→ Seance (1,N)
- Seance (1,1) ←→ Cotisation (1,N)
- Participation (1,1) ←→ Cotisation (1,N)
- Membre (1,1) ←→ Credit (1,N)
- Credit (1,1) ←→ RemboursementCredit (1,N)

### 4.2 Contraintes d'Intégrité

**Contraintes Clés**:
- Toutes les tables ont une clé primaire AUTO_INCREMENT
- Les clés étrangères sont définies avec ON DELETE/UPDATE appropriés

**Contraintes Métier**:
- UNIQUE(telephone) dans Membre
- UNIQUE(id_membre, id_tontine) dans Participation
- UNIQUE(id_tontine, numero_tour) dans Seance
- CHECK montant_verse ≤ montant_du dans Cotisation

**Triggers**:
1. `verifier_contrainte_beneficiaire`: Vérifie la contrainte des tontines optionnelles
2. `update_statut_credit`: Met à jour automatiquement le statut des crédits

### 4.3 Index

```sql
CREATE INDEX idx_membre_statut ON Membre(statut);
CREATE INDEX idx_participation_membre ON Participation(id_membre);
CREATE INDEX idx_participation_tontine ON Participation(id_tontine);
CREATE INDEX idx_cotisation_statut ON Cotisation(statut);
CREATE INDEX idx_credit_statut ON Credit(statut);
CREATE INDEX idx_seance_date ON Seance(date_seance);
```

---

## 5. Spécifications de l'Interface

### 5.1 Écrans Principaux

#### Tableau de Bord (Accueil)
- Statistiques clés (cartes visuelles)
- Indicateurs de performance
- Alertes et notifications

#### Écran Gestion Membres
- Liste des membres (tableau)
- Barre de recherche
- Boutons d'action (Ajouter, Modifier, Supprimer)
- Formulaire de saisie/modification

#### Écran Gestion Tontines
- Liste des tontines
- Filtre par statut
- Bouton "Participants" pour gérer les inscriptions
- Formulaire de création/modification

#### Écran Gestion Séances
- Liste des séances avec filtre par tontine
- Bouton "Cotisations" pour gérer les paiements
- Formulaire de planification

#### Écran Gestion Crédits
- Liste des crédits avec filtre par statut
- Bouton "Remboursement"
- Formulaire d'octroi de crédit
- Affichage des calculs automatiques

#### Écran Rapports
- Grille de boutons pour différents rapports
- Fenêtres modales pour affichage
- Options d'impression/export

### 5.2 Charte Graphique

**Couleurs**:
- Primaire: Bleu (#3498db)
- Succès: Vert (#2ecc71)
- Danger: Rouge (#e74c3c)
- Avertissement: Orange (#f39c12)
- Neutre: Gris (#95a5a6)

**Typographie**:
- Titres: Arial Bold 18-24pt
- Texte: Arial Regular 12-14pt
- Tableaux: Arial 11pt

**Icônes**: Emojis Unicode pour simplicité et compatibilité

---

## 6. Spécifications des Requêtes

### 6.1 Types de Requêtes Requises

**Minimum 15 requêtes** incluant:

#### Requêtes de Sélection (5+)
1. Liste des membres actifs
2. Tontines actives avec leur type
3. Séances du mois en cours
4. Crédits en cours
5. Pénalités impayées

#### Requêtes Paramétrées (3+)
1. Recherche de membre par critère
2. Cotisations d'un membre pour une tontine
3. Crédits d'un membre spécifique

#### Requêtes de Regroupement (4+)
1. Total des cotisations par tontine
2. Statistiques par membre
3. Nombre de séances par tontine
4. Répartition des membres par statut

#### Requêtes d'Action (3+)
1. Mise à jour du statut des crédits en retard
2. Marquage des cotisations impayées
3. Avancement du tour actuel

---

## 7. Livrables Attendus

### 7.1 Documents PDF

1. **Cahier des Charges** (ce document)
2. **Modèle Conceptuel de Données (MCD)**
3. **Modèle Logique de Données (MLD)**
4. **Dictionnaire des Données**
5. **Manuel Utilisateur**

### 7.2 Fichiers Techniques

1. **Scripts SQL**:
   - `schema.sql`: Création complète de la base
   - Données de test incluses

2. **Code Source Java**:
   - Packages structurés (models, dao, ui)
   - Code commenté et documenté
   - Respect des conventions Java

3. **Fichiers de Configuration**:
   - `database.properties`
   - `pom.xml` (si Maven)

4. **JAR Exécutable**:
   - Application packagée
   - Prête à l'exécution

### 7.3 Document Confidentiel

Le Chef de groupe peut fournir un document séparé indiquant le pourcentage de participation réelle de chaque membre. Ce pourcentage sera appliqué à la note de groupe pour obtenir les notes individuelles.

---

## 8. Critères d'Évaluation

### 8.1 Modélisation (25%)
- Pertinence du MCD
- Normalisation correcte
- Respect des contraintes
- Qualité du dictionnaire

### 8.2 Implémentation (30%)
- Qualité du code
- Respect de l'architecture MVC
- Gestion des erreurs
- Performance

### 8.3 Fonctionnalités (25%)
- Complétude des modules
- Respect des règles métier
- Contrainte majeure implémentée
- Automatisations fonctionnelles

### 8.4 Interface (10%)
- Ergonomie
- Design cohérent
- Navigation intuitive
- Messages clairs

### 8.5 Documentation (10%)
- Clarté des documents
- Manuel utilisateur complet
- Code commenté
- Qualité rédactionnelle

---

## 9. Planification

### 9.1 Jalons

**Semaine 1**: Analyse et Modélisation
- Étude du sujet
- Élaboration du MCD/MLD
- Dictionnaire de données

**Semaine 2**: Implémentation Base de Données
- Création des tables
- Insertion données de test
- Tests des contraintes

**Semaine 3**: Développement Backend
- Classes modèles
- Classes DAO
- Tests unitaires

**Semaine 4**: Développement Frontend
- Interfaces principales
- Intégration avec DAO
- Tests d'intégration

**Semaine 5**: Finalisation
- Rapports et requêtes
- Documentation
- Tests finaux
- Package de livraison

### 9.2 Date Limite

**Date**: 18 janvier 2026 à 12h00  
**Email**: etienne.kouokam@facsciences-uy1.cm  
**Référence horaire**: Heure Internet

⚠️ Tout travail remis hors délai pourra être pénalisé.

---

## 10. Modalités de Réalisation

### 10.1 Organisation

- **Groupes**: Environ 15 étudiants
- **Chef de groupe**: À désigner (responsable des livrables)
- **Répartition des tâches**: À définir en interne

### 10.2 Travail Collaboratif

Recommandations:
- Utiliser un système de contrôle de version (Git)
- Documenter les décisions importantes
- Tenir des réunions régulières
- Respecter les engagements individuels

### 10.3 Démonstration

Une séance de démonstration en présentiel sera organisée ultérieurement. Chaque groupe devra:
- Présenter l'application fonctionnelle
- Expliquer les choix techniques
- Répondre aux questions
- Démontrer les fonctionnalités clés

---

## 11. Ressources et Contacts

### 11.1 Ressources Techniques

- Documentation MySQL: https://dev.mysql.com/doc/
- Java Swing Tutorial: https://docs.oracle.com/javase/tutorial/uiswing/
- JDBC Guide: https://docs.oracle.com/javase/tutorial/jdbc/

### 11.2 Contact

**Enseignant**: Etienne Kouokam  
**Email**: etienne.kouokam@facsciences-uy1.cm  
**Institution**: Université de Yaoundé I - Faculté des Sciences

---

**Date d'émission**: Janvier 2026  
**Version**: 1.0

---

## Annexe A: Glossaire

- **CRUD**: Create, Read, Update, Delete
- **DAO**: Data Access Object
- **FIAC**: Fonds d'Investissement et d'Aide Communautaire
- **JAR**: Java Archive
- **JDBC**: Java Database Connectivity
- **MCD**: Modèle Conceptuel de Données
- **MLD**: Modèle Logique de Données
- **MVC**: Modèle-Vue-Contrôleur
- **SGBD**: Système de Gestion de Base de Données

---

**Approuvé par**: [Nom de l'enseignant]  
**Date**: [Date d'approbation]
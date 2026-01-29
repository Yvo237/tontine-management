# Cahier des Charges - Application de Gestion de Tontine

## 1. Présentation du Projet

### 1.1 Contexte
Le projet de gestion de tontine est une application de bureau développée dans le cadre du cours INF2212 à l'Université de Yaoundé I. Cette application vise à digitaliser et optimiser la gestion des tontines traditionnelles africaines.

### 1.2 Objectifs
- Automatiser la gestion des tontines
- Faciliter le suivi des participations et cotisations
- Gérer les crédits et remboursements
- Produire des rapports financiers
- Assurer la traçabilité des opérations

## 2. Architecture Technique

### 2.1 Architecture Globale
- **Pattern**: MVC (Modèle-Vue-Contrôleur)
- **Langage**: Java 17+
- **Interface**: Java Swing avec FlatLaf
- **Base de données**: PostgreSQL
- **ORM**: DAO Pattern personnalisé

### 2.2 Technologies Utilisées
- **Java SE**: 17+
- **PostgreSQL**: 14+
- **Maven**: Gestion des dépendances
- **JCalendar**: Sélecteur de dates
- **FlatLaf**: Design moderne
- **HikariCP**: Pool de connexions

## 3. Fonctionnalités

### 3.1 Gestion des Membres
- ✅ **Création**: Ajouter un nouveau membre
- ✅ **Modification**: Mettre à jour les informations
- ✅ **Suppression**: Retirer un membre
- ✅ **Recherche**: Filtrer par nom, téléphone
- ✅ **Validation**: Contrôle des données obligatoires

#### Champs gérés:
- Nom, Prénom
- Téléphone (unique)
- Email
- Adresse
- Date d'adhésion
- Statut (actif/inactif/suspendu)

### 3.2 Gestion des Types de Tontine
- ✅ **Configuration**: Définir les types de tontine
- ✅ **Paramètres**: Montant, fréquence, obligation
- ✅ **Description**: Détails et conditions

#### Types disponibles:
- Tontine de Présence (obligatoire)
- Tontine Épargne (optionnelle)
- Tontine Solidarité (optionnelle)

### 3.3 Gestion des Tontines
- ✅ **Création**: Nouvelle tontine
- ✅ **Modification**: Mise à jour des paramètres
- ✅ **Suppression**: Archivage
- ✅ **Suivi**: État des tours
- ✅ **Statuts**: Active/terminée/suspendue

#### Paramètres:
- Type de tontine
- Nom et description
- Dates de début/fin
- Nombre de tours
- Tour actuel

### 3.4 Gestion des Participations
- ✅ **Inscription**: Ajouter un participant
- ✅ **Parts multiples**: Gérer plusieurs parts
- ✅ **Suivi**: État des participations
- ✅ **Historique**: Traçabilité

### 3.5 Gestion des Séances
- ✅ **Planification**: Créer des séances
- ✅ **Suivi**: État des séances
- ✅ **Localisation**: Gestion des lieux
- ✅ **Observations**: Notes et commentaires

#### États des séances:
- Planifiée
- En cours
- Terminée

### 3.6 Gestion des Cotisations
- ✅ **Enregistrement**: Saisir les paiements
- ✅ **Suivi**: État des cotisations
- ✅ **Rapports**: Bilans par séance
- ✅ **Validation**: Contrôle des montants

### 3.7 Gestion des Crédits
- ✅ **Octroi**: Accorder des crédits
- ✅ **Suivi**: État des remboursements
- ✅ **Intérêts**: Calcul automatique
- ✅ **Échéances**: Gestion des dates
- ✅ **Statuts**: En cours/remboursé/en retard

### 3.8 Gestion des Bénéficiaires
- ✅ **Attribution**: Désigner les bénéficiaires
- ✅ **Gains**: Calculer les montants
- ✅ **Paiements**: Suivre les versements
- ✅ **Modes**: Espèces/virement/mobile money

### 3.9 Gestion des Projets FIAC
- ✅ **Création**: Nouveaux projets
- ✅ **Suivi**: Avancement
- ✅ **Contributions**: Collecte de fonds
- ✅ **Rapports**: État des projets

### 3.10 Rapports et Statistiques
- ✅ **Tableaux de bord**: Vue d'ensemble
- ✅ **Rapports financiers**: Bilans détaillés
- ✅ **Statistiques**: Indicateurs clés
- ✅ **Export**: Génération de rapports

## 4. Interfaces Utilisateur

### 4.1 Interface Principale
- **Design**: Moderne avec FlatLaf
- **Navigation**: Menu latéral intuitif
- **Tableau de bord**: Vue d'ensemble
- **Thème**: Clair et professionnel

### 4.2 Panneaux Disponibles
1. **Accueil**: Tableau de bord et statistiques
2. **Membres**: Gestion des adhérents
3. **Tontines**: Administration des tontines
4. **Séances**: Planification et suivi
5. **Crédits**: Gestion des prêts
6. **Rapports**: Documents et analyses

### 4.3 Dialogues de Saisie
- **Validation**: Contrôle en temps réel
- **Assistance**: Messages d'aide
- **JCalendar**: Sélecteur de dates
- **Formatage**: Standardisation des données

## 5. Base de Données

### 5.1 Schéma Relationnel
- **10 tables principales**
- **Relations intégrité référentielle**
- **Contraintes CHECK**
- **Index de performance**

### 5.2 Tables Principales
1. **membre**: Informations des adhérents
2. **typetontine**: Configuration des types
3. **tontine**: Instances de tontines
4. **participation**: Adhésions aux tontines
5. **seance**: Réunions planifiées
6. **cotisation**: Paiements effectués
7. **credit**: Prêts accordés
8. **beneficiaire**: Attribution des gains
9. **projetfiac**: Projets communautaires
10. **penalite**: Gestion des pénalités

## 6. Sécurité

### 6.1 Contrôle d'Accès
- **Authentification**: Utilisateur/mot de passe
- **Autorisations**: Rôles et permissions
- **Session**: Gestion sécurisée

### 6.2 Validation des Données
- **Contraintes**: Validation côté serveur
- **Formatage**: Standardisation automatique
- **Intégrité**: Contraintes référentielles

## 7. Performance

### 7.1 Optimisation
- **Index**: Requêtes optimisées
- **Pool**: Connexions partagées
- **Cache**: Mise en cache des données

### 7.2 Scalabilité
- **Architecture**: Modulaire et extensible
- **Base**: PostgreSQL scalable
- **Interface**: Responsive design

## 8. Déploiement

### 8.1 Prérequis
- **Java**: JRE 17+
- **PostgreSQL**: Serveur 14+
- **Système**: Windows/Linux/macOS

### 8.2 Installation
- **Package**: Exécutable autonome
- **Base**: Scripts SQL fournis
- **Configuration**: Fichier properties

## 9. Maintenance

### 9.1 Sauvegarde
- **Automatique**: Planifiée quotidiennement
- **Manuelle**: Sur demande
- **Restauration**: Procédures documentées

### 9.2 Mises à Jour
- **Versioning**: Gestion des versions
- **Migration**: Scripts automatiques
- **Compatibilité**: Ascendante garantie

## 10. Évolutions Prévues

### 10.1 Fonctionnalités Futures
- **Mobile**: Application Android/iOS
- **Web**: Interface web responsive
- **API**: Services web REST
- **Notification**: SMS/Email automatiques

### 10.2 Améliorations
- **IA**: Prédictions et analyses
- **Blockchain**: Traçabilité avancée
- **Multilingue**: Internationalisation
- **Cloud**: Hébergement cloud

---

**Version**: 3.0  
**Date**: 27 Janvier 2026  
**Auteur**: Équipe INF2212 - Université de Yaoundé I

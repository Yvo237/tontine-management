# Guide d'Installation et d'Exécution

## Application de Gestion de Tontine - INF2212

### Prérequis

#### 1. Java Development Kit (JDK)
- **Version requise**: Java 8 ou supérieur
- **Recommandé**: Java 11 ou supérieur
- **Vérification**: `java -version`

#### 2. Base de données MySQL
- **Version requise**: MySQL 8.0 ou supérieur
- **Configuration**: Activer le support des connexions distantes si nécessaire
- **Vérification**: `mysql --version`

#### 3. MySQL Connector/J
- **Version**: 8.0.33 (incluse dans le projet)
- **Fichier**: `lib/mysql-connector-java-8.0.33.jar`

#### 4. IDE (Optionnel)
- **Recommandé**: Eclipse IDE, IntelliJ IDEA ou NetBeans
- **Configuration**: Importer le projet Maven

### Étapes d'Installation

#### 1. Configuration de la Base de Données

```sql
-- Créer la base de données
CREATE DATABASE IF NOT EXISTS gestion_tontine;

-- Utiliser la base de données
USE gestion_tontine;

-- Exécuter le script de schéma
SOURCE sql/schema.sql;

-- Exécuter les triggers
SOURCE sql/triggers.sql;
```

#### 2. Configuration de la Connexion

Éditer le fichier `resources/database.properties`:

```properties
# Configuration de la base de données
db.url=jdbc:mysql://localhost:3306/gestion_tontine?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
db.username=votre_utilisateur_mysql
db.password=votre_mot_de_passe_mysql
db.driver=com.mysql.cj.jdbc.Driver
```

#### 3. Compilation et Exécution

##### Avec Eclipse IDE:
1. Importer le projet comme projet Maven
2. Ajouter les librairies externes:
   - Clic droit sur le projet → Build Path → Configure Build Path
   - Onglet "Libraries" → "Add External JARs"
   - Ajouter `lib/mysql-connector-java-8.0.33.jar`
3. Exécuter `ui.MainFrame`

##### Avec IntelliJ IDEA:
1. File → Open → Sélectionner le dossier du projet
2. File → Project Structure → Modules → Dependencies
3. Cliquer sur "+" → "JARs or directories"
4. Ajouter `lib/mysql-connector-java-8.0.33.jar`
5. Exécuter `ui.MainFrame`

##### En Ligne de Commande:
```bash
# Compilation
javac -cp "lib/*:src" -d build src/ui/MainFrame.java

# Exécution
java -cp "build:lib/*" ui.MainFrame
```

### Structure du Projet

```
tontine-management/
├── src/
│   ├── database/
│   │   └── DatabaseConnection.java
│   ├── models/
│   │   ├── Membre.java
│   │   ├── Tontine.java
│   │   ├── Participation.java
│   │   ├── Seance.java
│   │   ├── Cotisation.java
│   │   ├── Credit.java
│   │   ├── Beneficiaire.java
│   │   ├── Penalite.java
│   │   ├── ProjetFIAC.java
│   │   ├── ContributionFIAC.java
│   │   ├── RemboursementCredit.java
│   │   └── TypeTontine.java
│   ├── dao/
│   │   ├── MembreDAO.java
│   │   ├── TontineDAO.java
│   │   ├── ParticipationDAO.java
│   │   ├── SeanceDAO.java
│   │   ├── CotisationDAO.java
│   │   └── CreditDAO.java
│   └── ui/
│       ├── MainFrame.java
│       └── panels/
│           ├── AccueilPanel.java
│           ├── MembresPanel.java
│           ├── TontinesPanel.java
│           ├── SeancesPanel.java
│           ├── CreditsPanel.java
│           └── RapportsPanel.java
├── lib/
│   ├── mysql-connector-java-8.0.33.jar
│   └── jcalendar-1.4.jar
├── resources/
│   └── database.properties
├── sql/
│   ├── schema.sql
│   ├── triggers.sql
│   └── requetes_tontine.sql
├── docs/
│   ├── cahier_charges.md
│   ├── dictionnaire_donnees.md
│   └── manuel_utilisateur.md
└── README.md
```

### Fonctionnalités Implémentées

#### ✅ Modules Principaux
- **Gestion des Membres**: CRUD complet avec recherche
- **Gestion des Tontines**: Création, modification, suivi
- **Gestion des Séances**: Planification et suivi
- **Gestion des Crédits**: Octroi et remboursement
- **Gestion des Cotisations**: Suivi des paiements
- **Rapports**: Statistiques et exports

#### ✅ Fonctionnalités Techniques
- **Architecture MVC**: Séparation claire Modèle-Vue-Contrôleur
- **Connexion MySQL**: Gestion robuste des connexions
- **Triggers SQL**: Automatisation des règles métier
- **Interface Swing**: GUI moderne et intuitive
- **Validation**: Contrôles de saisie complets

### Dépannage

#### Problèmes Communs

**1. Erreur de connexion à la base de données**
```
❌ Impossible de se connecter au serveur MySQL
```
**Solutions**:
- Vérifier que MySQL est démarré
- Vérifier les identifiants dans `database.properties`
- Vérifier que la base `gestion_tontine` existe

**2. Erreur "No JREs installed"**
```
⚠️ Build path specifies execution environment JavaSE-1.8
```
**Solutions**:
- Installer JDK 8 ou supérieur
- Configurer le JRE dans l'IDE
- Ajouter le JRE système dans les préférences

**3. Erreur de classe introuvable**
```
❌ ClassNotFoundException: com.mysql.cj.jdbc.Driver
```
**Solutions**:
- Ajouter le JAR MySQL Connector au classpath
- Vérifier le chemin dans `database.properties`

#### Logs et Debug

Les erreurs sont affichées dans:
- Console de l'IDE
- Fichiers de logs du système
- Messages d'erreur Swing

### Test de l'Application

#### 1. Test de Connexion
```java
// Vérifier la connexion
DatabaseConnection db = DatabaseConnection.getInstance();
boolean test = db.testConnection();
System.out.println("Connexion: " + (test ? "OK" : "ÉCHOUÉ"));
```

#### 2. Test des Fonctionnalités
1. **Créer un membre**
2. **Créer une tontine**
3. **Ajouter une participation**
4. **Planifier une séance**
5. **Enregistrer une cotisation**
6. **Générer un rapport**

### Performance et Optimisation

#### Recommandations
- **Indexation**: Les tables sont déjà indexées
- **Connexions**: Utiliser le pool de connexions
- **Transactions**: Gérer les erreurs de transaction
- **Memory**: Optimiser l'utilisation mémoire dans l'interface

### Sécurité

#### Bonnes Pratiques
- **Validation**: Toutes les entrées sont validées
- **SQL Injection**: Utilisation de PreparedStatement
- **Mots de passe**: Ne pas stocker en clair
- **Permissions**: Droits d'accès MySQL limités

### Support et Maintenance

#### Documentation Complète
- `docs/cahier_charges.md`: Spécifications fonctionnelles
- `docs/dictionnaire_donnees.md`: Structure de la base
- `docs/manuel_utilisateur.md`: Guide utilisateur
- `sql/requetes_tontine.sql`: Requêtes SQL avancées

#### Contact Support
Pour toute question ou problème technique:
1. Consulter la documentation dans `docs/`
2. Vérifier les logs d'erreur
3. Tester avec les données de test incluses

---

**Version**: 1.0.0  
**Auteur**: Équipe INF2212 - Université de Yaoundé I  
**Date**: 2024

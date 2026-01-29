# Application de Gestion de Tontine

## INF2212 - Implémentation des Bases de Données
### Université de Yaoundé I - Faculté des Sciences

---

## 📋 Description du Projet

Application complète de gestion de tontine développée en Java avec MySQL, permettant de gérer :
- Les membres de la tontine
- Les tontines (présence obligatoire et optionnelles)
- Les séances et cotisations
- Les crédits internes
- Les pénalités
- Les projets collectifs (FIAC)

---

## 🛠️ Technologies Utilisées

- **Langage** : Java 8+
- **Base de données** : MySQL 8.0+
- **Interface graphique** : Java Swing
- **Driver JDBC** : MySQL Connector/J 8.0+
- **Architecture** : MVC (Modèle-Vue-Contrôleur)

---

## 📦 Structure du Projet

```
gestion-tontine/
│
├── src/
│   ├── DatabaseConnection.java
│   ├── models/
│   │   ├── Membre.java
│   │   ├── Tontine.java
│   │   ├── Seance.java
│   │   └── Credit.java
│           ├── dao/
│           │   ├── MembreDAO.java
│           │   ├── TontineDAO.java
│           │   ├── SeanceDAO.java
│           │   └── CreditDAO.java
│           └── ui/
│               ├── MainFrame.java
│               └── panels/
│                   ├── AccueilPanel.java
│                   ├── MembresPanel.java
│                   ├── TontinesPanel.java
│                   ├── SeancesPanel.java
│                   ├── CreditsPanel.java
│                   └── RapportsPanel.java
│
├── resources/
│   └── database.properties
│
├── sql/
│   └── schema.sql
│
└── lib/
    └── mysql-connector-java-8.0.x.jar
```

---

## 🚀 Installation et Configuration

### Prérequis

1. **Java JDK 8 ou supérieur**
   - Télécharger depuis : https://www.oracle.com/java/technologies/downloads/
   - Vérifier l'installation : `java -version`

2. **MySQL Server 8.0 ou supérieur**
   - Télécharger depuis : https://dev.mysql.com/downloads/mysql/
   - Installer et démarrer le service MySQL

3. **MySQL Connector/J (Driver JDBC)**
   - Télécharger depuis : https://dev.mysql.com/downloads/connector/j/
   - Ou utiliser Maven/Gradle pour la gestion des dépendances

### Étape 1 : Installation de la Base de Données

1. Connectez-vous à MySQL :
```bash
mysql -u root -p
```

2. Exécutez le script SQL fourni :
```sql
source /chemin/vers/schema.sql
```

Ou copiez-collez le contenu du fichier SQL dans votre client MySQL.

### Étape 2 : Configuration de l'Application

1. Créez un fichier `database.properties` dans le dossier `resources/` :

```properties
db.url=jdbc:mysql://localhost:3306/gestion_tontine?useSSL=false&serverTimezone=UTC
db.username=root
db.password=votre_mot_de_passe
```

2. Modifiez les paramètres selon votre configuration MySQL.

### Étape 3 : Compilation du Projet

#### Option A : Avec un IDE (Eclipse, IntelliJ, NetBeans)

1. Importez le projet dans votre IDE
2. Ajoutez le driver MySQL Connector/J aux bibliothèques du projet
3. Compilez et exécutez `MainFrame.java`

#### Option B : En ligne de commande

```bash
# Compiler
javac -cp ".;lib/mysql-connector-java-8.0.x.jar" -d bin src/com/tontine/**/*.java

# Exécuter
java -cp ".;bin;lib/mysql-connector-java-8.0.x.jar" com.tontine.ui.MainFrame
```

**Note** : Sous Linux/Mac, remplacez `;` par `:` dans le classpath.

---

## 📚 Utilisation de l'Application

### 1. Gestion des Membres

- **Ajouter un membre** : Cliquez sur "➕ Nouveau Membre"
- **Modifier un membre** : Sélectionnez un membre puis cliquez sur "✏️ Modifier"
- **Supprimer un membre** : Sélectionnez un membre puis cliquez sur "🗑️ Supprimer"
- **Rechercher** : Utilisez la barre de recherche en haut

### 2. Gestion des Tontines

- Créer des tontines de présence (obligatoires)
- Créer des tontines optionnelles à parts multiples
- Suivre l'état de chaque tontine

### 3. Gestion des Séances

- Planifier des séances
- Enregistrer les cotisations
- Désigner les bénéficiaires
- Appliquer des pénalités

### 4. Gestion des Crédits

- Accorder des crédits aux membres
- Suivre les remboursements
- Calculer automatiquement les intérêts

### 5. Rapports

- Situation financière par membre
- État de séance
- Liste des crédits en cours
- Synthèse pour assemblée générale

---

## 🔒 Contraintes Implémentées

### Contrainte Majeure des Tontines Optionnelles

La base de données utilise un **trigger** pour vérifier automatiquement que :

> Le montant cumulé perçu par un membre dans une tontine optionnelle ne doit jamais excéder le montant total qu'il est censé cotiser sur l'ensemble des tours.

**Exemple** :
- Tontine de 10 tours
- Membre avec 2 parts
- Cotisation de 10 000 FCFA par part
- Total à cotiser = 10 × 2 × 10 000 = 200 000 FCFA
- Le membre ne peut recevoir plus de 200 000 FCFA au total

---

## 📊 Requêtes SQL Implémentées

L'application inclut au minimum 15 requêtes variées :

1. **Requêtes de sélection** : Liste des membres, tontines actives, etc.
2. **Requêtes paramétrées** : Recherche de membres, filtrage par statut
3. **Requêtes de regroupement** : Statistiques par tontine, totaux par membre
4. **Requêtes d'action** : Mises à jour de statut, calculs automatiques

---

## 🧪 Jeux de Données de Test

Le fichier SQL inclut des données de test :
- 5 membres exemple
- 3 types de tontine
- 2 tontines actives

Vous pouvez les utiliser pour tester l'application ou les supprimer pour partir d'une base vierge.

---

## 🔧 Dépannage

### Problème de connexion à la base de données

**Erreur** : `Communications link failure`

**Solutions** :
1. Vérifiez que MySQL est démarré
2. Vérifiez l'URL de connexion dans `database.properties`
3. Vérifiez les identifiants (username/password)
4. Vérifiez que le port 3306 est disponible

### Erreur de driver JDBC

**Erreur** : `ClassNotFoundException: com.mysql.cj.jdbc.Driver`

**Solution** : Assurez-vous que le fichier JAR du MySQL Connector est dans le classpath

### Problème d'encodage des caractères

Si les caractères accentués ne s'affichent pas correctement :
```properties
db.url=jdbc:mysql://localhost:3306/gestion_tontine?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
```

---

## 📝 Fonctionnalités à Développer

Les panneaux suivants nécessitent encore du développement :
- [ ] Gestion complète des tontines
- [ ] Gestion complète des séances
- [ ] Gestion complète des crédits
- [ ] Module de rapports avancés
- [ ] Module FIAC (projets collectifs)

---

## 👥 Équipe de Développement

Ce projet est réalisé dans le cadre de l'UE INF2212 par un groupe de 15 étudiants environ.

**Chef de groupe** : [À compléter]

**Membres** :
1. [À compléter]
2. [À compléter]
3. ...

---

## 📅 Échéances

- **Date limite de remise** : 18 janvier 2026 à 12h00
- **Email de soumission** : etienne.kouokam@facsciences-uy1.cm
- **Démonstration** : Séance en présentiel (date à confirmer)

---

## 📄 Livrables

### Documents PDF
- ✅ Cahier des charges
- ✅ MCD/MLD (Modèle Conceptuel/Logique de Données)
- ✅ Dictionnaire des données
- ✅ Manuel utilisateur

### Fichiers Techniques
- ✅ Scripts SQL (schema.sql)
- ✅ Code source Java
- ✅ Données de test
- ✅ Fichier de configuration

### Document Confidentiel
- ✅ Pourcentages de participation (si nécessaire)

---

## 📜 Licence

Ce projet est développé à des fins pédagogiques dans le cadre de l'UE INF2212 de l'Université de Yaoundé I.

---

## 📞 Contact

Pour toute question concernant le projet :
- **Enseignant** : Etienne Kouokam
- **Email** : etienne.kouokam@facsciences-uy1.cm

---

**Bon courage pour votre projet ! 🎓**
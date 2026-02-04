# Tontine Management Elite

## Étapes d'installation et de lancement

### 1. Cloner le projet depuis GitHub

```bash
git clone https://github.com/Yvo237/tontine-management.git
cd tontine-management
```

### 2. Installation et configuration de PostgreSQL

#### Sur Linux (Ubuntu/Debian) :
```bash
# Installer PostgreSQL
sudo apt update
sudo apt install postgresql postgresql-contrib

# Démarrer le service PostgreSQL
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

#### Sur macOS 
```bash
# Installer PostgreSQL
brew install postgresql
brew services start postgresql
```

#### Sur Windows :
Télécharger et installer PostgreSQL depuis : https://www.postgresql.org/download/windows/

### 3. Configuration de la base de données

Le projet inclut un script automatisé pour configurer PostgreSQL :

```bash
# Rendre le script exécutable
chmod +x configure_postgresql.sh

# Exécuter le script de configuration
./configure_postgresql.sh
```

**Ou manuellement :**
```bash
# Se connecter à PostgreSQL
sudo -u postgres psql

# Créer l'utilisateur et la base de données
CREATE USER tontine_user WITH PASSWORD 'tontine123';
CREATE DATABASE gestion_tontine OWNER tontine_user;
GRANT ALL PRIVILEGES ON DATABASE gestion_tontine TO tontine_user;
\q
```

### 4. Création des tables et données initiales

```bash
# Appliquer le schéma complet de la base de données
PGPASSWORD=tontine123 psql -U tontine_user -d gestion_tontine -f sql/schema_complete_postgresql.sql

# Appliquer les corrections si nécessaire
PGPASSWORD=tontine123 psql -U tontine_user -d gestion_tontine -f sql/correction_complete.sql
```

### 5. Compilation de l'application

#### Option A : Avec Maven (recommandé pour le développement)
```bash
# Compiler et créer le JAR exécutable
mvn clean package

# Le JAR sera généré dans target/tontine-management-elite-3.0.0-RELEASE.jar
```

#### Option B : Compilation manuelle
```bash
# Télécharger les dépendances Maven dans un dossier lib/
mvn dependency:copy-dependencies -DoutputDirectory=lib

# Compiler les sources
javac -cp ".:lib/*" -d target/classes src/**/*.java

# Créer le JAR
jar cfe tontine-management.jar ui.MainFrame -C target/classes .
```

### 6. Lancement de l'application

#### Option A : Avec le script fourni (recommandé)
```bash
# Rendre le script exécutable
chmod +x run.sh

# Lancer l'application
./run.sh
```

#### Option B : Avec le JAR Maven
```bash
java -jar target/tontine-management-elite-3.0.0-RELEASE.jar
```

#### Option C : Directement avec les classes compilées
```bash
# Télécharger les dépendances 
mvn dependency:copy-dependencies -DoutputDirectory=lib

# Lancer avec le classpath complet
java -cp "target/classes:resources:lib/*" ui.MainFrame
```

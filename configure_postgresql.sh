#!/bin/bash

# Script pour configurer PostgreSQL et créer la base de données
echo "🔧 Configuration de PostgreSQL pour l'application de gestion de tontine..."

# Se connecter en tant que postgres et configurer
sudo -u postgres psql << 'EOF'
-- Créer ou modifier le mot de passe de l'utilisateur tontine_user
ALTER USER tontine_user WITH PASSWORD 'tontine123';

-- Créer la base de données si elle n'existe pas
CREATE DATABASE gestion_tontine OWNER tontine_user;

-- Donner les privilèges sur la base de données
GRANT ALL PRIVILEGES ON DATABASE gestion_tontine TO tontine_user;

-- Quitter
\q
EOF

echo "✅ Configuration PostgreSQL terminée"
echo "📊 Utilisateur: tontine_user"
echo "🔑 Mot de passe: tontine123"
echo "🗄️ Base de données: gestion_tontine"
echo ""
echo "🚀 Test de la connexion..."

# Tester la connexion
PGPASSWORD=tontine123 psql -U tontine_user -d gestion_tontine -c "SELECT version();" && echo "✅ Connexion réussie !" || echo "❌ Erreur de connexion"

echo ""
echo "🎯 Vous pouvez maintenant exécuter le script de correction :"
echo "PGPASSWORD=tontine123 psql -U tontine_user -d gestion_tontine -f sql/correction_complete.sql"

# Manuel Utilisateur
## Application de Gestion de Tontine

**Version:** 1.0  
**Date:** Janvier 2026  
**Pour:** INF2212 - Université de Yaoundé I

---

## Table des Matières

1. [Introduction](#introduction)
2. [Démarrage de l'Application](#démarrage)
3. [Navigation Générale](#navigation)
4. [Gestion des Membres](#membres)
5. [Gestion des Tontines](#tontines)
6. [Gestion des Séances](#seances)
7. [Gestion des Crédits](#credits)
8. [Rapports et Statistiques](#rapports)
9. [Résolution de Problèmes](#problemes)

---

<a name="introduction"></a>
## 1. Introduction

### 1.1 Présentation

L'application de Gestion de Tontine est un logiciel complet permettant de gérer tous les aspects d'une tontine :
- Inscription et suivi des membres
- Création et gestion de plusieurs tontines (obligatoires et optionnelles)
- Organisation des séances et enregistrement des cotisations
- Gestion des crédits internes
- Génération de rapports et statistiques

### 1.2 Concepts Clés

**Tontine de Présence (Obligatoire)**
- Tous les membres doivent y participer
- Une seule part par membre
- Cotisation fixe à chaque séance

**Tontine Optionnelle**
- Participation volontaire
- Possibilité de souscrire plusieurs parts
- **Règle importante**: Le montant total reçu ne peut jamais dépasser le montant total à cotiser

**Séance**
- Réunion périodique de la tontine
- Collecte des cotisations
- Désignation du/des bénéficiaire(s)

---

<a name="démarrage"></a>
## 2. Démarrage de l'Application

### 2.1 Première Utilisation

1. **Lancer l'application**
   - Double-cliquez sur `gestion-tontine.jar`
   - Ou via ligne de commande: `java -jar gestion-tontine.jar`

2. **Vérifier la connexion**
   - La connexion à la base de données s'établit automatiquement
   - Un message de confirmation apparaît dans la console

3. **Écran d'accueil**
   - Vous arrivez sur le **Tableau de Bord**
   - Vous y voyez les statistiques principales

### 2.2 Configuration Initiale

Si c'est votre première utilisation, commencez par:

1. **Ajouter des membres** (Menu Membres)
2. **Créer des types de tontine** (si nécessaire)
3. **Créer vos tontines** (Menu Tontines)
4. **Inscrire les participants** à chaque tontine

---

<a name="navigation"></a>
## 3. Navigation Générale

### 3.1 Menu Latéral

Le menu de navigation se trouve à gauche de l'écran:

```
🏠 Accueil       → Tableau de bord
👥 Membres       → Gestion des membres
💰 Tontines      → Gestion des tontines
📅 Séances       → Gestion des séances
💳 Crédits       → Gestion des crédits
📊 Rapports      → Statistiques et rapports
```

### 3.2 Barre de Menu

En haut de l'écran, vous trouverez:

- **Fichier** → Quitter
- **Gestion** → Accès rapide aux modules
- **Rapports** → Génération de rapports
- **Aide** → À propos

### 3.3 Barre d'État

En bas de l'écran:
- À gauche: Statut de l'application
- À droite: Date et heure actuelles

---

<a name="membres"></a>
## 4. Gestion des Membres

### 4.1 Ajouter un Nouveau Membre

1. Cliquez sur **👥 Membres** dans le menu
2. Cliquez sur **➕ Nouveau Membre**
3. Remplissez le formulaire:
   - **Nom*** (obligatoire)
   - **Prénom*** (obligatoire)
   - **Téléphone*** (obligatoire, unique)
   - Email (optionnel)
   - Adresse (optionnel)
   - Statut (actif par défaut)

4. Cliquez sur **💾 Enregistrer**

**⚠️ Attention**: Le numéro de téléphone doit être unique.

### 4.2 Modifier un Membre

1. Sélectionnez le membre dans la liste
2. Cliquez sur **✏️ Modifier**
3. Modifiez les informations
4. Cliquez sur **💾 Enregistrer**

### 4.3 Rechercher un Membre

1. Utilisez la barre de recherche en haut à droite
2. Tapez le nom, prénom ou téléphone
3. Appuyez sur **Entrée** ou cliquez sur **🔍**

### 4.4 Supprimer un Membre

1. Sélectionnez le membre
2. Cliquez sur **🗑️ Supprimer**
3. Confirmez la suppression

**⚠️ Attention**: La suppression est irréversible ! Un membre ayant des participations actives ne peut pas être supprimé.

### 4.5 Statuts des Membres

- **Actif**: Membre participant activement
- **Inactif**: Membre temporairement non actif
- **Suspendu**: Membre exclu temporairement

---

<a name="tontines"></a>
## 5. Gestion des Tontines

### 5.1 Créer une Nouvelle Tontine

1. Cliquez sur **💰 Tontines**
2. Cliquez sur **➕ Nouvelle Tontine**
3. Remplissez:
   - **Nom*** (ex: "Tontine Présence 2024")
   - **Type*** (sélectionnez dans la liste)
   - **Date de Début***
   - **Nombre de Tours*** (ex: 12 pour un an mensuel)
   - Statut (active par défaut)

4. Cliquez sur **💾 Enregistrer**

### 5.2 Types de Tontine Prédéfinis

L'application propose généralement:

1. **Tontine de Présence**
   - Obligatoire: ✅
   - Montant fixe
   - 1 part par membre

2. **Tontine Épargne**
   - Optionnelle: ❌
   - Parts multiples possibles
   - Montant plus élevé

3. **Tontine Solidarité**
   - Optionnelle: ❌
   - Pour projets communs

### 5.3 Gérer les Participants

1. Sélectionnez une tontine
2. Cliquez sur **👥 Participants**
3. Vous voyez la liste des participants
4. Cliquez sur **➕ Ajouter Participant** pour en ajouter

**Pour les tontines optionnelles**:
- Vous pouvez spécifier le nombre de parts (1, 2, 3...)
- Plus de parts = plus de cotisations mais plus de chances de gains

### 5.4 Modifier une Tontine

Vous pouvez modifier:
- Le nom
- La date de fin
- Le tour actuel
- Le statut

**⚠️ Vous ne pouvez PAS modifier**:
- Le type
- La date de début
- Le nombre de tours total

### 5.5 Statuts des Tontines

- **Active**: Tontine en cours
- **Terminée**: Tous les tours effectués
- **Suspendue**: Temporairement arrêtée

---

<a name="seances"></a>
## 6. Gestion des Séances

### 6.1 Planifier une Séance

1. Cliquez sur **📅 Séances**
2. Cliquez sur **➕ Nouvelle Séance**
3. Remplissez:
   - **Tontine*** (sélectionnez)
   - **Tour N°*** (numéro du tour)
   - **Date*** (date de la séance)
   - Lieu (optionnel, ex: "Domicile de Marie")
   - Observations (optionnel)

4. Cliquez sur **💾 Enregistrer**

**💡 Important**: Lors de la création d'une séance, les cotisations sont **automatiquement créées** pour tous les participants.

### 6.2 Gérer les Cotisations

1. Sélectionnez une séance
2. Cliquez sur **💰 Cotisations**
3. Vous voyez tous les membres avec leurs cotisations

Pour chaque cotisation:
- Montant Dû
- Montant Versé
- Reste à Payer
- Statut

### 6.3 Enregistrer un Paiement

1. Dans l'écran Cotisations
2. Sélectionnez une cotisation
3. Cliquez sur **💰 Enregistrer Paiement**
4. Entrez le montant versé
5. Le statut se met à jour automatiquement:
   - **Payé**: Si montant versé = montant dû
   - **Partiel**: Si 0 < montant versé < montant dû
   - **Impayé**: Si rien n'est versé après la séance

### 6.4 Désigner un Bénéficiaire

Cette fonctionnalité sera utilisée pour:
1. Sélectionner qui reçoit le gain du tour
2. Enregistrer le montant distribué
3. Choisir le mode de paiement

**Contrainte Importante pour Tontines Optionnelles**:
Le système vérifie automatiquement que le membre ne reçoit pas plus que ce qu'il doit cotiser sur tous les tours.

### 6.5 Filtrer les Séances

Utilisez le filtre **Tontine** en haut à droite pour:
- Voir toutes les séances
- Filtrer par tontine spécifique

---

<a name="credits"></a>
## 7. Gestion des Crédits

### 7.1 Accorder un Crédit

1. Cliquez sur **💳 Crédits**
2. Cliquez sur **➕ Nouveau Crédit**
3. Remplissez:
   - **Membre*** (emprunteur)
   - **Tontine*** (source du crédit)
   - **Montant*** (en FCFA)
   - **Taux d'Intérêt*** (en %, peut être 0)
   - **Date d'Emprunt***
   - **Date d'Échéance***

4. Cliquez sur **💾 Enregistrer**

**Calculs Automatiques**:
- Montant Total = Montant Emprunté × (1 + Taux/100)
- Reste = Montant Total - Montant Remboursé

### 7.2 Enregistrer un Remboursement

1. Sélectionnez un crédit
2. Cliquez sur **💵 Remboursement**
3. Entrez le montant remboursé
4. Choisissez le mode de paiement:
   - Espèces
   - Virement
   - Chèque
   - Mobile Money

5. Validez

**Mise à jour automatique**:
- Le montant remboursé s'ajoute au total
- Le statut change en "remboursé" si le total est atteint

### 7.3 Statuts des Crédits

- **En cours**: Crédit actif, non entièrement remboursé
- **Remboursé**: Crédit totalement remboursé
- **En retard**: Date d'échéance dépassée

**💡 Astuce**: Cliquez sur **🔄 Rafraîchir** pour mettre à jour les statuts des crédits en retard.

### 7.4 Consulter les Détails

1. Sélectionnez un crédit
2. Cliquez sur **📋 Détails**
3. Vous voyez:
   - Informations complètes
   - Historique des remboursements
   - Calculs détaillés

### 7.5 Filtrer les Crédits

Utilisez le filtre **Statut** pour voir:
- Tous les crédits
- Seulement ceux en cours
- Les crédits remboursés
- Les crédits en retard

---

<a name="rapports"></a>
## 8. Rapports et Statistiques

### 8.1 Tableau de Bord

L'écran d'**Accueil** affiche:

**Cartes de Statistiques**:
- 👥 Nombre total de membres
- ✅ Membres actifs
- 💰 Tontines actives
- 💳 Crédits en cours

**Actualisation**: Les statistiques se mettent à jour automatiquement.

### 8.2 Types de Rapports Disponibles

#### 📊 Situation Financière par Membre
- Cotisations versées
- Gains reçus
- Crédits en cours
- Pénalités
- **Solde net**

#### 📅 État de Séance
- Liste des présents
- Cotisations collectées
- Bénéficiaires du tour
- Montant distribué

#### 💳 Crédits en Cours
- Liste de tous les crédits actifs
- Montants empruntés
- Montants remboursés
- **Reste à recouvrer**

#### 📈 Synthèse AG (Assemblée Générale)
- Nombre de membres
- Tontines actives
- Total des cotisations
- Total des crédits
- Projets FIAC

#### 💰 Cotisations par Tontine
- Total attendu
- Total collecté
- **Taux de recouvrement**
- Retards de paiement

#### 👥 Liste des Membres
- Tous les membres avec coordonnées
- Statut de chacun
- Possibilité d'export (future version)

### 8.3 Générer un Rapport

1. Cliquez sur **📊 Rapports**
2. Choisissez le type de rapport
3. Le rapport s'affiche dans une fenêtre
4. Vous pouvez:
   - Lire les informations
   - Copier le texte
   - Imprimer (Ctrl+P)

---

<a name="problemes"></a>
## 9. Résolution de Problèmes

### 9.1 Problèmes de Connexion

**Symptôme**: "Erreur de connexion à la base de données"

**Solutions**:
1. Vérifiez que MySQL est démarré
2. Vérifiez les identifiants dans `database.properties`
3. Vérifiez que le port 3306 est libre
4. Redémarrez MySQL

### 9.2 Erreur "Téléphone Existe Déjà"

**Cause**: Chaque numéro de téléphone doit être unique

**Solution**: 
- Vérifiez si un membre existe déjà avec ce numéro
- Utilisez la recherche pour le trouver
- Modifiez l'ancien membre si nécessaire

### 9.3 Impossible de Supprimer un Membre

**Cause**: Le membre a des participations actives

**Solution**:
- Retirez d'abord le membre de toutes ses tontines
- Ou changez son statut en "inactif" au lieu de le supprimer

### 9.4 "Contrainte de Tontine Optionnelle"

**Symptôme**: Impossible d'attribuer un gain à un membre

**Cause**: Le membre a déjà reçu autant qu'il doit cotiser

**Explication**:
- Membre avec 2 parts dans une tontine de 10 tours à 5000 FCFA
- Total à cotiser = 2 × 10 × 5000 = 100 000 FCFA
- Il ne peut recevoir plus de 100 000 FCFA au total

**Solution**: C'est normal, c'est une règle de protection !

### 9.5 Crédit Passe en "En Retard"

**Cause**: Date d'échéance dépassée

**Solution**:
1. Enregistrez les remboursements effectués
2. Ou modifiez la date d'échéance si accord avec le membre
3. Cliquez sur Rafraîchir pour mettre à jour

### 9.6 Cotisations Non Créées

**Symptôme**: Aucune cotisation après création de séance

**Cause**: Aucun participant inscrit à la tontine

**Solution**:
1. Allez dans Tontines
2. Sélectionnez la tontine
3. Cliquez sur "Participants"
4. Ajoutez des participants
5. Recréez la séance

---

## 10. Raccourcis Clavier

| Touche | Action |
|--------|--------|
| Ctrl + N | Nouveau (dans le module actif) |
| Ctrl + S | Enregistrer |
| Ctrl + F | Rechercher |
| Ctrl + R | Rafraîchir |
| Echap | Fermer dialog |
| F5 | Actualiser |

---

## 11. Bonnes Pratiques

### ✅ À FAIRE:

1. **Sauvegardez régulièrement** la base de données
2. **Vérifiez les cotisations** avant chaque séance
3. **Mettez à jour les statuts** des crédits régulièrement
4. **Générez des rapports** mensuels
5. **Formez plusieurs utilisateurs** pour éviter la dépendance

### ❌ À ÉVITER:

1. Ne supprimez JAMAIS directement dans la base de données
2. Ne modifiez pas les types de tontine après création
3. N'accordez pas de crédit sans vérifier la capacité de remboursement
4. Ne sautez pas de numéros de tour
5. Ne fermez pas l'application pendant un enregistrement

---

## 12. Support et Contact

### Besoin d'Aide ?

1. **Consultez ce manuel** d'abord
2. **Vérifiez les messages d'erreur** affichés
3. **Contactez l'administrateur** de votre tontine

### Pour le Projet INF2212

- **Enseignant**: Etienne Kouokam
- **Email**: etienne.kouokam@facsciences-uy1.cm

---

## Annexes

### Annexe A: Formules de Calcul

**Cotisation Totale d'un Membre**:
```
Total = Nombre_Parts × Nombre_Tours × Montant_Cotisation
```

**Montant Total d'un Crédit**:
```
Total = Montant_Emprunté × (1 + Taux_Intérêt / 100)
```

**Taux de Recouvrement**:
```
Taux = (Montant_Collecté / Montant_Attendu) × 100
```

### Annexe B: Codes de Statut

**Membres**: actif, inactif, suspendu  
**Tontines**: active, terminee, suspendue  
**Séances**: planifiee, en_cours, terminee  
**Cotisations**: en_attente, partiel, paye, impaye  
**Crédits**: en_cours, rembourse, en_retard

---

**Fin du Manuel Utilisateur**

*Version 1.0 - Janvier 2026*
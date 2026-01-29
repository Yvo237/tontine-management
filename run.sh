#!/bin/bash

# Script de lancement pour l'application de gestion de tontine
# avec PostgreSQL et FlatLaf

echo " Lancement de l'application de gestion de tontine..."

# Définir le classpath avec les dépendances nécessaires
CLASSPATH="target/classes"
CLASSPATH="$CLASSPATH:resources"
CLASSPATH="$CLASSPATH:/home/yvo/.m2/repository/org/postgresql/postgresql/42.7.1/postgresql-42.7.1.jar"
CLASSPATH="$CLASSPATH:/home/yvo/.m2/repository/com/zaxxer/HikariCP/5.1.0/HikariCP-5.1.0.jar"
CLASSPATH="$CLASSPATH:/home/yvo/.m2/repository/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar"
CLASSPATH="$CLASSPATH:/home/yvo/.m2/repository/com/toedter/jcalendar/1.4/jcalendar-1.4.jar"
CLASSPATH="$CLASSPATH:/home/yvo/.m2/repository/com/formdev/flatlaf/3.4/flatlaf-3.4.jar"
CLASSPATH="$CLASSPATH:/home/yvo/.m2/repository/com/formdev/flatlaf-intellij-themes/3.4/flatlaf-intellij-themes-3.4.jar"

echo " Classpath: $CLASSPATH"

# Lancer l'application
java -cp "$CLASSPATH" ui.MainFrame

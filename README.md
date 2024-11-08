# Projet de Visualisation de Données

## Description

Ce projet a pour objectif de visualiser des données en 2D. Il offre la possibilité d'afficher des points sur un plan 2D, de sélectionner les axes de projection, de distinguer les catégories à l'aide de couleurs, de différencier les points ajoutés par leur forme, et de classifier les points selon leurs caractéristiques.

## Fonctionnalités

- Visualisation des points en 2D
- Choix des axes de projection
- Différenciation des points selon leurs catégories par la couleur et la forme
- Ajout de nouveaux points
- Classification des points
- Modèle MVC avec affichage en multiples fenêtres (Menu >- Affichage > Nouvelle fenêtre)

## Installation

Pour installer le projet, vous pouvez cloner le dépôt ou télécharger la dernière version disponible.

## Prérequis

Assurez-vous que Maven est installé sur votre machine pour exécuter le projet.

## Utilisation

### Pour exécuter le projet :

#### Depuis un terminal :
lancez le fichier `run.sh` situé dans le répertoire Application.

#### Depuis IntelliJ :
Créez une nouvelle configuration de lancement en sélectionnant Maven.

Utilisez la commande suivante dans les options de lancement : `clean javafx:run`

#### Exécution du fichier JAR :
Le .jar est disponible ici : https://gitlab.univ-lille.fr/sae302/2024/H2_SAE3.3/-/jobs/131513/artifacts/download

Utilisez la commande suivante dans un terminal en remplaçant /chemin/ par le chemin vers votre fichier dossier decompressé artifacts.zip :
````
java -jar /chemin/artifacts/Application/target/VisualisationDonnees-1.0-SNAPSHOT.jar
````

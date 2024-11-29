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
- Chargement de données depuis un fichier CSV
- Sauvegarde du graphique en image
- Visualisation avec une carte de chaleur
- Validation croisée pour évaluer la robustesse de l'algorithme de classification
- Test simple de robustesse à partir d'un fichier de test

## Installation

Pour installer le projet, vous pouvez cloner le dépôt ou télécharger la dernière version disponible.

## Prérequis

Assurez-vous que Maven est installé sur votre machine pour exécuter le projet.
Il est recommandé d'utiliser openjdk 21 pour une meilleure compatibilité.

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

### Comment utiliser un nouveau type de données ?

Pour utiliser un nouveau type de données, il suffit de créer une nouvelle classe étendant la classe Data. Il faut aussi que la classe soit dans le package fr.univlille.s3.S302.model.data. 
Ensuite, dans un bloc static, il convient d'appeler la fonction registerHeader de DataLoader, en y mettant le header de votre fichier CSV (sans le retour à la ligne). 

Il ne vous reste plus qu'à déclarer les attributs de votre classe en suivant les conventions d'OpenCSV. 
Si, pour un attribut, les valeurs entre elles n'ont pas d'ordre spécifique, il suffit d'ajouter l'annotation @HasNoOrder devant la déclaration de l'attribut.
Ainsi, la distance entre deux points sera calculée en fonction de l'égalité des attributs. 

Votre classe est maintenant prête à être utilisée dans le projet.

Il n'y a pas besoin d'indiquer quels champs sont susceptibles d'être des champs de classification, notre implémentation permet que tous les champs puissent être utilisés pour la classification.

# Fonctionnement

## Data

La classe `Data` est une classe abstraite qui permet à toute classe en héritant d'être traitée par le modèle. Le cœur de cette classe réside dans la fonction `makeData`, qui parcourt les attributs de la classe implémentant `Data`. Cette fonction attribue une valeur numérique à chaque attribut en utilisant la fonction `getIntValue`. Grâce à cela, chaque attribut de la classe peut être représenté sous forme numérique.

La fonction `getIntValue` complète la map `attributMap`, où chaque attribut est associé à une liste de valeurs (les valeurs originales). Les indices de cette liste jouent le rôle de valeurs numériques pour représenter la valeur originale de l'attribut.


### Autres Maps

La classe contient également plusieurs autres maps dont le principal objectif est de réduire le nombre de calculs nécessaires pour déterminer les distances entre les points :

- **attributesNumericalValueToAttributesOriginalMap** : Permet de retrouver l'attribut original à partir de la valeur numérique.
- **dataTypesMap** : Permet de retrouver le type de donnée de l'attribut.
- **fieldsMap** : Permet de retrouver l'objet de classe `Field` associé au nom de l'attribut.

Avec tout cela, l'application intègre tous les attributs implémentant l'interface `Comparable`. Pour les attributs n'ayant pas d'ordre spécifique, un traitement supplémentaire est effectué pour les transformer en valeurs numériques.

### Gestion des Attributs Sans Ordre Spécifique

Lors de la création d'une classe implémentant `Data`, il est nécessaire de définir les attributs qui sont sans ordre spécifique. Pour ce faire, il suffit d'ajouter l'annotation `@HasNoOrder`. Ces attributs sont d'abord traités comme les autres, et la fonction leur attribue une valeur numérique différente.

Lors du calcul de la distance entre deux points, un traitement supplémentaire est appliqué pour transformer ces attributs en valeurs numériques. La valeur numérique sera ajustée en fonction de l'égalité des attributs : si les attributs sont égaux, la valeur numérique sera 0 ; sinon, l'un des attributs sera noté 1 et l'autre 0. Ainsi, la distance ne dépendra que de leur égalité.


## DataManager

design pattern : Singleton

La classe DataManager permet de gérer les données. 
cette classe a comme attribut, principalement, une liste de Data `dataList` provenant d'une source de données (ici un csv).
Et une autre liste de Data qui sont elles des données ajouté au model ultérieurement `UserData`.

Cette classe est munie de tout un set de fonctions permettant de manipuler les données, les ajouter, les supprimer, les classifier, les charger, etc.

Pour permmettre a toutes les classe de partager le meme DataManager, on a utilisé le design pattern Singleton.

La classe delegue le chargment et la créationdes objets Data a la classe DataLoader.


## DataLoader

La classe DataLoader permet de charger les données depuis un fichier CSV.

Elle utilise la librairie OpenCSV pour lire les données du fichier CSV et les convertir en objets Data.

Avant de charger les données avec OpenCSV, DataLoader doit être configuré pour reconnaître les classes Data qui seront utilisées pour représenter les données.

Pour cela, DataLoader utilise la méthode statique `registerHeader` pour enregistrer les entêtes des colonnes des fichiers CSV.

La fonction getClassFromHeader permet de récupérer la classe Data correspondant à un entête CSV. Il ne reste plus qu'à appeler la fonction `csvToList` pour charger les données.
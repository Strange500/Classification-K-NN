### Implémentation de l'algorithme K-NN
Cette implémentation se trouve dans la fonction `getNearestDatas` de la classe `DataManager`.

L’implémentation de K-NN (K-Nearest Neighbors) une `PriorityQueue` pour identifier efficacement les `nbVoisin` (K) plus proches voisins d’une donnée cible. Voici les points clés de cette approche :

1. **PriorityQueue et MaxHeap** : L'emploi d'un `maxHeap` permet de maintenir les distances des K plus proches voisins de manière optimisée, avec des opérations d'insertion et de recherche en O(log K), contrairement à un tri complet qui aurait une complexité en O(N log N).

2. **Calcul des distances** : Pour chaque donnée dans `dataList`, la distance à la donnée cible est calculée via `Data.distance(data, d, distanceSouhaitee)`, ce qui permet de garder uniquement les K plus proches voisins et de réduire le nombre de comparaisons nécessaires.

3. **Gestion du MaxHeap** :
    - Si le `maxHeap` a une taille inférieure à K, la nouvelle donnée est ajoutée.
    - Si le `maxHeap` est plein et qu'une donnée a une distance inférieure à celle du maximum, elle remplace cet élément, garantissant ainsi la conservation des K voisins les plus proches.

4. **Extraction des résultats** : Les données des K plus proches voisins sont extraites du `maxHeap` et retournées dans une liste.

### Efficacité sur grands volumes de données

- **Complexité temporelle** : La complexité est O(N log K), optimisée pour le nombre de voisins à retrouver, plutôt qu'O(N log N).

- **Mémoire** : Le `maxHeap` fixe de taille K permet une gestion efficace de la mémoire, même avec des `dataList` volumineuses, réduisant l'empreinte mémoire de l’algorithme. (Une PriorityQueue est par default de taille 11, elle est dynamique et est augmentée si nécessaire)

- **Scalabilité** : Cette méthode est capable de traiter des ensembles de données croissants, car le temps de traitement dépend principalement du nombre de voisins et non de la taille totale des données.

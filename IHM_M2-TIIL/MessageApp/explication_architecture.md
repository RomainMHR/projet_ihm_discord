# Architecture MVC Multi-Vues : Coexistence de Swing et JavaFX

Ce document a pour but d'expliquer comment l'application `MessageApp` arrive à faire fonctionner en parallèle deux interfaces graphiques totalement différentes (l'antique **Swing** et le moderne **JavaFX**) de façon parfaitement synchronisée. 

## 1. Le Pattern d'Architecture : Le Modèle MVC strict
Le patron de conception (Design Pattern) utilisé principalement dans cette application est le **Modèle-Vue-Contrôleur (MVC)**. Ce patron sépare l'application en 3 grandes couches :

### a) Le Modèle (La Donnée)
Le Modèle, c'est ce qui gère la logique métier ("core") de l'application et les données sauvegardées. 
Ici, c'est représenté par le `DataManager` (le chef d'orchestre de la base de données) et ses dépendances (`Database`, entités de données, etc.).
- **Caractéristique Clé :** Le Modèle **ignore complètement** l'existence de l'IHM (Interface Homme-Machine). Il ne sait pas si c'est JavaFX ou Swing qui lit ses données !

### b) Les Vues (L'Esthétique)
La Vue est la représentation graphique sur l'écran. 
Dans notre projet, pour **une** fenêtre logique (ex: la liste des messages), nous avons développé **deux** Vues :
- Une classe Swing (`MessageListPanel.java` en JPanel Swing)
- Une classe JavaFX (`MessageListPanelFx.java` en VBox JavaFX)

### c) Le Contrôleur (Le Cerveau de la Vue)
C'est la pièce maîtresse du puzzle. Chaque composant (Vue) dispose de son propre **Contrôleur** (`MessageController`, `ChannelController`, `LoginController`, etc...).
- **Son rôle :** Le Contrôleur fait le lien entre la Vue d'un côté et le `DataManager` de l'autre. Quand on clique sur le bouton "Envoyer" de la *Vue*, c'est le *Contrôleur* qui est appelé et qui va lui-même parler au `DataManager`. Quand le *DataManager* met à jour la base de données, c'est le *Contrôleur* qui s'en rend compte et qui prévient la *Vue*.

---

## 2. Le Secret : Les Interfaces Java (L'Abstraction)
Si nous n'avions pas appliqué de règles de programmation "propres", un Contrôleur aurait dû manipuler directement une Vue de type "Swing", et il aurait fallu recréer tous les Contrôleurs pour "JavaFX".

**Pour nous épargner cela, nous avons introduit l'Abstraction.**
Le Contrôleur de l'application (`MessageController` par exemple) ne parle jamais *directement* ni à `MessageListPanel` (Swing) ni à `MessageListPanelFx` (JavaFX). 
Il s'adresse à un intermédiaire neutre que les deux Vues s'engagent à respecter : c'est notre interface **`IMessageListView`**.

### L'Interface IMessageListView
Dans le package `ihm.interfaces`, ce contrat contient par exemple une méthode `updateMessageList()`. 

1. **Point de vue du `MessageController` (Cerveau) :**
   *"Je m'en fiche de savoir qui est la Vue. Que ce soit Swing, JavaFX, ou même une application Android, peu m'importe. Je la connais sous le nom neutre de `IMessageListView` et je lui passe la liste des messages. C'est elle qui se débrouille avec l'esthétique !"* 
   (C'est ce qu'on appelle "coder vers l'interface et non l'implémentation").

2. **Point de vue de `MessageListPanel` (Swing) :**
   Elle garantit au contrôleur l'implémentation du contrat `IMessageListView`. Quand le contrôleur ordonne la mise à jour, la Vue Swing la dessine dans un `DefaultListModel` (système de dessin de liste Swing).

3. **Point de vue de `MessageListPanelFx` (JavaFX) :**
   Elle garantit elle-aussi le même contrat. Quand le même contrôleur ordonne la mise à jour, la Vue JavaFX la dessine dans un `ListView` (système de dessin de liste JavaFX).

**Bilan :** Le code "intelligent" (la logique métier, l'authentification, le routage des messages) codé dans les Contrôleurs n'est **écrit qu'une seule et unique fois pour l'intégralité du projet**. Les deux interfaces (Swing/JavaFX) profitent de l'infrastructure Controller-Model sous-jacente gratuitement. 

---

## 3. Le Partage des Objets Transversaux (Lancement)

Pour faire transiter les messages de l'interface Swing vers l'interface JavaFX instantanément, l'astuce se situe dans la classe de point d'entrée de notre programme : `MessageAppLauncher.java`.

Lorsqu'on démarre l'application, nous faisons ceci dans l'ordre :

1. **Partage du Cerveau Global**
   Nous instancions un seul et unique objet **`DataManager`** de façon globale. Cet objet sera la base de référence partagée entre les deux lanceurs d'interface.  
   Le DataManager utilise d'ailleurs un **Pattern Observer** (Observateur) en arrière-plan. Sitôt qu'un message est reçu par le Mock ou la base de données, le fichier `ConsoleDatabaseObserver` ou le contrôleur concerné réagit pour mettre à jour sa propre IHM.

2. **Création d'espaces temporaires distincts (Sessions)**
   Même s'ils partagent la même "base de données", la personne connectée sur JavaFX n'est pas forcément la même que sur Swing. Une session utilisateur (`Session`) est donc créée spécifiquement pour l'instance de `MessageApp` (Swing) et une autre est créée pour l'instance `MessageAppFx` (JavaFX).

3. **Le Lancement Asynchrone (Multithreading)**
   - L'application JavaFX doit s'exécuter dans son propre thread système (le fameux `JavaFX Application Thread`) via la ligne protégée :
     `javafx.application.Application.launch(MessageAppFx.class, args);`
   - Mais cette méthode bloque le fil d'exécution ! Tant que JavaFX tourne, le reste du programme s'arrête.
   - Nous avons donc créé un fil d'exécution secondaire (`Thread`) chargé d'allumer l'application Swing au même instant. Les deux machines virtuelles graphiques (Swing et JavaFX) tournent donc "en parallèle", côte à côte. La liaison de données "en direct" entre les deux (les messages saisis qui apparaissent partout en même temps) provient du fait qu'elles reposent sur ce fameux et unique **`DataManager` partagé** en mémoire vive.

## Conclusion

Cette architecture MVC par interfaces est très réputée en industrie logicielle pour sa **Scalabilité** (il est très simple d'ajouter de nouvelles fonctionnalités) et sa **Maintenabilité** (changer JavaFX pour une application Web demain prendrait un rien de temps, il suffirait de redéfinir les classes d'interfaces graphiques Web en gardant nos Contrôleurs inchangés).

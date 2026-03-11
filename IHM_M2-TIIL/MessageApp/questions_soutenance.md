# Préparation à la Soutenance - Questions & Réponses

Ce document regroupe les questions potentielles que votre professeur (S.Lucas ou autre) pourrait vous poser lors de la soutenance du projet MessageApp, accompagnées des réponses attendues.

---

## 🏗️ Architecture Générale

### Q1. "Comment avez-vous structuré et séparé la logique métier de l'interface graphique (Swing et JavaFX) ?"
**Réponse attendue :** 
"L'application suit une architecture MVC (Modèle-Vue-Contrôleur) avec une stricte séparation entre le `core` (métier) et l'`ihm` (interface). Le modèle (Package `datamodel` et `core`) est la source de vérité. Les vues (Swing et JavaFX) ne connaissent pas directement la base de données de manière active ; elles passent par des **Contrôleurs** (ex: `MessageController`, `ChannelController`) pour faire des requêtes. Cela a permis d'implémenter JavaFX *en plus* de Swing sans modifier la logique d'envoi de messages."

### Q2. "Comment les interfaces (Swing/JavaFX) sont-elles notifiées de l'arrivée d'un nouveau message ?"
**Réponse attendue :**
"Nous utilisons le patron de conception **Observateur/Observable** (`IDatabaseObserver`). Le `DataManager` (qui contient la `Database`) avertit tous ses observateurs enregistrés via des méthodes comme `notifyMessageAdded()`. Les contrôleurs (ex: `MessageController`) implémentent cette interface. Lorsqu'ils sont notifiés, ils effectuent le tri, le filtrage, et demandent à la vue (Swing ou JavaFX) de rafraîchir la liste via un `Platform.runLater()` pour FX ou en direct sur l'EDT pour Swing."

### Q3. "Je vois que vous utilisez un dossier partagé. Comment l'application sait-elle qu'un fichier a été modifié par un autre PC ?"
**Réponse attendue :**
"L'application utilise la classe `WatchableDirectory`. Elle s'appuie sur un système de **polling** (via un Thread dédié exécutant la méthode `watchDirectory()`) qui vérifie l'état des fichiers (nouveaux, modifiés, supprimés). Lorsqu'un fichier `.msg` par exemple apparaît, le `EntityManager` le lit, crée un objet `Message` et l'ajoute à la `Database`. Cela déclenche le patron Observateur vu précédemment."

### Q4. "Pourquoi avez-vous fait face à des `ConcurrentModificationException` et comment l'avez-vous résolu ?"
**Réponse attendue :**
"Comme `WatchableDirectory` s'exécute sur un Thread d'arrière-plan, il peut ajouter un message ou un utilisateur à la base (dans un `HashSet`) au moment exact où le Thread de l'interface (Swing ou JavaFX) lit ce même `HashSet` pour afficher la liste. Pour rendre le code **Thread-safe**, j'ai remplacé les listes classiques par des collections concurrentes de Java 21, notamment `ConcurrentHashMap.newKeySet()` pour les données, et `CopyOnWriteArraySet` pour la liste des observateurs."

---

## ✨ Interfaces et UX (User Experience)

### Q5. "Comment avez-vous géré le problème de 'sélection croisée' (clic dans la liste utilisateurs, puis clic dans la liste des canaux) ?"
**Réponse attendue :**
"Pour que la barre de chat au centre sache toujours au bénéfice de *qui* on écrit, chaque liste doit annuler la sélection de l'autre. J'ai ajouté une interface `clearSelection()` aux vues des listes. Dans la vue globale (`MessageAppMainView`), lorsqu'un écouteur d'évènement détecte un clic sur un canal, j'appelle manuellement `userListPanel.clearSelection()` pour désélectionner visuellement les utilisateurs, évitant ainsi un état ambigu."

### Q6. "Comment avez-vous implémenté le système de thèmes dans JavaFX ?"
**Réponse attendue :**
"JavaFX permet de styliser les applications avec des CSS. J'ai créé trois fichiers `.css` (Dark, Light, Discord) qui redéfinissent les couleurs standards (background, text-fill, couleurs de sélection de ListView). Le changement de thème se fait dynamiquement à l'exécution en récupérant la `Scene` principale (`this.getScene()`) et en remplaçant l'URL du fichier de style dans sa liste `getStylesheets()`."

---

## 🎮 Fonctionnalités Spécifiques (Emojis & Easter Eggs)

### Q7. "Comment avez-vous implémenté la traduction des Emojis (`:smile:` -> 😊) ?"
**Réponse attendue :**
"J'ai créé une classe utilitaire (`EmojiUtils`) avec un constructeur privé pour empêcher l'instanciation, et une `Map` statique contenant les associations. Dans les composants d'affichage (le CellRenderer de Swing, et le ListCell de JavaFX), la méthode `replaceEmojis()` est appelée **avant** le rendu visuel. Les messages bruts dans la base de données restent inchangés (ils gardent `:smile:`), ce qui permet à n'importe quel client (même non mis à jour) de les lire. C'est du "view-side rendering"."

### Q8. "Vous avez créé un Easter Egg `/earthquake` qui fait trembler l'écran sur tous les clients. Comment ça marche concrètement ?"
**Réponse attendue :**
"Lorsqu'un message est lu par le `MessageController` (dans `notifyMessageAdded()`), le contrôleur vérifie si le texte correspond à une commande spéciale. Si c'est le cas, il fait appel à une méthode `triggerEasterEgg()` sur l'interface principale (`IMessageApp`). 
- Sur **Swing**, un `javax.swing.Timer` modifie rapidement la position X/Y de la `JFrame` parente.
- Sur **JavaFX**, j'utilise l'API d'animation (`TranslateTransition`) pour appliquer un mouvement sur la `Scene` racine. C'est asynchrone pour ne pas bloquer l'interface."

---

## 🔒 Sécurité et Qualité de code

### Q9. "Vous avez géré un problème appelé "Zombie Listeners". De quoi s'agit-il et comment l'éviter ?"
**Réponse attendue :**
"Dans une architecture Observer/Observable, si un objet graphique s'enregistre auprès du `DataManager` (qui a une durée de vie globale) mais que cet objet graphique est détruit (par exemple lors d'une déconnexion/reconnexion), il reste inscrit dans la mémoire du `DataManager` (Memory Leak) et continue de réagir en tâche de fond. J'ai ajouté des méthodes `.dispose()` sur les contrôleurs pour qu'ils fassent explicitement un `.removeObserver(this)` lors de la déconnexion."

### Q10. "Comment avez-vous géré les failles HTML sur Swing (XSS) ?"
**Réponse attendue :**
"Swing utilise des balises `<html>` pour formater le texte riche. Si un utilisateur envoyait un message contenant `<p>Texte</p>`, l'interface tentait de l'interpréter. Pire, un utilisateur malveillant aurait pu casser l'interface. J'ai implémenté une fonction `escapeHTML()` qui transforme préventivement les `<`, `>`, `&`, `"`, `'` en entités HTML (`&lt;`, `&gt;`...) avant affichage, et ce *avant* d'appliquer nos propres styles (comme les mentions `@`)."

---

> **Astuce pour la soutenance** : S'il y a du code que vous ne maitrisez pas à 100%, n'hésitez pas à lancer l'application, partager l'écran sur le moment de l'effet que vous essayez d'expliquer (par exemple, montrer une Mention `@` en direct), et décrire visuellement le comportement. Ça détourne le regard du code brut !

# Spécifications du Projet MessageApp

## Gestion des Utilisateurs

- [x] **SRS-MAP-USR-001** : L'utilisateur peut enregistrer un compte utilisateur dans le système (nom, tag).
- [x] **SRS-MAP-USR-002** : Lors de l'enregistrement d'un compte utilisateur, le tag et le nom de l'utilisateur sont obligatoires.
- [x] **SRS-MAP-USR-003** : Le tag correspondant à un utilisateur est unique dans le système.
- [x] **SRS-MAP-USR-004** : L'utilisateur peut se connecter sur un compte préalablement enregistré.
- [x] **SRS-MAP-USR-005** : L'utilisateur connecté peut se déconnecter de l'application.
- [x] **SRS-MAP-USR-007** : L'utilisateur connecté peut consulter la liste des utilisateurs enregistrés.
- [x] **SRS-MAP-USR-008** : L'utilisateur connecté peut rechercher un utilisateur enregistré.
- [x] **SRS-MAP-USR-009** : L'utilisateur connecté peut modifier son nom d'utilisateur.
- [x] **SRS-MAP-USR-010** : L'utilisateur connecté peut supprimer son compte.

## Gestion des Canaux

- [x] **SRS-MAP-CHN-001** : L'utilisateur connecté peut consulter la liste des canaux enregistrés.
- [x] **SRS-MAP-CHN-002** : L'utilisateur connecté peut rechercher un canal.
- [x] **SRS-MAP-CHN-003** : L'utilisateur connecté peut créer un canal public.
- [x] **SRS-MAP-CHN-004** : L'utilisateur connecté peut créer un canal privé.
- [x] **SRS-MAP-CHN-005** : L'utilisateur connecté peut quitter un canal privé dont il n'est pas le propriétaire.
- [x] **SRS-MAP-CHN-006** : L'utilisateur connecté peut supprimer un canal privé dont il est le propriétaire.
- [x] **SRS-MAP-CHN-007** : L'utilisateur connecté peut ajouter un utilisateur à un canal privé dont il est le propriétaire.
- [x] **SRS-MAP-CHN-087** : L'utilisateur connecté peut supprimer un utilisateur d'un canal privé dont il est le propriétaire.
- [ ] **SRS-MAP-CHN-009** : La présence d'un nouveau message dans un canal est signalée par un indicateur graphique.
- [ ] **SRS-MAP-CHN-010** : La présence en ligne d'un utilisateur est signalée par un indicateur graphique.

## Gestion des Messages

- [x] **SRS-MAP-MSG-001** : L'utilisateur connecté peut consulter les messages d'un canal public.
- [x] **SRS-MAP-MSG-002** : L'utilisateur connecté peut envoyer un message dans un canal public.
- [x] **SRS-MAP-MSG-003** : L'utilisateur connecté peut consulter les messages d'un canal privé dont il est membre.
- [x] **SRS-MAP-MSG-004** : L'utilisateur connecté peut envoyer un message dans un canal privé dont il est membre.
- [x] **SRS-MAP-MSG-005** : L'utilisateur connecté peut rechercher un message dans un canal.
- [x] **SRS-MAP-MSG-006** : L'utilisateur connecté peut supprimer un message dont il est l'auteur.
- [ ] **SRS-MAP-MSG-007** : L'utilisateur connecté peut envoyer un message privé à un utilisateur.
- [x] **SRS-MAP-MSG-008** : Le texte d'un message ne dépasse pas 200 caractères.
- [ ] **SRS-MAP-MSG-009** : Lors de la rédaction d'un message, l'utilisateur connecté peut mentionner un autre utilisateur du canal en utilisant le caractère '@'.
- [ ] **SRS-MAP-MSG-010** : Une notification avertit l'utilisateur connecté lorsqu'un utilisateur lui envoie un message direct ou lorsqu'il est mentionné dans un canal.

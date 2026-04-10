# TodoApp 📝

Application Android de gestion de tâches développée en Kotlin,
suivant l'architecture MVVM recommandée par Google.

## Aperçu

TodoApp permet de gérer ses tâches quotidiennes avec une interface
simple et intuitive. Les données sont persistées localement grâce
à Room — elles survivent aux redémarrages de l'appareil.

## Fonctionnalités

- Ajouter une tâche via un bouton flottant (+)
- Modifier une tâche (appui long sur la carte)
- Supprimer une tâche (icône corbeille)
- Cocher / décocher une tâche (texte barré automatiquement)
- Filtrer les tâches par onglets : Toutes / Actives / Terminées
- Message "liste vide" affiché si aucune tâche
- Persistance locale : les tâches restent après fermeture de l'app

## Architecture

Le projet suit le pattern MVVM (Model - View - ViewModel)
recommandé par Google pour les applications Android.

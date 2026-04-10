// Déclaration du package — correspond au dossier physique data/
package com.example.todoapp.data

// Import de l'annotation Entity : dit à Room que cette classe = une table SQL
import androidx.room.Entity
// Import de l'annotation PrimaryKey : définit la clé primaire de la table
import androidx.room.PrimaryKey

// @Entity : Room va créer une table SQL nommée "tasks" pour cette classe
// tableName = "tasks" → nom exact de la table dans SQLite
@Entity(tableName = "tasks")

// data class : type Kotlin spécial qui génère automatiquement
// equals(), hashCode(), toString() et copy() — très utile pour comparer des tâches
data class Task(

    // @PrimaryKey : chaque tâche a un identifiant unique
    // autoGenerate = true → SQLite incrémente automatiquement 1, 2, 3...
    // val id: Int = 0 → valeur par défaut 0, Room la remplace à l'insertion
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Le titre de la tâche — colonne "title" dans la table SQL
    // String non nullable → le titre est obligatoire
    val title: String,

    // Booléen qui indique si la tâche est terminée
    // = false → par défaut, toute nouvelle tâche est non terminée
    val isDone: Boolean = false
)
package com.example.todoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// @Database : déclare cette classe comme base de données Room
// entities = [Task::class] → liste des tables (ici une seule : tasks)
// version = 1 → numéro de version du schéma (à incrémenter si on modifie les tables)
// exportSchema = false → ne pas exporter le schéma en JSON (simplifie le projet)
@Database(entities = [Task::class], version = 1, exportSchema = false)

// Classe abstraite : Room génère l'implémentation concrète automatiquement
abstract class TaskDatabase : RoomDatabase() {

    // Méthode abstraite : Room fournit l'implémentation du DAO
    // C'est par cette méthode qu'on accède aux requêtes SQL
    abstract fun taskDao(): TaskDao

    // companion object = équivalent du "static" en Java
    // Permet d'appeler getDatabase() sans créer d'instance : TaskDatabase.getDatabase(context)
    companion object {

        // @Volatile : la valeur d'INSTANCE est toujours lue depuis la mémoire principale
        // et non depuis le cache du CPU — garantit la visibilité entre threads
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        // Fonction qui retourne toujours la même instance de la base (Singleton)
        // context : nécessaire pour créer la base de données sur l'appareil
        fun getDatabase(context: Context): TaskDatabase {

            // Si INSTANCE existe déjà, on la retourne directement
            // Le ?: (Elvis operator) exécute le bloc seulement si INSTANCE est null
            return INSTANCE ?: synchronized(this) {
                // synchronized : verrou — un seul thread peut créer la base à la fois
                // Évite que deux threads créent deux bases différentes en parallèle

                // Room.databaseBuilder : construit la base de données
                // context.applicationContext → contexte global, évite les fuites mémoire
                // TaskDatabase::class.java → classe de la base
                // "task_database" → nom du fichier SQLite sur l'appareil
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                ).build()

                // On sauvegarde l'instance pour les prochains appels
                INSTANCE = instance
                instance
            }
        }
    }
}
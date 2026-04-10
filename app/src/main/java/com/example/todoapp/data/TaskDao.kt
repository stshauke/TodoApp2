// Package de la couche données
package com.example.todoapp.data

// LiveData : conteneur observable — l'UI se met à jour automatiquement
// quand les données changent dans la base
import androidx.lifecycle.LiveData

// Import de toutes les annotations Room : @Dao, @Query, @Insert, @Update, @Delete
import androidx.room.*

// @Dao (Data Access Object) : interface que Room implémente automatiquement
// à la compilation via KAPT — on écrit les signatures, Room écrit le code SQL
@Dao
interface TaskDao {

    // @Query : requête SQL personnalisée
    // "SELECT * FROM tasks" → récupère toutes les colonnes de la table tasks
    // "ORDER BY id DESC" → les tâches les plus récentes apparaissent en premier
    // Retourne LiveData<List<Task>> → Room observe la table en temps réel :
    // dès qu'une tâche est ajoutée/modifiée/supprimée, l'UI est notifiée
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): LiveData<List<Task>>

    // @Insert : insère une nouvelle tâche dans la table
    // onConflict = REPLACE → si l'id existe déjà, remplace l'ancienne entrée
    // suspend : fonction asynchrone (coroutine) — ne bloque pas le thread UI
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    // @Update : met à jour une tâche existante en se basant sur son id
    // Utilisé pour cocher/décocher une tâche (isDone)
    // suspend : s'exécute dans une coroutine
    @Update
    suspend fun update(task: Task)

    // @Delete : supprime une tâche de la table par son id
    // suspend : s'exécute dans une coroutine
    @Delete
    suspend fun delete(task: Task)
}
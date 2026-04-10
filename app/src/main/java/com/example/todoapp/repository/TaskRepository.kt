package com.example.todoapp.repository

import androidx.lifecycle.LiveData
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskDao

// Le Repository est l'intermédiaire entre le ViewModel et la base de données
// Son rôle : abstraire la source de données
// Avantage : si demain on ajoute une API REST, seul ce fichier change —
// le ViewModel n'a pas besoin de savoir d'où viennent les données
class TaskRepository(private val taskDao: TaskDao) {
    // taskDao : injecté via le constructeur — le Repository ne crée pas la DB lui-même

    // allTasks : propriété publique exposant le LiveData du DAO
    // Le ViewModel y accède directement, sans appeler getAllTasks() lui-même
    // LiveData<List<Task>> → liste observable des tâches, mise à jour automatiquement
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    // suspend : doit être appelé depuis une coroutine (dans le ViewModel)
    // Délègue simplement au DAO — le Repository ne fait pas de logique ici
    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    // Met à jour une tâche existante (ex: cocher isDone)
    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    // Supprime une tâche de la base de données
    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }
}
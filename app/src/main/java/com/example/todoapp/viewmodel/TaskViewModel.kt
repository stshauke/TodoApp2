package com.example.todoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData  // ← remplace Transformations
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskDatabase
import com.example.todoapp.repository.TaskRepository
import kotlinx.coroutines.launch

/**
 * TaskViewModel : intermédiaire entre l'UI et le Repository
 * Survit aux rotations d'écran — conserve les données sans les recharger
 * Hérite de AndroidViewModel pour accéder au contexte Application (nécessaire pour la DB)
 */
class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    val allTasks: LiveData<List<Task>>

    init {
        val dao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(dao)
        allTasks = repository.allTasks
    }

    /**
     * addTask : crée une nouvelle tâche avec le titre donné
     * viewModelScope.launch → coroutine annulée automatiquement si le ViewModel est détruit
     * Task(title=title) → id=0 (auto-incrémenté par Room), isDone=false par défaut
     */
    fun addTask(title: String) {
        viewModelScope.launch {
            repository.insert(Task(title = title))
        }
    }

    /**
     * toggleDone : inverse l'état cochée/non-cochée d'une tâche
     * task.copy(isDone = !task.isDone) → crée une copie avec isDone inversé
     * Principe d'immutabilité : on ne modifie jamais l'objet original
     */
    fun toggleDone(task: Task) {
        viewModelScope.launch {
            repository.update(task.copy(isDone = !task.isDone))
        }
    }

    /**
     * deleteTask : supprime définitivement une tâche de la base
     */
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }

    /**
     * editTask : modifie le titre d'une tâche existante
     * task.copy(title = newTitle) → copie avec seulement le titre changé
     * id et isDone restent identiques
     */
    fun editTask(task: Task, newTitle: String) {
        viewModelScope.launch {
            repository.update(task.copy(title = newTitle))
        }
    }

    /**
     * getFilteredTasks : retourne un LiveData filtré selon le filtre actif
     *
     * Utilise MediatorLiveData qui "écoute" allTasks et
     * ré-émet une liste filtrée à chaque changement
     *
     * MediatorLiveData est plus explicite que Transformations.map
     * et ne nécessite pas d'import supplémentaire problématique
     *
     * @param filter : "all" → toutes | "active" → non terminées | "done" → terminées
     */
    fun getFilteredTasks(filter: String): LiveData<List<Task>> {
        // MediatorLiveData : LiveData qui peut observer d'autres LiveData
        // et transformer/combiner leurs valeurs
        val result = MediatorLiveData<List<Task>>()

        // addSource : observe allTasks et réagit à chaque nouvelle liste émise
        result.addSource(allTasks) { tasks ->
            // Applique le filtre selon le paramètre reçu
            result.value = when (filter) {
                // "active" → garde uniquement les tâches non terminées
                "active" -> tasks.filter { task -> !task.isDone }
                // "done" → garde uniquement les tâches terminées
                "done"   -> tasks.filter { task -> task.isDone }
                // "all" ou autre → retourne toutes les tâches sans filtre
                else     -> tasks
            }
        }
        return result
    }
}
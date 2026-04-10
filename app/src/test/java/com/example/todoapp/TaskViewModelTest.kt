package com.example.todoapp

import com.example.todoapp.data.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests unitaires purs — s'exécutent sur le PC sans émulateur
 *
 * On teste ici uniquement la LOGIQUE PURE sans Android :
 * - Le filtrage des tâches
 * - La transformation des données (copy, filter...)
 *
 * Pas besoin de Mockito ici car on teste des fonctions pures
 * qui ne dépendent d'aucun composant Android
 */
@ExperimentalCoroutinesApi
class TaskViewModelTest {

    /**
     * Test 1 : le filtre "active" ne retourne que les tâches non terminées
     *
     * Arrange → Act → Assert (AAA) : patron standard des tests unitaires
     * Arrange : prépare les données
     * Act     : exécute l'action à tester
     * Assert  : vérifie le résultat
     */
    @Test
    fun filter_active_returnsOnlyUndoneTasks() {
        // ARRANGE : liste mixte avec tâches actives et terminées
        val tasks = listOf(
            Task(id = 1, title = "Tâche active 1", isDone = false),
            Task(id = 2, title = "Tâche terminée", isDone = true),
            Task(id = 3, title = "Tâche active 2", isDone = false)
        )

        // ACT : applique le filtre "active"
        // Reproduit exactement la logique de getFilteredTasks("active")
        val filtered = tasks.filter { task -> !task.isDone }

        // ASSERT : 2 tâches actives attendues
        assertEquals(2, filtered.size)
        // Vérifie que toutes les tâches retournées sont bien non terminées
        assertEquals(true, filtered.all { !it.isDone })
    }

    /**
     * Test 2 : le filtre "done" ne retourne que les tâches terminées
     */
    @Test
    fun filter_done_returnsOnlyDoneTasks() {
        // ARRANGE
        val tasks = listOf(
            Task(id = 1, title = "Active",      isDone = false),
            Task(id = 2, title = "Terminée 1",  isDone = true),
            Task(id = 3, title = "Terminée 2",  isDone = true)
        )

        // ACT : filtre "done"
        val filtered = tasks.filter { task -> task.isDone }

        // ASSERT : 2 tâches terminées
        assertEquals(2, filtered.size)
        assertEquals(true, filtered.all { it.isDone })
    }

    /**
     * Test 3 : le filtre "all" retourne toutes les tâches sans exception
     */
    @Test
    fun filter_all_returnsAllTasks() {
        // ARRANGE
        val tasks = listOf(
            Task(id = 1, title = "Tâche 1", isDone = false),
            Task(id = 2, title = "Tâche 2", isDone = true),
            Task(id = 3, title = "Tâche 3", isDone = false)
        )

        // ACT : pas de filtre = toutes les tâches
        val filtered = tasks.filter { true }

        // ASSERT : toutes les 3 tâches sont présentes
        assertEquals(3, filtered.size)
    }

    /**
     * Test 4 : copy() inverse correctement isDone
     *
     * Vérifie que task.copy(isDone = !task.isDone)
     * fonctionne comme attendu — c'est le cœur de toggleDone()
     */
    @Test
    fun taskCopy_invertsIsDone() {
        // ARRANGE : tâche non terminée
        val task = Task(id = 1, title = "Faire le ménage", isDone = false)

        // ACT : simule ce que fait toggleDone()
        val toggled = task.copy(isDone = !task.isDone)

        // ASSERT : isDone doit maintenant être true
        assertEquals(true, toggled.isDone)
        // L'id et le titre ne doivent pas avoir changé
        assertEquals(task.id,    toggled.id)
        assertEquals(task.title, toggled.title)
    }

    /**
     * Test 5 : copy() met à jour correctement le titre
     *
     * Vérifie que task.copy(title = newTitle)
     * fonctionne comme attendu — c'est le cœur de editTask()
     */
    @Test
    fun taskCopy_updatesTitle() {
        // ARRANGE : tâche avec titre original
        val task = Task(id = 1, title = "Ancien titre", isDone = false)

        // ACT : simule ce que fait editTask()
        val edited = task.copy(title = "Nouveau titre")

        // ASSERT : titre mis à jour, id et isDone inchangés
        assertEquals("Nouveau titre", edited.title)
        assertEquals(task.id,     edited.id)
        assertEquals(task.isDone, edited.isDone)
    }

    /**
     * Test 6 : un titre vide ne devrait pas créer de tâche
     *
     * Vérifie la logique de validation dans showAddTaskDialog()
     * title.isNotEmpty() → condition qui bloque l'ajout si vide
     */
    @Test
    fun emptyTitle_shouldNotBeAdded() {
        // ARRANGE : titre vide (après trim())
        val title = "   ".trim()   // trim() supprime les espaces

        // ACT + ASSERT : isNotEmpty() doit retourner false
        assertEquals(false, title.isNotEmpty())
    }
}
package com.example.todoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskDao
import com.example.todoapp.data.TaskDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests du DAO avec base Room en mémoire
 *
 * Ces tests s'exécutent sur l'émulateur car Room
 * a besoin d'un Context Android pour fonctionner
 *
 * "In-memory" = base créée en RAM, détruite après chaque test
 * → isolation parfaite entre les tests
 * → pas de données parasites d'un test à l'autre
 *
 * @RunWith(AndroidJUnit4::class) : indique à JUnit d'utiliser
 * le runner Android (fournit le Context)
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    /**
     * InstantTaskExecutorRule : force LiveData à s'exécuter
     * de manière synchrone sur le thread de test
     * Sans cette règle, getOrAwaitValue() ne fonctionnerait pas
     */
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TaskDatabase
    private lateinit var taskDao: TaskDao

    /**
     * @Before : s'exécute avant CHAQUE test
     * Crée une base Room en mémoire fraîche
     */
    @Before
    fun createDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TaskDatabase::class.java
        )
            // allowMainThreadQueries : autorise les requêtes sur le
            // thread principal dans les tests (interdit en production)
            .allowMainThreadQueries()
            .build()

        taskDao = database.taskDao()
    }

    /**
     * @After : s'exécute après CHAQUE test
     * Ferme et détruit la base en mémoire
     */
    @After
    fun closeDatabase() {
        database.close()
    }

    /**
     * Test 1 : insérer une tâche et la retrouver
     */
    @Test
    fun insert_andRetrieve() = runTest {
        // ARRANGE : tâche à insérer
        val task = Task(title = "Acheter du lait", isDone = false)

        // ACT : insertion
        taskDao.insert(task)

        // ASSERT : récupère toutes les tâches et vérifie
        val result = taskDao.getAllTasks().getOrAwaitValue()
        assertEquals(1, result.size)
        assertEquals("Acheter du lait", result[0].title)
        assertEquals(false, result[0].isDone)
    }

    /**
     * Test 2 : supprimer une tâche → liste vide
     */
    @Test
    fun insert_thenDelete_listIsEmpty() = runTest {
        // ARRANGE : insère une tâche
        val task = Task(title = "Tâche temporaire")
        taskDao.insert(task)

        // Récupère la tâche avec son id auto-généré
        val inserted = taskDao.getAllTasks().getOrAwaitValue()[0]

        // ACT : supprime la tâche
        taskDao.delete(inserted)

        // ASSERT : la liste est maintenant vide
        val result = taskDao.getAllTasks().getOrAwaitValue()
        assertTrue("La liste devrait être vide", result.isEmpty())
    }

    /**
     * Test 3 : mettre à jour isDone d'une tâche
     */
    @Test
    fun update_isDone_changesToTrue() = runTest {
        // ARRANGE : tâche non terminée
        val task = Task(title = "Faire la vaisselle", isDone = false)
        taskDao.insert(task)
        val inserted = taskDao.getAllTasks().getOrAwaitValue()[0]

        // ACT : coche la tâche (copy avec isDone = true)
        taskDao.update(inserted.copy(isDone = true))

        // ASSERT : isDone est maintenant true
        val result = taskDao.getAllTasks().getOrAwaitValue()[0]
        assertEquals(true, result.isDone)
    }

    /**
     * Test 4 : insérer plusieurs tâches
     */
    @Test
    fun insertMultiple_retrieveAll() = runTest {
        // ARRANGE : 3 tâches
        taskDao.insert(Task(title = "Tâche 1"))
        taskDao.insert(Task(title = "Tâche 2"))
        taskDao.insert(Task(title = "Tâche 3"))

        // ASSERT : 3 tâches en base
        val result = taskDao.getAllTasks().getOrAwaitValue()
        assertEquals(3, result.size)
    }

    /**
     * Test 5 : modifier le titre d'une tâche
     */
    @Test
    fun update_title_changesCorrectly() = runTest {
        // ARRANGE : tâche avec titre original
        val task = Task(title = "Ancien titre")
        taskDao.insert(task)
        val inserted = taskDao.getAllTasks().getOrAwaitValue()[0]

        // ACT : modifie le titre
        taskDao.update(inserted.copy(title = "Nouveau titre"))

        // ASSERT : le titre a bien changé
        val result = taskDao.getAllTasks().getOrAwaitValue()[0]
        assertEquals("Nouveau titre", result.title)
    }
}
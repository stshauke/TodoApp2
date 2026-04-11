package com.example.todoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskDao
import com.example.todoapp.data.TaskDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests du DAO avec base Room en mémoire
 * Utilise runBlocking au lieu de runTest pour éviter
 * les problèmes de dispatcher dans les tests instrumentés
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TaskDatabase
    private lateinit var taskDao: TaskDao

    /**
     * @Before : crée une base Room en mémoire avant chaque test
     * inMemoryDatabaseBuilder → stocke tout en RAM
     * allowMainThreadQueries → autorise les requêtes sur le thread principal
     */
    @Before
    fun createDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TaskDatabase::class.java
        ).allowMainThreadQueries().build()
        taskDao = database.taskDao()
    }

    /**
     * @After : ferme la base après chaque test
     * Libère la mémoire et isole les tests entre eux
     */
    @After
    fun closeDatabase() {
        database.close()
    }

    /**
     * Test 1 : insérer une tâche et la retrouver
     * runBlocking : bloque le thread jusqu'à la fin de la coroutine
     */
    @Test
    fun insert_andRetrieve() = runBlocking {
        // ARRANGE : tâche à insérer
        val task = Task(title = "Acheter du lait", isDone = false)

        // ACT : insertion en base
        taskDao.insert(task)

        // ASSERT : récupère et vérifie
        val result = taskDao.getAllTasks().getOrAwaitValue()
        assertEquals(1, result.size)
        assertEquals("Acheter du lait", result[0].title)
        assertEquals(false, result[0].isDone)
    }

    /**
     * Test 2 : supprimer une tâche → liste vide
     */
    @Test
    fun insert_thenDelete_listIsEmpty() = runBlocking {
        // ARRANGE : insère une tâche
        val task = Task(title = "Tâche temporaire")
        taskDao.insert(task)
        val inserted = taskDao.getAllTasks().getOrAwaitValue()[0]

        // ACT : supprime
        taskDao.delete(inserted)

        // ASSERT : liste vide
        val result = taskDao.getAllTasks().getOrAwaitValue()
        assertTrue("La liste devrait être vide", result.isEmpty())
    }

    /**
     * Test 3 : cocher une tâche → isDone passe à true
     */
    @Test
    fun update_isDone_changesToTrue() = runBlocking {
        // ARRANGE : tâche non terminée
        val task = Task(title = "Faire la vaisselle", isDone = false)
        taskDao.insert(task)
        val inserted = taskDao.getAllTasks().getOrAwaitValue()[0]

        // ACT : coche la tâche
        taskDao.update(inserted.copy(isDone = true))

        // ASSERT : isDone = true
        val result = taskDao.getAllTasks().getOrAwaitValue()[0]
        assertEquals(true, result.isDone)
    }

    /**
     * Test 4 : insérer plusieurs tâches → toutes présentes
     */
    @Test
    fun insertMultiple_retrieveAll() = runBlocking {
        // ARRANGE + ACT : 3 insertions
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
    fun update_title_changesCorrectly() = runBlocking {
        // ARRANGE : titre original
        val task = Task(title = "Ancien titre")
        taskDao.insert(task)
        val inserted = taskDao.getAllTasks().getOrAwaitValue()[0]

        // ACT : nouveau titre
        taskDao.update(inserted.copy(title = "Nouveau titre"))

        // ASSERT : titre mis à jour
        val result = taskDao.getAllTasks().getOrAwaitValue()[0]
        assertEquals("Nouveau titre", result.title)
    }
}
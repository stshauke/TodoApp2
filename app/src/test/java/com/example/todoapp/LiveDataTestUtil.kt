package com.example.todoapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Fonction d'extension sur LiveData utilisée dans les tests
 *
 * Problème : LiveData est asynchrone — quand on appelle
 * taskDao.getAllTasks(), la valeur n'est pas disponible
 * immédiatement. Sans cet utilitaire, le test se terminerait
 * avant que LiveData ait émis sa valeur → test raté.
 *
 * Solution : getOrAwaitValue() bloque le thread de test
 * jusqu'à ce que LiveData émette une valeur, puis la retourne.
 *
 * @param time     : durée maximale d'attente (défaut 2)
 * @param timeUnit : unité de temps (défaut SECONDS)
 * @throws TimeoutException si LiveData n'émet rien dans le délai
 */
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null

    // CountDownLatch(1) : verrou qui se débloque après 1 émission
    val latch = CountDownLatch(1)

    // Observer temporaire : capture la valeur dès qu'elle arrive
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            data = value
            latch.countDown()           // débloque l'attente
            removeObserver(this)        // se retire pour ne pas fuiter
        }
    }

    // observeForever : observe sans lifecycle owner
    // (pas de Activity/Fragment dans les tests)
    observeForever(observer)

    // Attend au maximum 'time' secondes
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException(
            "LiveData n'a pas émis de valeur en $time $timeUnit"
        )
    }

    // Cast sûr car l'observer a forcément reçu une valeur non nulle
    @Suppress("UNCHECKED_CAST")
    return data as T
}
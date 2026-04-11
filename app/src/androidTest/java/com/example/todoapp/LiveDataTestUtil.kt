package com.example.todoapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            removeObserver(this)
        }
    }
    observeForever(observer)
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException(
            "LiveData n'a pas émis de valeur en $time $timeUnit"
        )
    }
    @Suppress("UNCHECKED_CAST")
    return data as T
}
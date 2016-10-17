package com.github.bmsantos

import com.github.bmsantos.data.strategy.DataStrategy
import com.github.bmsantos.data.strategy.LocalDataStrategy
import com.github.bmsantos.data.strategy.RemoteDataStrategy
import com.github.bmsantos.interfaces.ObservableHandler

class App : ObservableHandler {

    var dataSource: DataStrategy = RemoteDataStrategy
    val receivedItems:MutableList<String> = mutableListOf()

    override fun onObservable(observable: Observable, dataSource: DataStrategy) {
        this.dataSource.onClose()
        this.dataSource = dataSource
        observable.subscribe({ value ->
            println("Received Server Message - ${value}")
                logResult(value)
                receivedItems.add(value)
        }, {
            ev ->
            println("Error occurred (observable completed): " + ev)
        }, {
            println("Observable completed")
        })
    }

    fun switch() {
        if (dataSource is RemoteDataStrategy) {
            useLocalDataSource()
        } else if (dataSource is LocalDataStrategy) {
            useEventBusDataSource()
        }
    }

    fun useEventBusDataSource() {
        dataSource.stop()
        dataSource = RemoteDataStrategy
        dataSource.start(this)
    }

    fun useLocalDataSource() {
        dataSource.stop()
        dataSource = LocalDataStrategy()
        dataSource.start(this)
    }

    fun logResult(result: dynamic) {
        println("Received event message: ${result}")
    }
}
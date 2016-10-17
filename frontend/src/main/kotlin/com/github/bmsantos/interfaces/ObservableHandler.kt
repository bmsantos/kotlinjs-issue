package com.github.bmsantos.interfaces

import com.github.bmsantos.Observable
import com.github.bmsantos.data.strategy.DataStrategy

interface ObservableHandler {
    fun onObservable(observable: Observable, dataSource: DataStrategy)
}
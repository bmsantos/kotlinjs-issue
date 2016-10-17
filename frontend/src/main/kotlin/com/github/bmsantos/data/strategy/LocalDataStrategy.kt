package com.github.bmsantos.data.strategy

import com.github.bmsantos.App
import com.github.bmsantos.Rx

class LocalDataStrategy : DataStrategy {
    var app: App? = null

    fun createLocalGeneratorObservable(): dynamic {
        return Rx.Observable.interval(1000)
                .timeInterval()
                .map({ ti -> "Local second: ${ti.value()}" })
    }

    override fun start(app: App) {
        this.app = app as App?

        app.onObservable(createLocalGeneratorObservable(), this)
    }

    override fun stop() {
    }

    override fun isConnected(): Boolean {
        return true
    }

    override fun isDisconnected(): Boolean {
        return false
    }

    override fun onClose() {
    }
}
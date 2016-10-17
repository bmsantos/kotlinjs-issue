package com.github.bmsantos.data.strategy

import com.github.bmsantos.App

interface DataStrategy {

    fun start(app: App)

    fun stop()

    fun isConnected(): Boolean

    fun isDisconnected(): Boolean

    fun onClose()
}
package com.github.bmsantos

import kotlin.browser.window

object Main {
    private val app: App = App()

    fun start() {
        registerOnlineEventHandler()
        registerOfflineEventHandler()

        if (navigator.onLine ?: false) {
            app.useEventBusDataSource()
        } else {
            app.useLocalDataSource()
        }
    }

    fun registerOnlineEventHandler() {
        window.addEventListener("online", { event: dynamic ->
            println("You are online")
        }, false)
    }

    fun registerOfflineEventHandler() {
        window.addEventListener("offline", { event: dynamic ->
            console.log("You are offline")
            app.useLocalDataSource()
        }, false)
    }
}

fun main(args: Array<String>) {
    Main.start()
}
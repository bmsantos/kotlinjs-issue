package com.github.bmsantos.data.strategy

import com.github.bmsantos.App
import com.github.bmsantos.EventBus
import com.github.bmsantos.Rx
import com.github.bmsantos.navigator
import kotlin.browser.window

object RemoteDataStrategy : DataStrategy {
    var app: App? = null
    var eb: EventBus? = null

    fun createEventBusObservable(): dynamic {
        return Rx.Observable.create({ subscriber: dynamic ->
            RemoteDataStrategy.eb?.registerHandler("toClient", { err, msg ->
                subscriber.onNext(msg)
            })
        }).map({ msg: dynamic -> msg.body })
    }

    override fun start(app: App) {
        RemoteDataStrategy.app = app as App?

        eb = EventBus("/eventbus/")
        eb?.onclose = {
            window.setTimeout({
                if (isDisconnected()) {
                    start(app)
                }
            }, 3000)
            onClose()
        }
        eb?.onopen = {
            RemoteDataStrategy.app?.onObservable(createEventBusObservable(), this)
        }
    }

    override fun stop() {
        if (eb != null) {
            eb?.close()
            eb = null
        }
    }

    override fun isConnected(): Boolean {
        return !isDisconnected()
    }

    override fun isDisconnected(): Boolean {
        return navigator.onLine ?: false && (eb == null || eb?.state != EventBus.OPEN)
    }

    override fun onClose() {
        RemoteDataStrategy.app?.switch()
    }
}
package com.github.bmsantos

@native
object Observable {
    fun create(observable: (subscriber: dynamic) -> Unit): dynamic
    fun interval(ms: Int): Observable
    fun timeInterval(): Observable
    fun map(f: (dynamic) -> dynamic): Observable
    fun subscribe(onNext: (value: dynamic) -> Unit,
                  onError: (error: dynamic) -> Unit = {},
                  onComplete: () -> Unit = {}): dynamic
}

@native("Rx")
object Rx {
    val Observable: Observable
}

@native("EventBus")
class EventBus(val url: String, val options: Map<String, Any>? = null) {
    companion object {
        val CONNECTING = 0
        val OPEN = 1
        val CLOSING = 2
        val CLOSED = 3
    }

    val state: Int
    var onclose: (error: dynamic) -> Unit
    var onopen: () -> Unit
    fun close()
    fun publish(eventType: String, commandEvent: Json)
    fun registerHandler(eventHandler: String, any: (err: dynamic, msg: dynamic) -> Unit)
}

@native("navigator")
object navigator {
    var onLine: Boolean?
}
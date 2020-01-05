package ru.ostrovskal.sshstd

import android.os.Message

/**
 * @author Шаталов С.В.
 * @since 1.1.2
 */

/** Класс, реализующий объект исполнения с передачей сообщения */

open class RunnableMessage : Pool.PoolObject(null), Runnable {

    /** Сообщение, как параметр */
    @JvmField var msg: Message? = null

    companion object {
        // пул объектов
        private val pool = Pool(4) { RunnableMessage( ) }

        /** Получение из пула */
        fun obtain(message: Message) = pool.obtain().apply { msg = message }
    }

    /** Возврат в пул */
    fun recycle() { pool.recycle(this); msg?.recycle() }

    /** Исполнение */
    override fun run() { }
}
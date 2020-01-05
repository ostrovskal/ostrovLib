package ru.ostrovskal.sshstd

/**
 * @author Шаталов С.В.
 * @since 1.1.2
 */

/** Класс, реализующий пул объектов */

@Suppress("UNCHECKED_CAST")
class Pool<T : Pool.PoolObject>(count: Int, private val block: () -> T) {

    /** Класс, для реализации списка объектов пула
     *
     * @property next Ссылка на следующий
     */
    open class PoolObject(@JvmField var next: PoolObject?)

    // текущий объект из пула
    private var current: PoolObject? = null

    // пул объектов
    private val pool = List(count) { block().apply { next = current; current = this } }

    // объект синхронизации
    private val sync = Any()

    /** Получение объекта из пула */
    fun obtain(): T {
        synchronized(sync) {
            current?.run {
                val rm = current
                current = rm?.next
                return rm as T
            }
            return block()
        }
    }

    /** Возврат в пул */
    fun recycle(o: PoolObject) {
        synchronized(sync) {
            o.next = current
            current = o
        }
    }
}
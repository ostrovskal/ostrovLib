@file:Suppress("DEPRECATION")

package ru.ostrovskal.sshstd

import android.content.AsyncTaskLoader
import android.content.Context
import ru.ostrovskal.sshstd.sql.RecordSet
import ru.ostrovskal.sshstd.sql.StmtSelect

/**
 * @author Шаталов С.В.
 * @since  0.0.1
 */

/** Класс коннектора к БД, с DML оператором SELECT или SELECT JOIN
 *
 * @property stmt   DML оператор
 */
open class Connector(context: Context, private var stmt: StmtSelect?) : AsyncTaskLoader<RecordSet>(context) {
	
	/** Установка DML оператора [dml] (SELECT или SELECT JOIN) */
	open fun reload(dml: StmtSelect) { stmt = dml; forceLoad() }
	
	/** Загрузка курсора в фоновом потоке */
	override fun loadInBackground() = stmt?.execute()
}

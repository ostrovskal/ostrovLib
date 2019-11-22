@file:Suppress("NOTHING_TO_INLINE")

package ru.ostrovskal.sshstd.sql

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteCursorDriver
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteQuery
import ru.ostrovskal.sshstd.Common
import ru.ostrovskal.sshstd.Common.SQL_DDL_OPS
import ru.ostrovskal.sshstd.Common.dbVersion
import ru.ostrovskal.sshstd.utils.debug
import ru.ostrovskal.sshstd.utils.info
import ru.ostrovskal.sshstd.utils.join
import ru.ostrovskal.sshstd.utils.valid
import java.util.*

/**
 * @author  Шаталов С.В.
 * @since   0.4.0
 */


/** Базовый объект, реализующий построение SQL запроса */
object SQL: SQLiteDatabase.CursorFactory {
	/** Создание собственного курсора [RecordSet] */
	override fun newCursor(db: SQLiteDatabase, masterQuery: SQLiteCursorDriver, editTable: String?, query: SQLiteQuery): Cursor {
		return RecordSet(masterQuery, query, fieldsIndex)
	}
	
	// Карта полей для запроса SELECT
	private var fieldsIndex = mutableMapOf<Field<*>, Int>()
	
	/** Объект БД */
	@JvmField var db: SQLiteDatabase? = null
	
	/** Квотирование идентификатора [identity], совпадающего с ключевым словом SQL */
	@JvmStatic fun quote(identity: String) = if(identity.contains('.'))
	{ identity.split('.').joinToString(".") { quoteToken(it) } } else quoteToken(identity)
	
	@JvmStatic private fun quoteToken(token: String): String {
		val hash = token.toLowerCase(Locale.ROOT).hashCode()
		return if(Common.sqlKeywords.any { it == hash }) "\'$token\'" else token
	}
	
	/** Добавление списка [tables] таблиц в БД */
	@JvmStatic fun create(vararg tables: Table) {
		if(tables.isNotEmpty()) {
			for(table in tables) {
				// создать таблицу
				exec(table.create, SQL_DDL_OPS)
				// создать индексы
				// уникальные
				ddlIndex(table, true)
				// обычные
				ddlIndex(table, false)
			}
		}
	}
	
	private fun ddlIndex(table: Table, unique: Boolean) {
		val index = StringBuilder(32)
		val name = StringBuilder(32)
		for((fld, uni) in table.indices) {
			val qName = quote(fld.name)
			if(uni == unique) {
				name.join(fld.name, "_")
				index.join(qName)
			}
		}
		if(index.isNotEmpty()) {
			name.insert(0, "${table.name}_")
			index.insert(0, "CREATE ${if(unique) "UNIQUE " else ""}INDEX IF NOT EXISTS $name${if(unique) "_unique" else ""} ON ${quote(table.name)} (").append(")")
			exec(index.toString(), SQL_DDL_OPS)
		}
	}
	
	/** Удаление таблиц [tables] из БД */
	@JvmStatic fun drop(vararg tables: Table) {
		if(tables.isNotEmpty()) {
			for(table in tables) {
				exec(table.drop, SQL_DDL_OPS)
			}
		}
	}
	
	/** Разорвать соединение с БД */
	@JvmStatic fun disconnetion() {
		db?.close()
		db = null
	}
	
	/** Вернуть признак коннекта с БД */
	inline fun isConnection() = db?.isOpen == true
	
	/**
	 * Соединение с базой данных
	 *
	 * @param tables    Список таблиц БД
	 * @param reset     Признак перезапуска БД
	 */
	@JvmStatic fun connection(context: Context, reset: Boolean, vararg tables: Table): Boolean {
		var ok = false
		try {
			db = if(Common.logTag.isEmpty()) SQLiteDatabase.create(null)
			else {
				try {
					context.openOrCreateDatabase(Common.logTag, 0, this, null)
				}
				catch(ex: SQLiteException) {
					sqlLog(ex.message)
					val path = context.getDatabasePath(Common.logTag).path
					SQLiteDatabase.openDatabase(path, this, SQLiteDatabase.OPEN_READONLY, null)
				}
			}
		} catch(ex: SQLiteException) {
			sqlLog(ex.message)
		}
		db?.apply {
			ok = dbVersion == version
			if(ok) {
				// проверить на целостность
				ok = isDatabaseIntegrityOk
			}
			if(!ok || reset) {
				drop(*tables)
				create(*tables)
				ok = false
			}
			version = dbVersion
		}
		return ok
	}
	
	/** Выполнение внутреннего запроса [query] определенного типа [type] к БД */
	@JvmStatic fun exec(query: String, type: Int) = try {
		sqlLog(query, false)
		db?.run {
			compileStatement(query).run {
				when(type) {
					Common.SQL_DML_INSERT   -> executeInsert()
					Common.SQL_DML_DELETE,
					Common.SQL_DML_UPDATE   -> executeUpdateDelete().toLong()
					else                    -> { execute(); -1L }
				}
			}
		} ?: error("No connection Database!")
	} catch(ex: SQLException) {
		sqlLog(ex.message)
		-1L
	}
	
	/** Выполнение запроса [query] к БД */
	@SuppressLint("Recycle")
	@JvmStatic fun exec(query: String, list: ArrayList<Expression<*>>): RecordSet? = try {
		sqlLog(query, false)
		fieldsIndex = mutableMapOf()
		list.forEachIndexed { index, any ->
			val row: Field<*> = when(any) {
				is Function<*>      -> any.field
				is AliasField<*>    -> any.field!!
				is Field<*>         -> any
				else                -> error("$any has inadmissible type for a data sampling column!")
			}
			fieldsIndex[row] = index
		}
		db?.rawQuery(query, null).valid() as? RecordSet
	} catch(ex: SQLException) {
		sqlLog(ex.message)
		null
	}
	
	@JvmStatic private fun sqlLog(message: String?, isError: Boolean = true) {
		val ops = if(isError) "ERROR:" else "INFO: "
		val msg = "SQL $ops $message"
		if(isError) msg.info() else if(Common.isSqlLog) msg.debug()
	}
}

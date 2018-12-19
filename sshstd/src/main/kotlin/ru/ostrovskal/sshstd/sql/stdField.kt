@file:Suppress("NOTHING_TO_INLINE")

package ru.ostrovskal.sshstd.sql

import android.database.Cursor.FIELD_TYPE_FLOAT
import android.database.Cursor.FIELD_TYPE_STRING
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.utils.test

/**
 * @author  Шаталов С.В.
 * @since   0.4.3
 */

/** Класс, реализующий SQL поле таблицы
*
* @property table  Ссылка на родительскую таблицу
* @property name   Имя поля
* @param    type   Тип поля
* @property parent Родительское поле, применяется только при создании псевдонима
*/
open class Field<T>(@JvmField val table: Table, @JvmField val name: String, type: Int, @JvmField val parent: Field<*>? = null): Expression<T>(type) {
	/** Признак ограничения NOT NULL */
	inline val isNotNull get()             = mConstraint test DDL_FIELD_NOT_NULL
	
	/** Признак ограничения UNIQUE */
	inline val isUnique get()              = mConstraint test DDL_FIELD_UNIQUE
	
	/** Признак ограничения CHECK */
	inline val isChecked get()             = check != null
	
	/** Признак ограничения DEFAULT */
	inline val isDefVal get()              = defValue != null
	
	/** Признак ограничения AUTO_INCREMENT */
	inline val isAutoInc get()             = mConstraint test DDL_FIELD_AUTO_INCREMENT
	
	/** Признак ограничения первичного ключа */
	inline val isPK get()                  = idxPK != -1
	
	/** Признак внешнего ключа. Ссылка на поле */
	@JvmField var refs: Field<*>           = this
	
	/** Действие при удалении поля, ссылаемой записи */
	@JvmField var onDelete: String?        = null
	
	/** Действие при обновлении поля, ссылаемой записи */
	@JvmField var onUpdate: String?        = null
	
	/** Индекс первичного ключа */
	@JvmField var idxPK                    = -1
	
	/** Выражение CHECK */
	@JvmField var check: Op<Boolean>?      = null
	
	/** Значение по умолчанию */
	@JvmField var defValue: T?             = null
	
	/** Набор ограничений поля */
	var mConstraint                        = 0
		set(v)                             { field = field or v }
	
	/** Получение SQL типа поля */
	@JvmField val sqlType = when(type) {
		FIELD_TYPE_FLOAT        -> "REAL"
		FIELD_TYPE_INTEGER,
		FIELD_TYPE_TIMESTAMP    -> "INTEGER"
		FIELD_TYPE_STRING       -> "TEXT"
		FIELD_TYPE_BLOB         -> "BLOB"
		else                    -> "NULL"
	}
	
	/** DDL создания поля */
	val create get() = buildString {
		append("${SQL.quote(name)} $sqlType")
		// DEFAULT() AUTOINCREMENT UNIQUE NOT NULL CHECK()
		if(isDefVal) {
			append(" DEFAULT ${ExpressionParameter.argument(defValue, type)}")
		}
		else {
			if(!isPK) {
				if(isAutoInc) append(" AUTOINCREMENT")
				else if(isUnique) append(" UNIQUE")
			}
		}
		if(isNotNull) append(" NOT")
		append(" NULL")
		if(isChecked && !isPK && !isAutoInc) append(" CHECK ($check)")
	}
	
	/** Преобразование поля в строку формата Table.field */
	override fun toString()  = "${SQL.quote(table.name)}.${SQL.quote(name)}"
}

@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package ru.ostrovskal.sshstd.sql

import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteCursorDriver
import android.database.sqlite.SQLiteQuery
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.SqlField
import ru.ostrovskal.sshstd.utils.fmtTime
import ru.ostrovskal.sshstd.utils.join

/**
 * @author  Шаталов С.В.
 * @since   0.4.5
 */

/** Класс, реализующий взаимодействие с записями результата оператора SELECT
 * @property fields Карта полей оператора SELECT
 */
open class RecordSet(driver: SQLiteCursorDriver, query: SQLiteQuery, private val fields: Map<Field<*>, Int>) : SQLiteCursor( driver, null, query), Iterator<RecordSet>  {
	
	// Признак первой итерации
	private var iteration   = false
	
	/** Проверка на наличие следующей записи */
	override fun hasNext(): Boolean {
		if(iteration) moveToNext() else iteration = true
		return !isAfterLast
	}
	
	/** Следующая запись */
	override fun next() = this
	
	/** Вернуть значение поля|псевдонима [f] из курсора */
	operator fun <T> get(f: Field<T>) = run {
		val alias = getAliasOrThrow(f)
		val idx = fields[f] ?: error("Поле <${f.name}> не найдено!")
		when(alias.type) {
			SQL_FIELD_TYPE_TEXT     -> text(idx)
			SQL_FIELD_TYPE_REAL     -> real(idx)
			SQL_FIELD_TYPE_BLOB     -> blob(idx)
			SQL_FIELD_TYPE_INTEGER  -> integer(idx)
			SQL_FIELD_TYPE_TIMESTAMP-> timestamp(idx)
			else                	-> null
		}
	} as T
	
	private fun <T> getAliasOrThrow(field: Field<T>): Field<*> {
		val alias = fields.keys.firstOrNull { field.name == it.name && field.table == it.table } ?: error("Поле $field не найдено!")
		if(alias.parent != field.parent) error("Недопустимый псевдоним $alias для поля ${alias.parent} <> ${field.parent}")
		return alias
	}
	
	/** Автоматическое извлечение значений объекта из полей таблицы. Поля объекта должны быть аннотированны @SqlField(nameField) */
	fun autoValues(obj: Any) {
		obj.javaClass.fields.filter { it.isAnnotationPresent(SqlField::class.java) }.forEach {
			// берем имя из аннотации к полю
			var name = it.getAnnotation(SqlField::class.java)?.name
			// если имя пустое -> берем из поля
			if(name.isNullOrEmpty()) name = it.name
			// ищем имя в списке столбцов таблицы. если его нет - переходим к следуюшему
			columnNames.firstOrNull{ column -> column == name } ?: return@forEach
			it.isAccessible = true
			when (val o = it.get(obj)) {
				is Int 		-> it.setInt(o, integer(name))
				is Byte 	-> it.setByte(o, integer(name).toByte())
				is Long 	-> it.setLong(o, timestamp(name))
				is Char 	-> it.setChar(o, integer(name).toChar())
				is Short 	-> it.setShort(o, integer(name).toShort())
				is Float 	-> it.setFloat(o, real(name))
				is Double 	-> it.setDouble(o, real(name).toDouble())
				is Boolean 	-> it.setBoolean(o, boolean(name))
				is String 	-> it.set(o, text(name))
				is ByteArray-> it.set(o, blob(name))
				else 		-> error("Unknown field type of an object $it")
			}
		}
	}
	
	/** Вернуть DATE значение поля [field] */
	inline fun date(field: Field<*>) = datetime(field, "%d.%m.%Y")
	
	/** Вернуть TIME значение поля [field] */
	inline fun time(field: Field<*>) = datetime(field, "%H.%M.%S")
	
	/** Вернуть DATETIME значение поля [field] определенного формата [pattern] */
	fun datetime(field: Field<*>, pattern: String = "%d.%m.%Y %H:%M:%S"): String {
		val alias = getAliasOrThrow(field)
		if(alias.type < SQL_FIELD_TYPE_INTEGER) error("Для получения даты/времени поле <${field.name}> должно иметь интегральный тип!")
		return fmtTime(timestamp(getColumnIndexOrThrow(alias.name)), pattern)
	}
	
	/** Вернуть BOOLEAN значение поля по индексу [idx] */
	inline fun boolean(idx: Int) = getLong(idx) == 1L
	
	/** Вернуть BOOLEAN значение поля [name] */
	inline fun boolean(name: String) = boolean(getColumnIndexOrThrow(name))
	
	/** Вернуть BOOLEAN значение поля [field] */
	fun boolean(field: Field<*>) = boolean(getAliasOrThrow(field).name)
	
	/** Вернуть INTEGER значение поля по индексу [idx] */
	inline fun integer(idx: Int) = getInt(idx)
	
	/** Вернуть INTEGER значение поля [name] */
	inline fun integer(name: String) = integer(getColumnIndexOrThrow(name))
	
	/** Вернуть INTEGER значение поля [field] */
	fun integer(field: Field<*>) = integer(getAliasOrThrow(field).name)
	
	/** Вернуть TIMESTAMP значение поля по индексу [idx] */
	inline fun timestamp(idx: Int) = getLong(idx)
	
	/** Вернуть TIMESTAMP значение поля [name] */
	inline fun timestamp(name: String) = timestamp(getColumnIndexOrThrow(name))
	
	/** Вернуть TIMESTAMP значение поля [field] */
	fun timestamp(field: Field<*>) = timestamp(getAliasOrThrow(field).name)
	
	/** Вернуть FLOAT значение поля по индексу [idx] */
	inline fun real(idx: Int) = getFloat(idx)
	
	/** Вернуть FLOAT значение поля [name] */
	inline fun real(name: String) = real(getColumnIndexOrThrow(name))
	
	/** Вернуть FLOAT значение поля [field] */
	fun real(field: Field<*>) = real(getAliasOrThrow(field).name)
	
	/** Вернуть текстовое значение поля по индексу [idx] */
	inline fun text(idx: Int) = getString(idx) ?: ""
	
	/** Вернуть текстовое значение поля [name] */
	inline fun text(name: String) = text(getColumnIndexOrThrow(name))
	
	/** Вернуть текстовое значение поля [field] */
	fun text(field: Field<*>) = text(getAliasOrThrow(field).name)
	
	/** Вернуть BLOB значение поля по индексу [idx] */
	inline fun blob(idx: Int): ByteArray = getBlob(idx)
	
	/** Вернуть BLOB значение поля [name] */
	inline fun blob(name: String) = blob(getColumnIndexOrThrow(name))
	
	/** Вернуть BLOB значение поля [field] */
	fun blob(field: Field<*>) = blob(getAliasOrThrow(field).name)
	
	/** Преобразовать текущую запись в строку */
	override fun toString() = buildString {
		fields.forEach {
			val f = it.key
			join("$f = ${ExpressionParameter.argument(this@RecordSet[f], f.type)}")
		}
	}
}

/*
%d		день месяца: 00
%f		частичная секунда: SS.SSS
%H		час: 00-24
%j		день года: 001-366
%J		номер Юлианского дня
%m		месяц: 01-12
%M		минута: 00-59
%s		секунда начиная с 1970-01-01
%S		секунда: 00-59
%w		день недели 0-6 начиная с Воскресенья == 0
%W		неделя года: 00-53
%Y		год: 0000-9999
%%		%
 */

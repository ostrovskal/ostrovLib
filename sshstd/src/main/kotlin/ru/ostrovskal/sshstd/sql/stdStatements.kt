package ru.ostrovskal.sshstd.sql

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.SqlField
import ru.ostrovskal.sshstd.utils.join
import ru.ostrovskal.sshstd.utils.releaseRun

/**
 * @author  Шаталов С.В.
 * @since   0.4.4
 */

/** Класс, реализующий DML оператор для таблицы
 *
 * @property table  Таблица для которой применяется оператор
 * @property dml    Тип оператора
 */
open class Statement(@JvmField val table: Table, @JvmField val dml: Int) {
	
	// Выражение WHERE
	private var where: Op<Boolean>?     = null
	
	/** Список полей таблицы и установленных значений */
	@JvmField val values                = mutableMapOf<Field<*>, Any?>()
	
	/** Устанавливает значение [value] в поле [field] */
	open operator fun <T: Any?> set(field: Field<T>, value: T?) {
		if(field.isNotNull && value == null) error("Попытка установить NULL в NOT_NULL поле $field")
		values[field] = value
	}
	
	/** Автоматическое заполнение полей таблицы значениями из полей объекта [obj]. Поля объекта должны быть аннотированны @SqlField(nameField) */
	fun autoValues(obj: Any, vararg exclude: String) {
		obj.javaClass.fields.filter { it.isAnnotationPresent(SqlField::class.java) }.forEach {
			var name = it.getAnnotation(SqlField::class.java)?.name
			if(name.isNullOrEmpty()) name = it.name
			if(exclude.firstOrNull { fld -> fld == name } != null) return@forEach
			it.isAccessible = true
			val value: Any? = when(it.get(obj)) {
				is Int          -> it.getInt(obj)
				is Byte         -> it.getByte(obj)
				is Long         -> it.getLong(obj)
				is Char         -> it.getChar(obj)
				is Short        -> it.getShort(obj)
				is Float        -> it.getFloat(obj)
				is Double       -> it.getDouble(obj)
				is Boolean      -> it.getBoolean(obj)
				is String       -> it.get(obj) as String?
				is ByteArray    -> it.get(obj) as ByteArray?
				else            -> error("Unknown field type of an object $it")
			}
			table.columns.firstOrNull { column -> column.name == name }?.apply { values[this] = value }
		}
	}
	
	/** Формирует выражение WHERE */
	open fun where(op: SqlBuilder.()->Op<Boolean>) { where = SqlBuilder.op() }
	
	/** Квотирует имя таблицы, если необходимо */
	open fun quoteTable() = SQL.quote(table.name)
	
	/**
	 * Формирование шаблона SQL оператора, в зависимости от типа DML
	 *
	 * @param p1 Первый параметр
	 * @param p2 Второй параметр
	 *
	 * @return Возвращает сформированный оператор в SQL формате
	 */
	protected fun template(p1: String = "", p2: String = ""): String {
		val wh = if(where != null) " WHERE $where" else ""
		val tbl = quoteTable()
		return when(dml) {
			SQL_DML_SELECT  -> "SELECT $p1 FROM $tbl$wh$p2"
			SQL_DML_UPDATE  -> "UPDATE $tbl SET $p1$wh"
			SQL_DML_INSERT  -> "INSERT INTO $tbl ($p1) VALUES ($p2)"
			SQL_DML_DELETE  -> "DELETE FROM $tbl$wh"
			else            -> error("Неизвестный тип DML $dml")
		}
	}
}


/** Класс, реализующий DML оператор INSERT INTO */
open class StmtInsert(table: Table) : Statement(table, SQL_DML_INSERT) {
	/** Формирование оператора и исполнение запроса к БД с возвратом ID последней записи */
	fun execute() : Long {
		val fields = StringBuilder(32)
		val vals = StringBuilder(32)
		values.forEach {
			it.key.apply {
				fields.join(SQL.quote(name))
				vals.join(ExpressionParameter.argument(it.value, type))
			}
		}
		return SQL.exec(template(fields.toString(), vals.toString()), dml)
	}
}

/** Класс, реализующий DML оператор UPDATE */
open class StmtUpdate(table: Table): Statement(table, SQL_DML_UPDATE) {
	/** Формирование оператора и исполнение запроса к БД с возвратом количества обновленных строк */
	suspend fun execute() = withContext(Dispatchers.IO) {
		val vals = buildString {
			values.forEach {
				it.key.apply {
					join("${SQL.quote(name)} = ${ExpressionParameter.argument(it.value, type)}")
				}
			}
		}
		SQL.exec(template(vals), dml)
	}
}

/** Класс, реализующий DML оператор DELETE FROM */
open class StmtDelete(table: Table): Statement(table, SQL_DML_DELETE) {
	/** Формирование оператора и исполнение запроса к БД с возвратом количества удаленных строк */
	suspend fun execute() = withContext(Dispatchers.IO) { SQL.exec(template(), dml) }
}

/** Класс, реализующий DML оператор SELECT
 *
 * @property fields Список полей оператора
 */
open class StmtSelect(table: Table, @JvmField vararg val fields: Expression<*>): Statement(table, SQL_DML_SELECT) {
	
	// Список полей запроса в конструкции GROUP BY
	private val groupedByFields     = mutableListOf<Expression<*>>()
	
	// Список полей запроса в конструкции ORDER BY
	private var orderByExpressions  = mutableListOf<Pair<Expression<*>, Boolean>>()
	
	// Признак добавления в запрос HAVING
	private var having: Op<Boolean>?= null
	
	// Ограничитель запроса
	private var limit               = 0
	
	// При наличии ограничителя - параметр смещения
	private var offset              = 0
	
	// Признак наличия операции COUNT(*)
	private var count               = false
	
	/** Установка DISTINCT */
	@JvmField var distinct          = false
	
	/** Установка конструкции GROUP BY для полей [fields] */
	fun groupBy(vararg fields: Expression<*>) { groupedByFields += fields }
	
	/** Установка конструкции HAVING */
	fun having(op: SqlBuilder.() -> Op<Boolean>) { having = SqlBuilder.op() }
	
	/** Установка конструкции ORDER BY для поля [field] с направлением сортировки [isAsc] */
	fun orderBy(field: Expression<*>, isAsc: Boolean = true) { orderBy(field to isAsc) }
	
	/** Установка конструкции ORDER BY для полей [fields] */
	fun orderBy(vararg fields: Pair<Expression<*>, Boolean>) { orderByExpressions.addAll(fields.map{ it.first to it.second } ) }
	
	/** Установка конструкции ограничителя [num] запроса, начиная с записи [offs] */
	fun limit(num: Int, offs: Int = 0) {
		limit = num
		offset = offs
	}
	
	/** Установка признака операции COUNT(*) */
	fun count() { count = true }
	
	/** Возвращает строковое представление объекта */
	override fun toString(): String {
		val flds = buildString {
			if(count) {
				append("COUNT(*)")
			}
			else {
				if(distinct && fields.isNotEmpty()) {
					append("DISTINCT ")
				}
				if(fields.isEmpty()) append("*")
				else {
					append(fields.joinToString { it.toString() })
				}
			}
		}
		val args = buildString {
			if(!count) {
				if(groupedByFields.isNotEmpty()) {
					append(" GROUP BY ")
					append(groupedByFields.joinToString { it.toString() })
					having?.let {
						append(" HAVING $it")
					}
				}
				if(orderByExpressions.isNotEmpty()) {
					append(" ORDER BY ")
					append(orderByExpressions.joinToString { "${it.first} ${if(it.second) "ASC" else "DESC"}" })
				}
				if(limit > 0) {
					append(" LIMIT $limit" + if(offset > 0) " OFFSET $offset" else "")
				}
			}
		}
		return template(flds, args)
	}
	
	/** Формирование оператора и исполнение запроса к БД в фоновом потоке */
	suspend fun <T> execute(block: RecordSet.() -> T) = withContext(Dispatchers.IO) {
		SQL.exec(this@StmtSelect.toString(), arrayListOf(*fields))?.releaseRun(block)
	}
}

/**
 * Класс, реализующий DML оператор SELECT JOIN
 *
 * @param       table  Таблица слева
 * @property    other  Таблица справа
 * @property    type   Тип объединения
 * @param       fields Список полей для выборки
 */
open class SelectJoin(table: Table, @JvmField val other: Table, @JvmField val type: String, vararg fields: Expression<*>): StmtSelect(table, *fields) {
	/** Список пар ссылок на внешние ключи */
	@JvmField val joinParts           = ArrayList<JoinPart>()
	
	/** Выражение JOIN */
	@JvmField var join: Op<Boolean>?  = null
	
	/** Формирует ограничение JOIN */
	inline fun join(crossinline op: SqlBuilder.()->Op<Boolean>) { join = SqlBuilder.op() }
	
	/** Формирует выражение JOIN */
	override fun quoteTable() = buildString {
		// table1 JOIN table2 ON expr
		append(SQL.quote(table.name))
		if(join != null) {
			append(" $type JOIN ${SQL.quote(other.name)} ON $join")
		} else {
			for(p in joinParts) {
				append(" $type JOIN ${SQL.quote(other.name)}")
				if(type != DML_JOIN_CROSS) {
					append(" ON ")
					append(p.cond.joinToString(" AND ") { (pkColumn, fkColumn) -> "$pkColumn = $fkColumn" })
				}
			}
		}
	}
	
	/** Внутренний класс, реализующий представление внешних ссылок
	 *
	 * @property cond Условие операции
	 */
	class JoinPart(@JvmField val cond: List<Pair<Expression<*>, Expression<*>>>)
}

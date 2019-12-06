@file:Suppress("NOTHING_TO_INLINE")

package ru.ostrovskal.sshstd.sql

import ru.ostrovskal.sshstd.Common.*

/**
 * @author  Шаталов С.В.
 * @since   0.4.2
 */

/** Класс, реализующий SQL таблицу */
open class Table {
	/** Имя таблицы. Использует рефлексию для определения имени */
	open val name       get()           = javaClass.simpleName.removeSuffix("Table")
	
	/** Доступ к списку колонок только для чтения */
	val columns: List<Field<*>> get()   = fields
	
	// Список полей
	private val fields                  = ArrayList<Field<*>>()
	
	/** Список индексов */
	@JvmField val indices               = HashMap<Field<*>, Boolean>()
	
	/** DDL создания таблицы */
	val create get() = buildString {
		append("CREATE TABLE IF NOT EXISTS ${SQL.quote(name)}")
		if(fields.isNotEmpty()) {
			append(fields.joinToString(prefix = " (") { it.create })
			primaryKeyConstraint()?.let { append(", $it") }
			fields.filter { it.refs != it }.let {
				if(it.isNotEmpty()) {
					append(it.joinToString(prefix = ", ", separator = ", ") { fld ->
						"FOREIGN KEY (${fld.name}) REFERENCES ${fld.refs.table.name}(${fld.refs.name})" +
						if(fld.onDelete == null) "" else " ON DELETE ${fld.onDelete}" + if(fld.onUpdate == null) "" else " ON UPDATE ${fld.onUpdate}"
					} )
				}
			}
			append(")")
		}
	}
	
	/** DDL удаления таблицы */
	@JvmField val drop = buildString { append("DROP TABLE IF EXISTS ${SQL.quote(name)}") }
	
	/**
	 * Создание внешнего ключа для поля
	 *
	 * @param ref   Ссылка на поле другой таблицы
	 * @param onDel Действие при удалении ссылаемой записи
	 * @param onUpd Действие при обновлении ссылаемой записи
	 */
	fun <T, S : T, C : Field<S>> C.references(ref: Field<T>, onDel: String? = null, onUpd: String? = null): C = apply {
		refs = ref
		onDelete = onDel
		onUpdate = onUpd
	}
	
	/** Дабавление ограничения первичного ключа(ей) для таблицы */
	private fun primaryKeyConstraint(): String? {
		var pkey = fields.filter { it.isPK }//.sortedBy { it.idxPK }
		if(pkey.isEmpty()) pkey = fields.filter { it.isAutoInc }
		return if(pkey.isNotEmpty())
			pkey.joinToString(prefix = "CONSTRAINT ${SQL.quote("pk_$name")} PRIMARY KEY (", postfix = ")") { SQL.quote(it.name) }
		else null
	}
	
	/** Добавление ограничения AUTO_INCREMENT */
	inline val Field<Int>.autoIncrement get() = apply { mConstraint = DDL_FIELD_AUTO_INCREMENT; notNull }
	
	/** Добавление ограничения UNIQUE */
	inline val <T> Field<T>.unique      get() = apply { mConstraint = DDL_FIELD_UNIQUE }
	
	/** Добавление ограничения NOT NULL для поля */
	inline val <T> Field<T>.notNull     get() = apply { mConstraint = DDL_FIELD_NOT_NULL }
	
	/** Добавление ограничения CHECK для поля */
	inline fun <T> Field<T>.checked(op: SqlBuilder.(Field<T>)->Op<Boolean>) = apply { check = SqlBuilder.op(this); notNull }
	
	/** Добавление значения [def] для ограничения DEFAULT */
	inline fun <T> Field<T>.default(def: T) = apply { defValue = def; notNull }
	
	/** Добавление ограничения PRIMERY_KEY для поля */
	fun <T> Field<T>.primaryKey(idx: Int = -1) = apply {
		if(idx != -1 && table.fields.any { it.idxPK == idx }) error("Таблица ${this@Table.name} уже содержит Primary Key на $idx")
		idxPK = if(idx == -1) table.fields.count { it.isPK } + 1 else idx
		notNull
	}
	
	/** Добавление индекса для поля, с признаком уникальности [isUnique] */
	inline fun <T> Field<T>.index(isUnique: Boolean) = apply { indices[this] = isUnique }
	
	/** Добавление поля [name] типа INTEGER */
	fun integer(name: String): Field<Int> = registerField(name, SQL_FIELD_TYPE_INTEGER)
	
	/** Добавление поля [name] типа FLOAT */
	fun real(name: String): Field<Float> = registerField(name, SQL_FIELD_TYPE_REAL)
	
	/** Добавление поля [name] типа TEXT */
	fun text(name: String): Field<String> = registerField(name, SQL_FIELD_TYPE_TEXT)
	
	/** Добавление поля [name] типа BLOB */
	fun blob(name: String): Field<ByteArray> = registerField(name, SQL_FIELD_TYPE_BLOB)
	
	/** Добавление поля [name] типа TIMESTAMP */
	fun timestamp(name: String): Field<Long> = registerField(name, SQL_FIELD_TYPE_TIMESTAMP)
	
	/** Регистрация поля в списке таблицы */
	private fun <T> registerField(name: String, type: Int) = Field<T>(this, name, type).apply { fields.add(this) }
	
	/** DML оператор INSERT INTO */
	inline fun insert(crossinline body: StmtInsert.(StmtInsert) -> Unit) = StmtInsert(this).run {
		body(this)
		execute()
	}
	
	// DML оператор UPDATE
	suspend inline fun update(crossinline body: StmtUpdate.(StmtUpdate) -> Unit) = StmtUpdate(this).run {
		body(this)
		execute()
	}
	
	// DML оператор DELETE FROM для удаления записей с условием
	suspend inline fun delete(crossinline block: StmtDelete.() -> Unit) = StmtDelete(this).run {
		block()
		execute()
	}
	
	// DML оператор DELETE FROM для удаление всех записей
	suspend inline fun deleteAll() = StmtDelete(this).execute()
	
	/** DML оператор SELECT для полей [fields] */
	inline fun select(vararg fields: Expression<*>, body: (StmtSelect.() -> Unit)) = StmtSelect(this, *fields).apply { body() }
	
	/** DML оператор SELECT для списка полей [fields], без вложений */
	inline fun select(vararg fields: Expression<*>) = StmtSelect(this, *fields)
	
	/** DML оператор SELECT (INNER|CROSS|OUTER LEFT) JOIN ON, для таблицы [other] и списка полей [fields] */
	fun joinTable(other: Table, type: String, vararg fields: Expression<*>, body: (SelectJoin.() -> Unit)) = join(other, type, *fields, body = body)

	// Выполнение оператора JOIN
	private fun join(other: Table, type: String, vararg fields: Expression<*>, body: SelectJoin.() -> Unit) =
			SelectJoin(this, other, type, *fields).apply {
		fun findKeys(a: Table, b: Table): List<Pair<Field<*>, List<Field<*>>>>? {
			val pkToFKeys = a.fields.map { a_pk -> a_pk to b.fields.filter { it.refs == a_pk } }.filter { it.second.isNotEmpty() }
			return if(pkToFKeys.isNotEmpty()) pkToFKeys else null
		}
		body()
		if(join == null) {
			val isCross = type != DML_JOIN_CROSS
			val fkKeys = findKeys(this@Table, other) ?: findKeys(other, this@Table) ?: emptyList()
			when {
				isCross && fkKeys.isEmpty()                      -> error("Error. Restriction of FOREIGN_KEY is not found for the table ${other.name}")
				isCross && fkKeys.any { it.second.count() > 1 }  -> {
					val references = fkKeys.joinToString(" & ") { "${it.first} -> ${it.second.joinToString { fld -> fld.toString() }}" }
					error("Error. Several restrictions of PRIMARY_KEY are found <-> FOREIGN_KEY $references для таблицы ${other.name}")
				}
				else                                             -> joinParts.add(SelectJoin.JoinPart(fkKeys.map { it.first to it.second.single() }))
			}
		}
	}
	
	/** Проверка на существование */
	suspend fun exist(where: SqlBuilder.() -> Op<Boolean>) = StmtSelect(this).run {
		where { SqlBuilder.where() }
		execute { true } ?: false
	}
	
	/** Подсчет количества записей */
	suspend fun count(op: (SqlBuilder.() -> Op<Boolean>)? = null) = StmtSelect(this).run {
		count()
		if(op != null) where { SqlBuilder.op() }
		execute { integer(0) } ?: 0
	}
	
	// Формирование массива из содержимого таблицы по некоторому полю [field], с сортировкой [order] и типом сортировки [isAsc]
	suspend fun listOf(field: Field<String>, order: Field<*>, isAsc: Boolean, where: (SqlBuilder.() -> Op<Boolean>)? = null) = StmtSelect(this, field).run {
		orderBy(order, isAsc)
		if(where != null) where { SqlBuilder.where() }
		execute { List(count) { hasNext(); next()[field] } } ?: listOf()
	}
}

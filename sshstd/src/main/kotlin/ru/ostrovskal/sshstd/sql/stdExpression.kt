package ru.ostrovskal.sshstd.sql

import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.utils.toBlob

/**
 * @author  Шаталов С.В.
 * @since   0.4.1
 */

/** Реализация функции LENGTH( поле ) */
fun <T: String?> Field<T>.length() = StdFunc(this, "LENGTH", SQL_FIELD_TYPE_INTEGER)

/** Реализация функции LOWER( поле ) */
fun<T: String?> Field<T>.lowerCase() = StdFunc(this, "LOWER")

/** Реализация функции UPPER( поле ) */
fun<T: String?> Field<T>.upperCase() = StdFunc(this, "UPPER")

/** Реализация функции COUNT( поле ) */
fun<T: Any?> Field<T>.count() = StdFunc(this, "COUNT", SQL_FIELD_TYPE_INTEGER)

/** Реализация функции SUM( поле ) */
fun<T: Number?> Field<T>.sum() = StdFunc(this, "SUM", type)

/** Реализация функции MAX( поле ) */
fun<T: Number?> Field<T>.max() = StdFunc(this, "MAX", type)

/** Реализация функции MIN( поле ) */
fun<T: Number?> Field<T>.min() = StdFunc(this, "MIN", type)

/** Реализация функции AVG( поле ) */
fun<T: Number?> Field<T>.avg() = StdFunc(this, "AVG", type)

/** Реализация функции AS - псевдоним поля */
infix fun <T> Expression<T>.AS(alias: String) = AliasField(this, alias)

/** Объект, реализующий построение SQL выражений */
object SqlBuilder {
	/** Реализация операции AND */
	@JvmStatic infix fun Op<Boolean>.and(op: Expression<Boolean>): Op<Boolean> = LogicOp(this, op, "AND")
	
	/** Реализация операции OR */
	@JvmStatic infix fun Op<Boolean>.or(op: Expression<Boolean>): Op<Boolean> = LogicOp(this, op, "OR")
	
	/** Реализация операции NOT */
	@JvmStatic fun not(op: Expression<Boolean>): Op<Boolean> = CompareOp(op, null, "NOT")
	
	/** Реализация операции IN ( список значений ) */
	@JvmStatic infix fun<T> Expression<T>.IN(list: Iterable<T>): Op<Boolean> = InListOrNotInListOp(this, list, true)
	
	/** Реализация операции NOT IN ( список значений ) */
	@JvmStatic infix fun<T> Expression<T>.notIN(list: Iterable<T>): Op<Boolean> = InListOrNotInListOp(this, list, false)
	
	/** Реализация операции BETWEEN, диапазона from..to */
	@JvmStatic fun<T : Number> Expression<out T>.between(from: T, to: T): Op<Boolean> = BetweenOp(this, from, to)
	
	/** Реализация подзапроса */
	@JvmStatic fun <T> Expression<T>.select(stmt: StmtSelect, ops: String? = null) : Op<Boolean> = SelectOp(this, stmt, ops)
	
	/** Реализация операции IS NULL */
	@JvmStatic fun <T> Expression<T>.isNull(): Op<Boolean> = CompareOp(this, null, "IS NULL", false)
	
	/** Реализация операции IS NOT NULL */
	@JvmStatic fun <T> Expression<T>.isNotNull(): Op<Boolean> = CompareOp(this, null, "IS NOT NULL", false)
	
	/** Реализация операции = ( параметр ) */
	@JvmStatic infix fun <T> Expression<T>.eq(t: T): Op<Boolean> = CompareOp(this, ExpressionParameter(t, type), "=")
	
	/** Реализация операции = ( выражение ) */
	@JvmStatic infix fun <T, S: T> Expression<T>.eq(other: Expression<S>) : Op<Boolean> = CompareOp(this, other, "=")
	
	/** Реализация операции != ( параметр ) */
	@JvmStatic infix fun <T> Expression<T>.neq(t: T): Op<Boolean> = CompareOp(this, ExpressionParameter(t, type), "!=")
	
	/** Реализация операции != ( выражение ) */
	@JvmStatic infix fun <T, S: T> Expression<T>.neq(other: Expression<S>) : Op<Boolean> = CompareOp(this, other, "!=")
	
	/** Реализация операции < ( параметр ) */
	@JvmStatic infix fun <T, S: T> Expression<S>.ls(t: T) : Op<Boolean> = CompareOp(this, ExpressionParameter(t, type), "<")
	
	/** Реализация операции < ( выражение ) */
	@JvmStatic infix fun <T, S: T> Expression<S>.ls(other: Expression<S>) = CompareOp(this, other, "<")
	
	/** Реализация операции <= ( параметр ) */
	@JvmStatic infix fun <T, S: T> Expression<S>.lseq(t: T) : Op<Boolean> = CompareOp(this, ExpressionParameter(t, type), "<=")
	
	/** Реализация операции <= ( выражение ) */
	@JvmStatic infix fun <T, S: T> Expression<S>.lseq(other: Expression<S>) : Op<Boolean> = CompareOp(this, other, "<=")
	
	/** Реализация операции > ( параметр ) */
	@JvmStatic infix fun <T, S: T> Expression<S>.gt(t: T) : Op<Boolean> = CompareOp(this, ExpressionParameter(t, type), ">")
	
	/** Реализация операции > ( выражение ) */
	@JvmStatic infix fun <T, S: T> Expression<S>.gt(other: Expression<S>) : Op<Boolean> = CompareOp(this, other, ">")
	
	/** Реализация операции >= ( параметр ) */
	@JvmStatic infix fun <T, S: T> Expression<S>.gteq(t: T) : Op<Boolean> = CompareOp(this, ExpressionParameter(t, type), ">=")
	
	/** Реализация операции >= ( выражение ) */
	@JvmStatic infix fun <T, S: T> Expression<S>.gteq(other: Expression<T>) : Op<Boolean> = CompareOp(this, other, ">=")
	
	/** Реализация операции LIKE ( выражение ) */
	@JvmStatic infix fun <T:String?> Expression<T>.like(pattern: String): Op<Boolean> =
			CompareOp(this, ExpressionParameter(pattern, SQL_FIELD_TYPE_TEXT), "LIKE")
	
	/** Реализация операции NOT LIKE ( выражение ) */
	@JvmStatic infix fun <T:String?> Expression<T>.notLike(pattern: String): Op<Boolean> =
			CompareOp(this, ExpressionParameter(pattern, SQL_FIELD_TYPE_TEXT), "NOT LIKE")
	
	/** Реализация операции + ( выражение ) */
	@JvmStatic infix operator fun <T, S: T> Expression<T>.plus(other: Expression<S>) : Expression<T> = ExpressionOp(this, other, "+")
	
	/** Реализация операции + ( параметр ) */
	@JvmStatic infix operator fun <T> Expression<T>.plus(t: T) : Expression<T> = ExpressionOp(this, ExpressionParameter(t, type), "+")
	
	/** Реализация операции - ( выражение ) */
	@JvmStatic infix operator fun <T, S: T> Expression<T>.minus(other: Expression<S>) : Expression<T> = ExpressionOp(this, other, "-")
	
	/** Реализация операции - ( параметр ) */
	@JvmStatic infix operator fun <T> Expression<T>.minus(t: T) : Expression<T> = ExpressionOp(this, ExpressionParameter(t, type), "-")
	
	/** Реализация операции * ( выражение ) */
	@JvmStatic infix operator fun <T, S: T> Expression<T>.times(other: Expression<S>) : Expression<T> = ExpressionOp(this, other, "*")
	
	/** Реализация операции * ( параметр ) */
	@JvmStatic infix operator fun <T> Expression<T>.times(t: T) : Expression<T> = ExpressionOp(this, ExpressionParameter(t, type), "*")
	
	/** Реализация операции / ( выражение ) */
	@JvmStatic infix operator fun <T, S: T> Expression<T>.div(other: Expression<S>) : Expression<T> = ExpressionOp(this, other, "/")
	
	/** Реализация операции / ( параметр ) */
	@JvmStatic infix operator fun <T> Expression<T>.div(t: T) : Expression<T> = ExpressionOp(this, ExpressionParameter(t, type), "/")
}

/** Класс, реализующий общий интерфейс выражений
 *
 * @property type Тип выражения
 */
abstract class Expression<T>(@JvmField val type: Int = SQL_FIELD_TYPE_NULL) {
	
	// Ленивое получение хэша
	private val _hashCode by lazy { toString().hashCode() }
	
	/** Операция сравнения выражений */
	override fun equals(other: Any?) = (other as? Expression<*>)?.toString() == toString()
	
	/** Получение хэша */
	override fun hashCode() = _hashCode
}

/** Класс, реализующий общий интерфейс операций */
abstract class Op<T> : Expression<T>()

/** Класс, реализующий общий интерфейс функций */
abstract class Function<T>(f: Field<T>, tp: Int) : Expression<T>() {
	/** Поле, созданное на основании родительского поля и типа SQL функции */
	@JvmField val field = Field<T>(f.table, f.name, tp)
}

/** Класс, реализующий общий интерфейс псевдонимов */
abstract class Alias<T> : Expression<T>()

/** Класс, реализующий параметр выражения
 *
 * @property value Значение параметра
 */
class ExpressionParameter<T>(@JvmField val value: T, type: Int) : Expression<T>(type) {
	companion object {
		/**  Создает строку по типу выражения */
		fun <T> argument(value: T, type: Int) = when(type) {
			SQL_FIELD_TYPE_TEXT -> "\'$value\'"
			SQL_FIELD_TYPE_BLOB -> (value as? ByteArray)?.toBlob() ?: "x\'00\'"
			else            	-> value.toString()
		}
	}
	
	/** Преобразует параметр в строку определенного типа */
	override fun toString() = argument(value, type)
}

/** Класс, реализующий арифметические операции
 *
 * @property expr1  Выражение слева
 * @property expr2  Выражение справа
 * @property opSign Тип операции
 */
class ExpressionOp<T, S: T>(@JvmField val expr1: Expression<T>, @JvmField val expr2: Expression<S>?, @JvmField val opSign: String) : Expression<T>() {
	/** Получение текста в SQL формате из параметров объекта */
	override fun toString() = if(expr2 == null) "$opSign($expr1)" else "$expr1 $opSign $expr2"
}

/**
 * Класс, реализующий операции сравнения
 *
 * @property expr1   Выражение слева
 * @property expr2   Выражение справа
 * @property opSign  Операция
 * @property forward Направление операции
 */
open class CompareOp(@JvmField val expr1: Expression<*>, @JvmField val expr2: Expression<*>?, @JvmField val opSign: String, @JvmField val forward: Boolean = true) :
		Op<Boolean>() {
	/** Получение текста в SQL формате из параметров объекта */
	override fun toString() = buildString {
		if(expr2 == null) {
			append(if(forward) "$opSign($expr1)" else "$expr1 $opSign")
		} else {
			if(expr1 is LogicOp) append("($expr1)") else append(expr1)
			append(" $opSign ")
			if(expr2 is LogicOp) append("($expr2)") else append(expr2)
		}
	}
}

/** Класс, реализующий логические операции AND или OR
 *
 * @property expr1 Выражение слева
 * @property expr2 Выражение справа
 */
class LogicOp(expr1: Expression<Boolean>, expr2: Expression<Boolean>, sign: String) : CompareOp(expr1, expr2, sign)

/** Класс, реализующий операцию BETWEEN
 *
 * @property expr   Выражение слева
 * @property from   Начальное значение диапазона
 * @property to     Конечное значение диапазона
 */
class BetweenOp<out T>(@JvmField val expr: Expression<out T>, @JvmField val from: T, @JvmField val to: T): Op<Boolean>() {
	/** Получение текста в SQL формате из параметров объекта */
	override fun toString() = "$expr BETWEEN $from AND $to"
}

/** Класс, реализующий операцию NOT IN список
 *
 * @property expr       Выражение слева
 * @property list       Список
 * @property isInList   Признак отрицания
 */
class InListOrNotInListOp<T>(@JvmField val expr: Expression<T>, @JvmField val list: Iterable<T>, @JvmField val isInList: Boolean = true): Op<Boolean>() {
	/** Получение текста в SQL формате из параметров объекта */
	override fun toString() = buildString {
		list.iterator().let {
			append("$expr " + if(isInList) "IN (" else "NOT IN (")
			append(list.joinToString(postfix = ")") { fld -> ExpressionParameter.argument(fld, expr.type) } )
		}
	}
}

/** Класс, реализующий стандартные SQL функции
 *
 * @property func Имя функции
 * @param	 tp Тип функции
 */
class StdFunc<T>(f: Field<T>, @JvmField val func: String, tp: Int = SQL_FIELD_TYPE_TEXT): Function<T>(f, tp) {
	/** Получение текста в SQL формате из параметров объекта */
	override fun toString() = "$func($field)"
}

/** Класс, реализующий псевдоним
 *
 * @property expr   Выражение слева
 * @property name   Имя псевдонима
 */
class AliasField<T>(@JvmField val expr: Expression<T>, @JvmField val name: String): Alias<T>() {
	/** Поле псевдоним */
	@JvmField var field: Field<T>?        = null
	
	init {
		val p = when(expr) {
			is Function<T> -> expr.field
			is Field<T>    -> expr
			else           -> error("Псевдоним может быть только у поля!")
		}
		if(name == p.name) error("Псевдоним совпадает с именем поля. Указывает сам на себя :)")
		field = Field(p.table, name, p.type, p)
	}
	
	/** Получение текста в SQL формате из параметров объекта */
	override fun toString() = "$expr AS $name"
}

/** Класс, реализующий подзапрос
 *
 * @property expr   Выражение
 * @property stmt   Подзапрос
 * @property ops    Операция
 */
class SelectOp<T>(@JvmField val expr: Expression<T>, @JvmField val stmt: StmtSelect, @JvmField val ops: String? = null): Op<Boolean>() {
	/** Получение текста в SQL формате из параметров объекта */
	override fun toString(): String {
		val prefs = when(ops) {
			DML_SUB_SELECT_EXISTS,
			DML_SUB_SELECT_NOT_EXISTS  -> ops
			DML_SUB_SELECT_IN,
			DML_SUB_SELECT_NOT_IN      -> "$expr $ops"
			else                       -> expr.toString()
		}
		return "$prefs ($stmt)"
	}
}

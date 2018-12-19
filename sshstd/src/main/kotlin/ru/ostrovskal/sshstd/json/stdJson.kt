package ru.ostrovskal.sshstd.json

import ru.ostrovskal.sshstd.Common.namePackage
import ru.ostrovskal.sshstd.JsonAdapter
import ru.ostrovskal.sshstd.JsonName
import ru.ostrovskal.sshstd.utils.escape

/**
 * @author Шаталов С.В.
 * @since 0.5.0
 *
*/

/** Интерфейс, реализующий адаптер для преобразования элемента */
interface JAdapter {
	/** Сериализация */
	fun serialize(value: Any?): Any?
	/** Десериализация */
	fun deserialize(value: String): Any?
}

/** Класс, реализующий сериализацию/десериализацию в формат JSON
 *
 * @property filter Класс аннотации для фильтрации полей
 */
open class Json(private val filter: Class<out Annotation>) {
	
	// Кэш адаптеров
	private val adapterCache = mutableMapOf<String, JAdapter>()
	
	// Объединение сериализуемых значений в строку
	private fun <T> Iterable<T>.joinToStringBuilder(stringBuilder: StringBuilder, prefix: CharSequence = "",
	                                                postfix: CharSequence = "", callback: ((T) -> Unit)? = null): StringBuilder {
		return joinTo(stringBuilder, ", ", prefix, postfix) {
			if (callback == null) return@joinTo it.toString()
			callback(it)
			""
		}
	}
	
	/** Сериализация полей объекта */
	open fun serialize(o: Any): String = buildString { serializeObject(o) }
	
	/** Десериализация полей объекта */
	open fun deserialize(o: Any, json: String) {
/*
		val stk = Parser.parse(json).apply { printDebug(0) }
		val clazz = o.javaClass
		if(clazz.isLocalClass) return
		clazz.declaredFields.filter { it.isAnnotationPresent(filter) }.forEach {
			val jsonName = it.getAnnotation(JsonName::class.java)
			val jsonAdapter = it.getAnnotation(JsonAdapter::class.java)
			val propName = jsonName?.name ?: it.name
			val adapter = jsonAdapter?.run {
				val name = jsonAdapter.adapterClass
				var adapter = adapterCache[name]
				if(adapter == null) {
					adapter = Class.forName("$namePackage.$name")?.newInstance() as? JAdapter
					if(adapter != null) adapterCache[name] = adapter
				}
				adapter
			}
			it.isAccessible = true
			//setializeValue(it.get(o), adapter)
		}
*/
	}
	
	// Внутренняя операция сериализации объекта
	private fun StringBuilder.serializeObject(o: Any) {
		val clazz = o.javaClass
		if(clazz.isLocalClass) return
		clazz.declaredFields.filter { it.isAnnotationPresent(filter) }.joinToStringBuilder(this, prefix = "{", postfix = "}") {
			val jsonName = it.getAnnotation(JsonName::class.java)
			val jsonAdapter = it.getAnnotation(JsonAdapter::class.java)
			val propName = jsonName?.name ?: it.name
			val adapter = jsonAdapter?.run {
				val name = jsonAdapter.adapterClass
				var adapter = adapterCache[name]
				if(adapter == null) {
					adapter = Class.forName("$namePackage.$name").newInstance() as? JAdapter
					if(adapter != null) adapterCache[name] = adapter
				}
				adapter
			}
			serializeEscapeString(propName)
			append(": ")
			it.isAccessible = true
			setializeValue(it.get(o), adapter)
		}
	}
	
	// Сериализация значение объекта с применением адаптера
	private fun StringBuilder.setializeValue(value: Any?, adapter: JAdapter?) {
		var nAdapter = adapter
		when(val nValue = adapter?.serialize(value)?.apply { nAdapter = null } ?: value) {
			null            -> append("null")
			is String       -> serializeEscapeString(nValue)
			is Number,
			is Boolean      -> append(nValue.toString())
			is IntArray     -> serializeList(nValue.toList(), nAdapter)
			is LongArray    -> serializeList(nValue.toList(), nAdapter)
			is ByteArray    -> serializeList(nValue.toList(), nAdapter)
			is ShortArray   -> serializeList(nValue.toList(), nAdapter)
			is FloatArray   -> serializeList(nValue.toList(), nAdapter)
			is DoubleArray  -> serializeList(nValue.toList(), nAdapter)
			is BooleanArray -> serializeList(nValue.toList(), nAdapter)
			is Array<*>     -> serializeList(nValue.toList(), nAdapter)
			is List<*>      -> serializeList(nValue, nAdapter)
			else            -> serializeObject(nValue)
		}
	}
	
	// Сериализация списка с адаптером для значений
	private fun StringBuilder.serializeList(data: List<Any?>, adapter: JAdapter?) {
		data.joinToStringBuilder(this, "[", "]") {
			setializeValue(it, adapter)
		}
	}
	
	// Применение экренирования для значений
	private fun StringBuilder.serializeEscapeString(s: String) {
		append('\"')
		s.forEach { append(it.escape()) }
		append('\"')
	}
}

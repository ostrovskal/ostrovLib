package ru.ostrovskal.sshstd.objects

import android.annotation.SuppressLint
import android.content.SharedPreferences
import ru.ostrovskal.sshstd.utils.bval
import ru.ostrovskal.sshstd.utils.fval
import ru.ostrovskal.sshstd.utils.ival

/**
 * @author  Шаталов С.В.
 * @since   0.0.1
 */

/** Менеджер параметров системы */
object Settings {
	
	// Список параметров
	private val options 	            = LinkedHashMap<String, Option>()
	
	// Ссылка на хранитель
	private var sp: SharedPreferences?  = null
	
	// Класс хранящий параметр
	private class Option(@JvmField var v: String, @JvmField val def: String) {
		@JvmField val f	= v.fval(0f)
		@JvmField val i	= v.ival(0, 10)
		@JvmField val b	= v.bval(false)
	}
	
	@JvmStatic private fun option(key: String): Option = options[key] ?: error("undefined key options {$key}")
	
	/** Инициализация менеджера хранителя [sprefs] параметров системы [params] */
	@JvmStatic fun initialize(sprefs: SharedPreferences, params: Array<String>) {
		sp = sprefs.apply {
			params.forEach {
				val list = it.split(',')
				val key = list[0]
				val def = list[1]
				options[key] = Option(getString(key, def) ?: def, def)
			}
		}
	}
	
	/** Уничтожить менеджер параметров системы */
	@JvmStatic fun close() { sp = null; options.clear() }
	
	/** Установка значения [value] параметра [key] */
	@SuppressLint("CommitPrefEdits")
	@JvmStatic operator fun <T> set(key: String, value: T) {
		val v = value.toString()
		options[key] = Option(v, option(key).def)
		sp?.edit()?.apply {
			putString(key, v)
			apply()
		}
	}
	
	/** Получение целочисленного значения опции [key] */
	@JvmStatic fun integer(key: String) = option(key).i
	
	/** Получение вешественного значения опции [key] */
	@JvmStatic fun float(key: String) = option(key).f
	
	/** Получение логического значения опции [key] */
	@JvmStatic fun boolean(key: String) = option(key).b
	
	/** Получение текстового значения опции [key] */
	@JvmStatic fun text(key: String) = option(key).v
	
	/** Сброс всех параметров в значения по умолчанию. Параметер, имя которого начинается с символа #, пропускается */
	@JvmStatic fun default() {
		val edit = sp?.edit() ?: error("Error to default settings")
		
		options.forEach {
			val key = it.key
			val def = it.value.def
			// не сбрасывать настройки с ключем начинающемся с #
			if(key[0] != '#') {
				options[key] = Option(def, def)
				edit.putString(key, def)
			}
		}
		edit.apply()
	}
}

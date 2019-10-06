
package ru.ostrovskal.sshstd.utils

import android.graphics.Color
import android.graphics.Rect
import ru.ostrovskal.sshstd.Common.hexChars
import ru.ostrovskal.sshstd.objects.Settings
import java.util.*

/** Установка/Получение текстовых настроек системы */
inline var String.s
	get()                       = Settings.text(this)
	set(value)                  { Settings[this] = value }

/** Установка/Получение целочисленных настроек системы */
inline var String.i
	get()                       = Settings.integer(this)
	set(v)                      { Settings[this] = v }

/** Установка/Получение логических настроек системы */
inline var String.b
	get()                       = Settings.boolean(this)
	set(v)                      { Settings[this] = v }

/** Установка/Получение вещественных настроек системы */
inline var String.f
	get()                       = Settings.float(this)
	set(v)                      { Settings[this] = v }

/**
 * Формирование прямоугольника из строки
 *
 * @param defRight  Правая граница по умолчанию
 * @param defBottom Нижняя граница по умолчанию
 */
fun String?.toRect(defRight: Int, defBottom: Int) = Rect(0, 0, defRight, defBottom).apply {
	this@toRect?.split(",")?.forEachIndexed { index, s ->
		val edge = s.ival(0, 10)
		when(index) {
			0       -> left = edge
			1       -> top = edge
			2       -> { right = edge; bottom = edge }
			3       -> bottom = edge
		}
	}
}

/**
 * Формирование диапазона из строки
 *
 * @param def   Значение по умолчанию
 * @param int   Признак определяющий тип значений
 */
fun String?.toRange(def: Int, int: Boolean): IntRange {
	var start = def; var end = def
	this?.split("..")?.forEachIndexed { index, s ->
		val edge = if(int) s.ival(def, 10) else s.cval(def)
		if(index == 0) start = edge else end = edge
	}
	return start..end
}

/**
 * Формирование массива целых чисел из строки
 *
 * @param count  Длина массива, если равна нулю - создавать на основе фактического количества элементов
 * @param def    Значение по умолчанию
 * @param radix  Система счисления значений
 * @param colors Признак того, что в массиве находятся значения цвета
 */
fun String.toIntArray(count: Int, def: Int, radix: Int, colors: Boolean, delimiter: Char) = split(delimiter).run {
	val iter = iterator()
	val c = if(count == 0) size else count
	IntArray(c) {
		if(iter.hasNext()) {
			val n = iter.next()
			if(colors) {
				n.cval(def)
			} else {
				n.ival(def, radix)
			}
		} else def
	}
}

/**
 * Формирование массива вещественных чисел из строки
 *
 * @param count Длина массива
 * @param def   Значение по умолчанию
 */
fun String.toFloatArray(count: Int, def: Float) = split(',').run {
	val iter = iterator()
	val c = if(count == 0) size else count
	FloatArray(c) { if(iter.hasNext()) iter.next().fval(def) else def }
}

/** Получение массива имен из перечисления */
fun <T> enumNames(v: Array<T>): Array<String> {
	val iter = v.iterator()
	return Array(v.size) { iter.next().toString() }
}

/**
 * Сформировать массив на основе строковых представлений значений
 *
 * @param count Длина массива
 * @param map   Карта маппинга
 * @param def   Значение по умолчанию
 */
fun String.toFlagsArray(count: Int, map: Map<String, Int>, def: Int, delimiter1: Char, delimiter2: Char) = split(delimiter1).run {
	val iter = iterator()
	IntArray(count) { if(iter.hasNext()) iter.next().getFlags(map, def, delimiter2) else def }
}

/** Получение значения флагов из строки */
fun String.getFlags(map: Map<String, Int>, def: Int, delimiter: Char) = toUpperCase(Locale.ROOT).split(delimiter).run {
	var flags = 0
	forEach { flags = flags or map.getOrElse(it) { def } }
	flags
}

/** Форматирование строки по шаблону [pattern] с аргументами [args] */
fun StringBuilder.fmt(pattern: CharSequence, vararg args: Any): CharSequence {
	val sb = StringBuilder()
	setLength(0)
	if(pattern.isNotEmpty()) {
		var idx = 0
		var idxArgs = 0
		while(idx < pattern.length) {
			val ch1 = pattern[idx++]
			if(ch1 != '%') append(ch1)
			else {
				val ch2 = pattern[idx++]
				if(ch2 == '%') append(ch2)
				else {
					var ln = (ch2 - 48).toInt()
					if(ln !in 0..9) { ln = 0; idx-- }
					if(idxArgs < args.size) {
						args[idxArgs++].toString().apply {
							when(pattern[idx++]) {
								'd'		-> append(sb.padZero(toInt() , ln, false))
								'f'		-> append(sb.padZero(toFloat(), ln, true))
								's'     -> append(this)
							}
						}
					}
				}
			}
		}
	}
	return this
}

/** Создание текста из числа [value] с начальными/конечными нулями в предопределенном буфере размера [len] */
fun <T> StringBuilder.padZero(value: T, len: Int, isEnd: Boolean): StringBuilder {
	setLength(0)
	return append(value).apply {
		for(i in 1..(len - length)) {
			if(isEnd) append('0') else insert(0, '0')
		}
	}
}

/** Преобразование байта [bt] в шестадцатиричное представление и помещение его в строку по индексу [idx] */
fun StringBuilder.toHex(bt: Byte, idx: Int): StringBuilder {
	val i = bt.toInt()
	setCharAt(idx, hexChars[(i and 240) shr 4])
	setCharAt(idx + 1, hexChars[i and 15])
	return this
}

/** Преобразование целого [int] в шестадцатиричное представление и помещение его в строку по индексу [idx] */
fun StringBuilder.toHex(int: Int , idx: Int): StringBuilder {
	var i = int
	val p = when {
		i < 256 	-> 2
		i < 65536 	-> 4
		else 		-> 6
	}
	for(t in p downTo 0 step 2) {
		toHex(i.toByte(), idx + t)
		i = i shr 8
	}
	return this
}

/** Обрезка строки по определенному символу [ch] */
fun StringBuilder.trim(ch: Char): String {
	var idxE = length
	var idxS = 0
	while(idxS < idxE) {
		if(get(idxS) != ch) break
		idxS++
	}
	while(idxE-- >= 0) {
		if(get(idxE) != ch) break
	}
	return substring(idxS, idxE + 1)
}

/** Безопасное преобразование строки в integer, в системе счисления [radix] и со значением по умолчанию [def] */
fun String?.ival(def: Int, radix: Int) = lval(def.toLong(), radix).toInt()

/** Безопасное преобразование строки в long, в системе счисления [radix] и со значением по умолчанию [def] */
fun String?.lval(def: Long, radix: Int) = try { this?.toLong(radix) ?: def} catch(e: NumberFormatException) { def }

/** Безопасное преобразование строки в float и со значением по умолчанию [def] */
fun String?.fval(def: Float) = try { this?.toFloat() ?: def } catch(e: NumberFormatException) { def }

/** Безопасное преобразование строки в boolean и со значением по умолчанию [def] */
fun String?.bval(def: Boolean) = this?.run { this == "true" } ?: def

/** Безопасное преобразование строки в color и со значением по умолчанию [def] */
fun String?.cval(def: Int) = try { this?.run { Color.parseColor(this) } ?: def } catch(e: IllegalArgumentException) { def }

/** Соединение строки [s] с разделителем [delim] */
fun StringBuilder.join(s: String, delim: String = ", ") { append((if(isNotEmpty()) delim else "") + s) }

/** Получение эскейп последовательности */
fun Char.escape(): Any = when(this) {
	'\\'    -> "\\\\"
	'\"'    -> "\\\""
	'\b'    -> "\\b"
	'\u000C'-> "\\f"
	'\n'    -> "\\n"
	'\r'    -> "\\r"
	'\t'    -> "\\t"
	else    -> this
}

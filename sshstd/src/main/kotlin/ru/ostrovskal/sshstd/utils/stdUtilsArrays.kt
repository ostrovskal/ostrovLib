@file:Suppress("NOTHING_TO_INLINE")

package ru.ostrovskal.sshstd.utils

import ru.ostrovskal.sshstd.Common
import ru.ostrovskal.sshstd.objects.ATTR_ATTR_MSK
import ru.ostrovskal.sshstd.objects.ATTR_VALUE_MSK

/** Создание двумерного байтового массива размерностью [dim1] на [dim2] */
inline fun byteArrayOf2D(dim1: Int, dim2: Int): ByteArray {
	val ba = ByteArray(dim1 * dim2 + 2)
	ba[0] = dim1.toByte()
	ba[1] = dim2.toByte()
	return ba
}

/** Создание двумерного целого массива размерностью [dim1] на [dim2] */
inline fun intArrayOf2D(dim1: Int, dim2: Int): IntArray {
	val ba = IntArray(dim1 * dim2 + 2)
	ba[0] = dim1
	ba[1] = dim2
	return ba
}

/** Поиск числа [key] в массиве. [def] Значение по умолчанию */
inline fun IntArray.search(key: Int, def: Int) = indexOf(key).run { if(this == -1) def else this }

/** Проверка на определенное состояние [state] элемента */
inline fun IntArray.checkState(state: Int) = binarySearch(state) >= 0

/** Проход по атрибутам стилям/темы */
inline fun IntArray.loopAttrs(action: (attr: Int, value: Int) -> Unit) {
	for(idx in 0 until size step 2) {
		action(this[idx], this[idx + 1])
	}
}

/** Поиск значения атрибута [attribute] в теме. [def] Значение по умолчанию */
fun IntArray.themeAttrValue(attribute: Int, def: Int): Int {
	val attr = attribute and ATTR_VALUE_MSK
	loopAttrs { a, v -> if(attr == a) return v }
	return if(def == -1) error("Атрибут ${attribute and ATTR_ATTR_MSK} не обнаружен в текущей теме!") else def
}

/** Получение первого доступного состояния элемента из списка возможных [states] */
fun IntArray.checkStates(vararg states: Int): Int {
	if(!checkState(Common.STATE_ENABLED)) return Common.STATE_DISABLED
	states.forEach { if(checkState(it)) return it }
	return 0
}

/** Преобразование байтового массива в формат BLOB БД */
fun ByteArray.toBlob(): String {
	val sb = StringBuilder()
	sb.setLength(size * 2 + 2)
	sb.setCharAt(0, 'x')
	sb.setCharAt(1, '\'')
	forEachIndexed { idx, bt -> sb.toHex(bt, idx * 2 + 2) }
	sb.append("\'")
	return sb.toString()
}

/**
 * Получение значения из двумерного байтового массива
 *
 * @param x X координата
 * @param y Y координата
 */
inline operator fun ByteArray.get(x: Int, y: Int) = this[ y * this[0] + x + 2 ].toInt()

/**
 * Установка байтового значения в двумерный байтовый массив
 *
 * @param x X координата
 * @param y Y координата
 * @param v Значение
 */
inline operator fun ByteArray.set(x: Int, y: Int, v: Byte) { this[ y * this[0] + x + 2 ] = v }

/**
 * Установка целого значения в двумерный байтовый массив
 *
 * @param x X координата
 * @param y Y координата
 * @param v Значение
 */
inline operator fun ByteArray.set(x: Int, y: Int, v: Int) { this[ y * this[0] + x + 2] = v.toByte() }

/** Поиск символа [key]. [def] Значение по умолчанию */
inline fun CharArray.search(key: Char, def: Int) = with(indexOf(key)) { if(this == -1) def else this }

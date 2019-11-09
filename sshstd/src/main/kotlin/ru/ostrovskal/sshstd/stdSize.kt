package ru.ostrovskal.sshstd

/**
 * @author Шаталов С.В.
 * @since 0.1.0
 */

/** Класс, реализующий размер типа integer
 *
 * @property w Ширина размера
 * @property h Высота размера
 */
data class Size(@JvmField var w: Int, @JvmField var h:Int) {
	/** Установка ширины [w] и высоты [h] */
	fun set(w: Int, h: Int) { this.w = w; this.h = h }
	
	/** Проверка на пустой размер */
	fun isEmpty() = w == 0 || h == 0

	/** Проверка на недействительный размер */
	fun isDirty() = w == Int.MIN_VALUE || h == Int.MIN_VALUE

	/** Установка пустого размера */
	fun empty() { w = 0; h = 0 }

	/** Установка недействительного размера */
	fun dirty() { w = Int.MIN_VALUE; w = Int.MIN_VALUE}

	/** Преобразование в строковое представление */
	//override fun toString() = "Size($w - $h)"

	/** Проверка на идентичность */
	//override operator fun equals(other: Any?) = if(other is Size) w == other.w && h == other.h else false
}

/**
 * @author Шаталов С.В.
 * @since 0.1.0
 */

/** Класс, реализующий размер типа float
 *
 * @property w Ширина размера
 * @property h Высота размера
 */
data class SizeF(@JvmField var w: Float, @JvmField var h: Float) {
	/** Установка ширины [w] и высоты [h] */
	fun set(w: Float, h: Float) { this.w = w; this.h = h }
	
	/** Проверка на пустой размер */
	fun isEmpty() = w < .00001f || h < .00001f
	
	/** Установка пустого размера */
	fun empty() { w = 0f; h = 0f }
	
	/** Преобразование в строковое представление */
	//override fun toString() = "SizeF($w - $h)"

	/** Проверка на идентичность */
	//override fun equals(other: Any?) = if(other is SizeF) w == other.w && h == other.h else false
}


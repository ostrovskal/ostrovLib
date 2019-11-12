@file:Suppress("NOTHING_TO_INLINE")

package ru.ostrovskal.sshstd

import android.graphics.PointF
import android.graphics.Rect
import android.view.MotionEvent
import android.view.ViewConfiguration
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.utils.contains
import ru.ostrovskal.sshstd.utils.test
import ru.ostrovskal.sshstd.utils.touchtId
import kotlin.math.*

/**
 * @author Шаталов С.В.
 * @since  0.1.0
 */

/** Менеджер обработки касания */
class Touch {

	/** Флаги операций */
	@JvmField var flags			= 0

	/** Тип касания */
	@JvmField var act			= MotionEvent.ACTION_CANCEL

	/** Начальная координата касания */
	@JvmField var ptBegin       = PointF()
	
	/** Текущая координата касания */
	@JvmField var ptCurrent     = PointF()
	
	/** Идентификатор касания */
	@JvmField var id            = 0
	
	/** Начальное время касания */
	@JvmField var tmBegin       = 0L
	
	/** Текущее время касания */
	@JvmField var tmCurrent     = 0L

	// Временное значение длины
	private var len				= -1f

	// Временное значение времени
	private var tm				= 0L

	/** Признак завершения касания */
	inline val isUnpressed		get() = flags test TOUCH_UNPRESSED

	/** Сброс в начальную позицию */
	inline fun resetPosition() { ptBegin.x = ptCurrent.x; ptBegin.y = ptCurrent.y }

	/** Величина смещения относительно начальной точки с учетом размера ячейки [cell] */
	private fun delta(cell: Size): Boolean {
		tempSize.set(((ptCurrent.x - ptBegin.x) / cell.w).roundToInt(), ((ptCurrent.y - ptBegin.y) / cell.h).roundToInt())
		return (abs(tempSize.w) > 0 || abs(tempSize.h) > 0)
	}
	
	/** Длина "линии" между начальной [p] и текущей точкой с учетом размера ячейки [cell] */
	private fun length(cell: Size, p: PointF): Float {
		val x = ((ptCurrent.x - p.x).toDouble() / cell.w).pow(2.0)
		val y = ((ptCurrent.y - p.y).toDouble() / cell.h).pow(2.0)
		return sqrt(x + y).toFloat()
	}
	
	/** Вычисление угла между центральной [center] и заданной [p] точками */
	inline fun rotate(center: PointF, p: PointF): Float {
		val a = (atan2((center.y - p.y).toDouble(), (center.x - p.x).toDouble()) / Math.PI * 180.0).toFloat()
		return if(a < 0f) a + 360f else a
	}
	
	/** Вернуть координату точки [p] с учетом чувствительности [cell] и записать ее в [ret] */
	inline fun point(cell: Size, p: PointF, ret: PointF): PointF {
		ret.set(p.x / cell.w, p.y / cell.h)
		return ret
	}
	
	/** Вернуть признак времени задержки, с учетом максимального времени [limit] */
	inline fun delayed(limit: Long) = tmCurrent - tmBegin >= limit
	
	/** Найти индекс области в который попадает точка [p] из массива областей [rcElems] */
	inline fun contains(rcElems: Array<Rect>, p: PointF) = rcElems.indices.firstOrNull { rcElems[it].contains(p) } ?: -1
	
	/** Уничтожение всех объектов касания */
	fun reset() {
		tmBegin = 0L; tmCurrent = 0L
		ptBegin.x = 0f; ptBegin.y = 0f
		ptCurrent.x = 0f; ptCurrent.y = 0f
		flags = 0; act = MotionEvent.ACTION_CANCEL
		tm = -1L
	}

	/** Обработка события касания */
	fun event(event: MotionEvent, touch2: Touch? = null): Touch {
		// событие
		val act = event.actionMasked
		// время
		val tm = System.currentTimeMillis()
		// индекс
		val idx = event.actionIndex

		(if(idx == 0) this else touch2)?.apply {

			this.act = act
			id = event.touchtId

			when (act) {
				MotionEvent.ACTION_DOWN,
				MotionEvent.ACTION_POINTER_DOWN -> {
					flags = flags and TOUCH_UNPRESSED.inv()
					flags = flags or TOUCH_PRESSED
					ptBegin.x = event.getX(idx)
					ptBegin.y = event.getY(idx)
					ptCurrent.x = ptBegin.x
					ptCurrent.y = ptBegin.y
					tmBegin = tm
					tmCurrent = tm
				}
				MotionEvent.ACTION_CANCEL -> flags = 0
				MotionEvent.ACTION_UP,
				MotionEvent.ACTION_POINTER_UP -> {
					flags = flags and TOUCH_INIT_DOUBLE_PRESSED.inv()
					flags = flags or TOUCH_UNPRESSED
				}
			}
			if (flags test TOUCH_INIT_DOUBLE_PRESSED) {
				if (flags test TOUCH_PRESSED) {
					if ((tmCurrent - this.tm) < ViewConfiguration.getDoubleTapTimeout()) {
						flags = flags or TOUCH_DOUBLE_CLICKED
					}
				}
			}
			// уже было отпускание?
			if (flags test TOUCH_UNPRESSED) {
				if (flags test TOUCH_PRESSED) {
					this.tm = tmCurrent
					flags = flags and TOUCH_PRESSED.inv()
					flags = flags or TOUCH_INIT_DOUBLE_PRESSED
				}
			}
		}
		if(act == MotionEvent.ACTION_MOVE) {
			repeat(event.pointerCount) {
				(if(it == 0) this else touch2)?.apply {
					ptCurrent.x = event.getX(it)
					ptCurrent.y = event.getY(it)
					tmCurrent = tm
				}
			}
		}
		return this
	}

	/** Двойной клик в области [rc] */
	inline fun dblClick(rc: Rect, noinline action: () -> Unit) {
		if(flags test TOUCH_DOUBLE_CLICKED) click(rc, action)
	}

	/** Клик в массиве областей [rects] */
	inline fun dblClick(rects: Array<Rect>, noinline action: (idx: Int) -> Unit) {
		if(flags test TOUCH_DOUBLE_CLICKED) click(rects, action)
	}

	/** Клик в области [rc] */
	fun click(rc: Rect, action: () -> Unit) {
		if(isUnpressed) {
			if(rc.contains(ptBegin) && rc.contains(ptCurrent)) action()
			flags = 0
		}
	}

	/** Клик в массиве областей [rects] */
	fun click(rects: Array<Rect>, action: (idx: Int) -> Unit) {
		if(isUnpressed) {
			val idx = contains(rects, ptBegin)
			if(idx != -1 && idx == contains(rects, ptCurrent)) action(idx)
			flags = 0
		}
	}

	/** Ротация вокруг центральной точки [center] и чувствительностью [cell] */
	fun rotate(cell: Size, center: PointF, action: (angle: Float, event: Boolean) -> Unit): Touch {
		val unpressed = isUnpressed
		if(delta(cell) || unpressed) {
			action(rotate(center, ptCurrent), !unpressed)
			resetPosition()
			if(unpressed) flags = 0
		}
		return this
	}

	/** Перетаскивание для касания с чувствительностью [cell] */
	fun drag(cell: Size, action: (offset: Size, event: Boolean) -> Unit) {
		val unpressed = isUnpressed
		val deltaSz = if(!unpressed) delta(cell) else false
		if(deltaSz || unpressed) {
			tempSize.w *= cell.w
			tempSize.h *= cell.h
			action(tempSize, !unpressed)
			resetPosition()
			if(unpressed) flags = 0
		}
	}

	/**
	 * Определение направления
	 *
	 * @param cell   Чувствительность
	 * @param center Точка относительно которой определять направление
	 * @param is4    Признак, определяющий количество направлений - 4 или 8
	 */
	fun direction(cell: Size, center: PointF, is4: Boolean, action: (dir: Int) -> Unit): Touch {
		if(!isUnpressed) {
			var dir = DIR0
			val dx = ptCurrent.x - center.x
			val dy = ptCurrent.y - center.y
			val adx = abs(dx)
			val ady = abs(dy)
			if(adx > cell.w || ady > cell.h) {
				if(is4) {
					dir = if(adx >= ady) if(dx < cell.w) DIRL else DIRR else if(dy < cell.h) DIRU else DIRD
				} else {
					val angle = rotate(center, ptCurrent).roundToInt()
					for(r in 0..9) {
						if(angle >= aranges[r] && angle < aranges[r + 1]) {
							dir = dirs8[r]
							break
						}
					}
				}
			}
			action(dir)
		} else flags = 0
		return this
	}

	/** Масштабирование для касаний с чувствительностью [cell] */
	fun scale(other: Touch, cell: Size, action: (len: Float, event: Boolean) -> Unit) {
		val unpressed1 = isUnpressed
		val unpressed2 = other.isUnpressed
		val deltaSz1 = if(!unpressed1) delta(cell) else false
		val deltaSz2 = if(!unpressed2) delta(cell) else false
		if((deltaSz1 || deltaSz2) || unpressed1 || unpressed2) {
			val l = length(cell, other.ptCurrent)
			if(len < 0f) len = l
			action( l / len, !unpressed1 && !unpressed2)
			resetPosition()
			other.resetPosition()
			if(unpressed1) { len = -1f; flags = 0 }
			if(unpressed2) other.flags = 0

		}
	}
}


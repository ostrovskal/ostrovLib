@file:Suppress("NOTHING_TO_INLINE")

package ru.ostrovskal.sshstd

import android.graphics.PointF
import android.graphics.Rect
import android.view.MotionEvent
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

	/** Количество точек касания */
	@JvmField var count         = 0

	/** Начальная координата касания */
	@JvmField var ptBegin       = PointF()
	
	/** Текущая координата касания */
	@JvmField var ptCurrent     = PointF()
	
	/** Признак касания */
	@JvmField var press         = false
	
	/** Идентификатор касания */
	@JvmField var id            = 0
	
	/** Начальное время касания */
	@JvmField var tmBegin       = 0L
	
	/** Текущее время касания */
	@JvmField var tmCurrent     = 0L

	// Временное значение
	private var idxClick		= -1

	// Временное значение
	private var len				= 0f

	// Временный размер 1
	private val tempSize1		= Size(0, 0)

	// Временный размер 2
	private val tempSize2		= Size(0, 0)

	/** Сброс в начальную позицию */
	inline fun resetPosition() { ptBegin.x = ptCurrent.x; ptBegin.y = ptCurrent.y }
	
	/** Сброс в начальное время */
	inline fun resetTime() { tmBegin = tmCurrent }
	
	/** Величина смещения [ret] относительно начальной точки с учетом размера ячейки [cell] */
	private fun delta(cell: Size, ret: Size): Boolean {
		ret.set(((ptCurrent.x - ptBegin.x) / cell.w).roundToInt(), ((ptCurrent.y - ptBegin.y) / cell.h).roundToInt())
		return (abs(ret.w) > 0 || abs(ret.h) > 0)
	}
	
	/** Длина "линии" между начальной [p] и текущей точкой с учетом размера ячейки [cell] */
	private fun length(cell: Size, p: PointF): Float {
		val x = ((ptCurrent.x - p.x) / cell.w.toDouble()).pow(2.0)
		val y = ((ptCurrent.y - p.y) / cell.h.toDouble()).pow(2.0)
		return sqrt(x + y).toFloat()
	}
	
	/** Вычисление угла между центральной [center] и заданной [p] точками */
	inline fun rotate(center: PointF, p: PointF): Float {
		val a = (atan2((center.y - p.y).toDouble(), (center.x - p.x).toDouble()) / Math.PI * 180.0).toFloat()
		return if(a < 0f) a + 360f else a
	}
	
	/** Вернуть координату точки [p] с учетом гранулярности [cell] и записать ее в [ret] */
	inline fun point(cell: Size, p: PointF, ret: PointF): PointF {
		ret.set(p.x / cell.w, p.y / cell.h)
		return ret
	}
	
	/** Вернуть признак времени задержки, с учетом максимального времени [limit] */
	inline fun delayed(limit: Long) = tmCurrent - tmBegin >= limit
	
	/** Найти индекс области в который попадает точка [p] из массива областей [rcElems] */
	inline fun contains(rcElems: Array<Rect>, p: PointF) = rcElems.indices.firstOrNull { rcElems[it].contains(p) } ?: -1
	
	/**
	 * Вернуть направление относительно точки [pt] с учетом гранулярности [cell]
	 * [is4] Признак, определяющий вычисление по четырем или восьми направлениям вычислять направление
	 */
	private fun direction(cell: Size, pt: PointF, is4: Boolean): Int {
		var dir = DIR0
		val dx = ptCurrent.x - pt.x
		val dy = ptCurrent.y - pt.y
		val adx = abs(dx)
		val ady = abs(dy)
		if(adx > cell.w || ady > cell.h) {
			if(is4) {
				dir = if(adx >= ady) if(dx < cell.w) DIRL else DIRR else if(dy < cell.h) DIRU else DIRD
			} else {
				val angle = rotate(pt, ptCurrent).roundToInt()
				for(r in 0..9) {
					if(angle >= aranges[r] && angle < aranges[r + 1]) {
						dir = dirs8[r]
						break
					}
				}
			}
		}
		return dir
	}

	/** Клик для области [rc] */
	fun click(rc: Rect, action: (time: Long) -> Unit) {
		if(press) {
			if(rc.contains(ptBegin)) flags = TOUCH_PRESSED
		} else {
			if(flags test TOUCH_PRESSED) {
				if(rc.contains(ptCurrent)) action(tmCurrent - tmBegin)
				flags = 0
			}
		}
	}

	/** Клик для массива областей [rects] */
	fun click(rects: Array<Rect>, action: (idx: Int, time: Long) -> Unit) {
		if(press) {
			idxClick = contains(rects, ptBegin).also { if(it != -1) flags = TOUCH_PRESSED }
		} else if(flags test TOUCH_PRESSED) {
			if (contains(rects, ptCurrent) == idxClick) action(idxClick, tmCurrent - tmBegin)
			flags = 0
			idxClick = -1
		}
	}

	/**
	 * Определение направления
	 *
	 * @param cell   Гранулярность
	 * @param center Точка относительно которой определять направление
	 * @param is4    Признак, определяющий количество направлений - 4 или 8
	 */
	fun direction(cell: Size, center: PointF, is4: Boolean, action: (dir: Int, t: Touch) -> Unit) {
		if(press) action(direction(cell, center, is4), this)
	}

	/** Перетаскивание для касания с гранулярностью [cell] */
	fun drag(cell: Size, action: (offset: Size, time: Long, t: Touch, event: Boolean) -> Unit) {
		if(press) {
			flags = TOUCH_PRESSED
			delta(cell, tempSize1)
			if(tempSize2 != tempSize1) {
				tempSize2.w = tempSize1.w
				tempSize2.h = tempSize1.h
				tempSize1.w *= cell.w
				tempSize1.h *= cell.h
				action(tempSize1, tmCurrent - tmBegin, this, true)
				resetTime()
			}
		} else if(flags test TOUCH_PRESSED) {
			action(tempSize1, tmCurrent - tmBegin, this, false)
			tempSize2.dirty()
			flags = 0
		}
	}

	/** Ротация вокруг центральной точки [center] для касания и гранулярностью [cell] */
	fun rotate(cell: Size, center: PointF, action: (angle: Float, time: Long, t: Touch, event: Boolean) -> Unit) {
		if(press) {
			flags = TOUCH_PRESSED
			if(delta(cell, tempSize1)) {
				action(rotate(center, ptCurrent), tmCurrent - tmBegin, this, true)
				resetPosition()
				resetTime()
			}
		} else if(flags test TOUCH_PRESSED) {
			action(0f, tmCurrent - tmBegin, this, false)
			flags = 0
		}
	}

	/** Масштабирование для касаний с гранулярностью [cell] */
	fun scale(other: Touch, cell: Size, action: (offs: Float, t1: Touch, t2: Touch, event: Boolean) -> Unit) {
		if(press && other.press) {
			if(delta(cell, tempSize1) || other.delta(cell, other.tempSize1)) {
				if(flags == 0) {
					flags = TOUCH_PRESSED
					other.flags = TOUCH_PRESSED
					len = length(cell, other.ptCurrent)
				}
				action((length(cell, other.ptCurrent) / len) - 1f, this, other, true)
				resetPosition()
				other.resetPosition()
			}
		} else if((flags test TOUCH_PRESSED) && (other.flags test TOUCH_PRESSED)) {
			action(0f, this, other, false)
			flags = 0
			other.flags = 0
		}
	}

	/** Уничтожение всех объектов касания */
	fun reset() {
		tmBegin = 0L; tmCurrent = 0L
		ptBegin.x = 0f; ptBegin.y = 0f
		ptCurrent.x = 0f; ptCurrent.y = 0f
		flags = 0; count = 0; act = MotionEvent.ACTION_CANCEL
		tempSize1.w = 0; tempSize1.h = 0
		tempSize2.w = 0; tempSize2.h = 0
		idxClick = -1; len = 0f
		press = false
	}

	/** Обработка события касания */
	fun event(event: MotionEvent): Touch {
		// время
		val tm = System.currentTimeMillis()
		// индекс
		val idx = event.actionIndex
		// координаты
		val x = event.getX(idx)
		val y = event.getY(idx)
		// событие
		act = event.actionMasked

		id = event.touchtId
		count = event.pointerCount

		when(act) {
			MotionEvent.ACTION_DOWN								-> {
				press = true
				ptBegin.x = x; ptBegin.y = y
				tmBegin = tm
			}
			MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL	-> press = false
		}
		tmCurrent = tm
		ptCurrent.x = x; ptCurrent.y = y
		return this
	}
}


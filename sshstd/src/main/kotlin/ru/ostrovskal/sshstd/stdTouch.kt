@file:Suppress("NOTHING_TO_INLINE")

package ru.ostrovskal.sshstd

import android.graphics.PointF
import android.graphics.Rect
import android.view.MotionEvent
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.utils.contains

/**
 * @author Шаталов С.В.
 * @since  0.1.0
 */

/** Менеджер обработки касания */
class Touch {
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
	
	/** Сброс в начальную позицию */
	inline fun resetPosition() { ptBegin.x = ptCurrent.x; ptBegin.y = ptCurrent.y }
	
	/** Сброс в начальное время */
	inline fun resetTime() { tmBegin = tmCurrent }
	
	/** Величина смещения [ret] относительно начальной точки с учетом размера ячейки [cell] */
	fun delta(cell: Size, ret: Size): Boolean {
		ret.set(Math.round((ptCurrent.x - ptBegin.x) / cell.w), Math.round((ptCurrent.y - ptBegin.y) / cell.h))
		return (Math.abs(ret.w) > 0 || Math.abs(ret.h) > 0)
	}
	
	/** Длина "линии" между начальной [p] и текущей точкой с учетом размера ячейки [cell] */
	fun length(cell: Size, p: PointF): Float {
		val x = Math.pow((ptCurrent.x - p.x) / cell.w.toDouble(), 2.0)
		val y = Math.pow((ptCurrent.y - p.y) / cell.h.toDouble(), 2.0)
		return Math.sqrt(x + y).toFloat()
	}
	
	/** Вычисление угла между центральной [center] и заданной [p] точками */
	inline fun rotate(center: PointF, p: PointF): Float {
		val a = (Math.atan2((center.y - p.y).toDouble(), (center.x - p.x).toDouble()) / Math.PI * 180.0).toFloat()
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
	fun direction(cell: Size, pt: PointF, is4: Boolean): Int {
		var dir = DIR0
		val dx = ptCurrent.x - pt.x
		val dy = ptCurrent.y - pt.y
		val adx = Math.abs(dx)
		val ady = Math.abs(dy)
		if(adx > cell.w || ady > cell.h) {
			if(is4) {
				dir = if(adx >= ady) if(dx < cell.w) DIRL else DIRR else if(dy < cell.h) DIRU else DIRD
			} else {
				val angle = Math.round(rotate(pt, ptCurrent))
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
	
	companion object {
		/** Массив объектов касания */
		@JvmField var touch = Array(10) { Touch() }
		
		/** Количество объектов касания */
		@JvmField var count           = 0
		
		/** Индекс области клика */
		@JvmField var clk             = -1
		
		/** Временный размер1 */
		@JvmField var tmpSz1          = Size(0, 0)
		
		/** Временный размер2 */
		@JvmField var tmpSz2          = Size(0, 0)
		
		/** Захваченный объект нажатия1 */
		@JvmField var tmpTh1: Touch?  = null
		
		/** Захваченный объект нажатия2 */
		@JvmField var tmpTh2: Touch?  = null
		
		/** Длина вектора */
		@JvmField var len             = 0f
		
		/** Первое касание */
		@JvmField var pt              = PointF(-1f, -1f)
		
	}
}

/** Клик с идентификатором [id] для области [rc] */
fun touchClick(id: Int, rc: Rect, action: (time: Long) -> Unit) {
	findTouch(id)?.apply { if(rc.contains(ptBegin)) Touch.tmpTh1 = this } ?: Touch.tmpTh1?.apply { Touch.tmpTh1 = null; if(rc.contains(ptCurrent))
		action(tmCurrent - tmBegin) }
}

/** Клик с идентификатором [id] для массива областей [rects] */
fun touchClick(id: Int, rects: Array<Rect>, action: (idx: Int, time: Long) -> Unit) {
	findTouch(id)?.apply { Touch.clk = contains(rects, ptBegin).also { if(it != -1) Touch.tmpTh1 = this } }
	?: Touch.tmpTh1?.apply { Touch.tmpTh1 = null; if(contains(rects, ptCurrent) == Touch.clk) action(Touch.clk, tmCurrent - tmBegin) }
}

/**
 * Определение направления
 *
 * @param id     Идентификатор касания
 * @param cell   Гранулярность
 * @param center Точка относительно которой определять направление
 * @param is4    Признак, определяющий количество направлений - 4 или 8
 */
inline fun touchDirection(id: Int, cell: Size, center: PointF, is4: Boolean, action: (dir: Int, t: Touch) -> Unit) {
	findTouch(id)?.apply { action(direction(cell, center, is4), this) }
}

/** Перетаскивание для касания с идентификатором [id] и гранулярностью [cell] */
fun touchDrag(id: Int, cell: Size, action: (offset: Size, time: Long, t: Touch, event: Boolean) -> Unit) {
	Touch.tmpTh1 = findTouch(id)?.apply {
		delta(cell, Touch.tmpSz1)
		if(Touch.tmpSz2 != Touch.tmpSz1) {
			Touch.tmpSz2.w = Touch.tmpSz1.w
			Touch.tmpSz2.h = Touch.tmpSz1.h
			Touch.tmpSz1.w *= cell.w
			Touch.tmpSz1.h *= cell.h
			action(Touch.tmpSz1, tmCurrent - tmBegin, this, true)
			resetTime()
		}
	} ?: Touch.tmpTh1?.apply {
		action(Touch.tmpSz1, tmCurrent - tmBegin, this, false)
		Touch.tmpSz2.empty()
	}
}

/** Ротация вокруг центральной точки [center] для касания с идентификатором [id] и гранулярностью [cell] */
fun touchRotate(id: Int, cell: Size, center: PointF, action: (angle: Float, time: Long, t: Touch, event: Boolean) -> Unit) {
	Touch.tmpTh1 = findTouch(id)?.apply {
		if(delta(cell, Touch.tmpSz1)) {
			action(this.rotate(center, ptCurrent), tmCurrent - tmBegin, this, true)
			resetPosition()
			resetTime()
		}
	} ?: Touch.tmpTh1?.apply {action(0f, tmCurrent - tmBegin, this, false) }
}

/** Масштабирование для касаний с идентификаторами [id1] и [id2] и гранулярностью [cell] */
fun touchScale(id1: Int, id2: Int, cell: Size, action: (offs: Float, t1: Touch, t2: Touch, event: Boolean) -> Unit) {
	val t1 = findTouch(id1)
	val t2 = findTouch(id2)
	if(t1 != null && t2 != null) {
		if(t1.delta(cell, Touch.tmpSz1) || t2.delta(cell, Touch.tmpSz1)) {
			if(Touch.tmpTh1 == null) {
				Touch.tmpTh1 = t1; Touch.tmpTh2 = t2
				Touch.len = t1.length(cell, t2.ptCurrent)
			}
			action((t1.length(cell, t2.ptCurrent) / Touch.len) - 1f, t1, t2, true)
			t1.resetPosition()
			t2.resetPosition()
		}
	} else Touch.tmpTh1?.apply { action(0f, this, Touch.tmpTh2 ?: this, false);  Touch.tmpTh1 = null; Touch.tmpTh2 = null }
}

/**
 * Симуляция мультитач
 *
 * @param id    Идентификатор касания
 * @param x     Горизонтальная позиция
 * @param y     Вертикальная позиция
 * @param pr    Признак нажатия
 * @param tm    Признак времени, true = начальное, false = текущее
 */
fun emulatorMultitouch(id: Int, x: Float, y: Float, pr: Boolean, tm: Boolean) {
	Touch.touch[id].apply {
		tmBegin = System.currentTimeMillis()
		ptBegin = PointF(x, y)
		this.id = id
		press = pr
		if(!tm) {
			tmCurrent = tmBegin
			ptCurrent = ptBegin
		}
	}
}

/** Уничтожение всех объектов касания */
fun touchReset() {
	Touch.touch.forEach {
		it.apply {
			tmBegin = 0L; tmCurrent = 0L
			ptBegin.x = 0f; ptBegin.y = 0f
			ptCurrent.x = 0f; ptCurrent.y = 0f
			press = false
		}
	}
	Touch.count = 0
}

/** Найти объект касания по определенному индексу [idx] с учетом признака нажатия [pressed] */
fun findTouch(idx: Int, pressed: Boolean = true): Touch? = Touch.touch[idx].run {
	if(!pressed) this
	else if(press) this else null
}

/** Обработка события касания */
fun onTouch(event: MotionEvent): Touch? {
	// время события
	val tm = System.currentTimeMillis()
	// событие касания
	val act = event.actionMasked
	// число касаний
	Touch.count = event.pointerCount
	repeat(Touch.count) {
		val x = event.getX(it)
		val y = event.getY(it)
		// касание
		Touch.touch[it].apply {
			when(act) {
				MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN                        -> {
					press = true
					ptCurrent.x = x; ptBegin.x = x
					ptCurrent.y = y; ptBegin.y = y
					Touch.pt.x = x; Touch.pt.y = y
					tmBegin = tm; tmCurrent = tm
					id = event.getPointerId(it)
				}
				MotionEvent.ACTION_MOVE                                                         -> {
					ptCurrent.x = x; ptCurrent.y = y
					tmCurrent = tm
				}
				MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_POINTER_UP -> {
					press = false
					ptCurrent.x = x; ptCurrent.y = y
					tmCurrent = tm
				}
			}
		}
	}
	return findTouch(0)
}


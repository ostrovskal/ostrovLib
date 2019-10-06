package ru.ostrovskal.sshstd.widgets.charts

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.utils.*

/**
 * @author  Шаталов С.В.
 * @since   0.2.3
 */

/** Класс реализующий простую диаграмму */
open class ChartDiagram(context: Context, style: IntArray) : Chart(context, style) {
	
	// Области диаграмм
	private var rects 							= arrayOf<RectF>()

	/** Максимальные значения сегментов */
	var maxValuesSegments			            = intArrayOf()
		set(v)                                  {
			field = v
			v.forEachIndexed { index, i ->  field[index] = if(i < 1) 1 else i }
			measure()
		}
	
	/** Текущие значения сегментов */
	override var currentValuesSegments          = intArrayOf()
		set(v)                                  {
			field = v
			v.forEachIndexed { index, i -> field[index] = (0..maxValuesSegments[index]).clamp(i) }
			measure()
		}
	
	/** Направление отображения */
	var direction                               = DIRU
		set(v)                                  { field = v; drawableSegments.gradientDir = v.dirRot90; measure() }
	
	init {
		direction = Theme.integer(context, style.themeAttrValue(ATTR_SSH_GRADIENT_DIR, DIRU))
	}
	
	/** Вычисление внутренних характеристик диаграммы */
	override fun measure() {
		val size = currentValuesSegments.size
		var w = measuredWidth.toFloat()
		var h = measuredHeight.toFloat()
		
		if(size > 0 && w > 0 && h > 0) {
			val pl = paddingStart
			val pr = paddingEnd
			val pt = paddingTop
			val pb = paddingBottom
			
			if(rects.size != size) rects = Array(size) { RectF() }
			
			val delta = (if(direction test DIRH) h else w) / size
			
			w -= pr
			h -= pb

			var x = 0f; var y = 0f
			
			rects.forEachIndexed { index, rect ->
				val cur = currentValuesSegments[index]
				val v = frame * (cur / animator.frames)
				val mx = maxValuesSegments[index]
				val vw = ((w / mx) * v)
				val vh = ((h / mx) * v)
				var x1 = x + pl; var y1 = y + pt
				var x2 = w; var y2 = h
				if(direction test DIRH) {
					y += delta
					y2 = y - pb
					if(direction == DIRL) x1 += w - vw else x2 = vw
				} else {
					x += delta
					x2 = x - pr
					if(direction == DIRU) y1 += h - vh else y2 = vh
				}
				rect.set(x1, y1, x2, y2)
			}
			invalidate()
		}
	}
	
	/** Отображение диаграммы */
	override fun draw(canvas: Canvas) {
		super.draw(canvas)
		if(currentValuesSegments.isEmpty()) return
		drawableSegments.apply {
			rects.forEachIndexed { idx, rect ->
				if(rect.isEmpty) return@forEachIndexed
				xyInt[0] = colorsSegments[idx * 2]
				xyInt[1] = colorsSegments[idx * 2 + 1]
				gradient = xyInt
				bounds = rect.toInt(iRect)
				draw(canvas)
				val v = currentValuesSegments[idx]
				if(isShowText && v != 0) {
					canvas.withSave {
						val cx = rect.centerX()
						val cy = rect.centerY()
						if(direction test DIRV) rotate(90f, cx, cy)
						drawText("$v", cx, cy - this@ChartDiagram.paint.fontMetrics.ascent / 2f, this@ChartDiagram.paint)
					}
				}
			}
		}
	}
}
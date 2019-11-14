package ru.ostrovskal.sshstd.widgets.charts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.utils.*
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * @author  Шаталов С.В.
 * @since   0.2.3
 */

/** Класс реализующий круговую диаграмму */
open class ChartCircular(context: Context, style: IntArray) : Chart(context, style) {
	
	// Область отображения
	private val rect                            = RectF()
	
	// Радиус
	private var radius                          = 0f
	
	// Сумма углов сегментов
	private var angles                          = 0f
	
	/** Радиус выделенного сегмента в процентах, относительно основного */
	var checkedRadius                           = 15
		set(v)                                  { field = v; measure() }
	
	/** Внутренний радиус в процентах, относительно основного */
	var innerRadius                             = 30
		set(v)                                  { field = v; measure() }
	
	/** Индексы выделенных сегментов */
	var checkedSegments                         = intArrayOf()
		set(v)                                  { field = v; invalidate() }
	
	/** Начальный угол для круговой диаграммы */
	var beginAngle                              = 30f
		set(v)                                  { field = (v % 360f); invalidate() }
	
	/** Текущие значения сегментов */
	override var currentValuesSegments          = intArrayOf()
		set(v)                                  { field = v; measure() }

	init {
		style.loopAttrs { attr, value ->
			Theme.attrProps(context, attr, value)
			when(attr) {
				ATTR_SSH_CHART_BEGIN_ANGLE    -> beginAngle = Theme.flt
				ATTR_SSH_CHART_CHECK_RADIUS   -> checkedRadius = Theme.int
				ATTR_SSH_CHART_INNER_RADIUS   -> innerRadius = Theme.int
			}
		}
	}
	
	/** Вычисление внутренних характеристик диаграммы */
	override fun measure() {
		val size = currentValuesSegments.size
		
		if(size > 0 && !rectScreen.isZero) {
			angles = currentValuesSegments.sum().toFloat()
			radius = min(measuredWidth - horizontalPadding, measuredHeight - verticalPadding) / 2f
			val cr = (radius * checkedRadius) / 100f
			val cx = rectScreen.centerX().toFloat()
			val cy = rectScreen.centerY().toFloat()
			rect.set(cx - radius, cy - radius, cx + radius, cy + radius)
			drawableSegments.path.makeFigure(TILE_SHAPE_CIRCLE, rect, null)
			rect.inset(cr, cr)
			drawableSegments.path.op(Path().apply { addCircle(cx, cy, ((radius - cr) * innerRadius) / 100f, Path.Direction.CCW) }, Path.Op.DIFFERENCE)
			invalidate()
		}
	}
	
	override fun draw(canvas: Canvas) {
		super.draw(canvas)
		var a1 = beginAngle
		var r: RectF
		val cr = (radius * checkedRadius) / 100f
		canvas.clipPath(drawableSegments.path)
		currentValuesSegments.forEachIndexed { idx, cur ->
			val percent = cur / angles
			val a = percent * 360.0f
			val v = frame * (a / animator.frames)
			val chk = checkedSegments.indexOf(idx) != -1
			val a2 = a1 + v / 2.0
			var offs = radius
			r = if(chk) {
				offs += cr
				rect.offsetAngle(a2, cr, cr, fRect)
			} else {
				rect
			}
			drawableSegments.paint.color = colorsSegments[idx]
			canvas.drawArc(r, a1, v, true, drawableSegments.paint)
			if(isShowText && v != 0f) {
				canvas.withSave {
					val rot = (a2 % 360f).toFloat()
					r = rect.offsetAngle(a2, offs * 0.66f, offs * 0.66f, fRect)
					rotate(if(rot > 100f && rot < 270f) rot + 180f else rot, r.centerX(), r.centerY())
					drawText("${(percent * 100.1f).roundToInt()}%", r.centerX(), r.centerY() - paint.fontMetrics.ascent / 2f, paint)
				}
			}
			a1 += a
		}
	}
}
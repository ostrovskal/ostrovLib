@file:Suppress("NOTHING_TO_INLINE")

package ru.ostrovskal.sshstd.layouts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.view.View
import android.view.ViewGroup
import ru.ostrovskal.sshstd.Common.MATCH
import ru.ostrovskal.sshstd.Common.WRAP
import ru.ostrovskal.sshstd.SizeF
import ru.ostrovskal.sshstd.objects.ATTR_SSH_COLOR_WIRED
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.utils.*

/**
 * @author  Шаталов С.В.
 * @since   0.3.8
 */

/** Константа для автоматического определения количества строк или столбцов */
const val CELL_LAYOUT_AUTO_FIT      = -1

/** Константа для добавления ячейки в начало */
const val CELL_LAYOUT_INSERT_BEGIN  = -100

/** Константа для добавления ячейки в конец */
const val CELL_LAYOUT_INSERT_END    = -200

/** Класс, реализующий ячеестую разметку
 *
 * @property cols    Количество колонок
 * @property rows    Количество строк
 * @property spacing Пространство между ячейками
 */
open class CellLayout(context: Context, @JvmField protected var cols: Int, @JvmField protected var rows: Int,
                      @JvmField protected var spacing: Int = 0, show: Boolean = false) : ViewGroup(context, null, 0) {
	
	/** Установка параметров разметки представления с гор. координатой [x] и верт. координатой [y] */
	inline fun <T : View> T.lps(x: Int, y: Int) = lps(x, y, 0, 0) {}
	
	/** Установка параметров разметки представления с гор. координатой [x] и верт. координатой [y], шириной [w] и высотой [h] */
	inline fun <T : View> T.lps(x: Int, y: Int, w: Int, h: Int) = lps(x, y, w, h) {}
	
	/** Установка параметров разметки представления с гор. координатой [x] и верт. координатой [y], шириной [w], высотой [h] и инициализатором [init] */
	inline fun <T : View> T.lps(x: Int, y: Int, w: Int, h: Int, init: T.() -> Unit): T {
		layoutParams = CellLayout.LayoutParams(x, y, w, h).apply { init() }
		return this
	}
	
	// Ширина ячейки в пикселях
	private var cellW		            = 0f
	
	// Высота ячейки в пикселях
	private var cellH		            = 0f
	
	// Рисователь сетки
	private var paint		 			= Paint().apply {
		color = Theme.integer(context, Theme.themeAttrValue(ATTR_SSH_COLOR_WIRED, 0xf9f9f9.color))
		strokeWidth = 1f
	}
	
	// Массив точек сетки
	private var pts: FloatArray?		= null
	
	/** Габариты ячейки */
	val cellSize
		get()                           = SizeF(cellW, cellH)
	
	/** Максимальная ячейка */
	val cellMax
		get()                           = Point(cols, rows)
	
	init { setWillNotDraw(!show) }
	
	/** Вычисление ячейки по экранным координатам [x] и [y], и запись результата в [out] */
	fun pointFromCoord(x: Float, y: Float, out: Point): Point {
		out.set((x / cellW).toInt(), (y / cellH).toInt())
		return out
	}
	
	/** Сдвиг всех ячеек, начиная с позиции [fromX] и [fromY], на величину [dx] и [dy] */
	fun shiftCells(fromX: Int, fromY: Int, dx: Int, dy: Int) {
		loopChildren {
			(it.layoutParams as? CellLayout.LayoutParams)?.apply {
				if(x >= fromX && y >= fromY) it.layoutParams = LayoutParams(x + dx, y + dy, w, h)
			}
		}
	}
	
	/** Вычисление габаритов представлений */
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		val ww = View.MeasureSpec.getSize(widthMeasureSpec)
		val hh = MeasureSpec.getSize(heightMeasureSpec)
		val spc = spacing * 2
		
		if(cellW == 0f && cellH == 0f) {
			val w = ww - horizontalPadding
			val h = hh - verticalPadding
			cellW = w / cols.toFloat()
			cellH = h / rows.toFloat()
			if(cols == CELL_LAYOUT_AUTO_FIT) {
				cellW = cellH
				cols = Math.round(w / cellW)
			}
			if(rows == CELL_LAYOUT_AUTO_FIT) {
				cellH = cellW
				rows = Math.round(h / cellH)
			}
		}
		loopChildren {
			if(it.visibility == View.GONE) return@loopChildren
			(it.layoutParams as CellLayout.LayoutParams?)?.apply {
				if(w <= 0) w = cols
				if(h <= 0) h = rows
				if(y == CELL_LAYOUT_INSERT_BEGIN) {
					shiftCells(0, 0, 0, h)
					y = 0
				}
				if(y == CELL_LAYOUT_INSERT_END) {
					// вставляем в конец
					var maxY = 0
					var maxH = 1
					loopChildren {view ->
						(view.layoutParams as? CellLayout.LayoutParams)?.apply {
							if((y + h) > maxY) { maxY = y; maxH = h }
						}
					}
					y = maxY + maxH
				}
				// корректируем габариты
				if(x + w > cols) w = cols - x
				if(y + h > rows) h = rows - y
				val childWidthSpec = View.MeasureSpec.makeMeasureSpec((w * cellW).toInt() - spc, View.MeasureSpec.EXACTLY)
				val childHeightSpec = View.MeasureSpec.makeMeasureSpec((h * cellH).toInt() - spc, View.MeasureSpec.EXACTLY)
				it.measure(childWidthSpec, childHeightSpec)
			}
		}
		setMeasuredDimension(ww, hh)
	}
	
	/** Вычисление позиций представлений */
	override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
		val pl = paddingStart
		val pt = paddingTop
		loopChildren {
			if(it.visibility == View.GONE) return@loopChildren
			(it.layoutParams as CellLayout.LayoutParams?)?.apply {
				val ll = (x * cellW).toInt() + pl + spacing
				val rr = ((x + w) * cellW).toInt() + pl - spacing
				val tt = (y * cellH).toInt() + pt + spacing
				val bb = ((y + h) * cellH).toInt() + pt - spacing
				it.layout(ll, tt, rr, bb)
			}
		}
		if(!willNotDraw())
			pts = makeWired(cols, rows, (r - l).toFloat(), (b - t).toFloat(), cellW, cellH)
	}
	
	/** Проверка параметров */
	override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean = p is LayoutParams
	
	/** Генерация параметров */
	override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams = LayoutParams(p)
	
	/** Параметры по умолчанию */
	override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams = LayoutParams(0, 0, MATCH, WRAP)
	
	/**
	 * Класс параметров представлений
	 *
	 * @property x  Горизонтальная позиция ячейки
	 * @property y  Вертикальная позиция ячейки
	 * @property w  Горизонтальное количество ячеек
	 * @property h  Вертикальное количество ячеек
	 */
	class LayoutParams @JvmOverloads constructor(@JvmField var x: Int, @JvmField var y: Int, @JvmField var w: Int = MATCH, @JvmField var h: Int = WRAP) :
			ViewGroup.LayoutParams(MATCH, WRAP) {
		companion object {
			// Текущая ширина ячейки
			private var currentWidth			= 1
			
			// Текущая высота ячейки
			private var currentHeight			= 1
		}
		
		init {
			w = if(w == 0) currentWidth else w
			h = if(h == 0) currentHeight else h
			if(w > 0) currentWidth = w
			if(h > 0) currentHeight = h
		}
		
		/** Конструктор копии параметров разметки */
		constructor(params: ViewGroup.LayoutParams) : this(0, 0) {
			if(params is LayoutParams) {
				x = params.x; y = params.y
				w = params.w; h = params.h
				currentWidth = w; currentHeight = h
			}
		}
	}
	
	/** Отрисовка сетки */
	override fun draw(canvas: Canvas) {
		super.draw(canvas)
		// нарисовать сетку
		pts?.apply{ canvas.drawLines(this, paint) }
	}
}

package ru.ostrovskal.sshstd.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.os.Parcelable
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import ru.ostrovskal.sshstd.*
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.layouts.AbsoluteLayout
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.utils.*
import kotlin.math.roundToInt

/**
 * @author  Шаталов С.В.
 * @since   0.2.1
 */

/** Символы карты контроллера */
@JvmField val controllerCharsMap = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ')

/** Карта контроллера по умолчанию 8x8 */
const val mapController = "1313111111111414\n" +
                          "1313111111111414\n" +
                          "3333131010144444\n" +
                          "3333300000404444\n" +
                          "3333300000404444\n" +
                          "3333232020244444\n" +
                          "2323222222222424\n" +
                          "2323222222222424"

/** Класс, реализующий контроллер управления игровыми событиями со стилем по умолчанию style_controller */
open class Controller(context: Context, ID: Int, grid: Boolean, style: IntArray = style_controller): Tile(context, style) {
	
	// Позиция
	private var tmpPt				= PointF(-1f, -1f)

	// Нажатые кнопки
	private var pressedButtons		= 0
	
	// Габариты тайлов всех кнопок
	private var buttonsRects        = arrayOf<Rect>()
	
	// Карта контроллера
	private var controllerMap       = byteArrayOf2D(0, 0)
	
	// Относительный размер карты к размеру на экране
	private val relativeSizeMap       = SizeF(0f, 0f)
	
	// Массив точек сетки
	private var pts: FloatArray?	= null
	
	/** Габариты карты */
	val sizeMap
		get()                       = Size(controllerMap[0].toInt(), controllerMap[1].toInt())
	
	/** Событие уведомления */
	var controllerButtonNotify: ((buttons: Int) -> Unit)?= null
	
	/** Габариты */
	@JvmField val szController	    = Size(160.dp, 160.dp)
	
	/** Позиция на разметке */
	@JvmField var position          = Point()
	
	init {
		id = ID
		Theme.setBaseAttr(context, this, style)
		style.loopAttrs { attr, value ->
			Theme.attrProps(context, attr, value)
			when(attr) {
				ATTR_SSH_CONTROLLER_WIDTH      -> szController.w = Theme.int
				ATTR_SSH_CONTROLLER_HEIGHT     -> szController.h = Theme.int
			}
		}
		// определить ректы всех тайлов
		buttonsRects = Array(countTiles) { resolveTile(it, Rect()) }
		if(grid) pts = floatArrayOf()
		// карта контроллера
		setControllerMap(mapController)
	}
	
	/** Обновление позиции котролера на разметке */
	fun updatePosition(xPos: Int, yPos: Int) {
		position.set(xPos, yPos)
		var mx = 0
		var my = 0
		var xx = xPos - szController.w / 2
		var yy = yPos - szController.h / 2
		(parent as? ViewGroup)?.apply {
			mx = measuredWidth
			my = measuredHeight
		}
		if(xx < 0) xx = 0
		else if(mx > 0 && xx + szController.w > mx) xx = mx - szController.w
		if(yy < 0) yy = 0
		else if(my > 0 && yy + szController.h > my) yy = my - szController.h
		(layoutParams as? AbsoluteLayout.LayoutParams)?.apply {
			x = xx; y = yy
			width = szController.w
			height = szController.h
		}
		requestLayout()
	}
	
	// index of [controllerCharsMap] -> number bit to button of controller
	/** Установка карты [strMap] кнопок контроллера */
	fun setControllerMap(strMap: String) {
		val map = strMap.lines()
		val cols = map[0].length / 2
		val rows = map.size

		controllerMap = byteArrayOf2D(cols, rows)
		repeat(rows) {y ->
			repeat(cols) {x ->
				val f = 1 shl controllerCharsMap.search(map[y][x * 2 + 0], 0)
				val s = 1 shl controllerCharsMap.search(map[y][x * 2 + 1], 0)
				controllerMap[x, y] = (f or s).toByte()
			}
		}
		setSize(szController.w, szController.h)
	}

	/** Установка размера */
	fun setSize(width: Int, height: Int) {
		val cols = controllerMap[0].toInt()
		val rows = controllerMap[1].toInt()
		relativeSizeMap.set((cols - 1f) / width, (rows - 1f) / height)
		if(pts != null) pts = makeWired(cols, rows, width.toFloat(), height.toFloat(),
			width / cols.toFloat(), height / rows.toFloat())
		szController.w = width
		szController.h = height
	}

	override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
		super.onLayout(changed, left, top, right, bottom)
		if(tmpPt.x >= 0f && tmpPt.y >= 0f && visibility == View.VISIBLE) {
			(parent as? ViewGroup)?.apply {
				updatePosition((tmpPt.x * measuredWidth).toInt(), (tmpPt.y * measuredHeight).toInt())
			}
			tmpPt.x = -1f; tmpPt.y = -1f
		}
	}
	
	/** Обработка событий касания кнопок контроллера */
	override fun onTouchEvent(event: MotionEvent): Boolean {
		onButtons(onTouch(event))
		return true
	}
	
	/** Обработка события касания кнопок */
	open fun onButtons(touch: Touch) {
		var ret = DIRN
		val old = pressedButtons
		// определить куда нажали, тип нажатия, вызвать уведомитель и обновить представление
		touch.apply {
			if (press) {
				val xx = (relativeSizeMap.w * ptCurrent.x).roundToInt()
				val yy = (relativeSizeMap.h * ptCurrent.y).roundToInt()
				if (xx >= 0 && xx < controllerMap[0] && yy >= 0 && yy < controllerMap[1])
					ret = controllerMap[xx, yy]
			}
		}
		pressedButtons = ret
		controllerButtonNotify?.invoke(ret)
		if (old != ret) invalidate()
	}
	
	/** Сброс */
	open fun reset() { pressedButtons = DIRN }
	
	/** Опрос состояний кнопок */
	open fun buttonStates() = pressedButtons
	
	/** Отображение контроллера и кнопок с учетом их состояния */
	override fun draw(canvas: Canvas) {
		super.draw(canvas)
		bitmap?.let {
			if(pressedButtons != 0) {
				for(bit in 0..10) {
					if(pressedButtons bits bit) {
						canvas.drawBitmap(it, buttonsRects[bit + 1], drawablePosition, drawable.paint)
					}
				}
			}
		}
		// нарисовать сетку
		pts?.apply { canvas.drawLines(this, paint) }
	}
	
	/** Класс хранения позиций [x], [y] и признака видимости [vis] контроллера */
	private class ControllerState(@JvmField val x: Float, @JvmField val y: Float, @JvmField val vis: Int, state: Parcelable?): View.BaseSavedState(state)
	
	/** Сохранение состояния */
	override fun onSaveInstanceState(): Parcelable = ControllerState(position.x.toFloat() / ((parent as? ViewGroup)?.measuredWidth ?: 1),
	                                                                 position.y.toFloat() / ((parent as? ViewGroup)?.measuredHeight ?: 1),
	                                                                 visibility, super.onSaveInstanceState())
	
	/** Восстановление состояния */
	override fun onRestoreInstanceState(state: Parcelable?) {
		var st = state
		if(st is ControllerState) {
			tmpPt.x = st.x
			tmpPt.y = st.y
			visibility = st.vis
			st = st.superState
		}
		super.onRestoreInstanceState(st)
	}
}

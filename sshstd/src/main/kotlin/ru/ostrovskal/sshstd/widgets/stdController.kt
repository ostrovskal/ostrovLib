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
import ru.ostrovskal.sshstd.Common.DIRN
import ru.ostrovskal.sshstd.layouts.AbsoluteLayout
import ru.ostrovskal.sshstd.objects.ATTR_SSH_CONTROLLER_HEIGHT
import ru.ostrovskal.sshstd.objects.ATTR_SSH_CONTROLLER_WIDTH
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.objects.style_controller
import ru.ostrovskal.sshstd.utils.*
import kotlin.math.roundToInt

/**
 * @author  Шаталов С.В.
 * @since   0.2.1
 */

/** Символы карты контроллера */
@JvmField val controllerCharsMap = charArrayOf('F', 'U', 'D', 'L', 'R', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'S', 'D', 'W', ' ')

/** Карта контроллера по умолчанию 8x8 */
const val mapController = "ULULUUUUUUUUURUR\n" +
                          "ULULUUUUUUUUURUR\n" +
                          "LLLLULUFUFURRRRR\n" +
                          "LLLLLFFFFFRFRRRR\n" +
                          "LLLLLFFFFFRFRRRR\n" +
                          "LLLLDLDFDFDRRRRR\n" +
                          "DLDLDDDDDDDDDRDR\n" +
                          "DLDLDDDDDDDDDRDR"

/** Класс, реализующий контроллер управления игровыми событиями со стилем по умолчанию style_controller */
open class Controller(context: Context, ID: Int, show: Boolean, style: IntArray = style_controller): Tile(context, style) {
	
	// Позиция
	private var tmpPt                  = PointF(-1f, -1f)
	
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
		if(show) pts = floatArrayOf()
		// карта контроллера
		setControllerMap(mapController)
	}
	
	/** Обновление позиции котролера на разметке */
	fun updatePosition(xPos: Int, yPos: Int) {
		position.set(xPos, yPos)
		var mx = 0
		var my = 0
		val cellW = szController.w / sizeMap.w
		val cellH = szController.h / sizeMap.h
		var xx = xPos - szController.w / 2
		var yy = yPos - szController.h / 2
		(parent as? ViewGroup)?.apply {
			mx = measuredWidth
			my = measuredHeight
		}
		if(xx < -cellW) xx = -cellW
		else if(mx > 0 && xx + szController.w > mx) xx = mx - szController.w + cellW
		if(yy < -cellH) yy = -cellH
		else if(my > 0 && yy + szController.h > my) yy = my - szController.h + cellH
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
		relativeSizeMap.set((cols - 1f) / szController.w, (rows - 1f) / szController.h)
		if(pts != null) pts = makeWired(cols, rows, szController.w.toFloat(), szController.h.toFloat(),
		                         szController.w / cols.toFloat(), szController.h / rows.toFloat())
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
		onTouch(event)
		repeat(Touch.count) {
			onButtons(findTouch(it))
		}
		return true
	}
	
	/** Обработка события касания кнопок */
	open fun onButtons(touch: Touch?): Int {
		var ret = DIRN
		val flg = pressedButtons
		touch?.apply {
			// определить куда нажали, тип нажатия, вызвать уведомитель и обновить представление
			val xx = (relativeSizeMap.w * ptCurrent.x).roundToInt()
			val yy = (relativeSizeMap.h * ptCurrent.y).roundToInt()
			if(xx < 0 || xx >= controllerMap[0] || yy < 0 || yy >= controllerMap[1]) return pressedButtons
			ret = controllerMap[xx, yy]
		}
		pressedButtons = ret
		controllerButtonNotify?.invoke(pressedButtons)
		if(flg != ret) invalidate()
		return ret
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
				for(bit in 0..31) {
					if(pressedButtons bits bit) {
						canvas.drawBitmap(it, buttonsRects[bit + 1], drawablePosition, paint)
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

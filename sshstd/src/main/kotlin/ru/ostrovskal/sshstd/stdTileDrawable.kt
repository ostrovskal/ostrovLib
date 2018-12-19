@file:Suppress("DEPRECATION")

package ru.ostrovskal.sshstd

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.objects.*
import ru.ostrovskal.sshstd.utils.*

/**
 * @author Шаталов С.В.
 * @since 0.1.0
 */

/** Патч9. Левая сторона */
const val VL = 1.toByte()

/** Патч9. Верхняя сторона */
const val VT = 2.toByte()

/** Патч9. Правая сторона */
const val VR = 3.toByte()

/** Патч9. Нижняя сторона */
const val VB = 4.toByte()

/** Карта для патч9 */
@JvmField val mapPatch = byteArrayOf(VL,  0, VT,  0, VL,  1, VT,  1,
                                     VR, -1, VT,  0, VR,  0, VT,  1,
                                     VL,  0, VB, -1, VL,  1, VB,  0,
                                     VR, -1, VB, -1, VR,  0, VB,  0,
                                     VL,  0, VT,  1, VL,  1, VB, -1,
                                     VR, -1, VT,  1, VR,  0, VB, -1,
                                     VL,  1, VT,  0, VR, -1, VT,  1,
                                     VL,  1, VB, -1, VR, -1, VB,  0,
                                     VL,  1, VT,  1, VR, -1, VB, -1)

/** Класс, реализующий управление картинкой, имеющей тайловую структуру
 *
 * @property context    Контекст
 */
open class TileDrawable(private val context: Context, style: IntArray) : Drawable() {
	
	// Количество тайлов по вертикали
	private var vert                        = 1
		set(v)                              { field = if(v <= 0) 1 else v }
	
	// Количество тайлов по горизонтали
	private var horz                        = 1
		set(v)                              { field = if(v <= 0) 1 else v }

	// Область для восстановления клиппинга
	private val clipRect                    = Rect()
	
	// Иконка
	private var drawableIcon: TileDrawable? = null
	
	// Рисователь выделения
	private val paintSelector               = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.style = Paint.Style.STROKE }
	
	// Рисователь тени
	private val paintShadow                 = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply { colorFilter = fltShadowed }
	
	/** Рисователь */
	@JvmField val paint                     = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
	
	/** Признак обновления содержимого */
	@JvmField var isRedraw                  = false
	
	/** Фон */
	@JvmField var background: Drawable?     = null
	
	/** Контур */
	@JvmField val path                      = Path()

	/** Имя рисунка */
	@JvmField var keyBitmap                 = ""
	
	/** Размеры тайла */
	@JvmField val tileSize                  = Size(0, 0)
	
	/** Область тайла в картинке */
	@JvmField val tileRect                  = Rect()
	
	/** Количество тайлов */
	val countTiles
		get()                               = horz * vert
	
	/** Признак отображения фона */
	var isShowBackground                    = true
		set(v)                              { field = v; redrawSelf(false) }
	
	/** Внутрений отступ */
	var padding                             = Rect()
		set(v)                              { field = v; if(keyBitmap.isNotEmpty()) setBitmap(keyBitmap, horz, vert, tile); updateBound(null) }
	
	/** Выравнивание */
	var align						        = TILE_GRAVITY_NONE
		set(v)						        { field = v; updateBound(null) }
	
	/** Выравнивание значка */
	var alignIcon						    = drawableIcon?.align ?: TILE_GRAVITY_NONE
		set(v)						        { drawableIcon?.align = v; updateBound(null) }
	
	/** Масштаб иконки */
	var scaleIcon                           = 0.5f
		set(v)                              { field = v; updateBound(null) }
	
	/** Состояние */
	var states                              = TILE_STATE_NONE
		set(v)                              { field = v; updateBound(null) }
	
	/** Масштабирование */
	var scale                               = TILE_SCALE_NONE
		set(v)                              { field = v; updateBound(null) }
	
	/** Номер тайла */
	var tile                                = -1
		set(v)                              { field = v; resolveTile(v, tileRect); redrawSelf(false) }
	
	/** Фильтр */
	var filter: ColorFilter?
		get()                               = paint.colorFilter
		set(v)                              { paint.colorFilter = v; drawableIcon?.filter = v; redrawSelf(false) }
	
	/** Смещение тени */
	var shadowOffset                        = 0f
		set(v)                              { field = v; updateBound(null) }
	
	/** Значок */
	var tileIcon                            = drawableIcon?.tile ?: -1
		set(v)                              {
			drawableIcon = if(v == -1) null else { (drawableIcon ?: TileDrawable(context, style_icon)).apply { tile = v } }
			updateBound(bounds)
		}
	
	/** Картинка с тайлами */
	val bitmap
		get()                               = context.bitmapGetCache(keyBitmap)
	
	/** Размеры углов скругленного прямоугольника */
	var radii                               = floatArrayOf(10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f)
		set(v)                              { field = v; updateBound(null) }
	
	/** Фигура */
	var shape                               = TILE_SHAPE_EMPTY
		set(v)                              { field = v; updateBound(null) }
	
	/** Цвет рамки */
	var selectorColor
		get()                               = paintSelector.color
		set(v)                              { paintSelector.color = v; redrawSelf(false) }
	
	/** Толщина рамки */
	var selectorWidth
		get()                               = paintSelector.strokeWidth
		set(v)                              { paintSelector.strokeWidth = v; updateBound(null) }
	
	/** Область обрезки картинки */
	var patch9                              = Rect()
		set(v)                              {
			val d = dMetrics.density
			field.left = (v.left * d).toInt()
			field.right = (v.right * d).toInt()
			field.top = (v.top * d).toInt()
			field.bottom = (v.bottom * d).toInt()
			redrawSelf(false)
		}
	
	/** Угол отображения */
	var angle                               = 0f
		set(v)                              { field = v; redrawSelf(false) }
	
	/** Масштаб отображения */
	var scaleFactor                         = 1f
		set(v)                              { field = v; redrawSelf(false) }
	
	/** Направление градиентной заливки фона */
	var gradientDir                         = DIRU
		set(v)                              {
			field = v
			(background as? GradientDrawable)?.apply { orientation = Common.gradient[v] }
			redrawSelf(false)
		}
	
	/** Градиентная заливка в качестве фона */
	var gradient                            = intArrayOf(0, 0)
		set(v)                              {
			field = v
			val orien = Common.gradient[gradientDir]
			background = (background as? GradientDrawable)?.apply { colors = v; orientation = orien } ?: GradientDrawable(orien, v)
			redrawSelf(false)
		}
	
	/** Сплошной цвет в качестве фона */
	var solid                               = Color.WHITE
		set(v)                              {
			field = v
			background = (background as? ColorDrawable)?.apply { color = v } ?: ColorDrawable(v)
			redrawSelf(false)
		}
	
	init {
		style.loopAttrs { attr, value ->
			Theme.attrProps(context, attr, value)
			when(attr) {
				ATTR_SSH_ALPHA          -> alpha = Theme.int
				ATTR_SSH_GRAVITY        -> align = Theme.int
				ATTR_SSH_STATES         -> states = Theme.int
				ATTR_SSH_PRESSED_OFFS   -> shadowOffset = Theme.flt
				ATTR_SSH_SCALE          -> scale = Theme.int
				ATTR_SSH_ICON           -> tileIcon = Theme.int
				ATTR_SSH_SOLID          -> solid = Theme.int
				ATTR_SSH_GRADIENT       -> gradient = Theme.str.toIntArray(2, Color.WHITE, 0,true, ',')
				ATTR_SSH_GRADIENT_DIR   -> gradientDir = Theme.int
				ATTR_SSH_SHAPE          -> shape = Theme.int
				ATTR_SSH_BACKGROUND     -> background = Theme.drw
				ATTR_SSH_COLOR_SELECTOR -> selectorColor = Theme.int
				ATTR_SSH_WIDTH_SELECTOR -> selectorWidth = Theme.int.toFloat()
				ATTR_SSH_RADII          -> radii = Theme.str.toFloatArray(8, 10f)
				ATTR_SSH_PATCH9         -> patch9 = Theme.str.toRect(0, 0)
				ATTR_SSH_BITMAP_NAME    -> keyBitmap = Theme.str
				ATTR_SSH_VERT           -> vert = Theme.int
				ATTR_SSH_HORZ           -> horz = Theme.int
				ATTR_SSH_TILE           -> tile = Theme.int
				ATTR_SSH_SCALE_ICON     -> scaleIcon = Theme.int / 65536f
				ATTR_SSH_GRAVITY_ICON   -> alignIcon = Theme.int
			}
		}
		setBitmap(keyBitmap, horz, vert, tile)
	}
	
	/** Установка прозрачности [alpha] */
	override fun setAlpha(alpha: Int) { paint.alpha = alpha; redrawSelf(false) }
	
	/** Признак прозрачности */
	override fun getOpacity(): Int = PixelFormat.OPAQUE
	
	/** Установка фильтра отображения [colorFilter] */
	override fun setColorFilter(colorFilter: ColorFilter?) { filter = colorFilter }
	
	/**
	 * Установка рисунка
	 *
	 * @param k  Имя рисунка
	 * @param cs Количество столбцов
	 * @param rs Количество строк
	 * @param nm Номер тайла
	 */
	fun setBitmap(k: String, cs: Int = 1, rs: Int = 1, nm: Int = 0) {
		keyBitmap = k
		if(keyBitmap.isNotEmpty()) {
			bitmap?.apply {
				horz = cs
				vert = rs
				tileSize.w = (width - (padding.left + padding.right)) / horz
				tileSize.h = (height - (padding.top + padding.bottom)) / vert
			}
		}
		else {
			tileSize.w = 1
			tileSize.h = 1
			horz = 1
			vert = 1
		}
		tile = nm
	}
	
	/** Вычисление габаритов тайла [n] и запись его в [r] */
	fun resolveTile(n: Int, r: Rect): Rect {
		if(n in 0..(countTiles - 1)) {
			val tx = (n % horz * tileSize.w) + padding.left
			val ty = (n / horz * tileSize.h) + padding.top
			r.set(tx, ty, tx + tileSize.w, ty + tileSize.h)
		} else r.setEmpty()
		return r
	}
	
	/** Рассчитанная ширина */
	override fun getIntrinsicWidth() = bounds.width()
	
	/** Рассчитанная высота */
	override fun getIntrinsicHeight() = bounds.height()
	
	/** Обновление габаритов */
	fun updateBound(rc: Rect?) {
		val r = rc ?: bounds
		val w: Int
		val h: Int
		var xx = r.left
		var yy = r.top
		val ww = r.width()
		val hh = r.height()
		if(ww > 0 && hh > 0) {
			val rel = tileSize.w.toFloat() / tileSize.h
			// расчитать размер в зависимости от типа масштабирования
			when(scale) {
				TILE_SCALE_TILE   -> {
					w = tileSize.w
					h = tileSize.h
				}
				TILE_SCALE_MIN    -> {
					h = if(ww < hh) ww else hh
					w = (h * rel).toInt()
				}
				TILE_SCALE_HEIGHT -> {
					h = hh
					w = (h * rel).toInt()
				}
				TILE_SCALE_WIDTH  -> {
					w = ww
					h = (w * rel).toInt()
				}
				else              -> {
					w = ww
					h = hh
				}
			}
			if(align ntest TILE_GRAVITY_NONE) {
				xx += when(align and TILE_GRAVITY_MASK_HORZ) {
					TILE_GRAVITY_END         -> ww - w
					TILE_GRAVITY_CENTER_HORZ -> (ww - w) / 2
					else                     -> 0
				}
			}
			yy += when(align and TILE_GRAVITY_MASK_VERT) {
				TILE_GRAVITY_BOTTOM      -> hh - h
				TILE_GRAVITY_CENTER_VERT -> (hh - h) / 2
				else                     -> 0
			}
			fRect.set(xx.toFloat(), yy.toFloat(), xx + w.toFloat(), yy + h.toFloat())
			// уменьшаем области на половину размера границы
			val border = selectorWidth / 2.0f
			fRect.inset(border, border)
			// создать фигуру
			path.makeFigure(shape, fRect, radii)
			background?.bounds = fRect.toInt(iRect)
			// уменьшить на тень
			if(states test (TILE_STATE_SHADOW or TILE_STATE_PRESS)) fRect.inset(shadowOffset, shadowOffset)
			fRect.toInt(iRect)
			if(bounds != iRect) {
				bounds?.set(iRect)
				redrawSelf(true)
			}
			// пересчитать иконку
			drawableIcon?.updateBound(bounds)
		}
	}
	
	/** Вызывается при изменение габаритов [r] */
	override fun onBoundsChange(r: Rect) {
		updateBound(r)
	}
	
	/** Отрисовка */
	override fun draw(canvas: Canvas) {
		val rect = bounds
		// ограничительная фигура
		if(shape != TILE_SHAPE_EMPTY) {
			// обводка
			if(selectorWidth > 0f) canvas.drawPath(path, paintSelector)
			canvas.getClipBounds(clipRect)
			canvas.clipPath(path)
		}
		// фон
		if(isShowBackground) background?.draw(canvas)
		// картинка
		if(keyBitmap.isNotEmpty() && !tileRect.isEmpty) {
			// битмап или патч9
			context.bitmapGetCache(keyBitmap)?.apply {
				if(patch9.isZero) drawShadow(this, canvas, rect)
				else drawPatch9(this, canvas, rect)
			}
		}
		// иконка
		drawableIcon?.apply {
			canvas.scale(scaleIcon, scaleIcon, rect.centerX().toFloat(), rect.centerY().toFloat())
			draw(canvas)
		}
		// восстановление ограничительной фигуры
		if(shape != TILE_SHAPE_EMPTY) canvas.clipRect(clipRect, Region.Op.REPLACE)
	}
	
	private fun drawPatch9(bitmap: Bitmap, canvas: Canvas, rect: Rect) {
		var x = 0; var y = 0
		
		fun getVal(idx: Int) {
			val f = mapPatch[idx + 0]
			var v = mapPatch[idx + 1].toInt()
			when(f) {
				VT -> { v *= patch9.top;     x = rect.top    + v; y = tileRect.top      + v }
				VR -> { v *= patch9.right;   x = rect.right  + v; y = tileRect.right    + v }
				VL -> { v *= patch9.left;    x = rect.left   + v; y = tileRect.left     + v }
				VB -> { v *= patch9.bottom;  x = rect.bottom + v; y = tileRect.bottom   + v }
			}
		}
		
		for(i in 0..64 step 8) {
			getVal(i + 0); iRect.left     = y; fRect.left   = x.toFloat()
			getVal(i + 2); iRect.top      = y; fRect.top    = x.toFloat()
			getVal(i + 4); iRect.right    = y; fRect.right  = x.toFloat()
			getVal(i + 6); iRect.bottom   = y; fRect.bottom = x.toFloat()
			canvas.drawBitmap(bitmap, iRect, fRect, paint)
		}
	}
	
	private fun drawShadow(bitmap: Bitmap, canvas: Canvas, rect: Rect) {
		// есть тень, либо возможность нажатия
		if(states test (TILE_STATE_PRESS or TILE_STATE_SHADOW)) {
			val degree = Math.toRadians((405.0 - angle))
			val sx = (shadowOffset * Math.cos(degree)).toFloat()
			val sy = (shadowOffset * Math.sin(degree)).toFloat()
			canvas.translate(sx, sy)
			if(paint.colorFilter != fltPressed && states test TILE_STATE_SHADOW) {
				// рисуем тень, если не нажали и тень есть
				canvas.drawBitmap(bitmap, tileRect, rect, paintShadow)
				canvas.translate(-sx, -sy)
			}
		} else {
			if(scaleFactor != 1f || angle != 0f) {
				val x = rect.centerX().toFloat()
				val y = rect.centerY().toFloat()
				canvas.rotate(angle, x, y)
				canvas.scale(scaleFactor, scaleFactor, x, y)
			}
		}
		canvas.drawBitmap(bitmap, tileRect, rect, paint)
	}
	
	/** Изменение состояния отображения */
	override fun onStateChange(state: IntArray?): Boolean {
		filter = when(state?.checkStates(STATE_PRESSED, STATE_FOCUSED)) {
			STATE_DISABLED 	-> fltDisabled
			STATE_PRESSED	-> when {
				states test TILE_STATE_HOVER -> fltHovered
				states test TILE_STATE_PRESS -> fltPressed
				else                          -> null
			}
			else			-> null
		}
		return true
	}
	
	/** Обновление себя при установке габаритов - отключено */
	override fun invalidateSelf() {}
	
	private fun redrawSelf(type: Boolean) {
		isRedraw = type
		super.invalidateSelf()
	}
	
	/** "Горячая" смена темы отображения */
	open fun onChangeTheme() {
		setBitmap(keyBitmap, horz, vert, tile)
		redrawSelf(false)
	}
/*
	
	override fun getPadding(padding: Rect): Boolean {
		padding.set(30, 10, 30, 10)
		return true
	}
*/
}
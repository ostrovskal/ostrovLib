package ru.ostrovskal.sshstd.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Parcelable
import android.view.View
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.utils.*
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * @author Шаталов С.В.
 * @since 0.2.0
*/

/** Класс, реализующий полосу продвижения */
open class Progress(context: Context, id: Int, max: Int, mode: Int, style: IntArray): Tile(context, style) {

	private val rectProgress	= Rect()

	// Угол отображения текста
	private var rot             = 0f
	
	// Вторичная область прогресса
	private val rectSecondary   = Rect()
	
	// Первичная область прогресса
	private val rectPrimary     = Rect()
	
	/** Максимум */
	var max                     = 100
		set(v)                  { field = v; measure() }
	
	/** Градиентные цвета областей */
	var colors                  = intArrayOf(0x404040.color, 0x202020.color, 0x405060.color, 0x806040.color, 0x007000.color, 0x800000.color)
		set(v)                  { field = v; invalidate() }
	
	/** Режим отображения */
	var mode                    = SSH_MODE_DIAGRAM
		set(v)                  { field = v; if(v == SSH_MODE_CIRCULAR) rot = 0f; measure() }
	
	/** Направление отображения */
	var direction               = DIRR
		set(v)                  {
			field = v
			drawable.gradientDir = when(v) {
				DIRD         -> DIRL
				DIRR         -> DIRU
				DIRU         -> DIRR
				else         -> DIRD
			}
			if(mode == SSH_MODE_DIAGRAM && (v == DIRU || v == DIRD)) rot = 90f
			measure()
		}

	/** Величина первичного прогресса */
	var primaryProgress
		get()                   = data.toInt()
		set(v)                  { data = ((0..max).clamp(v)).toFloat(); measure() }
	

	/** Величина вторичного прогресса */
	var secondaryProgress       = 0
		set(v)                  { field = (primaryProgress..max).clamp(v); measure() }
	
	/** Признак отображения текста */
	var isShowText              = false
		set(v)                  { field = v; invalidate() }
	
	init {
		doFrame = { _, _, frame, _, _ ->
			drawable.angle = 30f * (frame % 12)
			invalidate()
			false
		}

		paint.textAlign = Paint.Align.CENTER
		animator.apply { duration = 70; frames = Int.MAX_VALUE }
		
		//Theme.setBaseAttr(context, this, style)
		
		style.loopAttrs { attr, value ->
			Theme.attrProps(context, attr, value)
			when(attr) {
				ATTR_SSH_ANIMATOR_DURATION  -> animator.duration = Theme.int
				ATTR_SSH_GRADIENT_DIR       -> direction = Theme.int
				ATTR_SSH_SHOW               -> isShowText = Theme.bol
				ATTR_SSH_COLORS             -> colors = Theme.str.toIntArray(6, 0xffffff, 10, true, ',')
			}
		}
		this.id = id
		this.max = max
		this.mode = mode
	}
	
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		measure()
		if(mode == SSH_MODE_CIRCULAR && !animator.isRunning) animator.start(stop = true, reset = true)
	}
	
	private fun measure() {
		val w = measuredWidth
		val h = measuredHeight
		if(w != 0 && h != 0) {
			val pl = paddingStart
			val pr = paddingEnd
			val pt = paddingTop
			val pb = paddingBottom
			drawable.bounds.setEmpty()
			if (mode == SSH_MODE_DIAGRAM) {
				fun measureProgress(v: Int, r: Rect) {
					if (v >= 0) {
						var mx = max.toFloat()
						if(mx < Float.MIN_VALUE) mx = Float.MIN_VALUE
						val vw = (((w - pr - pl) / mx) * v).roundToInt()
						val vh = (((h - pb - pt) / mx) * v).roundToInt()

						var x1 = 0
						var y1 = 0
						var x2 = 0
						var y2 = 0

						//"$direction pl:$pl pr:$pr pt:$pt pb:$pb w:$w h:$h vw:$vw vh:$vh v: $v".info()
						when (direction) {
							DIRL -> { x1 = (w - vw) - pl; y1 = pt; x2 = w - pr; y2 = h - pb }
							DIRR -> { x1 = pl; y1 = pt; x2 = vw + pr; y2 = h - pb }
							DIRU -> { x1 = pl; y1 = (h - vh) - pt; x2 = w - pr; y2 = h - pb }
							DIRD -> { x1 = pl; y1 = pt; x2 = w - pr; y2 = vh + pb }
						}
						r.set(x1, y1, x2, y2)
					} else r.set(0, 0, 0, 0)
				}
				measureProgress(max, rectProgress)
				measureProgress(primaryProgress, rectPrimary)
				measureProgress((secondaryProgress - primaryProgress), rectSecondary)
				rectSecondary.apply {
					val ws = rectPrimary.width()
					val hs = rectPrimary.height()
					when (direction) {
						DIRL -> offset(-ws, 0)
						DIRR -> offset(ws, 0)
						DIRU -> offset(0, -hs)
						DIRD -> offset(0, hs)
					}
				}
			} else {
				iRect.set(pl, pt, w - pr, h - pb)
				drawable.updateBound(iRect)
				drawablePosition.offset(0, 0, rectProgress)
				val c = min(rectProgress.width(), rectProgress.height()) / 2
				val cx = rectProgress.centerX()
				val cy = rectProgress.centerY()
				rectProgress.set(cx - c, cy - c, cx + c, cy + c)
			}
			invalidate()
		}
	}

	private	fun drawDiagram(canvas: Canvas, idx: Int, r: Rect) {
		if(!r.isEmpty) {
			drawable.apply {
				xyInt[0] = colors[idx]; xyInt[1] = colors[idx + 1]
				gradient = xyInt
				bounds = r
				draw(canvas)
			}
		}
	}

	/** Отображение прогресса */
	override fun draw(canvas: Canvas) {
		super.draw(canvas)
		val cx = rectProgress.centerX().toFloat()
		val cy = rectProgress.centerY().toFloat()
		canvas.withSave {
			when(mode) {
				SSH_MODE_DIAGRAM -> {
					drawDiagram(this, 0, rectProgress)
					drawDiagram(this, 2, rectPrimary)
					drawDiagram(this, 4, rectSecondary)
				}
				SSH_MODE_CIRCULAR -> {
					bitmap?.apply {
						rotate(drawable.angle, cx, cy)
						drawBitmap(this, tileRect, rectProgress, paint)
					}
				}
			}
		}
		if(isShowText) {
			canvas.withSave {
				rotate(rot, cx, cy)
				drawText("${primaryProgress.toPercent(max)}%", cx, cy - paint.fontMetrics.ascent / 2f, paint)
			}
		}
	}
	
	/** Класс хранения состояния [sec] прогресса */
	private class ProgressState(@JvmField val sec: Int, state: Parcelable?): View.BaseSavedState(state)

	/** Сохранение состояния */
	override fun onSaveInstanceState(): Parcelable = ProgressState(secondaryProgress, super.onSaveInstanceState())
	
	/** Восстановление состояния */
	override fun onRestoreInstanceState(state: Parcelable?) {
		var st = state
		if(st is ProgressState) {
			secondaryProgress = st.sec
			st = st.superState
		}
		super.onRestoreInstanceState(st)
		requestLayout()
	}
}

package ru.ostrovskal.sshstd.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Parcelable
import android.view.View
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.objects.*
import ru.ostrovskal.sshstd.utils.*

/**
 * @author Шаталов С.В.
 * @since 0.2.0
*/

/** Класс, реализующий полосу продвижения */
open class Progress(context: Context, id: Int, max: Int, mode: Int, style: IntArray): Tile(context, style) {
	
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
			if(v == DIRU || v == DIRD) rot = 90f
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
		
		Theme.setBaseAttr(context, this, style)
		
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
		if(mode == SSH_MODE_DIAGRAM) {
			val pl = paddingStart
			val pr = paddingEnd
			val pt = paddingTop
			val pb = paddingBottom
			
			fun measureProgress(v: Int, r: Rect) {
				if(v >= 0) {
					val mx = max.toFloat()
					val vw = (((w - pr - pl) / mx) * v).toInt()
					val vh = (((h - pt - pb) / mx) * v).toInt()
					
					var x1 = 0; var y1 = 0
					var x2 = 0; var y2 = 0
					
					when(direction) {
						DIRL -> { x1 = pl + w - vw; y1 = pt; x2 = w - pr; y2 = h - pb }
						DIRR -> { x1 = pl; y1 = pt; x2 = vw; y2 = h - pb }
						DIRU -> { x1 = pl; y1 = pt + h - vh; x2 = w - pr; y2 = h - pb }
						DIRD -> { x1 = pl; y1 = pt; x2 = w - pr; y2 = vh }
					}
					r.set(x1, y1, x2, y2)
				} else r.set(0, 0, 0, 0)
			}
			rectScreen.set(0, 0, w, h)
			measureProgress(primaryProgress, rectPrimary)
			measureProgress((secondaryProgress - primaryProgress), rectSecondary)
			when(direction) {
				DIRL        -> rectSecondary.offset(-rectPrimary.width(), 0)
				DIRR        -> rectSecondary.offset(rectPrimary.width(), 0)
				DIRU        -> rectSecondary.offset(0, -rectPrimary.height())
				DIRD        -> rectSecondary.offset(0, rectPrimary.height())
			}
		} else {
			iRect.set(0, 0, w, h)
			drawable.updateBound(iRect)
			drawablePosition.offset(0, 0, rectScreen)
		}
		invalidate()
	}
	
	/** Отображение прогресса */
	override fun draw(canvas: Canvas) {
		super.draw(canvas)
		val cx = rectScreen.centerX().toFloat()
		val cy = rectScreen.centerY().toFloat()
		when(mode) {
			SSH_MODE_DIAGRAM  -> drawable.apply {
				fun drawDiagram(idx: Int, r: Rect) {
					if(!r.isEmpty) {
						gradient[0] = colors[idx]; gradient[1] = colors[idx + 1]
						gradient = gradient
						bounds = r
						draw(canvas)
					}
				}
				drawDiagram(0, rectScreen)
				drawDiagram(2, rectPrimary)
				drawDiagram(4, rectSecondary)
			}
			SSH_MODE_CIRCULAR -> {
				canvas.withSave {
					bitmap?.apply {
						rotate(drawable.angle, cx, cy)
						drawBitmap(this, tileRect, rectScreen, paint)
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

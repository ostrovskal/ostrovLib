package ru.ostrovskal.sshstd.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Parcelable
import android.view.MotionEvent
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.utils.offset
import ru.ostrovskal.sshstd.utils.themeAttrValue

/**
 * @author Шаталов С.В.
 * @since  0.1.4
 */

/** Класс Переключателя(switch) */
open class Switch(context: Context, id: Int, text: Int, style: IntArray) : Tile(context, style) {
	
	// Начальная гор. позиция ползунка
	private var x               = 0
	
	// Реальная ширина ползунка
	private var realWidthThumb  = 0
	
	// Габариты тайла ползунка
	private var tileThumb 		= Rect()
	
	// Позиция ползунка
	private var posThumb		= 0
	
	// Величина смещения ползунка
	private var deltaThumb		= 0f
	
	/** Признак активности */
	override var isChecked: Boolean
		get() 					= super.isChecked
		set(v) 					{ if(v != isChecked) { animator.start(stop = true, reset = true) } else performClick() }
	
	init {
		doFrame = { _, animator, frame, direction, _ ->
			posThumb = if(isChecked) 10 - frame else frame
			invalidate()
			((direction == 1 && frame == animator.frames) || (direction == -1 && frame == 0)).apply {
				if(this) { data = posThumb.toFloat(); isChecked = data.toInt() != 0 }
			}
		}

		realWidthThumb = (Theme.integer(context, style.themeAttrValue(ATTR_SSH_THUMB_WIDTH, 32)) * dMetrics.density).toInt()
		animator.apply { duration = 20; frames = 10 }
		this.id = id
		setText(text)
	}
	
	/** Обработка события касания */
	override fun onTouchEvent(event: MotionEvent): Boolean {
		if(isEnabled && (posThumb == 0 || posThumb == animator.frames)) {
			touch.event(event).click(rectScreen) {
				isChecked = !isChecked
			}
		}
		return true
	}
	
	override fun onRestoreInstanceState(state: Parcelable?) {
		super.onRestoreInstanceState(state)
		if(animator.isRunning && data.toInt() == 0) animator.reverse()
		else { val chk = data.toInt() != 0; data = 0f; isChecked = chk }
	}
	
	override fun onChangeTheme() {
		super.onChangeTheme()
		switchChange()
	}

	private fun switchChange() {
		resolveTile(1, tileThumb)
		val w = drawablePosition.width().toFloat()
		val h = (w / tileSize.w) * realWidthThumb
		deltaThumb = (w - h) / 10f
		x = (scrollX + width - (paddingRight + w)).toInt()
		y -= 2
	}
	
	/** Определение позиции переключателя */
	override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
		super.onLayout(changed, left, top, right, bottom)
		switchChange()
	}
	
	/** Отображение переключателя */
	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		bitmap?.apply {
			canvas.drawBitmap(this, tileThumb, drawablePosition.offset((x + deltaThumb * posThumb).toInt(), y, iRect), drawable.paint)
		}
	}
}

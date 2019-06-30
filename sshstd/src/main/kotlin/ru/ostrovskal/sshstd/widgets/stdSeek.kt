package ru.ostrovskal.sshstd.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.MotionEvent
import ru.ostrovskal.sshstd.Common
import ru.ostrovskal.sshstd.Common.SEEK_ANIM_NONE
import ru.ostrovskal.sshstd.Common.iRect
import ru.ostrovskal.sshstd.objects.ATTR_SSH_SEEK_ANIM
import ru.ostrovskal.sshstd.objects.THEME
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.onTouch
import ru.ostrovskal.sshstd.utils.*

/**
 * @author Шаталов С.В.
 * @since  0.1.5
*/

/** Класс, реализующий слайдер со стилем по умолчанию style_seek */
open class Seek(context: Context, id: Int, range: IntRange, enabled: Boolean, style: IntArray) : Tile(context, style) {
	
	// Величина смещения ползунка
	private var deltaThumb	= 0f
	
	// Габариты тайла трека
	private var tileTrack	= Rect()
	
	// Область, которую занимает ползунок при начальных вычислениях
	private val rect        = Rect()
	
	/** Диапазон */
	@JvmField var range		= 0..30
	
	/** Анимации ползунка */
	var animThumb           = SEEK_ANIM_NONE
		set(v)	            { field = v; resetThumbAnimator(); requestLayout() }
	
	/** Скорость анимации трека */
	var animSpeedTrack      = 2
		set(v)              { field = (v * config.multiplySW).toInt(); requestLayout() }
	
	/** Не используется - заглушка */
	final override var isChecked    = false
	
	/** Признак запуска анимации */
	var animated
		get()               = animator.isRunning
		set(v)				{ if(v) animator.start(stop = false, reset = false) else animator.stop() }
	
	/** Величина прогресса */
	var progress
		get()				= (data * ((range.interval / 1000f)) + range.first).toInt()
		set(v)				{ data = (range.clamp(v) - range.first) * (1000f / range.interval); invalidate() }

	init {
		doFrame = { _, _, frame, _, _ ->
			val f = frame % 12
			var result = false
			if(animThumb == SEEK_ANIM_NONE) {
				if(animSpeedTrack == 0) result = true else invalidate()
			} else {
				if (animThumb test Common.SEEK_ANIM_ROTATE) {
					drawable.angle = 30f * f
				}
				if (animThumb test Common.SEEK_ANIM_SCALE) {
					drawable.zoom = if (f >= 6) 1f - (12 - f) * 0.08333f else 1f - f * 0.08333f
				}
			}
			result
		}

		onChangeTheme()
		animator.apply { duration = 50; frames = Int.MAX_VALUE }
		this.id = id
		this.range = range
		isEnabled = enabled
	}
	
	// Сброс параметров анимации ползунка
	private fun resetThumbAnimator() {
		drawable.angle = 0f
		drawable.zoom = 1f
	}
	
	override fun onChangeTheme() {
		super.onChangeTheme()
		animThumb = Theme.integer(context, style.themeAttrValue(ATTR_SSH_SEEK_ANIM, ATTR_SSH_SEEK_ANIM or THEME))
		resolveTile(1, tileTrack)
	}
	
	/** Обработка события касания */
	override fun onTouchEvent(event: MotionEvent): Boolean {
		onTouch(event)?.apply {
			val resolvePosition = (0..1000).clamp(((ptCurrent.x - leftPadding - scrollX) / deltaThumb).toInt())
			val oldProgress = progress
			data = resolvePosition.toFloat()
			if(oldProgress != progress) {
				performClick()
				invalidate()
			}
		}
		return true
	}
	
	/** Установка доступности [enabled] */
	override fun setEnabled(enabled: Boolean) {
		super.setEnabled(enabled)
		if(!enabled) resetThumbAnimator()
		animated = enabled
	}
	
	/** Позиционирование слайдера */
	override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
		super.onLayout(changed, left, top, right, bottom)
		if(changed) {
			resolveTile(2, tileTrack)
			rect.set(drawablePosition)
			deltaThumb = (right - left - horizontalPadding) / 1000f
		}
		if(isEnabled != animated) animated = isEnabled
	}
	
	/** Отображение слайдера */
	override fun onDraw(canvas: Canvas) {
		val w = rect.width()
		if(w > 0) {
			bitmap?.let {
				// трэк
				val count =(width / w) + 1
				canvas.withSave {
					clipRect(leftPadding, topPadding, width - rightPadding, height - bottomPadding)
					val f = (animator.frame * animSpeedTrack) % w
					for(i in 0..count) {
						drawBitmap(it, tileTrack, rect.offset(i * w - f, y, iRect), drawable.paint)
					}
				}
			}
			rect.offset((scrollX + data * deltaThumb - w / 2f).toInt(), 0, drawable.bounds)
			super.onDraw(canvas)
		}
	}
}

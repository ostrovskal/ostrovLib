package ru.ostrovskal.sshstd.widgets.charts

import android.content.Context
import android.graphics.Paint
import ru.ostrovskal.sshstd.Common.ATTR_SSH_SHOW
import ru.ostrovskal.sshstd.TileDrawable
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.utils.themeAttrValue
import ru.ostrovskal.sshstd.widgets.Tile

/**
 * @author  Шаталов С.В.
 * @since   0.2.3
*/

/** Базовый класс диаграммы */
abstract class Chart(context: Context, style: IntArray) : Tile(context, style) {
	
	/** Текущий кадр анимации */
	protected var frame                         = 10
	
	/** Для отрисовки сегментов */
	protected val drawableSegments              = TileDrawable(context, style)
	
	/** Признак отображения текста */
	@JvmField var isShowText                    = false
	
	/** Цвета сегментов */
	var colorsSegments                          = intArrayOf()
		set(v)                                  { field = v; invalidate() }
	
	/** Текущие значения сегментов */
	abstract var currentValuesSegments: IntArray
	
	init {
		doFrame = { _, animator, frame, _, _ ->
			this.frame = frame
			measure()
			frame >= animator.frames
		}

		paint.textAlign = Paint.Align.CENTER
		isShowText = Theme.boolean(context, style.themeAttrValue(ATTR_SSH_SHOW, 0))
	}
	
	/** Запуск анимации */
	fun startAnimation() { animator.start(stop = true, reset = true) }
	
	/** Определение позиции */
	override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
		super.onLayout(changed, left, top, right, bottom)
		measure()
	}
	
	/** Вычисление внутренних характеристик диаграммы */
	abstract fun measure()
}

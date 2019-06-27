@file:Suppress("NOTHING_TO_INLINE")

package ru.ostrovskal.sshstd.layouts

import android.content.Context
import android.view.View
import android.view.ViewGroup
import ru.ostrovskal.sshstd.Common.WRAP
import ru.ostrovskal.sshstd.utils.horizontalPadding
import ru.ostrovskal.sshstd.utils.loopChildren
import ru.ostrovskal.sshstd.utils.verticalPadding
import kotlin.math.max

/**
 * @author  Шаталов С.В.
 * @since   0.3.0
 */

/** Класс, реализующий абсолютную разметку */
open class AbsoluteLayout(context: Context) : ViewGroup(context, null, 0) {
	/** Установка параметров разметки представления с горизонтальной позицией [x], вертикальной позицией [y], шириной [width] и высотой [height] */
	inline fun <T: View> T.lps(x: Int, y: Int, width: Int = WRAP, height: Int = WRAP) = lps(x, y, width, height) {}
	
	/** Установка параметров разметки представления с горизонтальной позицией [x], вертикальной позицией [y], шириной [width],
	 *  высотой [height] и инициализатором [init] */
	inline fun <T: View> T.lps(x: Int, y: Int, width: Int = WRAP, height: Int = WRAP, init: LayoutParams.() -> Unit): T {
		layoutParams = LayoutParams(width, height, x, y).apply { init() }
		return this
	}
	
	/** Вычисление габаритов разметки и дочерних представлений */
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		var maxHeight = 0
		var maxWidth = 0
		
		measureChildren(widthMeasureSpec, heightMeasureSpec)
		
		loopChildren {
			if(it.visibility == View.GONE) return@loopChildren
			(it.layoutParams as? LayoutParams)?.apply {
				val childRight = x + it.measuredWidth
				val childBottom = y + it.measuredHeight
				maxWidth = max(maxWidth, childRight)
				maxHeight = max(maxHeight, childBottom)
			}
		}
		maxWidth += horizontalPadding
		maxHeight += verticalPadding
		
		maxHeight = max(maxHeight, suggestedMinimumHeight)
		maxWidth = max(maxWidth, suggestedMinimumWidth)
		
		setMeasuredDimension(View.resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
		                     View.resolveSizeAndState(maxHeight, heightMeasureSpec, 0))
	}
	
	/** Вычисление позиций дочерних представлений */
	override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
		loopChildren {
			if(it.visibility == View.GONE) return@loopChildren
			(it.layoutParams as? LayoutParams)?.apply {
				val childLeft = paddingLeft + x
				val childTop = paddingTop + y
				it.layout(childLeft, childTop, childLeft + it.measuredWidth, childTop + it.measuredHeight)
			}
		}
	}
	
	/** Генерация параметров разметки дочерних представлений по умолчанию */
	override fun generateDefaultLayoutParams() = LayoutParams(WRAP, WRAP, 0, 0)
	
	/** Проверка на параметры разметки */
	override fun checkLayoutParams(source: ViewGroup.LayoutParams) = source is LayoutParams
	
	/** Генерация параметров разметки из родительской разметки */
	override fun generateLayoutParams(source: ViewGroup.LayoutParams) = LayoutParams(source)
	
	/**  Класс, реализующий параметры для абсолютной разметки */
	class LayoutParams : MarginLayoutParams {
		/** Гор. позиция */
		var x = 0
		
		/** Верт. позиция */
		var y = 0
		
		/** Инициализирующий конструктор */
		constructor(width: Int, height: Int, x: Int, y: Int) : super(width, height) {
			this.x = x
			this.y = y
		}
		
		/** Конструктор копии */
		constructor(source: ViewGroup.LayoutParams) : super(source)
	}
}

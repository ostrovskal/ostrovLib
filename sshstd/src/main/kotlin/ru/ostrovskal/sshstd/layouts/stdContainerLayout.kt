@file:Suppress("NOTHING_TO_INLINE")

package ru.ostrovskal.sshstd.layouts

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import ru.ostrovskal.sshstd.Common.MATCH
import ru.ostrovskal.sshstd.utils.*

/**
 * @author  Шаталов С.В.
 * @since   0.3.2
 */

/** Класс, реализующий разметку-контейнер
 *
 * @property percentWidth   Ширина, в процентах, относительно родителя
 * @property percentHeight  Высота, в процентах, относительно родителя
 * @property aligned        Выравниевание по умолчанию
 * */
open class ContainerLayout(context: Context, @JvmField protected var percentWidth: Int,
                           @JvmField protected var percentHeight: Int, @JvmField protected var aligned: Boolean):
		FrameLayout(context, null, 0) {
	
	/** Установка параметров разметки с шириной [width], высотой [height] и гравитацией [gravity] */
	inline fun <T: View> T.lps(width: Int = MATCH, height: Int = MATCH, gravity: Int = -1) = lps(width, height, gravity) {}
	
	/** Установка параметров разметки представления с шириной [width] и высотой [height], гравитацией [gravity] и инициализатором [init] */
	inline fun <T: View> T.lps(width: Int = MATCH, height: Int = MATCH, gravity: Int = -1, init: FrameLayout.LayoutParams.() -> Unit): T {
		layoutParams = FrameLayout.LayoutParams(width, height, gravity).apply { init() }
		return this
	}
	
	/** Вычисление габаритов представлений в лайауте */
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		val ww = percentWidth.fromPercent(MeasureSpec.getSize(widthMeasureSpec))
		val hh = percentHeight.fromPercent(MeasureSpec.getSize(heightMeasureSpec))
		
		loopChildren {
			if(it.visibility == View.GONE) return@loopChildren
			(it.layoutParams as? MarginLayoutParams)?.apply {
				val hPadding = horizontalPadding + horizontalMargin
				val vPadding = verticalPadding + verticalMargin
				val cwSpec = if(width == FrameLayout.LayoutParams.MATCH_PARENT)
					MeasureSpec.makeMeasureSpec(ww - hPadding, MeasureSpec.EXACTLY)
				else getChildMeasureSpec(widthMeasureSpec, hPadding, ww)
				val chSpec = if(height == FrameLayout.LayoutParams.MATCH_PARENT)
					MeasureSpec.makeMeasureSpec(hh - vPadding, MeasureSpec.EXACTLY)
				else getChildMeasureSpec(heightMeasureSpec, vPadding, hh)
				it.measure(cwSpec, chSpec)
			}
		}
		setMeasuredDimension(ww, hh)
	}
	
	/** Вычисление координат представлений в лайауте */
	override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
		val cw = r - l - horizontalPadding
		val ch = b - t - verticalPadding
		loopChildren {
			if(it.visibility == View.GONE) return@loopChildren
			val ww = it.measuredWidth
			val hh = it.measuredHeight
			val tt = paddingTop + if(aligned) (ch - hh) / 2 else 0
			val ll = paddingLeft + if(aligned) (cw - ww) / 2 else 0
			it.layout(ll, tt, ll + ww, tt + hh)
		}
	}
}

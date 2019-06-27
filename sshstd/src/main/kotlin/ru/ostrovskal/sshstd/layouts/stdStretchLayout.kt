package ru.ostrovskal.sshstd.layouts

import android.content.Context
import android.view.View
import ru.ostrovskal.sshstd.Common.MATCH
import ru.ostrovskal.sshstd.Common.WRAP
import ru.ostrovskal.sshstd.utils.horizontalPadding
import ru.ostrovskal.sshstd.utils.loopChildren
import ru.ostrovskal.sshstd.utils.verticalPadding
import kotlin.math.roundToInt

/**
 * @author  Шаталов С.В.
 * @since   0.3.3
 */

/** Класс, реализующий авто растягивающуюся линейную разметку */
open class StretchLayout(context: Context, vert: Boolean) : CommonLayout(context, vert) {
	
	/** Инициализатор веса [weight] представления */
	inline var <T: View> T.weight: Float
		get()                   =  (layoutParams as? LayoutParams)?.weight ?: 0f
		set(v)                  {   val isHorz = orientation == HORIZONTAL
									layoutParams = LayoutParams(if(isHorz) WRAP else MATCH, if(isHorz) MATCH else WRAP, v)
								}
	
	// Вернуть спецификатор
	private fun getSpec(v: Int, dir: Boolean) = when(v) {
		WRAP    -> if(dir) MeasureSpec.EXACTLY else MeasureSpec.UNSPECIFIED
		else    -> MeasureSpec.EXACTLY
	}
	
	// Вернуть размер
	private fun getSize(v1: Int, v2: Int, v3: Int) = when(v1) {
		MATCH -> v2
		WRAP  -> v3
		else  -> v1
	}
	
	/** Вычисление габаритов представлений */
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)

		var ww = MeasureSpec.getSize(widthMeasureSpec) - horizontalPadding
		var hh = MeasureSpec.getSize(heightMeasureSpec) - verticalPadding
		
		var count = 0f
		var v = if(orientation == VERTICAL) hh else ww
		
		fun getSize(v1: Int, v2: Int, w: Float) = when(v1) {
			MATCH -> v2
			WRAP  -> { count += w; 0 }
			else  -> v1
		}
		
		loopChildren {
			if(it.visibility == View.GONE) return@loopChildren
			(it.layoutParams as? LayoutParams)?.apply {
				when(orientation) {
					VERTICAL    -> v -= getSize(height, hh, weight)
					HORIZONTAL  -> v -= getSize(width, ww, weight)
				}
			}
		}
		v = ((if (v <= 0) 0 else v).toFloat() / count).roundToInt()
		loopChildren {
			if(it.visibility == View.GONE) return@loopChildren
			(it.layoutParams as? LayoutParams)?.apply {
				val size = (v * weight).roundToInt()
				val childWidthSpec = MeasureSpec.makeMeasureSpec(getSize(width, ww, if(orientation == VERTICAL) 0 else size),
				                                                      getSpec(width, orientation != VERTICAL))
				val childHeightSpec = MeasureSpec.makeMeasureSpec(getSize(height, hh, if(orientation == VERTICAL) size else 0),
				                                                       getSpec(height, orientation == VERTICAL))
				if(orientation == VERTICAL) hh = 0 else ww = 0
				it.measure(childWidthSpec, childHeightSpec)
			}
		}
	}
	
	/** Вычисление позиций представлений */
	override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
		var tt = paddingTop
		var ll = paddingStart
		loopChildren {
			if(it.visibility == View.GONE) return@loopChildren
			(it.layoutParams as? LayoutParams)?.apply {
				val width = it.measuredWidth
				val height = it.measuredHeight
				val pl = ll
				val pt = tt
				val pr = ll + width
				val pb = tt + height
				it.layout(pl, pt, pr, pb)
				if(orientation == VERTICAL) {
					tt += height
				} else {
					ll += width
				}
			}
		}
	}
	
	/** Генерация параметров разметки по умолчанию, взависимости от ориентации */
	override fun generateDefaultLayoutParams(): LayoutParams {
		if(orientation == HORIZONTAL) return LayoutParams(WRAP, MATCH, 1f)
		return LayoutParams(MATCH, WRAP, 1f)
	}
}
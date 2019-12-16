@file:Suppress("NOTHING_TO_INLINE")

package ru.ostrovskal.sshstd.layouts

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.github.ostrovskal.sshstd.R
import ru.ostrovskal.sshstd.Common.MATCH
import ru.ostrovskal.sshstd.Common.WRAP

/**
 * @author  Шаталов С.В.
 * @since   0.3.1
 */

/** Класс, реализующий линейную разметку */
open class CommonLayout(context: Context, @JvmField val vert: Boolean) : LinearLayout(context, null, R.attr.scrollBarsStyle) {
	
	init {
		orientation = if(vert) VERTICAL else HORIZONTAL
	}
	
	/** Установка горизонтального выравнивания */
	inline fun setGravityHorizontal(gravity: Int) {
		setHorizontalGravity(gravity)
	}
	
	/** Установка вертикального выравнивания */
	inline fun setGravityVertical(gravity: Int) {
		setVerticalGravity(gravity)
	}
	
	/** Установка параметров разметки представления с шириной [width], высотой [height] и весом [weight] */
	inline fun <T : View> T.lps(width: Int = MATCH, height: Int = WRAP, weight: Float = 0f) = lps(width, height, weight) {}
	
	/** Установка параметров разметки представления с шириной [width] и высотой [height], весом [weight] и инициализатором [init] */
	inline fun <T : View> T.lps(width: Int = MATCH, height: Int = WRAP, weight: Float = 0f, init: LayoutParams.() -> Unit): T {
		layoutParams = LayoutParams(width, height, weight).apply { init() }
		return this
	}
}


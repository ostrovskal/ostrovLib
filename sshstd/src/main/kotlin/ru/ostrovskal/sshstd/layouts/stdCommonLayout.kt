@file:Suppress("NOTHING_TO_INLINE")

package ru.ostrovskal.sshstd.layouts

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import ru.ostrovskal.sshstd.Common.MATCH
import ru.ostrovskal.sshstd.Common.WRAP

/**
 * @author  Шаталов С.В.
 * @since   0.3.1
 */

/** Класс, реализующий линейную разметку */
open class CommonLayout(context: Context, vert: Boolean) : LinearLayout(context, null, 0) {
	
	init {
		orientation = if(vert) LinearLayout.VERTICAL else LinearLayout.HORIZONTAL
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
	inline fun <T : View> T.lps(width: Int = MATCH, height: Int = WRAP, weight: Float = 0f, init: LinearLayout.LayoutParams.() -> Unit): T {
		layoutParams = LinearLayout.LayoutParams(width, height, weight).apply { init() }
		return this
	}
}


@file:Suppress("NOTHING_TO_INLINE")

package ru.ostrovskal.sshstd.layouts

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import ru.ostrovskal.sshstd.Common

/**
 * @author  Шаталов С.В.
 * @since   0.3.6
 */

/** Класс, реализующий горизонтальную прокрутку с параметрами разметки */
open class HorizontalScrollLayout(ctx: Context): HorizontalScrollView(ctx) {
	/** Установка параметров разметки с шириной [width], высотой [height] и гравитацией [gravity] */
	inline fun <T: View> T.lps(width: Int = Common.MATCH, height: Int = Common.MATCH, gravity: Int = -1) = lps(width, height, gravity) {}
	
	/** Установка параметров разметки представления с шириной [width] и высотой [height], гравитацией [gravity] и инициализатором [init] */
	inline fun <T: View> T.lps(width: Int = Common.MATCH, height: Int = Common.MATCH, gravity: Int = -1, init: FrameLayout.LayoutParams.() -> Unit): T {
		layoutParams = FrameLayout.LayoutParams(width, height, gravity).apply { init() }
		return this
	}
}


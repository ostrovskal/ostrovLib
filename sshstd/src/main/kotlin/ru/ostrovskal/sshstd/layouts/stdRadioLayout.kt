package ru.ostrovskal.sshstd.layouts

import android.content.Context
import android.view.View
import android.view.ViewGroup
import ru.ostrovskal.sshstd.utils.loopChildren
import ru.ostrovskal.sshstd.widgets.Radio

/**
 * @author  Шаталов С.В.
 * @since   0.3.4
 */

/** Класс, реализующий лайаут для радио кнопок */
open class RadioLayout(context: Context, vert: Boolean) : StretchLayout(context, vert) {
	
	/** Добавление представления. Если оно является радио кнопкой, то на нее ставится уведомитель */
	override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
		(child as? Radio)?.radioClickNotify = { radio ->
			// выполнить переключение на текущий
			loopChildren {
				(it as? Radio)?.isChecked = it == radio
			}
			radio.performClick()
		}
		super.addView(child, index, params)
	}
	
	/** Вернуть выбранную радио кнопку в разметке */
	fun isChecked(): Radio? {
		loopChildren {
			if(it !is Radio || it.visibility == GONE) return@loopChildren
			if(it.isChecked) return it
		}
		return null
	}
}
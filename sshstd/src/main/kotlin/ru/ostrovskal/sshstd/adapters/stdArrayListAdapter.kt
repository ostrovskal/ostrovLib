package ru.ostrovskal.sshstd.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ru.ostrovskal.sshstd.ui.UiComponent
import ru.ostrovskal.sshstd.ui.UiCtx
import ru.ostrovskal.sshstd.widgets.lists.Spinner

/**
 * @author Шаталов С.В.
 * @since  0.5.0
 */

 /** Класс адаптера спиннера */
open class ArrayListAdapter(context: Context, private val title: UiComponent, private val dropdown: UiComponent,
                               mObjects: List<String>): ArrayAdapter<String>(context, 0, mObjects) {
	// Цвет подсветки
	private var highColor  = 0
	
	// Цвет текста
	private var normColor  = 0
	
	/** Возвращает представление заголовка */
	override fun getView(position: Int, convertView: View?, parent: ViewGroup) = createView(position, convertView, title, parent, false).apply {
		(this as? TextView)?.apply {
			normColor = highlightColor
			highColor = currentTextColor
		}
	}
	
	/** Возвращает представление из выпадающего списка */
	override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup) = createView(position, convertView, dropdown, parent, true)
	
	/** Создает представление */
	private fun createView(position: Int, convertView: View?, resource: UiComponent, parent: ViewGroup, color: Boolean): View? {
		return ((convertView ?: resource.createView(UiCtx(context))) as? TextView)?.apply {
			text = getItem(position)
			val sel = (parent as? Spinner)?.selection ?: -1
			if(color) setTextColor(if(position == sel) highColor else normColor)
		}
	}
}

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

 /** Класс адаптера спиннера
  * @property title	 	Представление для заголовка(Spinner)
  * @property dropdown	Представление для элемента списка
  * */
open class ArrayListAdapter<T>(context: Context, protected val title: UiComponent, protected val dropdown: UiComponent,
                               mObjects: List<T>): ArrayAdapter<T>(context, 0, mObjects) {
	// Цвет подсветки
	private var highColor  = 0
	
	// Цвет текста
	private var normColor  = 0
	
	/** Возвращает представление заголовка */
	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View = createView(position, convertView, title, parent, false)?.run {
		(this as? TextView)?.apply {
			normColor = highlightColor
			highColor = currentTextColor
		}
	} ?: error("View is null!")
	
	/** Возвращает представление из выпадающего списка */
	override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? = createView(position, convertView, dropdown, parent, true)
	
	/** Создает представление */
	open fun createView(position: Int, convertView: View?, resource: UiComponent, parent: ViewGroup, color: Boolean): View? {
		return ((convertView ?: resource.createView(UiCtx(context))) as? TextView)?.apply {
			text = getItem(position) as CharSequence
			val sel = (parent as? Spinner)?.selection ?: -1
			if(color) setTextColor(if(position == sel) highColor else normColor)
		}
	}
}

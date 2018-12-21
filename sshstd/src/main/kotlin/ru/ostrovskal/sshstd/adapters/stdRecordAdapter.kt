package ru.ostrovskal.sshstd.adapters

import android.content.Context
import android.database.Cursor
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import ru.ostrovskal.sshstd.sql.RecordSet
import ru.ostrovskal.sshstd.ui.UiComponent
import ru.ostrovskal.sshstd.ui.UiCtx
import ru.ostrovskal.sshstd.utils.byIdx
import ru.ostrovskal.sshstd.widgets.*

/** Класс, реализующий адаптер с доступом к записи в БД
 * @property layout Компонент
 */
open class RecordAdapter(context: Context, @JvmField protected val layout: UiComponent, private val fields: Int) : CursorAdapter(context, null, false) {
	
	/** Путь к картинкам */
	@JvmField var path      = ""
	
	/** Создание нового компонента */
	override fun newView(context: Context, cursor: Cursor, parent: ViewGroup) = layout.createView(UiCtx(context))
	
	/** Привязка представлений компонента к полям курсора */
	override fun bindView(view: View, context: Context, cursor: Cursor) {
		repeat(fields) { bindField(view.byIdx(it), cursor as RecordSet, it) }
	}
	
	/** Привязка поля к представлению */
	open fun bindField(view: View?, rs: RecordSet, idx: Int) {
		rs.apply {
			when(view) {
				is Tile   -> { if(path.isNotEmpty()) { view.setBitmap("$path/${rs.text(idx)}.png"); view.tileIcon = 0 } }
				is Check  -> { view.isChecked = rs.boolean(idx); view.setTag(getItemId(position)) }
				is Text   -> view.text = rs.text(idx)
				is Switch -> view.isChecked = rs.boolean(idx)
				is Seek   -> view.progress = rs.getInt(idx)
			}
		}
	}
}
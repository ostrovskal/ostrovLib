package ru.ostrovskal.sshstd.layouts

import android.content.Context
import android.view.View
import android.view.ViewGroup
import ru.ostrovskal.sshstd.Animator
import ru.ostrovskal.sshstd.objects.style_text_hint
import ru.ostrovskal.sshstd.widgets.Edit
import ru.ostrovskal.sshstd.widgets.Text

/**
 * @author  Шаталов С.В.
 * @since   0.3.7
 */

/** Класс, реализующий разметку с полем ввода, имеющим всплывающую текстовую подсказку */
open class EditLayout(context: Context, cols: Int, rows: Int) : CellLayout(context, cols, rows) {
	
	// Текущая вертикальная позиция редактора
	private var yEditor					= 0
	
	// Текущая высота редактора
	private var hEditor					= 0
	
	// Текущая вертикальная позиция хинта
	private var yHint					= 0
	
	/** Редактор */
	lateinit var edit: Edit
	
	// Хинт
	private val hint					= Text(context, style_text_hint)
	
	// Признак пустого текста в редакторе
	private var emptyText				= true
	
	// Аниматор
	private val animator				by lazy {
		Animator(this, 7, 50) { _, animator, frame, direction, began ->
			(edit.layoutParams as? CellLayout.LayoutParams)?.apply {
				(hint.layoutParams as? CellLayout.LayoutParams)?.let { lh ->
					if(began) {
						yEditor = y
						hEditor = h
						yHint = lh.y
					}
					lh.y = yHint - frame
					y = yEditor + frame
					h = hEditor - frame / 2
				}
			}
			requestLayout()
			((direction == 1 && frame == animator.lastFrame) || (direction == -1 && frame == 0)).apply { if(this) animator.reverse() }
		}
	}

	/** Установка видимости */
	var visible: Boolean				= this.visibility == View.VISIBLE
		set(v) {
			val state 		= if(v) View.VISIBLE else View.GONE
			visibility 			= state
			edit.visibility 	= state
			hint.visibility 	= state
		}
	
	/** Добавление нового представления в разметку */
	override fun addView(child: View?, idx: Int, params: ViewGroup.LayoutParams?) {
		var lp = params
		if(child is Edit) {
			hint.text = child.hint
			hint.visibility = visibility
			lp = CellLayout.LayoutParams(0, 0, cols, rows)
			edit = child.apply {
				hint = null
				changeTextLintener = { text ->
					emptyText = text.isNullOrEmpty().apply {
						if(this != emptyText) {
							animator.apply { if(isRunning) reverse() else start(false, false) }
						}
					}
				}
			}
		}
		super.addView(child, idx, lp)
		if(edit == child) addView(hint, -1, CellLayout.LayoutParams(1, 0, cols - 2, rows))
	}
}

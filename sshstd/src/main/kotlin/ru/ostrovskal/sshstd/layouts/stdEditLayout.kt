package ru.ostrovskal.sshstd.layouts

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import ru.ostrovskal.sshstd.Animator
import ru.ostrovskal.sshstd.utils.padding
import ru.ostrovskal.sshstd.utils.textColor
import ru.ostrovskal.sshstd.widgets.Edit
import ru.ostrovskal.sshstd.widgets.Text

/**
 * @author  Шаталов С.В.
 * @since   0.3.7
 */

/** Класс, реализующий разметку с полем ввода, имеющим всплывающую текстовую подсказку */
open class EditLayout(context: Context, styleHint: IntArray) : AbsoluteLayout(context) {

	private var isInit					= false

	// Текущая вертикальная позиция редактора
	private var yEditor					= 0
	
	// Текущая высота редактора
	private var hEditor					= 0

	// Текущая вертикальная позиция хинта
	private var yHint					= 0
	
	/** Редактор */
	private var edit: Edit?				= null
	
	// Хинт
	private val hint					= Text(context, styleHint)

	// Признак пустого текста в редакторе
	private var emptyText				= true

	// Аниматор
	private val animator				by lazy {
		Animator(this, 9, 40) { _, animator, frame, direction, began ->
			(edit?.layoutParams as? LayoutParams)?.apply {
				(hint.layoutParams as? LayoutParams)?.let { lh ->
					val v = frame * 4
					if(began) {
						yEditor = y
						hEditor = height
						yHint = lh.y
					}
					lh.y = yHint - v
					//lh.x = xHint
					y = yEditor + v
					//height = hEditor - v
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
			edit?.visibility 	= state
			hint.visibility 	= state
		}
	
	/** Добавление нового представления в разметку */
	override fun addView(child: View?, idx: Int) {
		if(child is Edit) {
			hint.text = child.hint
			hint.visibility = visibility
			hint.padding = padding
			hint.textColor = child.currentHintTextColor
			edit = child.apply {
				hint = null
				changeTextLintener = { text ->
					emptyText = text.isNullOrEmpty().apply {
						if(this != emptyText) {
							animator.apply { if(isRunning) reverse() else start(stop = false, reset = false) }
						}
					}
				}
			}
		}
		super.addView(child, idx)
		if(child == edit) addView(hint, -1)
	}

	@SuppressLint("DrawAllocation")
	override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
		super.onLayout(changed, l, t, r, b)
		edit?.let {
			val x = (hint.layoutParams as? LayoutParams)?.x ?: return
			val px = it.paddingLeft
			if(px == x) isInit = true
			if(!isInit) {
				it.layoutParams = LayoutParams(measuredWidth, measuredHeight - 36, 0, 0)
				hint.layoutParams = LayoutParams(measuredWidth - it.paddingLeft, measuredHeight - 36, px, 0)
			}
		}
	}
}

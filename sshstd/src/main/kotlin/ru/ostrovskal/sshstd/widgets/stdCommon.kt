package ru.ostrovskal.sshstd.widgets

import android.content.Context
import android.graphics.Canvas
import android.os.Parcelable
import android.view.MotionEvent
import android.widget.TextView
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.onTouch
import ru.ostrovskal.sshstd.touchClick
import ru.ostrovskal.sshstd.utils.makeFont
import ru.ostrovskal.sshstd.utils.noGetter
import ru.ostrovskal.sshstd.utils.withSave

/*====================================================================================================================
 = 													CHECK BOX
 ====================================================================================================================*/

/**
 * @author Шаталов С.В.
 * @since  0.1.1
 */

/** Класс Флажка со стилем по умолчанию style_сheck */
open class Check(context: Context, id: Int, text: Int, style: IntArray) : Tile(context, style) {
	
	override var isChecked
		get()               = super.isChecked
		set(v)              { super.isChecked = v; tile = data.toInt(); invalidate() }
	
	init {
		this.id = id
		setText(text)
	}
	/** Обработка события клика */
	override fun onTouchEvent(event: MotionEvent): Boolean {
		onTouch(event)
		touchClick(0, rectScreen) {
			if(this@Check is Radio) radioClickNotify?.invoke(this@Check)
			else {
				isChecked = !isChecked
				performClick()
			}
		}
		return true
	}
	
	override fun onRestoreInstanceState(state: Parcelable?) {
		super.onRestoreInstanceState(state)
		isChecked = data.toInt() != 0
	}
	
}

/*====================================================================================================================
 = 													RADIO BUTTON
 ====================================================================================================================*/

/**
 * @author Шаталов С.В.
 * @since  0.1.2
 */

/** Класс Радио кнопки со стилем по умолчанию style_radio */
open class Radio(context: Context, id: Int, text: Int, style: IntArray) : Check(context, id, text, style) {
	
	/** Уведомление о входящем событии */
	var radioClickNotify: ((radio: Radio) -> Unit)? = null
}

/*====================================================================================================================
 = 													TEXT VIEW
 ====================================================================================================================*/

/**
 * @author Шаталов С.В.
 * @since  0.1.0
 */

/** Класс текста со стилем по умолчанию style_text_normal
 *  @param style Стиль текста
 * */
open class Text(context: Context, @JvmField val style: IntArray) : TextView(context, null, 0) {
	
	/** Событие изменения темы */
	open fun onChangeTheme() {
		Theme.updateTheme(context, this, style)
		invalidate()
	}
	
	/** Установка шрифта из активов */
	var font: String
		get()   = noGetter()
		set(v)  { typeface = context.makeFont(v) }
	
	init {
		Theme.setBaseAttr(context, this, style)
	}
	
	/** Отрисовка */
	override fun draw(canvas: Canvas) {
		canvas.withSave { super.draw(canvas) }
	}
}

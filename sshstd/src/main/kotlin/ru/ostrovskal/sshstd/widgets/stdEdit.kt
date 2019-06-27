package ru.ostrovskal.sshstd.widgets

import android.content.Context
import android.text.InputType.TYPE_CLASS_NUMBER
import android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
import android.widget.EditText
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.TileDrawable
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.utils.checkStates
import ru.ostrovskal.sshstd.utils.interval
import ru.ostrovskal.sshstd.utils.ival
import ru.ostrovskal.sshstd.utils.noGetter

/**
 * @author Шаталов С.В.
 * @since  0.1.0
 */

/** Класс, реализующий Поле ввода со стилем по умолчанию style_edit
 * @param style Стиль поля ввода
 * */
open class Edit(context: Context, id: Int, hint: Int, @JvmField val style: IntArray) : EditText(context) {
	
	/** Уведомление о изменении текста в редакторе */
	@JvmField var changeTextLintener: ((text: CharSequence?) -> Unit)? = null
	
	/** Установка подсказки текста из ресурсов */
	var hintResource: Int
		get()           = noGetter()
		set(v)          = setHint(v)
	
	/** Диапазон. Необходим в случае значений текста в виде чисел в определенном диапазоне  */
	var range 		    = 0..0
		set(v)          {   field = v; inputType = TYPE_CLASS_NUMBER or TYPE_NUMBER_FLAG_SIGNED
							hint = if(v.interval != 0) "$hint ${v.first}..${v.last}" else hint
						}
	
	/** Строковое значение текста */
	var string
		get()           = text.toString()
		set(v)          { setText(v) }
	
	/** Целое значение текста */
	var int
		get()           = string.ival(0, 10)
		set(v)          { setText(v.toString()); inputType = TYPE_CLASS_NUMBER or TYPE_NUMBER_FLAG_SIGNED }
	
	/** Проверка на валидность текста. Если текст не валиден - возбуждается исключение EditInvalidException */
	val valid: String
		get() {
			var isValid = string.isNotEmpty() && string.indexOfAny(charsUncorrect) == -1
			if(isValid && !range.isEmpty()) isValid = int in range
			if(!isValid) throw EditInvalidException("", this)
			return string
		}
	
	init {
		Theme.setBaseAttr(context, this, style)
		background = TileDrawable(context, style)
		this.id = id
		hintResource = hint
	}
	
	/** Вызов события, при изменении текста */
	override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
		super.onTextChanged(text, start, lengthBefore, lengthAfter)
		changeTextLintener?.invoke(text)
	}
	
	/** Установка состояния отображения */
	override fun drawableStateChanged() {
		super.drawableStateChanged()
		
		val cf = when(drawableState.checkStates(STATE_FOCUSED, STATE_PRESSED)) {
			STATE_FOCUSED 	-> fltFocused
			STATE_PRESSED 	-> fltPressed
			STATE_DISABLED 	-> fltDisabled
			else			-> null
		}
		paint.colorFilter = cf
		background?.colorFilter = cf
	}
	
	/** Событие изменения темы */
	open fun onChangeTheme() {
		Theme.updateTheme(context, this, style)
		invalidate()
	}
}

/**
 * Класс, реализующий исключение, возбуждаемого при проверке на валидность в поле ввода
 *
 * @property msg Сообщение об ошибке
 * @property et  Объект поля ввода, который вызвал ошибку
 */
class EditInvalidException(@JvmField val msg: String, @JvmField val et: Edit? = null): RuntimeException()

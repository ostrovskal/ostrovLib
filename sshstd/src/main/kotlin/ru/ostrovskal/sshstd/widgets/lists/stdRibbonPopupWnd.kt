package ru.ostrovskal.sshstd.widgets.lists

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.utils.horizontalPadding
import ru.ostrovskal.sshstd.utils.noGetter
import ru.ostrovskal.sshstd.utils.verticalPadding

/**
* @author Шаталов С.В.
* @since 0.2.5
*/

/** Всплывающее окно списка, которое отображает содержимое адаптера ListAdapter
* @property owner   Владелец
*/
open class RibbonPopupWnd(context: Context, val owner: View, style: IntArray): Ribbon(context, 0, true, style) {
	
	// Базовое окно
	private val mPopup                  = PopupWindow(context).apply { inputMethodMode = METHOD_NEEDED }
	
	/** Высота содержимого */
	var contentHeight                   = WRAP
		set(v)                          { field = v + (popupBackground?.verticalPadding ?: 0) }
	
	/** Щирина содержимого */
	var contentWidth                    = WRAP
		set(v)                          { field = v + (popupBackground?.horizontalPadding ?: 0) }
	
	/** Признак игнорирования касаний за пределами окна */
	@JvmField var isIgnoreOutsideTouch   = false
	
	/** Горизонтальное смещение относительно владельца */
	@JvmField var horizontalOffset      = 0
	
	/** Вертикальное смещение относительно владельца */
	@JvmField var verticalOffset        = 0
	
	/** Признак того, что всегда видимо */
	@JvmField var isAlwaysVisible       = false
	
	/** Гравитация */
	@JvmField var align             = Gravity.NO_GRAVITY
	
	/** Признак модального окна */
	var isModal                         = false
		set(modal)                      { field = true; mPopup.isFocusable = modal }
	
	/** Режим клавиатурного ввода */
	var softInputMode
		get()                           = mPopup.softInputMode
		set(mode)                       { mPopup.softInputMode = mode }
	
	/** Фон */
	var popupBackground: Drawable?
		get()                           = mPopup.background
		set(v)                          { mPopup.setBackgroundDrawable(v) }
	
	/** Метод ввода */
	var inputMethodMode
		get()                           = mPopup.inputMethodMode
		set(mode)                       { mPopup.inputMethodMode = mode }
	
	/** Признак отображения */
	val isShowing
		get()                           = mPopup.isShowing
	
	/** Обработчик завершения списка */
	var onDismissListener: PopupWindow.OnDismissListener
		get()                           = noGetter()
		set(v)                          { mPopup.setOnDismissListener(v) }
	
	/** Отобразить/Обновить окно */
	open fun show() {
		mPopup.contentView = this
		
		val maxHeight = mPopup.getMaxAvailableHeight(owner, verticalOffset) //, mDropdown.mInputMethodMode == METHOD_NOT_NEEDED)
		val height = if(isAlwaysVisible || contentHeight == MATCH) {
			maxHeight
		}
		else {
			measureHeightOrWidthOfChildren(0, -1, maxHeight - verticalPadding, -1)
		}
	
		val noInputMethod = inputMethodMode == METHOD_NOT_NEEDED
		mPopup.isOutsideTouchable = !isIgnoreOutsideTouch && !isAlwaysVisible
		
		if(mPopup.isShowing) {
			if(!owner.isAttachedToWindow) return
			val widthSpec = when(contentWidth) {
				MATCH -> MATCH
				WRAP  -> owner.width
				else  -> contentWidth
			}
			val heightSpec = when(contentHeight) {
				MATCH -> {
					mPopup.width = if(contentWidth == MATCH) MATCH else 0
					mPopup.height = if(noInputMethod) 0 else MATCH
					if(noInputMethod) height else MATCH
				}
				WRAP  -> height
				else  -> contentHeight
			}
			mPopup.update(owner, horizontalOffset, verticalOffset, if(widthSpec < 0) -1 else widthSpec, if(heightSpec < 0) -1 else heightSpec)
		}
		else {
			mPopup.width = when(contentWidth) {
				MATCH   -> MATCH
				WRAP    -> owner.width
				else    -> contentWidth
			}
			mPopup.height = when(contentHeight) {
				MATCH   -> MATCH
				WRAP    -> height
				else    -> contentHeight
			}
			mPopup.isClippingEnabled = false
			mPopup.showAsDropDown(owner, horizontalOffset, verticalOffset, align)

			if(!isModal || isInTouchMode) requestLayout()
			
		}
	}
	
	/** Удаление всплывающего списка */
	open fun dismiss() {
		mPopup.dismiss()
		mPopup.contentView = null
	}
}

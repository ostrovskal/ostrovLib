package ru.ostrovskal.sshstd.widgets.lists

import android.content.Context
import android.database.DataSetObserver
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.view.Gravity
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ListAdapter
import android.widget.SpinnerAdapter
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.TileDrawable
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.utils.*

/**
 * @author  Шаталов С.В.
 * @since   0.2.4
 */

/**
* Класс Спиннера
*
* @property style   Стиль по умолчанию style_select
* @param stylePopup Стиль по умолчанию для всплывающего списка style_dropdown
*/
open class Spinner(context: Context, id: Int, @JvmField val style: IntArray, stylePopup: IntArray) : ViewGroup(context, null, 0) {
	
	// Обсервер принимающий данные от адаптера
	private var observer                = SpinnerObserver()
	
	/** Признак изменения позиции выделения */
	@JvmField var mIsSelection          = true
	
	/** Всплывающий список */
	@JvmField protected var mDropdown   = DropdownPopup(context)
	
	/** Ширина выпадающего списка */
	@JvmField var dropdownWidth         = WRAP
	
	/** Гор. выравнивание списка */
	var gravity                         = Gravity.CENTER
		set(v)  {
			field = v or if(v ntest Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) Gravity.START else 0
			requestLayout()
		}
	
	/** Признак видимости списка */
	val isShowing
		get()                           = mDropdown.isShowing
	
	/** Высота разделителя */
	var dividerHeight
		get()                           = mDropdown.dividerHeight
		set(v)                          { mDropdown.dividerHeight = v }
	
	/** Разделитель */
	var divider: Drawable?
		get()                           = mDropdown.divider
		set(v)                          { mDropdown.divider = v }
	
	/**  Фоновый рисунок выпадающего списка */
	var dropDownBackground: Drawable?
		get()                           = mDropdown.popupBackground
		set(v)                          { mDropdown.popupBackground = v }
	
	/** Вертикальное смещение выпадающего списка */
	var vertOffset
		get()                           = mDropdown.verticalOffset
		set(v)                          { mDropdown.verticalOffset = v }
	
	/** Горизонтальное смещение выпадающего списка */
	@JvmField var horzOffset            = 0
	
	/** Событие клика по элементу выпадающего списка */
	@JvmField var itemClickListener: ((spinner: Spinner, view: View, position: Int, id: Long) -> Unit)? = null
	
	/** Адаптер */
	open var adapter: SpinnerAdapter?        = null
		set(v)                          {
			field?.unregisterDataSetObserver(observer)
			v?.apply { mDropdown.adapter = DropdownAdapter(this, this@Spinner) }
			field = v
			v?.registerDataSetObserver(observer)
			requestLayout()
		}
	
	/** Позиция выбранного элемента */
	var selection                       = 0
		set(v)                          {
			mDropdown.mNewFirstPosition = v
			field = v
			mIsSelection = true
			requestLayout()
		}
	
	/** Установка/Получение элемента по строке */
	var selectionString: String
		get()                           = adapter?.getItem(selection)?.toString() ?: ""
		set(v)                          {
			adapter?.apply {
				repeat(count) {
					if(v == getItem(it)) {
						selection = it
						return
					}
				}
//				selection = 0
			}
		}
	
	/** Селектор элемента списка */
	var selector: Drawable?
		get()                           = mDropdown.selector
		set(v)                          { mDropdown.selector = v }
	
	init {
		this.id = id
		background = TileDrawable(context, style)
		dropDownBackground = TileDrawable(context, stylePopup)
		Theme.setBaseAttr(context, this, style)
	}
	
	/** Событие - изменение темы */
	open fun onChangeTheme() {
		Theme.updateTheme(context, this, style)
		mDropdown.onChangeTheme()
		requestLayout()
	}
	
	/** Установка признака доступности */
	override fun setEnabled(enabled: Boolean) {
		super.setEnabled(enabled)
		loopChildren {
			it.isEnabled = enabled
		}
	}
	
	/** Установка состояния селекта */
	override fun drawableStateChanged() {
		super.drawableStateChanged()
		background?.colorFilter = if(drawableState.checkState(STATE_ENABLED)) null else fltDisabled
	}
	
	/** Выполняется при закрытии списка */
	override fun onDetachedFromWindow() {
		super.onDetachedFromWindow()
		if(isShowing) mDropdown.dismiss()
	}
	
	/** Исполнение клика на заголовке */
	override fun performClick(): Boolean {
		super.performClick()
		if(!isShowing) mDropdown.show(textDirection, textAlignment)
		return true
	}

	// Объект отложенного клика по элементу
	private val mClick = Runnable {
		adapter?.apply {
			val pos = selection
			itemClickListener?.invoke(this@Spinner, getChildAt(0), pos, getItemId(pos))
		}
	}

	/** @see android.view.View.onLayout */
	override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
		removeAllViewsInLayout()
		adapter?.apply {
			if(count > 0) {
				val pos = selection
				val child = getView(pos, null, this@Spinner)
				val lp = child.layoutParams ?: generateDefaultLayoutParams()
				addViewInLayout(child, 0, lp)
				measureChild(child, measuredHeightAndState, measuredWidthAndState)
				
				val childTop = paddingTop + (measuredHeight - verticalPadding - child.measuredHeight) / 2
				val width = child.measuredWidth
				child.layout(0, childTop, width, childTop + child.measuredHeight)
				
				val childrenLeft = mDropdown.mEdgeStart
				val childrenWidth = getWidth() - horizontalPadding
				val absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection)
				val selectedOffset = when(absoluteGravity and Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) {
					Gravity.CENTER_HORIZONTAL -> childrenLeft + (childrenWidth - width) / 2
					Gravity.START             -> childrenLeft + childrenWidth - width
					else                      -> childrenLeft
				}
				child.offsetLeftAndRight(selectedOffset)
				if(mIsSelection) postDelayed(mClick, ViewConfiguration.getTapTimeout().toLong())
				mIsSelection = false
			}
		}
		
		invalidate()
	}
	
	/** Вычисление габаритов заголовка */
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		val widthMode = MeasureSpec.getMode(widthMeasureSpec)
		
		var preferredHeight = verticalPadding
		var preferredWidth = if(widthMode == MeasureSpec.UNSPECIFIED) horizontalPadding else 0
		
		adapter?.let {
			if(it.count > 0) {
				it.getView(selection, null, this)?.apply {
					if(importantForAccessibility == View.IMPORTANT_FOR_ACCESSIBILITY_AUTO) importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
					if(layoutParams == null) layoutParams = generateDefaultLayoutParams()
					measureChild(this, widthMeasureSpec, heightMeasureSpec)
					preferredHeight += measuredHeight
					preferredWidth += measuredWidth
				}
			}
		}
		preferredHeight = preferredHeight.coerceAtLeast(suggestedMinimumHeight)
		preferredWidth = preferredWidth.coerceAtLeast(suggestedMinimumWidth)
		
		val heightSize = View.resolveSizeAndState(preferredHeight, heightMeasureSpec, 0)
		val widthSize = View.resolveSizeAndState(preferredWidth, widthMeasureSpec, 0)
		
		if(widthMode == MeasureSpec.AT_MOST)
			setMeasuredDimension(measuredWidth.coerceAtLeast(measureContentWidth(adapter)).coerceAtMost(MeasureSpec.getSize(widthMeasureSpec)), measuredHeight)
		else setMeasuredDimension(widthSize, heightSize)
	}
	
	private fun measureContentWidth(adapter: Adapter?): Int {
		var width = 0
		var itemView: View? = null
		
		val widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
		val heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
		
		var start = 0.coerceAtLeast(mDropdown.selection)
		val end = (adapter?.count ?: 0).coerceAtMost(start + 15)
		val count = end - start
		
		start = 0.coerceAtLeast(start - (15 - count))
		
		for(i in start until end) {
			itemView = adapter?.getView(i, itemView, this)?.apply {
				if(layoutParams == null) layoutParams = LayoutParams(WRAP, WRAP)
				measure(widthMeasureSpec, heightMeasureSpec)
				width = width.coerceAtLeast(measuredWidth)
			}
		}
		return width
	}
	
	// Внутренний адаптер выпадающего списка - осуществляет проброс через getDropDownView
	private class DropdownAdapter(private val mAdapter: SpinnerAdapter, private val spinner: Spinner, private var mListAdapter: ListAdapter? = null) :
			ListAdapter, SpinnerAdapter {
		init { if(mAdapter is ListAdapter) mListAdapter = mAdapter }
		override fun getCount() = mAdapter.count
		override fun getItem(position: Int): Any? = mAdapter.getItem(position)
		override fun getItemId(position: Int) = mAdapter.getItemId(position)
		override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? = getDropDownView(position, convertView, parent)
		override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? = mAdapter.getDropDownView(position, convertView, spinner)
		override fun hasStableIds() = mAdapter.hasStableIds()
		override fun registerDataSetObserver(observer: DataSetObserver) { mAdapter.registerDataSetObserver(observer) }
		override fun unregisterDataSetObserver(observer: DataSetObserver) { mAdapter.unregisterDataSetObserver(observer) }
		override fun areAllItemsEnabled() = mListAdapter?.areAllItemsEnabled() == true
		override fun isEnabled(position: Int) = mListAdapter?.isEnabled(position) == true
		override fun getItemViewType(position: Int) = 0
		override fun getViewTypeCount() = 1
		override fun isEmpty() = count == 0
	}
	
	/** Внутренний класс-обертка выпадающего списка */
	inner class DropdownPopup(context: Context) : RibbonPopupWnd(context, this@Spinner, style_spinner_dropdown) {
		init {
			isModal = true
			scrollTo(0, 0)
			itemClickListener = {_, _, position, _ ->
				this@Spinner.selection = position
				dismiss()
			}
		}
		
		/** Отображение выпадающего списка */
		fun show(textDirection: Int, textAlignment: Int) {
			val spinnerWidth = this@Spinner.width
			val spinnerPadding = spinnerWidth - this@Spinner.horizontalPadding
			contentWidth = when(dropdownWidth) {
				WRAP  -> {
					var contentWidth = measureContentWidth(adapter)
					val contentWidthLimit = dMetrics.widthPixels
					if(contentWidth > contentWidthLimit) contentWidth = contentWidthLimit
					contentWidth.coerceAtLeast(spinnerPadding)
				}
				MATCH -> spinnerPadding
				else  -> dropdownWidth
			}
			horizontalOffset = horzOffset + this@Spinner.paddingLeft
			inputMethodMode = METHOD_NOT_NEEDED
			setTextDirection(textDirection)
			setTextAlignment(textAlignment)
			super.show()
		}
	}
	
	/** Класс хранения состояния [data] спиннера */
	private class SpinnerState(@JvmField val data: Int, state: Parcelable?): View.BaseSavedState(state)
	
	/** Сохранение состояния */
	override fun onSaveInstanceState(): Parcelable = SpinnerState(selection, super.onSaveInstanceState())
	
	/** Восстановление состояния */
	override fun onRestoreInstanceState(state: Parcelable?) {
		var st = state
		if(st is SpinnerState) {
			selection = st.data
			st = st.superState
		}
		super.onRestoreInstanceState(st)
	}
	
	// Внутренний класс обсервера
	private inner class SpinnerObserver : DataSetObserver() {
		/** Изменение данных адаптера */
		override fun onChanged() {
			requestLayout()
		}
		
		/** При отсутствии данных */
		override fun onInvalidated() {
			onChanged()
		}
	}
}

package ru.ostrovskal.sshstd.layouts

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import ru.ostrovskal.sshstd.Common
import ru.ostrovskal.sshstd.Common.MATCH
import ru.ostrovskal.sshstd.objects.*
import ru.ostrovskal.sshstd.utils.*
import ru.ostrovskal.sshstd.widgets.Tile
import ru.ostrovskal.sshstd.widgets.lists.Ribbon

/**
 * @author  Шаталов С.В.
 * @since   0.7.0
 */

/** Класс, реализующий ленту с вкладками */
open class TabRibbonLayout(context: Context, idContent: Int, @JvmField protected val captionPos: Int, @JvmField protected val sizeCaption: Int,
                     @JvmField protected val style: IntArray) : CommonLayout(context, captionPos test Common.DIRV) {
	
	// Рисователь
	private val paint                   = Paint()
	
	// Рисователь выделенной вкладки
	private val paintSel                = Paint()
	
	/** Событие уведомления об активации вкладки */
	@JvmField var tabChangeListener: ((tab: Int, content: ViewGroup) -> Unit)?   = null
	
	/** Признак отрисовки стрипов вкладок */
	@JvmField var isDrawStrips          = true
	
	/** Содержимое текущей вкладки */
	@JvmField var currentContent: ViewGroup? = null
	
	/** Заголовок */
	@JvmField val caption               = Caption(context, captionPos test Common.DIRH)
	
	/** Содержимое */
	@JvmField val content               = Content(context, captionPos test Common.DIRH).apply { id = idContent }
	
	/** Количество вкладок */
	val tabsCount
		get()                           = content.childCount
	
	/** Толщина стрипа */
	var widthStrip
		get()                           = paint.strokeWidth
		set(v)                          { paintSel.strokeWidth = v; paint.strokeWidth = v / 2f; caption.invalidate() }
	
	/** Текущая вкладка */
	var currentTab                      = -1
		set(v) {
			if(v != field && v >= 0 && v < content.views.size) {
				field = v
				currentContent = (content.views[v] as? ViewGroup)?.apply {
					tabChangeListener?.invoke(v, this)
					content.selection = v
					caption.selection = v
				}
			}
		}
	
	inner class Adapter : ArrayAdapter<View>(context, 0, arrEmpty) {
		override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
			return when(parent) {
				is Caption      -> parent.views[position]
				is Content      -> parent.views[position]
				else            -> error("")
			}
		}
		
		override fun getCount() = caption.views.size
	}
	
	init {
		onChangeTheme()
	}
	
	/** Позиционирование на разметке */
	@SuppressLint("DrawAllocation")
	override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
		if(changed) {
			val vert = captionPos test Common.DIRV
			val width = measuredWidth.fromPercent(sizeCaption)
			val height = measuredHeight.fromPercent(sizeCaption)
			caption.layoutParams = LayoutParams(if(vert) MATCH else width, if(vert) height else MATCH)
			content.layoutParams = LayoutParams(if(vert) MATCH else measuredWidth - width, if(vert) measuredHeight - height else MATCH)
			if(content.adapter == null) {
				if(captionPos == Common.DIRU || captionPos == Common.DIRL) {
					addView(caption)
					addView(content)
				}
				else {
					addView(content)
					addView(caption)
				}
				caption.adapter = Adapter()
				content.adapter = Adapter()
			}
		}
		super.onLayout(changed, l, t, r, b)
	}
	
	/** Удаление всех представлений */
	override fun removeAllViews() {
		super.removeAllViews()
		caption.views.clear()
		content.views.clear()
		currentTab = -1
	}
	
	/** Добавление страницы с текстом [text], либо рисунком [nTile], либо иконкой [nIcon] */
	fun page(id: Int, text: String = "", nTile: Int = -1, nIcon: Int = -1, init: Content.() -> View) {
		val indicator = Tile(context, style).apply {
			if(nTile != -1) {
				if(nTile test 0x7f000000) tileResource = nTile else tile = nTile
			}
			if(nIcon != -1) {
				if(nIcon test 0x7f000000) iconResource = nIcon else tileIcon = nIcon
			}
			this.text = text.toUpperCase()
			isFocusable = false
			isClickable = false
		}
		caption.views.add(indicator)
		content.init().id = id
	}
	
	/** Событие изменения темы */
	fun onChangeTheme() {
		paint.color = Theme.integer(context, style.themeAttrValue(ATTR_SSH_COLOR_NORMAL, ATTR_SSH_COLOR_NORMAL or THEME))
		paint.strokeWidth = Theme.integer(context, style.themeAttrValue(ATTR_SSH_SIZE_SELECTOR_TAB, 4)).toFloat()
		paintSel.color = Theme.integer(context, style.themeAttrValue(ATTR_SSH_COLOR_SELECTOR, ATTR_SSH_COLOR_SELECTOR or THEME))
		paintSel.strokeWidth = Theme.integer(context, style.themeAttrValue(ATTR_SSH_SIZE_SELECTOR_SEL_TAB, 8)).toFloat()
		changeTheme()
		caption.invalidate()
	}
	
	/** Класс, реализующий список заголовков */
	inner class Caption(context: Context, vert: Boolean) : Ribbon(context, 0, vert, style_ribbon) {
		
		val views = mutableListOf<View>()
		
		init {
			isVerticalScrollBarEnabled = false
			isHorizontalScrollBarEnabled = false
			mIsGlow = false
			
			itemClickListener = { _, _, position, _ ->
				"caption itemClickListener $position".info()
				currentTab = position
			}
		}
		// Временная область
		private val rectTmp                 = Rect()
		
		override fun generateDefaultLayoutParams() = LayoutParams(250.dp, MATCH, 0, -1, -1)
		
		/** Диспетчер отрисовки содержитого заголовка вкладки на канве [canvas] */
		public override fun dispatchDraw(canvas: Canvas) {
			super.dispatchDraw(canvas)
			if(isDrawStrips) {
				"${views.size} $selectedItemPosition ".info()
				if(selectedItemPosition >= 0) {
					views[selectedItemPosition].let {
						it.getHitRect(rectTmp)
						val sz = paintSel.strokeWidth.toInt() - 1
						when(captionPos) {
							Common.DIRR -> Common.iRect.set(rectTmp.left, rectTmp.top + it.paddingTop,
							                                rectTmp.left + sz, rectTmp.bottom - it.paddingBottom)
							Common.DIRD -> Common.iRect.set(rectTmp.left + it.paddingStart, rectTmp.top,
							                                rectTmp.right - it.paddingEnd, rectTmp.top + sz)
							Common.DIRL -> Common.iRect.set(rectTmp.right - sz, rectTmp.top + it.paddingTop,
							                                rectTmp.right, rectTmp.bottom - it.paddingBottom)
							Common.DIRU -> Common.iRect.set(rectTmp.left + it.paddingStart, rectTmp.bottom - sz,
							                                rectTmp.right - it.paddingEnd, rectTmp.bottom)
						}
						canvas.drawRect(Common.iRect, paintSel)
					}
				}
			}
		}
	}
	
	val arrEmpty = listOf<View>()
	
	
	/** Класс, реализующий список содержимого */
	inner class Content(context: Context, vert: Boolean) : Ribbon(context, 0, vert, style_ribbon) {
		
		val views = mutableListOf<View>()
		
		init {
			selector = null
			isVerticalScrollBarEnabled = false
			isHorizontalScrollBarEnabled = false

			scrollListener = { _, delta, _, _, _ ->
				val pos = caption.selectedItemPosition + if(delta < 0) 1 else -1
				"content scrollListener $pos $delta".info()
				currentTab = pos
			}
		}
		override fun generateDefaultLayoutParams() = LayoutParams(MATCH, MATCH, 0, -1, -1)
		
		override fun addView(child: View) {
			views.add(child)
			if(currentTab == -1) currentTab = 0
		}
	}
	
	/** Класс хранения состояния [tab] */
	private class TabState(@JvmField val tab: Int, state: Parcelable?): View.BaseSavedState(state)
	
	/** Сохранение состояния */
	override fun onSaveInstanceState(): Parcelable = TabState(currentTab, super.onSaveInstanceState())
	
	/** Восстановление состояния */
	override fun onRestoreInstanceState(state: Parcelable?) {
		var st = state
		if(st is TabState) {
			currentTab = st.tab
			st = st.superState
		}
		super.onRestoreInstanceState(st)
		requestLayout()
	}
}

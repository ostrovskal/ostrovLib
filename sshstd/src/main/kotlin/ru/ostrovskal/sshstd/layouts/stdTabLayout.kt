package ru.ostrovskal.sshstd.layouts

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.objects.*
import ru.ostrovskal.sshstd.utils.*
import ru.ostrovskal.sshstd.widgets.Tile

/**
 * @author Шаталов С. В.
 * @since 0.2.2
*/

/** Класс, реализующий набор вкладок
 * @property captionPos  Позиция заголовка относительно содержимого
 * @property sizeCaption Размер заголовка, в процентах, относительно габаритов родителя
 * @property style       Cтиль вкладок
*/
open class TabLayout(context: Context, idContent: Int, @JvmField protected val captionPos: Int, @JvmField protected val sizeCaption: Int,
                     @JvmField protected val style: IntArray) : CommonLayout(context, captionPos test DIRV) {

	// Рисователь
	private val paint                   = Paint()
	
	// Рисователь выделенной вкладки
	private val paintSel                = Paint()
	
	// Временная область
	private val rectTmp                 = Rect()
	
	/** Событие уведомления об активации вкладки */
	@JvmField var tabChangeListener: ((tab: Int, content: ViewGroup) -> Unit)?   = null
	
	/** Признак отрисовки стрипов вкладок */
	@JvmField var isDrawStrips          = true
	
	/** Признак отрисовки стрипа не активной вкладки */
	@JvmField var isDrawInactiveStrips  = false
	
	/** Содержимое текущей вкладки */
	lateinit var currentContent: ViewGroup

	/** Заголовок */
	@JvmField val caption               = Caption(context, captionPos test DIRH)
	
	/** Содержимое */
	@JvmField val content               = Content(context, sizeCaption).apply { id = idContent }
	
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
			if(v != field && v >= 0 && v < content.childCount) {
				if(field != -1) content.getChildAt(field).visibility = View.GONE
				field = v
                currentContent = content.getChildAt(v) as ViewGroup
				currentContent.apply {
					tabChangeListener?.invoke(v, this)
					visibility = View.VISIBLE
					caption.invalidate()
				}
			}
		}
		
	init {
		onChangeTheme()
		if(captionPos == DIRU || captionPos == DIRL) {
			addView(caption)
			addView(content)
		} else {
			addView(content)
			addView(caption)
		}
	}
	
	/** Позиционирование на разметке */
	@SuppressLint("DrawAllocation")
	override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
		super.onLayout(changed, l, t, r, b)
		if(changed) {
			val vert = captionPos test DIRV
			caption.layoutParams = LayoutParams(
				if (vert) MATCH else measuredWidth.fromPercent(sizeCaption),
				if (vert) measuredHeight.fromPercent(sizeCaption) else MATCH
			)
		}
	}

	/** Удаление всех представлений */
	override fun removeAllViews() {
		super.removeAllViews()
		caption.removeAllViews()
		content.removeAllViews()
		currentTab = -1
	}
	
	/** Добавление страницы с текстом [text], либо рисунком [nTile], либо иконкой [nIcon] */
	fun page(id: Int, text: Int = -1, nTile: Int = -1, nIcon: Int = -1, init: Content.() -> View) {
		val indicator = Tile(context, style).apply {
			if(nTile != -1) {
				if(nTile test 0x7f000000) tileResource = nTile else tile = nTile
			}
			if(nIcon != -1) {
				if(nIcon test 0x7f000000) iconResource = nIcon else tileIcon = nIcon
			}
			this.text = if(text != -1) resources.getString(text) else ""
		}
		indicator.tag = caption.childCount
		caption.addView(indicator, LayoutParams(MATCH, MATCH))
		indicator.setOnClickListener {
			currentTab = ((it.tag as? Int) ?: currentTab)
		}
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
	
	/** Класс, реализующий заголовок вкладки */
	inner class Caption(context: Context, vert: Boolean) : CommonLayout(context, vert) {
		/** Диспетчер отрисовки содержитого заголовка вкладки на канве [canvas] */
		public override fun dispatchDraw(canvas: Canvas) {
			super.dispatchDraw(canvas)
			if(isDrawStrips) {
				val selChild = getChildAt(currentTab)
				caption.loopChildren {
					it.getHitRect(rectTmp)
					val p = if(it == selChild) {
						paintSel
					} else {
						if(isDrawInactiveStrips) paint else return@loopChildren
					}
					val sz = p.strokeWidth.toInt() - 1
					when(captionPos) {
						DIRR    -> iRect.set(rectTmp.left, rectTmp.top + it.paddingTop, rectTmp.left + sz, rectTmp.bottom - it.paddingBottom)
						DIRD    -> iRect.set(rectTmp.left + it.paddingStart, rectTmp.top, rectTmp.right - it.paddingEnd, rectTmp.top + sz)
						DIRL    -> iRect.set(rectTmp.right - sz, rectTmp.top + it.paddingTop, rectTmp.right, rectTmp.bottom - it.paddingBottom)
						DIRU    -> iRect.set(rectTmp.left + it.paddingStart, rectTmp.bottom - sz, rectTmp.right - it.paddingEnd, rectTmp.bottom)
					}
					canvas.drawRect(iRect, p)
				}
			}
		}
		
		/** Добавление дочернего представления заголовка вкладки */
		override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
			(params as? LayoutParams)?.weight = 1f
			super.addView(child, index, params)
		}
		
		/** Установка доступности вкладок */
		override fun setEnabled(enabled: Boolean) {
			super.setEnabled(enabled)
			loopChildren { it.isEnabled = enabled }
		}
	}
	
	/** Класс, реализующий содержимое вкладки */
	inner class Content(context: Context, size: Int) : ContainerLayout(context, 100 - if(captionPos == DIRR) size else 0,
	                                                                   100 - if(captionPos == DIRD) size else 0, true) {
		/** Добавление дочернего представления */
		override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
			super.addView(child, index, params)
			if(currentTab == -1) currentTab = 0 else child.visibility = View.GONE
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

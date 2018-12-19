@file:Suppress("NOTHING_TO_INLINE")

package ru.ostrovskal.sshstd.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.view.View
import ru.ostrovskal.sshstd.Animator
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.TileDrawable
import ru.ostrovskal.sshstd.objects.ATTR_CHECKED
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.utils.bitmapGetCache
import ru.ostrovskal.sshstd.utils.noGetter
import ru.ostrovskal.sshstd.utils.rectWithPadding
import ru.ostrovskal.sshstd.utils.themeAttrValue

/**
 * @author Шаталов С.В.
 * @since  0.1.3
 */

/** Класс реализующий управление тайлами */
open class Tile(context: Context, style: IntArray) : Text(context, style) {
	
	/** Начальная верт. позиция */
	@JvmField protected var y       = 0
	
	/** Внутренние данные */
	@JvmField var data	            = 0f
	
	/** Рисунок или фон */
	@JvmField var drawable          = TileDrawable(context, style)
	
	/** Область отображения элемента в экранных координатах */
	@JvmField val rectScreen        = Rect()
	
	/** Лямба для выполнения анимации */
	@JvmField var doFrame: ((view: View, animator: Animator, frame: Int, direction: Int, began: Boolean) -> Boolean)? = null

	/** Аниматор */
	val animator	               by lazy { Animator(this, 10, 30, doFrame) }
	
	/** Область позиционирования */
	val drawablePosition: Rect
		get()                       = drawable.bounds
	
	/** Выравнивание */
	var align
		get()                       = drawable.align
		set(v)						{ drawable.align = v }
	
	/** Выравнивание иконки */
	var alignIcon
		get()                       = drawable.alignIcon
		set(v)						{ drawable.alignIcon = v }
	
	/** № тайла */
	var tile
		get()                       = drawable.tile
		set(v) 						{ drawable.tile = v }
	
	/** ID тайла из ресурсов */
	var tileResource: Int
		get()                       = noGetter()
		set(v)                      { tile = context.resources.getInteger(v) }
	
	/** Размеры тайла */
	val tileSize
		get()                       = drawable.tileSize
	
	/** Габариты тайла */
	val tileRect
		get()                       = drawable.tileRect
	
	/** Количество тайлов */
	val countTiles
		get()                       = drawable.countTiles
	
	/** № тайла иконки */
	var tileIcon
		get() 						= drawable.tileIcon
		set(v) 						{ drawable.tileIcon = v }
	
	/** ID иконки из ресурсов */
	var iconResource: Int
		get()                       = noGetter()
		set(v)                      { tileIcon = context.resources.getInteger(v) }
	
	/** Масштаб иконки */
	var scaleIcon
		get()                       = drawable.scaleIcon
		set(v)                      { drawable.scaleIcon = v }
	
	/** Состояние */
	open var states
		get()                       = drawable.states
		set(v) 						{ drawable.states = v }
	
	/** Фигура */
	var shape
		get()                       = drawable.shape
		set(v) 						{ drawable.shape = v }
	
	/** Масштабирование */
	var scale
		get()                       = drawable.scale
		set(v) 						{ drawable.scale = v; requestLayout() }
	
	/** Цвет рамки */
	open var selectorColor
		get() 						= drawable.selectorColor
		set(v) 						{ drawable.selectorColor = v }
	
	/** Толщина рамки */
	open var selectorWidth
		get() 						= drawable.selectorWidth
		set(v) 						{ drawable.selectorWidth = v }
	
	/** Фильтр */
	var filter
		get() 						= drawable.filter
		set(v) 						{ drawable.filter = v }
	
	/** Смещение тени */
	var shadowOffset
		get()                       = drawable.shadowOffset
		set(v)                      { drawable.shadowOffset = v }
	
	/** Имя рисунка */
	var keyBitmap: String
		get() 						= drawable.keyBitmap
		set(v) 						{ drawable.setBitmap(v) }
	
	/** Рисунок с тайлами */
	val bitmap: Bitmap?
		get()		                = context.bitmapGetCache(keyBitmap)
	
	/** Признак выделения */
	open var isChecked
		get()						= data.toInt() != 0
		set(v) 						{ data = if(v) 1f else 0f }
	
	init {
		isChecked = Theme.boolean(context, style.themeAttrValue(ATTR_CHECKED, 0))
		setDrawables()
	}
	
	/**
	 * Установка рисунка
	 *
	 * @param key Имя рисунка
	 * @param cs Количество столбцов
	 * @param rs Количество строк
	 * @param nm Номер тайла
	 */
	inline fun setBitmap(key: String, cs: Int = 1, rs: Int = 1, nm: Int = 0) { drawable.setBitmap(key, cs, rs, nm) }
	
	/** Установка состояния отображения */
	override fun drawableStateChanged() {
		super.drawableStateChanged()
		drawable.state = drawableState
		paint.colorFilter = drawable.filter
	}
	
	/** Определение позиции тайла */
	override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
		super.onLayout(changed, left, top, right, bottom)
		getDrawingRect(rectScreen)
		val vspace = height - (compoundPaddingBottom + compoundPaddingTop)
		y = scrollY + compoundPaddingTop + (vspace - drawablePosition.height()) / 2
	}
	
	/** Вычисление габаритов тайла */
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		drawable.updateBound(rectWithPadding(iRect))
	}
	
	private fun setDrawables() {
		var drLeft: TileDrawable? = null
		var drRight: TileDrawable? = null
		when(align and TILE_GRAVITY_MASK_HORZ) {
			TILE_GRAVITY_START      -> drLeft = drawable
			TILE_GRAVITY_END        -> drRight = drawable
			TILE_GRAVITY_CENTER_HORZ-> background = drawable
		}
		setCompoundDrawablesWithIntrinsicBounds(drLeft, null, drRight, null)
	}
	
	/** Вычисление габаритов тайла [n] и запись его в [r] */
	inline fun resolveTile(n : Int, r: Rect) = drawable.resolveTile(n, r)
	
	/** Обновление картинки */
	override fun invalidateDrawable(drawable: Drawable) {
		(drawable as? TileDrawable)?.apply {
			super.invalidateDrawable(drawable)
			if(isRedraw) {
				if((align and TILE_GRAVITY_MASK_HORZ) != TILE_GRAVITY_CENTER_HORZ) setDrawables()
				invalidate()
			}
		}
	}
	
	override fun onChangeTheme() {
		super.onChangeTheme()
		drawable.onChangeTheme()
	}

	/** Установка угла отображения */
	override fun setRotation(rotation: Float) {
		super.setRotation(rotation)
		drawable.angle = rotation
	}
	
	/** Класс хранения состояния [data] тайла */
	private class TileState(@JvmField val data: Float, state: Parcelable?): View.BaseSavedState(state)
	
	/** Сохранение состояния */
	override fun onSaveInstanceState(): Parcelable = TileState(data, super.onSaveInstanceState())
	
	/** Восстановление состояния */
	override fun onRestoreInstanceState(state: Parcelable?) {
		var st = state
		if(st is TileState) {
			data = st.data
			st = st.superState
		}
		super.onRestoreInstanceState(st)
	}
}

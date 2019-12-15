package ru.ostrovskal.sshstd.widgets.lists

import android.content.Context
import android.database.DataSetObserver
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.view.*
import android.widget.EdgeEffect
import android.widget.ListAdapter
import android.widget.OverScroller
import com.github.ostrovskal.sshstd.R
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.Size
import ru.ostrovskal.sshstd.TileDrawable
import ru.ostrovskal.sshstd.Touch
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.utils.*
import ru.ostrovskal.sshstd.widgets.Text
import kotlin.math.abs

/**
 * @author Шаталов С.В.
 * @since 0.2.6
 */

/** Базовый класс, реализующий общий функционал построения списков
*
* @property mIsVert Ориентация
* @property style   Стиль
*/
abstract class BaseRibbon(context: Context, id: Int, @JvmField val mIsVert: Boolean, @JvmField val style: IntArray) : ViewGroup(context, null, R.attr.scrollBarsStyle) {
	
	/** Селектор */
	open var selector: Drawable? = null
	
	/** Разделитель элементов списка */
	open var divider: Drawable? = null
	
	/** Высота разделителя */
	open var dividerHeight      = 0
		set(v)                  { if(field != v) { field = v; requestLayout() } }
	
	/** Событие прокрутки */
	@JvmField var scrollListener: ((ribbon: BaseRibbon, delta: Int, firstItem: Int, itemCount: Int, totalCount: Int) -> Unit)? = null
	
	/** Событие клика на элементе списка */
	@JvmField var itemClickListener: ((ribbon: BaseRibbon, view: View, position: Int, id: Long) -> Unit)? = null

	/** Событие долгого клика на элементе */
	@JvmField var itemLongClickListener: ((ribbon: BaseRibbon, view: View, position: Int, id: Long) -> Unit)? = null

	/** Событие завершения свайпа */
	@JvmField var flingFinishedListener: ((ribbon: BaseRibbon) -> Unit)? = null

	/** Установка выбранной позиции */
	open var selection
		get()                   = selectedItemPosition
		set(v)                  {
			mFling.finish()
			selectedItemPosition = v
			if(v < mFirstPosition || v >= (mFirstPosition + childCount))
				mNewFirstPosition = v
			mDelta = 0
			requestLayout()
		}
	
	/** Адаптер */
	open var adapter: ListAdapter? = null
		set(v) {
			field?.unregisterDataSetObserver(observer)
			resetList()
			field = v
			v?.apply {
				registerDataSetObserver(observer)
				repeat(viewTypeCount) {
					mCacheViews.add(mutableListOf())
				}
				mCount = count
			}
			requestLayout()
		}
	
	/** Количество элементов в адаптере */
	val count                   get()           = mCount
	
	/** Количество элементов в линии. Используется в Table */
	@JvmField protected var lines               = 1
	
	/** Ширина и состояние списка */
	@JvmField protected var mWidthMeasureSpec   = 0
	
	/** Высота и состояние списка */
	@JvmField protected var mHeightMeasureSpec  = 0
	
	/** Позиция первого видимого элемента */
	@JvmField var mFirstPosition                = 0
	
	/** Новая позиция первого видимого элемента */
	@JvmField var mNewFirstPosition             = 0
	
	// Смещение первого видимого элемента
	private var mDelta                          = 0
	
	/** Позиция элемента по которому был совершен первый клик */
	private var mClickPosition                  = -1
	
	/** Выделенный элемент списка */
	@JvmField protected var mItemSelected: View?= null
	
	/** Последний выделенный элемент */
	@JvmField var selectedItemPosition          = -1
	
	/** Количество элементов */
	@JvmField protected var mCount              = 0
	
	/** Признак обновления данных адаптера */
	@JvmField protected var mDataChanged        = false
	
	/** Объект, создающий эффект оверскролла */
	@JvmField protected val mGlow               = EdgeEffect(context)

	/** Объект жеста прокрутки списка */
	@JvmField protected val mFling              = Fling()

	/** Признак определяющий привязано ли новое представление к окну */
	@JvmField protected var mIsAttachedChild    = false

	/** Идентификатор касания */
	@JvmField protected var mTouchId            = 0

	/** Объект касания */
	@JvmField protected val touch				= Touch()

	/** Скоростной трекер */
	@JvmField protected val mTracker: VelocityTracker = VelocityTracker.obtain()

	/** Габариты списка с учетом отступов */
	@JvmField val mRectList                     = Rect()
	
	/** Начальная грань списка(верхняя/левая) */
	@JvmField var mEdgeStart                    = 0
	
	/** Конечная грань списка(нижняя/правая) */
	@JvmField var mEdgeEnd                      = 0
	
	/** Список кэшированных представлений по количеству типов в адаптере */
	@JvmField val mCacheViews                   = mutableListOf< MutableList<View> >()
	
	/** Признак, определяющий показывать ли эффект оверскролла */
	@JvmField var mIsGlow                       = true
	
	/** Чувствительность прокрутки */
	@JvmField var mDragSensitive                = Size(8, 8)
	
	// Обсервер принимающий данные от адаптера
	private var observer                        = RibbonObserver()
	
	// Объект отложенного клика по элементу
	private val mClick = Runnable {
		if(!mDataChanged) {
			mItemSelected = getChildAt(mClickPosition - mFirstPosition)
			if(isLongClickable && mItemSelected != null) {
				longClickOriginalAttachCount = windowAttachCount
				postDelayed(mLongClick, ViewConfiguration.getLongPressTimeout().toLong())
			}
			invalidate()
		}
	}

	// Счетчик привязанных окон на момент запуска долгого клика
	private var longClickOriginalAttachCount = 0

	// Длинный клик
	private val mLongClick = Runnable {
		if(windowAttachCount == longClickOriginalAttachCount && !mDataChanged) {
			mItemSelected?.apply {
				adapter?.let { itemLongClickListener?.invoke(this@BaseRibbon, this, mClickPosition, it.getItemId(mClickPosition)) }
			}
		}
	}

	// Для реализации жеста прокрутки
	private var mOverflingDistance              = 0
	
	/** Признак, определяющий позицию эффекта оверскролла */
	@JvmField protected var mIsStartEdgeGlow    = false
	
	/** Минимальная скорость прокрутки */
    @JvmField var mMinVelocity                  = 0
	
	/** Максимальная скорость прокрутки */
    @JvmField var mMaxVelocity                  = 0f
	
	init {
		this.id = id
		selector = TileDrawable(context, style_drawable_tile).apply {
			solid = Theme.integer(context, Theme.themeAttrValue(ATTR_SSH_COLOR_SELECTOR, ATTR_SSH_COLOR_SELECTOR or THEME))
		}
		ViewConfiguration.get(context).apply {
			mMinVelocity = scaledMinimumFlingVelocity
			mMaxVelocity = scaledMaximumFlingVelocity.toFloat()
			mOverflingDistance = scaledOverflingDistance
		}
		isVerticalScrollBarEnabled = mIsVert
		isHorizontalScrollBarEnabled = !mIsVert
		isScrollbarFadingEnabled = true
		isClickable = true
		isFocusableInTouchMode = true
		setWillNotDraw(false)
	}
	
	/** Действия при изменении темы */
	open fun onChangeTheme() {
		// базовые стили
		Theme.updateTheme(context, this, style)
		// дочерние
		this.changeTheme()
		// в кэше
		mCacheViews.forEach {
			when(it) {
				is Text       -> it.onChangeTheme()
				is Spinner    -> it.onChangeTheme()
				is BaseRibbon -> it.onChangeTheme()
				is ViewGroup  -> it.changeTheme()
			}
		}
		invalidate()
	}

	/** Определение факта касания для дочерних элементов */
	override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
		var delta = 0
		touch.event(ev).drag(mDragSensitive) { offs, _ ->
			delta = if(mIsVert) offs.h else offs.w
			if(touch.isUnpressed) touch.flags = 0
		}
		return delta != 0
	}

	/** Обработка события касания */
	override fun onTouchEvent(ev: MotionEvent): Boolean {
		if(!isEnabled) return isClickable || isLongClickable
		if(!isAttachedToWindow) return false
		return onItemTouch(ev)
	}
	
	/** Обработка события касания [ev] на элементе */
	open fun onItemTouch(ev: MotionEvent): Boolean {
		touch.event(ev)
		mTracker.addMovement(ev)
		val selected = mItemSelected
		touch.apply {
			if(isUnpressed) {
				mItemSelected?.apply {
					mItemSelected = null
					selectedItemPosition = mClickPosition
					adapter?.let { itemClickListener?.invoke(this@BaseRibbon, this, mClickPosition, it.getItemId(mClickPosition)) }
				}
				mClickPosition = -1
			} else {
				val pos = itemFromPoint(ptCurrent)
				if(mClickPosition != pos) {
					mFling.finish()
					if(mClickPosition == -1) {
						mClickPosition = pos
						postDelayed(mClick, ViewConfiguration.getTapTimeout().toLong())
					} else mItemSelected = null
				}
			}
		}
		onFling()
		if(selected != mItemSelected) invalidate()
		return true
	}

	/** Обработка механизма прокрутки списка посредством свайпа */
	open fun onFling(): Int {
		var delta = 0
		touch.drag(mDragSensitive) { offs, event ->
			if(!event) {
				// проверить продолжать прокрутку?
				mTouchId = touch.id
				mTracker.computeCurrentVelocity(1000, mMaxVelocity)
				delta = (if(mIsVert) mTracker.getYVelocity(mTouchId) else mTracker.getXVelocity(mTouchId)).toInt()
				if(abs(delta) > mMinVelocity) mFling.start(-delta)
			} else {
				delta = if(mIsVert) offs.h else offs.w
				scrolling(delta)
			}
		}
		return delta
	}
	
	/** Прокрутка списка на [delta] */
	open fun scrolling(delta: Int): Boolean {
		val count = childCount
		removeCallbacks(mClick)
		removeCallbacks(mLongClick)
		if(count > 0) {
			if(delta != 0) {
				val isPosStart = mFirstPosition == 0
				val isPosEnd = (mFirstPosition + count) == mCount
				val isEdgeStart = getChildAt(0).edge(mIsVert, false) >= mEdgeStart
				val isEdgeEnd = getChildAt(count - 1).edge(mIsVert, true) <= mEdgeEnd
				val isScrollToStart = isPosStart && isEdgeStart
				val isScrollToEnd = isPosEnd && isEdgeEnd
				if((delta > 0 && !isScrollToStart) || (delta < 0 && !isScrollToEnd)) {
					var posView = 0
					var idxView = mFirstPosition
					while(posView < childCount) {
						val child = getChildAt(posView)
						val isStart = (child.edge(mIsVert, false) + delta) >= mEdgeEnd
						val isEnd = (child.edge(mIsVert, true) + delta) <= mEdgeStart
						if(isStart || isEnd) {
							if(isEnd) mFirstPosition++
							detachViewFromParent(child)
							addCacheView(child, idxView)
						} else posView++
						idxView++
					}
					mItemSelected = null
					scrollListener?.invoke(this, delta, mFirstPosition, childCount, mCount)
					offsetChildren(delta)
					fill(0)
					awakenScrollBars()
					mNewFirstPosition = mFirstPosition
					mDelta = getChildAt(0)?.run { edge(mIsVert, false) - mEdgeStart } ?: 0
					invalidate()
					return false
				}
				// Предел прокрутки. Запуск эффекта
				overScroll(-delta)
				if (mIsGlow && (overScrollMode == View.OVER_SCROLL_ALWAYS || overScrollMode == View.OVER_SCROLL_IF_CONTENT_SCROLLS)) {
					val value = delta.toFloat() / (if (mIsVert) measuredHeight else measuredWidth)
					mIsStartEdgeGlow = delta < 0
					mGlow.onPull(value)
					invalidate()
				}
			}
		}
		return true
	}
	
	/** Заполнение списка */
	abstract fun fill(edgePos: Int)
	
	/** Заполнение списка от текущей позиции [position] до конца [end] */
	abstract fun fillEnd(position: Int, end: Int)
	
	/** Заполнение списка от текущей позиции [position] до начала [start] */
	abstract fun fillStart(position: Int, start: Int)
	
	/** Вычисление габаритов дочернего представления [child] */
	open fun childMeasure(child: View) {
		measureChild(child, mWidthMeasureSpec, mHeightMeasureSpec)
	}
	
	/** Вычисление позиций видимых представлений списка */
	override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
		repeat(childCount) { addCacheView(getChildAt(it), mNewFirstPosition + it) }
		detachAllViewsFromParent()
		var pos = mNewFirstPosition
		if(pos < 0) pos = 0
		if(pos >= mCount) pos = mCount - 1
		if(mNewFirstPosition != pos) mDelta = 0
		mFirstPosition = pos
		mNewFirstPosition = mFirstPosition
		if(mCount > 0) fill(mDelta)
		mDataChanged = false
		invalidate()
	}
	
	/** Получить представление из адаптера по позиции [pos] */
	open fun obtainView(pos: Int): View {
		mIsAttachedChild = false
		val view = getCacheView(pos)
		//val cache = (view as? Text)?.text
		//val posCache = (view?.layoutParams as? LayoutParams)?.pos ?: -1
		lateinit var child: View
		adapter?.let {
			child = it.getView(pos, view, this)
			if(child.importantForAccessibility == View.IMPORTANT_FOR_ACCESSIBILITY_AUTO) child.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
			val vlp = child.layoutParams
			(if(vlp == null) generateDefaultLayoutParams() as? LayoutParams
			else if(!checkLayoutParams(vlp)) generateLayoutParams(vlp) as? LayoutParams
			else vlp as? LayoutParams)?.apply {
				if(it.hasStableIds()) this.id = it.getItemId(pos)
				this.type = it.getItemViewType(pos)
				if(this != vlp) child.layoutParams = this
			}
		}
		if(view != null) {
			// Не равны -> значит адаптер создал новое представление
			if(child != view) addCacheView(view, pos) else mIsAttachedChild = true
		}
		return child
	}
	
	/**
	 * Добавление видимого элемента из кэша или из адаптера
	 *
	 * @param x     Гор. позиция дочернего представления
	 * @param y     Верт. позиция дочернего представления
	 * @param pos   Позиция в адаптере
	 * @param flow  Признак того, какую грань использовать
	 * @param where Позиция в разметке для добавления
	 *
	 * @return Возвращает созданное представление
	 */
	open fun addView(x: Int, y: Int, pos: Int, flow: Boolean, where: Int): View {
		val child = obtainView(pos)
		val p = child.layoutParams
		val needToMeasure = !mIsAttachedChild || child.isLayoutRequested
		
		if(mIsAttachedChild) attachViewToParent(child, where, p) else addViewInLayout(child, where, p, true)
		if(needToMeasure) childMeasure(child) else cleanupLayoutState(child)
		
		val w = child.measuredWidth
		val h = child.measuredHeight
		val childTop = if(mIsVert) { if(flow) y else y - h } else y
		val childLeft = if(mIsVert) x else { if(flow) x else x - w }
		
		if(needToMeasure) {
			val childRight = childLeft + w
			val childBottom = childTop + h
			child.layout(childLeft, childTop, childRight, childBottom)
		}
		else {
			child.offsetLeftAndRight(childLeft - child.left)
			child.offsetTopAndBottom(childTop - child.top)
		}
		
		return child
	}
	
	/** Получение индекса элемента по координатам клика [p] */
	fun itemFromPoint(p: PointF): Int {
		val x = p.x.toInt()
		val y = p.y.toInt()
		val count = childCount
		for(i in count - 1 downTo 0) {
			val child = getChildAt(i)
			if(child.visibility == View.VISIBLE) {
				child.getHitRect(iRect)
				if(iRect.contains(x, y)) {
					return mFirstPosition + i
				}
			}
		}
		return -1
	}
	
	/** Сброс списка в исходное состояние */
	fun resetList() {
		mDataChanged = false
		mFirstPosition = 0
		mItemSelected = null
		mClickPosition = -1
		selectedItemPosition = -1
		clearCacheViews()
	}

	/**
	 * Выполнение оверскролла в зависимости от ориентации списка
	 *
	 * @param delta   Относительное приращение, используется для задания величины эффекта
	 */
	protected fun overScroll(delta: Int) =
			overScrollBy(if(mIsVert) 0 else delta, if(mIsVert) delta else 0, if(mIsVert) 0 else scrollX, if(mIsVert) scrollY else 0,
			             0, 0, if(mIsVert) 0 else mOverflingDistance, if(mIsVert) mOverflingDistance else 0, false)
	
	/** Отключение списка от окна */
	override fun onDetachedFromWindow() {
		super.onDetachedFromWindow()
		removeCallbacks(mFling)
		removeCallbacks(mClick)
		removeCallbacks(mLongClick)
		resetList()
	}
	
	/** Отрисовка разделителя на канве [canvas] в позиции [rect] */
	open fun drawDivider(canvas: Canvas, rect: Rect) {
		divider?.apply {
			bounds = iRect
			draw(canvas)
		}
	}
	
	/** Отрисовка селектора на канве [canvas] в позиции [rect] элемента [item] */
	open fun drawSelector(canvas: Canvas, rect: Rect, item: View) {
		if(rect.setIntersect(mRectList, rect)) {
			selector?.apply {
				bounds = rect
				draw(canvas)
			}
		}
	}
	
	private fun resolveDivider(canvas: Canvas, offs1: Int, offs2: Int, flow: Boolean, position: Int) {
		val child = getChildAt(position)
		val v1 = child.edge(mIsVert, flow) // bottom right
		val v2 = child.edge(!mIsVert, false) // left top
		val v3 = child.edge(!mIsVert, true) // right bottom
		if(mIsVert) {
			iRect.set(v2, v1 + offs1, v3, v1 + offs2)
			if(iRect.bottom <= mEdgeStart || iRect.top >= mEdgeEnd) return
		} else {
			iRect.set(v1 + offs1, v2, v1 + offs2, v3)
			if(iRect.right <= mEdgeStart || iRect.left >= mEdgeEnd) return
		}
		drawDivider(canvas, iRect)
	}
	
	/** Диспетчер отрисовки - отображение селектора и разделителя, если есть */
	override fun dispatchDraw(canvas: Canvas) {
		if(selector != null) {
			mItemSelected?.apply {
				getHitRect(iRect)
				drawSelector(canvas, iRect, this)
			}
		}
		if(divider != null) {
			var count = childCount
			val h = dividerHeight
			if(mFirstPosition + count == mCount) count--
			if(count > 0 && h > 0) {
				if(mFirstPosition > 0) {
					repeat(lines) { resolveDivider(canvas, -h, 0, false, it) }
				}
				repeat(count) { resolveDivider(canvas, 0, h, true, it) }
			}
		}
		super.dispatchDraw(canvas)
	}
	
	/** Отрисовка эффекта оверскролла */
	override fun draw(canvas: Canvas) {
		super.draw(canvas)
		
		val w = mRectList.width()
		val h = mRectList.height()
		if(!mGlow.isFinished) {
			var degree: Float
			canvas.withSave {
				if(mIsVert) {
					degree = if(mIsStartEdgeGlow) {
						translate(w.toFloat(), height.toFloat()); 180f
					}
					else {
						translate(mRectList.left.toFloat(), 0f); 0f
					}
					mGlow.setSize(w, h)
				}
				else {
					degree = if(mIsStartEdgeGlow) {
						translate(width.toFloat(), mRectList.top.toFloat()); 90f
					}
					else {
						translate(0f, (h + mRectList.top).toFloat()); 270f
					}
					mGlow.setSize(h, w)
				}
				rotate(degree, 0f, 0f)
				if(mGlow.draw(this)) invalidate() else mGlow.onRelease()
			}
		}
	}
	
	/** Вычисление габаритов списка */
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		
		val widthMode = MeasureSpec.getMode(widthMeasureSpec)
		val heightMode = MeasureSpec.getMode(heightMeasureSpec)
		var widthSize = MeasureSpec.getSize(widthMeasureSpec)
		var heightSize = MeasureSpec.getSize(heightMeasureSpec)
		
		mWidthMeasureSpec = widthMeasureSpec
		mHeightMeasureSpec = heightMeasureSpec
		
		var childWidth = 0
		var childHeight = 0
		var childState = 0
		
		if(mCount > 0 && (widthMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.UNSPECIFIED)) {
			val child = obtainView(0)
			
			measureCacheChild(child, 0)
			
			childWidth = child.measuredWidth
			childHeight = child.measuredHeight
			childState = View.combineMeasuredStates(childState, child.measuredState)
			
			addCacheView(child, -1)
		}
		
		widthSize = if(widthMode == MeasureSpec.UNSPECIFIED) horizontalPadding + childWidth + verticalScrollbarWidth
		else widthSize or (childState and View.MEASURED_STATE_MASK)
		
		if(heightMode == MeasureSpec.UNSPECIFIED) heightSize = verticalPadding + childHeight + verticalFadingEdgeLength * 2
		if(heightMode == MeasureSpec.AT_MOST) heightSize = measureHeightOrWidthOfChildren(0, -1, if(mIsVert) heightSize else widthSize, -1)
		
		setMeasuredDimension(widthSize, heightSize)
		
		rectWithPadding(mRectList)
		mEdgeStart = if(mIsVert) mRectList.top else mRectList.left
		mEdgeEnd = if(mIsVert) mRectList.bottom else mRectList.right
	}
	
	/** Вычислить высоту|ширину всех дочерних */
	fun measureHeightOrWidthOfChildren(startPos: Int, endPos: Int, max: Int, disallow: Int): Int {
		var returned = if(mIsVert) verticalPadding else horizontalPadding
		if(adapter == null) return returned
		val dividerHeight = if(dividerHeight > 0 && divider != null) dividerHeight else 0
		var prevHW = 0
		
		val end = if(endPos == -1) mCount - 1 else endPos
		
		var i = startPos
		while(i <= end) {
			val child = obtainView(i)
			measureCacheChild(child, i)
			if(i > 0) returned += dividerHeight
			addCacheView(child, -1)
			returned += if(mIsVert) child.measuredHeight else child.measuredWidth
			if(returned >= max) return if(disallow in 0 until i && prevHW > 0 && returned != max) prevHW else max
			if(disallow in 0..i) prevHW = returned
			++i
		}
		return returned
	}
	
	private fun measureCacheChild(child: View, position: Int) {
		var p = child.layoutParams as? LayoutParams
		if(p == null) {
			p = generateDefaultLayoutParams() as? LayoutParams
			child.layoutParams = p
		}
		p?.apply {
			p.type = adapter?.getItemViewType(position) ?: 0
			val height = p.height
			val childWidthSpec = getChildMeasureSpec(mWidthMeasureSpec, horizontalPadding, p.width)
			val childHeightSpec = MeasureSpec.makeMeasureSpec(if(height > 0) height else 0, if(height > 0) MeasureSpec.EXACTLY else MeasureSpec.UNSPECIFIED)
			child.measure(childWidthSpec, childHeightSpec)
		}
	}
	
	/** Помещение выпавшего представления из области видимости в кэш */
	protected fun addCacheView(child: View, position: Int) {
		(child.layoutParams as? LayoutParams)?.apply {
			//"addCacheView pos: $position child: ${(child as? Text)?.text} oldPos: ${this.pos} $type".info()
			this.pos = position
			mCacheViews[this.type].add(child)
		}
	}

	/** Очистка кэша представлений */
	fun clearCacheViews() {
		mCacheViews.forEach {views ->
			views.forEach { removeDetachedView(it, false) }
			views.clear()
		}
	}
	
	/** Извлечение представления из кэша */
	protected fun getCacheView(position: Int): View? {
		adapter?.let { adapter ->
			val type = adapter.getItemViewType(position)
			if(type >= 0 && type < mCacheViews.size) {
				val views = mCacheViews[type]
				val size = views.size
				if(size > 0) {
					for(i in size - 1 downTo 0) {
						val view = views[i]
						(view.layoutParams as? LayoutParams)?.apply {
							if(adapter.hasStableIds()) {
								if(adapter.getItemId(position) == this.id) return views.removeAt(i)
							}
							else if(this.pos == position) return views.removeAt(i)
						}
					}
					return views.removeAt(size - 1)
				}
			}
		}
		return null
	}
	
	/** Класс параметров разметки дочерних элементов списка
	 * @property type	Тип элемента
	 * @property id		Идентификатор элемента
	 * @property pos	Позиция элемента
	 */
	class LayoutParams(width: Int, height: Int, @JvmField var type: Int, @JvmField var id: Long, @JvmField var pos: Int) : ViewGroup.LayoutParams(width, height)
	
	/** Генерация параметров разметки по умолчанию */
	override fun generateDefaultLayoutParams() : ViewGroup.LayoutParams = LayoutParams(if(mIsVert) MATCH else WRAP, if(mIsVert) WRAP else MATCH, 0, -1, -1)
	
	/** Генерация параметров разметки из базовой */
	override fun generateLayoutParams(p: ViewGroup.LayoutParams) : ViewGroup.LayoutParams = LayoutParams(p.width, p.height, 0, -1, -1)
	
	/** Проверка на парамерты разметки */
	override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean = p is LayoutParams
	
	/** Сдвиг всех видимых элементов списка на [delta] */
	fun offsetChildren(delta: Int) {
		loopChildren { if(mIsVert) it.offsetTopAndBottom(delta) else it.offsetLeftAndRight(delta) }
	}
	
	/** Ликвидация расстояния между началом списка и первым элементом, с разделителем [h] */
	protected fun correctLow(h: Int) {
		if(childCount > 0) {
			var delta = getChildAt(0).edge(mIsVert, false) - mEdgeStart
			if(mFirstPosition != 0) delta -= h
			if(delta > 0) offsetChildren(-delta)
		}
	}
	
	/** Ликвидация расстояния между концом списка и последним элементом, с разделителем [h] */
	protected fun correctHigh(h: Int) {
		val count = childCount - 1
		if(mFirstPosition + count == mCount - 1 && count >= 0) {
			var offset = mEdgeEnd - getChildAt(count).edge(mIsVert, true)
			val first = getChildAt(0)
			val start = first.edge(mIsVert, false)
			if(offset > 0 && (mFirstPosition > 0 || start < mEdgeStart)) {
				if(mFirstPosition == 0) offset = offset.coerceAtMost(mEdgeStart - start)
				offsetChildren(offset)
				if(mFirstPosition > 0) fillStart(mFirstPosition - lines, first.edge(mIsVert, false) - h)
			}
		}
	}
	
	// Вычисление величины страницы горизонтальной/вертикальной прокрутки
	private fun scrollExtent(): Int {
		val count = childCount
		if(count > 0) {
			var extent = ((count + lines - 1) / lines) * 100
			var view = getChildAt(0)
			var s = view.edge(mIsVert, false) * 100
			var size = if(mIsVert) view.height else view.width
			if(size > 0) extent += s / size
			view = getChildAt(count - 1)
			s = view.edge(mIsVert, true)
			size = if(mIsVert) view.height else view.width
			if(size > 0) extent -= (s - if(mIsVert) height else width) * 100 / size
			return extent
		}
		return 0
	}
	
	// Вычисление смещения горизонтальной/вертикальной прокрутки
	private fun scrollOffset(): Int {
		if(childCount > 0) {
			val view = getChildAt(0)
			val s = view.edge(mIsVert, false) * 100
			val size = if(mIsVert) view.height else view.width
			val scroll = if(mIsVert) scrollY else scrollX
			if(size > 0) {
				val count = ((mCount + lines - 1) / lines) * 100
				val which = (mFirstPosition / lines) * 100
				return (which - s / size + (scroll.toFloat() / (if (mIsVert) height else width) * count).toInt()).coerceAtLeast(0)
			}
		}
		return 0
	}
	
	// Вычисление диапазона горизонтальной/вертикальной прокрутки
	private fun scrollRange(): Int {
		val count = ((mCount + lines - 1) / lines) * 100
		val scroll = if(mIsVert) scrollY else scrollX
		val size = if(mIsVert) height else width
		var result = count.coerceAtLeast(0)
		if(scroll != 0) result += abs((scroll.toFloat() / size * count).toInt())
		return result
	}
	
	/** Вычисление диапазона вертикальной прокрутки */
	override fun computeVerticalScrollRange() = scrollRange()
	
	/** Вычисление диапазона горизонтальной прокрутки */
	override fun computeHorizontalScrollRange() = scrollRange()
	
	/** Вычисление смещения вертикальной прокрутки */
	override fun computeVerticalScrollOffset() = scrollOffset()
	
	/** Вычисление смещения горизонтальной прокрутки */
	override fun computeHorizontalScrollOffset() = scrollOffset()
	
	/** Вычисление величины страницы вертикальной прокрутки */
	override fun computeVerticalScrollExtent() = scrollExtent()
	
	/** Вычисление величины страницы горизонтальной прокрутки */
	override fun computeHorizontalScrollExtent() = scrollExtent()
	
	/** Класс хранения состояния списка
	 * @property first  Первый видимый элемент
	 * @property item   Последний выбранный элемент
	 * @property delta  Сдвиг элемента относительно границы списка
 	 */
	private class RibbonState(@JvmField val first: Int, @JvmField val item: Int, @JvmField val delta: Int, state: Parcelable?) : View.BaseSavedState(state)
	
	/** Сохранение состояния */
	override fun onSaveInstanceState(): Parcelable = RibbonState(mNewFirstPosition, selectedItemPosition, mDelta, super.onSaveInstanceState())
	
	/** Восстановление состояния */
	override fun onRestoreInstanceState(state: Parcelable?) {
		var st = state
		if(st is RibbonState) {
			mNewFirstPosition = st.first
			selectedItemPosition = st.item
			mDelta = st.delta
			st = st.superState
		}
		super.onRestoreInstanceState(st)
	}
	
	// Внутренний класс обсервера
	private inner class RibbonObserver : DataSetObserver() {
		/** Изменение данных адаптера */
		override fun onChanged() {
			mCount = adapter?.count ?: 0
			resetList()
			mDataChanged = true
			requestLayout()
		}
		
		/** При отсутствии данных */
		override fun onInvalidated() {
			onChanged()
		}
	}

	/** Класс, реализующий свайп */
	inner class Fling : Runnable {
		// Скроллер
		private val mScroller = OverScroller(context)

		// Последняя позиция
		private var mLastFling = 0

		/** Режим */
		@JvmField var mTouchMode = FLING_FINISH

		/** Проверка на то, что все элементы помещаются в область списка */
		private fun contentFits(): Boolean {
			val count = childCount
			if(count == 0) return true
			if(count != mCount) return false
			return getChildAt(0).edge(mIsVert, false) >= mEdgeStart && getChildAt(count - 1).edge(mIsVert, true) <= mEdgeEnd
		}

		/** Запуск */
		fun start(initialVelocity: Int) {
			mLastFling = if(initialVelocity < 0) Int.MAX_VALUE else 0
			if(mIsVert) {
				mScroller.fling(0, mLastFling, 0, initialVelocity, 0, Int.MAX_VALUE, 0, Int.MAX_VALUE)
			}
			else {
				mScroller.fling(mLastFling, 0, initialVelocity, 0, 0, Int.MAX_VALUE, 0, Int.MAX_VALUE)
			}
			mTouchMode = FLING_FLING
			postOnAnimation(this)
		}

		/** Отскок */
		fun startSpringback() {
			mTouchMode = if(mScroller.springBack(if(mIsVert) 0 else scrollX, if(mIsVert) scrollY else 0, 0, 0, 0, 0)) {
				invalidate()
				postOnAnimation(this)
				FLING_OVERFLING
			}
			else FLING_FINISH
		}

		/** Завершение */
		fun finish() {
			mTouchMode = FLING_FINISH
			removeCallbacks(this)
			mScroller.abortAnimation()
			flingFinishedListener?.invoke(this@BaseRibbon)
		}

		/** Непосредственно реализация жеста */
		private fun fling() {
			if(mCount == 0 || childCount == 0) {
				finish()
				return
			}
			val more = mScroller.computeScrollOffset()
			val coord = if(mIsVert) mScroller.currY else mScroller.currX
			var delta = mLastFling - coord
			val limit = (if(mIsVert) mRectList.height() else mRectList.width()) - 1
			delta = if(delta > 0) limit.coerceAtMost(delta) else (-limit).coerceAtLeast(delta)
			val atEdge = scrolling(delta)
			val atEnd = atEdge && delta != 0
			if(atEnd) {
				if(more) {
					if(mIsVert) mScroller.notifyVerticalEdgeReached(scrollY, 0, mOverflingDistance)
					else mScroller.notifyHorizontalEdgeReached(scrollX, 0, mOverflingDistance)
					val mode = overScrollMode
					mTouchMode = if(mode == View.OVER_SCROLL_ALWAYS || mode == View.OVER_SCROLL_IF_CONTENT_SCROLLS && !contentFits()) {
						mIsStartEdgeGlow = delta < 0
						//mGlow.onAbsorb(mScroller.currVelocity.toInt())
						FLING_OVERFLING
					} else FLING_FINISH
					invalidate()
					postOnAnimation(this)
				}
			}
			else {
				if(more) {
					if(atEdge) invalidate()
					mLastFling = coord
					postOnAnimation(this)
				}
				else finish()
			}
		}

		/** Обработка */
		override fun run() {
			when(mTouchMode) {
				FLING_SCROLL 	-> if(!mScroller.isFinished) fling()
				FLING_FLING 	-> fling()
				FLING_OVERFLING -> {
					if(mScroller.computeScrollOffset()) {
						val curr = if(mIsVert) mScroller.currY else mScroller.currX
						val scroll = if(mIsVert) scrollY else scrollX
						val delta = curr - scroll
						if(overScroll(delta)) {
							val crossDown = scroll <= 0 && curr > 0
							val crossUp = scroll >= 0 && curr < 0
							if(crossDown || crossUp) {
								var velocity = mScroller.currVelocity.toInt()
								if(crossUp) velocity = -velocity
								mScroller.abortAnimation()
								start(velocity)
							}
							else startSpringback()
						}
						else {
							invalidate()
							postOnAnimation(this)
						}
					}
					else finish()
				}
				else            -> finish()
			}
		}
	}
}

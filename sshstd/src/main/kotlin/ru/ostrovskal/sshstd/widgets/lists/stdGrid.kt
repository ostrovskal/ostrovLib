package ru.ostrovskal.sshstd.widgets.lists

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.utils.edge
import ru.ostrovskal.sshstd.utils.horizontalPadding
import ru.ostrovskal.sshstd.utils.verticalPadding

/**
 * @author Шаталов С.В.
 * @since 0.2.8
 */

/** Класс, реализующий сетку элементов */
open class Grid(context: Context, id: Int, vert: Boolean, style: IntArray): BaseRibbon(context, id, vert, style) {
	
	// Временное представление выделения
	private var mTmpSel: View?      = null
	
	// Требуемый размер пространства между ячейками
	private var mReqCellSpacing     = 0
	
	// Требуемый размер ячейки
	private var mReqCellSize        = 0
	
	// Требуемое количество ячеек
	private var mReqNumCells        = 0
	
	// Размер пространства между ячейками
	private var mCellSpacing        = 0
	
	// Размер ячейки
	private var mCellSize           = 0
	
	override var divider: Drawable? = null
	
	override var dividerHeight      = 0
	
	/** Режим растягивания */
	var stretchMode: Int            = GRID_STRETCH_UNIFORM
		set(v)                      { if(field != v) { field = v; requestLayout() } }
	
	/** Размер ячейки */
	var cellSize                    get() = mCellSize
		set(v)                      { if(mReqCellSize != v) { mReqCellSize = v; requestLayout() } }
	
	/** Количество ячеек */
	var numCells                    get() = lines
		set(v)                      { if(mReqNumCells != v) { mReqNumCells = v; requestLayout() } }
	
	/** Пространство между ячейками */
	var cellSpacing                 get() = mCellSpacing
		set(v)                      { if(mReqCellSpacing != v) { mReqCellSpacing = v; requestLayout() } }
	
	/** Простанство между линиями */
	var lineSpacing                 = 0
		set(v)                      { if(field != v) { field = v; requestLayout() } }
	
	init { 	Theme.setBaseAttr(context, this, style) }
	
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		// Вычисление параметров ячеек
		val wh = if(mIsVert) mRectList.width() else mRectList.height()
		var spacing = mReqCellSpacing
		var size = mReqCellSize
		val num = Math.max(1, if(mReqNumCells > 0) mReqNumCells else { if(size <= 0) 2  else (wh + spacing) / (size + spacing) })
		
		val spaceOver = (wh - num * size - (num - 1) * spacing)
		
		when(stretchMode) {
			GRID_STRETCH_CELL   -> size += spaceOver / num
			GRID_STRETCH_SPACING-> spacing += spaceOver / (if(num > 1) num - 1 else 1)
			GRID_STRETCH_UNIFORM-> spacing += spaceOver / (num + 1)
		}
		mCellSize = size
		mCellSpacing = spacing
		lines = num
	}
	
	override fun fillEnd(position: Int, end: Int) {
		var pos = position
		var next = end
		val h = dividerHeight + lineSpacing
		
		while(next < mEdgeEnd && pos < mCount) {
			next = makeLine(pos, next, true).edge(mIsVert, true)
			next += h
			pos += numCells
		}
		correctHigh(h)
	}
	
	override fun fillStart(position: Int, start: Int) {
		var pos = position
		var next = start
		val h = dividerHeight + lineSpacing
		
		while(next > mEdgeStart && pos >= 0) {
			next = makeLine(pos, next, false).edge(mIsVert, false)
			next -= h
			mFirstPosition = pos
			pos -= numCells
		}
		correctLow(h)
	}
	
	// Создание линии
	private fun makeLine(position: Int, coord1: Int, flow: Boolean): View {
		lateinit var child: View
		var coord2 = (if(mIsVert) mRectList.left else mRectList.top) + if(stretchMode == GRID_STRETCH_UNIFORM) cellSpacing else 0
		val last = Math.min(position + lines, mCount)
		
		for(pos in position until last) {
			val where = if(flow) -1 else pos - position
			child = addView(if(mIsVert) coord2 else coord1, if(mIsVert) coord1 else coord2, pos, flow, where)
			if(pos == selection) mTmpSel = child
			coord2 += cellSize + if(pos < last - 1) cellSpacing else 0
		}
		return child
	}
	
	override fun fill(edgePos: Int) {
		val hd = dividerHeight + lineSpacing
		mFirstPosition -= mFirstPosition % lines
		getChildAt(0)?.apply {
			fillStart(mFirstPosition - lines, edge(mIsVert, false) - hd + edgePos)
		}
		with(getChildAt(childCount - 1)) {
			val start = (if(this == null) mEdgeStart else edge(mIsVert, true) + hd) + edgePos
			fillEnd(mFirstPosition + childCount, start)
		}
	}
	
	override fun childMeasure(child: View) {
		val params = child.layoutParams
		val childHeightSpec = if(mIsVert) {
			ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), verticalPadding, params.height)
		} else {
			ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(cellSize, View.MeasureSpec.EXACTLY), verticalPadding, params.height)
		}
		val childWidthSpec = if(mIsVert) {
			ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(cellSize, View.MeasureSpec.EXACTLY), horizontalPadding, params.width)
		} else {
			ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), horizontalPadding, params.width)
		}
		child.measure(childWidthSpec, childHeightSpec)
	}
}

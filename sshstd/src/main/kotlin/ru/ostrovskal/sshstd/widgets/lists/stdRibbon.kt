package ru.ostrovskal.sshstd.widgets.lists

import android.content.Context
import android.view.View
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.utils.edge

/**
 * @author Шаталов С.В.
 * @since 0.2.7
 */

/** Класс, реализующий список элементов */
open class Ribbon(context: Context, id: Int, vert: Boolean, style: IntArray) : BaseRibbon(context, id, vert, style) {
	
	init { Theme.setBaseAttr(context, this, style) }
	
	// Заполнение списка видимыми элементами
	override fun fill(edgePos: Int) {
		val h = dividerHeight
		
		getChildAt(0)?.apply {
			fillStart(mFirstPosition - 1, edge(mIsVert, false) - h + edgePos)
		}
		with(getChildAt(childCount - 1)) {
			val start = (if(this == null) mEdgeStart else edge(mIsVert, true) + h) + edgePos
			fillEnd(mFirstPosition + childCount, start)
		}
	}
	
	override fun fillStart(position: Int, start: Int) {
		var next = start
		var pos = position
		val h = dividerHeight
		
		while(next > mEdgeStart && pos >= 0) {
			next = addView(next, pos, false, 0).edge(mIsVert, false) - h
			pos--
		}
		mFirstPosition = pos + 1
		correctLow(h)
	}
	
	override fun fillEnd(position: Int, end: Int) {
		var next = end
		var pos = position
		val h = dividerHeight
		
		while(next < mEdgeEnd && pos < mCount) {
			next = addView(next, pos, true, -1).edge(mIsVert, true) + h
			pos++
		}
		correctHigh(h)
	}
	
	// Добавление видимого элемента из кэша или из адаптера
	private fun addView(coord: Int, position: Int, flow: Boolean, where: Int): View {
		val x = if(mIsVert) mRectList.left else coord
		val y = if(mIsVert) coord else mRectList.top
		return super.addView(x, y, position, flow, where)
	}
}


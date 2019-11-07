package ru.ostrovskal.sshstd

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.OverScroller

/** Класс реализации жеста прокрутки списка с инерцией */
open class Fling(context: Context, @JvmField val isVert: Boolean, layout: ViewGroup) : Runnable {
    // Скроллер
    private val mScroller = OverScroller(context)

    // Последняя позиция
    private var mLastFling = 0

    // Режим
    private var mTouchMode = 0

    // Проверка на то, что все элементы помещаются в область списка
    private fun contentFits(): Boolean {
        val count = childCount
        if(count == 0) return true
        if(count != mCount) return false
        return getChildAt(0).edge(isVert, false) >= mEdgeStart && getChildAt(count - 1).edge(isVert, true) <= mEdgeEnd
    }

    // Запуск
    fun start(initialVelocity: Int) {
        mLastFling = if(initialVelocity < 0) Int.MAX_VALUE else 0
        if(isVert) {
            mScroller.fling(0, mLastFling, 0, initialVelocity, 0, Int.MAX_VALUE, 0, Int.MAX_VALUE)
        }
        else {
            mScroller.fling(mLastFling, 0, initialVelocity, 0, 0, Int.MAX_VALUE, 0, Int.MAX_VALUE)
        }
        mTouchMode = Common.FLING_FLING
        postOnAnimation(this)
    }

    // Отскок
    fun startSpringback() {
        mTouchMode = if(mScroller.springBack(if(isVert) 0 else scrollX, if(mIsVert) scrollY else 0, 0, 0, 0, 0)) {
            invalidate()
            postOnAnimation(this)
            Common.FLING_OVERFLING
        }
        else Common.FLING_FINISH
    }

    // Завершение
    fun finish() {
        mTouchMode = Common.FLING_FINISH
        removeCallbacks(this)
        mScroller.abortAnimation()
        flingFinishedListener?.invoke(this@BaseRibbon)
    }

    // Непосредственно реализация жеста
    private fun fling() {
        if(mCount == 0 || childCount == 0) {
            finish()
            return
        }
        val more = mScroller.computeScrollOffset()
        val coord = if(isVert) mScroller.currY else mScroller.currX
        var delta = mLastFling - coord
        val limit = (if(isVert) mRectList.height() else mRectList.width()) - 1
        delta = if(delta > 0) limit.coerceAtMost(delta) else (-limit).coerceAtLeast(delta)
        val atEdge = scrolling(delta)
        val atEnd = atEdge && delta != 0
        if(atEnd) {
            if(more) {
                if(isVert) mScroller.notifyVerticalEdgeReached(scrollY, 0, mOverflingDistance)
                else mScroller.notifyHorizontalEdgeReached(scrollX, 0, mOverflingDistance)
                val mode = overScrollMode
                mTouchMode = if(mode == View.OVER_SCROLL_ALWAYS || mode == View.OVER_SCROLL_IF_CONTENT_SCROLLS && !contentFits()) {
                    mIsStartGlow = delta < 0
                    mGlow.onAbsorb(mScroller.currVelocity.toInt())
                    Common.FLING_OVERFLING
                } else Common.FLING_FINISH
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

    override fun run() {
        when(mTouchMode) {
            Common.FLING_SCROLL -> if(!mScroller.isFinished) fling()
            Common.FLING_FLING -> fling()
            Common.FLING_OVERFLING -> {
                if(mScroller.computeScrollOffset()) {
                    val curr = if(isVert) mScroller.currY else mScroller.currX
                    val scroll = if(isVert) scrollY else scrollX
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

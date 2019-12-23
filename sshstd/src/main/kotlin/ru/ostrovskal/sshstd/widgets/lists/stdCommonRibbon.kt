package ru.ostrovskal.sshstd.widgets.lists

import android.content.Context
import android.graphics.Rect
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.EdgeEffect
import android.widget.OverScroller
import ru.ostrovskal.sshstd.Common
import ru.ostrovskal.sshstd.Size
import ru.ostrovskal.sshstd.Touch
import ru.ostrovskal.sshstd.layouts.CommonLayout
import ru.ostrovskal.sshstd.utils.edge
import kotlin.math.abs

/**
 * @author Шаталов С.В.
 * @since 1.0.8
 */

/** Базовый класс, реализующий общий функционал построения списков
 *
 * @param    vert Ориентация
 * @property style   Стиль
 */

abstract class CommonRibbon(context: Context, vert: Boolean, @JvmField val style: IntArray) : CommonLayout(context, vert) {
    // Для реализации жеста прокрутки
    private var mOverflingDistance              = 0

    /** Количество элементов в линии. Используется в Table */
    @JvmField protected var lines               = 1

    /** Минимальная скорость прокрутки */
    @JvmField var mMinVelocity                  = 0

    /** Максимальная скорость прокрутки */
    @JvmField var mMaxVelocity                  = 0f

    /** Объект касания */
    @JvmField protected val touch				= Touch()

    /** Объект, создающий эффект оверскролла */
    @JvmField protected val mGlow               = EdgeEffect(context)

    /** Объект жеста прокрутки списка */
    @JvmField protected val mFling              = Fling()

    /** Идентификатор касания */
    @JvmField protected var mTouchId            = 0

    /** Скоростной трекер */
    @JvmField protected val mTracker: VelocityTracker = VelocityTracker.obtain()

    /** Признак, определяющий наличие эффекта оверскролла */
    @JvmField protected var mIsStartEdgeGlow    = false

    /** Количество элементов */
    @JvmField protected var mCount              = 0

    /** Признак, определяющий показывать ли эффект оверскролла */
    @JvmField var mIsGlow                       = true

    /** Чувствительность прокрутки */
    @JvmField var mDragSensitive                = Size(8, 8)

    /** Габариты списка с учетом отступов */
    @JvmField val mRectList                     = Rect()

    /** Начальная грань списка(верхняя/левая) */
    @JvmField var mEdgeStart                    = 0

    /** Конечная грань списка(нижняя/правая) */
    @JvmField var mEdgeEnd                      = 0

    /** Количество элементов в адаптере */
    val count                   get()       = mCount

    init {
        ViewConfiguration.get(context).apply {
            mMinVelocity = scaledMinimumFlingVelocity
            mMaxVelocity = scaledMaximumFlingVelocity.toFloat()
            mOverflingDistance = scaledOverflingDistance
        }
        isVerticalScrollBarEnabled = vert
        isHorizontalScrollBarEnabled = !vert
        isScrollbarFadingEnabled = true
        isClickable = true
        isFocusableInTouchMode = true
        setWillNotDraw(false)
    }

    /** Прокрутка на [delta] */
    abstract fun scrolling(delta: Int): Boolean

    /**
     * Выполнение оверскролла в зависимости от ориентации списка
     *
     * @param delta   Относительное приращение, используется для задания величины эффекта
     */
    open fun overScroll(delta: Int): Boolean {
        return overScrollBy(if (vert) 0 else delta, if (vert) delta else 0, if (vert) 0 else scrollX, if (vert) scrollY else 0,
                0, 0, if (vert) 0 else mOverflingDistance, if (vert) mOverflingDistance else 0, false)
    }

    /** Вычисление величины страницы вертикальной прокрутки */
    override fun computeVerticalScrollExtent() = scrollExtent()

    /** Вычисление величины страницы горизонтальной прокрутки */
    override fun computeHorizontalScrollExtent() = scrollExtent()

    /** Вычисление диапазона вертикальной прокрутки */
    override fun computeVerticalScrollRange() = scrollRange()

    /** Вычисление диапазона горизонтальной прокрутки */
    override fun computeHorizontalScrollRange() = scrollRange()

    // Вычисление диапазона горизонтальной/вертикальной прокрутки
    private fun scrollRange(): Int {
        val count = ((mCount + lines - 1) / lines) * 100
        val scroll = if(vert) scrollY else scrollX
        val size = if(vert) height else width
        var result = count.coerceAtLeast(0)
        if(scroll != 0) result += abs((scroll.toFloat() / size * count).toInt())
        return result
    }

    // Вычисление величины страницы горизонтальной/вертикальной прокрутки
    private fun scrollExtent(): Int {
        val count = childCount
        if(count > 0) {
            var extent = ((count + lines - 1) / lines) * 100
            var view = getChildAt(0)
            var s = view.edge(vert, false) * 100
            var size = if(vert) view.height else view.width
            if(size > 0) extent += s / size
            view = getChildAt(count - 1)
            s = view.edge(vert, true)
            size = if(vert) view.height else view.width
            if(size > 0) extent -= (s - if(vert) height else width) * 100 / size
            return extent
        }
        return 0
    }

    /** Класс, реализующий свайп */
    inner class Fling : Runnable {
        // Скроллер
        private val mScroller = OverScroller(context)

        // Последняя позиция
        private var mLastFling = 0

        /** Режим */
        @JvmField var mTouchMode = Common.FLING_FINISH

        /** Проверка на то, что все элементы помещаются в область списка */
        private fun contentFits(): Boolean {
            val count = childCount
            if(count == 0) return true
            if(count != mCount) return false
            return getChildAt(0).edge(vert, false) >= mEdgeStart && getChildAt(count - 1).edge(vert, true) <= mEdgeEnd
        }

        /** Запуск */
        fun start(initialVelocity: Int) {
            mLastFling = if(initialVelocity < 0) Int.MAX_VALUE else 0
            if(vert) {
                mScroller.fling(0, mLastFling, 0, initialVelocity, 0, Int.MAX_VALUE, 0, Int.MAX_VALUE)
            }
            else {
                mScroller.fling(mLastFling, 0, initialVelocity, 0, 0, Int.MAX_VALUE, 0, Int.MAX_VALUE)
            }
            mTouchMode = Common.FLING_FLING
            postOnAnimation(this)
        }

        /** Отскок */
        fun startSpringback() {
            mTouchMode = if(mScroller.springBack(if(vert) 0 else scrollX, if(vert) scrollY else 0, 0, 0, 0, 0)) {
                invalidate()
                postOnAnimation(this)
                Common.FLING_OVERFLING
            }
            else Common.FLING_FINISH
        }

        /** Завершение */
        fun finish() {
            mTouchMode = Common.FLING_FINISH
            removeCallbacks(this)
            mScroller.abortAnimation()
            //flingFinishedListener?.invoke(this@BaseRibbon)
        }

        /** Непосредственно реализация жеста */
        private fun fling() {
            if(mCount == 0 || childCount == 0) {
                finish()
                return
            }
            val more = mScroller.computeScrollOffset()
            val coord = if(vert) mScroller.currY else mScroller.currX
            var delta = mLastFling - coord
            val limit = (if(vert) mRectList.height() else mRectList.width()) - 1
            delta = if(delta > 0) limit.coerceAtMost(delta) else (-limit).coerceAtLeast(delta)
            val atEdge = scrolling(delta)
            val atEnd = atEdge && delta != 0
            if(atEnd) {
                if(more) {
                    if(vert) mScroller.notifyVerticalEdgeReached(scrollY, 0, mOverflingDistance)
                    else mScroller.notifyHorizontalEdgeReached(scrollX, 0, mOverflingDistance)
                    val mode = overScrollMode
                    mTouchMode = if(mode == View.OVER_SCROLL_ALWAYS || mode == View.OVER_SCROLL_IF_CONTENT_SCROLLS && !contentFits()) {
                        mIsStartEdgeGlow = delta < 0
                        //mGlow.onAbsorb(mScroller.currVelocity.toInt())
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

        /** Обработка */
        override fun run() {
            when(mTouchMode) {
                Common.FLING_SCROLL -> if(!mScroller.isFinished) fling()
                Common.FLING_FLING -> fling()
                Common.FLING_OVERFLING -> {
                    if(mScroller.computeScrollOffset()) {
                        val curr = if(vert) mScroller.currY else mScroller.currX
                        val scroll = if(vert) scrollY else scrollX
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

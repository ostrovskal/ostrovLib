package ru.ostrovskal.sshstd.ui

import android.view.ViewManager
import com.github.ostrovskal.sshstd.R
import ru.ostrovskal.sshstd.Common.DIRU
import ru.ostrovskal.sshstd.layouts.*
import ru.ostrovskal.sshstd.objects.style_tab_page
import ru.ostrovskal.sshstd.utils.uiView

/** Реализация линейной разметки с ориентацией [vert] и инициализатором [init] */
inline fun ViewManager.linearLayout(vert: Boolean = true, init: CommonLayout.() -> Unit) =
		uiView( { CommonLayout(it, vert) }, init)

/** Реализация абсолютной разметки с инициализатором [init] */
inline fun ViewManager.absoluteLayout(init: AbsoluteLayout.() -> Unit) = uiView({ AbsoluteLayout(it) }, init)

/** Реализация разметки вертикальной прокрутки с инициализатором [init] */
inline fun ViewManager.vertScrollLayout(init: VerticalScrollLayout.() -> Unit) = uiView({ VerticalScrollLayout(it) }, init)

/** Реализация разметки горизонтальной прокрутки с инициализатором [init] */
inline fun ViewManager.horzScrollLayout(init: HorizontalScrollLayout.() -> Unit) = uiView({ HorizontalScrollLayout(it) }, init)

/** Реализация разметки для радио кнопок, ориентацией [vert] и инициализатором [init] */
inline fun ViewManager.radioGroup(vert: Boolean = true, init: RadioLayout.() -> Unit) =
		uiView( { RadioLayout(it, vert) }, init)

/** Реализация растягивающей разметки с ориентацией [vert] и инициализатором [init]  */
inline fun ViewManager.stretchLayout(vert: Boolean = true, init: StretchLayout.() -> Unit) =
		uiView( { StretchLayout(it, vert) }, init)

/** Реализация Tab с кастомным стилем вкладок [style], позицией заголовка [captionPos], относительным размером заголовка [sizeCaption] и инициализатором [init] */
inline fun ViewManager.tabLayout(id: Int = R.id.tabHost, idContent: Int = R.id.tabContent, captionPos: Int = DIRU, sizeCaption: Int = 20, style: IntArray = style_tab_page, init: TabLayout.() -> Unit) =
		uiView( { TabLayout(it, idContent, captionPos, sizeCaption, style).apply { this.id = id } }, init)

/** Реализация TabRibbon с кастомным стилем вкладок [style], позицией заголовка [captionPos], относительным размером заголовка [sizeCaption] и инициализатором [init] */
inline fun ViewManager.tabRibbonLayout(id: Int = R.id.tabHost, idContent: Int = R.id.tabContent, captionPos: Int = DIRU, sizeCaption: Int = 20, style: IntArray = style_tab_page, init: TabRibbonLayout.() -> Unit) =
		uiView({ TabRibbonLayout(it, idContent, captionPos, sizeCaption, style).apply { this.id = id } }, init)

/** Реализация контейнерной разметки, шириной [width] и высотой [height] в процентах относительно родителя,
 *  варавниванием [aligned] дочерних представлений и инициализатором [init] */
inline fun ViewManager.containerLayout(width: Int, height: Int, aligned: Boolean, init: ContainerLayout.() -> Unit) =
		uiView({ ContainerLayout(it, width, height, aligned) }, init)

/** Реализация ячеистой разметки, со столбцами [cols] и строками [rows], пространством между ячейками [spacing],
 * признаком [show] отображения отладочной сетки и инициализатором [init] */
inline fun ViewManager.cellLayout(cols: Int, rows: Int, spacing: Int = 0, show: Boolean = false, init: CellLayout.() -> Unit)
		= uiView({ CellLayout(it, cols, rows, spacing, show) }, init)

/** Реализация разметки для анимированного поля ввода с инициализатором [init] */
inline fun ViewManager.editLayout(cols: Int = 19, rows: Int = 18, init: EditLayout.() -> Unit) = uiView({ EditLayout(it, cols, rows) }, init)


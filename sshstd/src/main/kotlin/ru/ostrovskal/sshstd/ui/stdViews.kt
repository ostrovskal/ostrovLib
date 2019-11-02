@file:Suppress("NOTHING_TO_INLINE")

package ru.ostrovskal.sshstd.ui

import android.view.View
import android.view.ViewManager
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.TileDrawable
import ru.ostrovskal.sshstd.utils.getUiContext
import ru.ostrovskal.sshstd.utils.uiCustom
import ru.ostrovskal.sshstd.utils.uiView
import ru.ostrovskal.sshstd.widgets.*
import ru.ostrovskal.sshstd.widgets.charts.ChartCircular
import ru.ostrovskal.sshstd.widgets.charts.ChartDiagram
import ru.ostrovskal.sshstd.widgets.html.Html
import ru.ostrovskal.sshstd.widgets.lists.Grid
import ru.ostrovskal.sshstd.widgets.lists.Ribbon
import ru.ostrovskal.sshstd.widgets.lists.Spinner

/** Установка фона с кастомным стилем [style] */
inline fun View.backgroundSet(style: IntArray = style_drawable_tile) {
	background = drawableTile(style) {}
}

/** Установка фона с кастомным стилем [style] и инициализатором [init] */
inline fun View.backgroundSet(style: IntArray = style_drawable_tile, init: TileDrawable.()-> Unit) {
	background = drawableTile(style, init)
}

/** Реализация drawableTile с кастомным стилем [style] */
inline fun View.drawableTile(style: IntArray = style_drawable_tile) =
		drawableTile(style) {}

/** Реализация drawableTile с кастомным стилем [style] и с инициализатором [init] */
inline fun View.drawableTile(style: IntArray = style_drawable_tile, init: TileDrawable.()-> Unit) =
		TileDrawable(context, style).apply { init() }

/** Реализация поля ввода с кастомным стилем [style] и подсказкой [hint] */
inline fun ViewManager.edit(id: Int, hint: Int, style: IntArray = style_edit) = edit(id, hint, style) {}

/** Реализация поля ввода с кастомным стилем [style], подсказкой [hint] и инициализатором [init] */
inline fun ViewManager.edit(id: Int, hint: Int, style: IntArray = style_edit, init: Edit.() -> Unit) =
		uiView( { Edit(it, id, hint, style) }, init)

/** Реализация поля ввода с кастомным стилем [style] и подсказкой [hint] */
inline fun ViewManager.editEx(id: Int, hint: Int, style: IntArray = style_edit, styleEx: IntArray = style_editEx) = editEx(id, hint, style, styleEx) {}

/** Реализация поля ввода с кастомным стилем [style], подсказкой [hint] и инициализатором [init] */
inline fun ViewManager.editEx(id: Int, hint: Int, style: IntArray = style_edit, styleEx: IntArray = style_editEx, init: EditEx.() -> Unit) =
	uiView( { EditEx(it, id, hint, style, styleEx) }, init)

/** Реализация текста с кастомным стилем [style] и текстом [text] */
inline fun ViewManager.text(text: Int, style: IntArray = style_text_normal) = text(text, style) {}

/** Реализация текста с кастомным стилем [style], текстом [text] и инициализатором [init] */
inline fun ViewManager.text(text: Int, style: IntArray = style_text_normal, init: Text.() -> Unit) =
		uiView({ Text(it, style).apply { setText(text) } }, init)

/** Реализация слайдера с идентификатором [id] и кастомным стилем [style], диапазоном [r], признаком доступности [enabled] */
inline fun ViewManager.seek(id: Int, r: IntRange, enabled: Boolean, style: IntArray = style_seek) = seek(id, r, enabled, style) {}

/** Реализация слайдера с идентификатором [id] и кастомным стилем [style], диапазоном [r],
 * признаком доступности [enabled] и инициализатором [init] */
inline fun ViewManager.seek(id: Int, r: IntRange, enabled: Boolean, style: IntArray = style_seek, init: Seek.() -> Unit) =
		uiView( { Seek(it, id, r, enabled, style) }, init)

/** Реализация переключателя с идентификатором [id], кастомным стилем [style] и текстом [text] */
inline fun ViewManager.switch(id: Int, text: Int, style: IntArray = style_switch) = switch(id, text, style) {}

/** Реализация переключателя с идентификатором [id] и кастомным стилем [style], текстом [text] и инициализатором [init] */
inline fun ViewManager.switch(id: Int, text: Int, style: IntArray = style_switch, init: Switch.() -> Unit) =
		uiView({ Switch(it, id, text, style) }, init)

/** Реализация флажка с идентификатором [id], кастомным стилем [style] и текстом [text] */
inline fun ViewManager.check(id: Int, text: Int, style: IntArray = style_check) = check(id, text, style) {}

/** Реализация флажка с идентификатором [id] и кастомным стилем [style], текстом [text] и инициализатором [init] */
inline fun ViewManager.check(id: Int, text: Int, style: IntArray = style_check, init: Check.() -> Unit) =
		uiView({ Check(it, id, text, style) }, init)

/** Реализация радио кнопки с кастомным стилем [style] и текстом [text] */
inline fun ViewManager.radio(id: Int, text: Int, style: IntArray = style_radio) = radio(id, text, style) {}

/** Реализация радио кнопки с кастомным стилем [style], текстом [text] и инициализатором [init] */
inline fun ViewManager.radio(id: Int, text: Int, style: IntArray = style_radio, init: Radio.() -> Unit) =
		uiView({ Radio(it, id, text, style) }, init)

/** Реализация кнопки с кастомным стилем [style] */
inline fun ViewManager.button(style: IntArray = style_button) = button(style) {}

/** Реализация кнопки с кастомным стилем [style] и инициализатором [init] */
inline fun ViewManager.button(style: IntArray = style_button, init: Tile.() -> Unit) =
		uiView({ Tile(it, style) }, init)

/** Реализация прогресса с идентификатором [id], максимальным значением [max], режимом отображения [mode] и кастомным стилем [style] */
inline fun ViewManager.progress(id: Int, max: Int, mode: Int, style: IntArray = style_progress) = progress(id, max, mode, style) {}

/** Реализация прогресса с идентификатором [id], максимальным значением [max], режимом отображения [mode],
 *  кастомным стилем [style] и инициализатором [init] */
inline fun ViewManager.progress(id: Int, max: Int, mode: Int, style: IntArray = style_progress, init: Progress.() -> Unit) =
		uiView({ Progress(it, id, max, mode, style) }, init)

/** Реализация простой диаграммы с кастомным стилем [style] и инициализатором [init] */
inline fun ViewManager.chartDiagram(style: IntArray = style_chart_diagram, init: ChartDiagram.() -> Unit) =
		uiView({ ChartDiagram(it, style) }, init)

/** Реализация круговой диаграммы с кастомным стилем [style] и инициализатором [init] */
inline fun ViewManager.chartCircular(style: IntArray = style_chart_circular, init: ChartCircular.() -> Unit) =
		uiView({ ChartCircular(it, style) }, init)

/** Реализация HTML с кастомным стилем [style] и инициализатором [init] */
inline fun ViewManager.html(style: IntArray = style_text_html, init: Html.() -> Unit) = uiView({ Html(it, style) }, init)

/** Реализация Spinner с кастомным стилем [style], стилем выпадающего списка [style_dropdown] и инициализатором [init] */
inline fun ViewManager.spinner(id: Int, style: IntArray = style_spinner, style_dropdown: IntArray = style_spinner_dropdown, init: Spinner.() -> Unit) =
		uiView( { Spinner(it, id, style, style_dropdown) }, init)

/** Реализация списка с кастомным стилем [style], ориентацией [vert] и инициализатором [init] */
inline fun ViewManager.ribbon(id: Int, vert: Boolean = true, style: IntArray = style_ribbon, init: Ribbon.() -> Unit) =
		uiView( { Ribbon(it, id, vert, style) }, init)

/** Реализация Сетки с кастомным стилем [style], ориентацией [vert] и инициализатором [init] */
inline fun ViewManager.grid(id: Int, vert: Boolean = true, style: IntArray = style_grid, init: Grid.() -> Unit) =
		uiView( { Grid(it, id, vert, style) }, init)

/** Подключение компонента [ui] пользовательского интерфейса в основную разметку с инициализитором [init] */
inline fun ViewManager.include(ui: UiComponent, init: UiComponent.(View) -> Unit) =
		uiView({ ui.createView(UiCtx(getUiContext())) }) {}.apply { ui.init(this) }

/** Реализация кастомного представления с кастомным стилем */
inline fun <reified T : View> ViewManager.custom(init: T.() -> Unit): T =
		uiView({ it.uiCustom(T::class.java) }) { init() }

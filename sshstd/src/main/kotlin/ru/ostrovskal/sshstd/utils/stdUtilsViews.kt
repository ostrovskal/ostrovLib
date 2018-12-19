@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package ru.ostrovskal.sshstd.utils

import android.content.Context
import android.graphics.Rect
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.TextView
import ru.ostrovskal.sshstd.Form
import ru.ostrovskal.sshstd.Wnd
import ru.ostrovskal.sshstd.layouts.TabLayout
import ru.ostrovskal.sshstd.ui.UiComponent
import ru.ostrovskal.sshstd.ui.UiCtx
import ru.ostrovskal.sshstd.widgets.Edit
import ru.ostrovskal.sshstd.widgets.Text
import ru.ostrovskal.sshstd.widgets.html.Html
import ru.ostrovskal.sshstd.widgets.lists.BaseRibbon
import ru.ostrovskal.sshstd.widgets.lists.Spinner

/** Возвращает представление из разметки по идентификатору [id] */
inline fun <T: View> ViewGroup.byId(id: Int): T? = findViewById(id)

/** Возвращает представление из разметки по индексу [idx] */
inline fun <T: View> ViewGroup.byIdx(idx: Int) : T = getChildAt(idx) as T

/** Возвращает представление из другого представления по индексу [idx] */
inline fun <T: View> View.byIdx(idx: Int) : T = (this as? ViewGroup)?.getChildAt(idx) as T

/** Добавление представления */
fun <T : View> ViewManager.addUiView(view: T) {
	when(this) {
		is ViewGroup -> addView(view)
		is UiCtx     -> addView(view, null)
		else         -> error("Invalid parent element ($this)!")
	}
}

/** Получение контекста */
fun ViewManager.getUiContext(): Context = when(this) {
	is ViewGroup -> context
	is UiCtx     -> ctx
	else         -> error("($this) invalid parent element!")
}

/** Динамическое создание представления, определенного класса [clazz] */
inline fun <reified T : View> Context.uiCustom(clazz: Class<T>): T = try {
	clazz.getConstructor(Context::class.java).newInstance(this)
} catch (e: NoSuchMethodException) { error("Не удалось создать объект класса ${clazz.name}: не найден подходящий конструктор!") }

/** Установка разметки в активити [activity] с параметрами [flags] */
fun <T : Wnd> UiComponent.setContent(activity: T, flags: Int) = activity.setLayout(createView(UiCtx(activity)) as ViewGroup, flags)

/** Создание разметки из контекста или активити */
inline fun Context.ui(init: UiCtx.() -> Unit) = UiCtx(this).apply { init() }

/** Создание разметки из фрагмента */
inline fun Form.ui(init: UiCtx.() -> Unit) = UiCtx(activity).apply { init() }

/** Добавление представления в разметку */
inline fun <T : View> ViewManager.uiView(factory: (ctx: Context) -> T, init: T.() -> Unit) =
		factory(getUiContext()).apply { init(); addUiView(this) }

/** Обновить тему у дочерних представлений разметки */
fun ViewGroup.changeTheme() {
	loopChildren {
		when(it) {
			is Spinner    -> it.onChangeTheme()
			is BaseRibbon -> it.onChangeTheme()
			is Text       -> it.onChangeTheme()
			is Edit       -> it.onChangeTheme()
			is Html       -> it.onChangeTheme()
			is TabLayout  -> it.onChangeTheme()
			is ViewGroup  -> it.changeTheme()
		}
	}
}

/** Загрузка разметки [layoutRes] из XML */
fun ViewGroup.inflate(layoutRes: Int): View = LayoutInflater.from(context).inflate(layoutRes, this, false)

/** Приведение тэга [key] представления в требуемый тип */
fun <T> View.tag(key: Int) = getTag(key) as T

/**
 * Определение границ представления
 *
 * @param vert  Вертикальный признак
 * @param flow  false = Передний, true = Задний
 */
fun View.edge(vert: Boolean, flow: Boolean) = if(vert) { if(flow) bottom else top } else { if(flow) right else left }

/** Формирование прямоугольника из представления с отступами */
fun View.rectWithPadding(out: Rect): Rect {
	out.left = paddingLeft
	out.top = paddingTop
	out.right = measuredWidth - paddingRight
	out.bottom = measuredHeight - paddingBottom
	return out
}

/** Цикл по представлениям родительской разметки */
inline fun ViewGroup.loopChildren(action: (View) -> Unit) {
	for(indexChild in 0 until childCount) action(getChildAt(indexChild))
}

/** Установка вертикального внешнего отступа */
var ViewGroup.MarginLayoutParams.verticalMargin
	get()                                           = topMargin + bottomMargin
	set(v)                                          { topMargin = v; bottomMargin = v }

/** Установка горизонтального внешнего отступа */
var ViewGroup.MarginLayoutParams.horizontalMargin
	get()                                           = leftMargin + rightMargin
	set(v)                                          { leftMargin = v; rightMargin = v }

/** Установка внешнего отступа */
var ViewGroup.MarginLayoutParams.margin
	get()                                           = leftMargin + rightMargin + topMargin + bottomMargin
	set(v)                                          = setMargins(v, v, v, v)

/** Получение/Установка левого внутреннего отступа */
inline var View.leftPadding
	get()                                           = paddingLeft
	set(value)                                      = setPadding(value, paddingTop, paddingRight, paddingBottom)

/** Получение/Установка верхнего внутреннего отступа */
inline var View.topPadding
	get()                                           = paddingTop
	set(value)                                      = setPadding(paddingLeft, value, paddingRight, paddingBottom)

/** Получение/Установка правого внутреннего отступа */
inline var View.rightPadding
	get()                                           = paddingRight
	set(value)                                      = setPadding(paddingLeft, paddingTop, value, paddingBottom)

/** Получение/Установка нижнего внутреннего отступа */
inline var View.bottomPadding
	get()                                           = paddingBottom
	set(value)                                      = setPadding(paddingLeft, paddingTop, paddingRight, value)

/** Установка горизонтального внутреннего отступа */
var View.horizontalPadding
	get()                                           = paddingStart + paddingEnd
	set(value)                                      = setPadding(value, paddingTop, value, paddingBottom)

/** Установка вертикального внутреннего отступа */
var View.verticalPadding
	get()                                           = paddingTop + paddingBottom
	set(value)                                      = setPadding(paddingLeft, value, paddingRight, value)

/** Установка внутреннего отступа */
var View.padding: Int
	get()                                           = paddingTop + paddingBottom + paddingLeft + paddingBottom
	set(value)                                      = setPadding(value, value, value, value)

/** Установка фонового цвета */
inline var View.backgroundColor: Int
	get()                                           = noGetter()
	set(v)                                          = setBackgroundColor(v)

/** Установка фоновой картинки из ресурсов */
inline var View.backgroundResource: Int
	get()                                           = noGetter()
	set(v)                                          = setBackgroundResource(v)

/** Установка цвета по умолчанию текста */
inline var TextView.textColor: Int
	get()                                           = currentTextColor
	set(v)                                          = setTextColor(v)

/** Установка максимальной длины текста */
inline var TextView.maxLength: Int
	get()                                           = noGetter()
	set(v)                                          { filters = arrayOf(InputFilter.LengthFilter(v)) }

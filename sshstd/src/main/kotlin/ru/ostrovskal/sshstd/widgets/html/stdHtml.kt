package ru.ostrovskal.sshstd.widgets.html

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcelable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.view.Gravity
import android.view.View
import android.widget.ScrollView
import android.widget.TableRow
import com.github.ostrovskal.sshstd.R
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.TileDrawable
import ru.ostrovskal.sshstd.Wnd
import ru.ostrovskal.sshstd.forms.Form
import ru.ostrovskal.sshstd.layouts.CommonLayout
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.utils.*
import ru.ostrovskal.sshstd.widgets.Text
import java.util.*
import kotlin.math.max

/**
 * @author  Шаталов С.В.
 * @since   0.6.0
 *
 * Класс реализующий HTML разметку посредством spannable
 *
 * @param context   Контекст
 * @param style     Стиль по умолчанию style_wnd
 */
open class Html(context: Context, @JvmField val style: IntArray): ScrollView(context) {
	companion object {
		/** Карта HTML флагов */
		val mapHtmlArray  = mapOf("CENTER" to Gravity.CENTER, "CENTER_VERTICAL" to Gravity.CENTER_VERTICAL,
												 "CENTER_HORIZONTAL" to Gravity.CENTER_HORIZONTAL, "START" to Gravity.START,
												 "END" to Gravity.END, "TOP" to Gravity.TOP, "BOTTOM" to Gravity.BOTTOM)
	}

	private inner class HtmlLayout(context: Context) : CommonLayout(context, true) {
		/** Позиция вертикальной прокрутки страницы */
		@JvmField var scrolling = -1
		
		override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
			super.onLayout(changed, l, t, r, b)
			if(scrolling >= 0) {
				this@Html.scrollTo(0, scrolling)
				scrolling = -1
			}
		}
	}

	// Цвет ссылки
	private var link					= 0
	
	// Цвет заголовка
	private var header					= 0
	
	// Цвет большого текста
	private var large					= 0
	
	// Цвет маленького текста
	private var small					= 0
	
	private var tdColspan               = 0
	private var tdAlign: String?        = null
	
	// Spannable текст
	private var text 				    = SpannableStringBuilder()
	
	// Парсер HTML
	private val parser                  = HtmlParser()
	
	/** Псевдонимы картинок */
	@JvmField var aliases               = mutableMapOf<String, BitmapAlias>()

    private var posStack        = -1

    // стек страниц
    private val stackPages= MutableList(32) { Article("", 0) }

	// Текущая таблица
	private var table: HtmlTable?       = null
	
	// Текущая строка таблицы
	private var row: HtmlRow?           = null
	
	// Владелец
	private val owner                   = HtmlLayout(context)
	
	// Текущая страница
	private var doc: Article?	        = null

	/** Получение признака активности */
	val valid get()                     = doc != null
	
	/** Корневая страница */
	@JvmField var root					 = ""
	
	/**
	 * Класс изображений псевдонимов
	 *
	 * @property key    Имя в кэше
	 * @property bmp    Ссылка на битмап
	 * @property cols   Количество колонок
	 * @property rows   Количество строк
	 * @property tile   Номер тайла
	 * @property icon   Номер иконки
	 * @property w      Ширина картинки, если < 0, то вписать в окно
	 * @property h      Высота картинки
	 * @property rot    Угол ротации
	 */
	class BitmapAlias(@JvmField val key: String, @JvmField val bmp: Bitmap?, @JvmField val cols: Int = 1, @JvmField val rows: Int = 1,
	                  @JvmField val tile: Int = 0, @JvmField val icon: Int = -1, @JvmField val w: Int = -1,
	                  @JvmField val h: Int = -1, @JvmField val rot: Float = 0f)
	
	/** Класс страниц со ссылкой [ref] на страницу и прокруткой [scroll] */
	private class Article(@JvmField val ref: String, @JvmField var scroll: Int)
	
	// Класс стилизаации полужирного/наклонного текста
	private class Styles(@JvmField val isBold: Boolean)
	
	// Класс, определяющий вертикальную позицию текста
	private class Vert(@JvmField val isSub: Boolean)

	// Класс, определяющий параметры текста
	private class Font(@JvmField val size: Float, @JvmField val bg: Int, @JvmField val col: Int)
	
	// Класс заголовка
	private class Title
	
	// Класс подчеркнутого текста
	private class Underline
	
	// Класс для относительных элементов текста к основному размеру
	private inner class RelSpan(level: Float, private val color: Int): RelativeSizeSpan(level) {
		/** Параметры отрисовки */
		override fun updateDrawState(ds: TextPaint) {
			ds.color = color
			ds.isFakeBoldText = color == header
			super.updateDrawState(ds)
		}
	}
	
	// Класс ссылок
	private inner class Href(href: String) : URLSpan(href) {
		/** Клик на ссылке */
		override fun onClick(arg0: View) {
			if(url.compareTo("back", true) == 0) {
				// выход, если стек пуст
				if(!back()) (context as? Wnd)?.apply { findForm<Form>(tagForm)?.backPressed() }
			} else setArticle(url, 0)
		}
		
		/** Параметры отрисовки */
		override fun updateDrawState(ds: TextPaint) {
			ds.color = link
			ds.isUnderlineText = true
		}
	}
	
	init {
		Theme.setBaseAttr(context, this, style)
		addView(owner)
		onChangeTheme()
	}

	/** Перехват запроса разметки для обновления владельца */
	override fun requestLayout() {
		super.requestLayout()
		if(childCount != 0) owner.requestLayout()
	}
	
	/** Событие при изменении темы оформления */
	fun onChangeTheme() {
		link	= Theme.integer(context, style.themeAttrValue(ATTR_SSH_COLOR_LINK, ATTR_SSH_COLOR_LINK or THEME))
		header	= Theme.integer(context, style.themeAttrValue(ATTR_SSH_COLOR_HTML_HEADER, ATTR_SSH_COLOR_HTML_HEADER or THEME))
		large	= Theme.integer(context, style.themeAttrValue(ATTR_SSH_COLOR_LARGE, ATTR_SSH_COLOR_LARGE or THEME))
		small	= Theme.integer(context, style.themeAttrValue(ATTR_SSH_COLOR_SMALL, ATTR_SSH_COLOR_SMALL or THEME))
		try {  if(posStack >= 0) stackPages[posStack].apply { --posStack; setArticle(ref, scroll) }
		} catch(e: EmptyStackException) { }
	}
	
	// Параграф в тексте
	private fun paragraph() {
		// параграф - две "\n\n" - проверим они уже есть? (только в начале не ставим)
		val len = text.length
		if(len >= 1 && text[len - 1] == '\n') {
			if(len >= 2 && text[len - 2] == '\n') return
			text.append("\n")
			return
		}
		if(len != 0) text.append("\n\n")
	}

	/** Возврат на предыдущую страницу или выход */
	fun back(): Boolean = try {
        if(posStack > 0) {
			doc = null
			stackPages[--posStack].apply { --posStack; setArticle(ref, scroll) }
		}
		true
	} catch(e: EmptyStackException) { false }
	
	/** Установка страницы [path] с прокруткой [scrolling] */
	fun setArticle(path: String, scrolling: Int) {
		doc?.scroll = scrollY
		doc = Article(path, 0).apply { stackPages[++posStack] = this }
		owner.removeAllViews()
		val link = "$root/${if(path.isBlank()) "index" else path}.html"
		parser.parseFromAssets(context.assets, link, context.resources.getString(R.string.error404, link)) { what, out ->
			when(what) {
				HTML_TEXT_TAG   -> text.append(out)
				else            -> parseTag(what == HTML_OPEN_TAG, out.toString().toLowerCase(Locale.ROOT))
			}
		}
		addEmptyTable()
		owner.scrolling = scrolling
	}
	
	// Выворачивание спанов
	private fun revertSpanned(): Spannable {
		val objs = text.getSpans(0, text.length, ParagraphStyle::class.java)
		for(o in objs) {
			val start = text.getSpanStart(o)
			var end = text.getSpanEnd(o)
			if(end - 2 >= 0) { if(text[end - 1] == '\n' && text[end - 2] == '\n') end-- }
			if(end == start) text.removeSpan(o) else text.setSpan(o, start, end, Spannable.SPAN_PARAGRAPH)
		}
		
		val spans = text.getSpans(0, text.length, Any::class.java)
		val ret = Spannable.Factory.getInstance().newSpannable(text.toString())
		if(spans.isNotEmpty())
			for(s in spans.reversed()) {
				ret.setSpan(s, text.getSpanStart(s), text.getSpanEnd(s), text.getSpanFlags(s))
			}
		text.clearSpans()
		text.clear()
		return ret
	}
	
	// Добавить пустую таблицу
	private fun addEmptyTable() {
		if(text.isNotEmpty()) {
			HtmlTable(context, false, 0, 1, "100", "START").let {
				it.addView(HtmlRow(context).apply { makeTD(1, style, revertSpanned(), null, it) })
				owner.addView(it)
			}
		}
	}
	
	// Создать ячейку в строке таблицы
	private fun makeTD(lst: Boolean) {
		var align = tdAlign
		table?.apply table@ {
			if(isList == lst) {
				row?.apply {
					val spanned = revertSpanned()
					if(lst) {
						if(indexList == 0) text.append("\u25cf") else { text.append("$indexList."); indexList++ }
						makeTD(1, style, revertSpanned(), null, this@table)
						align = null
					}
					makeTD(tdColspan, style, spanned, align, this@table)
				}
			}
		}
	}

	private fun parseImg(): ImageSpan? {
		val src = parser.attrs["src"] ?: error("<img src=???/>")
		val lst = src.split(',')
		val alias = aliases[lst[0]]
		if(alias?.bmp == null) {
			"ImageSpan unknown alias for ${lst[0]}".debug()
			return null
		}
		val bitmap = alias.bmp
		val tile = TileDrawable(context, style_tile).apply { scale = TILE_SCALE_MIN }
		val width: Int
		val height: Int
		var tileNum = 0
		var w = -1
		var h = -1
		repeat(6) {index ->
			val s = if(index < lst.size) lst[index] else ""
			when(index) {
				// Номер тайла
				1 -> tileNum = context.loadResource(s, "integer", alias.tile)
				// Иконка
				2 -> tile.tileIcon = context.loadResource(s, "integer", alias.icon)
				// Относительная ширина
				3 -> w = s.ival(alias.w, 10)
				// Относительная высота
				4 -> h = s.ival(w, 10)
				// Угол
				5 -> tile.angle = s.fval(alias.rot)
			}
		}
		if(w < 0f) {
			width = measuredWidth
			val rel = width.toFloat() / bitmap.width
			height = (bitmap.height * rel).toInt()
		}
		else {
			width = w.dp
			height = h.dp
		}
		tile.setBitmap(alias.key, alias.cols, alias.rows, tileNum)
		tile.setBounds(0, 0, width, height)
		text.append("\uFFFC")
		return ImageSpan(tile, src)
	}

	private fun parseTag(opening: Boolean, tag: String) {
		val len = text.length
		val span: Any? = when(tag) {
			"ul", "ol", "table"     -> {
				table = if(opening) {
					addEmptyTable()
					HtmlTable(context, tag == "ul" || tag == "ol",
					          if(tag == "ol") parser.attrs["start"].ival(1, 10) else 0,
					          parser.attrs["frame"]?.ival(1, 10) ?: 2,
					          parser.attrs["width"] ?: "12,88",
					          parser.attrs["align"] ?: "CENTER_HORIZONTAL,START")
				} else {
					owner.addView(table)
					null
				}
				null
			}
			"li", "tr"              -> {
				if(opening) table?.apply { row = HtmlRow(context); addView(row) } else {
					if(tag == "li") makeTD(true)
					row = null
				}
				null
			}
			"td"                    -> {
				if(opening) {
					tdColspan = parser.attrs["colspan"].ival(1, 10)
					tdAlign = parser.attrs["align"]
				} else {
					makeTD(false)
				}
				null
			}
			"title"                 -> if(opening) Title() else Title::class.java
			"a"                     -> if(opening) Href(parser.attrs["href"] ?: error("<a href=??? />")) else Href::class.java
			"br", "p", "div"        -> { if(opening) paragraph(); null }
			"b", "i"                -> if(opening) Styles(tag == "b") else Styles::class.java
			"font"                  -> {
				if(opening) {
					Font(parser.attrs["size"].fval(0.5f), parser.attrs["style"].cval(0), parser.attrs["color"].cval(0))
				} else Font::class.java
			}
			"sup", "sub"            -> {
				// еще добавить уменьшение шрифта
				if(opening) {
					val size = parser.attrs["style"].fval(0.5f)
					text.setSpan(RelativeSizeSpan(size), len, len, Spannable.SPAN_MARK_MARK)
					Vert(tag == "sub")
				} else {
					end(RelativeSizeSpan::class.java)
					Vert::class.java
				}
			}
			"u"                     -> if(opening) Underline() else Underline::class.java
			"big", "small"          -> {
				if(opening) {
					RelSpan(if(tag == "big") 1.15f else 0.8f, if(tag == "big") large else small)
				} else {
					RelSpan::class.java
				}
			}
			"h1", "h2",
			"h3", "h4",
			"h5", "h6"              -> {
				text.append("\n")
				if(opening) {
					RelSpan(htmlHeaderSize[tag.hashCode() - HTML_TAG_H1], header)
				} else {
					RelSpan::class.java
				}
			}
			"img"                   -> if(opening) parseImg() else null
			else                    -> null
		}
		if(span != null) {
			if(opening) {
				text.setSpan(span, len, text.length, if(span is ImageSpan) Spannable.SPAN_EXCLUSIVE_EXCLUSIVE else Spannable.SPAN_MARK_MARK)
			} else if(span is Class<*>){
				end(span)
			}
		}
		
	}
	
	// Вставка тэга в спан с преобразованием
	private fun end(kind: Class<*>) {
		var end = text.length
		var obj = text.getSpans(0, text.length, kind)?.let { if(it.isEmpty()) null else it[it.size - 1] }
		val start = text.getSpanStart(obj)
		text.removeSpan(obj)
		obj = when(obj) {
			is Title		-> {
				(context as? Wnd)?.findViewById<Text>(android.R.id.title)?.text = text.subSequence(start, end)
				text.delete(start, end)
				null
			}
			is Font         -> {
				when {
					obj.bg != 0  -> BackgroundColorSpan(obj.bg)
					obj.col != 0 -> ForegroundColorSpan(obj.col)
					else         -> RelativeSizeSpan(obj.size)
				}
			}
			is Styles		-> StyleSpan(if(obj.isBold) Typeface.BOLD else Typeface.ITALIC)
			is Underline	-> UnderlineSpan()
			is Vert		    -> if(obj.isSub) SubscriptSpan() else SuperscriptSpan()
			is RelSpan		-> { while(end > start && text[end - 1] == '\n') end--; obj }
			else			-> obj
		}
		if(obj != null) text.setSpan(obj, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
	}

	/**
	 * Класс, реализующий HTML таблицу, используемую в качестве разметки
	 * @property isList     Признак списка
	 * @property indexList  Начальный индекс для нумерованного списка, или 0, в ином случае
	 * @param    columns    Количество колонок
	 * @param    width      Ширина всех колонок в процентах
	 * @param    align      Выравнивание в колонках
	 */
	private class HtmlTable(context: Context, @JvmField val isList: Boolean, @JvmField var indexList: Int,
	                        columns: Int, width: String?, align: String?) : CommonLayout(context, true) {
		
		/** Массив размеров ширины в процентах каждой колонки */
		@JvmField var widths          = intArrayOf(100)
		
		/** Массив выравнивания каждой колонки */
		@JvmField var aligns          = intArrayOf(Gravity.CENTER)
		
		init {
			if(columns > 0) {
				width?.apply { widths = toIntArray(columns, 0, 10, false, ',') }
				align?.apply { aligns = toFlagsArray(columns, mapHtmlArray, Gravity.CENTER, ',', '|') }
				// проверить, чтобы сумма всех значений = 100%
				val sum = widths.sum()
				if(sum != 100) error("The sum of values of width of all columns has to be equal to 100% -> $sum!")
			}
		}
	}
	
	/** Класс, реализующий строку HTML таблицы */
	private class HtmlRow(context: Context): CommonLayout(context, false) {
		
		/**
		 * Создание ячейки таблицы
		 *
		 * @param colspan   Количество колонок, занимаемые ячейкой
		 * @param textStyle Стиль текста
		 * @param spanned   Содержимое ячейки
		 * @param align     Тип выравнивания
		 * @param table     Ссылка на таблицу
		 */
		fun makeTD(colspan: Int, textStyle: IntArray, spanned: Spannable, align: String?, table: HtmlTable) {
			val idx = childCount
			val count = table.widths.size
			if(idx + colspan > count) error("Colspan $colspan columns out of range!")
			Text(context, textStyle).apply {
				addView(this, TableRow.LayoutParams(WRAP, MATCH).apply { span = colspan })
				linksClickable = true
				text = spanned
				gravity = align?.getFlags(mapHtmlArray, Gravity.CENTER, '|') ?: table.aligns[idx]
				var width = 0
				repeat(colspan) { width += table.widths[it + idx] }
				setTag(HTML_KEY_WIDTH, width)
				movementMethod = LinkMovementMethod.getInstance()
			}
		}
		
		/** Вычисление размеров строки таблицы, на основании ширины каждой колонки и типа выравнивания */
		override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec)
			val w = MeasureSpec.getSize(widthMeasureSpec)
			val h = MeasureSpec.getSize(heightMeasureSpec)
			var hMax = 0
			// расчитать ширину и максимальную высоту
			loopChildren {
				(it.layoutParams as? TableRow.LayoutParams)?.apply {
					val childWidthSpec = MeasureSpec.makeMeasureSpec(w.fromPercent(it.tag(HTML_KEY_WIDTH)), MeasureSpec.EXACTLY)
					val childHeightSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.UNSPECIFIED)
					it.measure(childWidthSpec, childHeightSpec)
					hMax = max(hMax, it.measuredHeight)
				}
			}
			// установить высоту ячеек
			loopChildren {
				val childWidthSpec = MeasureSpec.makeMeasureSpec(it.measuredWidth, MeasureSpec.EXACTLY)
				val childHeightSpec = MeasureSpec.makeMeasureSpec(hMax, MeasureSpec.EXACTLY)
				it.measure(childWidthSpec, childHeightSpec)
			}
			setMeasuredDimension(w, hMax)
		}
	}
	
	/** Класс хранения состояния Html в [bundle] */
	private class HtmlState(@JvmField val bundle: Bundle, state: Parcelable?): View.BaseSavedState(state)
	
	/** Сохранение состояния */
	override fun onSaveInstanceState(): Parcelable {
		var i = 1
		val state = Bundle()
		state.putInt("size", posStack)
		stackPages.forEach {
			state.put("ref$i", it.ref)
			state.put("pos$i", it.scroll)
			i++
		}
		state.put("path", doc?.ref ?: "")
		state.put("scroll", scrollY)
		return HtmlState(state, super.onSaveInstanceState())
	}
	
	/** Восстановление состояния */
	override fun onRestoreInstanceState(state: Parcelable?) {
		var st = state
		if(st is HtmlState) {
			val bundle = st.bundle
            posStack = -1
			val size = bundle.getInt("size")
            repeat(size) { stackPages[++posStack] = Article(bundle.getString("ref$it") ?: "", bundle.getInt("pos$it")) }
			setArticle(bundle.getString("path") ?: "", bundle.getInt("scroll"))
			st = st.superState
		}
		super.onRestoreInstanceState(st)
		requestLayout()
	}
}

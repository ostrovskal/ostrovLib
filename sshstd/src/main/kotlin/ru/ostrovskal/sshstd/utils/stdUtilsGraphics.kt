@file:Suppress("DEPRECATION", "NOTHING_TO_INLINE", "UNCHECKED_CAST")

package ru.ostrovskal.sshstd.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.LruCache
import android.view.Gravity
import android.view.View
import android.widget.Toast
import ru.ostrovskal.sshstd.Common
import ru.ostrovskal.sshstd.Size
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.ui.UiComponent
import ru.ostrovskal.sshstd.ui.UiCtx
import java.io.File

/** Кэш картинок */
@JvmField val cacheBitmap       = object : LruCache<String, Bitmap>(config.mem / 8) {
	override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount / 1024
}

/** Кэш для шрифта */
@JvmField val cacheFont   = mutableMapOf<String, Typeface>()

/**
 * Формирование графического объекта Path
 *
 * @param shape Фигура для построения
 * @param r     Ограничивающий прямоугольник
 * @param radii Массив значений углов для скругленного прямоугольника
 */

fun Path.makeFigure(shape: Int, r: RectF, radii: FloatArray?) {
	reset()
	when(shape) {
		Common.TILE_SHAPE_CIRCLE -> addCircle(r.centerX(), r.centerY(), Math.min(r.height(), r.width()) / 2.0f, Path.Direction.CCW)
		Common.TILE_SHAPE_OVAL   -> addOval(r, Path.Direction.CCW)
		Common.TILE_SHAPE_ROUND  -> {
			if(radii == null) addRoundRect(r, r.width() / 4f, r.height() / 4f, Path.Direction.CCW)
			else addRoundRect(r, radii, Path.Direction.CCW)
		}
		Common.TILE_SHAPE_RECT   -> addRect(r, Path.Direction.CCW)
	}
}

/**
 * Копирование вещественного прямоугольника со смещением
 *
 * @param dx  Горизонтальное смещение
 * @param dy  Вертикальное смещение
 * @param dst Ссылка на копируемый прямоугольник
 *
 * @return Возвращает скопированный прямоугольник
 */
fun RectF.offset(dx: Float, dy: Float, dst: RectF) : RectF {
	dst.set(left + dx, top + dy, right + dx, bottom + dy)
	return dst
}

/**
 * Копирование целого прямоугольника со смещением
 *
 * @param dx  Горизонтальное смещение
 * @param dy  Вертикальное смещение
 * @param dst Ссылка на копируемый прямоугольник
 *
 * @return Возвращает скопированный прямоугольник
 */
fun Rect.offset(dx: Int, dy: Int, dst: Rect) : Rect {
	dst.set(left + dx, top + dy, right + dx, bottom + dy)
	return dst
}

/**
 * Сдвинуть прямоугольник на угол
 *
 * @param angle Угол
 * @param rx    Горизонтальный радиус
 * @param ry    Вертикальный радиус
 * @param dst   Ссылка на регультирующий прямоугольник
 */
fun RectF.offsetAngle(angle: Double, rx: Float, ry: Float, dst: RectF): RectF {
	val degree = Math.toRadians(angle)
	val x = (rx * Math.cos(degree)).toFloat()
	val y = (ry * Math.sin(degree)).toFloat()
	return offset(x, y, dst)
}

/**
 * Упаковка текста в прямоугольную область с выравниванием
 *
 * @param canvas    Канва
 * @param text      Текст
 * @param dst       Ограничительня область
 * @param gravity   Тип выравнивания
 *
 * @return          Возвращает высоту текста в пикселях
 */
fun Paint.drawTextInBounds(canvas: Canvas, text: String, dst: RectF, gravity: Int): Float {
	val hl = fontSpacing
	val asc = -fontMetricsInt.ascent
	
	val width = dst.width()
	val buffer = mutableListOf<String>()
	val lenSpc = measureText(" ").toInt()
	val msg = StringBuilder(32)
	var lenMsg = 0
	text.split(' ', '\t', '\n').forEach {
		if(it.isEmpty()) return@forEach
		var lenIt = measureText(it).toInt()
		if((lenIt + lenMsg) >= width)
		{
			buffer += if(lenMsg != 0) msg.trim(' ') else { lenIt = 0; it }
			lenMsg = 0; msg.setLength(0)
		}
		if(lenIt != 0) msg.append(it).append(' ')
		lenMsg += (lenIt + lenSpc)
	}
	if(lenMsg != 0) buffer += msg.trim(' ')
	
	val count = buffer.size
	val hfont = count * hl
	val x = when(gravity and Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) {
		Gravity.START -> dst.left
		Gravity.END   -> dst.right
		else          -> dst.left + dst.width() / 2
	}
	var y = when(gravity and Gravity.VERTICAL_GRAVITY_MASK) {
		        Gravity.TOP    -> dst.top
		        Gravity.BOTTOM -> dst.bottom - hfont
		        else           -> dst.top + (dst.height() - hfont) / 2
	        } + asc
	textAlign = when(gravity and Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) {
		Gravity.START -> Paint.Align.LEFT
		Gravity.END   -> Paint.Align.RIGHT
		else          -> Paint.Align.CENTER
	}
	buffer.forEach {
		canvas.drawText(it, x, y, this)
		y += hl
	}
	return hfont
}

/** Формирование шрифта [name] с использованием кэша */
fun Context.makeFont(name: String): Typeface = cacheFont.getOrElse(name) {
	try {
		Typeface.createFromAsset(assets, "font/$name.ttf").apply {
			cacheFont[name] = this
			"Шрифт <$name - $this> создан!".debug()
		}
	} catch(e: RuntimeException) {
		Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
	}
}

/** Получение ресурсного объекта по его имени [name] и типу [type]. [def] Значение по умолчанию. Применяется, если ресурсный объект не был найден */
@SuppressLint("Recycle")
fun <T> Context.loadResource(name: String, type: String, def: T): T {
	val ret: Any? = try {
		resources.run {
			if(name.isEmpty()) def else {
				val id = getIdentifier(name, if(type == "fraction") "dimen" else type, packageName)
				when(type) {
					"fraction"  -> getFraction(id, 1, 1)
					"integer"   -> getInteger(id)
					"dimen"     -> getDimension(id)
					"drawable"  -> (getDrawable(id) as? BitmapDrawable)?.bitmap
					"text"      -> getText(id)
					"bool"      -> getBoolean(id)
					"string"    -> getString(id)
					"array_str" -> getStringArray(id)
					"array_int" -> getIntArray(id)
					"array"     -> {
						obtainTypedArray(id).recycledRun {
							IntArray(length()) { peekValue(it).run { if(resourceId == 0) data else resourceId } }
						}
					}
					"color"     -> getColor(id)
					else        -> error("Context.getResource() - Неизвестный тип ресурса $type!")
				}
			}
		}
	} catch(e: Resources.NotFoundException) { def }
	return ret as T
}

/**
 * Отображение тоста
 *
 * @param text   Текст тоста
 * @param isLong Время отображения
 * @param parent Родительское представление, относительно которого отображать тост
 * @param ui     Компонент разметки
 * @param offsX  Горизонтальное смещение относительно родительского представления
 * @param offsY  Вертикальное смещение относительно родительского представления
 */

@SuppressLint("ShowToast")
fun Context.toast(text: CharSequence, isLong: Boolean, parent: View?, ui: UiComponent?, offsX: Int, offsY: Int): Toast {
	val delay = if(isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
	val t = if(ui == null) {
		Toast.makeText(this, text, delay)
	} else {
		Toast(this).apply {
			view = ui.createView(UiCtx(this@toast))
			setText(text)
			duration = delay
		}
	}
	if(parent != null) {
		parent.getLocationOnScreen(Common.xyInt)
		t.setGravity(Gravity.TOP, Common.xyInt[0] - parent.width + offsX.dp, Common.xyInt[1] - parent.height + offsY.dp)
	} else {
		t.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 32.dp)
	}
	t.show()
	return t
}

/** Извлечение картинки [key] из кэша */
fun Context.bitmapGetCache(key: String): Bitmap? {
	if(key.isEmpty()) {
		"get cacheBitmap -> key null!".info()
		return null
	}
	var bitmap =  cacheBitmap[key]
	if(bitmap == null) {
		var nkey = key
		// создать из ресурсов/файла
		bitmap = (if(File(nkey).exists()) {
			BitmapFactory.decodeFile(nkey)
		} else {
			if(nkey.startsWith("theme_")) nkey += Theme.name
			loadResource<Bitmap?>(nkey, "drawable", null)
		})?.apply {
			// снова поместить в кэш
			val old = cacheBitmap.size()
			cacheBitmap.put(key, this)
			val new = cacheBitmap.size()
			"get cacheBitmap -> $nkey(${(new - old).mb}) total: ${new.mb}".debug()
		} ?: null.apply { "get cacheBitmap -> Bitmap $key not found!".debug() }
	}
	return bitmap
}

/** Помещение картинки [bmp] в кэш под именем [key] */
fun bitmapSetCache(bmp: Bitmap, key: String) {
	if(key.isNotEmpty()) {
		// проверить в кэше уже есть?
		if(cacheBitmap[key] == null) {
			// поместить в кэш
			val old = cacheBitmap.size()
			cacheBitmap.put(key, bmp)
			val new = cacheBitmap.size()
			"set cacheBitmap -> $key(${(new - old).mb}) total: ${new.mb}".debug()
		}
	}
}

/**
 * Формирование графической отладочной сетки на кастомном виджете
 *
 * @param cols   Количество колонок
 * @param rows   Количество строк
 * @param ww     Ширина
 * @param hh     Высота
 * @param cellW  Ширина ячейки
 * @param cellH  Высота ячейки
 */
fun makeWired(cols: Int, rows: Int, ww: Float, hh: Float, cellW: Float, cellH: Float) = FloatArray(cols * 4 + rows * 4 + 16).apply {
	this[0] = 0.5f; this[1] = 0.5f; this[2] = ww - 0.5f; this[3] = 0.5f
	this[4] = 0.5f; this[5] = 0.5f; this[6] = 0.5f; this[7] = hh - 0.5f
	this[8] = ww - 0.5f; this[9] = 0.5f; this[10] = ww - 0.5f; this[11] = hh - 0.5f
	this[12] = 0.5f; this[13] = hh - 0.5f; this[14] = ww - 0.5f; this[15] = hh - 0.5f
	var idx = 16
	var xx = cellW
	var yy = cellH
	repeat(rows - 1) {
		this[idx + 0] = 0f; this[idx + 1] = yy; this[idx + 2] = ww; this[idx + 3] = yy; idx += 4; yy += cellH
	}
	repeat(cols - 1) {
		this[idx + 0] = xx; this[idx + 1] = 0f; this[idx + 2] = xx; this[idx + 3] = hh; idx += 4; xx += cellW
	}
}

/** Сравнение точки [p] в диапазоне [s] */
inline fun PointF.equals(p: PointF, s: Size) = (Math.abs(x - p.x) <= s.w && Math.abs(y - p.y) <= s.h)

/** Преобразование целой точки в вещественную */
inline fun Point.toFloat(out: PointF): PointF {
	out.set(x.toFloat(), y.toFloat())
	return out
}

/** Упрощенная версия отображения тоста [text] с длинной задержкой */
inline fun Context.longToast(text: CharSequence) = toast(text, true, null, null, 0, 0)

/** Упрощенная версия отображения тоста [text] с короткой задержкой */
inline fun Context.shortToast(text: CharSequence) = toast(text, false, null, null, 0, 0)

/** Преобразование вещественной точки в целую */
inline fun PointF.toInt(out: Point): Point {
	out.set(x.toInt(), y.toInt())
	return out
}

/** Проверить на попадание точки в область */
inline fun RectF.contains(p: PointF) = contains(p.x, p.y)

/** Проверить на попадание точки в область */
inline fun Rect.contains(p: PointF) = contains(p.x.toInt(), p.y.toInt())

/** Преобразование вещественного прямоугольника в целый аналог */
inline fun RectF.toInt(out: Rect): Rect {
	out.set(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
	return out
}

/** Преобразование целого прямоугольника в вещественный аналог */
inline fun Rect.toFloat(out: RectF): RectF {
	out.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
	return out
}

/** Признак прямоугольника на 0 */
inline val Rect.isZero
	get()                       = left == 0 && right == 0 && top == 0 && bottom == 0

/** Получение горизонтального внутреннего отступа */
inline val Drawable.horizontalPadding: Int
	get()                       { getPadding(Common.iRect); return Common.iRect.left + Common.iRect.right }

/** Получение вертикального внутреннего отступа */
inline val Drawable.verticalPadding: Int
	get()                       { getPadding(Common.iRect); return Common.iRect.top + Common.iRect.bottom }

/** Преобразование прямоугольника из пиксельной системы координат в аппаратно независимый формат */
inline val Rect.dp
	@JvmName("dp") get()  = apply { left = left.dp; right = right.dp; top = top.dp; bottom = bottom.dp }

/** Преобразование целых экранных координат в пиксели */
inline val Int.dp
	@JvmName("dp") get()   = Math.round(this * Common.dMetrics.density * config.multiplySW)

/** Преобразование вещественных экранных координат в пиксели */
inline val Float.dp
	@JvmName("dp") get()   = this * Common.dMetrics.density * config.multiplySW

/** Преобразование целого размера шрифта в пиксели */
inline val Int.sp
	@JvmName("sp") get()   = Math.round(this * Common.dMetrics.scaledDensity)

/** Преобразование вещественного размера шрифта в пиксели */
inline val Float.sp
	@JvmName("sp") get()   = this * Common.dMetrics.scaledDensity

/** Преобразование числа в цвет */
inline val Long.color
	@JvmName("color") get()= this.toInt()

/** Преобразование целого числа в цвет */
inline val Int.color
	@JvmName("color") get()= this or 0xff000000.toInt()

/** Преобразование пикселей в экранные координаты */
inline val Int.dp2px
	@JvmName("dp2px") get()= (this / Common.dMetrics.density).toInt()

/** Преобразование пикселей в размер шрифта */
inline val Int.sp2px
	@JvmName("sp2px") get()= (this / Common.dMetrics.scaledDensity).toInt()


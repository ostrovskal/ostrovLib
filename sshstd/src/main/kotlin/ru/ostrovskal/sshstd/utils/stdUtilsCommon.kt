@file:Suppress("NOTHING_TO_INLINE", "DEPRECATION", "UNCHECKED_CAST")

package ru.ostrovskal.sshstd.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.database.Cursor
import android.graphics.Canvas
import android.os.*
import android.text.format.Time
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import ru.ostrovskal.sshstd.*
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.sql.SQL
import java.io.Closeable
import java.io.File
import java.io.IOException
import kotlin.math.roundToInt

/** Конфигурация устройства */
@JvmField var config     = Config(ScreenSize.UNDEF, 0, "", Orientation.UNDEF, false, 0, 0, UiMode.UNDEF,
	night = false,
	rtl = false,
	sw = 0
)

/** Карта HTML флагов */
@JvmField val mapHtmlArray  = mapOf("CENTER" to Gravity.CENTER, "CENTER_VERTICAL" to Gravity.CENTER_VERTICAL,
                                    "CENTER_HORIZONTAL" to Gravity.CENTER_HORIZONTAL, "START" to Gravity.START,
                                    "END" to Gravity.END, "TOP" to Gravity.TOP, "BOTTOM" to Gravity.BOTTOM)

/** Исключение при попытке вызова getter() */
fun noGetter(): Nothing = error("Property does not have a getter")

/** Маршаллинг в парсел */
@SuppressLint("Recycle")
fun Any.marshall(): ByteArray = Parcel.obtain().recycledRun {
	writeToParcel(this, this@marshall)
	marshall()
}

/** Анмаршаллинг парсела */
@SuppressLint("Recycle")
fun Any.unmarshall(parcel: ByteArray) {
	Parcel.obtain().recycled {
		unmarshall(parcel, 0, parcel.size)
		setDataPosition(0)
		writeToFields(this, this@unmarshall)
	}
}

/** Чтение байтового массива из активов [path] */
fun Context.readAssets(path: String) = try { assets.open(path).releaseRun { readBytes() } } catch(e: IOException) { null }

/** Добавление значения [value] по ключу [key] в Bundle, с автоопределением типа */
fun Bundle.put(key: String, value: Any?) {
	when(value) {
		// Scalars
		is Boolean      -> putBoolean(key, value)
		is Byte         -> putByte(key, value)
		is Char         -> putChar(key, value)
		is Double       -> putDouble(key, value)
		is Float        -> putFloat(key, value)
		is Int          -> putInt(key, value)
		is Long         -> putLong(key, value)
		is Short        -> putShort(key, value)
		// References
		is Bundle       -> putBundle(key, value)
		is CharSequence -> putCharSequence(key, value)
		is Parcelable   -> putParcelable(key, value)
		// Scalar arrays
		is BooleanArray -> putBooleanArray(key, value)
		is ByteArray    -> putByteArray(key, value)
		is CharArray    -> putCharArray(key, value)
		is DoubleArray  -> putDoubleArray(key, value)
		is FloatArray   -> putFloatArray(key, value)
		is IntArray     -> putIntArray(key, value)
		is LongArray    -> putLongArray(key, value)
		is ShortArray   -> putShortArray(key, value)
		else            -> error("Неизвестный тип для установки в Bundle($key, $value)")
	}
}

/** Идентификатор касания */
inline val MotionEvent.touchtId		get()	= getPointerId(actionIndex)

/** Преобразование числа в представление Килобайт/Мегабайт */
val Int.mb: String                   get() {
	var postfix = "KB"
	val ret = if(this >= 1024) { postfix = "MB"; this / 1024f } else this.toFloat()
	return String.format("%.2f %s", ret, postfix)
}

/**
 * Отправка сообщения хэндлеру
 *
 * @param recepient Адресат
 * @param act       Действие
 * @param delay     Задержка
 * @param a1        Аргумент 1
 * @param a2        Аргумент 2
 * @param o         Объект
 */
fun Handler.send(recepient: Int = RECEPIENT_WND, act: Int = 0, delay: Long = 0L, a1: Int = 0, a2: Int = 0, o: Any? = null) {
	sendMessageDelayed(Message.obtain().apply { this.recepient = recepient; this.action = act; obj = o; arg1 = a1; arg2 = a2 }, delay)
}

/** Проверка курсора на валидность */
fun Cursor?.valid() : Cursor? {
	if(this != null) {
		if(moveToFirst()) return this
		close()
	}
	return null
}

private var messageNames    = arrayOf<String>()

/** Формирование информации об сообщении хэндлера */
val Message.info: String get() {
	val a = action + 4
	val act = if(a >= messageNames.size) "???<${a - 4}>" else messageNames[a]
	return "${if(recepient == RECEPIENT_FORM) "FORM" else "WND"} -> $act($arg1, $arg2, $obj)"
}

/**
 * Запуск логирования
 *
 * @param context       Контекст
 * @param tag           Тэг лога
 * @param version       Версия БД
 * @param appVersion    Версия программы
 * @param buildConfig   Признак отладки
 * @param msgInfos      Массив имен сообщений хэндлера
 */
fun startLog(context: Context, tag: String, sqlLog: Boolean, version: Int, appVersion: String, buildConfig: Boolean, msgInfos: Array<String>? = null) {
	folderCache = context.cacheDir.path
	folderFiles = context.filesDir.path
	folderData  = context.getDatabasePath(tag).path
	dMetrics    = context.resources.displayMetrics
	namePackage = context.packageName
	config      = context.queryConfiguration()
	logTag      = tag
	isSqlLog    = sqlLog
	isDebug     = buildConfig
	dbVersion   = version
	messageNames= arrayOf("ACT_EMPTY", "ACT_INIT_SURFACE_THREAD", "ACT_BACKPRESSED", "ACT_EXIT") + (msgInfos ?: arrayOf(""))
	
	"--------------------------------------------------------".info()
	"Executed $logTag $appVersion - ${System.currentTimeMillis().datetime}".info()
	config.debug()
	"--------------------------------------------------------".info()
	"".info()
}

/**
 * Создает файл или директорию определенного типа
 *
 * @param folder Дополнительная директория
 * @param type  Тип предопределенной директории
 * @param file  Имя файла
 *
 * @return      Возвращает полученный файл
 */
fun makeDirectories(folder: String, type: Int, file: String? = null): File {
	var dir = when(type) {
		FOLDER_CACHE	-> File(folderCache)
		FOLDER_FILES	-> File(folderFiles)
		FOLDER_DATABASE	-> File(folderData)
		FOLDER_STORAGE	-> if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
			Environment.getExternalStorageDirectory() else error("External storage undefined!")
		else 			-> error("Unknown type file!")
	}
	if(dir != null && type != FOLDER_DATABASE)
	{
		dir = File(dir, folder)
		if(!dir.isDirectory && !dir.mkdirs()) { "It was not succeeded to create the folder ${dir.path}".debug(); dir = null }
		if(file?.isNotEmpty() == true) dir = File(dir, file)
	}
	return dir
}

/** Форматирование времени */
fun fmtTime(v: Long, pattern: String): String {
	val time = Time()
	time.set(v)
	return time.format(pattern)
}

// Чтение полей и упаковка их в парсел
private fun writeToParcel(ret: Parcel, obj: Any): Parcel {
	val clazz = obj.javaClass
	clazz.fields.filter { it.isAnnotationPresent(STORAGE::class.java) }.forEach {
		it.isAccessible = true
		when(val o = it.get(obj)) {
			is Int          -> ret.writeInt(o)
			is Byte         -> ret.writeByte(o)
			is Long         -> ret.writeLong(o)
			is Char         -> ret.writeInt(o.toInt())
			is Short        -> ret.writeInt(o.toInt())
			is Float        -> ret.writeFloat(o)
			is Double       -> ret.writeDouble(o)
			is Boolean      -> ret.writeInt(if(o) 1 else 0)
			is String       -> ret.writeString(o)
			is IntArray     -> ret.writeIntArray(o)
			is ByteArray    -> ret.writeByteArray(o)
			is LongArray    -> ret.writeLongArray(o)
			is CharArray    -> ret.writeCharArray(o)
			is ShortArray   -> { ret.writeInt(o.size); for(i in o) ret.writeInt(i.toInt()) }
			is FloatArray   -> ret.writeFloatArray(o)
			is DoubleArray  -> ret.writeDoubleArray(o)
			is BooleanArray -> ret.writeBooleanArray(o)
			is Array<*>     -> ret.writeStringArray(o as Array<String>?)
			else            -> writeToParcel(ret, o)
		}
	}
	return ret
}

// Извлечение полей из парсела и запись их в объект
private fun writeToFields(ret: Parcel, obj: Any): Any? {
	val clazz = obj.javaClass
	clazz.fields.filter { it.isAnnotationPresent(STORAGE::class.java) }.forEach {
		it.isAccessible = true
		val o: Any? = when(val tmp = it.get(obj)) {
			is Int          -> ret.readInt()
			is Byte         -> ret.readByte()
			is Long         -> ret.readLong()
			is Char         -> ret.readInt().toChar()
			is Short        -> ret.readInt().toShort()
			is Float        -> ret.readFloat()
			is Double       -> ret.readDouble()
			is Boolean      -> ret.readInt() == 1
			is String       -> ret.readString()
			is IntArray     -> ret.createIntArray()
			is ByteArray    -> ret.createByteArray()
			is LongArray    -> ret.createLongArray()
			is CharArray    -> ret.createCharArray()
			is ShortArray   -> ShortArray(ret.readInt()) { ret.readInt().toShort() }
			is FloatArray   -> ret.createFloatArray()
			is DoubleArray  -> ret.createDoubleArray()
			is BooleanArray -> ret.createBooleanArray()
			is Array<*>     -> ret.createStringArray()
			else            -> writeToFields(ret, tmp)
		}
		if(o != null) it.set(obj, o)
	}
	return null
}

/** Выполнение блока кода с авто закрытием */
inline fun <T: Closeable> T.release(block: T.() -> Unit) { block().apply { close() } }

/** Запуск блока кода с авто закрытием */
inline fun <T : Closeable, R> T.releaseRun(block: T.() -> R): R { return block().apply { close() } }

/** Выполнение блока кода с auto recycle */
inline fun TypedArray.recycled(block: TypedArray.() -> Unit) { block().apply { recycle() } }

/** Запуск блока кода с auto recycle */
inline fun <R> TypedArray.recycledRun(block: TypedArray.() -> R): R { return block().apply { recycle() } }

/** Выполнение блока кода с auto recycle */
inline fun Parcel.recycled(block: Parcel.() -> Unit) { block().apply { recycle() } }

/** Запуск блока кода с auto recycle */
inline fun <R> Parcel.recycledRun(block: Parcel.() -> R): R { return block().apply { recycle() } }

/** Выполнение блока кода с предварительным сохранением состояния канвы и восстановлением ее, при выходе */
inline fun Canvas.withSave(block: Canvas.()->Unit) { save(); block(); restore() }

/** Возвращает величину диапазона */
inline val IntRange.interval get() = (endInclusive - start)// + 1

/** Обрезать значение [v] по диапазону */
inline fun IntRange.clamp(v: Int) = if(v < start) start else if(v > endInclusive) endInclusive else v

/** Горизонтальное направление */
inline val Int.dirHorz      get() = (((this and DIRH) shr 2) - 3)

/** Вертикальное направление */
inline val Int.dirVert      get() = ((this and DIRV) - 3)

/** Поменять направление на 90 градусов */
inline val Int.dirRot90     get() = if(this test DIRH) this shr 2 else this shl 2

/** Возвращает дату и время */
inline val Long.datetime    get() = fmtTime(this, "%d.%m.%Y %H:%M:%S")

/** Возвращает время */
inline val Long.time        get() = fmtTime(this, "%H:%M:%S")

/** Возвращает миллисекунды */
inline val Long.millis      get() = this % 1000//fmtTime(this, "%f %c %F %D %z %SS")

/** Возвращает дату */
inline val Long.date        get() = fmtTime(this, "%d.%m.%Y")

/** Установка/Получение адресата из сообщения хэндлера */
inline var Message.recepient
	get()                   = what and 1
	set(v)                  { what = (what and -1) or (v and 1) }

/** Установка/Получение действия из сообщения хэндлера */
inline var Message.action
	get()                   = what shr 1
	set(v)                  { what = (what and 1) or (v shl 1) }

/** Проверка на бит [bit] */
inline infix fun Int.bits(bit: Int) = (this and ( 1 shl bit ) ) != 0

/** Побитовая проверка на [f] */
inline infix fun Int.test(f: Int) = (this and f) != 0

/** Побитовая проверка на не [f] */
inline infix fun Int.ntest(f: Int) = (this and f) == 0

/** Получение процента от значения [value] */
inline fun Int.toPercent(value: Int) = (this / value.toFloat() * 100.0f).roundToInt()

/** Преобразование процентов [percent] в значение */
inline fun Int.fromPercent(percent: Int) = (percent / 100.0f * this).toInt()

/** Вывод отладочной информации об объекте в лог */
inline fun <T> T.debug() { if(isDebug) Log.d(logTag, toString()) }

/** Вывод информации о объекте в лог */
inline fun <T> T.info() = Log.i(logTag, toString())

/** Запуск блока кода с активацией транзакции */
fun transaction(exclusive: Boolean = false, body: SQL.() ->Unit) {
	SQL.db?.apply {
		if(exclusive) {
			beginTransactionNonExclusive()
		} else {
			beginTransaction()
		}
		try {
			SQL.body()
			setTransactionSuccessful()
		} finally {
			endTransaction()
		}
	}
}

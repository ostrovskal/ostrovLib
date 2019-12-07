@file:Suppress("NOTHING_TO_INLINE")

package ru.ostrovskal.sshstd

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import ru.ostrovskal.sshstd.utils.mb
import java.util.*

/**
 * @author Шаталов С.В.
 * @since 0.0.2
 */

/** Перечисление, описывающее стандартные размеры экрана */
enum class ScreenSize {
	/** Маленький */
	SMALL,
	/** Нормальный */
	NORMAL,
	/** Большой */
	LARGE,
	/** Сверхбольшой */
	XLARGE,
	/** Неопределенный */
	UNDEF
}

/** Перечисление, описывающее стандартные режимы UI */
enum class UiMode {
	/** Смартфон|Планшет */
	NORMAL,
	/** Автомобиль */
	CAR,
	/** Компьютер */
	DESK,
	/** Телевизор */
	TELEVISION,
	/** Без дисплея (например, IoT) */
	APPLIANCE,
	/** Часы */
	WATCH,
	/** Неопределено */
	UNDEF
}

/** Перечисление, описывающее стандартные положения устройства */
enum class Orientation {
	/** Портретная ориентация */
	PORTRAIT,
	/** Ландшафтная ориентация */
	LANDSCAPE,
	/** Неопределено */
	UNDEF
}

/**
 * Класс, реализующий параметры конфигурации устройства
 *
 * @property screen         Тип размера экрана
 * @property dip            Плотность экрана
 * @property lang           Язык по умолчанию
 * @property orien          Ориентация устройства
 * @property long           Длинный режим
 * @property sdk            Версия SDK
 * @property mem            Максимальный объем памяти, выделяемый приложению
 * @property ui             Режим интерфейса
 * @property night          Ночной режим
 * @property rtl            Направление отображения текста
 * @property sw             Минимальная ширина экрана
 */
class Config(@JvmField val screen: ScreenSize, @JvmField val dip: Int, @JvmField val lang: String, @JvmField val orien: Orientation,
             @JvmField val long: Boolean, @JvmField val sdk: Int, @JvmField val mem: Int, @JvmField val ui: UiMode,
             @JvmField val night: Boolean, @JvmField val rtl: Boolean, @JvmField val sw: Int) {
	
	/** Отношение стандартного размера экрана к текущему */
	@JvmField var multiplySW                          		= sw / 320f
	
	/** Проверка на ориентацию устройства */
	inline val portrait                                    get() = orien == Orientation.PORTRAIT
	
	/** Выполнить проверку на тип размера экрана */
	inline fun checkScreen(size: ScreenSize)       = screen == size
	
	/** Выполнить проверку на диапазон плотности */
	inline fun checkDensity(dp: ClosedRange<Int>)  = (dip >= dp.start && dip < dp.endInclusive)
	
	/** Выполнить проверку на язык */
	fun checkLanguage(lng: String)         = if(lng.indexOf('_') >= 0) lng == lang else lang.substring(lang.indexOf('_') + 1) == lng
	
	/** Выполнить проверку на минимальную версию SDK */
	inline fun checkFromSDK(sdk: Int)              = this.sdk >= sdk
	
	/** Выполнить проверку на текущую версию SDK */
	inline fun checkSDK(sdk: Int)                  = this.sdk == sdk
	
	/** Выполнить проверку на режим интерфейса */
	inline fun checkUiMode(mode: UiMode)           = ui == mode
	
	/** Выполнить проверку на минимальную ширину экрана */
	inline fun checkSW(sw: Int)                    = this.sw >= sw
	
	/** Выполнить проверку на диапазон доступной памяти */
	inline fun checkMem(mem: ClosedRange<Int>)     = this.mem in mem
	
	/** Строковое представление объекта */
	override fun toString(): String {
		return "Config(screen=$screen, dip=$dip, lang=$lang, orien=$orien, long=$long, sdk=$sdk, mem=${mem.mb}, ui=$ui, night=$night, rtl=$rtl, sw=$sw)"
	}
}

/** Запрос конфигурации системы */
fun Context.queryConfiguration(): Config {
	val cfg = resources.configuration
	
	val screen = when (cfg.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
		Configuration.SCREENLAYOUT_SIZE_SMALL  -> ScreenSize.SMALL
		Configuration.SCREENLAYOUT_SIZE_NORMAL -> ScreenSize.NORMAL
		Configuration.SCREENLAYOUT_SIZE_LARGE  -> ScreenSize.LARGE
		Configuration.SCREENLAYOUT_SIZE_XLARGE -> ScreenSize.XLARGE
		else                                   -> ScreenSize.UNDEF
	}
	
	val dip = resources.displayMetrics.densityDpi
	val lang = Locale.getDefault().toString()
	
	val orien = when (cfg.orientation) {
		Configuration.ORIENTATION_LANDSCAPE -> Orientation.LANDSCAPE
		Configuration.ORIENTATION_PORTRAIT  -> Orientation.PORTRAIT
		else                                -> Orientation.UNDEF
	}
	
	val long = (cfg.screenLayout and Configuration.SCREENLAYOUT_LONG_MASK) == Configuration.SCREENLAYOUT_LONG_YES
	
	val sdk = Build.VERSION.SDK_INT
	
	val ui = when(cfg.uiMode and Configuration.UI_MODE_TYPE_MASK) {
		Configuration.UI_MODE_TYPE_NORMAL     -> UiMode.NORMAL
		Configuration.UI_MODE_TYPE_DESK       -> UiMode.DESK
		Configuration.UI_MODE_TYPE_CAR        -> UiMode.CAR
		Configuration.UI_MODE_TYPE_TELEVISION -> UiMode.TELEVISION
		Configuration.UI_MODE_TYPE_APPLIANCE  -> UiMode.APPLIANCE
		Configuration.UI_MODE_TYPE_WATCH      -> UiMode.WATCH
		else                                  -> UiMode.UNDEF
	}
	
	val rtl = (cfg.screenLayout and Configuration.SCREENLAYOUT_LAYOUTDIR_MASK) == Configuration.SCREENLAYOUT_LAYOUTDIR_RTL
	
	val sw = cfg.smallestScreenWidthDp
	
	val night = (getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager)?.nightMode == UiModeManager.MODE_NIGHT_YES
	
	val mem = (Runtime.getRuntime().maxMemory() / 1024).toInt()
	
	return Config(screen, dip, lang, orien, long, sdk, mem, ui, night, rtl, sw)
}

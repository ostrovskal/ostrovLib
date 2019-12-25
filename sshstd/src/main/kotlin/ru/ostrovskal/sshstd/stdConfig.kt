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

/** Объект, реализующий параметры конфигурации устройства */
object Config {

	/** Ориентация устройства */
	@JvmField var orientation	= Orientation.UNDEF

	/** Тип размера экрана */
	@JvmField var screen		= ScreenSize.UNDEF

	/** Режим интерфейса */
	@JvmField var ui			= UiMode.UNDEF

	/** Максимальный объем памяти, выделяемый приложению */
	@JvmField var maxMem		= 0

	/** Версия SDK */
	@JvmField var sdk			= 0

	/** Плотность экрана */
	@JvmField var dip			= 0

	/** Ширина экрана */
	@JvmField var screenWidth	= 0

	/** Высота экрана */
	@JvmField var screenHeight	= 0

	/** Минимальная ширина экрана */
	@JvmField var smallWidth	= 0

	/** Язык по умолчанию */
	@JvmField var language		= ""

	/** Регион по умолчанию */
	@JvmField var region		= ""

	/** Длинный режим */
	@JvmField var isLong		= false

	/** Ночной режим */
	@JvmField var isNight		= false

	/** Направление отображения текста */
	@JvmField var isRtl			= false

	/** Признак портретной ориентации */
	@JvmField var isPortrait	= false

	/** Плотность экрана в точных единицах */
	@JvmField var density		= 0f

	/** Плотность масштабирования шрифтов */
	@JvmField var scaledDensity	= 0f

	/** Отношение стандартного размера экрана к текущему */
	val multiplySW get() = smallWidth / 320f

	/** Запрос конфигурации системы */
	fun query(context: Context) {
		val cfg = context.resources.configuration
		val dMetrics = context.resources.displayMetrics

		screen = when (cfg.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
			Configuration.SCREENLAYOUT_SIZE_SMALL  -> ScreenSize.SMALL
			Configuration.SCREENLAYOUT_SIZE_NORMAL -> ScreenSize.NORMAL
			Configuration.SCREENLAYOUT_SIZE_LARGE  -> ScreenSize.LARGE
			Configuration.SCREENLAYOUT_SIZE_XLARGE -> ScreenSize.XLARGE
			else                                   -> ScreenSize.UNDEF
		}
		ui = when(cfg.uiMode and Configuration.UI_MODE_TYPE_MASK) {
			Configuration.UI_MODE_TYPE_NORMAL     -> UiMode.NORMAL
			Configuration.UI_MODE_TYPE_DESK       -> UiMode.DESK
			Configuration.UI_MODE_TYPE_CAR        -> UiMode.CAR
			Configuration.UI_MODE_TYPE_TELEVISION -> UiMode.TELEVISION
			Configuration.UI_MODE_TYPE_APPLIANCE  -> UiMode.APPLIANCE
			Configuration.UI_MODE_TYPE_WATCH      -> UiMode.WATCH
			else                                  -> UiMode.UNDEF
		}
		orientation = when (cfg.orientation) {
			Configuration.ORIENTATION_LANDSCAPE -> Orientation.LANDSCAPE
			Configuration.ORIENTATION_PORTRAIT  -> Orientation.PORTRAIT
			else                                -> Orientation.UNDEF
		}

		Locale.getDefault().toString().apply {
			language = substringBefore('_')
			region = substringAfter('_')
		}

		sdk = Build.VERSION.SDK_INT
		smallWidth = cfg.smallestScreenWidthDp
		maxMem = (Runtime.getRuntime().maxMemory() / 1024).toInt()

		dip = dMetrics.densityDpi
		density = dMetrics.density
		scaledDensity = dMetrics.scaledDensity
		screenHeight = dMetrics.heightPixels
		screenWidth = dMetrics.widthPixels

		isLong = (cfg.screenLayout and Configuration.SCREENLAYOUT_LONG_MASK) == Configuration.SCREENLAYOUT_LONG_YES
		isRtl = (cfg.screenLayout and Configuration.SCREENLAYOUT_LAYOUTDIR_MASK) == Configuration.SCREENLAYOUT_LAYOUTDIR_RTL
		isNight = (context.getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager)?.nightMode == UiModeManager.MODE_NIGHT_YES

		isPortrait = orientation == Orientation.PORTRAIT
	}

	/** Выполнить проверку на тип размера экрана */
	inline fun checkScreen(size: ScreenSize)       = screen == size
	
	/** Выполнить проверку на диапазон плотности */
	inline fun checkDensity(dp: ClosedRange<Int>)  = (dip >= dp.start && dip < dp.endInclusive)
	
	/** Выполнить проверку на минимальную версию SDK */
	inline fun checkMinSDK(sdk: Int)               = this.sdk >= sdk
	
	/** Выполнить проверку на текущую версию SDK */
	inline fun checkSDK(sdk: Int)                  = this.sdk == sdk
	
	/** Выполнить проверку на режим интерфейса */
	inline fun checkUiMode(mode: UiMode)           = ui == mode
	
	/** Выполнить проверку на минимальную ширину экрана */
	inline fun checkSW(sw: Int)                    = smallWidth >= sw
	
	/** Выполнить проверку на диапазон доступной памяти */
	inline fun checkMem(mem: ClosedRange<Int>)     = maxMem in mem
	
	/** Строковое представление объекта */
	override fun toString(): String {
		return "Config(screen=$screen, dip=$dip, lang=$language, region=$region, orien=$orientation, long=$isLong, sdk=$sdk, mem=${maxMem.mb}," +
				" ui=$ui, night=$isNight, rtl=$isRtl, sw=$smallWidth, isPortrait=$isPortrait, width=$screenWidth, height=$screenHeight)"
	}
}

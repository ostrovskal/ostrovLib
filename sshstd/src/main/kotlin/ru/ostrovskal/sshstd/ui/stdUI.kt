package ru.ostrovskal.sshstd.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.Gravity
import android.view.View
import android.view.View.TEXT_ALIGNMENT_GRAVITY
import android.view.ViewGroup
import android.view.ViewManager
import com.github.ostrovskal.sshstd.R
import ru.ostrovskal.sshstd.Common.DIRDR
import ru.ostrovskal.sshstd.Common.TILE_SHAPE_ROUND
import ru.ostrovskal.sshstd.layouts.CELL_LAYOUT_INSERT_BEGIN
import ru.ostrovskal.sshstd.layouts.CellLayout
import ru.ostrovskal.sshstd.objects.*
import ru.ostrovskal.sshstd.utils.*

/**
 * @author Шаталов С. В.
 * @since 0.1.0
*/

/**
* Класс, реализующий контекст UI
*
* @constructor Создает контекст пользовательского динамического интерфейса
* @property ctx    Контекст
* @property root   Корневое представление
*/
open class UiCtx(@JvmField val ctx: Context, private var root: View? = null) : ViewManager {
	
	/** Корневой элемент разметки */
	val view: View get() = root ?: error("View hasn't been established!")
	
	/** Обновление параметров разметки [params] для представления [view] */
	override fun updateViewLayout(view: View, params: ViewGroup.LayoutParams) {
		(root as? ViewGroup)?.apply {
			findViewById<View>(view.id)?.layoutParams = params
		}
	}
	
	/** Удаление представления [view] из корневой разметки */
	override fun removeView(view: View) { (root as? ViewGroup)?.removeView(view) }
	
	/** Добавление корневого представления в контекст UI */
	override fun addView(view: View, params: ViewGroup.LayoutParams?) {
		if(root != null) error("The root element ($root) is already exist!")
		root = view
	}
}

/** Базовый интерфейс компонента UI */
abstract class UiComponent {
	/** Создать компонент */
	abstract fun createView(ui: UiCtx): View

	/**
	 * Формирование картинки из разметки
	 *
	 * @param context   Контекст
	 * @param key       Имя картинки для помещения в кэш
	 * @param width     Ширина картинки
	 * @param height    Высота картинки
	 */
	fun makeBitmap(context: Context, key: String, width: Int, height: Int): Bitmap {
		var bitmap = cacheBitmap[key]
		if(bitmap == null) {
			val view = createView(UiCtx(context))
			bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
			val w = width - 4
			val h = height - 4
			view.measure(View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY))
			view.layout(0, 0, w, h)
			Canvas(bitmap).apply {
				translate(2f, 2f)
				view.draw(this)
			}
			if(key.isNotEmpty()) bitmapSetCache(bitmap, key)
		}
		return bitmap
	}
}

/**
 * Установка футера формы. Максимальное количество кнопок - три. Они автоматически центрируются по горизонтали родительской разметки
 * @param btn Кнопки футера в формате (ID кнопки, ID иконки)
 */
fun ViewManager.formFooter(vararg btn: Int) {
	val coord = when(btn.size) {
		2   -> intArrayOf(9, 15)
		4   -> intArrayOf(1, 15, 17, 15)
		6   -> intArrayOf(2, 9, 12, 9, 22, 9)
		else-> error("The footer of a form can have one, two or three buttons!")
	}
	cellLayout(33, 2) {
		lps(0, -200, -1, 2)
		for(bt in 0 until btn.size step 2) {
			button(style_footer) {
				id = btn[bt]
				iconResource = btn[bt + 1]
			}.lps(coord[bt], 0, coord[bt + 1], 2)
		}
	}
}

/** Класс, реализующий элемент выпадающего списка спиннера */
class SpinnerItem : UiComponent() {
	override fun createView(ui: UiCtx) = ui.run { text(R.string.null_text, style_spinner_item) { backgroundSet(style_spinner_item) } }
}

/** Класс, реализующий заголовок спиннера */
class SpinnerPopup : UiComponent() {
	override fun createView(ui: UiCtx) = ui.run { text(R.string.null_text, style_spinner_title) }
}

/** Заголовок формы */
fun ViewManager.formHeader(title: Int) {
	if(title != 0) {
		text(title, style_text_header) {
			id = android.R.id.title
			backgroundSet(style_text_header)
			layoutParams = CellLayout.LayoutParams(0, CELL_LAYOUT_INSERT_BEGIN, -1, 2)
		}
	}
}

/** Класс, реализующий разметку для отображения тоста */
class ToastLayout: UiComponent() {
	override fun createView(ui: UiCtx): View = with(ui) {
		text(R.string.null_text) {
			id = android.R.id.message
			textSize = Theme.dimen(context, 18, true).toFloat()
			gravity = Gravity.CENTER
			textAlignment = TEXT_ALIGNMENT_GRAVITY
			horizontalPadding = Theme.dimen(context, 16)
			verticalPadding = Theme.dimen(context, 8)
			backgroundSet(intArrayOf()) {
				radii = floatArrayOf(20f, 10f, 20f, 10f, 20f, 10f, 20f, 10f)
				shape = TILE_SHAPE_ROUND
				selectorWidth = 2f.dp
				selectorColor = 0xaf000000.color
				isShowBackground = true
				gradientDir = DIRDR
				gradient = intArrayOf(0xaf6f6f6f.color, 0xaf404040.color)
			}
		}
	}
}

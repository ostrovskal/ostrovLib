package ru.ostrovskal.ostrovlib

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.ArrayAdapter
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.Wnd
import ru.ostrovskal.sshstd.adapters.ArrayListAdapter
import ru.ostrovskal.sshstd.layouts.CellLayout
import ru.ostrovskal.sshstd.layouts.RadioLayout
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.ui.*
import ru.ostrovskal.sshstd.utils.*
import ru.ostrovskal.sshstd.widgets.lists.Ribbon

const val actDblClick	= 0
const val actClick		= 1
const val actDirect		= 2
const val actRotate		= 3
const val actScale		= 4
const val actDrag		= 5

lateinit var touchGrp: RadioLayout

inline fun ViewManager.touchSurface(init: SurfaceTouch.() -> Unit) = uiView( { SurfaceTouch(it) }, init)

val std_theme_d = intArrayOf(ATTR_SSH_THEME_NAME, R.string.std_theme_d,
                           ATTR_SSH_SEEK_ANIM, SEEK_ANIM_ROTATE,
                           ATTR_SSH_COLOR_LAYOUT, 0x2d2929 or COLOR,
                           ATTR_SSH_COLOR_NORMAL, 0x9599f7 or COLOR,
                           ATTR_SSH_COLOR_LARGE, 0xbc5a1d or COLOR,
                           ATTR_SSH_COLOR_SMALL, 0x2ea362 or COLOR,
                           ATTR_SSH_COLOR_HINT, 0xf77499 or COLOR,
                           ATTR_SSH_COLOR_SELECTOR, 0xa000a0 or COLOR,
                           ATTR_SSH_COLOR_DIVIDER, 0x7a7a7a or COLOR,
                           ATTR_SSH_COLOR_HEADER, 0xcfba41 or COLOR,
                           ATTR_SSH_COLOR_HTML_HEADER, 0xf22782 or COLOR,
                           ATTR_SSH_COLOR_LINK, 0xb8fa01 or COLOR,
                           ATTR_SSH_COLOR_MESSAGE, 0xd2fa64 or COLOR,
                           ATTR_SSH_COLOR_WINDOW, 0x030303 or COLOR,
                           ATTR_SSH_COLOR_WIRED, 0x808080 or COLOR,
                           ATTR_SSH_BM_MENU, R.drawable.menu_common,
                           ATTR_SSH_BM_ICONS, R.drawable.icon_tiles,
                           ATTR_SSH_BM_TILES, R.drawable.sprites,
                           ATTR_SSH_BM_BACKGROUND, R.drawable.background,
                           ATTR_SSH_BM_HEADER, R.drawable.theme_header_dark,
                           ATTR_SSH_BM_SPINNER, R.drawable.theme_spinner_dark,
                           ATTR_SSH_BM_EDIT, R.drawable.theme_edit_dark,
                           ATTR_SSH_BM_TOOLS, R.drawable.theme_tool_dark,
                           ATTR_SSH_BM_BUTTONS, R.drawable.theme_button_dark,
                           ATTR_SSH_BM_RADIO, R.drawable.theme_radio_dark,
                           ATTR_SSH_BM_CHECK, R.drawable.theme_check_dark,
                           ATTR_SSH_BM_SEEK, R.drawable.theme_seek_dark,
                           ATTR_SSH_BM_SWITCH, R.drawable.theme_switch_dark)

val std_theme_l = intArrayOf(ATTR_SSH_THEME_NAME, R.string.std_theme_l,
                             ATTR_SSH_SEEK_ANIM, SEEK_ANIM_SCALE,
                             ATTR_SSH_COLOR_LAYOUT, 0x9e5e1e or COLOR,
                             ATTR_SSH_COLOR_NORMAL, 0xea5191 or COLOR,
                             ATTR_SSH_COLOR_LARGE, 0x5670f1 or COLOR,
                             ATTR_SSH_COLOR_SMALL, 0x9841df or COLOR,
                             ATTR_SSH_COLOR_HINT, 0xcca6fc or COLOR,
                             ATTR_SSH_COLOR_SELECTOR, 0xa0a000 or COLOR,
                             ATTR_SSH_COLOR_DIVIDER, 0x7f7f7f or COLOR,
                             ATTR_SSH_COLOR_HEADER, 0xa9f145 or COLOR,
                             ATTR_SSH_COLOR_HTML_HEADER, 0x9a4dfc or COLOR,
                             ATTR_SSH_COLOR_LINK, 0xa0a0a0 or COLOR,
                             ATTR_SSH_COLOR_MESSAGE, 0xd2fa64 or COLOR,
                             ATTR_SSH_COLOR_WINDOW, 0x020202 or COLOR,
                             ATTR_SSH_COLOR_WIRED, 0x404040 or COLOR,
                             ATTR_SSH_BM_MENU, R.drawable.menu_common,
                           ATTR_SSH_BM_ICONS, R.drawable.icon_tiles,
                           ATTR_SSH_BM_TILES, R.drawable.sprites,
                           ATTR_SSH_BM_BACKGROUND, R.drawable.background,
                           ATTR_SSH_BM_HEADER, R.drawable.theme_header_light,
                           ATTR_SSH_BM_SPINNER, R.drawable.theme_spinner_light,
                           ATTR_SSH_BM_EDIT, R.drawable.theme_edit_light,
                           ATTR_SSH_BM_TOOLS, R.drawable.theme_tool_light,
                           ATTR_SSH_BM_BUTTONS, R.drawable.theme_button_light,
                           ATTR_SSH_BM_RADIO, R.drawable.theme_radio_light,
                           ATTR_SSH_BM_CHECK, R.drawable.theme_check_light,
                           ATTR_SSH_BM_SEEK, R.drawable.theme_seek_light,
                           ATTR_SSH_BM_SWITCH, R.drawable.theme_switch_light
)

class MainWnd : Wnd() {

	var typeTheme = 1
	// ДПО4 ПОЛУЧЕНО
	// 89857707575
	override fun onCreate(savedInstanceState: Bundle?) {
		startLog(this, "LIB", true, BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME, BuildConfig.DEBUG, null)
		super.onCreate(savedInstanceState)
/*
		thread {
			val dbx = DropBox("dbxSerg", "8iL3GSZ-JygAAAAAAAAHlIMEO_3cUJi2zLr1pR5tI8NCshh6KZ225aSqcNKLK-Wt")
			dbx.list("/ZX")?.apply {
				forEach {
					"${it.name} ${it.path} ${it.rev}".info()
				}
			}
		}
*/
		TestTouch(this).setContent(this, SSH_APP_MODE_GAME)
	}
	
	override fun applyTheme() {
		Theme.setTheme(this, if(typeTheme == 0) std_theme_l else std_theme_d)
	}
	
	override fun initialize(restart: Boolean) {
		"initialize restart: $restart hand: $hand".info()
		isRestart = false
		if(hand == null) {
			hand = Handler(Looper.getMainLooper(), this)
			applyTheme()
		}
	}
}

class TestTouch(val wnd: MainWnd): UiComponent() {
	override fun createView(ui: UiCtx) = with(ui) {
		linearLayout(false) {
			containerLayout(70, 100, true) {
				touchSurface { id = R.id.check }
			}
			touchGrp = radioGroup {
				radio(actDblClick, R.string.radio_dclick)
				radio(actClick, R.string.radio_click)
				radio(actDirect, R.string.radio_direct)
				radio(actDrag, R.string.radio_drag)
				radio(actRotate, R.string.radio_rotate)
				radio(actScale, R.string.radio_scale)
			}
		}
	}
}

class Abs(val wnd: MainWnd): UiComponent() {
	override fun createView(ui: UiCtx) = with(ui) {
		cellLayout(12, 10) {
			ribbon(0, false) {
				adapter = WndAdapter(context, ItemRibbon())
				backgroundSet {
					solid = 0x4000ff00
				}
			}.lps(0, 0, 6, 5)
		}
	}
	private class WndAdapter(context: Context, val item: UiComponent) : ArrayAdapter<Ribbon>(context, 0, listOf()) {

		/** Возвращает представление заголовка */
		override fun getView(position: Int, convertView: View?, parent: ViewGroup) = createView(position, convertView)

		override fun getCount(): Int = 3

		/** Возвращает представление из выпадающего списка */
		override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup) = createView(position, convertView)

		/** Создает представление */
		private fun createView(position: Int, convertView: View?): View? {
			return ((convertView ?: item.createView(UiCtx(context))) as? Ribbon)?.apply {
				var lst = listOf("nothing")
				when(position) {
					0		-> lst = listOf("1", "2", "3", "4", "5", "6", "7")
					1		-> lst = listOf("11", "21", "31", "41")
					2		-> lst = listOf("121", "122", "123", "124", "125", "126", "127")
				}
				adapter = ArrayListAdapter(context, ItemIO(), ItemIO(), lst)
			}
		}
	}

	private class ItemRibbon : UiComponent() {
		override fun createView(ui: UiCtx) = ui.run {
			ribbon(0, true) {
				padding = 16.dp
				layoutParams = CellLayout.LayoutParams(1, 1, 4, 4)
				backgroundSet {
					solid = 0x80202020.toInt()
				}
			}
		}
	}

	/** Класс, реализующий элемент списка файлов для загрузки */
	private class ItemIO : UiComponent() {
		override fun createView(ui: UiCtx) = ui.run { check(0, R.string.null_text) }
	}
}

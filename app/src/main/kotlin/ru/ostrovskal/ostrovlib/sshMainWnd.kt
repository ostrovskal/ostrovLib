@file:Suppress("DEPRECATION", "IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")

package ru.ostrovskal.ostrovlib

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.DropBox
import ru.ostrovskal.sshstd.Size
import ru.ostrovskal.sshstd.Wnd
import ru.ostrovskal.sshstd.adapters.ArrayListAdapter
import ru.ostrovskal.sshstd.forms.FormMessage
import ru.ostrovskal.sshstd.forms.FormProgress
import ru.ostrovskal.sshstd.layouts.CellLayout
import ru.ostrovskal.sshstd.layouts.RadioLayout
import ru.ostrovskal.sshstd.objects.Theme
import ru.ostrovskal.sshstd.sql.SQL
import ru.ostrovskal.sshstd.sql.Table
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
                           ATTR_SSH_BM_SWITCH, R.drawable.theme_switch_dark,
							ATTR_SSH_ICON_HORZ, 10, ATTR_SSH_ICON_VERT, 3
)

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
                           ATTR_SSH_BM_SWITCH, R.drawable.theme_switch_light,
		ATTR_SSH_ICON_HORZ, 10, ATTR_SSH_ICON_VERT, 3
)

object Ostrov: Table() {
	@JvmField val id  = integer("_id").primaryKey(1)
	@JvmField val time= timestamp("time").notNull.index(true)
	@JvmField val text= text("text").notNull.index(false)
	@JvmField val real= real("real").default(0f).checked { it gteq 0f }.index(true)
}

class MainWnd : Wnd() {

	var typeTheme = 1

	// ДПО4 ПОЛУЧЕНО
	// 89857707575
    override fun onCreate(savedInstanceState: Bundle?) {

		startLog(this, "LIB", true, BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME, BuildConfig.DEBUG, null)
		super.onCreate(savedInstanceState)

        TestTouch(this).setContent(this, SSH_APP_MODE_GAME)

		val dbx = DropBox("zx", getString(R.string.dropbox_token))

		launch {
			FormProgress().show(this@MainWnd, R.string.loading, true).doInBackground(10) { fp ->
				val result = withContext(Dispatchers.IO) { dbx.folders("/ZX") }
				result?.run {
					fp.maximum = this.size
					forEachIndexed { idx, f ->
						f.name.info()
						delay(10L)
						fp.primary = idx
					}
					BTN_OK
				} ?: BTN_NO
			}
		}
	}

	override fun handleMessage(msg: Message): Boolean {
		if(msg.action == 10) {
			FormMessage().show(this, intArrayOf(R.string.app_name, if(msg.arg1 == BTN_OK) R.string.success else R.string.failed, R.integer.I_YES, 0, 0, 0, 0))
		}
		return super.handleMessage(msg)
	}
	override fun applyTheme() {
		Theme.setTheme(this, if(typeTheme == 0) std_theme_l else std_theme_d)
	}
	
	override fun initialize(restart: Boolean) {
		"initialize restart: $restart hand: $hand".debug()
		isRestart = false
		if(hand == null) {
			hand = Handler(Looper.getMainLooper(), this)
			applyTheme()
		}
		if(!SQL.connection(this, true, Ostrov)) {
			repeat(10) {idx ->
				Ostrov.insert {
					values[Ostrov.time] = System.currentTimeMillis()
					values[Ostrov.text] = idx.toString()
					values[Ostrov.real] = idx * 2f
				}
			}
		}
		launch {
			Ostrov.exist { Ostrov.real gt 10f }.info()
			Ostrov.count { Ostrov.real ls 14f }.info()
			Ostrov.listOf(Ostrov.text, Ostrov.time, false) { Ostrov.real ls 14f }.info()
			Ostrov.select(Ostrov.text, Ostrov.real, Ostrov.time).execute {
				forEach {
					"${it.text(Ostrov.text)} - ${it.real(Ostrov.real)} - ${it.time(Ostrov.time)}".info()
				}
				true
			}
		}
		"sql conn ok!".info()
	}
}

class TestTouch(val wnd: MainWnd): UiComponent() {
	override fun createView(ui: UiCtx) = with(ui) {
		linearLayout(false) {
			containerLayout(70, 100, true) {
				touchSurface {
					id = R.id.check
				}
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
				this.mDragSensitive = Size(64, 64)
				backgroundSet {
					solid = 0x4000ff00
				}
			}.lps(0, 0, 6, 5)
		}
	}
	private class WndAdapter(context: Context, val item: UiComponent) : ArrayListAdapter<Ribbon>(context, item, item, listOf()) {

		val list1 = List(100) { it.toString() }
		val list2 = List(50) { "${it + 100}" }

		override fun getCount(): Int = 3

		/** Создает представление */
		override fun createView(position: Int, convertView: View?, resource: UiComponent, parent: ViewGroup, color: Boolean): View? {
			return ((convertView ?: item.createView(UiCtx(context))) as? Ribbon)?.apply {
				var lst = listOf("nothing")
				when(position) {
					0		-> lst = listOf("1", "2", "3", "4", "5", "6", "7")
					1		-> lst = list1
					2		-> lst = list2
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

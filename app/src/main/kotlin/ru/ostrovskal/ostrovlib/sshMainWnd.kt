package ru.ostrovskal.ostrovlib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.SurfaceHolder
import android.view.ViewGroup
import ru.ostrovskal.sshstd.*
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.adapters.ArrayListAdapter
import ru.ostrovskal.sshstd.json.JAdapter
import ru.ostrovskal.sshstd.layouts.AbsoluteLayout
import ru.ostrovskal.sshstd.objects.*
import ru.ostrovskal.sshstd.sql.SQL
import ru.ostrovskal.sshstd.sql.Table
import ru.ostrovskal.sshstd.ui.*
import ru.ostrovskal.sshstd.utils.*
import ru.ostrovskal.sshstd.widgets.Controller
import ru.ostrovskal.sshstd.widgets.html.Html


object Serg: Table() {
	@JvmField val id  = integer("_id").primaryKey(1)
	@JvmField val date  = timestamp("date").notNull
}

object Ostrov: Table() {
	@JvmField val id  = integer("_id").primaryKey(1)
	@JvmField val time= timestamp("time").notNull.index(true)
	@JvmField val date= timestamp("date").notNull.index(false).references(Serg.date)
	@JvmField val blob= blob("blob").index(true)
	@JvmField val text= text("text").notNull.index(false)
	@JvmField val real= real("real").default(0f).checked { it gt 0f }.index(true)
}

class AdapterHex : JAdapter {
	override fun serialize(value: Any?): Any? {
		return (value as? Int)?.toString(16)
	}
	
	override fun deserialize(value: String): Any? {
		return null
	}
}

class AdapterArr : JAdapter {
	override fun serialize(value: Any?): Any? {
		val r = value as? Int ?: return null
		return floatArrayOf(r / 3f, r / 2f, r.toFloat())
	}
	
	override fun deserialize(value: String): Any? {
		return null
	}
}

class AdapterDate : JAdapter {
	override fun serialize(value: Any?): Any? {
		return (value as? Long)?.datetime
	}
	
	override fun deserialize(value: String): Any? {
		return null
	}
}

class Primer {
	@STORAGE
	@JvmField val str1 = """Шаталов
		Влад"""
	@STORAGE @JvmField val flt1 = 3.05f
	@STORAGE @JvmField val dbl1 = 21.102030
	@STORAGE @JvmField val isB1 = true
}

object Example {
	@JsonAdapter("AdapterHex") @STORAGE @JsonName("vladislav") @JvmField var vlad = 500
	@JsonAdapter("AdapterHex") @STORAGE @JvmField val lst = listOf(11, 22, 33, 44, 55)
	@STORAGE @JsonName("ФИО") private val str = "Шаталов Сергей Викторович!!!"
	@STORAGE val flt = 1.05f
	@JsonAdapter("AdapterDate") @STORAGE val date = System.currentTimeMillis()
	@STORAGE val arrP = arrayOf(Primer(), Primer())
	@STORAGE const val dbl = 1.102030
	@JsonName("boolean value") var isB = false
	@JsonAdapter("AdapterArr") @STORAGE @JvmField val arr = intArrayOf(10, 20, 30)
	@STORAGE @JvmField val astr= arrayOf("serg", "vlad", "max", "viktor")
}

lateinit var ctr1: Controller
lateinit var ctr2: Controller
var nhtml: Html? = null
//lateinit var surface: ExampleSurface

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

enum class MsgEx {
	EMPTY1, INIT1, TAB_CHANGED
}

class MainWnd : Wnd() {
	var typeTheme = 1

	override fun onCreate(savedInstanceState: Bundle?) {
		startLog(this, "LIB", true, BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME, BuildConfig.DEBUG, enumNames(MsgEx.values()))
		super.onCreate(savedInstanceState)
		/*
		val j = Json(STORAGE::class.java)
		val ret = j.serialize(Example)
		ret.info()
		j.deserialize(this, ret)
*/

		Abs(this).setContent(this, SSH_APP_MODE_GAME)
		// загружаем фрагмент
		if(savedInstanceState == null) {
			//instanceForm(FORM_MENU)
		}
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
			if(!SQL.connection(this, false, Serg, Ostrov)) {
				"sql conn failed!".info()
			}
			"sql conn ok!".info()
		}
	}
	
	override fun onStart() {
		if(nhtml?.valid == false) nhtml?.setArticle("", 0)
		(content as? AbsoluteLayout)?.let {
			
			ctr1 = Controller(this, R.id.controller1, false).apply {
				controllerButtonNotify = { buttons ->
					"controller1 $buttons".info()
				}
			}
			ctr2 = Controller(this, R.id.controller2, false).apply {
				controllerButtonNotify = { buttons ->
					"controller2 $buttons".info()
				}
			}
			it.addView(ctr1)
			it.addView(ctr2)
			"start".info()
		}
		super.onStart()
	}
	
}

class SpinnerItem1 : UiComponent() {
	override fun createView(ui: UiCtx) = ui.run { text(R.string.null_text, style_spinner_item) }
}

class GridItem : UiComponent() {
	override fun createView(ui: UiCtx) = ui.run {
		text(R.string.null_text, style_spinner_item) {
			layoutParams = ViewGroup.LayoutParams(250, 50)
		}
	}
}

class ExampleSurface(context: Context) : Surface(context) {
	val paint = Paint().apply {
		this.color = Color.WHITE
		this.strokeWidth = 1f
		this.textSize = 24f
	}
	
	override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
		super.surfaceChanged(holder, format, width, height)
		"surfaceChanged $width $height".info()
	}
	
	override fun surfaceCreated(holder: SurfaceHolder) {
		super.surfaceCreated(holder)
		"surfaceCreated".info()
	}
	
	override fun surfaceDestroyed(holder: SurfaceHolder) {
		super.surfaceDestroyed(holder)
		"surfaceDestroyed".info()
	}
	
	override fun draw(canvas: Canvas) {
		super.draw(canvas)
		canvas.drawColor(Color.RED)
		canvas.drawText("fps: $fps", 0f, 40f, paint)
	}
	
	override fun handleMessage(msg: Message): Boolean {
		"handleMessage ${msg.info}".info()
		return super.handleMessage(msg)
	}
}

class Abs(val wnd: MainWnd): UiComponent() {
	override fun createView(ui: UiCtx) = with(ui) {
		cellLayout(10, 10) {
			tabLayout {
				page(R.id.page1, R.string.check1) {
					cellLayout(20, 15, 0, true) {
						editLayout {
							editEx(0, R.string.hint, style_edit, style_editEx) {
						}

						}.lps(0, 0, 20, 2)
					}
				}
				page(R.id.page2, R.string.check2) {
					cellLayout(10, 10) {
						chartDiagram {
							this.direction = DIRU
							this.colorsSegments = context.resources.getString(R.string.colors_chart_diagram).toIntArray(14, Color.RED, 10, true, ',')
							this.maxValuesSegments = intArrayOf(1000, 1000, 1000, 1000, 1000, 1000, 1000)
							this.currentValuesSegments = intArrayOf(600, 800, 200, 400, 350, 1000, 700)
						}.lps(0, 0, 10, 10)
					}
				}
				page(R.id.page3, R.string.check3) {
					cellLayout(10, 20, 0, true) {
						backgroundSet {
							setBitmap("menu_common")
						}
						grid(R.id.ribbon, false) {
							adapter = ArrayListAdapter(ctx, GridItem(), GridItem(),
							                           listOf("1", "2", "3", "Сергей", "Влад", "Макс", "Виктор", "Мирослав", "Ольга", "Раиса", "4", "5", "6", "Варвара"))
							itemClickListener = { _, _, position, id ->
								"itemClick $position $id".info()
							}
							dividerHeight = 5.dp
							divider = drawableTile {
								gradient = intArrayOf(Color.BLACK, Color.RED)
								gradientDir = DIRL
							}
							cellSize = 30.dp
							stretchMode = GRID_STRETCH_CELL
							numCells = 3
						}.lps(2, 2, 6, 6)
					}
				}
			}//.lps(0, 0, 10, 10)
/*
			tabLayout {
				content.apply {
					padding = 4.dp
					backgroundSet {
						setBitmap("menu_common")
						padding = Rect(50, 50, 50, 50)
					}
				}
				tabChangeListener = {tab, content ->
					if(tab == 0) {
						content.byIdx<Chart>(0).startAnimation()
					}
					wnd.hand?.send(act = MsgEx.TAB_CHANGED.ordinal, a1 = tab)
				}
				page(R.id.page1, nIcon = R.integer.I_CANCEL, nTile = 0) {
					stretchLayout(false) {
						setOnTouchListener { _, event ->
							if(event.action == MotionEvent.ACTION_UP) {
								byIdx<Chart>(0).startAnimation()
							}
							true
						}
						chartDiagram {
							this.direction = DIRD
							this.colorsSegments = context.resources.getString(R.string.colors_chart_diagram).toIntArray(14, Color.RED, 10, true, ',')
							this.maxValuesSegments = intArrayOf(1000, 1000, 1000, 1000, 1000, 1000, 1000)
							this.currentValuesSegments = intArrayOf(600, 800, 200, 400, 350, 1000, 700)
						}
					}
				}
				page(R.id.page2, nIcon = R.integer.I_EDITOR, nTile = 1) {
					html {
						id = R.id.check1
						nhtml = this
						//backgroundSet(style_form)
						root = "game/ru"
						var key = "theme_tool"
						aliases["tool"] = Html.BitmapAlias(key, context.bitmapGetCache(key), 3, 1, 0, 0, 30, 30)
						key = "sprites"
						aliases["main"] = Html.BitmapAlias(key, context.bitmapGetCache(key), 10, 4, 0, 0, 23, 23)
						key = "icon_tiles"
						aliases["icons"] = Html.BitmapAlias(key, context.bitmapGetCache(key), 10, 3, 0, 0, 30, 30)
						key = "controller_tiles"
						aliases["cursor"] = Html.BitmapAlias(key, context.bitmapGetCache(key), 6, 1, 0, 0, 45, 45)
						key = "droid"
						aliases["game_panel"] = Html.BitmapAlias(key, context.bitmapGetCache(key))
						key = "menu_common"
						aliases["game_record"] = Html.BitmapAlias(key, context.bitmapGetCache(key))
					}.lps(MATCH, MATCH)
				}
				page(R.id.page3, nTile = 0, nIcon = R.integer.I_OPTIONS) {
					linearLayout {
						val g = grid(R.id.ribbon, false) {
							adapter = ArrayListAdapter(ctx, GridItem(), GridItem(), listOf("1", "2", "3", "Сергей", "Влад", "Макс", "Виктор", "Мирослав", "Ольга",
							                                                               "Раиса", "4", "5", "6", "Варвара"))
							itemClickListener = {_, _, position, id ->
								"itemClick $position $id".info()
							}
							dividerHeight = 5.dp
							divider = drawableTile {
								gradient = intArrayOf(Color.BLACK, Color.RED)
								gradientDir = DIRL
							}
*/
/*
							selector = drawableTile {
								gradient = intArrayOf(Color.WHITE, Color.GRAY)
								gradientDir = DIRD
								shape = TILE_SHAPE_ROUND
								radii = floatArrayOf(20f, 10f, 20f, 10f, 20f, 10f, 20f, 10f)
							}
*//*

							cellSize = 30.dp
							stretchMode = GRID_STRETCH_CELL
							numCells = 3
						}.lps(MATCH, 120.dp)
						spinner(R.id.spinner) {
							adapter = ArrayListAdapter(ctx, SpinnerPopup(), SpinnerItem(), listOf("1", "2", "3", "Сергей", "Владислав", "Макс", "Виктор", "Мирослав", "Ольга",
							                                                                         "Раиса", "4", "5", "6"))
							dividerHeight = 5.dp
							vertOffset = 0
							divider = drawableTile {
								gradient = intArrayOf(Color.BLACK, Color.MAGENTA)
								gradientDir = DIRU
							}
*/
/*
							selector = drawableTile {
								gradient = intArrayOf(0xffffffff.toInt(), 0xffa0a0a0.toInt())
								gradientDir = DIRDL
								shape = TILE_SHAPE_ROUND
								radii = floatArrayOf(20f, 10f, 20f, 10f, 20f, 10f, 20f, 10f)
								selectorWidth = 6f
								selectorColor = Color.RED
							}
*//*

							itemClickListener = {spinner, _, position, _ ->
								wnd.typeTheme = position % 2
								"spinner click $position ${spinner.selectionString} grid pos: ${g.selectedItemPosition} ${g.selection}".info()
								wnd.changeTheme()
							}
						}.lps(MATCH, WRAP)
					}
				}
			}.lps(0, if(config.portrait) 200.dp else 150.dp, MATCH, 300.dp)
*/
		}
	}
}


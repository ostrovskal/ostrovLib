@file:Suppress("DEPRECATION")

package ru.ostrovskal.sshstd.objects

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.TEXT_ALIGNMENT_GRAVITY
import android.view.inputmethod.EditorInfo.IME_FLAG_NO_EXTRACT_UI
import android.widget.TextView
import com.github.ostrovskal.sshstd.R
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.utils.*
import ru.ostrovskal.sshstd.widgets.Tile
import ru.ostrovskal.sshstd.widgets.lists.BaseRibbon
import ru.ostrovskal.sshstd.widgets.lists.Grid
import ru.ostrovskal.sshstd.widgets.lists.Ribbon
import ru.ostrovskal.sshstd.widgets.lists.Spinner

/**
 * @author Шаталов С.В.
 * @since 0.1.0
 */

/** Объект, реализующий механизм взаимодействия с темами, атрибутами и стилями представлений */
object Theme {
	
	// Текущая тема
	private var theme           = intArrayOf()
	
	/**  Значение целого атрибута */
	@JvmField var int           = 0
	
	/** Значение вещественного атрибута */
	@JvmField var flt           = 0f
	
	/** Значение логического атрибута */
	@JvmField var bol           = false
	
	/** Значение строкового атрибута */
	@JvmField var str           = ""
	
	/** Значение drawable атрибута */
	@JvmField var drw: Drawable?= null

	/** Имя текущей темы */
	@JvmField var name          = ""
	
	/** Извлечь значение атрибута [attr] из текущей темы. [def] Значение атрибута по умолчанию */
	@JvmStatic fun themeAttrValue(attr: Int, def: Int) = theme.themeAttrValue(attr, def)
	
	/**
	 * Получить значение атрибута
	 *
	 * @param context Контекст
	 * @param attr    Атрибут
	 * @param value   Значение
	 */
	@JvmStatic fun attrProps(context: Context, attr: Int, value: Int) {
		when(attr and ATTR_APROPS_MSK) {
			ATTR_INT        -> int = integer(context, value)
			ATTR_FLT        -> flt = dimen(context, value, true).toFloat()
			ATTR_DMN        -> int = dimen(context, value)
			ATTR_STR        -> str = string(context, value)
			ATTR_BOL        -> bol = boolean(context, value)
			// Либо color, либо id drawable
			ATTR_DRW        -> drw = drawable(context, value, attr == ATTR_SSH_BITMAP_NAME)
			else            -> error("Свойство атрибута ${attr and ATTR_ATTR_MSK} не определено!")
		}
	}
	
	/** Вернуть drawable значение [value], если [isName] установлен в false, или имя картинки, в противном случае */
	@JvmStatic fun drawable(context: Context, value: Int, isName: Boolean = false): Drawable? {
		return when(value and ATTR_VPROPS_MSK) {
			THEME   -> drawable(context, theme.themeAttrValue(value, -1, ATTR_DRW), isName)
			else    -> try {
                // Если имя темы не указано, то возвращаем "пустую" картинку
                val nvalue = if(name.isBlank()) R.drawable.drawable_not_found else value
                if(!isName) context.resources.getDrawable(nvalue)
				else {
					val resName = context.resources.getResourceEntryName(nvalue)
					str = if(resName.startsWith("theme_"))
						// Вырезаем имя картинки, с именем текущей темы в конце
						resName.substringBeforeLast('_')
					else
						// Просто возвращаем имя картинки
						resName
					null
				}
			}
			catch(e: Resources.NotFoundException) { if(!isName) ColorDrawable(integer(context, value)) else {str = ""; null } }
		}
	}
	
	/** Вернуть dimen значение [value] (ID в ресурсах, непосредственное значение, значение из темы) */
	@JvmStatic fun dimen(context: Context, value: Int, isFloat: Boolean = false): Int {
		return when(value and ATTR_VPROPS_MSK) {
			THEME   -> dimen(context, theme.themeAttrValue(value, -1, ATTR_DMN), isFloat)
			IDRES   -> {
				val ret = (context.resources.getDimensionPixelOffset(value) * config.multiplySW).toInt()
				if(isFloat) ret.sp2px else ret
			}
			else    -> value.dp
		}
	}
	
	/** Вернуть целое значение [value] (ID в ресурсах, непосредственное значение, значение из темы, цвет) */
	@JvmStatic fun integer(context: Context, value: Int): Int {
		return when(value and ATTR_VPROPS_MSK) {
			COLOR   -> value.color
			THEME   -> integer(context, theme.themeAttrValue(value, -1, ATTR_INT))
			IDRES   -> context.resources.getInteger(value)
			else    -> value
		}
	}
	
	/** Вернуть строковое значение [value] (ID в ресурсах strings или значение из темы) */
	@JvmStatic fun string(context: Context, value: Int): String {
		return when(value and ATTR_VPROPS_MSK) {
			THEME   -> string(context, theme.themeAttrValue(value, -1, ATTR_STR))
			else    -> context.resources.getString(value)
		}
	}
	
	/** Вернуть логическое значение [value] (ID в ресурсах, непосредственное значение, значение из темы) */
	@JvmStatic fun boolean(context: Context, value: Int): Boolean {
		return when(value and ATTR_VPROPS_MSK) {
			THEME   -> boolean(context, theme.themeAttrValue(value, -1, ATTR_BOL))
			IDRES   -> context.resources.getBoolean(value)
			else    -> value != 0
		}
	}
	
	/**
	 * Обновить цвета/картинки объекта
	 *
	 * @param context   Контекст
	 * @param obj       Объект
	 * @param style     Массив атрибутов и их значений
	 */
	@JvmStatic fun updateTheme(context: Context, obj: Any, style: IntArray) {
		style.loopAttrs { attr, value ->
			if(value test THEME) {
				attrProps(context, attr, value)
				(obj as? View)?.apply {
					if(attr == ATTR_BACKGROUND) background = drw
				}
				(obj as? Tile)?.apply {
                    when(attr) {
                        ATTR_SSH_BACKGROUND -> drawable.background = drw
                        ATTR_SSH_BITMAP_NAME-> setBitmap(str, drawable.horz, drawable.vert, tile)
                    }
				}
				(obj as? TextView)?.apply {
					when(attr) {
						ATTR_COLOR_DEFAULT   -> { setTextColor(int); paint.color = int }
						ATTR_COLOR_HINT      -> setHintTextColor(int)
						ATTR_COLOR_LINK      -> setLinkTextColor(int)
						ATTR_COLOR_HIGHLIGHT -> highlightColor = int
					}
				}
				(obj as? BaseRibbon)?.apply {
					when(attr) {
						ATTR_SELECTOR -> if(selector is ColorDrawable) selector = drw
						ATTR_DIVIDER  -> if(divider is ColorDrawable) divider = drw
					}
				}
				(obj as? Spinner)?.apply {
					when(attr) {
						ATTR_SELECTOR -> if(selector is ColorDrawable) selector = drw
						ATTR_DIVIDER  -> if(divider is ColorDrawable) divider = drw
					}
				}
			}
		}
	}
	
	/**
	 * Установить базовые атрибуты объекта
	 *
	 * @param context   Контекст
	 * @param obj       Объект
	 * @param style     Массив атрибутов и их значений
	 */
	@JvmStatic fun setBaseAttr(context: Context, obj: View, style: IntArray) {
		style.loopAttrs { attr, value ->
			attrProps(context, attr, value)
			(obj as? View)?.apply {
				when(attr) {
					ATTR_PADDING            -> padding = int
					ATTR_PADDING_HORZ       -> horizontalPadding = int
					ATTR_PADDING_VERT       -> verticalPadding = int
					ATTR_PADDING_LEFT       -> leftPadding = int
					ATTR_PADDING_RIGHT      -> rightPadding = int
					ATTR_PADDING_TOP        -> topPadding = int
					ATTR_PADDING_BOTTOM     -> bottomPadding = int
					ATTR_MIN_HEIGHT         -> {val mh = minimumHeight; if(mh != int) minimumHeight = int }
					ATTR_MIN_WIDTH          -> {val mw = minimumWidth; if(mw != int) minimumWidth = int }
					ATTR_VISIBILITY         -> visibility = int
					ATTR_CLICKABLE          -> isClickable = bol
					ATTR_LONG_CLICKABLE     -> isLongClickable = bol
					ATTR_FOCUSABLE          -> isFocusable = bol
					ATTR_ENABLED            -> isEnabled = bol
					ATTR_BACKGROUND         -> background = drw
					ATTR_SCROLLBARS         -> {
                        isVerticalScrollBarEnabled = (int test SCROLLBARS_VERT)
						isHorizontalScrollBarEnabled = (int test SCROLLBARS_HORZ)
					}
					ATTR_FADING_EDGE        -> {
						isVerticalFadingEdgeEnabled = (int test SCROLLBARS_VERT)
                        isHorizontalFadingEdgeEnabled = (int test SCROLLBARS_HORZ)
                    }
					ATTR_FOCUSABLE_TOUCH_MODE-> isFocusableInTouchMode = bol
				}
			}
			(obj as? TextView)?.apply {
				when(attr) {
					ATTR_SIZE            -> textSize = flt
					ATTR_COLOR_DEFAULT   -> { setTextColor(int); paint.color = int }
					ATTR_COLOR_HINT      -> setHintTextColor(int)
					ATTR_COLOR_LINK      -> setLinkTextColor(int)
					ATTR_COLOR_HIGHLIGHT -> highlightColor = int
					ATTR_FONT            -> typeface = context.makeFont(str)
					ATTR_STYLE           -> setTypeface(typeface, int)
					ATTR_TEXT_ALIGN      -> textAlignment = int
					ATTR_IME_OPTIONS     -> imeOptions = int
					ATTR_INPUT_TYPE      -> inputType = int
					ATTR_MAX_LENGTH      -> filters = arrayOf(InputFilter.LengthFilter(int))
					ATTR_GRAVITY         -> gravity = int
					ATTR_SHADOW_TEXT     -> setShadowText(obj, str)
					ATTR_MAX_HEIGHT      -> maxHeight = int
					ATTR_MAX_WIDTH       -> maxWidth = int
				}
			}
			(obj as? Grid)?.apply {
				when(attr) {
					ATTR_SPACING_LINE   -> lineSpacing = int
					ATTR_CELL_SIZE      -> cellSize = int
					ATTR_CELL_NUM       -> numCells = int
					ATTR_DIVIDER_SIZE   -> dividerHeight = int
					ATTR_DIVIDER        -> divider = drw
					ATTR_SPACING_CELL   -> cellSpacing = int
					ATTR_STRETCH_MODE   -> stretchMode = int
					ATTR_SELECTOR       -> selector = drw
				}
			}
			(obj as? Ribbon)?.apply {
				when(attr) {
					ATTR_SELECTOR     -> selector = drw
					ATTR_DIVIDER_SIZE -> dividerHeight = int
					ATTR_DIVIDER      -> divider = drw
				}
			}
			(obj as? Spinner)?.apply {
				when(attr) {
					ATTR_GRAVITY                -> gravity = int
					ATTR_SELECTOR               -> selector = drw
					ATTR_DIVIDER_SIZE           -> dividerHeight = int
					ATTR_DIVIDER                -> divider = drw
					ATTR_SSH_DROPDOWN_HORZ_OFFS -> horzOffset = int
					ATTR_SSH_DROPDOWN_VERT_OFFS -> vertOffset = int
					ATTR_SSH_DROPDOWN_WIDTH     -> dropdownWidth = int
				}
			}
		}
	}
	
	/** Установка слоя тени для текста [view] из строки [str] в формате: dx, dy, radius, color */
	@JvmStatic fun setShadowText(view: TextView, str: String) {
        val shadow = str.split(',')
        view.setShadowLayer(shadow[2].fval(0f).dp, shadow[0].fval(0f).dp, shadow[1].fval(0f).dp, shadow[3].cval(0))
	}
	
	/** Установка текущей темы [theme] */
	@JvmStatic fun setTheme(context: Context, theme: IntArray) {
		this.name = string(context, theme.themeAttrValue(ATTR_SSH_THEME_NAME, -1, ATTR_STR))
		this.theme = theme
	}
}
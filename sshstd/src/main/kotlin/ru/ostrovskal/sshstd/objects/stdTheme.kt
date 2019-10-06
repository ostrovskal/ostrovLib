@file:Suppress("DEPRECATION")

package ru.ostrovskal.sshstd.objects

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.InputFilter
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

/** Свойство атрибута. Целое  */
const val ATTR_INT              = 0x00800000

/** Свойство атрибута. Вещественное  */
const val ATTR_FLT              = 0x00400000

/** Свойство атрибута. Строка  */
const val ATTR_STR              = 0x00200000

/** Свойство атрибута. Логическое  */
const val ATTR_BOL              = 0x00100000

/** Свойство атрибута. dimen  */
const val ATTR_DMN              = 0x00080000

/** Свойство атрибута. Drawable  */
const val ATTR_DRW              = 0x00040000

/** Типы атрибута. Значение из текущей темы  */
const val THEME                 = -0x80000000

/** Типы атрибута. Цвет  */
const val COLOR                 = 0x40000000

/** Типы атрибута. Значение из ресурсов  */
const val IDRES                 = 0x70000000

/** Маска значения атрибута  */
const val ATTR_VALUE_MSK        = 0x0fffffff

/** Маска свойств значения атрибута  */
const val ATTR_APROPS_MSK       = 0x00fc0000

/** Маска свойств типа атрибута  */
const val ATTR_VPROPS_MSK       = -0x10000000

/** Маска индекса атрибута  */
const val ATTR_ATTR_MSK         = 0x0000ffff

// Текстовые атрибуты стилей
/** Цвет текста по умолчанию */
const val ATTR_COLOR_DEFAULT        = 0 or ATTR_INT
/** Цвет подсказки поля ввода */
const val ATTR_COLOR_HINT           = 1 or ATTR_INT
/** Цвет ссылки в html */
const val ATTR_COLOR_LINK           = 2 or ATTR_INT
/** Цвет подсвеченного текста */
const val ATTR_COLOR_HIGHLIGHT      = 3 or ATTR_INT
/** Размер шрифта */
const val ATTR_SIZE                 = 4 or ATTR_FLT
/** Шрифт */
const val ATTR_FONT                 = 5 or ATTR_STR
/** Стиль начертания текста */
const val ATTR_STYLE                = 6 or ATTR_INT
/** Выравнивание текста */
const val ATTR_TEXT_ALIGN           = 7 or ATTR_INT
/** Тип клавиатуры при вводе текста в поле ввода */
const val ATTR_IME_OPTIONS          = 8 or ATTR_INT
/** Тип вводимого текста в поле ввода (текст, цифра и тд.) */
const val ATTR_INPUT_TYPE           = 9 or ATTR_INT
/** Максимальная длина текста в поле ввода */
const val ATTR_MAX_LENGTH           = 10 or ATTR_INT
/** Параметры тени текста */
const val ATTR_SHADOW_TEXT          = 11 or ATTR_STR

// Стандартные атрибуты стилей
/** Внутренний отступ */
const val ATTR_PADDING              = 50 or ATTR_DMN
/** Внутренний горизонтальный отступ */
const val ATTR_PADDING_HORZ         = 51 or ATTR_DMN
/** Внутренний вертикальный отступ */
const val ATTR_PADDING_VERT         = 52 or ATTR_DMN
/** Внутренний левый отступ */
const val ATTR_PADDING_LEFT         = 53 or ATTR_DMN
/** Внутренний правый отступ */
const val ATTR_PADDING_RIGHT        = 54 or ATTR_DMN
/** Внутренний верхний отступ */
const val ATTR_PADDING_TOP          = 55 or ATTR_DMN
/** Внутренний нижний отступ */
const val ATTR_PADDING_BOTTOM       = 56 or ATTR_DMN
/** Признак возможности клика на представлении */
const val ATTR_CLICKABLE            = 57 or ATTR_BOL
/** Признак получения фокуса */
const val ATTR_FOCUSABLE            = 58 or ATTR_BOL
/** Режим отображение представления */
const val ATTR_VISIBILITY           = 59 or ATTR_INT
/** Гравитация представления */
const val ATTR_GRAVITY              = 60 or ATTR_INT
/** Минимальная высота представления */
const val ATTR_MIN_HEIGHT           = 61 or ATTR_DMN
/** Минимальная ширина представления */
const val ATTR_MIN_WIDTH            = 62 or ATTR_DMN
/** Максимальная высота представления */
const val ATTR_MAX_HEIGHT           = 63 or ATTR_DMN
/** Максимальная ширина представления */
const val ATTR_MAX_WIDTH            = 64 or ATTR_DMN
/** Признак доступности представления */
const val ATTR_ENABLED              = 65 or ATTR_BOL
/** Виды прокруток у представления */
const val ATTR_SCROLLBARS           = 66 or ATTR_INT
/** Вид отбрасывания тени у представления */
const val ATTR_FADING_EDGE          = 67 or ATTR_INT
/** Расстояние между ячейками в Grid */
const val ATTR_SPACING_CELL         = 68 or ATTR_DMN
/** Расстояние между строками в Grid */
const val ATTR_SPACING_LINE         = 69 or ATTR_DMN
/** Размер ячейки */
const val ATTR_CELL_SIZE            = 70 or ATTR_DMN
/**  Количество ячеек в Grid */
const val ATTR_CELL_NUM             = 71 or ATTR_INT
/** Режим отображения ячеек в Grid */
const val ATTR_STRETCH_MODE         = 72 or ATTR_INT
/** Фон представления */
const val ATTR_BACKGROUND           = 73 or ATTR_DRW
/** Признак длинного клика на представлении */
const val ATTR_LONG_CLICKABLE       = 74 or ATTR_BOL
/** Селектор */
const val ATTR_SELECTOR             = 75 or ATTR_DRW
/** Разделитель */
const val ATTR_DIVIDER              = 76 or ATTR_DRW
/** Размер разделителя */
const val ATTR_DIVIDER_SIZE         = 77 or ATTR_DMN
/** Признак того, что  представление было выбрано */
const val ATTR_CHECKED              = 78 or ATTR_BOL
/** Признак получения фокуса ввода при касании на виджете (поле ввода) */
const val ATTR_FOCUSABLE_TOUCH_MODE = 79 or ATTR_BOL

// Библиотечные атрибуты стилей, Доступ к картинкам
/** Картинка для тайлов иконок */
const val ATTR_SSH_BM_ICONS         = 100 or ATTR_DRW
/** Картинка для базовых тайлов */
const val ATTR_SSH_BM_TILES         = 101 or ATTR_DRW
/** Картинка для фона форм */
const val ATTR_SSH_BM_BACKGROUND    = 102 or ATTR_DRW
/** Картинка для заголовка форм/диалогов */
const val ATTR_SSH_BM_HEADER        = 103 or ATTR_DRW
/** Картинка для кнопок */
const val ATTR_SSH_BM_BUTTONS       = 104 or ATTR_DRW
/** Картинка для инструментальных кнопок */
const val ATTR_SSH_BM_TOOLS         = 105 or ATTR_DRW
/** Картинка для радио кнопок */
const val ATTR_SSH_BM_RADIO         = 106 or ATTR_DRW
/** Картинка для флажков */
const val ATTR_SSH_BM_CHECK         = 107 or ATTR_DRW
/** Картинка для спиннера */
const val ATTR_SSH_BM_SPINNER       = 108 or ATTR_DRW
/** Картинка для переключателя */
const val ATTR_SSH_BM_SWITCH        = 109 or ATTR_DRW
/** Картинка для поля ввода */
const val ATTR_SSH_BM_EDIT          = 110 or ATTR_DRW
/** Картинка для слайдера */
const val ATTR_SSH_BM_SEEK          = 111 or ATTR_DRW
/** Картинка для главного экрана приложения */
const val ATTR_SSH_BM_MENU          = 112 or ATTR_DRW

// Библиотечные атрибуты стилей, Цвета
/** Цвет обычного текста */
const val ATTR_SSH_COLOR_NORMAL     = 150 or ATTR_INT
/** Цвет маленького текста */
const val ATTR_SSH_COLOR_SMALL      = 151 or ATTR_INT
/** Цвет большого текста */
const val ATTR_SSH_COLOR_LARGE      = 152 or ATTR_INT
/** Цвет html ссылки */
const val ATTR_SSH_COLOR_LINK       = 153 or ATTR_INT
/** Цвет подсказки в поле ввода */
const val ATTR_SSH_COLOR_HINT       = 154 or ATTR_INT
/** Цвет текста заголовка формы/диалога */
const val ATTR_SSH_COLOR_HEADER     = 155 or ATTR_INT
/** Цвет фона разметки */
const val ATTR_SSH_COLOR_LAYOUT     = 156 or ATTR_INT
/** Цвет отладочной сетки */
const val ATTR_SSH_COLOR_WIRED      = 157 or ATTR_INT
/** Цвет разделителя */
const val ATTR_SSH_COLOR_DIVIDER    = 158 or ATTR_INT
/** Цвет сообщений */
const val ATTR_SSH_COLOR_MESSAGE    = 159 or ATTR_INT
/** Цвет окон */
const val ATTR_SSH_COLOR_WINDOW     = 160 or ATTR_INT
/** Цвет html заголовков */
const val ATTR_SSH_COLOR_HTML_HEADER= 161 or ATTR_INT
/** Цвет селектора */
const val ATTR_SSH_COLOR_SELECTOR   = 162 or ATTR_INT

// Библиотечные атрибуты стилей, Виджеты
/** Состояние виджета */
const val ATTR_SSH_STATES               = 200 or ATTR_INT
/** Ограничительная фигура виджета */
const val ATTR_SSH_SHAPE                = 201 or ATTR_INT
/** Градиентная заливка фона виджета */
const val ATTR_SSH_GRADIENT             = 202 or ATTR_STR
/** Сплошной цвет заливки фона виджета */
const val ATTR_SSH_SOLID                = 203 or ATTR_INT
/** Признак отображения отладочной сетки/Текста */
const val ATTR_SSH_SHOW                 = 204 or ATTR_BOL
/** Реальная ширина ползунка переключателя */
const val ATTR_SSH_THUMB_WIDTH          = 205 or ATTR_INT
/** Щирина селектора */
const val ATTR_SSH_WIDTH_SELECTOR       = 206 or ATTR_DMN
/** Направление градиентной заливки фона виджета */
const val ATTR_SSH_GRADIENT_DIR         = 207 or ATTR_INT
/** Гравитация виджета */
const val ATTR_SSH_GRAVITY              = 208 or ATTR_INT
/** Имя картинки виджета */
const val ATTR_SSH_BITMAP_NAME          = 210 or ATTR_DRW
/** Тип анимации ползунка слайдера */
const val ATTR_SSH_SEEK_ANIM            = 211 or ATTR_INT
/** Ширина контроллера */
const val ATTR_SSH_CONTROLLER_WIDTH     = 212 or ATTR_DMN
/** Высота контроллера */
const val ATTR_SSH_CONTROLLER_HEIGHT    = 213 or ATTR_DMN
/** Альфа виджета */
const val ATTR_SSH_ALPHA                = 214 or ATTR_INT
/** Патч9 для виджета */
const val ATTR_SSH_PATCH9               = 216 or ATTR_STR
/** Массив углов для скругленного прямоугольника виджета */
const val ATTR_SSH_RADII                = 217 or ATTR_STR
/** Задержка в анимации */
const val ATTR_SSH_ANIMATOR_DURATION    = 218 or ATTR_INT
/** Массив цветов(начальный..конечный) сегментов диаграммы */
const val ATTR_SSH_COLORS               = 219 or ATTR_STR
/** Начальный угол первого сегмента круговой диаграммы */
const val ATTR_SSH_CHART_BEGIN_ANGLE    = 220 or ATTR_FLT
/** Радиус, в процентах, выделенных сегментов круговой диаграммы */
const val ATTR_SSH_CHART_CHECK_RADIUS   = 221 or ATTR_INT
/** Величина, в процентах, внутреннего радиуса круговой диаграммы */
const val ATTR_SSH_CHART_INNER_RADIUS   = 222 or ATTR_INT
/** Смещение тени/нажатия виджета */
const val ATTR_SSH_PRESSED_OFFS         = 223 or ATTR_FLT
/** Количество тайлов по горизонтали в картинке виджета */
const val ATTR_SSH_HORZ                 = 224 or ATTR_INT
/** Количество тайлов по вертикали в картинке виджета */
const val ATTR_SSH_VERT                 = 225 or ATTR_INT
/** Номер тайла по умолчанию в картинке виджета */
const val ATTR_SSH_TILE                 = 226 or ATTR_INT
/** Номер тайла иконки виджета */
const val ATTR_SSH_ICON                 = 227 or ATTR_INT
/** Тип масштабирования виджета */
const val ATTR_SSH_SCALE                = 228 or ATTR_INT
/** Смещение по вертикали при отображение выпадающего списка спиннера */
const val ATTR_SSH_DROPDOWN_VERT_OFFS   = 220 or ATTR_DMN
/** Смещение по горизонтали при отображение выпадающего списка спиннера */
const val ATTR_SSH_DROPDOWN_HORZ_OFFS   = 230 or ATTR_DMN
/** Ширина выпадающего списка спиннера */
const val ATTR_SSH_DROPDOWN_WIDTH       = 231 or ATTR_INT
/** Имя темы */
const val ATTR_SSH_THEME_NAME           = 232 or ATTR_STR
/** Фон рисунка виджета */
const val ATTR_SSH_BACKGROUND           = 233 or ATTR_DRW
/** Коеффицент масштабирования иконки относительно размеров виджета */
const val ATTR_SSH_SCALE_ICON           = 234 or ATTR_INT
/** Тип выравнивания иконки относительно виджета */
const val ATTR_SSH_GRAVITY_ICON         = 235 or ATTR_INT
/** Размер селектора вкладки */
const val ATTR_SSH_SIZE_SELECTOR_TAB    = 236 or ATTR_INT
/** Размер селектора активно вкладки */
const val ATTR_SSH_SIZE_SELECTOR_SEL_TAB= 237 or ATTR_INT

/** Стиль по умолчанию для Большого текста */
@JvmField val style_text_large            = intArrayOf(ATTR_SHADOW_TEXT, R.string.shadow_text,
                                             ATTR_GRAVITY, Gravity.CENTER_VERTICAL,
                                             ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_LARGE or THEME,
                                             ATTR_SIZE, R.dimen.large,
                                             ATTR_FONT, R.string.font_large)

/** Стиль по умолчанию для Обычного текста */
@JvmField val style_text_normal           = intArrayOf(ATTR_SHADOW_TEXT, R.string.shadow_text,
                                             ATTR_GRAVITY, Gravity.CENTER_VERTICAL,
                                             ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_NORMAL or THEME,
                                             ATTR_SIZE, R.dimen.normal,
                                             ATTR_FONT, R.string.font_normal)

/** Стиль по умолчанию для Маленького текста */
@JvmField val style_text_small            = intArrayOf(ATTR_SHADOW_TEXT, R.string.shadow_text,
                                             ATTR_GRAVITY, Gravity.CENTER_VERTICAL,
                                             ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_SMALL or THEME,
                                             ATTR_SIZE, R.dimen.small,
                                             ATTR_FONT, R.string.font_small)

/** Стиль по умолчанию для Заголовка форма/диалога */
@JvmField val style_text_header           = intArrayOf(ATTR_SHADOW_TEXT, R.string.shadow_text,
                                             ATTR_GRAVITY, Gravity.CENTER,
                                             ATTR_STYLE, Typeface.BOLD_ITALIC,
                                             ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_HEADER or THEME,
                                             ATTR_SIZE, R.dimen.header,
                                             ATTR_PADDING, 1,
                                             ATTR_SSH_TILE, 0,
                                             ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_HEADER or THEME,
                                             ATTR_SSH_PATCH9, R.string.patch9_header,
                                             ATTR_FONT, R.string.font_large,
                                             ATTR_MIN_HEIGHT, R.dimen.heightHeader)

/** Стиль по умолчанию для Текста подсказки в поле ввода */
@JvmField val style_text_hint             = intArrayOf(ATTR_SHADOW_TEXT, R.string.shadow_text,
                                             ATTR_GRAVITY, Gravity.CENTER_VERTICAL,
                                             ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_HINT or THEME,
                                             ATTR_SIZE, R.dimen.hint,
                                             ATTR_FONT, R.string.font_small)

/** Стиль по умолчанию для HTML текста */
@JvmField val style_text_html             = intArrayOf(ATTR_PADDING, 2,
                                             ATTR_SHADOW_TEXT, R.string.shadow_text,
                                             ATTR_COLOR_LINK, 0x00ffff or COLOR,
                                             ATTR_GRAVITY, Gravity.CENTER,
                                             ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_NORMAL or THEME,
                                             ATTR_SIZE, R.dimen.html,
                                             ATTR_FONT, R.string.font_small)

/** Стиль по умолчанию для Текста в диалоге */
@JvmField val style_text_dlg              = intArrayOf(ATTR_PADDING, R.dimen.paddingDlg,
                                             ATTR_SHADOW_TEXT, R.string.shadow_text,
                                             ATTR_GRAVITY, Gravity.CENTER,
                                             ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_LARGE or THEME,
                                             ATTR_SIZE, R.dimen.large,
                                             ATTR_FONT, R.string.font_normal)

/** Стиль по умолчанию для Тайла */
@JvmField val style_tile                  = intArrayOf(ATTR_SSH_WIDTH_SELECTOR, 0,
                                             ATTR_SSH_PRESSED_OFFS, 0,
                                             ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_NORMAL or THEME,
                                             ATTR_SIZE, R.dimen.normal,
                                             ATTR_FONT, R.string.font_normal,
                                             ATTR_CLICKABLE, 0,
                                             ATTR_FOCUSABLE, 0,
                                             ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_TILES or THEME)

/** Стиль по умолчанию для Иконок */
@JvmField val style_icon                  = intArrayOf(ATTR_SSH_PRESSED_OFFS, R.dimen.pressedOffs,
                                             ATTR_SSH_SCALE, TILE_SCALE_MIN,
                                             ATTR_CLICKABLE, 0,
                                             ATTR_FOCUSABLE, 0,
                                             ATTR_SSH_GRAVITY, TILE_GRAVITY_CENTER or TILE_GRAVITY_BACKGROUND,
                                             ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_ICONS or THEME,
                                             ATTR_SSH_SCALE_ICON, 40000,
                                             ATTR_SSH_VERT, 3,
                                             ATTR_SSH_HORZ, 10)

/** Стиль по умолчанию для Радио кнопки */
@JvmField val style_radio                 = intArrayOf(ATTR_SHADOW_TEXT, R.string.shadow_text,
                                             ATTR_STYLE, Typeface.BOLD,
                                             ATTR_GRAVITY, Gravity.START or Gravity.CENTER_VERTICAL,
                                             ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_NORMAL or THEME,
                                             ATTR_SIZE, R.dimen.normal,
                                             ATTR_FONT, R.string.font_small,
                                             ATTR_TEXT_ALIGN, TEXT_ALIGNMENT_GRAVITY,
                                             ATTR_MIN_HEIGHT, R.dimen.heightRadio,
                                             ATTR_SSH_HORZ, 2, ATTR_SSH_TILE, 0,
                                             ATTR_SSH_SCALE, TILE_SCALE_MIN,
                                             ATTR_SSH_GRAVITY, TILE_GRAVITY_LEFT or TILE_GRAVITY_CENTER_VERT,
                                             ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_RADIO or THEME)

/** Стиль по умолчанию для Флажка */
@JvmField val style_check                 = intArrayOf(ATTR_SHADOW_TEXT, R.string.shadow_text,
                                             ATTR_STYLE, Typeface.BOLD,
                                             ATTR_GRAVITY, Gravity.START or Gravity.CENTER_VERTICAL,
                                             ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_NORMAL or THEME,
                                             ATTR_SIZE, R.dimen.normal,
                                             ATTR_FONT, R.string.font_small,
                                             ATTR_TEXT_ALIGN, TEXT_ALIGNMENT_GRAVITY,
                                             ATTR_MIN_HEIGHT, R.dimen.heightCheck,
                                             ATTR_SSH_HORZ, 2,
                                             ATTR_SSH_TILE, 0,
                                             ATTR_SSH_SCALE, TILE_SCALE_MIN,
                                             ATTR_SSH_GRAVITY, TILE_GRAVITY_LEFT or TILE_GRAVITY_CENTER_VERT,
                                             ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_CHECK or THEME)

/** Стиль по умолчанию для Переключателя */
@JvmField val style_switch                = intArrayOf(ATTR_SHADOW_TEXT, R.string.shadow_text,
                                             ATTR_STYLE, Typeface.BOLD_ITALIC,
                                             ATTR_GRAVITY, Gravity.START or Gravity.CENTER_VERTICAL,
                                             ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_NORMAL or THEME,
                                             ATTR_SIZE, R.dimen.normal,
                                             ATTR_FONT, R.string.font_small,
                                             ATTR_TEXT_ALIGN, TEXT_ALIGNMENT_GRAVITY,
                                             ATTR_MIN_HEIGHT, R.dimen.heightSwitch,
                                             ATTR_PADDING_HORZ, 3.dp,
                                             ATTR_SSH_VERT, 2,
                                             ATTR_SSH_TILE, 0,
                                             ATTR_SSH_THUMB_WIDTH, 32,
                                             ATTR_SSH_SCALE, TILE_SCALE_HEIGHT,
                                             ATTR_SSH_GRAVITY, TILE_GRAVITY_RIGHT or TILE_GRAVITY_CENTER_VERT,
                                             ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_SWITCH or THEME)

/** Стиль по умолчанию для Простой диаграммы */
@JvmField val style_chart_diagram         = intArrayOf(ATTR_COLOR_DEFAULT, 0 or COLOR,
                                                       ATTR_SSH_SHOW, 1,
                                                       ATTR_SSH_GRADIENT_DIR, DIRU,
                                                       ATTR_SIZE, R.dimen.chart,
                                                       ATTR_FONT, R.string.font_small)

/** Стиль по умолчанию для Круговой диаграммы */
@JvmField val style_chart_circular        = intArrayOf(ATTR_COLOR_DEFAULT, 0 or COLOR,
                                                       ATTR_SSH_SHOW, 1,
                                                       ATTR_SIZE, R.dimen.chart,
                                                       ATTR_FONT, R.string.font_small,
                                                       ATTR_SSH_CHART_BEGIN_ANGLE, 30,
                                                       ATTR_SSH_CHART_CHECK_RADIUS, 15,
                                                       ATTR_SSH_CHART_INNER_RADIUS, 50)

/** Стиль по умолчанию для Слайдера */
@JvmField val style_seek                  = intArrayOf(ATTR_MIN_HEIGHT, R.dimen.heightSeek,
                                             ATTR_SSH_VERT, 2,
                                             ATTR_SSH_TILE, 0,
                                             ATTR_PADDING_HORZ, 18,
                                             ATTR_MIN_WIDTH, R.dimen.widthSeek,
                                             ATTR_SSH_SCALE, TILE_SCALE_MIN,
                                             ATTR_SSH_GRAVITY, TILE_GRAVITY_LEFT or TILE_GRAVITY_CENTER_VERT,
                                             ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_SEEK or THEME,
                                             ATTR_SSH_SEEK_ANIM, ATTR_SSH_SEEK_ANIM or THEME)

/** Стиль по умолчанию для Поля ввода */
@JvmField val style_edit                  = intArrayOf(ATTR_SHADOW_TEXT, R.string.shadow_text,
                                             ATTR_GRAVITY, Gravity.CENTER_VERTICAL,
                                             ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_NORMAL or THEME,
                                             ATTR_SIZE, R.dimen.edit,
                                             ATTR_FONT, R.string.font_small,
                                             ATTR_FOCUSABLE_TOUCH_MODE, 1,
                                             ATTR_COLOR_HINT, ATTR_SSH_COLOR_HINT or THEME,
                                             ATTR_FOCUSABLE, 1,
                                             ATTR_CLICKABLE, 1,
                                             ATTR_SSH_TILE, 0,
                                             ATTR_PADDING_HORZ, R.dimen.paddingHorzEdit,
                                             ATTR_TEXT_ALIGN, TEXT_ALIGNMENT_GRAVITY,
                                             ATTR_PADDING_VERT, R.dimen.paddingVertEdit,
                                             ATTR_IME_OPTIONS, IME_FLAG_NO_EXTRACT_UI,
                                             ATTR_INPUT_TYPE, android.text.InputType.TYPE_CLASS_TEXT,
                                             ATTR_MAX_LENGTH, 15,
                                             ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_EDIT or THEME,
                                             ATTR_SSH_PATCH9, R.string.patch9_edit)

/** Стиль по умолчанию для элемента выпадающего списка Spinner */
@JvmField val style_spinner_item          = intArrayOf(ATTR_SHADOW_TEXT, R.string.shadow_text,
                                                       ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_LARGE or THEME,
                                                       ATTR_COLOR_HIGHLIGHT, ATTR_SSH_COLOR_NORMAL or THEME,
                                                       ATTR_GRAVITY, Gravity.CENTER,
                                                       ATTR_SIZE, R.dimen.normal,
                                                       ATTR_FONT, R.string.font_normal,
                                                       ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_SPINNER or THEME,
                                                       ATTR_MIN_HEIGHT, R.dimen.heightSelectItem,
                                                       ATTR_PADDING_HORZ, R.dimen.paddingHorzSelectItem,
                                                       ATTR_SSH_VERT, 3,
                                                       ATTR_SSH_PATCH9, R.string.patch9_select_item,
                                                       ATTR_SSH_TILE, 2)

/** Стиль по умолчанию для заголовка Spinner */
@JvmField val style_spinner_title         = intArrayOf(ATTR_SHADOW_TEXT, R.string.shadow_text,
                                             ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_LARGE or THEME,
                                             ATTR_COLOR_HIGHLIGHT, ATTR_SSH_COLOR_NORMAL or THEME,
                                             ATTR_PADDING_HORZ, R.dimen.paddingHorzSelectItem,
                                             ATTR_GRAVITY, Gravity.CENTER,
                                             ATTR_SIZE, R.dimen.large,
                                             ATTR_FONT, R.string.font_normal,
                                             ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_SPINNER or THEME,
                                             ATTR_SSH_VERT, 3,
                                             ATTR_SSH_TILE, 0)

/** Стиль по умолчанию для объекта Spinner */
@JvmField val style_spinner               = intArrayOf(ATTR_CLICKABLE, 1,
                                             ATTR_GRAVITY, Gravity.CENTER,
                                             ATTR_MIN_HEIGHT, R.dimen.heightSelectCaption,
                                             ATTR_SSH_PATCH9, R.string.patch9_select_caption,
                                             ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_SPINNER or THEME,
                                             ATTR_SSH_VERT, 3,
                                             ATTR_SSH_TILE, 1,
                                             ATTR_SSH_DROPDOWN_VERT_OFFS, R.dimen.vertOffsSelect,
                                             ATTR_SSH_DROPDOWN_WIDTH, MATCH,
                                             ATTR_SELECTOR, ATTR_SSH_COLOR_SELECTOR or THEME,
                                             ATTR_DIVIDER_SIZE, 0)

/** Стиль по умолчанию для Выпадающего списка Spinner*/
@JvmField val style_spinner_dropdown      = intArrayOf(ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_SPINNER or THEME,
                                                       ATTR_SSH_VERT, 3,
                                                       ATTR_SSH_TILE, 0,
                                                       ATTR_PADDING, 5,
                                                       ATTR_SSH_PATCH9, R.string.patch9_dropdown)

/** Стиль по умолчанию для Progress */
@JvmField val style_progress              = intArrayOf(ATTR_COLOR_DEFAULT, 0xffffff or COLOR,
                                             ATTR_SSH_SHOW, 1,
                                             ATTR_SIZE, R.dimen.heightProgress,
                                             ATTR_SSH_STATES, TILE_STATE_HOVER,
                                             ATTR_FONT, R.string.font_small)

/** Стиль по умолчанию для Элемента списка */
@JvmField val style_item                  = intArrayOf(ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_SPINNER or THEME,
                                             ATTR_SSH_VERT, 3,
                                             ATTR_SSH_TILE, 2,
                                             ATTR_SSH_PATCH9, R.string.patch9_item)

/** Стиль по умолчанию для Формы */
@JvmField val style_form                  = intArrayOf(ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_BACKGROUND or THEME,
                                             ATTR_SSH_TILE, 0)

/** Стиль по умолчанию для Диалога */
@JvmField val style_dlg                   = intArrayOf(ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_BACKGROUND or THEME,
                                             ATTR_SSH_SHAPE, TILE_SHAPE_ROUND,
                                             ATTR_SSH_RADII, R.string.radii_dlg)

/** Стиль по умолчанию для Главного экрана приложения */
@JvmField val style_menu                  = intArrayOf(ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_MENU or THEME,
                                             ATTR_SSH_TILE, 0)

/** Стиль по умолчанию для Контроллера */
@JvmField val style_controller            = intArrayOf(ATTR_VISIBILITY, GONE,
                                                       ATTR_SSH_CONTROLLER_WIDTH, 160,
                                                       ATTR_SSH_CONTROLLER_HEIGHT, 160,
                                                       ATTR_SSH_ALPHA, 184,
                                                       ATTR_SSH_GRAVITY, TILE_GRAVITY_CENTER or TILE_GRAVITY_BACKGROUND,
                                                       ATTR_SSH_BITMAP_NAME, R.drawable.controller_tiles,
                                                       ATTR_SSH_HORZ, 6,
                                                       ATTR_SSH_VERT, 1,
                                                       ATTR_SSH_TILE, 0)

/** Стиль по умолчанию для Кнопки */
@JvmField val style_button                = intArrayOf(ATTR_SHADOW_TEXT, R.string.shadow_text,
                                             ATTR_SIZE, R.dimen.button,
                                             ATTR_FONT, R.string.font_normal,
                                             ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_NORMAL or THEME,
                                             ATTR_CLICKABLE, 1,
                                             ATTR_PADDING, 1,
                                             ATTR_MIN_HEIGHT, R.dimen.heightButton,
                                             ATTR_SSH_PRESSED_OFFS, R.dimen.pressedOffs,
                                             ATTR_SSH_WIDTH_SELECTOR, R.dimen.widthSelector,
                                             ATTR_SSH_COLOR_SELECTOR, ATTR_SSH_COLOR_SELECTOR or THEME,
                                             ATTR_SSH_HORZ, 2,
                                             ATTR_SSH_TILE, 0,
                                             ATTR_SSH_STATES, TILE_STATE_HOVER,
                                             ATTR_GRAVITY, Gravity.CENTER,
                                             ATTR_SSH_GRAVITY, TILE_GRAVITY_CENTER or TILE_GRAVITY_BACKGROUND,
                                             ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_BUTTONS or THEME)

/** Стиль по умолчанию для Кнопки футера */
@JvmField val style_footer                = intArrayOf(ATTR_CLICKABLE, 1,
                                             ATTR_MIN_HEIGHT, R.dimen.heightButton,
                                             ATTR_SSH_PRESSED_OFFS, R.dimen.pressedOffs,
                                             ATTR_SSH_STATES, TILE_STATE_HOVER,
                                             ATTR_SSH_GRAVITY, TILE_GRAVITY_CENTER or TILE_GRAVITY_BACKGROUND,
                                             ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_BUTTONS or THEME,
                                             ATTR_SSH_HORZ, 2,
                                             ATTR_SSH_TILE, 1)

/** Стиль по умолчанию для Инструментальной кнопки */
@JvmField val style_tool                  = intArrayOf(ATTR_CLICKABLE, 1,
                                             ATTR_MIN_HEIGHT, R.dimen.heightButton,
                                             ATTR_SSH_PRESSED_OFFS, R.dimen.pressedOffs,
                                             ATTR_SSH_HORZ, 3,
                                             ATTR_SSH_TILE, 0,
                                             ATTR_SSH_STATES, TILE_STATE_HOVER,
                                             ATTR_SSH_GRAVITY, TILE_GRAVITY_CENTER or TILE_GRAVITY_BACKGROUND,
                                             ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_TOOLS or THEME)

/** Стиль по умолчанию для Кнопки со стрелкой */
@JvmField val style_tool_arrow            = intArrayOf(ATTR_CLICKABLE, 1,
                                             ATTR_MIN_HEIGHT, R.dimen.heightButton,
                                             ATTR_SSH_PRESSED_OFFS, R.dimen.pressedOffs,
                                             ATTR_SSH_STATES, TILE_STATE_PRESS or TILE_STATE_SHADOW,
                                             ATTR_SSH_GRAVITY, TILE_GRAVITY_CENTER or TILE_GRAVITY_BACKGROUND,
                                             ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_TOOLS or THEME,
                                             ATTR_SSH_HORZ, 3,
                                             ATTR_SSH_TILE, 1)

/** Стиль по умолчанию для Списка */
@JvmField val style_ribbon                = intArrayOf(ATTR_SELECTOR, ATTR_SSH_COLOR_SELECTOR or THEME,
                                                       ATTR_PADDING, 2)

/** Стиль по умолчанию для GridView */
@JvmField val style_grid                  = intArrayOf(ATTR_SELECTOR, ATTR_SSH_COLOR_SELECTOR or THEME,
                                                       ATTR_SPACING_LINE, 1,
                                                       ATTR_SPACING_CELL, 1,
                                                       ATTR_CELL_SIZE, 130,
                                                       ATTR_PADDING, 2,
                                                       ATTR_STRETCH_MODE, GRID_STRETCH_UNIFORM)

/** Стиль по умолчанию для вкладки TabLayout */
@JvmField val style_tab_page              = intArrayOf(ATTR_SSH_VERT, 1,
                                                       ATTR_SSH_HORZ, 2,
                                                       ATTR_CLICKABLE, 1,
                                                       ATTR_FOCUSABLE, 0,
                                                       ATTR_SSH_SCALE, TILE_SCALE_NONE,
                                                       ATTR_SSH_WIDTH_SELECTOR, 0,
                                                       ATTR_SSH_GRAVITY, TILE_GRAVITY_CENTER or TILE_GRAVITY_BACKGROUND,
                                                       ATTR_GRAVITY, Gravity.CENTER,
                                                       ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_NORMAL or THEME,
                                                       ATTR_FONT, R.string.font_normal,
                                                       ATTR_SIZE, R.dimen.normal,
                                                       ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_BUTTONS or THEME)

/** Стиль по умолчанию для DrawableTile */
@JvmField val style_drawable_tile         = intArrayOf()

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
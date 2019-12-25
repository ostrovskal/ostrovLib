package ru.ostrovskal.sshstd;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.widget.ListPopupWindow;

import com.github.ostrovskal.sshstd.R;

import static android.view.View.GONE;
import static android.view.View.TEXT_ALIGNMENT_GRAVITY;
import static android.view.inputmethod.EditorInfo.IME_FLAG_NO_EXTRACT_UI;

/** Java класс, являющийся контейнером для глобальных переменных и констант */
public final class Common {
    private Common() { }

    /** Временный вещественный массив координат */
    public static final float[] xyFloat = { 0f, 0f };

    /** Временный целый массив координат */
    public static final int[] xyInt     = {0, 0};

    /** Вещественная временная область */
    final public static RectF fRect     = new RectF();

    /** Целая временная область */
    final public static Rect iRect      = new Rect();

    /** Вещественная временная точка */
    final public static PointF fPt      = new PointF();

    /** Целая временная точка */
    final public static Point iPt       = new Point();

    // Временный размер
    final public static Size tempSize	= new Size(0, 0);

    /** Признак активности отладки */
    public static boolean isDebug       = false;

    /** Признак активности лога SQL запросов */
    public static boolean isSqlLog      = false;

    /** Тэг для лога, имя базы данных */
    public static String logTag         = "OSTROV";

    /** Версия БД */
    public static int dbVersion         = 0;

    /** Путь к папке /data/data/package/cache */
    public static String folderCache    = "";

    /** Путь к папке /data/data/package/files */
    public static String folderFiles	= "";

    /** Путь к папке /data/data/package */
    public static String folderData	    = "";

    /** Имя пакета приложения */
    public static String                namePackage;

    /** Цветовые фильтры. Нажатие */
    public static final ColorMatrixColorFilter fltPressed  =
            new ColorMatrixColorFilter(new ColorMatrix(new float[]{0.75f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.75f, 0.00f, 0.00f, 0.00f,
                                                                   0.00f, 0.00f, 0.75f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 1.00f, 0.00f}));
    /** Цветовые фильтры. Тень */
    public static final ColorMatrixColorFilter fltShadowed =
            new ColorMatrixColorFilter(new ColorMatrix(new float[]{0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
                                                                   0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.40f, 0.00f}));
    /** Цветовые фильтры. Отключено */
    public static final ColorMatrixColorFilter fltDisabled =
            new ColorMatrixColorFilter(new ColorMatrix(new float[]{0.213f, 0.715f, 0.072f, 0.00f, 0.00f, 0.213f, 0.715f, 0.072f, 0.00f, 0.00f,
                                                                   0.213f, 0.715f, 0.072f, 0.00f, 0.00f, 0.000f, 0.000f, 0.000f, 1.00f, 0.00f}));
    /** Цветовые фильтры. Подсветка */
    public static final ColorMatrixColorFilter fltHovered =
            new ColorMatrixColorFilter(new ColorMatrix(new float[]{1.35f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 1.35f, 0.00f, 0.00f, 0.00f,
                                                                   0.00f, 0.00f, 1.35f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 1.35f, 0.00f}));
    /** Цветовые фильтры. Фокус */
    public static final ColorMatrixColorFilter fltFocused =
            new ColorMatrixColorFilter(new ColorMatrix(new float[]{1.35f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 1.35f, 0.00f, 0.00f, 0.00f,
                                                                   0.00f, 0.00f, 1.35f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 1.35f, 0.00f}));
    /** Цветовые фильтры. Красный */
    public static final ColorMatrixColorFilter fltRed =
            new ColorMatrixColorFilter(new ColorMatrix(new float[]{1.00f, 1.00f, 1.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
                                                                   0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 1.00f, 0.00f}));
    /** Цветовые фильтры. Зеленый */
    public static final ColorMatrixColorFilter fltGreen =
            new ColorMatrixColorFilter(new ColorMatrix(new float[]{0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 1.00f, 1.00f, 1.00f, 0.00f, 0.00f,
                                                                   0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 1.00f, 0.00f}));
    /** Цветовые фильтры. Синий */
    public static final ColorMatrixColorFilter fltBlue =
            new ColorMatrixColorFilter(new ColorMatrix(new float[]{0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
                                                                   1.00f, 1.00f, 1.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 1.00f, 0.00f}));
    /** Цветовые фильтры. Инверсия */
    public static final ColorMatrixColorFilter fltInvert =
            new ColorMatrixColorFilter(new ColorMatrix(new float[]{-1.00f, 0.00f, 0.00f, 0.00f, 255.00f, 0.00f, -1.00f, 0.00f, 0.00f, 255.00f,
                                                                   0.00f, 0.00f, -1.00f, 0.00f, 255.00f, 0.00f, 0.00f, 0.00f, 1.00f, 0.00f}));
    /** Цветовые фильтры. Черный бинарный */
    public static final ColorMatrixColorFilter fltBlackBinary =
            new ColorMatrixColorFilter(new ColorMatrix(new float[]{85.00f, 85.00f, 85.00f, 0.00f, -128.00f * 255.00f, 85.00f, 85.00f, 85.00f, 0.00f,
                                                                   -128.00f * 255.00f, 85.00f, 85.00f, 85.00f, 0.00f, -128.00f * 255.00f, 0.00f, 0.00f,
                                                                   0.00f, 1.00f, 0.00f}));
    /** Цветовые фильтры. Черный бинарный */
    public static final ColorMatrixColorFilter fltBinary =
            new ColorMatrixColorFilter(new ColorMatrix(new float[]{255.0f, 0.00f, 0.00f, 0.00f, -128.00f * 255.00f, 0.00f, 255.00f, 0.00f, 0.00f,
                                                                   -128.00f * 255.00f, 0.00f, 0.00f, 255.00f, 0.00f, -128.00f * 255.00f, 0.00f,
                                                                   0.00f, 0.00f, 1.00f, 0.00f}));

    /** */
    public static final int DBX_FIO         = 0;

    /** */
    public static final int DBX_LINK        = 1;

    /** */
    public static final int DBX_EMAIL       = 2;

    /** */
    public static final int DBX_COUNTRY     = 3;

    /** */
    public static final int DBX_PHOTO       = 4;

    /** Идентификатор загрузчика */
    public static final int CONNECTOR           = 1;

    /** Сообщение текущему окну/активити */
    public static final int RECEPIENT_WND       = 0;

    /** Сообщение для текущей формы */
    public static final int RECEPIENT_FORM      = 1;

    /** Сообщение для поверхности UI треда текущей формы */
    public static final int RECEPIENT_SURFACE_UI= 2;

    /** Сообщение для поверхности фонового треда текущей формы */
    public static final int RECEPIENT_SURFACE_BG= 3;

    /** Действие. Сообщение о результате формы сообщений */
    public static final int ACT_MESSAGE_RESULT  = -5;

    /** Действие. Пропуск */
    public static final int ACT_EMPTY           = -4;

    /** Действие. Запуск фонового треда */
    public static final int ACT_INIT_SURFACE    = -3;

    /** Действие. Нажатие на кнопку BACK */
    public static final int ACT_BACKPRESSED     = -2;

    /** Действие. Выход */
    public static final int ACT_EXIT            = -1;

    /** Папки. Хранилище */
    public static final int FOLDER_STORAGE 	    = 0;

    /** Папки. Кэш */
    public static final int FOLDER_CACHE 		= 1;

    /** Папки. Файлы */
    public static final int FOLDER_FILES 		= 2;

    /** Папки. БД */
    public static final int FOLDER_DATABASE 	= 3;

    /** Псевдоним для LayoutParams.MATCH_PARENT */
    public static final int MATCH               = android.view.ViewGroup.LayoutParams.MATCH_PARENT;

    /** Псевдоним для LayoutParams.WRAP_CONTENT */
    public static final int WRAP                = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

    /** Псевдоним для android.R.id.button1 */
    public static final int BTN_OK              = android.R.id.button1;

    /** Псевдоним для android.R.id.button2 */
    public static final int BTN_NO              = android.R.id.button2;

    /** Псевдоним для android.R.id.button3 */
    public static final int BTN_DEF             = android.R.id.button3;

    /** Псевдонимы для состояний элементов. Нажато */
    public static final int STATE_PRESSED 	    = android.R.attr.state_pressed;

    /** Псевдонимы для состояний элементов. Подсвечено */
    public static final int STATE_HOVERED 	    = android.R.attr.state_hovered;

    /** Псевдонимы для состояний элементов. В фокусе */
    public static final int STATE_FOCUSED 	    = android.R.attr.state_focused;

    /** Псевдонимы для состояний элементов. Активно */
    public static final int STATE_ACTIVATED 	= android.R.attr.state_activated;

    /** Псевдонимы для состояний элементов. Выбрано */
    public static final int STATE_SELECTED 	    = android.R.attr.state_selected;

    /** Псевдонимы для состояний элементов. Выделено */
    public static final int STATE_CHECKED 	    = android.R.attr.state_checked;

    /** Псевдонимы для состояний элементов. Доступно */
    public static final int STATE_ENABLED 	    = android.R.attr.state_enabled;

    /** Псевдонимы для состояний элементов. Заблокировано */
    public static final int STATE_DISABLED 	    = -1;

    /** Флаг кнопки контроллера. Без операции */
    public static final int DIRN 				= 0x00;

    /** Флаг кнопки контроллера. Огонь */
    public static final int DIR0                = 0x01;

    /** Флаг кнопки контроллера. Вверх */
    public static final int DIRU 				= 0x02;

    /** Флаг кнопки контроллера. Вниз */
    public static final int DIRD 				= 0x04;

    /** Флаг кнопки контроллера. Влево */
    public static final int DIRL 				= 0x08;

    /** Флаг кнопки контроллера. Вправо */
    public static final int DIRR 				= 0x10;

    /** Флаг горизонтальных направлений */
    public static final int DIRH 				= DIRR | DIRL;

    /** Флаг горизонтальных направлений */
    public static final int DIRV 				= DIRU | DIRD;

    /** Флаг кнопки контроллера. Вверх и вправо */
    public static final int DIRUR               = DIRU | DIRR;

    /** Флаг кнопки контроллера. Вверх и влево */
    public static final int DIRUL               = DIRU | DIRL;

    /** Флаг кнопки контроллера. Вниз и вправо */
    public static final int DIRDR               = DIRD | DIRR;

    /** Флаг кнопки контроллера. Вниз и влево */
    public static final int DIRDL               = DIRD | DIRL;

    /** Флаги при старте активити. Игра */
    public static final int SSH_APP_MODE_GAME       = 1;

    /** Флаги при старте активити. Показывать заголовок */
    public static final int SSH_APP_MODE_TITLE      = 2;

    /** Флаги при старте активити. Скрывать область уведомлений */
    public static final int SSH_APP_MODE_FULLSCREEN = 4;

    /** Тип выравнивания тайла. Слева от текста */
    public static final int TILE_GRAVITY_LEFT       = 128;

    /** Тип выравнивания тайла. Выше текста */
    public static final int TILE_GRAVITY_UP         = 256;

    /** Тип выравнивания тайла. Справа от текста */
    public static final int TILE_GRAVITY_RIGHT      = 512;

    /** Тип выравнивания тайла. Ниже текста */
    public static final int TILE_GRAVITY_DOWN       = 1024;

    /** Тип выравнивания тайла. Внутри текста */
    public static final int TILE_GRAVITY_BACKGROUND = 2048;

    /** Тип выравнивания тайла. По левой границе */
    public static final int TILE_GRAVITY_START      = 1;

    /** Тип выравнивания тайла. По правой границе */
    public static final int TILE_GRAVITY_END        = 3;

    /** Тип выравнивания тайла. По горизонтальному цетру */
    public static final int TILE_GRAVITY_CENTER_HORZ= 5;

    /** Тип выравнивания тайла. По верхней границе */
    public static final int TILE_GRAVITY_TOP	    = 24;

    /** Тип выравнивания тайла. По нижней границе */
    public static final int TILE_GRAVITY_BOTTOM	    = 40;

    /** Тип выравнивания тайла. По вертикальному центру */
    public static final int TILE_GRAVITY_CENTER_VERT= 72;

    /** Тип выравнивания тайла. Маска для центральных значений */
    public static final int TILE_GRAVITY_CENTER     = TILE_GRAVITY_CENTER_HORZ | TILE_GRAVITY_CENTER_VERT;

    /** Тип выравнивания тайла. Маска для вертикальных значений */
    public static final int TILE_GRAVITY_MASK_VERT  = 120;

    /** Тип выравнивания тайла. Маска для горизонтальных значений */
    public static final int TILE_GRAVITY_MASK_HORZ  = 7;

    /** Фигура. Без фигуры */
    public static final int TILE_SHAPE_EMPTY	    = 0;

    /** Фигура. Прямоугольник */
    public static final int TILE_SHAPE_RECT		    = 1;

    /** Фигура. Овал */
    public static final int TILE_SHAPE_OVAL		    = 2;

    /** Фигура. Окружность */
    public static final int TILE_SHAPE_CIRCLE	    = 3;

    /** Фигура. Скругленный прямоугольник */
    public static final int TILE_SHAPE_ROUND	    = 4;

    /** Стиль нажатия. Без нажатия */
    public static final int TILE_STATE_NONE         = 0;

    /** Стиль нажатия. Тень */
    public static final int TILE_STATE_SHADOW       = 1;

    /** Стиль нажатия. Подстветка */
    public static final int TILE_STATE_HOVER        = 2;

    /** Стиль нажатия. Нажатие */
    public static final int TILE_STATE_PRESS        = 4;

    /** Масштабирование. Вписать */
    public static final int TILE_SCALE_NONE		    = 0;

    /** Масштабирование. Как есть */
    public static final int TILE_SCALE_TILE		    = 1;

    /** Масштабирование. По минимальной величине */
    public static final int TILE_SCALE_MIN          = 2;

    /** Масштабирование. По высоте */
    public static final int TILE_SCALE_HEIGHT	    = 3;

    /** Масштабирование. По ширине */
    public static final int TILE_SCALE_WIDTH	    = 4;

    /** Режим растягивания. Без растягивания */
    public static final int TABLE_STRETCH_NO         = 0;

    /** Режим растягивания. Пространство между ячейками */
    public static final int TABLE_STRETCH_SPACING    = 1;

    /** Режим растягивания. Растягивать ячейки */
    public static final int TABLE_STRETCH_CELL       = 2;

    /** Режим растягивания. Пространство между ячейками. Расстояние одинаковое */
    public static final int TABLE_STRETCH_UNIFORM    = 3;

    /** Прокрутка. Горизонтальная */
    public static final int SCROLLBARS_HORZ         = 0x00000100;

    /** Прокрутка. Вертикальная */
    public static final int SCROLLBARS_VERT         = 0x00000200;

    /** Тип анимации ползунка. Без анимации */
    public static final int SEEK_ANIM_NONE		    = 0;

    /** Тип анимации ползунка. Ротация */
    public static final int SEEK_ANIM_ROTATE        = 1;

    /** Тип анимации ползунка. Масштабирование */
    public static final int SEEK_ANIM_SCALE         = 2;

    /** Тип диаграммы/прогресса. Диаграмма */
    public static final int SSH_MODE_DIAGRAM	    = 0;

    /** Тип диаграммы/прогресса. Круговая */
    public static final int SSH_MODE_CIRCULAR	    = 1;

    /** Начальный хэш код для HTML тега "h1' */
    public static final int HTML_TAG_H1 		    = 3273;

    /** Ключь для установки тега ширины */
    public static final int HTML_KEY_WIDTH          = 0xffff0010;

    /** Метод ввода в всплывающем окне. Из фокуса */
    public static final int METHOD_FOCUSABLE        = ListPopupWindow.INPUT_METHOD_FROM_FOCUSABLE;

    /** Метод ввода в всплывающем окне. Требуется */
    public static final int METHOD_NEEDED           = ListPopupWindow.INPUT_METHOD_NEEDED;

    /** Метод ввода в всплывающем окне. Не требуется */
    public static final int METHOD_NOT_NEEDED       = ListPopupWindow.INPUT_METHOD_NOT_NEEDED;

    /** Флинг. Непосредственный режим */
    public static final int FLING_FLING             = 0;

    /** Флинг. Режим завершеия */
    public static final int FLING_FINISH            = 1;

    /** Флинг. Режим оверскролла */
    public static final int FLING_OVERFLING         = 2;

    /*Флинг. Режим прокрутки */
    public static final int FLING_SCROLL            = 3;

    /** DDL оператор */
    public static final int SQL_DDL_OPS             = 0;

    /** DML оператор UPDATE */
    public static final int SQL_DML_UPDATE          = 1;

    /** DML оператор INSERT INTO */
    public static final int SQL_DML_INSERT          = 2;

    /** DML оператор DELETE FROM */
    public static final int SQL_DML_DELETE          = 3;

    /** DML оператор SELECT */
    public static final int SQL_DML_SELECT          = 4;

    /** Операции с внешним ключем. Удаление/обновление из зависимых таблиц */
    public static final String SQL_RULE_CASCADE     = "CASCADE";

    /** Операции с внешним ключем. Установка в значение NULL */
    public static final String SQL_RULE_SET_NULL    = "SET NULL";

    /** Операции с внешним ключем. Запрещение операции */
    public static final String SQL_RULE_RESTRICT    = "RESTRICT";

    /** Операция SelectJoin. Внутреннее объединение с предикатом */
    public static final String DML_JOIN_INNER       = "INNER";

    /** Операция SelectJoin. Левостороннее внешнее объединение с предикатом */
    public static final String DML_JOIN_LEFT        = "OUTER LEFT";

    /** Операция SelectJoin. CROSS объединение без предиката */
    public static final String DML_JOIN_CROSS       = "CROSS";

    /** IN (SELECT ...) */
    public static final String DML_SUB_SELECT_IN    = "IN";

    /** NOT IN (SELECT ...) */
    public static final String DML_SUB_SELECT_NOT_IN= "NOT IN";

    /** EXISTS (SELECT ...) */
    public static final String DML_SUB_SELECT_EXISTS= "EXISTS";

    /** NOT EXISTS (SELECT ...) */
    public static final String DML_SUB_SELECT_NOT_EXISTS= "NOT EXISTS";

    /** Признак NOT NULL ограничения поля */
    public static final int DDL_FIELD_NOT_NULL      = 1;

    /** Признак AUTOINCREMENT ограничения поля */
    public static final int DDL_FIELD_AUTO_INCREMENT= 2;

    /** Признак UNIQUE ограничения поля */
    public static final int DDL_FIELD_UNIQUE        = 4;

    /** Тип поля. NULL */
    public static final int SQL_FIELD_TYPE_NULL     = 0;

    /** Тип поля. Вещественное DOUBLE */
    public static final int SQL_FIELD_TYPE_REAL     = 1;

    /** Тип поля. Строковое STRING */
    public static final int SQL_FIELD_TYPE_TEXT     = 2;

    /** Тип поля. Байтовый массив BYTE[] */
    public static final int SQL_FIELD_TYPE_BLOB     = 3;

    /** Тип поля. Целое INT */
    public static final int SQL_FIELD_TYPE_INTEGER  = 4;

    /** Тип поля. Лата/Время LONG */
    public static final int SQL_FIELD_TYPE_TIMESTAMP= 5;

    /** JSON лексема. Запятая */
    public static final int JSON_COMMA              = 0;

    /** JSON лексема. Двоеточие */
    public static final int JSON_COLON              = 1;

    /** JSON лексема. { */
    public static final int JSON_LBRACE             = 2;

    /** JSON лексема. } */
    public static final int JSON_RBRACE             = 3;

    /** JSON лексема. [ */
    public static final int JSON_LBRACKET           = 4;

    /** JSON лексема. ] */
    public static final int JSON_RBRACKET           = 5;

    /** JSON лексема. Кавычка */
    public static final int JSON_KAV                = 6;

    /** JSON лексема. Конец строки */
    public static final int JSON_EOF                = 7;

    /** JSON лексема. Цифра */
    public static final int JSON_DIGIT              = 8;

    /** JSON лексема. Точка */
    public static final int JSON_DOT                = 9;

    /** JSON лексема. Буква */
    public static final int JSON_LETTER             = 10;

    /** JSON лексема. Недопустимый символ */
    public static final int JSON_UNDEF              = 11;

    /** JSON лексема. Эскейп последовательность*/
    public static final int JSON_ESCAPE             = 12;

    /** Флаги касания. Фиксация нажатия */
    public static final int TOUCH_PRESSED           = 0x00000001;

    /** Флаги касания. Двойной клик */
    public static final int TOUCH_DOUBLE_CLICKED    = 0x00000002;

    /** Флаги касания. Отпуск нажатия */
    public static final int TOUCH_UNPRESSED         = 0x00000004;

    /** Флаги касания. Инит двойного клика */
    public static final int TOUCH_INIT_DOUBLE_PRESSED= 0x00000008;

    /** Относительные размеры HTML подзаголовков */
    public static final float[] htmlHeaderSize      = {1.7f, 1.6f, 1.5f, 1.4f, 1.3f, 1.2f};

    /** Символы шестандцатиричной системы счисления */
    public static final char[] hexChars             = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /*Восемь направлений, в зависимости от угла */
    public static final int[] dirs8                 = {DIRL, DIR0, DIRL, DIRL | DIRU, DIRU, DIRR | DIRU, DIRR, DIRR | DIRD, DIRD, DIRL | DIRD};

    /** Диапазоны углов для определения восьми направлений */
    public static final int[] aranges               = {338, 360, 0, 23, 68, 113, 158, 203, 248, 293, 338};

    /** Массив некорректных символов для имени */
    public static final char[] charsUncorrect       = {',', '.', '/', '\\', '\'', '\"', '?', '>', '<', '|', '&', '%', '^', ':', '?', '*', ';'};

    /** Хэши ключевых слов SQL */
    public static final int[] sqlKeywords           = {92611376, -1422950858, 96417, 92734940, 96673, 92913686, -864330420, 96727, 3122, 96881, -1407259067,
                                                       -143285600, -1392885889, 93616297, -216634360, 3159, 554829492, 3046192, 3046207, 94627080, 949441574,
                                                       -1354837162, -1354815177, -580047918, -190376483, -1352294148, 94935104, 1468427956, 1468912083,
                                                       1812340176, 1789464955, 1544803905, -147210086, 647890911, -1335458389, 3079825, -1335224429, 288698108,
                                                       3092207, 3105281, 3116345, 100571, -1294172031, -1289550567, 1686617758, -1289358244, -1309162249, 3135262,
                                                       101577, -677674796, 3151786, 3154575, 3175800, 98629247, -1224334299, 3357, -1190396462, 1124382641,
                                                       3365, 100346066, 1943292145, -248994863, 100355670, -1183792455, 1957573442, 503014687, 3237472, 3370,
                                                       -1179308623, 3267882, 106079, 3317767, 3321751, 102976443, 103668165, 1728911401, 3521, 109267,
                                                       2129514202, 3392903, 3543, -1019779949, 3551, 3555, 106006350, 106111099, 3443497, -980228804,
                                                       -314765822, 107944136, 108275692, 1165780018, 1384950408, -934799095, 1088094847, 1090594823,
                                                       -934594754, 1094496948, -336545092, 108511772, -259719452, 113114, 199686707, -906021636, 113762,
                                                       110115790, 3556308, 1984986705, 3558941, 3707, 2141246174, -1059891784, 111433423, -840528943,
                                                       -838846263, 111582340, -824080459, -823812830, 3619493, 466165515, 3648314, 113097959, 3649734, 1355153608};

    /** Направления градиентной заливки */
    public static final GradientDrawable.Orientation[] gradient= {GradientDrawable.Orientation.BOTTOM_TOP,
                                                                  GradientDrawable.Orientation.BOTTOM_TOP,
                                                                  GradientDrawable.Orientation.BOTTOM_TOP,
                                                                  GradientDrawable.Orientation.BOTTOM_TOP,
                                                                  GradientDrawable.Orientation.TOP_BOTTOM,
                                                                  GradientDrawable.Orientation.BOTTOM_TOP,
                                                                  GradientDrawable.Orientation.BOTTOM_TOP,
                                                                  GradientDrawable.Orientation.BOTTOM_TOP,
                                                                  GradientDrawable.Orientation.RIGHT_LEFT,
                                                                  GradientDrawable.Orientation.BOTTOM_TOP,
                                                                  GradientDrawable.Orientation.BR_TL,
                                                                  GradientDrawable.Orientation.BOTTOM_TOP,
                                                                  GradientDrawable.Orientation.TR_BL,
                                                                  GradientDrawable.Orientation.BOTTOM_TOP,
                                                                  GradientDrawable.Orientation.BOTTOM_TOP,
                                                                  GradientDrawable.Orientation.BOTTOM_TOP,
                                                                  GradientDrawable.Orientation.LEFT_RIGHT,
                                                                  GradientDrawable.Orientation.BOTTOM_TOP,
                                                                  GradientDrawable.Orientation.BL_TR,
                                                                  GradientDrawable.Orientation.BOTTOM_TOP,
                                                                  GradientDrawable.Orientation.TL_BR};

    /** Патч9. Левая сторона */
    public static final byte VL                     = 1;

    /** Патч9. Верхняя сторона */
    public static final byte VT                     = 2;

    /** Патч9. Правая сторона */
    public static final byte VR                     = 3;

    /** Патч9. Нижняя сторона */
    public static final byte VB                     = 4;

    /** Карта для патч9 */
    public static final byte[] mapPatch             = { VL,  0, VT,  0, VL,  1, VT,  1,// LEFT_TOP
                                                        VR, -1, VT,  0, VR,  0, VT,  1,// RIGHT_TOP
                                                        VL,  0, VB, -1, VL,  1, VB,  0,// LEFT_BOTTOM
                                                        VR, -1, VB, -1, VR,  0, VB,  0,// RIGHT_BOTTOM
                                                        VL,  0, VT,  1, VL,  1, VB, -1,// LEFT
                                                        VR, -1, VT,  1, VR,  0, VB, -1,// RIGHT
                                                        VL,  1, VT,  0, VR, -1, VT,  1,// TOP
                                                        VL,  1, VB, -1, VR, -1, VB,  0,// BOTTOM
                                                        VL,  1, VT,  1, VR, -1, VB, -1 // CENTER
    };

    /** Символы карты контроллера */
    public static final char[] controllerCharsMap   = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ' };

    /** Карта контроллера по умолчанию 8x8 */
    public static final String mapController        =   "1313111111111414\n" +
                                                        "1313111111111414\n" +
                                                        "3333131010144444\n" +
                                                        "3333300000404444\n" +
                                                        "3333300000404444\n" +
                                                        "3333232020244444\n" +
                                                        "2323222222222424\n" +
                                                        "2323222222222424";

    /** Текст тега */
    public static final int HTML_TEXT_TAG           = 0;

    /** Открывающий тег */
    public static final int HTML_OPEN_TAG           = 1;

    /** Закрывающий тег*/
    public static final int HTML_CLOSE_TAG          = 2;

    /** Закрывающий тег при пробросе */
    public static final int HTML_CLOSE_WRONG_TAG    = 3;

    /** Константа для автоматического определения количества строк или столбцов */
    public static final int CELL_LAYOUT_AUTO_FIT    = -1;

    /** Константа для добавления ячейки в начало */
    public static final int CELL_LAYOUT_INSERT_BEGIN= -100;

    /** Константа для добавления ячейки в конец */
    public static final int CELL_LAYOUT_INSERT_END  = -200;

    /** Свойство атрибута. Целое  */
    public static final int ATTR_INT              = 0x00800000;

    /** Свойство атрибута. Вещественное  */
    public static final int ATTR_FLT              = 0x00400000;

    /** Свойство атрибута. Строка  */
    public static final int ATTR_STR              = 0x00200000;

    /** Свойство атрибута. Логическое  */
    public static final int ATTR_BOL              = 0x00100000;

    /** Свойство атрибута. dimen  */
    public static final int ATTR_DMN              = 0x00080000;

    /** Свойство атрибута. Drawable  */
    public static final int ATTR_DRW              = 0x00040000;

    /** Типы атрибута. Значение из текущей темы  */
    public static final int THEME                 = 0x80000000;

    /** Типы атрибута. Цвет  */
    public static final int COLOR                 = 0x40000000;

    /** Типы атрибута. SP для шрифта  */
    public static final int SP_FONT               = 0x40000000;

    /** Типы атрибута. DP из ресурсов  */
    public static final int ID_RES                = 0x70000000;

    /** Маска значения атрибута  */
    public static final int ATTR_VALUE_MSK        = 0x0fffffff;

    /** Маска свойств значения атрибута  */
    public static final int ATTR_APROPS_MSK       = 0x00fc0000;

    /** Маска свойств типа атрибута  */
    public static final int ATTR_VPROPS_MSK       = -0x10000000;

    /** Маска индекса атрибута  */
    public static final int ATTR_ATTR_MSK         = 0x0000ffff;

// Текстовые атрибуты стилей
    /** Цвет текста по умолчанию */
    public static final int ATTR_COLOR_DEFAULT        = 0 | ATTR_INT;
    /** Цвет подсказки поля ввода */
    public static final int ATTR_COLOR_HINT           = 1 | ATTR_INT;
    /** Цвет ссылки в html */
    public static final int ATTR_COLOR_LINK           = 2 | ATTR_INT;
    /** Цвет подсвеченного текста */
    public static final int ATTR_COLOR_HIGHLIGHT      = 3 | ATTR_INT;
    /** Размер шрифта */
    public static final int ATTR_SIZE                 = 4 | ATTR_DMN;
    /** Шрифт */
    public static final int ATTR_FONT                 = 5 | ATTR_STR;
    /** Стиль начертания текста */
    public static final int ATTR_STYLE                = 6 | ATTR_INT;
    /** Выравнивание текста */
    public static final int ATTR_TEXT_ALIGN           = 7 | ATTR_INT;
    /** Тип клавиатуры при вводе текста в поле ввода */
    public static final int ATTR_IME_OPTIONS          = 8 | ATTR_INT;
    /** Тип вводимого текста в поле ввода (текст, цифра и тд.) */
    public static final int ATTR_INPUT_TYPE           = 9 | ATTR_INT;
    /** Максимальная длина текста в поле ввода */
    public static final int ATTR_MAX_LENGTH           = 10 | ATTR_INT;
    /** Параметры тени текста */
    public static final int ATTR_SHADOW_TEXT          = 11 | ATTR_STR;
    /** Максимальное количество строк в тексте */
    public static final int ATTR_MAX_LINES            = 12 | ATTR_INT;

// Стандартные атрибуты стилей
    /** Внутренний отступ */
    public static final int ATTR_PADDING              = 50 | ATTR_DMN;
    /** Внутренний горизонтальный отступ */
    public static final int ATTR_PADDING_HORZ         = 51 | ATTR_DMN;
    /** Внутренний вертикальный отступ */
    public static final int ATTR_PADDING_VERT         = 52 | ATTR_DMN;
    /** Внутренний левый отступ */
    public static final int ATTR_PADDING_LEFT         = 53 | ATTR_DMN;
    /** Внутренний правый отступ */
    public static final int ATTR_PADDING_RIGHT        = 54 | ATTR_DMN;
    /** Внутренний верхний отступ */
    public static final int ATTR_PADDING_TOP          = 55 | ATTR_DMN;
    /** Внутренний нижний отступ */
    public static final int ATTR_PADDING_BOTTOM       = 56 | ATTR_DMN;
    /** Признак возможности клика на представлении */
    public static final int ATTR_CLICKABLE            = 57 | ATTR_BOL;
    /** Признак получения фокуса */
    public static final int ATTR_FOCUSABLE            = 58 | ATTR_BOL;
    /** Режим отображение представления */
    public static final int ATTR_VISIBILITY           = 59 | ATTR_INT;
    /** Гравитация представления */
    public static final int ATTR_GRAVITY              = 60 | ATTR_INT;
    /** Минимальная высота представления */
    public static final int ATTR_MIN_HEIGHT           = 61 | ATTR_DMN;
    /** Минимальная ширина представления */
    public static final int ATTR_MIN_WIDTH            = 62 | ATTR_DMN;
    /** Максимальная высота представления */
    public static final int ATTR_MAX_HEIGHT           = 63 | ATTR_DMN;
    /** Максимальная ширина представления */
    public static final int ATTR_MAX_WIDTH            = 64 | ATTR_DMN;
    /** Признак доступности представления */
    public static final int ATTR_ENABLED              = 65 | ATTR_BOL;
    /** Виды прокруток у представления */
    public static final int ATTR_SCROLLBARS           = 66 | ATTR_INT;
    /** Вид отбрасывания тени у представления */
    public static final int ATTR_FADING_EDGE          = 67 | ATTR_INT;
    /** Расстояние между ячейками в Table */
    public static final int ATTR_SPACING_CELL         = 68 | ATTR_DMN;
    /** Расстояние между строками в Table */
    public static final int ATTR_SPACING_LINE         = 69 | ATTR_DMN;
    /** Размер ячейки */
    public static final int ATTR_CELL_SIZE            = 70 | ATTR_DMN;
    /**  Количество ячеек в Table */
    public static final int ATTR_CELL_NUM             = 71 | ATTR_INT;
    /** Режим отображения ячеек в Table */
    public static final int ATTR_STRETCH_MODE         = 72 | ATTR_INT;
    /** Фон представления */
    public static final int ATTR_BACKGROUND           = 73 | ATTR_DRW;
    /** Признак длинного клика на представлении */
    public static final int ATTR_LONG_CLICKABLE       = 74 | ATTR_BOL;
    /** Селектор */
    public static final int ATTR_SELECTOR             = 75 | ATTR_DRW;
    /** Разделитель */
    public static final int ATTR_DIVIDER              = 76 | ATTR_DRW;
    /** Размер разделителя */
    public static final int ATTR_DIVIDER_SIZE         = 77 | ATTR_DMN;
    /** Признак того, что  представление было выбрано */
    public static final int ATTR_CHECKED              = 78 | ATTR_BOL;
    /** Признак получения фокуса ввода при касании на виджете (поле ввода) */
    public static final int ATTR_FOCUSABLE_TOUCH_MODE = 79 | ATTR_BOL;

// Библиотечные атрибуты стилей, Доступ к картинкам
    /** Картинка для тайлов иконок */
    public static final int ATTR_SSH_BM_ICONS         = 100 | ATTR_DRW;
    /** Картинка для базовых тайлов */
    public static final int ATTR_SSH_BM_TILES         = 101 | ATTR_DRW;
    /** Картинка для фона форм */
    public static final int ATTR_SSH_BM_BACKGROUND    = 102 | ATTR_DRW;
    /** Картинка для заголовка форм/диалогов */
    public static final int ATTR_SSH_BM_HEADER        = 103 | ATTR_DRW;
    /** Картинка для кнопок */
    public static final int ATTR_SSH_BM_BUTTONS       = 104 | ATTR_DRW;
    /** Картинка для инструментальных кнопок */
    public static final int ATTR_SSH_BM_TOOLS         = 105 | ATTR_DRW;
    /** Картинка для радио кнопок */
    public static final int ATTR_SSH_BM_RADIO         = 106 | ATTR_DRW;
    /** Картинка для флажков */
    public static final int ATTR_SSH_BM_CHECK         = 107 | ATTR_DRW;
    /** Картинка для спиннера */
    public static final int ATTR_SSH_BM_SPINNER       = 108 | ATTR_DRW;
    /** Картинка для переключателя */
    public static final int ATTR_SSH_BM_SWITCH        = 109 | ATTR_DRW;
    /** Картинка для поля ввода */
    public static final int ATTR_SSH_BM_EDIT          = 110 | ATTR_DRW;
    /** Картинка для слайдера */
    public static final int ATTR_SSH_BM_SEEK          = 111 | ATTR_DRW;
    /** Картинка для главного экрана приложения */
    public static final int ATTR_SSH_BM_MENU          = 112 | ATTR_DRW;

// Библиотечные атрибуты стилей, Цвета
    /** Цвет обычного текста */
    public static final int ATTR_SSH_COLOR_NORMAL     = 150 | ATTR_INT;
    /** Цвет маленького текста */
    public static final int ATTR_SSH_COLOR_SMALL      = 151 | ATTR_INT;
    /** Цвет большого текста */
    public static final int ATTR_SSH_COLOR_LARGE      = 152 | ATTR_INT;
    /** Цвет html ссылки */
    public static final int ATTR_SSH_COLOR_LINK       = 153 | ATTR_INT;
    /** Цвет подсказки в поле ввода */
    public static final int ATTR_SSH_COLOR_HINT       = 154 | ATTR_INT;
    /** Цвет текста заголовка формы/диалога */
    public static final int ATTR_SSH_COLOR_HEADER     = 155 | ATTR_INT;
    /** Цвет фона разметки */
    public static final int ATTR_SSH_COLOR_LAYOUT     = 156 | ATTR_INT;
    /** Цвет отладочной сетки */
    public static final int ATTR_SSH_COLOR_WIRED      = 157 | ATTR_INT;
    /** Цвет разделителя */
    public static final int ATTR_SSH_COLOR_DIVIDER    = 158 | ATTR_INT;
    /** Цвет сообщений */
    public static final int ATTR_SSH_COLOR_MESSAGE    = 159 | ATTR_INT;
    /** Цвет окон */
    public static final int ATTR_SSH_COLOR_WINDOW     = 160 | ATTR_INT;
    /** Цвет html заголовков */
    public static final int ATTR_SSH_COLOR_HTML_HEADER= 161 | ATTR_INT;
    /** Цвет селектора */
    public static final int ATTR_SSH_COLOR_SELECTOR   = 162 | ATTR_INT;

// Библиотечные атрибуты стилей, Виджеты
    /** Состояние виджета */
    public static final int ATTR_SSH_STATES               = 200 | ATTR_INT;
    /** Ограничительная фигура виджета */
    public static final int ATTR_SSH_SHAPE                = 201 | ATTR_INT;
    /** Градиентная заливка фона виджета */
    public static final int ATTR_SSH_GRADIENT             = 202 | ATTR_STR;
    /** Сплошной цвет заливки фона виджета */
    public static final int ATTR_SSH_SOLID                = 203 | ATTR_INT;
    /** Признак отображения отладочной сетки/Текста */
    public static final int ATTR_SSH_SHOW                 = 204 | ATTR_BOL;
    /** Реальная ширина ползунка переключателя */
    public static final int ATTR_SSH_THUMB_WIDTH          = 205 | ATTR_INT;
    /** Щирина селектора */
    public static final int ATTR_SSH_WIDTH_SELECTOR       = 206 | ATTR_DMN;
    /** Направление градиентной заливки фона виджета */
    public static final int ATTR_SSH_GRADIENT_DIR         = 207 | ATTR_INT;
    /** Гравитация виджета */
    public static final int ATTR_SSH_GRAVITY              = 208 | ATTR_INT;
    /** Имя картинки виджета */
    public static final int ATTR_SSH_BITMAP_NAME          = 210 | ATTR_DRW;
    /** Тип анимации ползунка слайдера */
    public static final int ATTR_SSH_SEEK_ANIM            = 211 | ATTR_INT;
    /** Ширина контроллера */
    public static final int ATTR_SSH_CONTROLLER_WIDTH     = 212 | ATTR_DMN;
    /** Высота контроллера */
    public static final int ATTR_SSH_CONTROLLER_HEIGHT    = 213 | ATTR_DMN;
    /** Альфа виджета */
    public static final int ATTR_SSH_ALPHA                = 214 | ATTR_INT;
    /** Патч9 для виджета */
    public static final int ATTR_SSH_PATCH9               = 216 | ATTR_STR;
    /** Массив углов для скругленного прямоугольника виджета */
    public static final int ATTR_SSH_RADII                = 217 | ATTR_STR;
    /** Задержка в анимации */
    public static final int ATTR_SSH_ANIMATOR_DURATION    = 218 | ATTR_INT;
    /** Массив цветов(начальный..конечный) сегментов диаграммы */
    public static final int ATTR_SSH_COLORS               = 219 | ATTR_STR;
    /** Начальный угол первого сегмента круговой диаграммы */
    public static final int ATTR_SSH_CHART_BEGIN_ANGLE    = 220 | ATTR_FLT;
    /** Радиус, в процентах, выделенных сегментов круговой диаграммы */
    public static final int ATTR_SSH_CHART_CHECK_RADIUS   = 221 | ATTR_INT;
    /** Величина, в процентах, внутреннего радиуса круговой диаграммы */
    public static final int ATTR_SSH_CHART_INNER_RADIUS   = 222 | ATTR_INT;
    /** Смещение тени/нажатия виджета */
    public static final int ATTR_SSH_PRESSED_OFFS         = 223 | ATTR_FLT;
    /** Количество тайлов по горизонтали в картинке виджета */
    public static final int ATTR_SSH_HORZ                 = 224 | ATTR_INT;
    /** Количество тайлов по вертикали в картинке виджета */
    public static final int ATTR_SSH_VERT                 = 225 | ATTR_INT;
    /** Номер тайла по умолчанию в картинке виджета */
    public static final int ATTR_SSH_TILE                 = 226 | ATTR_INT;
    /** Номер тайла иконки виджета */
    public static final int ATTR_SSH_ICON                 = 227 | ATTR_INT;
    /** Тип масштабирования виджета */
    public static final int ATTR_SSH_SCALE                = 228 | ATTR_INT;
    /** Смещение по вертикали при отображение выпадающего списка спиннера */
    public static final int ATTR_SSH_DROPDOWN_VERT_OFFS   = 220 | ATTR_DMN;
    /** Смещение по горизонтали при отображение выпадающего списка спиннера */
    public static final int ATTR_SSH_DROPDOWN_HORZ_OFFS   = 230 | ATTR_DMN;
    /** Ширина выпадающего списка спиннера */
    public static final int ATTR_SSH_DROPDOWN_WIDTH       = 231 | ATTR_INT;
    /** Имя темы */
    public static final int ATTR_SSH_THEME_NAME           = 232 | ATTR_STR;
    /** Фон рисунка виджета */
    public static final int ATTR_SSH_BACKGROUND           = 233 | ATTR_DRW;
    /** Коеффицент масштабирования иконки относительно размеров виджета */
    public static final int ATTR_SSH_SCALE_ICON           = 234 | ATTR_INT;
    /** Тип выравнивания иконки относительно виджета */
    public static final int ATTR_SSH_GRAVITY_ICON         = 235 | ATTR_INT;
    /** Размер селектора вкладки */
    public static final int ATTR_SSH_SIZE_SELECTOR_TAB    = 236 | ATTR_INT;
    /** Размер селектора активно вкладки */
    public static final int ATTR_SSH_SIZE_SELECTOR_SEL_TAB= 237 | ATTR_INT;
    /** Количество иконок тайлов по вертикали */
    public static final int ATTR_SSH_ICON_VERT            = 238 | ATTR_INT;
    /** Количество иконок тайлов по горизонтали */
    public static final int ATTR_SSH_ICON_HORZ            = 239 | ATTR_INT;

    /** Стиль по умолчанию для Большого текста */
    public static final int[] style_text_large            = {
            ATTR_SHADOW_TEXT, R.string.shadow_text,
            ATTR_GRAVITY, Gravity.CENTER_VERTICAL,
            ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_LARGE | THEME,
            ATTR_MAX_LINES, 1,
            ATTR_SIZE, R.dimen.large,
            ATTR_FONT, R.string.font_large};

    /** Стиль по умолчанию для Обычного текста */
    public static final int[] style_text_normal           = {
            ATTR_SHADOW_TEXT, R.string.shadow_text,
            ATTR_GRAVITY, Gravity.CENTER_VERTICAL,
            ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_NORMAL | THEME,
            ATTR_MAX_LINES, 1,
            ATTR_SIZE, R.dimen.normal,
            ATTR_FONT, R.string.font_normal};

    /** Стиль по умолчанию для Маленького текста */
    public static final int[] style_text_small            = {
            ATTR_SHADOW_TEXT, R.string.shadow_text,
            ATTR_GRAVITY, Gravity.CENTER_VERTICAL,
            ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_SMALL | THEME,
            ATTR_MAX_LINES, 1,
            ATTR_SIZE, R.dimen.small,
            ATTR_FONT, R.string.font_small
    };

    /** Стиль по умолчанию для Заголовка форма/диалога */
    public static final int[] style_text_header           = {
            ATTR_SHADOW_TEXT, R.string.shadow_text,
            ATTR_GRAVITY, Gravity.CENTER,
            ATTR_STYLE, Typeface.BOLD_ITALIC,
            ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_HEADER | THEME,
            ATTR_SIZE, R.dimen.header,
            ATTR_PADDING, 1,
            ATTR_SSH_TILE, 0,
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_HEADER | THEME,
            ATTR_SSH_PATCH9, R.string.patch9_header,
            ATTR_FONT, R.string.font_large,
            ATTR_MIN_HEIGHT, R.dimen.heightHeader
    };

    /** Стиль по умолчанию для Текста подсказки в поле ввода */
    public static final int[] style_text_hint             = {
            ATTR_SHADOW_TEXT, R.string.shadow_text,
            ATTR_GRAVITY, Gravity.CENTER_VERTICAL,
            ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_HINT | THEME,
            ATTR_SIZE, R.dimen.hint,
            ATTR_FONT, R.string.font_small};

    /** Стиль по умолчанию для HTML текста */
    public static final int[] style_text_html             = {
            ATTR_PADDING, 2,
            ATTR_SHADOW_TEXT, R.string.shadow_text,
            ATTR_COLOR_LINK, 0x00ffff | COLOR,
            ATTR_GRAVITY, Gravity.CENTER,
            ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_NORMAL | THEME,
            ATTR_SIZE, R.dimen.html,
            ATTR_FONT, R.string.font_small
    };

    /** Стиль по умолчанию для Текста в диалоге */
    public static final int[] style_text_dlg              = {
            ATTR_PADDING, R.dimen.paddingDlg,
            ATTR_SHADOW_TEXT, R.string.shadow_text,
            ATTR_GRAVITY, Gravity.CENTER,
            ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_LARGE | THEME,
            ATTR_SIZE, R.dimen.normal,
            ATTR_FONT, R.string.font_normal
    };

    /** Стиль по умолчанию для Тайла */
    public static final int[] style_tile                  = {
            ATTR_SSH_WIDTH_SELECTOR, 0,
            ATTR_SSH_PRESSED_OFFS, 0,
            ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_NORMAL | THEME,
            ATTR_SIZE, R.dimen.normal,
            ATTR_FONT, R.string.font_normal,
            ATTR_CLICKABLE, 0,
            ATTR_FOCUSABLE, 0,
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_TILES | THEME
    };

    /** Стиль по умолчанию для Иконок */
    public static final int[] style_icon                  = {
            ATTR_SSH_PRESSED_OFFS, R.dimen.pressedOffs,
            ATTR_SSH_SCALE, R.integer.scaleIconType,
            ATTR_CLICKABLE, 0,
            ATTR_FOCUSABLE, 0,
            ATTR_SSH_GRAVITY, TILE_GRAVITY_CENTER | TILE_GRAVITY_BACKGROUND,
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_ICONS | THEME,
            ATTR_SSH_SCALE_ICON, R.integer.scaleIcon,
            ATTR_SSH_VERT, ATTR_SSH_ICON_VERT | THEME,
            ATTR_SSH_HORZ, ATTR_SSH_ICON_HORZ | THEME
    };

    /** Стиль по умолчанию для Радио кнопки */
    public static final int[] style_radio                 = {
            ATTR_SHADOW_TEXT, R.string.shadow_text,
            ATTR_STYLE, Typeface.BOLD,
            ATTR_GRAVITY, Gravity.START | Gravity.CENTER_VERTICAL,
            ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_NORMAL | THEME,
            ATTR_SIZE, R.dimen.normal,
            ATTR_FONT, R.string.font_small,
            ATTR_MAX_LINES, 1,
            ATTR_TEXT_ALIGN, TEXT_ALIGNMENT_GRAVITY,
            ATTR_MIN_HEIGHT, R.dimen.heightRadio,
            ATTR_SSH_HORZ, 2, ATTR_SSH_TILE, 0,
            ATTR_SSH_SCALE, TILE_SCALE_MIN,
            ATTR_SSH_GRAVITY, TILE_GRAVITY_LEFT | TILE_GRAVITY_CENTER_VERT,
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_RADIO | THEME
    };

    /** Стиль по умолчанию для Флажка */
    public static final int[] style_check                 = {
            ATTR_SHADOW_TEXT, R.string.shadow_text,
            ATTR_STYLE, Typeface.BOLD,
            ATTR_GRAVITY, Gravity.START | Gravity.CENTER_VERTICAL,
            ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_NORMAL | THEME,
            ATTR_SIZE, R.dimen.normal,
            ATTR_FONT, R.string.font_small,
            ATTR_TEXT_ALIGN, TEXT_ALIGNMENT_GRAVITY,
            ATTR_MIN_HEIGHT, R.dimen.heightCheck,
            ATTR_SSH_HORZ, 2,
            ATTR_SSH_TILE, 0,
            ATTR_MAX_LINES, 1,
            ATTR_SSH_SCALE, TILE_SCALE_MIN,
            ATTR_SSH_GRAVITY, TILE_GRAVITY_LEFT | TILE_GRAVITY_CENTER_VERT,
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_CHECK | THEME
    };

    /** Стиль по умолчанию для Переключателя */
    public static final int[] style_switch                = {
            ATTR_SHADOW_TEXT, R.string.shadow_text,
            ATTR_STYLE, Typeface.BOLD_ITALIC,
            ATTR_MAX_LINES, 1,
            ATTR_GRAVITY, Gravity.START | Gravity.CENTER_VERTICAL,
            ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_NORMAL | THEME,
            ATTR_SIZE, R.dimen.normal,
            ATTR_FONT, R.string.font_small,
            ATTR_TEXT_ALIGN, TEXT_ALIGNMENT_GRAVITY,
            ATTR_MIN_HEIGHT, R.dimen.heightSwitch,
            ATTR_PADDING_HORZ, R.dimen.padHorzSwitch,
            ATTR_SSH_VERT, 2,
            ATTR_SSH_TILE, 0,
            ATTR_SSH_THUMB_WIDTH, 32,
            ATTR_SSH_SCALE, TILE_SCALE_HEIGHT,
            ATTR_SSH_GRAVITY, TILE_GRAVITY_RIGHT | TILE_GRAVITY_CENTER_VERT,
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_SWITCH | THEME
    };

    /** Стиль по умолчанию для Простой диаграммы */
    public static final int[] style_chart_diagram         = {
            ATTR_COLOR_DEFAULT, 0 | COLOR,
            ATTR_SSH_SHOW, 1,
            ATTR_SSH_GRADIENT_DIR, DIRU,
            ATTR_SIZE, R.dimen.chart,
            ATTR_FONT, R.string.font_small
    };

    /** Стиль по умолчанию для Круговой диаграммы */
    public static final int[] style_chart_circular        = {
            ATTR_COLOR_DEFAULT, 0 | COLOR,
            ATTR_SSH_SHOW, 1,
            ATTR_SIZE, R.dimen.chart,
            ATTR_FONT, R.string.font_small,
            ATTR_SSH_CHART_BEGIN_ANGLE, 30,
            ATTR_SSH_CHART_CHECK_RADIUS, 15,
            ATTR_SSH_CHART_INNER_RADIUS, 50
    };

    /** Стиль по умолчанию для Слайдера */
    public static final int[] style_seek                  = {
            ATTR_MIN_HEIGHT, R.dimen.heightSeek,
            ATTR_SSH_VERT, 2,
            ATTR_SSH_TILE, 0,
            ATTR_PADDING_HORZ, 18,
            ATTR_MIN_WIDTH, R.dimen.widthSeek,
            ATTR_SSH_SCALE, TILE_SCALE_MIN,
            ATTR_SSH_GRAVITY, TILE_GRAVITY_LEFT | TILE_GRAVITY_CENTER_VERT,
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_SEEK | THEME,
            ATTR_SSH_SEEK_ANIM, ATTR_SSH_SEEK_ANIM | THEME
    };

    /** Стиль по умолчанию для Поля ввода */
    public static final int[] style_edit                  = {
            ATTR_SHADOW_TEXT, R.string.shadow_text,
            ATTR_GRAVITY, Gravity.CENTER_VERTICAL,
            ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_NORMAL | THEME,
            ATTR_SIZE, R.dimen.edit,
            ATTR_FONT, R.string.font_small,
            ATTR_FOCUSABLE_TOUCH_MODE, 1,
            ATTR_COLOR_HINT, ATTR_SSH_COLOR_HINT | THEME,
            ATTR_FOCUSABLE, 1,
            ATTR_CLICKABLE, 1,
            ATTR_SSH_TILE, 0,
            ATTR_PADDING_HORZ, R.dimen.paddingHorzEdit,
            ATTR_TEXT_ALIGN, TEXT_ALIGNMENT_GRAVITY,
            ATTR_PADDING_VERT, R.dimen.paddingVertEdit,
            ATTR_IME_OPTIONS, IME_FLAG_NO_EXTRACT_UI,
            ATTR_INPUT_TYPE, android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT,
            ATTR_MAX_LENGTH, 15,
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_EDIT | THEME,
            ATTR_SSH_PATCH9, R.string.patch9_edit
    };

    public static final int[] style_editEx              = {
            ATTR_FOCUSABLE, 1,
            ATTR_CLICKABLE, 1,
            ATTR_SSH_TILE, 0,
            ATTR_SSH_HORZ, 1,
            ATTR_SSH_VERT, 1,
            ATTR_PADDING, R.dimen.paddingEditEx,
            ATTR_SSH_GRAVITY, TILE_GRAVITY_BACKGROUND,
            ATTR_SSH_STATES, TILE_STATE_HOVER,
            ATTR_SSH_BITMAP_NAME, R.drawable.edit_ex
    };

    /** Стиль по умолчанию для элемента выпадающего списка Spinner */
    public static final int[] style_spinner_item          = {
            ATTR_SHADOW_TEXT, R.string.shadow_text,
            ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_LARGE | THEME,
            ATTR_COLOR_HIGHLIGHT, ATTR_SSH_COLOR_NORMAL | THEME,
            ATTR_GRAVITY, Gravity.CENTER,
            ATTR_SIZE, R.dimen.normal,
            ATTR_FONT, R.string.font_normal,
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_SPINNER | THEME,
            ATTR_MIN_HEIGHT, R.dimen.heightSpinnerItem,
            ATTR_PADDING_HORZ, R.dimen.paddingHorzSelectItem,
            ATTR_SSH_VERT, 3,
            ATTR_SSH_PATCH9, R.string.patch9_select_item,
            ATTR_SSH_TILE, 2
    };

    /** Стиль по умолчанию для заголовка Spinner */
    public static final int[] style_spinner_title         = {
            ATTR_SHADOW_TEXT, R.string.shadow_text,
            ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_LARGE | THEME,
            ATTR_COLOR_HIGHLIGHT, ATTR_SSH_COLOR_NORMAL | THEME,
            ATTR_PADDING_HORZ, R.dimen.paddingHorzSelectItem,
            ATTR_GRAVITY, Gravity.CENTER,
            ATTR_PADDING_RIGHT, R.dimen.paddingRightSpinnerCaption,
            ATTR_SIZE, R.dimen.large,
            ATTR_FONT, R.string.font_normal,
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_SPINNER | THEME,
            ATTR_SSH_VERT, 3,
            ATTR_SSH_TILE, 0
    };

    /** Стиль по умолчанию для объекта Spinner */
    public static final int[] style_spinner               = {
            ATTR_CLICKABLE, 1,
            ATTR_GRAVITY, Gravity.CENTER,
            ATTR_MIN_HEIGHT, R.dimen.heightSpinnerCaption,
            ATTR_SSH_PATCH9, R.string.patch9_select_caption,
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_SPINNER | THEME,
            ATTR_SSH_VERT, 3,
            ATTR_SSH_TILE, 1,
            ATTR_SSH_DROPDOWN_VERT_OFFS, R.dimen.paddingVertOffsSpinner,
            ATTR_SSH_DROPDOWN_WIDTH, MATCH,
            ATTR_SELECTOR, ATTR_SSH_COLOR_SELECTOR | THEME,
            ATTR_DIVIDER_SIZE, 0
    };

    /** Стиль по умолчанию для Выпадающего списка Spinner*/
    public static final int[] style_spinner_dropdown      = {
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_SPINNER | THEME,
            ATTR_SSH_VERT, 3,
            ATTR_SSH_TILE, 0,
            ATTR_PADDING, 5,
            ATTR_SSH_PATCH9, R.string.patch9_dropdown
    };

    /** Стиль по умолчанию для Progress */
    public static final int[] style_progress              = {
            ATTR_COLOR_DEFAULT, 0xffffff | COLOR,
            ATTR_SSH_SHOW, 1,
            ATTR_SIZE, R.dimen.progress,
            ATTR_SSH_STATES, TILE_STATE_HOVER,
            ATTR_FONT, R.string.font_small
    };

    /** Стиль по умолчанию для Элемента списка */
    public static final int[] style_item                  = {
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_SPINNER | THEME,
            ATTR_SSH_VERT, 3,
            ATTR_SSH_TILE, 2,
            ATTR_SSH_PATCH9, R.string.patch9_item
    };

    /** Стиль по умолчанию для Формы */
    public static final int[] style_form                  = {
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_BACKGROUND | THEME,
            ATTR_SSH_TILE, 0
    };

    /** Стиль по умолчанию для Диалога */
    public static final int[] style_dlg                   = {
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_BACKGROUND | THEME,
            ATTR_SSH_SHAPE, TILE_SHAPE_ROUND,
            ATTR_SSH_RADII, R.string.radii_dlg
    };

    /** Стиль по умолчанию для Главного экрана приложения */
    public static final int[] style_menu                  = {
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_MENU | THEME,
            ATTR_SSH_TILE, 0
    };

    /** Стиль по умолчанию для Контроллера */
    public static final int[] style_controller            = {
            ATTR_VISIBILITY, GONE,
            ATTR_SSH_CONTROLLER_WIDTH, 160,
            ATTR_SSH_CONTROLLER_HEIGHT, 160,
            ATTR_SSH_ALPHA, 184,
            ATTR_SSH_GRAVITY, TILE_GRAVITY_CENTER | TILE_GRAVITY_BACKGROUND,
            ATTR_SSH_BITMAP_NAME, R.drawable.controller_tiles,
            ATTR_SSH_HORZ, 6,
            ATTR_SSH_VERT, 1,
            ATTR_SSH_TILE, 0
    };

    /** Стиль по умолчанию для Кнопки */
    public static final int[] style_button                = {
            ATTR_SHADOW_TEXT, R.string.shadow_text,
            ATTR_SIZE, R.dimen.button,
            ATTR_FONT, R.string.font_normal,
            ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_NORMAL | THEME,
            ATTR_CLICKABLE, 1,
            ATTR_PADDING, 1,
            ATTR_MIN_HEIGHT, R.dimen.heightButton,
            ATTR_SSH_PRESSED_OFFS, R.dimen.pressedOffs,
            ATTR_SSH_WIDTH_SELECTOR, R.dimen.widthSelector,
            ATTR_SSH_COLOR_SELECTOR, ATTR_SSH_COLOR_SELECTOR | THEME,
            ATTR_SSH_HORZ, 2,
            ATTR_SSH_TILE, 0,
            ATTR_SSH_STATES, TILE_STATE_HOVER,
            ATTR_GRAVITY, Gravity.CENTER,
            ATTR_SSH_GRAVITY, TILE_GRAVITY_CENTER | TILE_GRAVITY_BACKGROUND,
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_BUTTONS | THEME
    };

    /** Стиль по умолчанию для Кнопки футера */
    public static final int[] style_footer                = {
            ATTR_CLICKABLE, 1,
            ATTR_MIN_HEIGHT, R.dimen.heightButton,
            ATTR_SSH_PRESSED_OFFS, R.dimen.pressedOffs,
            ATTR_SSH_STATES, TILE_STATE_HOVER,
            ATTR_SSH_GRAVITY, TILE_GRAVITY_CENTER | TILE_GRAVITY_BACKGROUND,
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_BUTTONS | THEME,
            ATTR_SSH_HORZ, 2,
            ATTR_SSH_TILE, 1
    };

    /** Стиль по умолчанию для Инструментальной кнопки */
    public static final int[] style_tool                  = {
            ATTR_CLICKABLE, 1,
            ATTR_MIN_HEIGHT, R.dimen.heightButton,
            ATTR_SSH_PRESSED_OFFS, R.dimen.pressedOffs,
            ATTR_SSH_HORZ, 3,
            ATTR_SSH_TILE, 0,
            ATTR_SSH_STATES, TILE_STATE_HOVER,
            ATTR_SSH_GRAVITY, TILE_GRAVITY_CENTER | TILE_GRAVITY_BACKGROUND,
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_TOOLS | THEME
    };

    /** Стиль по умолчанию для Кнопки со стрелкой */
    public static final int[] style_tool_arrow            = {
            ATTR_CLICKABLE, 1,
            ATTR_MIN_HEIGHT, R.dimen.heightButton,
            ATTR_SSH_PRESSED_OFFS, R.dimen.pressedOffs,
            ATTR_SSH_STATES, TILE_STATE_PRESS | TILE_STATE_SHADOW,
            ATTR_SSH_GRAVITY, TILE_GRAVITY_CENTER | TILE_GRAVITY_BACKGROUND,
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_TOOLS | THEME,
            ATTR_SSH_HORZ, 3,
            ATTR_SSH_TILE, 1
    };

    /** Стиль по умолчанию для Списка */
    public static final int[] style_ribbon                = {
            ATTR_SELECTOR, ATTR_SSH_COLOR_SELECTOR | THEME,
            ATTR_PADDING, 2
    };

    /** Стиль по умолчанию для GridView */
    public static final int[] style_grid                  = {
            ATTR_SELECTOR, ATTR_SSH_COLOR_SELECTOR | THEME,
            ATTR_SPACING_LINE, 1,
            ATTR_SPACING_CELL, 1,
            ATTR_CELL_SIZE, 130,
            ATTR_PADDING, 2,
            ATTR_STRETCH_MODE, TABLE_STRETCH_UNIFORM
    };

    /** Стиль по умолчанию для вкладки TabLayout */
    public static final int[] style_tab_page              = {
            ATTR_SSH_VERT, 1,
            ATTR_SSH_HORZ, 2,
            ATTR_CLICKABLE, 1,
            ATTR_FOCUSABLE, 0,
            ATTR_SSH_SCALE, TILE_SCALE_NONE,
            ATTR_SSH_WIDTH_SELECTOR, 0,
            ATTR_SSH_GRAVITY, TILE_GRAVITY_CENTER | TILE_GRAVITY_BACKGROUND,
            ATTR_GRAVITY, Gravity.CENTER,
            ATTR_COLOR_DEFAULT, ATTR_SSH_COLOR_NORMAL | THEME,
            ATTR_FONT, R.string.font_normal,
            ATTR_SIZE, R.dimen.normal,
            ATTR_SSH_BITMAP_NAME, ATTR_SSH_BM_BUTTONS | THEME
    };

    /** Стиль по умолчанию для DrawableTile */
    public static final int[] style_drawable_tile         = {};

}

package ru.ostrovskal.sshstd;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.widget.ListPopupWindow;

/** Java класс, являющийся контейнером для глобальных переменных и констант */
public final class Common
{
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

    /** Характеристики дисплея */
    public static DisplayMetrics        dMetrics;

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
    /** Идентификатор загрузчика */
    public static final int CONNECTOR           = 1;

    /** Сообщение текущему окну/активити */
    public static final int RECEPIENT_WND       = 0;

    /** Сообщение для текущей формы */
    public static final int RECEPIENT_FORM      = 1;

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

    /** Тип выравнивания тайла. Без выравнивания */
    public static final int TILE_GRAVITY_NONE       = 128;

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
    public static final int GRID_STRETCH_NO         = 0;

    /** Режим растягивания. Пространство между ячейками */
    public static final int GRID_STRETCH_SPACING    = 1;

    /** Режим растягивания. Растягивать ячейки */
    public static final int GRID_STRETCH_CELL       = 2;

    /** Режим растягивания. Пространство между ячейками. Расстояние одинаковое */
    public static final int GRID_STRETCH_UNIFORM    = 3;

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

    /** Тип диаграммы/прогресса. Диарграмма */
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
    public static final int FIELD_TYPE_NULL         = 0;

    /** Тип поля. Целое INT */
    public static final int FIELD_TYPE_INTEGER      = 1;

    /** Тип поля. Вещественное DOUBLE */
    public static final int FIELD_TYPE_REAL         = 2;

    /** Тип поля. Строковое STRING */
    public static final int FIELD_TYPE_TEXT         = 3;

    /** Тип поля. Байтовый массив BYTE[] */
    public static final int FIELD_TYPE_BLOB         = 4;

    /** Тип поля. Лата/Время LONG */
    public static final int FIELD_TYPE_TIMESTAMP    = 5;

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
}

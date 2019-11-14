package ru.ostrovskal.sshstd

import android.graphics.Point
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.github.ostrovskal.sshstd.R
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.layouts.CellLayout
import ru.ostrovskal.sshstd.ui.*
import ru.ostrovskal.sshstd.utils.*
import ru.ostrovskal.sshstd.widgets.Progress
import kotlin.concurrent.thread

/** Установка максимума  */
const val PROGRESS_MAXIMUM    = 0

/** Завершение  */
const val PROGRESS_FINISH     = 1

/** Установка первичного значения  */
const val PROGRESS_PRIMARY    = 2

/** Установка вторичного значения  */
const val PROGRESS_SECONDARY  = 3

/**
 * @author Шаталов С.В.
 * @since 1.0.2
 */

/** Класс, реализующий стандартную форму отображения прогресса */
open class FormProgress : Form() {

    // инициализатор макета
    private lateinit var initLayout: CellLayout.() -> Unit

    // Форма ожидания
    private val waitingLayout: CellLayout.() -> Unit = {
        var x = 0
        arguments.getInt("text").apply { if(this != 0) { text(this).lps(0, 0, 5, 4); x = 5 } }
        progress(R.id.progressForm, 100, SSH_MODE_CIRCULAR) { isShowText = false; setBitmap("progress") }.lps(x, 0, 5, 4)
        layoutParams = LinearLayout.LayoutParams(220.dp, 96.dp)
    }

    // Форма загрузки
    private val loadLayout: CellLayout.() -> Unit = {
        arguments.getInt("text").apply {
            if(this != 0) text(this).lps(0, 0, 5, 2)
        }
        progress(R.id.progressForm, 100, SSH_MODE_DIAGRAM).lps(0, 2, 10, 1)
        layoutParams = LinearLayout.LayoutParams(220.dp, 80.dp)
    }

    // Объект продвижения прогресса
    private var obj: Progress?  = null

    // фоновый тред
    private var thread: Thread? = null

    // аргументы для передачи в UI поток
    private val args            = Point(0, 0)

    /** Первичное значение */
    var primary
        get()           = obj?.primaryProgress ?: 0
        set(v)          { send(PROGRESS_PRIMARY, v) }

    /** Вторичное значение */
    var secondary
        get()           = obj?.secondaryProgress ?: 0
        set(v)          { send(PROGRESS_SECONDARY, v) }

    /** Максимум */
    var maximum
        get()           = obj?.max ?: 0
        set(v)          { send(PROGRESS_MAXIMUM, v) }

    // обработка сообщений
    private val handler = Runnable {
        obj?.apply {
            val value = args.y
            when (args.x) {
                PROGRESS_FINISH     -> footer(BTN_OK, 0)
                PROGRESS_PRIMARY    -> primaryProgress = value
                PROGRESS_SECONDARY  -> secondaryProgress = value
                PROGRESS_MAXIMUM    -> max = value
            }
        }
    }

    // отправка в UI поток
    private fun send(msg: Int, value: Int) {
        args.x = msg; args.y = value
        obj?.post(handler)
    }

    /** Создание формы */
    fun show(wnd: Wnd, text: Int, isLoading: Boolean, init: (CellLayout.() -> Unit)? = null): FormProgress {
        initLayout = init?.run { this } ?: if(isLoading) loadLayout else waitingLayout
        wnd.instanceForm(this, "progress", 0, 1, "text", text)
        return this
    }

    /** Прервать фоновую операцию */
    open fun interruptBackground() {
        try { thread?.join()
        } catch (e: InterruptedException) { }
        thread = null
        super.footer(BTN_NO, 0)
    }

    /** Завершение формы */
    override fun footer(btn: Int, param: Int) {
        interruptBackground()
    }

    /** Фоновая обработка
     * @param msg   Сообщение в случае успешного завершения
     * @param block Блок кода
     * */
    open fun inBackground(msg: Int, block: (fp: FormProgress) -> Boolean) {
        thread = thread {
            val result = block(this)
            thread = null
            finish()
            if(msg != 0) wnd.hand?.send(RECEPIENT_FORM, msg, a1 = result.toInt)
        }
    }


    /** Вернуть тему */
    override fun getTheme() = R.style.dialog_progress

    /** Завершение формы */
    open fun finish() {
        obj?.removeCallbacks(null)
        send(PROGRESS_FINISH, 0)
    }

    /** Создание разметки */
    override fun inflateContent(container: LayoutInflater): UiCtx {
        return ui {
            linearLayout {
                root = cellLayout(10, 4, 0, false, initLayout)
                // найти прогресс
                root.loopChildren { if(it is Progress) obj = it }
            }
        }
    }
}


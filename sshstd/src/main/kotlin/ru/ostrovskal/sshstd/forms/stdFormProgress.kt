@file:Suppress("DEPRECATION")

package ru.ostrovskal.sshstd.forms

import android.view.LayoutInflater
import android.widget.LinearLayout
import com.github.ostrovskal.sshstd.R
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.Wnd
import ru.ostrovskal.sshstd.layouts.CellLayout
import ru.ostrovskal.sshstd.ui.*
import ru.ostrovskal.sshstd.utils.dp
import ru.ostrovskal.sshstd.utils.send
import ru.ostrovskal.sshstd.utils.ui
import ru.ostrovskal.sshstd.widgets.Progress

/**
 * @author Шаталов С.В.
 * @since 1.0.2
 */

/** Ожидание */
const val FORM_PROGRESS_WAIT        = 0

/** Загрузка */
const val FORM_PROGRESS_DOWNLOAD    = 1

/** Класс, реализующий стандартную форму отображения прогресса */
open class FormProgress : Form() {

    /** Событие инициализации контента */
    @JvmField var initializeContent: ((root: CellLayout) -> Unit)? = null

    /** Объект продвижения прогресса */
    lateinit var obj: Progress

    /** Первичное значение */
    var primary
        get()           = obj.primaryProgress
        set(v)          { obj.primaryProgress = v }

    /** Вторичное значение */
    var secondary
        get()           = obj.secondaryProgress
        set(v)          { obj.secondaryProgress = v }

    /** Максимум */
    var maximum
        get()           = obj.max
        set(v)          { obj.max = v }

    /** Создание формы */
    fun show(wnd: Wnd, text: Int, initType: Int): FormProgress {
        wnd.instanceForm(this, "progress", 0, 1, "text", text, "type", initType)
        return this
    }

    /** Фоновая обработка
     * @param msg   Сообщение после завершения
     * @param block Блок кода
     * */
    open suspend fun doInBackground(msg: Int, block: suspend (fp: FormProgress) -> Int) {
        val btn = block(this@FormProgress)
        wnd.apply {
            footer(btn, 0)
            if (msg != 0) hand?.send(RECEPIENT_FORM, msg, a1 = btn)
        }
    }

    /** Вернуть тему */
    override fun getTheme() = R.style.dialog_progress

    /** Создание разметки */
    override fun inflateContent(container: LayoutInflater): UiCtx {
        return ui {
            linearLayout {
                root = cellLayout(10, 4, 0, false) {
                    val text = arguments.getInt("text")
                    when(arguments.getInt("type")) {
                        FORM_PROGRESS_WAIT           -> {
                            var x = 0
                            if(text != 0) { text(text).lps(0, 0, 5, 4); x = 5 }
                            obj = progress(R.id.progressForm, 100, SSH_MODE_CIRCULAR) {
                                isShowText = false
                                setBitmap("progress")
                            }.lps(x, 0, 5, 4)
                            layoutParams = LinearLayout.LayoutParams(220.dp, 80.dp)
                        }
                        FORM_PROGRESS_DOWNLOAD       -> {
                            if (text != 0) text(text).lps(0, 0, 5, 2)
                            obj = progress(R.id.progressForm, 100, SSH_MODE_DIAGRAM).lps(0, 2, 10, 1)
                            layoutParams = LinearLayout.LayoutParams(220.dp, 80.dp)
                        }
                    }
                    initializeContent?.invoke(this)
                }
            }
        }
    }
}


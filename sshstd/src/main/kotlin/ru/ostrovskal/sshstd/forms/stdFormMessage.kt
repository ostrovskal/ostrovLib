@file:Suppress("DEPRECATION")

package ru.ostrovskal.sshstd.forms

import android.view.LayoutInflater
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.Wnd
import ru.ostrovskal.sshstd.ui.*
import ru.ostrovskal.sshstd.utils.dp
import ru.ostrovskal.sshstd.utils.send
import ru.ostrovskal.sshstd.utils.ui

/**
 * @author Шаталов С.В.
 * @since 1.0.2
 */

/** Класс, реализующий стандартную форму отображения сообщения */
open class FormMessage : Form() {
    /** Отображение формы
     * @param params Массив параметров. 0 - заголовок, 1 - сообщение, 2 - иконка OK, 3 - иконка NO, 4 - иконка DEF, 5 - width, 6 - height
     * */
    fun show(wnd: Wnd, params: IntArray): FormMessage {
        wnd.instanceForm(this, "message", 0, 0, "params", params)
        return this
    }

    override fun inflateContent(container: LayoutInflater): UiCtx {
        val params = arguments.getIntArray("params") ?: error("В форме сообщений не задан массив параметров!")
        val width = params[5].run { if(this == 0 ) 330.dp else this }
        val height = params[6].run { if(this == 0 ) 180.dp else this }
        return ui {
            linearLayout {
                root = cellLayout(10, 9) {
                    formHeader(params[0])
                    text(params[1], style_text_dlg).lps(0, 0, -1, 5)
                    val but1 = params[2]; val but2 = params[3]; val but3 = params[4]
                    when {
                        but2 == 0   -> formFooter(BTN_OK, but1)
                        but3 == 0   -> formFooter(BTN_OK, but1, BTN_NO, but2)
                        else        -> formFooter(BTN_OK, but1, BTN_NO, but2, BTN_DEF, but3)
                    }
                }.lps(width, height)
            }
        }
    }

    override fun footer(btn: Int, param: Int) {
        wnd.apply {
            super.footer(btn, param)
            hand?.send(RECEPIENT_FORM, ACT_MESSAGE_RESULT, a1 = btn)
        }
    }
}
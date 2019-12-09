@file:Suppress("DEPRECATION")

package ru.ostrovskal.sshstd.forms

import android.app.Dialog
import android.app.DialogFragment
import android.app.FragmentTransaction
import android.content.DialogInterface
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.github.ostrovskal.sshstd.R
import kotlinx.coroutines.cancelChildren
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.Surface
import ru.ostrovskal.sshstd.Wnd
import ru.ostrovskal.sshstd.adapters.RecordAdapter
import ru.ostrovskal.sshstd.sql.StmtSelect
import ru.ostrovskal.sshstd.ui.UiCtx
import ru.ostrovskal.sshstd.utils.put
import ru.ostrovskal.sshstd.utils.send
import ru.ostrovskal.sshstd.widgets.Tile

/**
 * @author Шаталов С.В.
 * @since 0.0.1
*/

/**
 * Реализация класса фрагмента на базе диалога.
 * Является базовым классом для всех фрагментов(форм) в системе, обеспечивая взаимодействие
 * с активити и передачу сообщений посредством хэндлера.
 * Реализует интерфейсы для взаимодействия со spinner, view и корутинами
 */
open class Form : DialogFragment(), View.OnClickListener {

	/** Разметка */
	lateinit var content: ViewGroup
	
	/** Корневой элемент */
	lateinit var root: ViewGroup
	
	/** Анимация дрожания */
	lateinit var shake: Animation

	/** Кэшируемое выражение SELECT */
	lateinit var stmt: StmtSelect

    // Предыдущий тег формы
    @JvmField protected var prevTag                 = ""

    /** Адаптер */
	@JvmField var adapter: RecordAdapter?			= null

	/** Передача результата */
	@JvmField var result     				= Result

	/** Используется для обработки нажатия кнопки BACK и события onBackPress */
	@JvmField protected var tmBACK     				= 0L
	
	/** Доступ к активити */
	val wnd get()                             = activity as Wnd

	/** Фоновый объект */
	open val surface: Surface? get()				= null

	/** Доступ к индексу структуры, описывающей данную форму */
	val index get()                          	= arguments.getInt("IDX")

	/** удаление формы по ее тегу [tag] */
	fun removeForm(tag: String) {
		fragmentManager.apply {
			findFragmentByTag(tag)?.let {
				beginTransaction().remove(it).commit()
			}
		}
	}

	/** Обработка нажатия на кнопку BACK */
	open fun backPressed() {
		val tm = if(twicePressed()) tmBACK + 2000 else System.currentTimeMillis() + 10000
		if(tm > System.currentTimeMillis()) {
			if(wnd.fragmentManager.backStackEntryCount != 0) footer(BTN_NO, ACT_BACKPRESSED)
		}
		else wnd.showToast(getString(R.string.again_press_for_exit), parent = content, offsX = -1, offsY = -1)
		tmBACK = System.currentTimeMillis()
	}
	
	/** Запрос темы диалога */
	override fun getTheme(): Int = R.style.dialog_custom
	
	/** Создание диалога */
	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		setStyle(STYLE_NO_TITLE, theme)
		return Dialog(activity, theme).apply {
			setCanceledOnTouchOutside(false)
			setCancelable(false)
		}
	}
	
	/** Сохранение состояния */
	override fun onSaveInstanceState(outState: Bundle) {
		outState.put("prevTag", prevTag)
		saveState(outState)
		super.onSaveInstanceState(outState)
	}
	
	/** Возобновление формы */
	override fun onResume() {
		prevTag = wnd.tagForm
		wnd.tagForm = tag
		super.onResume()
	}

	/** Уничтожение формы */
	override fun onDestroy() {
		super.onDestroy()
		wnd.coroutineContext.cancelChildren()
	}

	/** Создание представления формы */
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		shake = AnimationUtils.loadAnimation(wnd, R.anim.shake)
		wnd.toast = null
		// восстановление состояния
		savedInstanceState?.apply {
			prevTag = getString("prevTag") ?: ""
			restoreState(this)
		}
		// загружаем/создаем содержимое
		content = (inflateContent(inflater).view as ViewGroup).apply {
			// инициализация содержимого
			initContent(this)
			// ставим слушатели на стандартные кнопки
			findViewById<Tile>(BTN_OK)?.setOnClickListener(this@Form)
			findViewById<Tile>(BTN_NO)?.setOnClickListener(this@Form)
			findViewById<Tile>(BTN_DEF)?.setOnClickListener(this@Form)
		}
		return content
	}
	
	/** Смена темы */
	open fun changeTheme() {
		content.apply {
			changeTheme()
			invalidate()
		}
	}
	
	/** Инициализация [content] */
	open fun initContent(content: ViewGroup) {}
	
	/** Обработка клика на представлении [v] */
	override fun onClick(v: View) {
		footer(v.id, 0)
	}
	
	/** В режиме диалога, выход при клике за пределами формы */
	override fun onCancel(dialog: DialogInterface?) {
		footer(BTN_NO, 0)
	}
	
	/**
	 * Обработчик футера
	 *
	 * @param btn   Идентификатор представления
	 * @param param Передаваемый объект
	 */
	open fun footer(btn: Int, param: Int) {
		wnd.tagForm = prevTag
		fragmentManager.popBackStackImmediate()
		dismiss()
	}
	
	/** Сохранение состояния */
	open fun saveState(state: Bundle) {}
	
	/** Восстановление состояния */
	open fun restoreState(state: Bundle) {}
	
	/** Признак двойного нажатия кнопки BACK, при выходе */
	open fun twicePressed() = false
	
	/** Обработка сообщений хэндлера. По умолчанию отправляется предыдущей форме. */
	open fun handleMessage(msg: Message): Boolean {
		//"handleMessage default prevTag: $prevTag".debug()
		if(prevTag.isNotEmpty()) return (fragmentManager.findFragmentByTag(prevTag) as? Form)?.handleMessage(msg) ?: true
		return true
	}
	
	/** Установка анимации */
	open fun setAnimation(trans: FragmentTransaction) {
		trans.setCustomAnimations(R.animator.form_enter, R.animator.form_exit, R.animator.form_enter, R.animator.form_exit)
	}
	
	/**
	 * Отправка результата работы формы
	 *
	 * @param recepient Адресат
	 * @param action    Действие
	 * @param param     Дополнительный параметр
	 */
	open fun sendResult(recepient: Int, action: Int, param: Int = 0) {
		wnd.hand?.send(recepient, action, a1 = param, o = result)
	}
	
	/** Загружает содержимое */
	protected open fun inflateContent(container: LayoutInflater) = UiCtx(activity)

	/** Строка запроса базы данных */
	open fun query(block: () -> StmtSelect) { stmt = block() }
}

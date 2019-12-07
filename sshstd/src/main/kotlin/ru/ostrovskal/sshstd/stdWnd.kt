@file:Suppress("UNCHECKED_CAST", "DEPRECATION")

package ru.ostrovskal.sshstd

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.forms.Form
import ru.ostrovskal.sshstd.objects.Settings
import ru.ostrovskal.sshstd.objects.Sound
import ru.ostrovskal.sshstd.sql.SQL
import ru.ostrovskal.sshstd.ui.UiComponent
import ru.ostrovskal.sshstd.utils.*
import kotlin.coroutines.CoroutineContext

/**
 * @author Шаталов С. В.
 * @since 0.0.1
*/

/** Базовый класс, реализующий активити и интерфейс хэндлера */
abstract class Wnd : Activity(), Handler.Callback, CoroutineScope {

	/** Задача для корутин */
	@JvmField protected val job = SupervisorJob()

	/** Контент */
	lateinit var content: ViewGroup

	/** Контекст корутин */
	override val coroutineContext: CoroutineContext
		get() = Dispatchers.Main + job

	/** Массив структур форм */
	@JvmField protected var forms 			= intArrayOf()
	
	/** Признак рестарта */
	@JvmField var isRestart 				= false
	
	/** Текущая форма */
	@JvmField var tagForm                   = ""
	
	/** UI хэндлер */
	@JvmField var hand: Handler?            = null
	
	/** Тост */
	var toast: Toast? 		                = null
		set(v)                              { field?.cancel(); field = v }
	
	/** Поиск формы по тегу [tag] */
	fun <T> findForm(tag: String)       = fragmentManager.findFragmentByTag(tag) as T?
	
	/** Изменение главной темы */
	open fun changeTheme() {
		cacheBitmap.evictAll()
		applyTheme()
		content.changeTheme()
		findForm<Form>(tagForm)?.changeTheme()
	}
	
	/** Применение темы */
	abstract fun applyTheme()
	
	/** Обработка нажатия кнопки BACK */
	override fun onBackPressed() { findForm<Form>(tagForm)?.backPressed() ?: hand?.send(RECEPIENT_WND, ACT_EXIT) }
	
	/** Обработка хэндлера. True, если обработка завершена */
	override fun handleMessage(msg: Message): Boolean {
		msg.info.debug()
		return if(msg.recepient == RECEPIENT_WND) {
			if(msg.action == ACT_EXIT) { finish(); true } else false
		} else {
			findForm<Form>(tagForm)?.run {
				when(msg.recepient) {
					RECEPIENT_FORM 		-> handleMessage(msg)
					RECEPIENT_SURFACE_UI-> surface?.handleMessage(msg)
					RECEPIENT_SURFACE_BG-> surface?.hand?.sendMessage(Message.obtain(msg))
					else				-> false
				}
			} ?: false
		}
	}
	
	/**
	 * Отображение тоста
	 *
	 * @param msg       Отображаемый текст
	 * @param islong    Время отображения в мс
	 * @param parent    Представление по экранным координатам которого отображать тост, если null, то используется экран
	 * @param ui        Компонент разметки
	 * @param offsX     Горизонтальное смещение, относительно координат представления parent
	 * @param offsY     Вертикальное смещение, относительно координат представления parent
	 */
	fun showToast(msg: CharSequence, islong: Boolean = false, parent: View? = null, ui: UiComponent? = null, offsX: Int = 0, offsY: Int = 0) {
		toast = toast(msg, islong, parent, ui, offsX, offsY)
	}
	
	/** Установка текущих режимов */
	protected fun setFlags(flags: Int) {
		if(flags ntest SSH_APP_MODE_TITLE) {
			requestWindowFeature(Window.FEATURE_NO_TITLE)
		}
		if(flags test SSH_APP_MODE_FULLSCREEN) {
			window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
		}
		if(flags test SSH_APP_MODE_GAME) {
			window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
			window.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
			window.setBackgroundDrawable(null)
		}
	}
	
	/** Установка разметки [layout] и состояний [flags] */
	fun setLayout(layout: ViewGroup, flags: Int) {
		setFlags(flags)
		content = layout
		super.setContentView(layout)
	}
	
	/** Создание активити */
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		// формы
		forms = loadResource("forms", "array", intArrayOf())
		// начальная инициализация
		initialize(false)
	}

	/** Запуск формы, используя непосредственное создание, с аргументами [args] */
	fun instanceForm(form: Form, tag: String, container: Int, stack: Int, vararg args: Any?) {
		val params 		= Bundle()
		// основные/переданные аргументы
		params.put("IDX", -1)
		for(index in args.indices step 2) { params.put(args[index].toString(), args[index + 1]) }
		form.arguments = params
		// отображаем форму
		showForm(form, container, tag, stack)
	}

	/** Запуск формы, используя информацию из массива структур. По индексу [idx] и с аргументами [args] */
	fun instanceForm(idx: Int, vararg args: Any?) {
		val formName	= getString(forms[idx * 4 + 0])
		val form 	    = Class.forName(formName).newInstance() as Form
		val params 		= Bundle()
		// идентификатор контейнера, куда помещать форму
		val container	= forms[idx * 4 + 1]
		// тэг для поиска формы
		val tag 		= getString(forms[idx * 4 + 2])
		// признак добавления в стэк
		val stack		= forms[idx * 4 + 3]// == 1
		// основные/переданные аргументы
		params.put("IDX", idx)
		for(index in args.indices step 2) { params.put(args[index].toString(), args[index + 1]) }
		form.arguments = params
		// отображаем форму
		showForm(form, container, tag, stack)
	}

	/** Отображение формы
	 * @param form		Объект формы
	 * @param container	ИД контейнера
	 * @param tag		Имя формы
	 * @param stack		Тип добавление в стэк фрагментов(2 - добавить, 1 - заменить)
	 * */
	fun showForm(form: Form, container: Int, tag: String, stack: Int) {
		val trans = fragmentManager.beginTransaction()
		form.setAnimation(trans)
		// выбираем как создать
		if(container == 0) {
			trans.add(form, tag)
		} else {
			if(stack == 2) {
				trans.add(container, form, tag)
			} else {
				trans.replace(container, form, tag)
			}
		}
		if(stack != 0) {
			trans.addToBackStack(null)
		}
		trans.commit()
	}

	/** Начальная инициализация при создании, либо рестарте */
	abstract fun initialize(restart: Boolean)
	
	/** Рестарт */
	override fun onRestart() {
		super.onRestart()
		"onRestart".debug()
		isRestart = true
	}
	
	/** Возобновление */
	override fun onResume() {
		"onResume".debug()
		if(isRestart) initialize(true)
		super.onResume()
	}
	
	/** Остановка */
	override fun onStop() {
		Sound.close()
		SQL.disconnetion()
		Settings.close()
		hand?.removeCallbacksAndMessages(null)
		hand = null
		super.onStop()
	}

	/** Уничтожение активити */
	override fun onDestroy() {
		super.onDestroy()
		coroutineContext.cancel()
	}
}

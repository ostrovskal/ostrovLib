package ru.ostrovskal.sshstd

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.view.SurfaceHolder
import android.view.SurfaceView
import ru.ostrovskal.sshstd.utils.*
import java.lang.ref.WeakReference

/**
 * @author Шаталов С.В.
 * @since 0.1.0
*/

/** Базовый класс, реализующий представление с обработкой в фоновом потоке и обеспечивающей передачу сообщений фоновым тредом и UI тредом
 * @property tagMarshalling Имя для маршиллинга параметров
 */
abstract class Surface(context: Context, private val tagMarshalling: String = "main") : SurfaceView(context, null, 0), Handler.Callback, SurfaceHolder.Callback {

	// Фоновый тред
	private var thread: SurfaceThread? 		= null

	/** Область канвы */
	@JvmField val canvasRect          		= Rect()

	/** Задержка отрисовки */
	@JvmField var delay			            = 100L
	
	/** Хэндлер */
	@JvmField var hand: Handler? 		    = null

	/** Усыпить\Возобновить тред */
	@JvmField var running                   = true
	
	/** Запрос на Frame Per Second */
	@JvmField var fps                       = 0
	
	/** Присоединение поверхности к окну */
	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
		holder.addCallback(this)
	}
	
	/** Отсоединение поверхности от окна */
	override fun onDetachedFromWindow() {
		holder.removeCallback(this)
		super.onDetachedFromWindow()
	}
	
	/** Остановка фонового треда */
	fun stopThread() {
		thread?.apply {
			running = false
			interrupt()
			quit()
		}
		hand?.removeCallbacksAndMessages(null)
		thread = null
	}
	
	/**
	 * Изменение поверхности и запуск фонового треда
	 *
	 * @param holder Интерфейс поверхности
	 * @param format Формат пикселей поверхности
	 * @param width  Ширина поверхности
	 * @param height Высота поверхности
	 */
	override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
		touchReset()
		if(thread?.isAlive == false)
			thread?.start()
		running = true
		canvasRect.set(0, 0, width, height)
	}
	
	/** Создание поверхности и фонового треда */
	override fun surfaceCreated(holder: SurfaceHolder) { thread = SurfaceThread(WeakReference(this)) }
	
	/** Уничтожение поверхности и фонового треда */
	override fun surfaceDestroyed(holder: SurfaceHolder) { stopThread() }

	/** Обработка хэндлера */
	override fun handleMessage(msg: Message): Boolean = true
	
	/**
	 * Восстановление состояния данного объекта и всех производных.
	 * Используется механизм маршаллинга.
	 * Восстановливаются только те поля, которые помечены аннотацией @STORAGE.
	 */
	open fun restoreState(state: Bundle, vararg params: Any?) {
		unmarshall(state.getByteArray(tagMarshalling) ?: byteArrayOf() )
		params.forEach { it?.apply { it.unmarshall(state.getByteArray(it.toString()) ?: byteArrayOf() ) } }
	}
	
	/**
	 * Сохранение состояния данного объекта и всех производных.
	 * Используется механизм маршаллинга.
	 * Сохранаются только те поля, которын помечены аннотацией @STORAGE.
	 */
	open fun saveState(state: Bundle, vararg params: Any?) {
		stopThread()
		state.put(tagMarshalling, marshall())
		params.forEach { it?.apply { state.put(it.toString(), it.marshall()) } }
	}
	
	private class SurfaceThread(private val weak: WeakReference<Surface>) : HandlerThread("surfaceThread") {
		
		private var runner: Runnable? = null
		
		override fun onLooperPrepared() {
			weak.get()?.let {
				it.hand = Handler(looper, it).apply {
					send(act = Common.ACT_INIT_SURFACE)
					post(runner)
				}
			}
		}
		
		init {
			runner = Runnable {
				if(isInterrupted) return@Runnable
				val surface = weak.get() ?: return@Runnable
				surface.apply {
					var diff = 0L
					if(running) {
						var canvas: Canvas? = null
						try {
							canvas = holder.lockCanvas()?.apply {
								val start = System.currentTimeMillis()
								draw(this)
								diff = (System.currentTimeMillis() - start)
								"diff $diff ${delay - diff}".info()
								fps = (1000 / if(diff > 0) diff else 1).toInt()
							}
						}
						finally {
							if(canvas != null) holder.unlockCanvasAndPost(canvas)
						}
					}
					hand?.apply {
						postDelayed(runner, if(diff < delay) delay - diff else 0)
					}
				}
			}
		}
	}
}

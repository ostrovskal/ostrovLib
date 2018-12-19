package ru.ostrovskal.sshstd

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.view.SurfaceHolder
import android.view.SurfaceView
import ru.ostrovskal.sshstd.utils.marshall
import ru.ostrovskal.sshstd.utils.put
import ru.ostrovskal.sshstd.utils.send
import ru.ostrovskal.sshstd.utils.unmarshall
import java.lang.ref.WeakReference

/**
 * @author Шаталов С.В.
 * @since 0.1.0
*/

/** Базовый класс, реализующий представление с обработкой в фоновом потоке и обеспечивающей передачу сообщений фоновым тредом и UI тредом */
abstract class Surface(context: Context) : SurfaceView(context, null, 0), Handler.Callback, SurfaceHolder.Callback {
	
	/** Задержка отрисовки */
	@JvmField var delay			            = 100L
	
	/** Хэндлер */
	@JvmField var hand: Handler? 		    = null

	// Фоновый тред
	private var thread: SurfaceThread? 		= null
	
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
		thread?.start()
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
		unmarshall(state.getByteArray("main") ?: byteArrayOf() )
		params.forEach { it?.apply { it.unmarshall(state.getByteArray(it.toString()) ?: byteArrayOf() ) } }
	}
	
	/**
	 * Сохранение состояния данного объекта и всех производных.
	 * Используется механизм маршаллинга.
	 * Сохранаются только те поля, которын помечены аннотацией @STORAGE.
	 */
	open fun saveState(state: Bundle, vararg params: Any?) {
		stopThread()
		state.put("main", marshall())
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
				val delay = surface.delay
				var diff = delay
				if(surface.running) {
					var canvas: Canvas? = null
					try {
						canvas = surface.holder.lockCanvas()?.apply {
							val start = System.currentTimeMillis()
							surface.draw(this)
							diff = System.currentTimeMillis() - start
							surface.fps = (1000 / (delay - diff)).toInt()
						}
					}
					finally {
						if(canvas != null) surface.holder.unlockCanvasAndPost(canvas)
					}
				}
				surface.hand?.apply {
					// компенсация задержки при отправке сообщения хэндлера
					diff += 10
					postDelayed(runner, if(diff < delay) delay - diff else 0)
					//if(diff < delay) postDelayed(runner, delay - diff) else post(runner)
				}
				
			}
		}
	}
}

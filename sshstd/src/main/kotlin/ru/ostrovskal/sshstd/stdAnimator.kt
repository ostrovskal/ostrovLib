package ru.ostrovskal.sshstd

import android.view.View

/**
 * @author Шаталов С.В.
 * @since 0.0.1
*/

/**
 * Реализация покадровой анимации в UI потоке
 *
 * @property view      Представление, для которого выполнять анимационные действия
 * @property frames    Количество кадров анимации
 * @property duration  Задержка между кадрами в мс
 * @property callback  Реализация интерфейса, для уведомления об анимационных событиях
 */
open class Animator(private val view: View, @JvmField var frames: Int, @JvmField var duration: Int,
                    private val callback: ((view: View, animator: Animator, frame: Int, direction: Int, began: Boolean) -> Boolean)?): Runnable {
	
	// Направление
	private var direction 			= 1
	
	// Признак первого запуска
	private var begin 				= true
	
	/** Текущий кадр */
	@JvmField var frame 			= 0
	
	/** Признак активности */
	@JvmField var isRunning	        = false
	
	/** Индекс последнего кадра */
	val lastFrame 		            get() = frames - 1
	
	/** Исполнение блока кода */
	override fun run() {
		if(isRunning) {
			if(frame in 0..frames) {
				if(callback?.invoke(view, this, frame, direction, begin) == false) {
					frame += direction
					view.postOnAnimationDelayed(this, duration.toLong())
				}
				else isRunning = false
				begin = false
			}
		}
	}
	
	/** Сброс состояния к исходным значениям. [b] Признак сброса инициализатора */
	open fun reset(b: Boolean) {
		begin = b
		frame = 0
		direction = 1
	}
	
	/** Запуск анимации с признаком остановки предыдущей [stop] и сброса состояния [reset] */
	open fun start(stop: Boolean, reset: Boolean) {
		if(stop) stop()
		if(reset) reset(false)
		if(!isRunning) {
			view.postOnAnimation(this)
			isRunning = true
		}
	}
	
	/** Остановка анимации */
	open fun stop() {
		if(isRunning) {
			view.removeCallbacks(this)
			isRunning = false
		}
		
	}
	
	/** Инверсия направления приращения кадров анимации */
	open fun reverse() { direction = if(direction == 1) -1 else 1 }
}

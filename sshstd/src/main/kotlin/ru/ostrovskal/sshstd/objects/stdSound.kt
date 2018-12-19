@file:Suppress("DEPRECATION")

package ru.ostrovskal.sshstd.objects

import android.content.Context
import android.graphics.Point
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import kotlin.random.Random

/**
 * @author Шаталов С.В.
 * @since 0.0.1
*/

/**
 * Менеджер звуков и музыки
 */
object Sound {
	/** Идентификатор текущего трека музыки */
	@JvmField var track         = -1
	
	// Список идентификаторов звуков
	private val sndIDs          = mutableListOf<Int>()
	
	// Список идентификаторов треков
	private var musIDs          = IntArray(0)
	
	// Менеджер звуков
	private var sp: SoundPool?  = null
	
	// Менеджер музыки
	private var mp: MediaPlayer?= null
	
	// Громкость звуков
	private var volumeSnd       = 1f
	
	// Громкость музыки
	private var volumeMus       = 1f
	
	/**
	 * Начальная инициализация менеджеров звуков и музыки
	 *
	 * @param context контекст
	 * @param streams количество звуковых потоков
	 * @param listSnd список звуков
	 * @param listMus список музыкальных треков
	 */
	@JvmStatic fun initialize(context: Context, streams: Int, listSnd: Array<String>, listMus: IntArray) {
		close()
		musIDs = listMus
		sp = SoundPool(streams, AudioManager.STREAM_MUSIC, 0)
		listSnd.forEach { sndIDs.add(sp?.load(context.assets.openFd(it), 0) ?: -1) }
	}
	
	/** Остановить воспроизведение музыки */
	@JvmStatic fun stopMusic() {
		if(isPlayMusic()) mp?.stop()
		mp?.release(); mp = null
		track = -1
	}
	
	/** Возобновить воспроизведение звуков и музыки */
	@JvmStatic fun resumeAll() {
		sp?.autoResume(); mp?.start()
	}
	
	/** Остановить воспроизведение звуков и музыки */
	@JvmStatic fun stopAll() {
		sp?.autoPause(); stopMusic()
	}
	
	/** Приостановить воспроизведение звуков и музыки */
	@JvmStatic fun pauseAll() {
		sp?.autoPause(); mp?.pause()
	}
	
	/** Уничтожить менеджеры звуков и музыки */
	@JvmStatic fun close() {
		sp?.release()
		stopMusic()
		sndIDs.clear()
		musIDs = intArrayOf()
		sp = null
	}
	
	/** Воспроизведение звукового эффекта [idx] с громкостью [volume] */
	@JvmStatic fun playSound(idx: Int, volume: Float) {
		sp?.play(sndIDs[idx], volume, volume, 0, 0, 1f)
	}
	
	/** Установка громкости музыки [volMus] и звуковых эффектов [volSnd] */
	@JvmStatic fun setVolume(volSnd: Float, volMus: Float) {
		volumeSnd = volSnd
		volumeMus = volMus
		mp?.apply { if(isPlaying) setVolume(volMus, volMus) }
	}
	
	/** Запуск на воспроизведение музыки в случайном порядке */
	@JvmStatic fun playRandomMusic(context: Context, loop: Boolean) {
		val count = musIDs.size - 1
		if(count > 0) playMusic(context, Random.nextInt(count) + 1, loop)
	}
	
	/** Признак воспроизведения музыки */
	@JvmStatic fun isPlayMusic() = mp?.isPlaying == true
	
	/**
	 * Запуск воспроизведения музыки
	 *
	 * @param context   контекст
	 * @param idx       индекс музыки
	 * @param loop      признак зацикленности
	 * @param volume    громкость, если 0 - использовать общую громкость
	 */
	@JvmStatic fun playMusic(context: Context, idx: Int, loop: Boolean, volume: Float = 0f) {
		val vm = if(volume == 0f) volumeMus else volume
		if(isPlayMusic() && idx == track) {
			mp?.setVolume(vm, vm)
		}
		else {
			stopMusic()
			if(vm > 0f && idx >= 0 && idx < musIDs.size) {
				mp = MediaPlayer.create(context, musIDs[idx])?.apply {
					setVolume(vm, vm)
					isLooping = loop
					track = idx
					seekTo(0)
					start()
				}
			}
		}
	}
	
	/**
	 * Запуск звукого эффекта на воспроизведение
	 *
	 * @param idx    индекс звука
	 * @param from   позиция источника
	 * @param to     позиция приемника
	 * @param repeat количество повторений
	 */
	@JvmStatic fun playSound(idx: Int, from: Point = Point(), to: Point = Point(), repeat: Int = 0) {
		if(volumeSnd > 0f && idx >= 0 && idx < sndIDs.size) {
			val left = if(from.x < to.x || from.y < to.y) 0.5f else 1f
			val right = if(from.x >= to.x || from.y >= to.y) 0.5f else 1f
			sp?.play(sndIDs[idx], volumeSnd * left, volumeSnd * right, 0, repeat, 1f)
		}
	}
}
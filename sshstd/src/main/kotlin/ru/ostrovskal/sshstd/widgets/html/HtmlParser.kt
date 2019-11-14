package ru.ostrovskal.sshstd.widgets.html

import android.content.res.AssetManager
import ru.ostrovskal.sshstd.utils.debug
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.Reader

/** Текст тега */
const val HTML_TEXT_TAG         = 0

/** Открывающий тег */
const val HTML_OPEN_TAG         = 1

/** Закрывающий тег*/
const val HTML_CLOSE_TAG        = 2

/** Закрывающий тег при пробросе */
const val HTML_CLOSE_WRONG_TAG  = 3

/**
 * @author Шаталов С.В.
 *
 * @since 0.7.1
 */

/** Класс, реализующий парсер HTML */
class HtmlParser {
	private val sb = StringBuilder(32)
	private var ch = ' '
	private var next = -1
	private var wrongTag = ""
	
	/** Атрибуты текущего тега */
	@JvmField val attrs = mutableMapOf<String, String>()

	/** Признак пустого тега */
	@JvmField var isEmpty = false

	/** Номер текущей строки */
	@JvmField var line = 1
	
	private fun Reader.skipSpace() {
		while(next != -1) {
			ch = next.toChar()
			if(!ch.isWhitespace()) break
			if(ch == '\n') line++
			next = read()
		}
	}
	
	private fun Reader.readWord(isTag: Boolean, isVal: Boolean, out: StringBuilder): Boolean {
		var isStr = false
		var isSlash = false
		skipSpace()
		out.clear()
		while(next != -1) {
			ch = next.toChar()
			if(isVal) {
				if(ch == '"') {
					next = read()
					if(isStr) break
					isStr = true
					continue
				}
			}
			if(!isStr) {
				if(ch == '/') {
					if(isSlash) break
					isEmpty = out.isNotEmpty()
					next = read()
					isSlash = true
					continue
				}
				if(ch.isLetter() || ch.isDigit() || ch == '.' || ch == '_' || ch == '-') {
					if(ch.isDigit() && out.isEmpty() && !isVal) break
					if(!isTag && ch == '.') break
				}
				else break
			}
			out.append(ch)
			next = read()
		}
		return isSlash
	}
	
	private fun Reader.readTag(out: StringBuilder): Int {
		attrs.clear()
		isEmpty = false
		next = read()
		var isSlash = readWord(true, isVal = false, out = out)
		if(out.isEmpty()) error("Ошибка. Недопустимое имя тега в строке ($line)!")
		if(!isSlash) {
			// attributes
			while(next != -1) {
				isSlash = readWord(false, isVal = false, out = sb)
				if(ch == '>') break
				if(sb.isEmpty()) error("Ошибка. Недопустимое имя атрибута тега <$out> в строке ($line)!")
				skipSpace()
				val nameAttr = sb.toString()
				if(ch != '=') error("Ошибка. Ожидается знак \"=\" для значения атрибута <$nameAttr> тега <$out> в строке ($line)!")
				next = read()
				skipSpace()
				readWord(false, isVal = true, out = sb)
				if(sb.isEmpty()) error("Ошибка. Недопустимое значение атрибута <$nameAttr> тега<$out> в строке ($line)!")
				attrs[nameAttr] = sb.toString()
			}
		}
		skipSpace()
		if(ch == '>') {
			if(isSlash) {
				if(isEmpty || attrs.isNotEmpty()) return 2
				return 1
			}
			return 0
		}
		error("Ошибка. Недопустимо задан тег <$out> в строке ($line)!")
	}
	
	private fun Reader.parseLoop(tag: String, block: (what: Int, text: StringBuilder) -> Unit): Boolean {
		try {
			val sb = StringBuilder()
			var prevCh = 0.toChar()
			next = read()
			while(next != -1) {
				ch = next.toChar()
				if(ch == '<') {
					if(sb.isNotEmpty() && sb.isNotBlank()) {
						block(HTML_TEXT_TAG, sb)
					}
					when(readTag(sb)) {
						0 -> {
							block(HTML_OPEN_TAG, sb)
							if(!parseLoop(sb.toString(), block)) {
								sb.clear()
								sb.append(tag)
								if(wrongTag != tag) {
									"Проброс на верхний уровень <$tag> <$wrongTag> в строке ($line)!".debug()
									block(HTML_CLOSE_WRONG_TAG, sb)
									return false
								} else {
									block(HTML_CLOSE_TAG, sb)
								}
								return true
							}
						}
						1 -> {
							wrongTag = sb.toString()
							if(wrongTag != tag) {
								block(HTML_CLOSE_WRONG_TAG, StringBuilder(tag))
								return false
							}
							block(HTML_CLOSE_TAG, sb)
							return true
						}
						2 -> {
							block(HTML_OPEN_TAG, sb)
							block(HTML_CLOSE_TAG, sb)
						}
					}
					sb.clear()
				}
				else {
					if(ch == '\n') { line++ }
					else if(ch == '\r' || ch.isWhitespace() && prevCh.isWhitespace()) { }
					else {
						sb.append(ch)
						prevCh = ch
					}
				}
				next = read()
			}
		} catch(e: IllegalStateException) {
			e.message.debug()
		}
		return true
	}
	
	/** Запуск парсера */
	fun parseFromAssets(assets: AssetManager, path: String, error: String, block: (what: Int, text: StringBuilder) -> Unit) {
		line = 1
		val r = try {
			assets.open(path)
		} catch(e: IOException) {
			ByteArrayInputStream(error.toByteArray())
		}
		r.reader().parseLoop("", block)
		r.close()
	}
}

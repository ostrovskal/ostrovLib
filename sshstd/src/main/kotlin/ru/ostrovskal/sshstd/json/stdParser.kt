package ru.ostrovskal.sshstd.json

import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.utils.info

/**
 * @author Шаталов С.В.
 * @since 0.5.1
 *
 */

/** Базовый класс, реализующий общую структуру JSON строки
 *
 * @property name   Имя поля
 * @property value  Значение поля
 */
open class JStruct(@JvmField val name: String?, @JvmField val value: String) {
	/** Текущая позиция парсера */
	@JvmField var pos = 0
	
	/** Дочерние элементы */
	@JvmField val child = mutableListOf<JStruct>()
	
	/** Парсинг */
	open fun parse() {}
	
	/** Для просмотра структуры после парсинга */
	fun printDebug(depth: Int) {
		val tabs = "".padEnd(depth, '\t')
		"$tabs $name <$value>".info()
		child.forEach {
			it.printDebug(depth + 1)
		}
	}
}

/** Класс, реализующий структуру JSON объекта */
class JObject(name: String?, value: String) : JStruct(name, value) {
	override fun parse() {
		while(true) {
			// "name"
			val nName = Parser.parseString(this)
			// :
			if(Parser.nextLexem(this, true) != JSON_COLON) Parser.throwsError("После имени объекта отсутствует двоеточие!", this)
			// [|{|"|0..9
			val type = Parser.nextLexem(this, false)
			val nValue = Parser.parseValue(this)
			val obj = when(type) {
				JSON_KAV, JSON_DIGIT,
				JSON_LETTER          -> JProperty(nName, nValue)
				JSON_LBRACE          -> JObject(nName, nValue)
				JSON_LBRACKET        -> JList(nName, nValue)
				else                 -> Parser.throwsError("Неизвестное значение!", this)
			}
			child.add(obj)
			obj.parse()
			// следующий элемент или зывершение
			val lexem = Parser.nextLexem(this, true)
			if(lexem == JSON_EOF) break
			else if(lexem != JSON_COMMA) Parser.throwsError("Ошибка при парсинге объекта!", this)
		}
	}
}

/** Класс, реализующий структуру JSON списка */
class JList(name: String?, value: String) : JStruct(name, value) {
	override fun parse() {
		while(true) {
			val type = Parser.nextLexem(this, false)
			val nValue = Parser.parseValue(this)
			val obj = when(type) {
				JSON_KAV, JSON_DIGIT,
				JSON_LETTER          -> JProperty(null, nValue)
				JSON_LBRACE          -> JObject(null, nValue)
				JSON_LBRACKET        -> JList(null, nValue)
				else                 -> Parser.throwsError("Неизвестное значение!", this)
			}
			child.add(obj)
			obj.parse()
			// следующий элемент или зывершение
			val lexem = Parser.nextLexem(this, true)
			if(lexem == JSON_EOF) break
			else if(lexem != JSON_COMMA) Parser.throwsError("Ошибка при парсинге списка!", this)
		}
	}
}

/** Класс, реализующий структуру JSON свойства */
class JProperty(name: String?, value: String) : JStruct(name, value)

/** Объект для парсинга JSON структуры */
object Parser {
	/** Текущий символ */
	@JvmField var lexChar = ' '
	
	/** Парсинг */
	fun parse(json: String) = JObject(null, json.substring(1, json.length - 1)).apply { parse() }
	
	/** Парсинг строки */
	fun parseString(stk: JStruct): String {
		// начальная кавычка
		if(nextLexem(stk, false) != JSON_KAV) throwsError("В данной позиции строка не обнаружена!", stk)
		val begin = ++stk.pos
		while(true) {
			val lexem = nextLexem(stk, true)
			if(lexem == JSON_EOF) throwsError("Неожиданный конец строки!", stk)
			else if(lexem == JSON_ESCAPE) {
				nextLexem(stk, true)
				if(lexChar == 'u') {
					stk.pos += 4
					if(stk.pos >= stk.value.length) throwsError("Недопустимая эскейп последовательность <u>!", stk)
				} else {
					if(!"\"\'nrtb\\bf".contains(lexChar)) throwsError("Неизвестная эскейп последовательность !", stk)
				}
			}
			else if(lexem == JSON_KAV) break
		}
		return stk.value.substring(begin, stk.pos - 1)
	}
	
	/** Парсинг числа */
	fun parseNumber(stk: JStruct): String {
		var lexem = nextLexem(stk, false)
		if(lexem != JSON_DIGIT && lexem != JSON_DOT) throwsError("В данной позиции число не обнаружено!", stk)
		val begin = stk.pos
		while(true) {
			lexem = nextLexem(stk, true)
			if(lexem == JSON_EOF || lexem != JSON_DIGIT && lexem != JSON_DOT) break
		}
		stk.pos--
		return stk.value.substring(begin, stk.pos)
	}
	
	/** Проверка на слово */
	fun isWord(stk: JStruct, word: String): String? {
		if(stk.value.startsWith(word, stk.pos)) {
			stk.pos += word.length
			return word
		}
		return null
	}

	/** Парсинг значения */
	fun parseValue(stk: JStruct): String {
		return when(val sign = nextLexem(stk, false)) {
			JSON_LBRACE,
			JSON_LBRACKET   -> {
				var count = 0
				val begin = stk.pos + 1
				val endSign = sign + 1
				while(true) {
					val lexem = nextLexem(stk, true)
					if(lexem == sign) {
						count++
					} else if(lexem == JSON_KAV) {
						stk.pos--
						parseString(stk)
					} else if(lexem == endSign) {
						count--
						if(count == 0) break
					} else if(lexem == JSON_EOF) throwsError("Неожиданный конец значения!", stk)
				}
				stk.value.substring(begin, stk.pos - 1)
			}
			JSON_DIGIT      -> parseNumber(stk)
			JSON_KAV        -> parseString(stk)
			JSON_LETTER     -> isWord(stk, "true") ?: isWord(stk, "false") ?: isWord(stk, "null") ?: throwsError("Неизветное значение выражения!", stk)
			else            -> throwsError("Неизвестный тип значения!", stk)
		}
	}
	
	/** Получение следующей лексемы */
	fun nextLexem(stk: JStruct, isNextPos: Boolean): Int {
		stk.pos--
		do {
			++stk.pos
			if(stk.pos >= stk.value.length) {
				if(isNextPos) stk.pos++
				return JSON_EOF
			}
			val ch = stk.value[stk.pos]
		} while("\n\r\t ".contains(ch))
		lexChar = stk.value[stk.pos]
		if(isNextPos) stk.pos++
		return when(lexChar) {
			':'         -> JSON_COLON
			','         -> JSON_COMMA
			'\"'        -> JSON_KAV
			'0', '1',
			'2', '3',
			'4', '5',
			'6', '7',
			'8', '9'    -> JSON_DIGIT
			'.'         -> JSON_DOT
			'\\'        -> JSON_ESCAPE
			'['         -> JSON_LBRACKET
			']'         -> JSON_RBRACKET
			'{'         -> JSON_LBRACE
			'}'         -> JSON_RBRACE
			else        -> {
				if("-+!@#$%^&*();<>=|/\'?~".contains(lexChar)) JSON_UNDEF else JSON_LETTER
			}
		}
	}

	/** Возбуждение ошибки с сообщением [msg] при парсинге структуры [stk] */
	fun throwsError(msg: String, stk: JStruct): Nothing {
		error("$msg position: <$stk.pos> instance: ${stk.value.substring(stk.pos..(stk.pos + 30))}")
	}
}

package ru.ostrovskal.sshstd

import com.dropbox.core.DbxException
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.DeleteErrorException
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.ListFolderErrorException
import com.dropbox.core.v2.files.ListFolderResult
import com.dropbox.core.v2.users.FullAccount
import ru.ostrovskal.sshstd.Common.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

/**
 * @author Шаталов С.В.
 * @since 1.0.0
 */

/** Класс, реализующий работу с облаком DropBox
 *
 * @param name  Имя сессии
 * @param token Секретный ключь
 * */
open class DropBox(name: String, token: String) {

    // Конфигурация
    private val config  = DbxRequestConfig.newBuilder(name).build()

    // Клиент
    private val client  = DbxClientV2(config, token)

    // Аккаунт
    private var account: FullAccount? = null

    /** Информация о файлах
     * @property name   Имя
     * @property path   Путь
     * @property rev    Ревизия. Если значение пустое, значит папка
     * */
    class FileInfo(@JvmField val name: String, @JvmField val path: String, @JvmField val rev: String)

    /** Вернуть информацию о профиле по запросу [what] */
    fun accountInfo(what: Int): String {
        if(account == null) account = client.users().currentAccount
        return account?.run {
            when (what) {
                DBX_EMAIL   -> email
                DBX_FIO     -> name.displayName
                DBX_LINK    -> referralLink
                DBX_PHOTO   -> profilePhotoUrl
                DBX_COUNTRY -> country
                else        -> ""
            }
        } ?: ""
    }

    /** Вернуть список файлов из папки [folder] */
    fun folders(folder: String): List<FileInfo>? {
        val list: ListFolderResult? = try {
            client.files().listFolder(folder)
        } catch (dbxf: ListFolderErrorException) { null
        } catch (dbxe: DbxException) { null
        } catch(e: Exception) { null }
        return list?.entries?.run {
            val iter = iterator()
            MutableList(size) {
                var name = ""; var path = ""; var rev = ""
                if (iter.hasNext()) {
                    iter.next().apply {
                        name = this.name
                        path = pathDisplay
                        rev =  (this as? FileMetadata)?.rev ?: ""
                    }
                }
                FileInfo(name, path, rev)
            }
        }
    }

    /** Скачать файл [file] и записать его в [path] */
    fun download(file: FileInfo, path: String): Boolean {
        val ba = download(file) ?: return false
        if(ba.isEmpty()) return false
        File(path).writeBytes(ba)
        return true
    }

    /** Скачать файл [file] */
    fun download(file: FileInfo): ByteArray? {
        return try {
            val ret = ByteArrayOutputStream()
            val downloader = client.files()?.download(file.path.toLowerCase(Locale.ROOT), file.rev)
            downloader?.download(ret)
            downloader?.close()
            ret.toByteArray()
        } catch (dbxf: ListFolderErrorException) { null
        } catch (dbxe: DbxException) { null
        } catch(e: Exception) { null }
    }

    /** Закачать файл [path] в облако в папку [pathTo] */
    fun upload(path: String, pathTo: String) = upload(File(path).readBytes(), pathTo)

    /** Закачать массив байт [file] в облако в папку [path] */
    fun upload(file: ByteArray, path: String): Boolean {
        return try {
            client.files()?.uploadBuilder(path)?.uploadAndFinish(ByteArrayInputStream(file))
            true
        } catch (dbxe: DbxException) { false
        } catch (e: Exception) { false }
    }

    /** Удаление файла или папки */
    fun remove(path: String): Boolean {
        return try {
            client.files()?.deleteV2(path); true
        } catch (dbxd: DeleteErrorException) { false
        } catch (dbxe: DbxException) { false
        } catch (e: Exception) { false }
    }
}
package ru.ostrovskal.sshstd

/** Аннотация для указания того, что поле будет автоматически сохраняться и восстанавливаться */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class STORAGE

/**
 *  Аннотация для указания автоматического добавления поля [name] таблицы в набор, при выполенении DML оператора БД
 *
 * @property name Имя поля
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class SqlField(val name: String = "")

/**
 * Аннотация для указания имени поля, при сериализации/десериализации в json
 *
 * @property name Имя поля
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonName(val name: String)

/**
 *  Аннотация для указания имени класса адаптера [adapterClass], при сериализации/десериализации в json
 *
 * @property adapterClass Класс адаптера
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonAdapter(val adapterClass: String)
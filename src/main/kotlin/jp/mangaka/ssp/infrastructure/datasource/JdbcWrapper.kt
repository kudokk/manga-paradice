package jp.mangaka.ssp.infrastructure.datasource

import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomBeanPropertySqlParameterSource
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import jp.mangaka.ssp.infrastructure.datasource.mapper.KDataClassRowMapper
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSource
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.function.UnaryOperator
import kotlin.reflect.KClass

/**
 * COMPASSでの利用に合わせたJdbcTemplateの拡張
 */
class JdbcWrapper(
    private val jdbc: NamedParameterJdbcTemplate
) {
    /**
     * 引数で指定したクエリ・パラメータに合致する単一のレコードを取得する.
     *
     * @param query クエリ
     * @param param パラメータ
     * @param clazz 戻り値の型
     * @return 単体オブジェクトへのクエリの取得結果. レコードが存在しない場合は null
     */
    fun <T : Any> queryForObject(query: String, param: SqlParameterSource, clazz: KClass<T>): T? {
        return try {
            // RowMapperを使わなくていい場合は別の内容を呼び出す
            if (PRIMITIVE_CLASS_SET.contains(clazz.javaObjectType)) {
                jdbc.queryForObject(query, param, clazz.javaObjectType)
            } else {
                jdbc.queryForObject<T>(query, param, KDataClassRowMapper.newInstance(clazz))
            }
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    /**
     * 引数で指定したクエリ・パラメータに合致するレコードを取得する.
     *
     * @param query クエリ
     * @param param パラメータ
     * @param clazz 戻り値の型
     * @return 複数オブジェクトへのクエリの取得結果. レコードが存在しない場合は 空のリスト
     */
    fun <T : Any> query(query: String, param: SqlParameterSource, clazz: KClass<T>): List<T> {
        return try {
            // RowMapperを使わなくていい場合は別の内容を呼び出す
            if (PRIMITIVE_CLASS_SET.contains(clazz.javaObjectType)) {
                jdbc.queryForList(query, param, clazz.javaObjectType)
            } else {
                jdbc.query(query, param, KDataClassRowMapper.newInstance(clazz))
            }
        } catch (e: EmptyResultDataAccessException) {
            emptyList()
        }
    }

    /**
     * 引数で指定したクエリに合致するレコードを取得する.
     *
     * @param query クエリ
     * @param clazz 戻り値の型
     * @return 複数オブジェクトへのクエリの取得結果. レコードが存在しない場合は 空のリスト
     */
    fun <T : Any> query(query: String, clazz: KClass<T>): List<T> =
        query(query, CustomMapSqlParameterSource(), clazz)

    /**
     * 引数で指定したクエリ・インサートオブジェクトの内容でテーブルに登録する.
     *
     * @param query クエリ
     * @param insertObject インサートオブジェクト
     * @return JDBCドライバが出力する影響を受けた行数
     */
    fun insertExecuteAndReturnId(query: String, insertObject: Any): Long {
        jdbc.update(query, CustomBeanPropertySqlParameterSource(insertObject))

        return jdbc.jdbcTemplate.queryForObject<Long>("SELECT LAST_INSERT_ID()", Long::class.java)!!
    }

    /**
     * 引数で指定したインサートオブジェクトの内容を builder で指定したテーブルに登録する.
     *
     * @param builder SimpleJdbcInsertの作成関数
     * @param insertObject インサートオブジェクト
     * @return JDBCドライバが出力する影響を受けた行数
     */
    fun insertExecute(builder: UnaryOperator<SimpleJdbcInsert>, insertObject: Any): Int {
        return builder.apply(SimpleJdbcInsert(jdbc.jdbcTemplate))
            .execute(CustomBeanPropertySqlParameterSource(insertObject))
    }

    /**
     * 引数で指定したインサートオブジェクトの内容を builder で指定したテーブルに登録する.
     *
     * @param builder SimpleJdbcInsertの作成関数
     * @param insertObject インサートオブジェクト
     * @return 自動採番されたID
     */
    fun insertExecuteAndReturnId(builder: UnaryOperator<SimpleJdbcInsert>, insertObject: Any): Number {
        return builder.apply(SimpleJdbcInsert(jdbc.jdbcTemplate))
            .executeAndReturnKey(CustomBeanPropertySqlParameterSource(insertObject))
    }

    /**
     * 引数で指定したインサートオブジェクトの内容を builder で指定したテーブルに一括登録する.
     *
     * @param builder SimpleJdbcInsertの作成関数
     * @param insertObjects インサートオブジェクトのリスト
     * @return JDBCドライバが出力する影響を受けた行数の配列
     */
    fun insertExecuteBatch(builder: UnaryOperator<SimpleJdbcInsert>, insertObjects: Collection<Any>): IntArray {
        val records = insertObjects
            .map { CustomBeanPropertySqlParameterSource(it) }
            .toTypedArray()

        return builder.apply(SimpleJdbcInsert(jdbc.jdbcTemplate)).executeBatch(*records)
    }

    /**
     * 引数で指定したクエリ・パラメータの内容でテーブルに一括登録する.
     *
     * @param query クエリ
     * @param param パラメータ
     * @return 一括登録結果
     */
    fun bulkInsertAndReturnResult(query: String, param: SqlParameterSource): BulkInsertResult {
        // updateでinsertクエリを実行し、挿入した行数を取得
        val rowCount = jdbc.update(query, param)

        // 最後に実行された挿入クエリで挿入されたものの内最初のIDを取得
        val lastInsertId = jdbc.jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long::class.java)!!
        return BulkInsertResult(rowCount, lastInsertId)
    }

    /**
     * 引数で指定したクエリ・更新オブジェクトの内容でテーブルを更新する.
     *
     * @param query クエリ
     * @param updateObject 更新オブジェクト
     * @return JDBCドライバが出力する影響を受けた行数の配列
     */
    fun update(query: String, updateObject: Any): Int {
        return update(query, CustomBeanPropertySqlParameterSource(updateObject))
    }

    /**
     * 引数で指定したクエリ・パラメータの内容でテーブルを更新する.
     *
     * @param query クエリ
     * @param param 更新オブジェクト
     * @return JDBCドライバが出力する影響を受けた行数の配列
     */
    fun update(query: String, param: SqlParameterSource): Int {
        return jdbc.update(query, param)
    }

    /**
     * 引数で指定したクエリ・更新オブジェクトの内容でテーブルを更新する.
     *
     * @param query クエリ
     * @param updateObjects 更新オブジェクトのリスト
     * @return JDBCドライバが出力する影響を受けた行数の配列
     */
    fun bachUpdate(query: String, updateObjects: Collection<Any>): IntArray {
        return jdbc.batchUpdate(query, updateObjects.map(::CustomBeanPropertySqlParameterSource).toTypedArray())
    }

    data class BulkInsertResult(private val rowCount: Int, private val lastInsertId: Long) {
        /**
         * @param transformer IDの変換方法
         * @return 一括登録で自動採番されたIDのリスト
         */
        fun <T> getIds(transformer: (Long) -> T): List<T> = (0 until rowCount).map { transformer(it + lastInsertId) }
    }

    companion object {
        private val PRIMITIVE_CLASS_SET = setOf(
            // 数値・文字列系
            Byte::class,
            Short::class,
            Integer::class,
            BigInteger::class,
            Float::class,
            Double::class,
            BigDecimal::class,
            String::class,
            // TimeApi系
            LocalDate::class,
            LocalDateTime::class,
            LocalTime::class
        ).map { it.javaObjectType }
    }
}

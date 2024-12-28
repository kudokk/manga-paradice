package jp.mangaka.ssp.util.localfile

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jp.mangaka.ssp.util.exception.CompassManagerException
import jp.mangaka.ssp.util.localfile.valueobject.LocalFileType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Component
class LocalFileUtils(
    @Value("\${app.constant.localDirPath}") localDirPath: String
) {
    val rootPath: Path = if (localDirPath.isNotEmpty()) {
        Paths.get(localDirPath)
    } else {
        Paths.get(System.getProperty("user.dir"), "nfs")
    }
    val jsonMapper = jacksonObjectMapper()

    /**
     * 引数で指定したファイルを読み込みます.
     *
     * @param filePath ファイルのパス（ルートディレクトリからの相対パス）
     * @return 読み込んだファイルの内容
     * @throws java.io.IOException ファイルの読み込みに失敗した場合
     */
    fun read(filePath: Path): ByteArray {
        return Files.readAllBytes(rootPath.resolve(filePath))
    }

    /**
     * 引数で指定したファイルを指定のクラスのオブジェクトとして読み込みます.
     *
     * @param fileType 読み込むファイルに対応するクラス
     * @return 読み込んだファイルのオブジェクト
     * @throws jp.mangaka.ssp.util.exception.CompassManagerException ファイルの読み込みに失敗した場合
     */
    fun <T : Any> loadConfig(fileType: LocalFileType<T>): T = try {
        val filePath = Paths.get(fileType.filePath)
        val bytes = read(filePath)

        when (fileType) {
            LocalFileType.CommonConfig -> jsonMapper.readValue(bytes, fileType.fileType.javaObjectType)
        }
    } catch (e: Exception) {
        throw CompassManagerException("ファイルの読み込みに失敗しました。fileType=$fileType", e)
    }
}

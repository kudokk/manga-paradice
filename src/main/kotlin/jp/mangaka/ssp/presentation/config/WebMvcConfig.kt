package jp.mangaka.ssp.presentation.config

import jp.mangaka.ssp.application.valueobject.IdValueObjectConverterFactory
import jp.mangaka.ssp.presentation.config.interceptor.CoAccountRequestInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.ConverterRegistry
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.VersionResourceResolver

@Configuration
class WebMvcConfig(
    private val coAccountRequestInterceptor: CoAccountRequestInterceptor
) : WebMvcConfigurer {
    override fun addFormatters(registry: FormatterRegistry) {
        // 変換サービス類の登録、SpringのconversionServiceと共通サービス両方に登録が必要
        // DefaultConversionService.getSharedInstanceへの登録はどこでもできるが、分かりやすさのためここで登録している
        listOf(registry, DefaultConversionService.getSharedInstance() as ConverterRegistry).forEach { r ->
            r.addConverterFactory(IdValueObjectConverterFactory())
        }
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
            .addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/")
            // キャッシュ対策 jsやcssなどの外部ファイル読み込み時のキャッシュバスター
            .resourceChain(true)
            // コンテンツデータのMD5ハッシュ値によるバージョニング機能の有効化
            .addResolver(VersionResourceResolver().addContentVersionStrategy("/**"))
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        listOf(coAccountRequestInterceptor).forEach {
            registry
                .addInterceptor(it)
                // 適用対象のパス(パターン)を指定する
                .addPathPatterns("/**")
                // 除外するパス(パターン)を指定する
                .excludePathPatterns("/static/**")
        }
    }
}

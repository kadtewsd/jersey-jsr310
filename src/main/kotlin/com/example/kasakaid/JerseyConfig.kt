package com.example.kasakaid

import com.example.kasakaid.controller.JerseyController
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonTokenId
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.glassfish.jersey.server.ResourceConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.ws.rs.ApplicationPath
import my.controller.MyJerseyResource


@ApplicationPath("/app")
@Component
class JerseyConfig : ResourceConfig() {

    init {
        this.register(JerseyController::class.java)
        this.register(MyJerseyResource::class.java)
    }
}

@Configuration
class Configurable {
    /**
     * これで登録するとすべて Unix Time になってしまう
     */
    @Bean
    fun myObjectMapper(environment: Environment): ObjectMapper {
        val m = JavaTimeModule()
        m.addSerializer(LocalDateTime::class.java, MyLocalDateTimeSerializer(
                environment["spring.jackson.time-zone"]!!,
                environment["spring.jackson.date-format"]!!
        ))
        m.addDeserializer(LocalDateTime::class.java, MyLocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        return Jackson2ObjectMapperBuilder.json()
                .modulesToInstall(m, KotlinModule())
                // ここを無効化しないと Unix Time でフォーマットされてしまう。
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToEnable(SerializationFeature.INDENT_OUTPUT)
                .build()

    }
}

class MyLocalDateTimeSerializer(
        private val timeZone: String,
        private val format: String
) : JsonSerializer<LocalDateTime>() {

    override fun serialize(value: LocalDateTime?, gen: JsonGenerator?, provider: SerializerProvider?) {
        val v = value ?: run {
            gen!!.writeString("")
            return
        }
        val a = v.atZone(ZoneId.of(timeZone))
        gen!!.writeString(a.format(DateTimeFormatter.ofPattern(format)))
    }

}

/**
 * テスト時の readEntity では、なにをどうやっても、LocalDateTime の各メンバーがバラバラになって取得することしかできない。
 * "localDateTime":{"dayOfYear":2,"dayOfWeek":"WEDNESDAY","month":"JANUARY","dayOfMonth":2,"year":2019,"monthValue":1,"hour":13,"minute":3,"second":1,"nano":0,"chronology":{"id":"ISO","calendarType":"iso8601"}}
 * そのため、tokenId の開始が ID_STRING にならず、START_OBJECT になり、ハンドリングできない構造であると判断されて、規定のでシリアライザーでは例外が吐かれてしまう。
 * しかたがないので、これでデシリアライズする。
 * ただし、REST でリクエストがくると、入力内容 (2019-01-01T12:00:00.000) がそのままサーバー側に到達するので、ID_STRING でデシリアライズできていた。
 */
class MyLocalDateTimeDeserializer(
        pattern: DateTimeFormatter
//) : StdDeserializer<LocalDateTime>(LocalDateTime::class.java) {
//): JsonDeserializer<LocalDateTime>() {
) : LocalDateTimeDeserializer(pattern) {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalDateTime {

        return if (
                p!!.hasTokenId(JsonTokenId.ID_STRING) ||
                p.isExpectedStartArrayToken) {
            super.deserialize(p, ctxt)
        } else {
//        return super.deserialize(p, ctxt)
            val node = p.codec.readTree(p) as ObjectNode
            LocalDateTime.of(
                    node["year"].asInt(),
                    node["monthValue"].asInt(),
                    node["dayOfMonth"].asInt(),
                    node["hour"].asInt(),
                    node["minute"].asInt(),
                    node["second"].asInt()
            )
        }
    }
}

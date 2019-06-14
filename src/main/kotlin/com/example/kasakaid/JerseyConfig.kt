package com.example.kasakaid

import com.example.kasakaid.controller.JerseyController
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
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
import java.time.Instant
import com.fasterxml.jackson.databind.node.TextNode


@ApplicationPath("/app")
@Component
class JerseyConfig : ResourceConfig() {

    init {
        this.register(JerseyController::class.java)
    }
}

//class CustomObjectMapper(objectMapper: ObjectMapper) : ObjectMapper() {
//
//    init {
//        BeanUtils.copyProperties(objectMapper, this)
//    }
//
//    fun remove(value: Class<*>) {
//        _registeredModuleTypes.remove(value)
//    }
//}

@Configuration
class Configurable {
    /**
     * これで登録するとすべて Unix Time になってしまう
     */
    @Bean
    fun myObjectMapper(environment: Environment): ObjectMapper {
        val pattern = DateTimeFormatter.ofPattern(environment["spring.jackson.date-format"])
        val m = JavaTimeModule()
        m.addSerializer(LocalDateTime::class.java, MyLocalDateTimeSerializer(environment))
//        m.addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(pattern))
        m.addDeserializer(LocalDateTime::class.java, MyLocalDateTimeDeserializer(pattern))
//        val om = ObjectMapper()
//        return om.registerModules(Jdk8Module(), m, KotlinModule(), ParameterNamesModule(), JsonComponentModule())
//                .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
//                .enable(SerializationFeature.INDENT_OUTPUT)
        return Jackson2ObjectMapperBuilder.json()
//        val v1: ObjectMapper = Jackson2ObjectMapperBuilder.json()
//                .modules(Jdk8Module(), m, KotlinModule(), ParameterNamesModule(), JsonComponentModule())
//                .modulesToInstall(Jdk8Module(), m, KotlinModule(), ParameterNamesModule(), JsonComponentModule())
                .modulesToInstall(m, KotlinModule())
                // ここを無効化しないと Unix Time でフォーマットされてしまう。
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToEnable(SerializationFeature.INDENT_OUTPUT)
                .build()

//        Jackson2ObjectMapperBuilder.json()
//                .configure(v1)

    }
}

class MyLocalDateTimeSerializer(private val environment: Environment) : JsonSerializer<LocalDateTime>() {

    override fun serialize(value: LocalDateTime?, gen: JsonGenerator?, provider: SerializerProvider?) {
        val v = value ?: run {
            gen!!.writeString("")
            return
        }
        val a = v.atZone(ZoneId.of(this.environment["spring.jackson.time-zone"]!!))
        gen!!.writeString(a.format(DateTimeFormatter.ofPattern(environment["spring.jackson.date-format"])))
    }

}

/**
 * なにをどうやっても、LocalDateTime の各メンバーがバラバラになって取得することしかできない。
 * しかたがないので、これでデシリアライズする。
 * ただし、REST でシリアライズするときは問題なくシリアライズできていた。
 */
class MyLocalDateTimeDeserializer(
        private val pattern: DateTimeFormatter
//) : StdDeserializer<LocalDateTime>(LocalDateTime::class.java) {
) : JsonDeserializer<LocalDateTime>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalDateTime {
//        val node = p!!.codec?.readTree(p)
//        return p!!.codec.readValue(p, LocalDateTime::class.java)
        val codec = p!!.codec
        val node = codec.readTree(p) as ObjectNode
//        val dateString = node.textValue()
//        val instant = Instant.parse(node.asText())
//        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return LocalDateTime.of(
                node["year"].asInt(),
                node["monthValue"].asInt(),
                node["dayOfMonth"].asInt(),
                node["hour"].asInt(),
                node["minute"].asInt(),
                node["second"].asInt()
        )
    }
}

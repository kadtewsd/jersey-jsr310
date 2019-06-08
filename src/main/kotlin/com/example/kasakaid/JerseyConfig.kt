package com.example.kasakaid

import com.example.kasakaid.controller.JerseyController
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import org.glassfish.jersey.server.ResourceConfig
import org.springframework.beans.BeanUtils
import org.springframework.boot.jackson.JsonComponentModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.ws.rs.ApplicationPath


@ApplicationPath("/app")
@Component
class JerseyConfig(environment: Environment) : ResourceConfig() {

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
class Cingigurable {
    /**
     * これで登録するとすべて Unix Time になってしまう
     */
    @Bean
    fun myObjectMapper(environment: Environment): ObjectMapper {
        val m = JavaTimeModule()
        m.addSerializer(LocalDateTime::class.java, MyLocalDateTimeDeserializer(environment))
        return Jackson2ObjectMapperBuilder.json()
                .modules(Jdk8Module(), m, KotlinModule(), ParameterNamesModule(), JsonComponentModule())
                // ここを無効化しないと Unix Time でフォーマットされてしまう。
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToEnable(SerializationFeature.INDENT_OUTPUT)
                .build()

    }
//
//    @Bean
//    fun mappingJackson2HttpMessageConverter(objectMapper: ObjectMapper, environment: Environment): MappingJackson2HttpMessageConverter {
//        val myO = CustomObjectMapper(objectMapper)
//        val jsonConverter = MappingJackson2HttpMessageConverter()
//        val m = JavaTimeModule()
//        m.addSerializer(LocalDateTime::class.java, MyLocalDateTimeDeserializer(environment))
//        // ここの registerModule が登録済みになりスルーされて登録したシリアライザーが動作しない
//        myO.registerModule(m)
//        myO.remove(JavaTimeModule::class.java)
//        myO.registerModule(m)
//        jsonConverter.objectMapper = objectMapper
//        return jsonConverter
//    }
}

class MyLocalDateTimeDeserializer(private val environment: Environment) : JsonSerializer<LocalDateTime>() {

    override fun serialize(value: LocalDateTime?, gen: JsonGenerator?, provider: SerializerProvider?) {
        val v = value?: run  {
            gen!!.writeString("")
            return
        }
        val a = v.atZone(ZoneId.of(this.environment["spring.jackson.time-zone"]!!))
        gen!!.writeString(a.format(DateTimeFormatter.ofPattern(environment["spring.jackson.date-format"])))
    }

}

package com.example.kasakaid.config

import com.example.kasakaid.MyLocalDateTimeSerializer
import com.example.kasakaid.controller.JerseyController
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.client.JerseyClientBuilder
import org.glassfish.jersey.internal.inject.Custom
import org.glassfish.jersey.jackson.JacksonFeature
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.JaxRSFeature
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.test.JerseyTest
import org.junit.rules.ExternalResource
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Component
import java.io.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import javax.annotation.Priority
import javax.ws.rs.Consumes
import javax.ws.rs.Produces
import javax.ws.rs.WebApplicationException
import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Invocation
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.ext.ContextResolver
import javax.ws.rs.ext.MessageBodyReader
import javax.ws.rs.ext.MessageBodyWriter
import javax.ws.rs.ext.Provider


@Configuration
class KasakaidConfiguration {

    @Bean
    fun jacksonJaxbJsonProvider(environment: Environment, myObjectMapper: ObjectMapper): JacksonJaxbJsonProvider {
        val jsonConverter = JacksonJaxbJsonProvider()
//        val m = JavaTimeModule()
//        m.addSerializer(LocalDateTime::class.java, MyLocalDateTimeSerializer(environment))
        // ここの registerModule が登録済みになりスルーされて登録したシリアライザーが動作しない
//        objectMapper.registerModule(m)
        return MixInJacksonJsonProvider(objectMapper = myObjectMapper)
                .enable(JaxRSFeature.DYNAMIC_OBJECT_MAPPER_LOOKUP) as JacksonJaxbJsonProvider
    }
}

@Component
class TestJerseyConfig(
        private val applicationContext: ApplicationContext,
        private val myObjectMapper: ObjectMapper,
        private val jacksonJaxbJsonProvider: JacksonJaxbJsonProvider
) : ExternalResource() {

    private val jerseyTest: JerseyTest

    init {
        this.jerseyTest = object : JerseyTest() {
            override fun configure(): ResourceConfig {
                return ResourceConfig(JerseyController::class.java)
                        // ここにあるコードを書いても response のシリアライズに影響はない
//                        .register(JacksonFeature::class.java)
//                        .register(myObjectMapper, ObjectMapper::class.java)
//                        .register(jacksonJaxbJsonProvider, JacksonJaxbJsonProvider::class.java)
//                        .register(JacksonConfigurator(myObjectMapper), JacksonConfigurator::class.java)
//                        .register(MixInJacksonJsonProvider(myObjectMapper), JacksonJaxbJsonProvider::class.java)
//                        .register(ObjectMapperProvider<T>(myObjectMapper), ObjectMapperProvider<T>(objectMapper = myObjectMapper)::class.java)
//                        .register(JacksonConfigurator::class.java)
//                        .register(MixInJacksonJsonProvider::class.java)
//                        .register(ObjectMapperProvider<DateTimeResult>(objectMapper = myObjectMapper)::class.java)
                        .property("contextConfig", applicationContext)
            }

            /**
             * クライアント側でデシリアライズするのでシリアライザーを登録
             */
            override fun getClient(): Client {
//                return ClientBuilder.newClient()
                return JerseyClientBuilder.createClient()
                        .configuration
//                        .register(myObjectMapper)
                        // 下記のうちいずれかで response 時に該当の objectMapper が使えるようになる。
//                        .register(JacksonConfigurator(myObjectMapper))
//                        .register(JacksonConfigurator())
                        .register(MixInJacksonJsonProvider(objectMapper = myObjectMapper))
//                        .register(ObjectMapperProvider<Serializable>(myObjectMapper))
                        .client
            }

            override fun configureClient(config: ClientConfig?) {
//                config!!.register(JacksonConfigurator::class.java)
//                config!!.register(JacksonConfigurator(myObjectMapper))
                config!!.register(JacksonConfigurator())
//                super.configureClient(config)
            }

        }
        println("initialize...")
        // 起動しないと NullPointerExceptin
        this.jerseyTest.setUp()

    }

    fun webTarget(url: String): WebTarget {
        return this.jerseyTest.target(url)
    }

    fun get(url: String): Invocation.Builder {
        return this.webTarget(url)
                .request()
                .accept(MediaType.APPLICATION_JSON)
    }
}


@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class JacksonConfigurator(
//        private val myObjectMapper: ObjectMapper
) : ContextResolver<ObjectMapper> {

    init {
//        myObjectMapper.dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    }

    override fun getContext(arg0: Class<*>): ObjectMapper {
//        return myObjectMapper
        val m = JavaTimeModule()
        return Jackson2ObjectMapperBuilder.json()
                .modules(m)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build()
    }
}

@Provider
@Priority(0)
@Custom
class MixInJacksonJsonProvider(objectMapper: ObjectMapper) : JacksonJaxbJsonProvider() {
    init {
        setMapper(objectMapper)
    }
}


@Provider
@Produces(value = [MediaType.APPLICATION_JSON])
@Consumes(value = [MediaType.APPLICATION_JSON])
class ObjectMapperProvider<T : Serializable>(private val objectMapper: ObjectMapper) : MessageBodyReader<T>, MessageBodyWriter<T> {

    override fun isReadable(type: Class<*>, genericType: Type,
                            annotations: Array<Annotation>, mediaType: MediaType): Boolean {
        return true
    }

    @Throws(IOException::class, WebApplicationException::class)
    override fun readFrom(type: Class<T>, genericType: Type, annotations: Array<Annotation>,
                          mediaType: MediaType, httpHeaders: MultivaluedMap<String, String>,
                          entityStream: InputStream): T {

        val reader = InputStreamReader(entityStream, "UTF-8")
        reader.use {
            return objectMapper.readValue(it, type)
        }
    }

    override fun isWriteable(type: Class<*>, genericType: Type,
                             annotations: Array<Annotation>, mediaType: MediaType): Boolean {
        return true
    }

    override fun getSize(t: T, type: Class<*>, genericType: Type,
                         annotations: Array<Annotation>, mediaType: MediaType): Long {
        return -1
    }

    @Throws(IOException::class, WebApplicationException::class)
    override fun writeTo(t: T, type: Class<*>, genericType: Type, annotations: Array<Annotation>,
                         mediaType: MediaType, httpHeaders: MultivaluedMap<String, Any>,
                         entityStream: OutputStream) {

        val printWriter = PrintWriter(entityStream)
        printWriter.use {
            val json = objectMapper.writeValueAsString(it)
            it.write(json)
            it.flush()
        }
    }
}




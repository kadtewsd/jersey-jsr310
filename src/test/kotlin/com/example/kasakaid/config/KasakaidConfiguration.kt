package com.example.kasakaid.config

import com.example.kasakaid.controller.JerseyController
import com.fasterxml.jackson.databind.ObjectMapper
import org.glassfish.jersey.client.JerseyClientBuilder
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.test.JerseyTest
import org.junit.rules.ExternalResource
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import javax.ws.rs.client.Client
import javax.ws.rs.client.Invocation
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.MediaType
import java.text.SimpleDateFormat
import javax.ws.rs.ext.ContextResolver
import javax.ws.rs.Produces
import javax.ws.rs.ext.Provider


@Component
class TestJerseyConfig(
        private val applicationContext: ApplicationContext,
        private val myObjectMapper: ObjectMapper
) : ExternalResource() {

    private val jerseyTest: JerseyTest

    init {
        this.jerseyTest = object : JerseyTest() {
            override fun configure(): ResourceConfig {
                return ResourceConfig(JerseyController::class.java)
                        .register(myObjectMapper)
                        .register(JacksonConfigurator(myObjectMapper))
                        .property("contextConfig", applicationContext)
            }

            override fun getClient(): Client = JerseyClientBuilder.createClient()

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
@Produces(MediaType.APPLICATION_JSON)
class JacksonConfigurator(val myObjectMapper: ObjectMapper) : ContextResolver<ObjectMapper> {

    init {
        myObjectMapper.dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    }

    override fun getContext(arg0: Class<*>): ObjectMapper {
        return myObjectMapper
    }
}
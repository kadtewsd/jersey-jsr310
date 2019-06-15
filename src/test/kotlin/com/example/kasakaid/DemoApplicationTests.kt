package com.example.kasakaid

import com.example.kasakaid.config.KasakaidConfiguration
import com.example.kasakaid.config.TestJerseyConfig
import com.example.kasakaid.controller.LocalDateTimeResult
import com.example.kasakaid.controller.MVCController
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.io.InputStream
import javax.ws.rs.core.MediaType


@RunWith(SpringRunner::class)
@WebMvcTest(value = [Configurable::class, TestJerseyConfig::class, MVCController::class, KasakaidConfiguration::class])
class DemoApplicationTests {

    @Autowired
    lateinit var jerseyConfig: TestJerseyConfig

    @Autowired
    lateinit var myObjectMapper: ObjectMapper

    @Autowired
    private lateinit var mockMvc: MockMvc  // ③

    /**
     * readEntity をするとどうしても、パースエラーになる。
     */
    @Test
    fun apiTest() {
        jerseyConfig.get("/jersey/localDateTime").get().let {
            assertThat(it.status, `is`(200))
            val content = it.readEntity(LocalDateTimeResult::class.java)
            val parsed = myObjectMapper.writeValueAsString(content)
            println(parsed)
            assertThat(parsed, containsString("2019-01-02T13:03:01.000+09:00"))
            assertThat(parsed, containsString("hoge"))
        }
    }

    @Test
    fun Responseでラップしない() {
        jerseyConfig.get("/jersey/localDateTimeWithoutResponse").get().let {
            assertThat(it.status, `is`(200))
            val content = it.readEntity(LocalDateTimeResult::class.java)
            val parsed = myObjectMapper.writeValueAsString(content)
            println(parsed)
            assertThat(parsed, containsString("2019-01-02T13:03:01.000+09:00"))
            assertThat(parsed, containsString("fuga"))
        }
    }

    @Test
    fun SpringMvcWebApiTest() {
        val result = mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/mvc")
//                                .content(myObjectMapper.writeValueAsString(todoRequest))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is2xxSuccessful).andReturn()
//        val content = myObjectMapper.readValue(result.response.contentAsByteArray, DateTimeResult::class.java)
//        val parsed = myObjectMapper.writeValueAsString(content)
        val parsed = result.response.contentAsString
        assertThat(parsed, containsString("\"localDateTime\" : \"2019-01-02T13:03:01.000+09:00\""))

    }
}



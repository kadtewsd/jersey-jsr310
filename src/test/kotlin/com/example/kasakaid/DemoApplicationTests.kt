package com.example.kasakaid

import com.example.kasakaid.config.TestJerseyConfig
import com.example.kasakaid.controller.DateTimeResult
import com.example.kasakaid.controller.MVCController
import com.fasterxml.jackson.databind.ObjectMapper
import org.glassfish.jersey.client.internal.HttpUrlConnector
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import java.io.InputStream
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import javax.ws.rs.core.MediaType


@RunWith(SpringRunner::class)
//@SpringBootTest(classes = [Configurable::class, TestJerseyConfig::class])
@WebMvcTest(value =[Configurable::class, TestJerseyConfig::class, MVCController::class])
class DemoApplicationTests {

    @Autowired
    lateinit var jerseyConfig: TestJerseyConfig

    @Autowired
//    lateinit var objectMapper: ObjectMapper
    lateinit var myObjectMapper: ObjectMapper

    @Autowired
    private lateinit var mockMvc: MockMvc  // ③

    /**
     * readEntity をするとどうしても、パースエラーになる。
     */
    @Ignore
    @Test
    fun apiTest() {
        jerseyConfig.get("/jersey/get").get().let {
            //            val parsed = this.readEntity(String::class.java)
//            val content = myObjectMapper.readValue(parsed, DateTimeResult::class.java)
            val content = it.readEntity(DateTimeResult::class.java)
            val parsed = myObjectMapper.writeValueAsString(content)
            assertThat(parsed, containsString("\"localDateTime\":\"2019-01-02T13:03:01+9:00\""))
//            assertThat(this.status, `is`(200))
        }
    }

    @Test
    fun SpringMvcWebApiTest() {
        val result = mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/mvc")
//                                .content(myObjectMapper.writeValueAsString(todoRequest))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful).andReturn()
//        val content = myObjectMapper.readValue(result.response.contentAsByteArray, DateTimeResult::class.java)
//        val parsed = myObjectMapper.writeValueAsString(content)
        val parsed = result.response.contentAsString
        assertThat(parsed, containsString("\"localDateTime\" : \"2019-01-02T13:03:01.000+09:00\""))

    }
}


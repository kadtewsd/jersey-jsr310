package com.example.kasakaid

import com.example.kasakaid.controller.JerseyController
import org.glassfish.jersey.server.ResourceConfig
import org.springframework.stereotype.Component
import javax.ws.rs.ApplicationPath

@ApplicationPath("/app")
@Component
class JerseyConfig : ResourceConfig() {

    init {
        this.register(JerseyController::class.java)
    }
}
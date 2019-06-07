package com.example.kasakaid.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MVCController {

    @GetMapping("/mvc")
    fun get(): String {
        return "mvc"
    }
}


package com.dwb.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class TestController {

    @GetMapping("/api/test")
    @Operation(summary = "for checking api authorisation working!")
    public String test() {
        return "Protected API working";
    }
}
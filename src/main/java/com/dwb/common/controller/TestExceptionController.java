package com.dwb.common.controller;

import com.dwb.exception.custom.BadRequestException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestExceptionController {

    @GetMapping("/test-error")
    public String testError() {

        throw new BadRequestException(
                "This is a test exception"
        );
    }
}
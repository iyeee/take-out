package com.modq.reggie.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommonControllerTest {

    @Test
    void upload() {
        String fileName="12e.jpg";
        String suffix = fileName.substring(fileName.indexOf("."));
        System.out.println(suffix);
    }
}
package pers.cyz.bigdatatool.uiservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/start")
public class StartController {

    @GetMapping
    @ResponseBody
    public String start() {
        System.out.println("hello world");
        return "{\"result\":true}";
    }
}

package ru.emil.springwebapp.first.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/files")
public class WebFilesController {

    @GetMapping("/trading/common.css")
    public String getTradingCSS(){
        return "trading/common.css";
    }

    @GetMapping("/trading/active.js")
    public String getTradingActiveJS(){
        return "trading/active.js";
    }

}

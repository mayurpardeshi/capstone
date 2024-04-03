package com.capstone.demo;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequestMapping("/greet")
public class SampleController {
//    @GetMapping("/{name}")
    public String sayHello(@PathVariable("name") String name){
        return "Hello "+name;
    }

    public void fun(){

    }

    public void fun2(){

    }
}

package com.free.tbschedule.demo.task;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("classpath:schedule.xml");
        context.start();
        System.out.println("Tbschedule is started!");
        System.in.read();
    }
}

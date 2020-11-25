package com.deepblue.downloadPackage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
//@EnableSwagger2
public class DownloadProxyApplication {
    public static void main(String [] args){
        SpringApplication.run(DownloadProxyApplication.class,args);
    }
}

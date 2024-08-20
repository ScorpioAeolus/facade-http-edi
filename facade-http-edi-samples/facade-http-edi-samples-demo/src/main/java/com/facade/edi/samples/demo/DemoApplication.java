package com.facade.edi.samples.demo;

import com.facade.edi.starter.annotation.EnableEdiApiScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = {"com.facade.edi.samples.demo"})
@EnableEdiApiScan(scanBasePackages = "com.facade.edi.samples.demo.proxy",clientType = EnableEdiApiScan.ClientType.REST_TEMPLATE)
public class DemoApplication
{
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}

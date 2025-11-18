package com.ms.producto_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.ms.producto_ms.client")
public class InventarioMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventarioMsApplication.class, args);
	}

}

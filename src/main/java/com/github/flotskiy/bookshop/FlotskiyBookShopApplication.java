package com.github.flotskiy.bookshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class FlotskiyBookShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlotskiyBookShopApplication.class, args);
	}
}

package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;

@EnableMongoRepositories("com.example.demo.repository")
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		var context = SpringApplication.run(DemoApplication.class, args);

		String[] beanNames = context.getBeanDefinitionNames();
		for(String beanName: beanNames){
			System.out.println("beanName = " + beanName);
		}
		context.close();
	}

}

//@Configuration
//@ComponentScan
//public class LazyInitializationLauncherApplication{
//	public static void main(String[] args){
//		try(var context =
//				new AnnotationConfigApplicationContext(
//						LazyInitializationLauncherApplication.class
//				)){
//			Arrays.stream(context.getBeanDefinitionNames())
//					.forEach(System.out::println);
//		}
//	}
//}
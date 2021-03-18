package pers.cyz.bigdatatool.node.uiservice;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UiServiceApplication {

	public static void main(String[] args) {
		run();
	}

	public static void run(){
		SpringApplication.run(UiServiceApplication.class);

//		SpringApplication app = new SpringApplication(UiServiceApplication.class);
//		app.setBannerMode(Banner.Mode.OFF);
//		app.run();
	}

}

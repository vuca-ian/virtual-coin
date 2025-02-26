package cn.virtual.coin.server;


import cn.vuca.cloud.trace.logback.MDCAdapterManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class VirtualCoinApplication {

    public static void main(String[] args) {
        System.setProperty("spring.devtools.restart.enabled", "false");
        MDCAdapterManager.inject(null, String.valueOf(System.nanoTime()));
        MDCAdapterManager.injectUser("v_coin");
        SpringApplication application = new SpringApplication(VirtualCoinApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
}

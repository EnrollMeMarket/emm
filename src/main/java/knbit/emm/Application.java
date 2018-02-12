package knbit.emm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@EnableScheduling
public class Application {

    @RequestMapping("/{path:[^\\.]+}/**")
    public String forward() {
        return "forward:/";
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
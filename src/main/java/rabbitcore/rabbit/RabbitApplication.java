package rabbitcore.rabbit;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rabbitcore.rabbit.spring_amqp.producer.SpringProducer;

@SpringBootApplication
@EnableRabbit
public class RabbitApplication implements ApplicationRunner {

    @Autowired
    private SpringProducer springProducer;

    public static void main(String[] args) {
        SpringApplication.run(RabbitApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        springProducer.sendMessage();
        //springProducer.reciveMessage();
    }
}


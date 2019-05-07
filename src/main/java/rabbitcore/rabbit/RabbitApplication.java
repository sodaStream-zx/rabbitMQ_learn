package rabbitcore.rabbit;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rabbitcore.rabbit.TimeOutQueue.ProProducer;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableRabbit
public class RabbitApplication {

//    @Autowired
//    private SpringProducer springProducer;

    @Autowired
    private ProProducer proProducer;

    public static void main(String[] args) {
        SpringApplication.run(RabbitApplication.class, args);
    }

    @PostConstruct
    public void show() {
        // springProducer.sendMessage();
        //springProducer.reciveMessage();
        proProducer.sendMessage();
    }
}


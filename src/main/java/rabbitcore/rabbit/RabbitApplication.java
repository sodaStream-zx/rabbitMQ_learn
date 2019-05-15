package rabbitcore.rabbit;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import rabbitcore.rabbit.TimeOutQueue.ProProducer;

@SpringBootApplication
@EnableRabbit
public class RabbitApplication implements ApplicationRunner {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private ProProducer proProducer;

    public static void main(String[] args) {
        SpringApplication.run(RabbitApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        // springProducer.sendMessage();
        //springProducer.reciveMessage();
        proProducer.sendMessage();
    }

    @Bean(value = "manualFactory")
    public RabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        //默认的确认模式是AcknowledgeMode.AUTO
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMaxConcurrentConsumers(1);
        factory.setConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        return factory;
    }
}


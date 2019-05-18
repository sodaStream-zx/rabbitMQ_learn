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

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@SpringBootApplication
@EnableRabbit
public class RabbitApplication implements ApplicationRunner {


    private ConnectionFactory connectionFactory;
    private ProProducer proProducer;

    @Autowired
    public RabbitApplication(ConnectionFactory connectionFactory, ProProducer proProducer) {
        this.connectionFactory = connectionFactory;
        this.proProducer = proProducer;
    }

    public static void main(String[] args) {
        SpringApplication.run(RabbitApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        // springProducer.sendMessage();
        //springProducer.reciveMessage();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 500, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10));
        IntStream.range(0, 5).forEach(value -> threadPoolExecutor.execute(() -> proProducer.sendMessage()));

    }

    @Bean(value = "manualFactory")
    public RabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        //默认的确认模式是AcknowledgeMode.AUTO
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMaxConcurrentConsumers(5);
        factory.setConcurrentConsumers(5);
        factory.setPrefetchCount(1);
//        RetryTemplate retryTemplate = new RetryTemplate();
//        RabbitProperties.Retry retry = new RabbitProperties.Retry();
//        retry.setEnabled(true);
//        retry.setMaxAttempts(1);
//        retry.setMaxInterval(Duration.ofMinutes(1));
//        factory.setRetryTemplate();
        return factory;
    }
}


package pe.elections.microservices.core.newsarticle.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
public class SchedulerConfiguration {

    public static final Logger LOG = LoggerFactory.getLogger(SchedulerConfiguration.class);

    private final Integer threadPoolSize;
    private final Integer taskQueueSize;

    @Autowired
    public SchedulerConfiguration(
        @Value("${app.threadPoolSize:10}") Integer threadPoolSize,
        @Value("${app.taskQueueSize:10}") Integer taskQueueSize
    ) {
        this.threadPoolSize = threadPoolSize;
        this.taskQueueSize = taskQueueSize;
        LOG.info("Scheduler configured with threadPoolSize: {}, taskQueueSize: {}", threadPoolSize, taskQueueSize);
    }

    @Bean
    public Scheduler jdbcScheduler() {
        return Schedulers.newBoundedElastic(threadPoolSize, taskQueueSize, "jdbc-pool");
    }

}

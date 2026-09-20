package dev.pawan.systemdesign.retrymechenism;

import dev.pawan.systemdesign.retrymechenism.backoff.BackoffStrategy;
import dev.pawan.systemdesign.retrymechenism.backoff.ExponentialBackoffStrategy;
import dev.pawan.systemdesign.retrymechenism.policy.RetryPolicy;
import dev.pawan.systemdesign.retrymechenism.policy.SimpleRetryPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.Set;

@Configuration
public class RetryConfigSpringDemo {

    @Bean
    public BackoffStrategy defaultBackoffStrategy() {
        return new ExponentialBackoffStrategy(
                Duration.ofMillis(200),
                Duration.ofSeconds(3),
                2.0,
                true
        );
    }

    @Bean
    public RetryPolicy defaultRetryPolicy() {
        return new SimpleRetryPolicy(
                3,
                Set.of(IOException.class, SocketTimeoutException.class, HttpServerErrorException.class)
        );
    }

    @Bean
    @Primary
    public RetryOperations retryExecutor(RetryPolicy policy, BackoffStrategy backoff) {
        return new DefaultRetryExecutor(policy, backoff);
    }
}

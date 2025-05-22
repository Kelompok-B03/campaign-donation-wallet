package id.ac.ui.cs.gatherlove.campaigndonationwallet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${request.service.base-url}") // From application.properties
    private String paymentServiceBaseUrl;

    @Bean
    public WebClient paymentServiceWebClient() {
        return WebClient.builder()
                .baseUrl(paymentServiceBaseUrl)
                .build();
    }
}

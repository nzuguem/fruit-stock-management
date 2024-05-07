package me.nzuguem.fruitstockmanagement.clients;

import me.nzuguem.fruitstockmanagement.models.FruitCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@Component
public class FruitCodeProviderClient {

    private final RestClient restClient;

    @Value("${application.clients.code-provider.url}")
    private String baseUrl;

    public FruitCodeProviderClient() {
        this.restClient = RestClient.create();
    }

    public FruitCode getFruitCode(String name) {
        return this.restClient
                .get()
                .uri(STR."\{this.baseUrl}/codes/{name}", name)
                .exchange(
                        (_, response) -> {
                            if (response.getStatusCode().is2xxSuccessful()) {
                                return Objects.requireNonNull(response.bodyTo(FruitCode.class));
                            }

                            throw new RuntimeException(STR."Code Provider Service Error - Response status : \{response.getStatusCode()}");
                        });
    }
}

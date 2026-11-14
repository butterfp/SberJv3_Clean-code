package org.butterfp.sj3.adapter;

import org.butterfp.sj3.service.ClientDataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Component
public class HttpClientDataService implements ClientDataService {
    private final RestClient client;

    public HttpClientDataService(@Value("${clients.profile.base-url}") String baseUrl) {
        client = RestClient.create(baseUrl);
    }

    @Override
    public boolean hasSberPrime(UUID clientId) {
        var profile = client.get()
                .uri("/clients/{id}/profile", clientId)
                .retrieve()
                .body(ClientProfile.class);
        if (profile == null || profile.hasSberPrime() == null) {
            throw new RestClientException("Client profile is missing hasSberPrime");
        }
        return profile.hasSberPrime();
    }

    private record ClientProfile(Boolean hasSberPrime) {}
}

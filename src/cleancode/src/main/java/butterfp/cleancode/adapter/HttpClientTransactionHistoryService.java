package org.butterfp.sj3.adapter;

import org.butterfp.sj3.service.ClientTransactionHistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

@Component
public class HttpClientTransactionHistoryService implements ClientTransactionHistoryService {
    private final RestClient client;

    public HttpClientTransactionHistoryService(@Value("${clients.transactions.base-url}") String baseUrl) {
        client = RestClient.create(baseUrl);
    }

    @Override
    public BigDecimal totalPurchases(UUID clientId, YearMonth month) {
        var purchases = client.get()
                .uri("/clients/{id}/transactions/purchases?month={month}", clientId, month.toString())
                .retrieve()
                .body(Purchases.class);
        if (purchases == null || purchases.totalPurchases() == null) {
            throw new RestClientException("Transaction history is missing totalPurchases");
        }
        return purchases.totalPurchases();
    }

    private record Purchases(BigDecimal totalPurchases) {}
}

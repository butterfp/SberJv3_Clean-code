package butterfp.cleancode.adapter;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClientDataService {
    private static final UUID SAMPLE_PRIME_CLIENT =
            UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    public boolean hasSberPrime(UUID clientId) {
        return SAMPLE_PRIME_CLIENT.equals(clientId);
    }
}

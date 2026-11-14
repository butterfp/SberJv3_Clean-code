package butterfp.cleancode.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rate")
public class InterestRateController {
    @PostMapping
    public int calc(@RequestBody RateRequest request) {
        if (request.date() == null || request.id() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Необходимы дата и id");
        }

        LocalDate today = LocalDate.now();
        if (request.date().isAfter(today)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата не должна быть в будущем");
        }

        if (today.isBefore(request.date().plusMonths(3))) {
            return 10 + 2;
        }
        return 10;
    }

    public record RateRequest(LocalDate date, UUID id) {}
}

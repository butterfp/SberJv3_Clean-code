package butterfp.cleancode.web;

import butterfp.cleancode.service.InterestRateService;
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
    private final InterestRateService interestRateService;

    public InterestRateController(InterestRateService interestRateService) {
        this.interestRateService = interestRateService;
    }

    @PostMapping
    public int calculateRate(@RequestBody RateRequest request) {
        if (request.date() == null || request.id() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Необходимы дата и id");
        }

        LocalDate today = LocalDate.now();
        if (request.date().isAfter(today)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата не должна быть в будущем");
        }

        return interestRateService.calc(request.date(), request.id(), today);
    }

    public record RateRequest(LocalDate date, UUID id) {}
}
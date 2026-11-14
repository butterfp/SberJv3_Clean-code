package butterfp.cleancode;

import butterfp.cleancode.web.InterestRateController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static com.google.common.truth.Truth.assertThat;

class Sj3ApplicationTests {

    @Test
    void registersInterestRateController() {
        var application = new SpringApplicationBuilder(CleancodeApplication.class).web(WebApplicationType.NONE);

        try (var context = application.run()) {
            var controllers = context.getBeansOfType(InterestRateController.class);

            assertThat(controllers).hasSize(1);
        }
    }


}
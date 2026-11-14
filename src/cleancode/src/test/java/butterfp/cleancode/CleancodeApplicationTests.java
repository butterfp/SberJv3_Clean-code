package butterfp.cleancode;

import butterfp.cleancode.web.InterestRateController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static com.google.common.truth.Truth.assertThat;

class Sj3ApplicationTests {

    @Test
    void contextLoads() {
        try (var context = new SpringApplicationBuilder(CleancodeApplication.class)
                .web(WebApplicationType.NONE)
                .run()) {
            assertThat(context.getBean(InterestRateController.class)).isNotNull();
        }
    }

}
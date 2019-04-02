package test.io.ticktok.server.clock;

import io.ticktok.server.clock.ClockRequest;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class ClockRequestValidationTest {

    static final ClockRequest CLOCK_WITH_NO_NAME = new ClockRequest("every.5.seconds", "");
    static final ClockRequest CLOCK_WITH_NO_SCHEDULE = new ClockRequest(null, "kuku");

    @Test
    void validateNameIsNotEmpty() {
        assertThat(createSpringValidator().validate(CLOCK_WITH_NO_NAME).size(), is(1));
    }

    @Test
    void validateSchedualeIsNotEmpty() {
        assertThat(createSpringValidator().validate(CLOCK_WITH_NO_SCHEDULE).size(), is(1));
    }

    private LocalValidatorFactoryBean createSpringValidator() {
        LocalValidatorFactoryBean localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
        return localValidatorFactory;
    }
}
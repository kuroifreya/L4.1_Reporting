package ru.netology;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.selector.ByText;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.util.Random;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class CardDeliveryChangeMeetingTimeTest {
    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }
    //Тесты на user path
    @Test
    void shouldTestAcceptCorrectData() {
        DataGenerator.UserInfo user = DataGenerator.generateUser("ru");
        String appointmentDay = DataGenerator.generateDate(3);

        SelenideElement form = $("form");
        form.$("[data-test-id=city] input").setValue(user.getCity());
        form.$("[data-test-id=date] input").doubleClick().press(Keys.BACK_SPACE).setValue(appointmentDay);
        form.$("[data-test-id=name] input").setValue(user.getName());
        form.$("[data-test-id=phone] input").setValue(user.getPhone());
        form.$("[data-test-id=agreement] .checkbox__box").click();
        form.$(new ByText("Запланировать")).click();

        SelenideElement successNotification = $("[data-test-id=success-notification]");
        successNotification.shouldBe(visible, Duration.ofSeconds(15));
        successNotification.$(".notification__title").shouldHave(text("Успешно"));
        successNotification.$(".notification__content").shouldHave(text("Встреча успешно запланирована на " + appointmentDay));
    }

    @Test
    void shouldRearrangeMeeting() throws InterruptedException {
        DataGenerator.UserInfo user = DataGenerator.generateUser("ru");
        int minDaysToAdd = 3;
        String appointmentDay = DataGenerator.generateDate(new Random().nextInt(360)+minDaysToAdd);
        String diffAppointmentDay = DataGenerator.generateDate(new Random().nextInt(360)+minDaysToAdd);

        SelenideElement form = $("form");
        form.$("[data-test-id=city] input").setValue(user.getCity());
        form.$("[data-test-id=date] input").doubleClick().press(Keys.BACK_SPACE).setValue(appointmentDay);
        form.$("[data-test-id=name] input").setValue(user.getName());
        form.$("[data-test-id=phone] input").setValue(user.getPhone());
        form.$("[data-test-id=agreement] .checkbox__box").click();
        form.$(new ByText("Запланировать")).click();

        SelenideElement successNotification = $("[data-test-id=success-notification]");
        successNotification.shouldBe(visible, Duration.ofSeconds(15));
        successNotification.$(".notification__title").shouldHave(text("Успешно!"));
        successNotification.$(".notification__content").shouldHave(text("Встреча успешно запланирована на "+ appointmentDay));

        form.$("[data-test-id=date] input").doubleClick().press(Keys.BACK_SPACE).setValue(diffAppointmentDay);
        form.$(new ByText("Запланировать")).click();

        SelenideElement replanNotification = $("[data-test-id=replan-notification]");
        replanNotification.shouldBe(visible, Duration.ofSeconds(15));
        replanNotification.$(".notification__title").shouldHave(text("Необходимо подтверждение"));
        replanNotification.$(".notification__content").shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        replanNotification.$(".notification__content .button__text").shouldHave(text("Перепланировать")).click();

        successNotification.shouldBe(visible, Duration.ofSeconds(15));
        successNotification.$(".notification__title").shouldHave(text("Успешно!"));
        successNotification.$(".notification__content").shouldHave(text("Встреча успешно запланирована на "+ diffAppointmentDay));
    }
}

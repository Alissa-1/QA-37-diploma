package ru.netology.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.pages.StartPage;
import ru.netology.pages.PaymentPage;

import static com.codeborne.selenide.Selenide.closeWindow;
import static com.codeborne.selenide.Selenide.open;

public class PaymentTest {
    StartPage startPage = open("http://localhost:8080/", StartPage.class);

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setUP() {
        Configuration.holdBrowserOpen = true;
    }

    @AfterEach
    void tearDown() {
        closeWindow();
    }


    @Test
    void shouldMakeSuccessTransactionByActiveCard() {
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataIfApprovedCard();
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.confirmationOfBank();
    }

    @Test
    void shouldMakeDeclineIfRandom() {
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataIfRandomCard();
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.errorRestricted();
    }

    @Test
    void shouldMakeDeclineIfRestricted(){
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataIfDeclinedCard();
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.errorRestricted();
    }

    @Test
    void shouldMakeHintIfEmptyMonthField() {
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataIfApprovedCard();
        var paymentPage = new PaymentPage();
        paymentPage.insertPaymentCardIfEmptyMonth(cardInfo);
        paymentPage.wrongMonth("Неверный формат");
    }

    @Test
    void shouldMakeHintIfEmptyCardNumberField() {
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataIfApprovedCard();
        var paymentPage = new PaymentPage();
        paymentPage.insertPaymentCardIfEmptyCardNumber(cardInfo);
        paymentPage.attentionUnderNumberCard("Неверный формат");

    }

    @Test
    void shouldMakeHintIfEmptyYearField() {
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataIfApprovedCard();
        var paymentPage = new PaymentPage();
        paymentPage.insertPaymentCardEmptyYear(cardInfo);
        paymentPage.attentionUnderYear("Неверный формат");
    }

    @Test
    void shouldMakeHintIfEmptyCVCField() {
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataIfApprovedCard();
        var paymentPage = new PaymentPage();
        paymentPage.insertPaymentCardEmptyCVC(cardInfo);
        paymentPage.attentionUnderCVC("Неверный формат");
    }

    @Test
    void shouldMakeHintIfEmptyHolderField() {
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataIfApprovedCard();
        var paymentPage = new PaymentPage();
        paymentPage.insertPaymentCardEmptyHolder(cardInfo);
        paymentPage.wrongName("Поле обязательно для заполнения");
    }

    @Test
    void shouldDeclineIfMonth00() {
        startPage.paymentPage();
        var validYear = Integer.parseInt(DataHelper.getCurrentYear()) + 1;
        var cardInfo = DataHelper.approvedCardIfParametrizedMonthAndYear("00", String.valueOf(validYear));
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.errorRestricted();
    }

    @Test
    void shouldMakeHintIfIfOneFigureOfMonth() {
        startPage.paymentPage();
        var validYear = Integer.parseInt(DataHelper.getCurrentYear()) + 1;
        var cardInfo = DataHelper.approvedCardIfParametrizedMonthAndYear("1", String.valueOf(validYear));
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.wrongMonth("Неверный формат");

    }

    @Test
    void shouldMakeSuccessTransactionIfMinAllowedDate() {
        startPage.paymentPage();
        var currentMonth = DataHelper.getCurrentMonth();
        var currentYear = DataHelper.getCurrentYear();
        var cardInfo = DataHelper.approvedCardIfParametrizedMonthAndYear(currentMonth, currentYear);
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.confirmationOfBank();
    }

    @Test
    void shouldMakeSuccessTransactionIfMaxAllowedDate() {
        startPage.paymentPage();
        var currentMonth = DataHelper.getCurrentMonth();
        var maxYear = Integer.parseInt(DataHelper.getCurrentYear()) + 5;
        var cardInfo = DataHelper.approvedCardIfParametrizedMonthAndYear(currentMonth, String.valueOf(maxYear));
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.confirmationOfBank();
    }

    @Test
    void shouldDeclineIfPreviousYear() {
        startPage.paymentPage();
        var currentMonth = Integer.parseInt(DataHelper.getCurrentMonth());
        var previousYear = Integer.parseInt(DataHelper.getCurrentYear()) - 1;
        var cardInfo = DataHelper.approvedCardIfParametrizedMonthAndYear
                (String.valueOf(currentMonth), String.valueOf(previousYear));
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.attentionUnderYear("Истёк срок действия карты");
    }

    @Test
    void shouldDeclineIfPreviousMonth() {
        startPage.paymentPage();
        var currentMonth = Integer.parseInt(DataHelper.getCurrentMonth());
        var previousMonth = 0;
        var currentYearMinusMonth = Integer.parseInt(DataHelper.getCurrentYear());
        if (currentMonth == 1) {
            previousMonth = 12;
            currentYearMinusMonth = currentYearMinusMonth - 1;
        } else previousMonth = currentMonth - 1;
        String previousMonthZero = "";
        if (previousMonth < 10) {
            previousMonthZero = "0" + previousMonth;
        }

        var cardInfo = DataHelper.approvedCardIfParametrizedMonthAndYear
                (String.valueOf(previousMonthZero), String.valueOf(currentYearMinusMonth));
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.wrongMonth("Неверно указан срок действия карты");
    }

    @Test
    void shouldDeclineIfInvalidMonth(){
        startPage.paymentPage();
        var currentYear = DataHelper.getCurrentYear();
        var cardInfo = DataHelper.approvedCardIfParametrizedMonthAndYear
                (String.valueOf("50"), currentYear);
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.wrongMonth("Неверно указан срок действия карты");

    }

    @Test
    void shouldMakeSuccessTransactionIfMaxAllowedDateMinusMonth() {
        startPage.paymentPage();
        var currentMonth = Integer.parseInt(DataHelper.getCurrentMonth());
        var previousMonth = 0;
        var maxYear = Integer.parseInt(DataHelper.getCurrentYear()) + 5;
        if (currentMonth == 1) {
            previousMonth = 12;
            maxYear = maxYear - 1;
        } else previousMonth = currentMonth - 1;
        String previousMonthZero = "";
        if (previousMonth < 10) {
            previousMonthZero = "0" + previousMonth;
        }

        var cardInfo = DataHelper.approvedCardIfParametrizedMonthAndYear
                (String.valueOf(previousMonthZero), String.valueOf(maxYear));
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.confirmationOfBank();
    }

    @Test
    void shouldMakeSuccessTransactionIfGotMinLengthOfNameHolder(){
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataIfParametrizedLengthHolder(3);
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.confirmationOfBank();
    }

    @Test
    void shouldMakeSuccessTransactionIfGotMaxLengthOfNameHolder(){
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataIfParametrizedLengthHolder(30);
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.confirmationOfBank();
    }


    @Test
    void shouldDeclineIfNoValidMaxLengthOfNameHolder (){
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataIfParametrizedLengthHolder(31);
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.wrongName("Корректно введите имя с платежной карты");
    }

    @Test
    void shouldDeclineIfInvalidMinLengthOfNameHolder(){
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataIfParametrizedLengthHolder(2);
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.wrongName("Корректно введите имя с платежной карты");
    }

    @Test
    void shouldDeclineIfNameHolderInCyrillic (){
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataForParametrizedName("Иван Васильев");
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.wrongName("Корректно введите имя с платежной карты");
    }

    @Test
    void shouldDeclineIfNameHolderHasDashes (){
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataForParametrizedName("Anna-Lisa");
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.confirmationOfBank();
    }

    @Test
    void shouldDeclineIfNameHolderIfSpecialCharacters (){
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataForParametrizedName("Ivan &6$%#@8");
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.wrongName("Корректно введите имя с платежной карты");
    }
    @Test
    void shouldDeclineIfNameHolderIfNumbers (){
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataForParametrizedName("Ivan Vasi456lev");
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.wrongName("Корректно введите имя с платежной карты");
    }
}
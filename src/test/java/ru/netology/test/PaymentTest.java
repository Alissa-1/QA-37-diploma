package ru.netology.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.DbUtils;
import ru.netology.pages.StartPage;
import ru.netology.pages.PaymentPage;

import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @BeforeEach
    public void openPage() throws SQLException {
        DbUtils.clearTables();
        String url = System.getProperty("sut.url");
        open(url);
    }

    @Test
    void shouldMakeSuccessTransactionByApprovedCard() throws SQLException {
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataIfApprovedCard();
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.confirmationOfBank();
        assertEquals("APPROVED", DbUtils.findPaymentStatus());
    }

    @Test
    void shouldMakeDeclineIfRandomNumberCard() throws SQLException {
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataIfRandomCard();
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.errorRestricted();
        assertEquals("0", DbUtils.countRecords());
    }

    @Test
    void shouldMakeDeclineIfRestrictedCard() throws SQLException {
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataIfDeclinedCard();
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.errorRestricted();
        assertEquals("DECLINED", DbUtils.findPaymentStatus());
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
        var validYear = Integer.parseInt(DataHelper.getCurrentYear());
        var cardInfo = DataHelper.approvedCardIfParametrizedMonthAndYear("00", String.valueOf(validYear));
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.wrongMonth("Неверный формат");
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
        System.out.println(currentMonth);
        var maxYear = Integer.parseInt(DataHelper.getCurrentYear()) + 5;
        var cardInfo = DataHelper.approvedCardIfParametrizedMonthAndYear(currentMonth, String.valueOf(maxYear));
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.confirmationOfBank();
    }

    @Test
    void shouldDeclineIfPreviousYear() {
        startPage.paymentPage();
        var cardInfo = DataHelper.getInvalidExpDateCard(-12);
        startPage.paymentPage();
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.attentionUnderYear("Истёк срок действия карты");
    }

    @Test
    void shouldDeclineIfPreviousMonth() {
        startPage.paymentPage();
        var cardInfo = DataHelper.getInvalidExpDateCard(-1);
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.attentionUnderYear("Истёк срок действия карты");
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
        var cardInfo = DataHelper.getInvalidExpDateCard(49);
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
    void shouldDeclineIfNameHolderOnCyrillic (){
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
        var cardInfo = DataHelper.generatedDataForParametrizedName("Ivan &$%#@");
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.wrongName("Корректно введите имя с платежной карты");
    }
    @Test
    void shouldDeclineIfNameHolderHasNumbers (){
        startPage.paymentPage();
        var cardInfo = DataHelper.generatedDataForParametrizedName("Ivan Vasi456lev");
        var paymentPage = new PaymentPage();
        paymentPage.insertCardData(cardInfo);
        paymentPage.wrongName("Корректно введите имя с платежной карты");
    }
}
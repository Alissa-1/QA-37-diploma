package ru.netology.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.DbUtils;
import ru.netology.pages.CreditPage;
import ru.netology.pages.StartPage;

import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreditTest {
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
        startPage.creditPage();
        var cardInfo = DataHelper.generatedDataIfApprovedCard();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.confirmationOfBank();
        assertEquals("APPROVED", DbUtils.findCreditStatus());
    }
    
    @Test
    void shouldDeclineIfRandomNumberCard() throws SQLException {
        startPage.creditPage();
        var cardInfo = DataHelper.generatedDataIfRandomCard();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.errorRestricted();
        assertEquals("0", DbUtils.countRecords());
    }

    @Test
    void shouldDeclineIfRestrictedCreditCard() throws SQLException {
        startPage.creditPage();
        var cardInfo = DataHelper.generatedDataIfDeclinedCard();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.errorRestricted();
        assertEquals("DECLINED", DbUtils.findCreditStatus());
    }

    @Test
    void shouldShowHintEmptyMonthField() {
        startPage.creditPage();
        var cardInfo = DataHelper.generatedDataIfApprovedCard();
        var creditPage = new CreditPage();
        creditPage.insertPayCardIfEmptyMonth(cardInfo);
        creditPage.wrongMonth("Неверный формат");
    }
    
    @Test
    void shouldShowHintEmptyCardNumberField() {
        startPage.creditPage();
        var cardInfo = DataHelper.generatedDataIfApprovedCard();
        var creditPage = new CreditPage();
        creditPage.insertPayCardIfEmptyCardNumber(cardInfo);
        creditPage.wrongNumberCard("Неверный формат");

    }

    @Test
    void shouldShowHintEmptyHolderField() {
        startPage.creditPage();
        var cardInfo = DataHelper.generatedDataIfApprovedCard();
        var creditPage = new CreditPage();
        creditPage.insertPayCardEmptyHolder(cardInfo);
        creditPage.wrongName("Поле обязательно для заполнения");
    }
    
    @Test
    void shouldShowHintEmptyYearField() {
        startPage.creditPage();
        var cardInfo = DataHelper.generatedDataIfApprovedCard();
        var creditPage = new CreditPage();
        creditPage.insertPayCardEmptyYear(cardInfo);
        creditPage.wrongYear("Неверный формат");
    }

    @Test
    void shouldShowHintEmptyCVCField() {
        startPage.creditPage();
        var cardInfo = DataHelper.generatedDataIfApprovedCard();
        var creditPage = new CreditPage();
        creditPage.insertPayCardEmptyCVC(cardInfo);
        creditPage.attentionUnderCVC("Неверный формат");
    }
    
    @Test
    void shouldShowHintOneFigureMonth() {
        startPage.creditPage();
        var validYear = Integer.parseInt(DataHelper.getCurrentYear()) + 1;
        var cardInfo = DataHelper.approvedCardIfParametrizedMonthAndYear("1", String.valueOf(validYear));
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongMonth("Неверный формат");
    }
    
    @Test
    void shouldDeclineIfZeroMonth00() {
        startPage.creditPage();
        var validYear = Integer.parseInt(DataHelper.getCurrentYear());
        var cardInfo = DataHelper.approvedCardIfParametrizedMonthAndYear("00", String.valueOf(validYear));
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongMonth("Неверный формат");
    }

    @Test
    void shouldMakeSuccessTransactionIfMinAllowedDate() {
        startPage.creditPage();
        var currentMonth = DataHelper.getCurrentMonth();
        var currentYear = DataHelper.getCurrentYear();
        var cardInfo = DataHelper.approvedCardIfParametrizedMonthAndYear(currentMonth, currentYear);
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.confirmationOfBank();
    }
    
    @Test
    void shouldMakeSuccessTransactionIfMaxAllowedDate() {
        startPage.creditPage();
        var currentMonth = DataHelper.getCurrentMonth();
        var maxYear = Integer.parseInt(DataHelper.getCurrentYear()) + 5;
        var cardInfo = DataHelper.approvedCardIfParametrizedMonthAndYear(currentMonth, String.valueOf(maxYear));
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.confirmationOfBank();
    }

    @Test
    void shouldDeclineIfPreviousYear() {
        startPage.creditPage();
        var cardInfo = DataHelper.getInvalidExpDateCard(-12);
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongYear("Истёк срок действия карты");
    }

    @Test
    void shouldDeclineIfPreviousMonth() {
        startPage.creditPage();
        var cardInfo = DataHelper.getInvalidExpDateCard(-1);
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongYear("Истёк срок действия карты");
    }

    @Test
    void shouldDeclineIfInvalidMonth() {
        startPage.creditPage();
        var currentYear = DataHelper.getCurrentYear();
        var cardInfo = DataHelper.approvedCardIfParametrizedMonthAndYear
                (String.valueOf("50"), currentYear);
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongMonth("Неверно указан срок действия карты");

    }

    @Test
    void shouldMakeSuccessTransactionIfMaxAllowedDateMinusMonth() {
        startPage.creditPage();
        var cardInfo = DataHelper.getInvalidExpDateCard(49);
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.confirmationOfBank();
    }

    @Test
    void shouldMakeSuccessTransactionIfMaxLengthOfNameHolder() {
        startPage.creditPage();
        var cardInfo = DataHelper.generatedDataIfParametrizedLengthHolder(30);
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.confirmationOfBank();
    }

    @Test
    void shouldMakeSuccessTransactionIfMinLengthOfNameHolder() {
        startPage.creditPage();
        var cardInfo = DataHelper.generatedDataIfParametrizedLengthHolder(3);
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.confirmationOfBank();
    }

    @Test
    void shouldDeclineIfInvalidMinLengthOfNameHolder() {
        startPage.creditPage();
        var cardInfo = DataHelper.generatedDataIfParametrizedLengthHolder(2);
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongName("Корректно введите имя с платежной карты");
    }

    @Test
    void shouldDeclineIfInvalidMaxLengthOfNameHolder() {
        startPage.creditPage();
        var cardInfo = DataHelper.generatedDataIfParametrizedLengthHolder(31);
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongName("Корректно введите имя с платежной карты");
    }

    @Test
    void shouldDeclineIfNameHolderHasDashes() {
        startPage.creditPage();
        var cardInfo = DataHelper.generatedDataForParametrizedName("Anna-Lisa");
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.confirmationOfBank();
    }

    @Test
    void shouldDeclineIfNameHolderOnCyrillic() {
        startPage.creditPage();
        var cardInfo = DataHelper.generatedDataForParametrizedName("Иван Васильев");
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongName("Корректно введите имя с платежной карты");
    }

    @Test
    void shouldDeclineIfNameHolderHasSpecialCharacters() {
        startPage.creditPage();
        var cardInfo = DataHelper.generatedDataForParametrizedName("Ivan &$%#@");
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongName("Корректно введите имя с платежной карты");
    }

    @Test
    void shouldDeclineIfNameHolderHasNumbers() {
        startPage.creditPage();
        var cardInfo = DataHelper.generatedDataForParametrizedName("Ivan Vasi456lev");
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongName("Корректно введите имя с платежной карты");
    }
}

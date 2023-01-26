package ru.netology.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.pages.CreditPage;
import ru.netology.pages.StartPage;

import static com.codeborne.selenide.Selenide.closeWindow;
import static com.codeborne.selenide.Selenide.open;

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

    @AfterEach
    void tearDown() {
        closeWindow();
    }

    @Test
    void shouldMakeSuccessTransactionByActiveCard() {
        startPage.creditPage();
        var cardInfo = DataHelper.generatedDataIfApprovedCard();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.confirmationOfBank();
    }
    
    @Test
    void shouldDeclineIfRandomNumberCard() {
        startPage.creditPage();
        var cardInfo = DataHelper.generatedDataIfRandomCard();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.errorRestricted();
    }

    @Test
    void shouldDeclineIfDeclinedCreditCard() {
        startPage.creditPage();
        var cardInfo = DataHelper.generatedDataIfDeclinedCard();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.errorRestricted();
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
        var validYear = Integer.parseInt(DataHelper.getCurrentYear()) + 1;
        var cardInfo = DataHelper.approvedCardIfParametrizedMonthAndYear("00", String.valueOf(validYear));
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.errorRestricted();
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
        var currentMonth = Integer.parseInt(DataHelper.getCurrentMonth());
        var pastYear = Integer.parseInt(DataHelper.getCurrentYear()) - 1;
        var cardInfo = DataHelper.approvedCardIfParametrizedMonthAndYear
                (String.valueOf(currentMonth), String.valueOf(pastYear));
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongYear("Истёк срок действия карты");
    }

    @Test
    void shouldDeclineIfPreviousMonth() {
        startPage.creditPage();
        var currentMonth = Integer.parseInt(DataHelper.getCurrentMonth());
        var pastMonth = 0;
        var currentYearMinusMonth = Integer.parseInt(DataHelper.getCurrentYear());
        if (currentMonth == 1) {
            pastMonth = 12;
            currentYearMinusMonth = currentYearMinusMonth - 1;
        } else pastMonth = currentMonth - 1;
        String previousMonthZero = "";
        if (pastMonth < 10) {
            previousMonthZero = "0" + pastMonth;
        }

        var cardInfo = DataHelper.approvedCardIfParametrizedMonthAndYear
                (String.valueOf(previousMonthZero), String.valueOf(currentYearMinusMonth));
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongMonth("Неверно указан срок действия карты");
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
        var currentMonth = Integer.parseInt(DataHelper.getCurrentMonth());
        var pastMonth = 0;
        var maxYear = Integer.parseInt(DataHelper.getCurrentYear()) + 5;
        if (currentMonth == 1) {
            pastMonth = 12;
            maxYear = maxYear - 1;
        } else pastMonth = currentMonth - 1;
        String previousMonthZero = "";
        if (pastMonth < 10) {
            previousMonthZero = "0" + pastMonth;
        }

        var cardInfo = DataHelper.approvedCardIfParametrizedMonthAndYear
                (String.valueOf(previousMonthZero), String.valueOf(maxYear));
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
        var cardInfo = DataHelper.generatedDataForParametrizedName("Ivan &6$%#@8");
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

# Отчёт о проведённом тестировании
### Краткое описание
Проведено тестирование веб-сервиса по покупке тура. Все тестовые сценарии автоматизированы. Проверены два варианта оплаты:
- по дебетовой карте (Купить)
- по кредитной карте (Купить в кредит)

Автоматизированные тесты были успешно запущены для двух СУБД:
- MySQL
- PostgreSQL

Всего составлено 48 тест-кейсов, из них:
- Успешных - 34
- Неуспешных - 14
- Проигнорировано: 0

#### Отчет по результатам тестирования Gradle:
Отображение ссылки на отчет в IDEA:
![Screenshot_Postgres](https://skr.sh/i/290123/cWx4simA.jpg?download=1&name=%D0%A1%D0%BA%D1%80%D0%B8%D0%BD%D1%88%D0%BE%D1%82%2029-01-2023%2023:14:42.jpg)

Отчёт Test Summary. Failed Tests:
![Screenshot_Postgres](https://skr.sh/i/290123/NT5RagUx.jpg?download=1&name=%D0%A1%D0%BA%D1%80%D0%B8%D0%BD%D1%88%D0%BE%D1%82%2029-01-2023%2023:18:01.jpg)

Отчёт Test Summary. Packages:
![Post2](https://skr.sh/i/290123/mW10RwLi.jpg?download=1&name=%D0%A1%D0%BA%D1%80%D0%B8%D0%BD%D1%88%D0%BE%D1%82%2029-01-2023%2023:22:34.jpg)

Отчёт Test Summary. Classes:
![Post3](https://skr.sh/i/290123/WkR73ha3.jpg?download=1&name=%D0%A1%D0%BA%D1%80%D0%B8%D0%BD%D1%88%D0%BE%D1%82%2029-01-2023%2023:24:38.jpg)

#### Отчет по результатам тестирования Allure:
Отчёт Overview:
![Allure](https://skr.sh/i/290123/D5bLb6kb.jpg?download=1&name=%D0%A1%D0%BA%D1%80%D0%B8%D0%BD%D1%88%D0%BE%D1%82%2029-01-2023%2023:27:54.jpg)

Отчёт Suites. CreditTest:
![Allure2](https://skr.sh/i/290123/t8vQqcYV.jpg?download=1&name=%D0%A1%D0%BA%D1%80%D0%B8%D0%BD%D1%88%D0%BE%D1%82%2029-01-2023%2023:26:31.jpg)

Отчёт Suites. PaymentTest:
![Allure3](https://skr.sh/i/290123/IJn6mRxq.jpg?download=1&name=%D0%A1%D0%BA%D1%80%D0%B8%D0%BD%D1%88%D0%BE%D1%82%2029-01-2023%2023:27:25.jpg)

Отчёт Suites. Graphs:
![Allure4](https://skr.sh/i/290123/lDwqzbdj.jpg?download=1&name=%D0%A1%D0%BA%D1%80%D0%B8%D0%BD%D1%88%D0%BE%D1%82%2029-01-2023%2023:28:34.jpg)




### Общие рекомендации

**Общие рекомендации**:
* Исправить найденные баги
* Создать документацию для данного сервиса
* Добавить функциональность блокирования кнопки "Продолжить" до тех пор, пока все поля не будут заполнены корректными значениями
* Заменить сообщения "Неверный формат" на более информативные, указывающие в чем конкретно ошибка
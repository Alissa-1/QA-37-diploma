# Дипломный проект по профессии «Тестировщик»

Дипломный проект представляет собой автоматизацию тестирования комплексного сервиса, взаимодействующего с СУБД и API Банка

# Инструкция по запуску автотестов
## 1. Подготовка окружения
### 1.1 Установленное ПО
- Git
- IntelliJ IDEA
- JDK 11
- Docker Desktop ([инструкция](https://github.com/netology-code/aqa-homeworks/blob/master/docker/installation.md), обратить внимание на системные требования)

### 1.2 Клонирование репозитория
1. Перейти по ссылке на страницу проекта
2. Нажать на кнопку `Code` и в открывшемся окне скопировать URL-адрес
3. Выбрать каталог, в котором будет размещаться клон репозитория
4. Нажать на него правой кнопкой мыши и выбрать `Git Bash Here`
5. В открывшейся консоли выполнить команду `git clone <URL>`

### 1.3 Запуск Docker Desktop
1. Открыть Docker Desktop
2. При первом использовании может понадобиться авторизация на Docker Hub

### 1.4 Запуск IntelliJ IDEA
1. `File - Open`
2. В открывшемся окне указать путь к репозиторию с проектом, нажать `ОК`
3. При необходимости подождать, пока выполнится сканирование и загрузка

### 1.5 Инициализация контейнеров с СУБД MySQL, PostgreSQL и симулятором банковских сервисов
1. В IntelliJ IDEA открыть терминал (Alt+F12)
2. Выполнить команду: `docker-compose up`
3. Дождаться сборки контейнера

### 1.6. Запуск SUT с подключением к MySQL
1. В IntelliJ IDEA открыть дополнительную вкладку с терминалом кликом по кнопке +
2. Выполнить команду: `java -jar artifacts\aqa-shop.jar --spring.datasource.url=jdbc:mysql://localhost:3306/app`

### 1.7. Запуск SUT с подключением к PostgreSQL
1. В IntelliJ IDEA открыть допjava -jar artifacts\aqa-shop.jar --spring.datasource.url=jdbc:postgresql://localhost:5432/appолнительную с терминалом кликом по кнопке +
2. Выполнить команду: `java -jar artifacts\aqa-shop.jar --spring.datasource.url=jdbc:postgresql://localhost:5432/app`

## 2. Запуск автотестов
1. В IntelliJ IDEA дважды нажать Ctrl и в командной строке «Run Anything» выполнить одну из команд в зависимости от выбранной СУБД:
 - **MySQL:** `./gradlew clean test -Ddb.url=jdbc:mysql://localhost:3306/app -Dselenide.headless=true`
 - **PostgreSQL:** `./gradlew clean test -Ddb.url=jdbc:postgresql://localhost:5432/app -Dselenide.headless=true`

## 3. Создание отчёта Allure
1. В IntelliJ IDEA дважды нажать `Ctrl` и в командной строке «Run Anything» выполнить команду:
   `gradlew allureServe`

После выполнения всех тестов остановить docker контейнер командой в консоли: `docker-compose down`
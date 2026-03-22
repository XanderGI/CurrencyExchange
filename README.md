# 💱 Currency Exchange REST API

REST API сервис для учета валют и вычисления обменных курсов. Проект реализует классическую многослойную архитектуру MVCS.

## 📋 Описание

Проект разработан в рамках учебного курса [Java Backend Learning Course](https://zhukovsd.github.io/java-backend-learning-course/projects/currency-exchange/). 
Это веб-приложение предоставляет интерфейс для управления списком валют, обменными курсами и расчета переводов из одной валюты в другую, включая вычисление кросс-курсов через базовую валюту (USD).

### Основной функционал

- ✅ **CRUD операции** для валют и обменных курсов
- ✅ **Конвертация валют** (Прямая, Обратная, Кросс-курс через USD)
- ✅ **Многослойная архитектура** (Controller -> Service -> DAO)
- ✅ **Ручное внедрение зависимостей (Manual DI)** через `ServletContextListener`
- ✅ **Единая обработка ошибок** (Global Exception Handling) возвращающая JSON с HTTP статусами
- ✅ **Автоматические миграции БД** при старте сервера с помощью Flyway
- ✅ **Маппинг объектов** с помощью MapStruct

## 📡 API Endpoints

| HTTP Метод | Эндпоинт | Описание |
| :--- | :--- | :--- |
| `GET` | `/currencies` | Получение списка всех валют |
| `GET` | `/currency/EUR` | Получение конкретной валюты |
| `POST` | `/currencies` | Добавление новой валюты (x-www-form-urlencoded) |
| `GET` | `/exchangeRates` | Получение списка всех обменных курсов |
| `GET` | `/exchangeRate/USDEUR` | Получение конкретного обменного курса |
| `POST` | `/exchangeRates` | Добавление нового обменного курса (x-www-form-urlencoded) |
| `PATCH`| `/exchangeRate/USDEUR` | Обновление существующего обменного курса |
| `GET` | `/exchange?from=USD&to=EUR&amount=10` | Расчет перевода определенной суммы из одной валюты в другую |

## 🚀 Установка и деплой

Приложение компилируется в `.war` артефакт и предназначено для запуска в сервлет-контейнере (например, Apache Tomcat). Благодаря использованию встроенной БД SQLite, установка отдельного SQL-сервера не требуется.

### Требования
- Java 17+
- Gradle (в проекте используется Gradle Wrapper, так что локально устанавливать не обязательно)
- Apache Tomcat 10.x (Jakarta EE 10)

### Запуск проекта

Вы можете запустить проект двумя способами: классическим (вручную на локальном Tomcat) или с использованием Docker.

#### Способ 1: Локальный запуск (Без Docker)
1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/XanderGI/CurrencyExchange.git
   cd CurrencyExchange
   ```
2. Соберите `.war` артефакт с помощью Gradle:
      Для Linux/macOS:
      ```bash
      ./gradlew clean build
      ```
      Для Windows:
      ```bash
      gradlew clean build
      ```
3. Скопируйте собранный файл из `build/libs/CurrencyExchange-1.0-SNAPSHOT.war` в папку `webapps/` вашего сервера Tomcat.

4. Запустите Tomcat. Приложение автоматически создаст SQLite базу данных и накатит миграции(с тестовыми данными).

5. API будет доступно по адресу: `http://localhost:8080/` (или `http://localhost:8080/CurrencyExchange-1.0-SNAPSHOT/` в зависимости от названия war-файла).

#### Способ 2: Запуск через Docker
Если у вас установлен Docker, вам **НЕ** нужно локально настраивать Tomcat.

1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/XanderGI/CurrencyExchange.git
   cd CurrencyExchange
   ```
2. Соберите проект (артефакт .war должен лежать в `build/libs/` перед сборкой образа):
   ```bash
   ./gradlew clean build
   ```
3. Соберите Docker-образ:
   ```bash
   docker build -t currency-api .
   ```
4. Запустите контейнер:
   ```bash
   docker run -d -p 8080:8080 --name currency-app currency-api
   ```
5. API будет доступно по адресу: `http://localhost:8080/`

## 🎯 Особенности реализации
В проекте постарался соблюсти принципы SOLID, DRY и Clean Code.

- **Presentation Layer (Servlets / Controllers):** Сервлеты остаются максимально "тонкими". Они отвечают только за прием HTTP-запроса, делегирование логики в сервисы и возврат JSON ответа. Унаследованы от единого BaseServlet, который перехватывает исключения.
- **Service Layer:** Содержит всю бизнес-логику (например, сложную математику поиска и вычисления кросс-курсов).
- **Data Access Layer (DAO):** Реализует безопасную работу с JDBC (использование PreparedStatement, try-with-resources для предотвращения утечек соединений и курсоров).
- **DTO Pattern & MapStruct:** Разделение моделей базы данных (Entity) от объектов передачи данных (DTO).

## 🛠️ Технологии

- **Java 17**
- **Gradle**
- **Git**
- **Jakarta Servlets 6.0 (Tomcat 10+)**
- **SQLite & JDBC**
- **HikariCP**
- **Flyway**
- **MapStruct**
- **Jackson**
- **JUnit 5 & Mockito**

## 📁 Структура проекта

```
├───main
│   ├───java
│   │   └───io.github.XanderGI
│   │       ├───dao        // Слой доступа к данным (интерфейсы)
│   │       │   └───impl   // Реализации DAO (JDBC)
│   │       ├───dto        // Объекты передачи данных
│   │       ├───exception  // Кастомные исключения для API
│   │       ├───filter     // Web-фильтры (CORS, Кодировка)
│   │       ├───listener   // Инициализация приложения и Manual DI Container
│   │       ├───mapper     // Интерфейсы MapStruct
│   │       ├───model      // Доменные сущности (Entities)
│   │       ├───service    // Слой бизнес-логики
│   │       ├───servlet    // Контроллеры (Обработка HTTP)
│   │       └───utils      // Утилиты (DatabaseManager, ValidationUtils, JsonMapper)
│   ├───resources
│   │   ├───application.properties
│   │   └───db.migration   // SQL скрипты Flyway
│   └───webapp             // Web-контекст сборки
└───test                   // Модульные тесты
```

## 🎓 Что я изучил в ходе реализации проекта

- ✅ Понимание спецификации Jakarta EE Servlets (жизненный цикл, фильтры, слушатели контекста).
- ✅ Самостоятельная реализация Dependency Injection без Spring framework.
- ✅ Принципы построения RESTful API (правильное использование HTTP методов и статус-кодов).
- ✅ Работа с "сырым" JDBC: пулы соединений (HikariCP), защита от SQL-инъекций, правильное управление ресурсами (Connection, ResultSet).
- ✅ Управление схемой базы данных с помощью Flyway.
- ✅ Паттерны проектирования: DAO, DTO, Singleton (в контексте мапперов и утилит).
- ✅ Обработка CORS-запросов.
- ✅ Навыки сборки с помощью Gradle и деплоя Java-приложений (.war) на веб-сервер Apache Tomcat.
- ✅ Навык написания Dockerfile для деплоя проекта через Docker.

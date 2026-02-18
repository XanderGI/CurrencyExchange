package io.github.XanderGI.dao;

import io.github.XanderGI.exception.DatabaseAccessException;
import io.github.XanderGI.model.Currency;
import io.github.XanderGI.utils.DatabaseManager;
import io.github.XanderGI.utils.SqlUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// todo: done почему я здесь не юзаю маппер когда пытаюсь вернуть объект?! Как будто бы надо сделать.

public class CurrencyDao {
    private static final String SQL_FIND_ALL = "SELECT * FROM Currencies";
    private static final String SQL_FIND_BY_CODE = "SELECT * FROM Currencies WHERE Code = ?";
    private static final String SQL_SAVE_CURRENCY = "INSERT INTO Currencies(Code, FullName, Sign) VALUES (?,?,?)";

    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(SQL_FIND_ALL);

            while (resultSet.next()) {
                Currency currency = mapRow(resultSet);
                currencies.add(currency);
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException(e);
        }
        return currencies;
    }

    public Optional<Currency> findByCode(String code) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_CODE)) {

            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException(e);
        }
        return Optional.empty();
    }

    public Optional<Currency> save(Currency currency) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SAVE_CURRENCY, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                return Optional.of(new Currency(
                        resultSet.getLong(1),
                        currency.getFullName(),
                        currency.getCode(),
                        currency.getSign()
                ));
            }
        } catch (SQLException e) {
            if (SqlUtils.isUniqueConstraintViolation(e)) {
                return Optional.empty();
            }
            throw new DatabaseAccessException(e);
        }

        return Optional.empty();
    }

    private Currency mapRow(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getLong("id"),
                resultSet.getString("FullName"),
                resultSet.getString("Code"),
                resultSet.getString("Sign")
        );
    }
}
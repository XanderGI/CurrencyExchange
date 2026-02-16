package io.github.XanderGI.dao;

import io.github.XanderGI.model.Currency;
import io.github.XanderGI.utils.DatabaseManager;
import io.github.XanderGI.utils.SqlUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// todo: почему я здесь не юзаю маппер когда пытаюсь вернуть объект?! Как будто бы надо сделать.

public class CurrencyDao {

    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        String sql = "SELECT * FROM Currencies";
        try (Connection connection = DatabaseManager.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Currency currency = new Currency(
                        resultSet.getLong("id"),
                        resultSet.getString("fullName"),
                        resultSet.getString("code"),
                        resultSet.getString("sign")
                );
                currencies.add(currency);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currencies;
    }

    public Optional<Currency> findByCode(String code) {
        String sql = "SELECT * FROM Currencies WHERE Code = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(new Currency(
                        resultSet.getLong("id"),
                        resultSet.getString("fullName"),
                        resultSet.getString("code"),
                        resultSet.getString("sign")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<Currency> save(Currency currency) {
        String sql = "INSERT INTO Currencies(Code, FullName, Sign) VALUES (?,?,?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return Optional.of(
                        new Currency(
                                resultSet.getLong(1),
                                currency.getCode(),
                                currency.getFullName(),
                                currency.getSign()
                        )
                );
            }
        } catch (SQLException e) {
            if (SqlUtils.isUniqueConstraintViolation(e)) {
                return Optional.empty();
            }
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }
}
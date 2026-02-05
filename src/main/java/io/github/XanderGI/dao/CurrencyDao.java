package io.github.XanderGI.dao;

import io.github.XanderGI.model.Currency;
import io.github.XanderGI.utils.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao {

    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Currencies");

            while (resultSet.next()) {
                Currency currency = new Currency(
                  resultSet.getLong("id"),
                  resultSet.getString("code"),
                  resultSet.getString("fullName"),
                  resultSet.getString("sign")
                );
                currencies.add(currency);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return currencies;
    }

    public Optional<Currency> findByCode(String code) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Currencies WHERE Code = ?")) {
            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(new Currency(
                        resultSet.getLong("id"),
                        resultSet.getString("code"),
                        resultSet.getString("fullName"),
                        resultSet.getString("sign")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
package io.github.XanderGI.dao;

import io.github.XanderGI.model.Currency;
import io.github.XanderGI.model.ExchangeRate;
import io.github.XanderGI.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDao {

    public List<ExchangeRate> findAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();

        String sql = """
                SELECT 
                    ExchangeRates.ID, Rate,
                    base.ID AS base_id, base.FullName AS base_name, base.code AS base_code, base.sign AS base_sign,
                    target.ID AS target_id, target.FullName AS target_name, target.code AS target_code, target.sign AS target_sign
                FROM ExchangeRates
                JOIN Currencies base ON ExchangeRates.BaseCurrencyId = base.ID
                JOIN Currencies target ON ExchangeRates.TargetCurrencyId = target.ID
                """;

        try (Connection connection = DatabaseManager.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Currency baseCurrency = new Currency(
                        resultSet.getLong("base_id"),
                        resultSet.getString("base_name"),
                        resultSet.getString("base_code"),
                        resultSet.getString("base_sign")

                );
                Currency targetCurrency = new Currency(
                        resultSet.getLong("target_id"),
                        resultSet.getString("target_name"),
                        resultSet.getString("target_code"),
                        resultSet.getString("target_sign")

                );
                ExchangeRate exchangeRate = new ExchangeRate(
                        resultSet.getLong("id"),
                        baseCurrency,
                        targetCurrency,
                        resultSet.getBigDecimal("rate")
                );
                exchangeRates.add(exchangeRate);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exchangeRates;
    }
}
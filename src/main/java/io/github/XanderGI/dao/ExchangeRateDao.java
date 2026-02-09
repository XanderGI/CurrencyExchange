package io.github.XanderGI.dao;

import io.github.XanderGI.model.Currency;
import io.github.XanderGI.model.ExchangeRate;
import io.github.XanderGI.utils.DatabaseManager;
import io.github.XanderGI.utils.SqlUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                Currency baseCurrency = mapRowToCurrency(resultSet, "base_");
                Currency targetCurrency = mapRowToCurrency(resultSet, "target_");

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

    public Optional<ExchangeRate> findByCodes(String baseCode, String targetCode) {
        String sql = """
                SELECT 
                    ExchangeRates.ID, Rate,
                    base.ID AS base_id, base.FullName AS base_name, base.code AS base_code, base.sign AS base_sign,
                    target.ID AS target_id, target.FullName AS target_name, target.code AS target_code, target.sign AS target_sign
                FROM ExchangeRates
                JOIN Currencies base ON ExchangeRates.BaseCurrencyId = base.ID
                JOIN Currencies target ON ExchangeRates.TargetCurrencyId = target.ID
                WHERE base.code = ? AND target.code = ?
                """;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, baseCode);
            preparedStatement.setString(2, targetCode);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Currency baseCurrency = mapRowToCurrency(resultSet, "base_");
                Currency targetCurrency = mapRowToCurrency(resultSet, "target_");

                ExchangeRate exchangeRate = new ExchangeRate(
                        resultSet.getLong("id"),
                        baseCurrency,
                        targetCurrency,
                        resultSet.getBigDecimal("rate")
                );

                return Optional.of(exchangeRate);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<ExchangeRate> save(ExchangeRate exchangeRate) {
        String sql = "INSERT  INTO ExchangeRates(BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?,?,?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, exchangeRate.getBaseCurrency().getId());
            preparedStatement.setLong(2, exchangeRate.getTargetCurrency().getId());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return Optional.of(new ExchangeRate(
                        resultSet.getLong(1),
                        exchangeRate.getBaseCurrency(),
                        exchangeRate.getTargetCurrency(),
                        exchangeRate.getRate()
                ));
            }
        } catch (SQLException e) {
            if (SqlUtils.isUniqueConstraintViolation(e)) {
                return Optional.empty();
            }
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    private Currency mapRowToCurrency(ResultSet resultSet, String prefix) throws SQLException {
        return new Currency(
                resultSet.getLong(prefix + "id"),
                resultSet.getString(prefix + "name"),
                resultSet.getString(prefix + "code"),
                resultSet.getString(prefix + "sign")
        );
    }
}
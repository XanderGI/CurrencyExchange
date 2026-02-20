package io.github.XanderGI.dao.impl;

import io.github.XanderGI.dao.ExchangeRateDao;
import io.github.XanderGI.exception.DatabaseAccessException;
import io.github.XanderGI.model.Currency;
import io.github.XanderGI.model.ExchangeRate;
import io.github.XanderGI.utils.DatabaseManager;
import io.github.XanderGI.utils.SqlUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDaoImpl implements ExchangeRateDao {
    private final static String SQL_SELECT_ALL_WITH_JOINS = """
            SELECT 
                    ExchangeRates.ID, Rate,
                    base.ID AS base_id, base.FullName AS base_name, base.code AS base_code, base.sign AS base_sign,
                    target.ID AS target_id, target.FullName AS target_name, target.code AS target_code, target.sign AS target_sign
            FROM ExchangeRates
            JOIN Currencies base ON ExchangeRates.BaseCurrencyId = base.ID
            JOIN Currencies target ON ExchangeRates.TargetCurrencyId = target.ID
            """;

    private final static String SQL_FIND_ALL = SQL_SELECT_ALL_WITH_JOINS;
    private final static String SQL_FIND_BY_CODES = SQL_SELECT_ALL_WITH_JOINS + "WHERE base.code = ? AND target.code = ?";

    private final static String SQL_SAVE_EXCHANGE_RATE = """
            INSERT INTO ExchangeRates(BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?,?,?)
            """;

    private final static String SQL_UPDATE_EXCHANGE_RATE = """
            UPDATE ExchangeRates SET Rate = ? WHERE ID = ?
            """;

    private final static String SQL_FIND_ALL_USD_RELATED_PAIRS = SQL_SELECT_ALL_WITH_JOINS + """
             WHERE (base.code = 'USD' OR target.code = 'USD') AND ((base.code = ? OR target.code = ?) OR (base.code = ? OR target.code = ?))
            """;

    @Override
    public List<ExchangeRate> findAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQL_FIND_ALL);

            while (resultSet.next()) {
                ExchangeRate exchangeRate = mapRow(resultSet);
                exchangeRates.add(exchangeRate);
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException(e);
        }

        return exchangeRates;
    }

    @Override
    public Optional<ExchangeRate> findByCodes(String baseCode, String targetCode) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_CODES)) {

            preparedStatement.setString(1, baseCode);
            preparedStatement.setString(2, targetCode);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                ExchangeRate exchangeRate = mapRow(resultSet);
                return Optional.of(exchangeRate);
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException(e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<ExchangeRate> save(ExchangeRate exchangeRate) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SAVE_EXCHANGE_RATE, Statement.RETURN_GENERATED_KEYS)) {

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
            throw new DatabaseAccessException(e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<ExchangeRate> update(ExchangeRate exchangeRate) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_EXCHANGE_RATE)) {

            preparedStatement.setBigDecimal(1, exchangeRate.getRate());
            preparedStatement.setLong(2, exchangeRate.getId());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                return Optional.of(new ExchangeRate(
                        exchangeRate.getId(),
                        exchangeRate.getBaseCurrency(),
                        exchangeRate.getTargetCurrency(),
                        exchangeRate.getRate()
                ));
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseAccessException(e);
        }
    }

    @Override
    public List<ExchangeRate> findAllUsdRelatedPairs(String baseCode, String targetCode) {
        List<ExchangeRate> exchangeRates = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_ALL_USD_RELATED_PAIRS)) {

            preparedStatement.setString(1, baseCode);
            preparedStatement.setString(2, baseCode);
            preparedStatement.setString(3, targetCode);
            preparedStatement.setString(4, targetCode);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ExchangeRate exchangeRate = mapRow(resultSet);
                exchangeRates.add(exchangeRate);
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException(e);
        }

        return exchangeRates;
    }

    private ExchangeRate mapRow(ResultSet resultSet) throws SQLException {
        Currency baseCurrency = mapRowToCurrency(resultSet, "base_");
        Currency targetCurrency = mapRowToCurrency(resultSet, "target_");

        return new ExchangeRate(
                resultSet.getLong("id"),
                baseCurrency,
                targetCurrency,
                resultSet.getBigDecimal("rate")
        );
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
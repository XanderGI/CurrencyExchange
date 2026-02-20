package io.github.XanderGI.dao;

import io.github.XanderGI.model.ExchangeRate;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateDao extends Dao<ExchangeRate> {
    Optional<ExchangeRate> findByCodes(String baseCode, String targetCode);
    Optional<ExchangeRate> update(ExchangeRate exchangeRate);
    List<ExchangeRate> findAllUsdRelatedPairs(String baseCode, String targetCode);
}
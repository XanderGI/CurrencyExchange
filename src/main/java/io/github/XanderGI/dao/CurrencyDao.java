package io.github.XanderGI.dao;

import io.github.XanderGI.model.Currency;

import java.util.Optional;

public interface CurrencyDao extends Dao<Currency> {
    Optional<Currency> findByCode(String code);
}
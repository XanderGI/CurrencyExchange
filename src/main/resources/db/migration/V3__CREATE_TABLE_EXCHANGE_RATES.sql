CREATE TABLE ExchangeRates (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    BaseCurrencyId INTEGER,
    TargetCurrencyId INTEGER,
    Rate DECIMAL(6),
    FOREIGN KEY (BaseCurrencyId) REFERENCES Currencies(ID),
    FOREIGN KEY (TargetCurrencyId) REFERENCES Currencies(ID)
);

CREATE UNIQUE INDEX uq_idx_exchange_rates_base_target
ON ExchangeRates(BaseCurrencyId, TargetCurrencyId);
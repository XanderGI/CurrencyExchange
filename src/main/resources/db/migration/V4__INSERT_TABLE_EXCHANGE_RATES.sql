INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
VALUES (
   (SELECT ID FROM Currencies WHERE Code = 'USD'),
   (SELECT ID FROM Currencies WHERE Code = 'EUR'),
   0.85
);

INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
VALUES (
   (SELECT ID FROM Currencies WHERE Code = 'EUR'),
   (SELECT ID FROM Currencies WHERE Code = 'USD'),
   1.17
);

INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
VALUES (
   (SELECT ID FROM Currencies WHERE Code = 'USD'),
   (SELECT ID FROM Currencies WHERE Code = 'RUB'),
   77.8
);

INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
VALUES (
   (SELECT ID FROM Currencies WHERE Code = 'USD'),
   (SELECT ID FROM Currencies WHERE Code = 'UAH'),
   42.8
);
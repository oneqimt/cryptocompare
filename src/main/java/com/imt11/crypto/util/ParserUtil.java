package com.imt11.crypto.util;

import com.imt11.crypto.model.Coin;
import com.imt11.crypto.model.CryptoValue;
import com.imt11.crypto.model.PercentageDTO;
import com.imt11.crypto.model.Person;

import org.json.simple.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dennis Miller
 */
public class ParserUtil {

    private ParserUtil() {
    }

    public static List<CryptoValue> parseMainCryptos(JSONObject jsonObject, List<Person> persons) {
        List<CryptoValue> cryptos = new ArrayList<>();
        NumberFormat currencyFormat = CryptoUtil.getCurrencyFormat();

        // 4 main coins for now
        JSONObject btc = (JSONObject) jsonObject.get(CryptoUtil.BTC_SYMBOL);
        JSONObject eth = (JSONObject) jsonObject.get(CryptoUtil.ETH_SYMBOL);
        JSONObject ltc = (JSONObject) jsonObject.get(CryptoUtil.LTC_SYMBOL);
        JSONObject bch = (JSONObject) jsonObject.get(CryptoUtil.BCH_SYMBOL);
        // if person has these main coins, parse them
        for (Person person : persons) {
            String symbol = person.getCoin().getCoin_symbol();
            if (btc != null) {
                if (symbol.equalsIgnoreCase(CryptoUtil.BTC_SYMBOL)) {
                    CryptoValue btcCrypto = new CryptoValue();
                    Coin btccoin = new Coin();
                    btccoin.setCoin_id(person.getCoin().getCoin_id());
                    btccoin.setCoin_symbol(symbol);
                    btccoin.setCoin_name(person.getCoin().getCoin_name());
                    btccoin.setCmc_id(person.getCoin().getCmc_id());
                    btccoin.setSlug(person.getCoin().getSlug());
                    btcCrypto.setCoin(btccoin);
                    btcCrypto.setUSD(currencyFormat.format(btc.get(CryptoUtil.USD)));
                    Double btcDbl = Double.valueOf(btc.get(CryptoUtil.USD).toString());
                    btcCrypto.setHoldingValue(CryptoUtil.formatDoubleValue(person.getHoldings().getQuantity(), btcDbl));
                    btcCrypto.setCost(CryptoUtil.formatDoubleValue(person.getHoldings().getQuantity(), person.getHoldings().getCost()));
                    btcCrypto.setQuantity(person.getHoldings().getQuantity());

                    double aggregateDBl;
                    if (bch != null) {
                        // Add BCH here
                        Double bchval = Double.valueOf(bch.get(CryptoUtil.USD).toString());
                        // add bch to btcDbl
                        aggregateDBl = btcDbl + bchval;
                    } else {
                        aggregateDBl = btcDbl;
                    }


                    PercentageDTO btcdto = CryptoUtil.getPercentage(person.getHoldings().getQuantity(), person.getHoldings().getCost(), aggregateDBl);
                    btcCrypto.setPercentage(btcdto.getValueString());
                    if (btcdto.getValueDouble() >= 0.0) {
                        btcCrypto.setIncreaseDecrease(CryptoUtil.INCREASE);
                    } else {
                        btcCrypto.setIncreaseDecrease(CryptoUtil.DECREASE);
                    }

                    cryptos.add(btcCrypto);
                    System.out.println("BTC CryptoValue is:" + " " + btcCrypto.toString());
                }

            }

            if (eth != null) {
                if (symbol.equalsIgnoreCase(CryptoUtil.ETH_SYMBOL)) {
                    CryptoValue ethCrypto = new CryptoValue();
                    Coin ethcoin = new Coin();
                    ethcoin.setCoin_id(person.getCoin().getCoin_id());
                    ethcoin.setCoin_symbol(symbol);
                    ethcoin.setCoin_name(person.getCoin().getCoin_name());
                    ethcoin.setCmc_id(person.getCoin().getCmc_id());
                    ethcoin.setSlug(person.getCoin().getSlug());
                    ethCrypto.setCoin(ethcoin);
                    ethCrypto.setUSD(currencyFormat.format(eth.get(CryptoUtil.USD)));
                    Double ethDbl = Double.valueOf(eth.get(CryptoUtil.USD).toString());
                    ethCrypto.setHoldingValue(CryptoUtil.formatDoubleValue(person.getHoldings().getQuantity(), ethDbl));
                    ethCrypto.setCost(CryptoUtil.formatDoubleValue(person.getHoldings().getQuantity(), person.getHoldings().getCost()));
                    ethCrypto.setQuantity(person.getHoldings().getQuantity());

                    PercentageDTO ethdto = CryptoUtil.getPercentage(person.getHoldings().getQuantity(), person.getHoldings().getCost(), ethDbl);
                    ethCrypto.setPercentage(ethdto.getValueString());
                    if (ethdto.getValueDouble() >= 0.0) {
                        ethCrypto.setIncreaseDecrease(CryptoUtil.INCREASE);
                    } else {
                        ethCrypto.setIncreaseDecrease(CryptoUtil.DECREASE);
                    }
                    cryptos.add(ethCrypto);

                }
            }

            if (bch != null) {
                if (symbol.equalsIgnoreCase(CryptoUtil.BCH_SYMBOL)) {
                    CryptoValue bchCrypto = new CryptoValue();
                    Coin bchcoin = new Coin();
                    bchcoin.setCoin_id(person.getCoin().getCoin_id());
                    bchcoin.setCoin_symbol(symbol);
                    bchcoin.setCoin_name(person.getCoin().getCoin_name());
                    bchcoin.setCmc_id(person.getCoin().getCmc_id());
                    bchcoin.setSlug(person.getCoin().getSlug());
                    bchCrypto.setCoin(bchcoin);
                    bchCrypto.setCoin(bchcoin);
                    bchCrypto.setUSD(currencyFormat.format(bch.get(CryptoUtil.USD)));
                    Double bchDbl = Double.valueOf(bch.get(CryptoUtil.USD).toString());
                    bchCrypto.setHoldingValue(CryptoUtil.formatDoubleValue(bchDbl, person.getHoldings().getQuantity()));
                    bchCrypto.setCost(CryptoUtil.formatDoubleValue(person.getHoldings().getQuantity(), person.getHoldings().getCost()));
                    bchCrypto.setIncreaseDecrease(CryptoUtil.INCREASE);
                    bchCrypto.setPercentage("SEE BITCOIN");
                    bchCrypto.setQuantity(person.getHoldings().getQuantity());

                    cryptos.add(bchCrypto);
                }

            }
            if (ltc != null) {
                if (symbol.equalsIgnoreCase(CryptoUtil.LTC_SYMBOL)) {
                    CryptoValue ltcCrypto = new CryptoValue();
                    Coin ltccoin = new Coin();
                    ltccoin.setCoin_id(person.getCoin().getCoin_id());
                    ltccoin.setCoin_symbol(symbol);
                    ltccoin.setCoin_name(person.getCoin().getCoin_name());
                    ltccoin.setCmc_id(person.getCoin().getCmc_id());
                    ltccoin.setSlug(person.getCoin().getSlug());
                    ltcCrypto.setCoin(ltccoin);
                    ltcCrypto.setCoin(ltccoin);
                    ltcCrypto.setUSD(currencyFormat.format(ltc.get(CryptoUtil.USD)));
                    Double ltcDbl = Double.valueOf(ltc.get(CryptoUtil.USD).toString());
                    ltcCrypto.setHoldingValue(CryptoUtil.formatDoubleValue(person.getHoldings().getQuantity(), ltcDbl));
                    ltcCrypto.setCost(CryptoUtil.formatDoubleValue(person.getHoldings().getQuantity(), person.getHoldings().getCost()));
                    ltcCrypto.setQuantity(person.getHoldings().getQuantity());

                    PercentageDTO ltcdto = CryptoUtil.getPercentage(person.getHoldings().getQuantity(), person.getHoldings().getCost(), ltcDbl);
                    ltcCrypto.setPercentage(ltcdto.getValueString());
                    if (ltcdto.getValueDouble() >= 0.0) {
                        ltcCrypto.setIncreaseDecrease(CryptoUtil.INCREASE);
                    } else {
                        ltcCrypto.setIncreaseDecrease(CryptoUtil.DECREASE);
                    }
                    cryptos.add(ltcCrypto);
                }
            }

        }
        return cryptos;
    }

    public static List<CryptoValue> parseAltCryptos(JSONObject jsonObject, List<Person> persons) {
        List<CryptoValue> cryptos = new ArrayList<>();
        NumberFormat currencyFormat = CryptoUtil.getCurrencyFormat();

        for (Person person : persons) {
            String symbol = person.getCoin().getCoin_symbol();
            if (!symbol.equalsIgnoreCase(CryptoUtil.BTC_SYMBOL)
                    && !symbol.equalsIgnoreCase(CryptoUtil.BCH_SYMBOL)
                    && !symbol.equalsIgnoreCase(CryptoUtil.ETH_SYMBOL)
                    && !symbol.equalsIgnoreCase(CryptoUtil.LTC_SYMBOL)) {
                JSONObject obj = (JSONObject) jsonObject.get(symbol);
                if (obj != null) {
                    CryptoValue cryptoValue = new CryptoValue();
                    Coin coin = new Coin();
                    coin.setCoin_name(person.getCoin().getCoin_name());
                    coin.setCoin_symbol(symbol);
                    coin.setCoin_id(person.getCoin().getCoin_id());
                    coin.setCmc_id(person.getCoin().getCmc_id());
                    coin.setSlug(person.getCoin().getSlug());
                    cryptoValue.setCoin(coin);
                    cryptoValue.setUSD(currencyFormat.format(obj.get(CryptoUtil.USD)));
                    Double dbl = Double.valueOf(obj.get(CryptoUtil.USD).toString());
                    cryptoValue.setHoldingValue(CryptoUtil.formatDoubleValue(person.getHoldings().getQuantity(), dbl));
                    cryptoValue.setCost(CryptoUtil.getAltCost(person.getHoldings().getCost(), person.getHoldings().getQuantity()));
                    cryptoValue.setQuantity(person.getHoldings().getQuantity());
                    if (!symbol.equalsIgnoreCase(CryptoUtil.BTG_SYMBOL)) {
                        PercentageDTO dto = CryptoUtil.getPercentage(person.getHoldings().getQuantity(), person.getHoldings().getCost(), dbl);
                        cryptoValue.setPercentage(dto.getValueString());
                        if (dto.getValueDouble() >= 0.0) {
                            cryptoValue.setIncreaseDecrease(CryptoUtil.INCREASE);
                        } else {
                            cryptoValue.setIncreaseDecrease(CryptoUtil.DECREASE);
                        }
                    } else {
                        cryptoValue.setIncreaseDecrease(CryptoUtil.INCREASE);
                        cryptoValue.setPercentage("SEE BITCOIN");
                    }
                    cryptos.add(cryptoValue);
                }
            }

        }

        return cryptos;
    }

}

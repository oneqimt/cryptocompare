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

    private static final String TAG = ParserUtil.class.getSimpleName();

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
            if (symbol.equalsIgnoreCase(CryptoUtil.BTC_SYMBOL)) {
                CryptoValue btcCrypto = new CryptoValue();
                Coin btccoin = new Coin();
                btccoin.setCoin_id(person.getCoin().getCoin_id());
                btccoin.setCoin_symbol(symbol);
                btccoin.setCoin_name(person.getCoin().getCoin_name());
                btcCrypto.setCoin(btccoin);
                btcCrypto.setUSD(currencyFormat.format(btc.get(CryptoUtil.USD)));
                Double btcDbl = Double.valueOf(btc.get(CryptoUtil.USD).toString());
                btcCrypto.setHoldingValue(CryptoUtil.formatDoubleValue(person.getQuantity(), btcDbl));
                btcCrypto.setCost(CryptoUtil.formatDoubleValue(person.getQuantity(), person.getCost()));
                btcCrypto.setQuantity(person.getQuantity());

                // Add BCH here
                Double bchval = Double.valueOf(bch.get(CryptoUtil.USD).toString());
                // add bch to btcDbl
                Double aggregateDBl = btcDbl + bchval;

                PercentageDTO btcdto = CryptoUtil.getPercentage(person.getQuantity(), person.getCost(), aggregateDBl);
                btcCrypto.setPercentage(btcdto.getValueString());
                if (btcdto.getValueDouble() >= 0.0) {
                    btcCrypto.setIncreaseDecrease(CryptoUtil.INCREASE);
                } else {
                    btcCrypto.setIncreaseDecrease(CryptoUtil.DECREASE);
                }

                cryptos.add(btcCrypto);
                System.out.println("BTC CryptoValue is:" + " " + btcCrypto.toString());
            }
            if(symbol.equalsIgnoreCase(CryptoUtil.ETH_SYMBOL)){
                CryptoValue ethCrypto = new CryptoValue();
                Coin ethcoin = new Coin();
                ethcoin.setCoin_id(person.getCoin().getCoin_id());
                ethcoin.setCoin_symbol(symbol);
                ethcoin.setCoin_name(person.getCoin().getCoin_name());
                ethCrypto.setCoin(ethcoin);
                ethCrypto.setUSD(currencyFormat.format(eth.get(CryptoUtil.USD)));
                Double ethDbl = Double.valueOf(eth.get(CryptoUtil.USD).toString());
                ethCrypto.setHoldingValue(CryptoUtil.formatDoubleValue(person.getQuantity(), ethDbl));
                ethCrypto.setCost(CryptoUtil.formatDoubleValue(person.getQuantity(), person.getCost()));
                ethCrypto.setQuantity(person.getQuantity());

                PercentageDTO ethdto = CryptoUtil.getPercentage(person.getQuantity(), person.getCost(), ethDbl);
                ethCrypto.setPercentage(ethdto.getValueString());
                if (ethdto.getValueDouble() >= 0.0) {
                    ethCrypto.setIncreaseDecrease(CryptoUtil.INCREASE);
                } else {
                    ethCrypto.setIncreaseDecrease(CryptoUtil.DECREASE);
                }
                cryptos.add(ethCrypto);
                System.out.println("ETH CryptoValue is:" + " " + ethCrypto.toString());
            }

            if(symbol.equalsIgnoreCase(CryptoUtil.BCH_SYMBOL)){
                CryptoValue bchCrypto = new CryptoValue();
                Coin bchcoin = new Coin();
                bchcoin.setCoin_id(person.getCoin().getCoin_id());
                bchcoin.setCoin_symbol(symbol);
                bchcoin.setCoin_name(person.getCoin().getCoin_name());
                bchCrypto.setCoin(bchcoin);
                bchCrypto.setCoin(bchcoin);
                bchCrypto.setUSD(currencyFormat.format(bch.get(CryptoUtil.USD)));
                Double bchDbl = Double.valueOf(bch.get(CryptoUtil.USD).toString());
                bchCrypto.setHoldingValue(CryptoUtil.formatDoubleValue(bchDbl, person.getQuantity()));
                bchCrypto.setCost(CryptoUtil.formatDoubleValue(person.getQuantity(), person.getCost()));
                bchCrypto.setIncreaseDecrease(CryptoUtil.INCREASE);
                bchCrypto.setPercentage("SEE BITCOIN");
                bchCrypto.setQuantity(person.getQuantity());

                cryptos.add(bchCrypto);
                System.out.println("BCH CryptoValue is:" + " " + bchCrypto.toString());
            }

            if(symbol.equalsIgnoreCase(CryptoUtil.LTC_SYMBOL)){
                CryptoValue ltcCrypto = new CryptoValue();
                Coin ltccoin = new Coin();
                ltccoin.setCoin_id(person.getCoin().getCoin_id());
                ltccoin.setCoin_symbol(symbol);
                ltccoin.setCoin_name(person.getCoin().getCoin_name());
                ltcCrypto.setCoin(ltccoin);
                ltcCrypto.setCoin(ltccoin);
                ltcCrypto.setUSD(currencyFormat.format(ltc.get(CryptoUtil.USD)));
                Double ltcDbl = Double.valueOf(ltc.get(CryptoUtil.USD).toString());
                ltcCrypto.setHoldingValue(CryptoUtil.formatDoubleValue(person.getQuantity(), ltcDbl));
                ltcCrypto.setCost(CryptoUtil.formatDoubleValue(person.getQuantity(), person.getCost()));
                ltcCrypto.setQuantity(person.getQuantity());

                PercentageDTO ltcdto = CryptoUtil.getPercentage(person.getQuantity(), person.getCost(), ltcDbl);
                ltcCrypto.setPercentage(ltcdto.getValueString());
                if (ltcdto.getValueDouble() >= 0.0) {
                    ltcCrypto.setIncreaseDecrease(CryptoUtil.INCREASE);
                } else {
                    ltcCrypto.setIncreaseDecrease(CryptoUtil.DECREASE);
                }
                cryptos.add(ltcCrypto);
                System.out.println("LTC CryptoValue is:" + " " + ltcCrypto.toString());
            }
        }
        return cryptos;
    }

    public static List<CryptoValue> parseAltCryptos(JSONObject jsonObject, List<Person> persons) {
        List<CryptoValue> cryptos = new ArrayList<>();
        NumberFormat currencyFormat = CryptoUtil.getCurrencyFormat();

        for (Person person : persons) {
            String symbol = person.getCoin().getCoin_symbol();
            if(!symbol.equalsIgnoreCase(CryptoUtil.BTC_SYMBOL)
                    && !symbol.equalsIgnoreCase(CryptoUtil.BCH_SYMBOL)
                    && !symbol.equalsIgnoreCase(CryptoUtil.ETH_SYMBOL)
                    && !symbol.equalsIgnoreCase(CryptoUtil.LTC_SYMBOL)){
                JSONObject obj = (JSONObject)jsonObject.get(symbol);
                if(obj != null){
                    CryptoValue cryptoValue = new CryptoValue();
                    Coin coin = new Coin();
                    coin.setCoin_name(person.getCoin().getCoin_name());
                    coin.setCoin_symbol(symbol);
                    coin.setCoin_id(person.getCoin().getCoin_id());
                    cryptoValue.setCoin(coin);
                    cryptoValue.setUSD(currencyFormat.format(obj.get(CryptoUtil.USD)));
                    Double dbl = Double.valueOf(obj.get(CryptoUtil.USD).toString());
                    cryptoValue.setHoldingValue(CryptoUtil.formatDoubleValue(person.getQuantity(), dbl));
                    cryptoValue.setCost(CryptoUtil.getAltCost(person.getCost(), person.getQuantity()));
                    cryptoValue.setQuantity(person.getQuantity());
                    if(!symbol.equalsIgnoreCase(CryptoUtil.BTG_SYMBOL)){
                        PercentageDTO dto = CryptoUtil.getPercentage(person.getQuantity(), person.getCost(), dbl);
                        cryptoValue.setPercentage(dto.getValueString());
                        if (dto.getValueDouble() >= 0.0) {
                            cryptoValue.setIncreaseDecrease(CryptoUtil.INCREASE);
                        } else {
                            cryptoValue.setIncreaseDecrease(CryptoUtil.DECREASE);
                        }
                    }else{
                        cryptoValue.setIncreaseDecrease(CryptoUtil.INCREASE);
                        cryptoValue.setPercentage("SEE BITCOIN");
                    }
                    cryptos.add(cryptoValue);
                    System.out.println("CryptoValue is:" + " " + cryptoValue.toString());
                }
            }

        }

        return cryptos;
    }

}

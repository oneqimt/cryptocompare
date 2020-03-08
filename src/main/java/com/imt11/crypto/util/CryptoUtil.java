package com.imt11.crypto.util;

import com.imt11.crypto.LoginServlet;
import com.imt11.crypto.model.CryptoValue;
import com.imt11.crypto.model.PercentageDTO;
import com.imt11.crypto.model.Person;
import com.imt11.crypto.model.TotalValues;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Dennis Miller
 */
public class CryptoUtil {

    public static final String INCREASE = "Increase";
    public static final String DECREASE = "Decrease";
    public static final String PERCENT = "%";
    public static final String ROLE_USER = "ROLE_USER";
    public static final int ENABLED = 1;
    public static final String SAVE = "save";
    public static final String UPDATE = "update";
    public static final String DELETE = "delete";
    public static final String GET_SLUGS = "getslugs";
    public static final String ADD_HOLDING = "addholding";
    public static final String DELETE_HOLDING = "deleteholding";
    public static final String UPDATE_HOLDING = "updateholding";
    public static final String ADD_COIN = "addcoin";

    public static final String CMC_COINS = "cmccoins";
    public static final String SINGLE_CMC_COIN = "singlecmccoin";
    public static final String DB_COINS = "dbcoins";
    public static final String SINGLE_DB_COIN = "singledbcoin";

    public static String USD = "USD";
    public static String BTC_SYMBOL = "BTC";
    public static String ETH_SYMBOL = "ETH";
    public static String LTC_SYMBOL = "LTC";
    public static String BCH_SYMBOL = "BCH";
    public static String BTG_SYMBOL = "BTG";
    private static DecimalFormat df2 = new DecimalFormat(".##");
    /*400 = Bad Request
    401 = Unauthorized
    402 = Payment Required
    403 = Forbidden
    404 = Not Found*/

    private CryptoUtil() {
    }

    public static void loadBaselineData() throws IOException, ParseException {
		InputStream resourceAsStream = LoginServlet.class.getResourceAsStream("/baseline.json");
		if (resourceAsStream != null) {
			InputStreamReader streamReader = new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8);
			BufferedReader reader = new BufferedReader(streamReader);
			StringBuilder sb = new StringBuilder();
			for (String line; (line = reader.readLine()) != null; ) {
				sb.append(line);
			}
			JSONParser parser = new JSONParser();
			JSONObject baseline = (JSONObject) parser.parse(sb.toString());
			String host = (String) baseline.get("host");
			String username = (String) baseline.get("username");
			String password = (String) baseline.get("password");
			String driver = (String) baseline.get("driver");
			String cryptoControlApiKey = (String) baseline.get("crypto_control_api_key");
			String cryptoCompareApiKey = (String) baseline.get("crypto_compare_api_key");
			String smtpHost = (String) baseline.get("smtp_host");
			String smtpPort = (String) baseline.get("smtp_port");
			String smtpPassword = (String) baseline.get("smtp_password");
			String smtpSenderEmail = (String) baseline.get("smtp_sender_email");
			String coinMarketCapProdApiKey = (String)baseline.get("coin_market_cap_prod_api_key");
			String coinMarketCapTestApiKey = (String)baseline.get("coin_market_cap_test_api_key");
			SecurityUtil.getInstance().setCoinMarketCapProdApiKey(coinMarketCapProdApiKey);
			SecurityUtil.getInstance().setCoinMarketCapTestApiKey(coinMarketCapTestApiKey);
			SecurityUtil.getInstance().setSmtpHost(smtpHost);
			SecurityUtil.getInstance().setSmtpPort(smtpPort);
			SecurityUtil.getInstance().setSmtpPassword(smtpPassword);
			SecurityUtil.getInstance().setSmtpSenderEmail(smtpSenderEmail);
			SecurityUtil.getInstance().setDriver(driver);
			SecurityUtil.getInstance().setHost(host);
			SecurityUtil.getInstance().setPassword(password);
			SecurityUtil.getInstance().setUsername(username);
			SecurityUtil.getInstance().setCryptoControlApi(cryptoControlApiKey);
			SecurityUtil.getInstance().setCryptoCompareApi(cryptoCompareApiKey);
		}
	}

    public static String getMainCryptoEndpoint(List<Person> persons) {

        String main = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=";
        String coinstr;
        String coda = "&tsyms=USD&e=Coinbase";
        String apiKey = "&apiKey=" + SecurityUtil.getInstance().getCryptoCompareApi();
        List<String> symbols = new ArrayList<>();
        for (Person person : persons) {
            String symbol = person.getCoin().getCoin_symbol();
            if (symbol.equalsIgnoreCase(CryptoUtil.BTC_SYMBOL)) {
                symbols.add(symbol);
            }
            if (symbol.equalsIgnoreCase(CryptoUtil.BCH_SYMBOL)) {
                symbols.add(symbol);
            }
            if (symbol.equalsIgnoreCase(CryptoUtil.ETH_SYMBOL)) {
                symbols.add(symbol);
            }
            if (symbol.equalsIgnoreCase(CryptoUtil.LTC_SYMBOL)) {
                symbols.add(symbol);
            }
        }

        coinstr = String.join(",", symbols);

        return main + coinstr + coda + apiKey;
    }

    public static String getAltCryptoEndpoint(List<Person> persons) {
        String main = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=";
        String coinstr;
        String coda = "&tsyms=BTC,USD";
        String apiKey = "&apiKey=" + SecurityUtil.getInstance().getCryptoCompareApi();
        List<String> symbols = new ArrayList<>();
        for (Person person : persons) {
            // don't add the main coins in
            String symbol = person.getCoin().getCoin_symbol();
            if (!symbol.equalsIgnoreCase(CryptoUtil.BTC_SYMBOL)
                    && !symbol.equalsIgnoreCase(CryptoUtil.BCH_SYMBOL)
                    && !symbol.equalsIgnoreCase(CryptoUtil.ETH_SYMBOL)
                    && !symbol.equalsIgnoreCase(CryptoUtil.LTC_SYMBOL)) {
                symbols.add(symbol);
            }

        }
        coinstr = String.join(",", symbols);

        return main + coinstr + coda + apiKey;
    }

    public static NumberFormat getCurrencyFormat() {
        Locale locale = new Locale("en", "US");
        return NumberFormat.getCurrencyInstance(locale);
    }

    public static NumberFormat getNumberFormat() {
        Locale locale = new Locale("en", "US");
        return NumberFormat.getInstance(locale);
    }

    public static String formatDoubleValue(Double val, Double factor) {
        NumberFormat currencyFormat = getCurrencyFormat();
        double dbl = val * factor;
        BigDecimal bd = new BigDecimal(Double.toString(dbl));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        double roundedDbl = bd.doubleValue();

        return currencyFormat.format(roundedDbl);
    }

    public static PercentageDTO getPercentage(Double quantityHeld, Double costPerUnit, Double currentValue) {

        double cost = Math.round(quantityHeld * costPerUnit);

        double value = Math.round(quantityHeld * currentValue);

        double testdbl = value - cost;

        double perc = (testdbl / cost);
        perc *= 100;
        String finalPercStr = df2.format(perc);

        String display = finalPercStr + PERCENT;

        PercentageDTO dto = new PercentageDTO();
        dto.setValueString(display);
        dto.setValueDouble(perc);

        return dto;

    }


    public static double getCost(double val, double factor) {
        //String mycost = getCurrencyFormat().format(cost);

        // TEST
        double dbl = val * factor;
        BigDecimal bd = new BigDecimal(Double.toString(dbl));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        //String test = df2.format(dbl);
        //System.out.println("DJM cost double is: "+" "+test);

        return bd.doubleValue();
    }

    public static String getAltCost(Double cost, Double factor) {

        double dbl = cost * factor;
        BigDecimal bd = new BigDecimal(Double.toString(dbl));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        double roundedDbl = bd.doubleValue();

        return getCurrencyFormat().format(roundedDbl);
    }

    public static TotalValues getGrandTotals(List<CryptoValue> combinedList) {

        // AGGREGATE VALUES
        double aggregate = 0.0;
        double btgval = 0.0;
        double btcval = 0.0;
        NumberFormat cf = CryptoUtil.getCurrencyFormat();
        Number btgnum = null;
        Number btcnum = null;
        for (CryptoValue val : combinedList) {
            if (val.getCoin().getCoin_name().equalsIgnoreCase(CryptoUtil.BTG_SYMBOL)) {
                try {
                    btgnum = cf.parse(val.getHoldingValue());
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                if (btgnum != null) {
                    btgval = btgnum.doubleValue();
                }
            }
            if (val.getCoin().getCoin_name().equalsIgnoreCase(CryptoUtil.BTC_SYMBOL)) {
                try {
                    btcnum = cf.parse(val.getUSD());
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                if (btcnum != null) {
                    btcval = btcnum.doubleValue();
                }
            }
        }
        // add BTG to total BTC
        aggregate = btgval + btcval;

        Number totalCost = null;
        Number totalValue = null;
        double costdbl = 0.0;
        double totaldbl = 0.0;

        for (CryptoValue val : combinedList) {
            try {
                totalCost = cf.parse(val.getCost());
                totalValue = cf.parse(val.getHoldingValue());
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            if (totalCost != null) {
                costdbl += totalCost.doubleValue();
            }
            if (totalValue != null) {
                totaldbl += totalValue.doubleValue();
            }

        }

        TotalValues grandTotals = new TotalValues();
        grandTotals.setTotalCost(cf.format(costdbl));
        grandTotals.setTotalValue(cf.format(totaldbl));
        double perc = (totaldbl / costdbl);
        perc *= 100;
        if (perc >= 0.0) {
            grandTotals.setIncreaseDecrease(CryptoUtil.INCREASE);
        } else {
            grandTotals.setIncreaseDecrease(CryptoUtil.DECREASE);
        }
        String finalPercStr = df2.format(perc);

        String display = finalPercStr + PERCENT;
        grandTotals.setTotalPercentageIncreaseDecrease(display);


		/*System.out.println("TOTAL COST is: "+" "+costdbl);
		System.out.println("TOTAL VALUE is:"+" "+totaldbl);
		System.out.println("TOTAL PERCENTAGE INCREASE/DECREASE :"+" "+display);
		System.out.println("TOTALINCREASE/DECREASE :"+" "+grandTotals.getIncreaseDecrease());*/

        return grandTotals;

    }

    public static String getStringJson(String endpoint) {

        StringBuilder sb = new StringBuilder();

        try {

            URL url = new URL(endpoint);

            URLConnection mainurlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mainurlConnection.getInputStream()));

            String mainLine;

            while ((mainLine = bufferedReader.readLine()) != null) {
                sb.append(mainLine).append("\n");
            }

            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();

    }


}

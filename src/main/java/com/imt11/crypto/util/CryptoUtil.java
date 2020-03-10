package com.imt11.crypto.util;

import com.imt11.crypto.LoginServlet;
import com.imt11.crypto.model.CryptoValue;
import com.imt11.crypto.model.PercentageDTO;
import com.imt11.crypto.model.TotalValues;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
    public static final String PERSON_CMC_COINS = "personcmccoins";
    public static final String DB_COINS = "dbcoins";
    public static final String SINGLE_DB_COIN = "singledbcoin";

    public static String USD = "USD";
    public static String BTC_SYMBOL = "BTC";
    public static String ETH_SYMBOL = "ETH";
    public static String LTC_SYMBOL = "LTC";
    public static String BCH_SYMBOL = "BCH";
    public static String BTG_SYMBOL = "BTG";
    public static int PROD_FLAG = 1;
    public static int TEST_FLAG = 2;
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
            String coinMarketCapProdApiKey = (String) baseline.get("coin_market_cap_prod_api_key");
            String coinMarketCapTestApiKey = (String) baseline.get("coin_market_cap_test_api_key");
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
            // SET PROD or TEST API flag here only
            SecurityUtil.getInstance().setFlag(PROD_FLAG);
        }
    }

    public static NumberFormat getCurrencyFormat() {
        Locale locale = new Locale("en", "US");
        return NumberFormat.getCurrencyInstance(locale);
    }

    public static String formatDoubleValue(Double val, Double factor) {
        NumberFormat currencyFormat = getCurrencyFormat();
        double dbl = val * factor;
       /* BigDecimal bd = new BigDecimal(Double.toString(dbl));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        double roundedDbl = bd.doubleValue();*/

        return currencyFormat.format(dbl);
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

    public static TotalValues getNewGrandTotals(List<CryptoValue> cryptoValues) {

        TotalValues grandTotals = new TotalValues();
        NumberFormat cf = CryptoUtil.getCurrencyFormat();
        Number totalCost = null;
        Number totalValue = null;
        double costdbl = 0.0;
        double totaldbl = 0.0;

        for (CryptoValue cv : cryptoValues) {
            try {
                totalCost = cf.parse(cv.getCost());
                totalValue = cf.parse(cv.getHoldingValue());
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
        grandTotals.setTotalCost(cf.format(costdbl));
        grandTotals.setTotalValue(cf.format(totaldbl));

        System.out.println("TOTAL COST is: " + " " + costdbl);
        System.out.println("TOTAL VALUE is: " + " " + totaldbl);
        double percentage = ((totaldbl - costdbl) / costdbl) * 100;
        System.out.println("PERCENTAGE is : " + " " + percentage);

        if (percentage >= 0.0) {
            grandTotals.setIncreaseDecrease(CryptoUtil.INCREASE);
        } else {
            grandTotals.setIncreaseDecrease(CryptoUtil.DECREASE);
        }
        String finalPercStr = df2.format(percentage);

        String display = finalPercStr + PERCENT;
        grandTotals.setTotalPercentageIncreaseDecrease(display);

        return grandTotals;

    }

}

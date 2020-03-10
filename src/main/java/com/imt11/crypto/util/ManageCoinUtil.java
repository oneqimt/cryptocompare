package com.imt11.crypto.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.imt11.crypto.model.Coin;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Dennis Miller
 */
public class ManageCoinUtil {

    public static final String PROD_ENDPOINT = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/";
    public static final String SANDBOX_ENDPOINT = "https://sandbox-api.coinmarketcap.com/v1/cryptocurrency/";
    public static final String LISTINGS_LATEST = "listings/latest";
    public static final String QUOTES_LATEST = "quotes/latest";
    // headers
    public static final String X_CMC_PRO_API_KEY = "X-CMC_PRO_API_KEY";
    public static final String APPLICATION_JSON = "application/json";

    private ManageCoinUtil(){}

    public static String getLatestCoinMarketCapEndpoint(){

        String prodEndpoint = PROD_ENDPOINT + LISTINGS_LATEST;
        String sandboxEndpoint =  SANDBOX_ENDPOINT + LISTINGS_LATEST;

        switch(SecurityUtil.getInstance().getFlag()){
            case 1:
                return prodEndpoint;
            case 2:
                return sandboxEndpoint;
        }
        return null;
    }

    public static String getQuotesCoinMarketCapEndpoint(){
        String prodEndpoint = PROD_ENDPOINT + QUOTES_LATEST;
        String sandboxEndpoint =  SANDBOX_ENDPOINT + QUOTES_LATEST;

        switch (SecurityUtil.getInstance().getFlag()){
            case 1:
                return prodEndpoint;
            case 2:
                return sandboxEndpoint;
        }

        return null;
    }


    public static Boolean checkUpdateCounts(int[] updateCounts) {
        Boolean noErrors = null;
        for (int updateCount : updateCounts) {
            if (updateCount >= 0) {
                System.out.println("OK; updateCount=" + updateCount);
                noErrors = true;
            } else if (updateCount == Statement.SUCCESS_NO_INFO) {
                System.out.println("OK; updateCount=Statement.SUCCESS_NO_INFO");
                noErrors = true;
            } else if (updateCount == Statement.EXECUTE_FAILED) {
                System.out.println("Failure; updateCount=Statement.EXECUTE_FAILED");
                noErrors = false;
            }
        }

        return noErrors;
    }

    public static List<Coin> parsePersonCoinList(String responseStr){
        List<Coin> coins = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonElement jsonTree = parser.parse(responseStr);

        if (jsonTree.isJsonObject()){
            JsonObject jsonObject = jsonTree.getAsJsonObject();
            JsonElement dataObj = jsonObject.get("data");
            if (dataObj.isJsonObject()) {
                JsonObject mydata = dataObj.getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entrySet = mydata.entrySet();
                for (Map.Entry<String, JsonElement> entry : entrySet) {
                    Coin coin = new Coin();
                    JsonObject valueobj = (JsonObject) entry.getValue();
                    JsonElement name = valueobj.get("name");
                    JsonElement id = valueobj.get("id");
                    JsonElement symbol = valueobj.get("symbol");
                    JsonElement myslug = valueobj.get("slug");
                    JsonElement cncRank = valueobj.get("cmc_rank");
                    //this is our coin_id which we will not get from CoinMarketCap
                    coin.setCoin_id(0);
                    coin.setCoin_name(name.getAsString());
                    coin.setCoin_symbol(symbol.getAsString());
                    coin.setCmc_id(id.getAsInt());
                    coin.setSlug(myslug.getAsString());
                    coin.setCmc_rank(cncRank.getAsInt());

                    Set<Map.Entry<String, JsonElement>> quoteEntrySet = valueobj.entrySet();
                    for (Map.Entry<String, JsonElement> quoteEntry: quoteEntrySet){
                        String str = quoteEntry.getKey();
                        if (str.equalsIgnoreCase("quote")){
                            JsonObject quoteObj = (JsonObject) quoteEntry.getValue();
                            JsonObject usdobj = (JsonObject) quoteObj.get("USD");
                            JsonElement marketCap = usdobj.get("market_cap");
                            JsonElement price = usdobj.get("price");
                            coin.setMarket_cap(marketCap.getAsBigDecimal());
                            coin.setCurrentPrice(price.getAsBigDecimal());
                        }
                    }

                    coins.add(coin);

                }
            }
        }

        return coins;
    }

    public static Coin parseSingleCoin(String responseStr){
        Coin coin = new Coin();
        JsonParser parser = new JsonParser();
        JsonElement jsonTree = parser.parse(responseStr);

        if (jsonTree.isJsonObject()) {
            JsonObject jsonObject = jsonTree.getAsJsonObject();
            JsonElement dataObj = jsonObject.get("data");

            if (dataObj != null && dataObj.isJsonObject()) {
                JsonObject mydata = dataObj.getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entrySet = mydata.entrySet();
                for (Map.Entry<String, JsonElement> entry : entrySet) {
                    JsonObject valueobj = (JsonObject) entry.getValue();
                    JsonElement name = valueobj.get("name");
                    JsonElement id = valueobj.get("id");
                    JsonElement symbol = valueobj.get("symbol");
                    JsonElement myslug = valueobj.get("slug");
                    JsonElement cncRank = valueobj.get("cmc_rank");
                    //this is our coin_id which we will not get from CoinMarketCap
                    coin.setCoin_id(0);
                    coin.setCoin_name(name.getAsString());
                    coin.setCoin_symbol(symbol.getAsString());
                    coin.setCmc_id(id.getAsInt());
                    coin.setSlug(myslug.getAsString());
                    coin.setCmc_rank(cncRank.getAsInt());

                    Set<Map.Entry<String, JsonElement>> quoteEntrySet = valueobj.entrySet();
                    for (Map.Entry<String, JsonElement> quoteEntry: quoteEntrySet){
                        String str = quoteEntry.getKey();
                        if (str.equalsIgnoreCase("quote")){
                            JsonObject quoteObj = (JsonObject) quoteEntry.getValue();
                            JsonObject usdobj = (JsonObject) quoteObj.get("USD");
                            JsonElement marketCap = usdobj.get("market_cap");
                            JsonElement price = usdobj.get("price");
                            coin.setMarket_cap(marketCap.getAsBigDecimal());
                            coin.setCurrentPrice(price.getAsBigDecimal());
                        }
                    }

                }
            }
        }

        return coin;
    }



}

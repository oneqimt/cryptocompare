package com.imt11.crypto.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.imt11.crypto.model.Coin;

import java.sql.Statement;
import java.util.Map;
import java.util.Set;

/**
 * @author Dennis Miller
 */
public class ManageCoinUtil {

    private ManageCoinUtil(){}

    public static String getCoinMarketCapEndpoint(int flag){

        String prodEndpoint = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        String sandboxEndpoint =  "https://sandbox-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";

        switch(flag){
            case 1:
                return prodEndpoint;
            case 2:
                return sandboxEndpoint;
        }
        return null;
    }

    public static String getSingleCoinCoinMarketCapEndpoint(int flag){
        String prodEndpoint = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest";
        String sandboxEndpoint =  "https://sandbox-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest";

        switch (flag){
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
                    //this is our coin_id which we will not get from CoinMarketCap
                    coin.setCoin_id(0);
                    coin.setCoin_name(name.getAsString());
                    coin.setCoin_symbol(symbol.getAsString());
                    coin.setCmc_id(id.getAsInt());
                    coin.setSlug(myslug.getAsString());

                    Set<Map.Entry<String, JsonElement>> quoteEntrySet = valueobj.entrySet();
                    for (Map.Entry<String, JsonElement> quoteEntry: quoteEntrySet){
                        String str = quoteEntry.getKey();
                        if (str.equalsIgnoreCase("quote")){
                            JsonObject quoteObj = (JsonObject) quoteEntry.getValue();
                            JsonObject usdobj = (JsonObject) quoteObj.get("USD");
                            JsonElement marketCap = usdobj.get("market_cap");
                            coin.setMarket_cap(marketCap.getAsBigDecimal());
                        }
                    }

                }
            }
        }

        return coin;
    }



}

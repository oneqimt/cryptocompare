package com.imt11.crypto.util;

/**
 * @author Dennis Miller
 */
public class HoldingsUtil {

    private HoldingsUtil(){

    }

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

}

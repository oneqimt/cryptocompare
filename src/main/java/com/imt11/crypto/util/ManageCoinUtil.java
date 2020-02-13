package com.imt11.crypto.util;

import java.sql.Statement;

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



}

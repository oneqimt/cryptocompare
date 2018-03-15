package com.imt11.crypto.util;

import com.imt11.crypto.model.CryptoValue;
import com.imt11.crypto.model.PercentageDTO;
import com.imt11.crypto.model.Person;
import com.imt11.crypto.model.TotalValues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Dennis Miller
 */
public class CryptoUtil {

	public static double BTC = 12.83;

	public static String USD = "USD";
	public static String BTC_SYMBOL = "BTC";
	public static String ETH_SYMBOL = "ETH";
	public static String LTC_SYMBOL = "LTC";
	public static String BCH_SYMBOL = "BCH";
	public static String BTG_SYMBOL = "BTG";

	public static final String INCREASE = "Increase";
	public static final String DECREASE = "Decrease";
	public static final String PERCENT = "%";

	private static DecimalFormat df2 = new DecimalFormat(".##");


private CryptoUtil(){}

	public static String getMainCryptoEndpoint(List<Person> persons){
		//String endpoint = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,ETH,LTC,BCH&tsyms=USD&e=Coinbase";

		// https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,BCH,ETH,LTC&tsyms=USD&e=Coinbase
		String main = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=";
		String coinstr = "";
		String coda = "&tsyms=USD&e=Coinbase";
		List<String> symbols = new ArrayList<>();
		for(Person person : persons){
			String symbol = person.getCoin().getCoin_symbol();
			if (symbol.equalsIgnoreCase(CryptoUtil.BTC_SYMBOL)){
				symbols.add(symbol);
			}
			if(symbol.equalsIgnoreCase(CryptoUtil.BCH_SYMBOL)){
				symbols.add(symbol);
			}
			if(symbol.equalsIgnoreCase(CryptoUtil.ETH_SYMBOL)){
				symbols.add(symbol);
			}
			if(symbol.equalsIgnoreCase(CryptoUtil.LTC_SYMBOL)){
				symbols.add(symbol);
			}
		}

		coinstr = String.join(",", symbols);

		String endpoint = main + coinstr + coda;

		System.out.println("ENDPOUNT is: "+endpoint);

		return endpoint;
	}

	public static String getAltCryptoEndpoint(List<Person> persons){
		String main = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=";
		String coinstr = "";
		String coda = "&tsyms=BTC,USD";
		List<String> symbols = new ArrayList<>();
		List<Person> cleanPersons = new ArrayList<>();
		for (Person person : persons){
			// don't add the main coins in
			String symbol = person.getCoin().getCoin_symbol();
			if(!symbol.equalsIgnoreCase(CryptoUtil.BTC_SYMBOL)
					&& !symbol.equalsIgnoreCase(CryptoUtil.BCH_SYMBOL)
					&& !symbol.equalsIgnoreCase(CryptoUtil.ETH_SYMBOL)
					&& !symbol.equalsIgnoreCase(CryptoUtil.LTC_SYMBOL)){
				symbols.add(symbol);
			}

		}
		coinstr = String.join(",", symbols);
		String endpoint = main + coinstr + coda;

		System.out.println("ALT ENDPOUNT is: "+endpoint);

		return endpoint;
	}

	public static NumberFormat getCurrencyFormat(){
		Locale locale = new Locale("en", "US");
		NumberFormat format = NumberFormat.getCurrencyInstance(locale);
		return format;
	}

	public static NumberFormat getNumberFormat(){
		Locale locale = new Locale("en", "US");
		NumberFormat format = NumberFormat.getInstance(locale);
		return format;
	}

	public static String formatDoubleValue(Double val, Double factor){
		NumberFormat currencyFormat = getCurrencyFormat();
		double dbl = val * factor;
		BigDecimal bd = new BigDecimal(Double.toString(dbl));
		bd = bd.setScale(2, RoundingMode.HALF_UP);
		double roundedDbl = bd.doubleValue();
		String aggregate = currencyFormat.format(roundedDbl);

		return aggregate;
	}

	public static PercentageDTO getPercentage(Double quantityHeld, Double costPerUnit, Double currentValue){

		/*System.out.println("UTL quantityHeld : "+" "+quantityHeld);
		System.out.println("UTL costPerUnit : "+" "+costPerUnit);
		System.out.println("UTL currentValue : "+" "+currentValue);*/

		double cost = Math.round(quantityHeld * costPerUnit);
		//System.out.println("UTL COST is:"+" "+cost);
		double value = Math.round(quantityHeld * currentValue);
		//System.out.println("UTL VALUE"+" "+value);

		double testdbl = value - cost;
		//System.out.println("UTL VALUE-COST is: "+" "+testdbl);

		double perc = (testdbl / cost);
		perc *= 100;
		String finalPercStr = df2.format(perc);
		//System.out.println("UTL TEST PERCENT INCREASE/DECREASE is:"+" "+finalPercStr);

		String display = finalPercStr + "%";
		//System.out.println("UTL TEST DISPLAY PERCENT is: "+" "+display);

		PercentageDTO dto = new PercentageDTO();
		dto.setValueString(display);
		dto.setValueDouble(perc);

		return dto;

	}


	public static double getCost(double val, double factor){
		//String mycost = getCurrencyFormat().format(cost);

		// TEST
		double dbl = val * factor;
		BigDecimal bd = new BigDecimal(Double.toString(dbl));
		bd = bd.setScale(2, RoundingMode.HALF_UP);
		//String test = df2.format(dbl);
		//System.out.println("DJM cost double is: "+" "+test);
		double roundedDbl = bd.doubleValue();

		return roundedDbl;
	}

	public static String getAltCost(Double cost, Double factor){

		double dbl = cost * factor;
		BigDecimal bd = new BigDecimal(Double.toString(dbl));
		bd = bd.setScale(2, RoundingMode.HALF_UP);
		double roundedDbl = bd.doubleValue();
		String aggregate = getCurrencyFormat().format(roundedDbl);

		return aggregate;
	}

	public static TotalValues getGrandTotals(List<CryptoValue> combinedList){

		// AGGREGATE VALUES
		double aggregate = 0.0;
		double btgval = 0.0;
		double btcval = 0.0;
		NumberFormat cf = CryptoUtil.getCurrencyFormat();
		Number btgnum = null;
		Number btcnum = null;
		for (CryptoValue val : combinedList){
			if(val.getCoin().getCoin_name().equalsIgnoreCase(CryptoUtil.BTG_SYMBOL)){
				try{
					btgnum = cf.parse(val.getHoldingValue());
				}catch(java.text.ParseException e){
					e.printStackTrace();
				}
				btgval = btgnum.doubleValue();
			}
			if (val.getCoin().getCoin_name().equalsIgnoreCase(CryptoUtil.BTC_SYMBOL)) {
				try{
					btcnum = cf.parse(val.getUSD());
				}catch (java.text.ParseException e){
					e.printStackTrace();
				}
				btcval = btcnum.doubleValue();
			}
		}
		// add BTG to total BTC
		aggregate = btgval + btcval;

		Number totalCost = null;
		Number totalValue = null;
		double costdbl = 0.0;
		double totaldbl = 0.0;

		for(CryptoValue val : combinedList){

			if (val.getCoin().getCoin_name().equalsIgnoreCase(CryptoUtil.BTC_SYMBOL)){
				// update holding value and percentage to include BTG
				PercentageDTO btcdto = CryptoUtil.getPercentage(CryptoUtil.BTC, 600.00, aggregate);
				val.setPercentage(btcdto.getValueString());
				if (btcdto.getValueDouble() >= 0.0 ){
					val.setIncreaseDecrease(CryptoUtil.INCREASE);
				}else{
					val.setIncreaseDecrease(CryptoUtil.DECREASE);
				}

				System.out.println("NEW BTC CRYPTO is: "+" "+val.toString());
			}
			try{
				totalCost =  cf.parse(val.getCost());
				totalValue = cf.parse(val.getHoldingValue());
			}catch(java.text.ParseException e){
				e.printStackTrace();
			}
			costdbl += totalCost.doubleValue();
			totaldbl += totalValue.doubleValue();

		}

		TotalValues grandTotals = new TotalValues();
		grandTotals.setTotalCost(cf.format(costdbl));
		grandTotals.setTotalValue(cf.format(totaldbl));
		double perc = (totaldbl / costdbl);
		perc *= 100;
		if(perc >= 0.0){
			grandTotals.setIncreaseDecrease(CryptoUtil.INCREASE);
		}else{
			grandTotals.setIncreaseDecrease(CryptoUtil.DECREASE);
		}
		String finalPercStr = df2.format(perc);

		String display = finalPercStr + "%";
		grandTotals.setTotalPercentageIncreaseDecrease(display);


		/*System.out.println("TOTAL COST is: "+" "+costdbl);
		System.out.println("TOTAL VALUE is:"+" "+totaldbl);
		System.out.println("TOTAL PERCENTAGE INCREASE/DECREASE :"+" "+display);
		System.out.println("TOTALINCREASE/DECREASE :"+" "+grandTotals.getIncreaseDecrease());*/

		return grandTotals;

	}

	public static String getStringJson(String endpoint){

		StringBuilder sb = new StringBuilder();

		try{

			URL url = new URL(endpoint);
			URLConnection mainurlConnection = url.openConnection();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mainurlConnection.getInputStream()));

			String mainLine;

			while ((mainLine = bufferedReader.readLine()) != null){
				sb.append(mainLine + "\n");
			}

			bufferedReader.close();

		}catch(MalformedURLException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}

		return sb.toString();

	}



}

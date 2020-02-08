package com.imt11.crypto.database;

import com.imt11.crypto.util.HoldingsUtil;
import com.imt11.crypto.util.SecurityUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dennis Miller
 */
public class HoldingsDAO {

    // get coin slugs from coin market cap API

    public String getLatestFromCoinMarketCap() throws URISyntaxException, IOException {
        String responseContent = "";
        String testApiKey = SecurityUtil.getInstance().getCoinMarketCapTestApiKey();
        String prodApiKey = SecurityUtil.getInstance().getCoinMarketCapProdApiKey();

        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("start","1"));
        parameters.add(new BasicNameValuePair("limit","200"));
        parameters.add(new BasicNameValuePair("convert","USD"));

        // 1 = prod 2 = test
        String uri = HoldingsUtil.getCoinMarketCapEndpoint(2);
        URIBuilder query = new URIBuilder(uri);
        query.addParameters(parameters);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader("X-CMC_PRO_API_KEY", testApiKey);

        try (CloseableHttpResponse response = client.execute(request)) {
            System.out.println("RESPONSE IS: " + " " + response.getStatusLine());
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        }

        return responseContent;
    }

}

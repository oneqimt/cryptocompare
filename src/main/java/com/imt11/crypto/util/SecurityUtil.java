package com.imt11.crypto.util;

/**
 * @author Dennis Miller
 */
public class SecurityUtil {

    private static SecurityUtil instance;

    private String host;
    private String username;
    private String password;
    private String driver;
    private String cryptoCompareApi;
    private String smtpHost;
    private String smtpPort;
    private String smtpPassword;
    private String smtpSenderEmail;
    private String coinMarketCapProdApiKey;
    private String coinMarketCapTestApiKey;
    private int flag;

    private SecurityUtil(){

    }

    public static synchronized SecurityUtil getInstance(){
        if(instance == null){
            instance = new SecurityUtil();
        }

        return instance;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getCryptoCompareApi() {
        return cryptoCompareApi;
    }

    public void setCryptoCompareApi(String cryptoCompareApi) {
        this.cryptoCompareApi = cryptoCompareApi;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    public String getSmtpSenderEmail() {
        return smtpSenderEmail;
    }

    public void setSmtpSenderEmail(String smtpSenderEmail) {
        this.smtpSenderEmail = smtpSenderEmail;
    }

    public String getCoinMarketCapProdApiKey() {
        return coinMarketCapProdApiKey;
    }

    public void setCoinMarketCapProdApiKey(String coinMarketCapProdApiKey) {
        this.coinMarketCapProdApiKey = coinMarketCapProdApiKey;
    }

    public String getCoinMarketCapTestApiKey() {
        return coinMarketCapTestApiKey;
    }

    public void setCoinMarketCapTestApiKey(String coinMarketCapTestApiKey) {
        this.coinMarketCapTestApiKey = coinMarketCapTestApiKey;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

}

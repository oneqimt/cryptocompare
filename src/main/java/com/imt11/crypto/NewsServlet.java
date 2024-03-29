package com.imt11.crypto;

import com.google.gson.Gson;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dennis Miller
 */
@WebServlet(name = "NewsServlet", urlPatterns = {"/coinnews"},
        initParams = {@WebInitParam(name = "coin_name", value = "")})
public class NewsServlet extends HttpServlet {

    private Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // arg passed in from client
        String coin_name = request.getParameter("coin_name");

        // https://www.imtmobileapps.com/cryptocompare/coinnews?coin_name=bitcoin

        System.out.println("IN NEWS SERVLET and coin_name is: " + " " + coin_name);

        // Connect to the CryptoControl API
       // CryptoControlApi api = new CryptoControlApi(SecurityUtil.getInstance().getCryptoControlApi());

       // List<io.cryptocontrol.cryptonewsapi.models.Article> articles = new ArrayList<>();

        if (coin_name != null) {

            // WE NEED A NEW NEWS API form somwhere
        }
            /*api.getTopNewsByCoin(coin_name, new CryptoControlApi.OnResponseHandler<List<io.cryptocontrol.cryptonewsapi.models.Article>>() {

                @Override
                public void onSuccess(List<io.cryptocontrol.cryptonewsapi.models.Article> body) {

                    articles.addAll(body);
                    *//*for (io.cryptocontrol.cryptonewsapi.models.Article article : body) {

                    }*//*
                }

                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                    System.out.println("FAIL and error is : " + " " + e.getLocalizedMessage());

                }
            });

            String articleJson = this.gson.toJson(articles);
            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(articleJson);
            out.flush();
        } else {

            System.out.println("NO COINS FOR THAT NAME!");

        }*/

        /*if (articles.size() > 0) {
            for (Article article : articles) {
                System.out.println("OUTER LOOP of arraylist: " + " " + article.getTitle());
                Article.Source source = article.getSource();
                System.out.println("OUTER LOOP source name is : "+" "+source.getName());

                List<Article.SimilarArticle> similarArticles = article.getSimilarArticles();
                if (similarArticles != null){
                    for (Article.SimilarArticle similarArticle : similarArticles){
                        System.out.println("INNER LOOP and similarArticle title is: "+" "+similarArticle.getTitle());

                    }

                }

            }
        }*/


    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }


}

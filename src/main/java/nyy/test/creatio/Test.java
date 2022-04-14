package nyy.test.creatio;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import java.io.IOException;

public class Test {


  public static void main(String[] args) throws IOException {

    SSLConnectionSocketFactory nailedSocketFactory = new SSLConnectionSocketFactory(
            SSLContexts.createDefault(),
            new String[]{"TLSv1.1", "TLSv1.2"},
            null,
            SSLConnectionSocketFactory.getDefaultHostnameVerifier());


    CloseableHttpClient nailedClient = HttpClients.custom()
                                               .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                                               .setSSLSocketFactory(nailedSocketFactory)
                                               .build();


    CloseableHttpClient normalClient = HttpClients.custom()
                                               .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                                               .build();


    System.out.println("\nUsing Nailed Client");
    testPost("https://sit-jurassic.creatio.com", nailedClient);
    testPost("https://uat-jurassic.creatio.com", nailedClient);
    testPost("https://jurassic.creatio.com", nailedClient);

    System.out.println("\nUsing Standard Client");
    testPost("https://sit-jurassic.creatio.com", normalClient);
    testPost("https://uat-jurassic.creatio.com", normalClient);
    testPost("https://jurassic.creatio.com", normalClient);

    normalClient.close();
    nailedClient.close();
  }


  static void testPost(String host,CloseableHttpClient client) throws IOException {
    try {
      HttpPost httpPost = new HttpPost(host + "/ServiceModel/AuthService.svc/Login");

      String json = """
              {
                "UserName": "prod_int",
                "UserPassword": "wrong_password"
              }
                          """;
      StringEntity entity = new StringEntity(json);
      httpPost.setEntity(entity);
      httpPost.setHeader("Accept", "application/json");
      httpPost.setHeader("Content-type", "application/json");

      client.execute(httpPost);
      System.out.println("Host "+ host+ " Success!");
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Host "+ host+ " Failed!");
    }
  }
}

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetItemIDServletTest {

    public static void main(String[] args) throws IOException {

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(
                "http://localhost:8080/recommendSystem/getItemId?userid=2001731");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);
        BufferedReader br = new BufferedReader(new InputStreamReader(
                response.getEntity().getContent()));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        System.out.println(sb.toString());
        client.close();
    }
}
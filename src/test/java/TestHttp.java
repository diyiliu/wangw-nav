import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Description: TestHttp
 * Author: DIYILIU
 * Update: 2017-11-06 16:27
 */

public class TestHttp {

    @Test
    public void test() throws Exception {
        URL url = new URL("http://mvnrepository.com/");
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.62 Safari/537.36");

            int respCode = httpURLConnection.getResponseCode();
            if (respCode != 200){
                String location = httpURLConnection.getHeaderField("Location");
                httpURLConnection = (HttpURLConnection) new URL(location).openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.62 Safari/537.36");

            }

            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
            String line;
            while ((line = br.readLine())!= null ){
                System.out.println(line);
                if (line.contains(".ico")){
                    System.out.println("ok!");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: TestHttp
 * Author: DIYILIU
 * Update: 2017-11-06 16:27
 */

public class TestHttp {

    @Test
    public void test() throws Exception {
        String location = "http://www.baidu.com";
        URL url = new URL(location);
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.62 Safari/537.36");

            int respCode = httpURLConnection.getResponseCode();

            System.out.println(respCode);
            if (respCode != 200){
                location = httpURLConnection.getHeaderField("Location");
                httpURLConnection = (HttpURLConnection) new URL(location).openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.62 Safari/537.36");
                respCode = httpURLConnection.getResponseCode();
            }

            System.out.println(respCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
            String line;
            while ((line = br.readLine())!= null ){
                System.out.println(line);
                if (line.contains(".ico")){
                    // 取出有用的范围
                    Pattern p = Pattern.compile("(.*)(href=\")(.*?)(.ico\")(.*)");
                    Matcher m = p.matcher(line);
                    if (m.matches()){
                        String path = m.group(3);
                        if (path.contains("//")){
                            path += ".ico";
                        }else {
                            path = location + path + ".ico";
                        }

                        System.out.println(path);
                        System.out.println("ok!");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2(){

        String str = "https://";

        System.out.println(str.substring(0, str.indexOf(":") + 1));
    }
}

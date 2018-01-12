import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
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
        String location = "http://hao.uisdc.com";
        URL url = new URL(location);
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.62 Safari/537.36");

            int respCode = httpURLConnection.getResponseCode();

            if (respCode != 200) {
                location = httpURLConnection.getHeaderField("Location");
                httpURLConnection = (HttpURLConnection) new URL(location).openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.62 Safari/537.36");
                respCode = httpURLConnection.getResponseCode();
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
            String line;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                if (line.contains(".ico")) {
                    // 取出有用的范围
                    Pattern p = Pattern.compile("<link[^>]*href=\"(?<href>[^\"]*)\"[^>]*>");
                    Matcher m = p.matcher(line);
                    if (m.matches()) {
                        String path = m.group(1);
                        if (path.contains("//")) {
                            path += ".ico";
                        } else {
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
    public void test2() {

        String str = "https://";

        System.out.println(str.substring(0, str.indexOf(":") + 1));
    }


    @Test
    public void test3() throws Exception {
        String url = "www.ui.cn";
        url = "www.baidu.com";
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.62 Safari/537.36";
        String scheme = "http";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.USER_AGENT, userAgent);

        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                scheme + "://" + url,
                HttpMethod.GET,
                new HttpEntity<byte[]>(headers),
                byte[].class);


        int statusCode = responseEntity.getStatusCode().value();
        if (301 == statusCode || 302 == statusCode) {
            URI uri = responseEntity.getHeaders().getLocation();
            scheme = uri.getScheme();
            url = uri.getHost();
            responseEntity = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<byte[]>(headers),
                    byte[].class);
            statusCode = responseEntity.getStatusCode().value();
        }

        if (200 != statusCode) {
            System.out.println(statusCode);
            return;
        }

        InputStream inputStream = new ByteArrayInputStream(responseEntity.getBody());
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains(".ico")) {
                // 取出有用的范围
                Pattern p = Pattern.compile(".*<link[^>]*href=\"(?<href>[^\"]*)\"[^>]*>");
                Matcher m = p.matcher(line.trim());
                if (m.matches()) {
                    String path = m.group(1);
                    if (!path.contains("//")) {
                        path = scheme + "://" + url + path;
                    }

                    System.out.println(path);
                    System.out.println("ok!");
                    break;
                }
            }
        }
    }

    @Test
    public void test4() {
        String str = "<link rel=\"shortcut icon\" href=\"/favicon.ico\" type=\"image/x-icon\" />";
        Pattern p = Pattern.compile(".*<link[^>]*href=\"(?<href>[^\"]*)\"[^>]*>");
        Matcher m = p.matcher(str);
        if (m.matches()) {
            String path = m.group(1);

            System.out.println(path);
            System.out.println("ok!");
        }
    }
}

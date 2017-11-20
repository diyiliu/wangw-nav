import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: TestReg
 * Author: DIYILIU
 * Update: 2017-10-31 16:59
 */


public class TestReg {

    @Test
    public void test(){

        String reg = "[hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://|/+$";

        String url = "http://www.baidu.com///////";
        System.out.println(url.replaceAll(reg, ""));
    }

    @Test
    public void test1(){
        String reg ="^<img.*>";

        String str = "<img src=\\\"upload/icon3845535834536403953.ico\\\">淘宝";
        System.out.println(str.replaceAll(reg, ""));
    }

    @Test
    public void test2(){
        String str = "<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"http://static.zcool.cn/git_z/z/site/favicon.ico?version=1510912853039\">";
        Pattern p = Pattern.compile("(.*)(href=\")(.*?.ico.*\")(.*)");
        Matcher m = p.matcher(str);
        if (m.matches()) {
            String path = m.group(3);
            System.out.println(path);
        }




    }
}

import org.junit.Test;

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
}

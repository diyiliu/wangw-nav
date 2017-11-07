import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.nio.file.Files;

/**
 * Description: TestFile
 * Author: DIYILIU
 * Update: 2017-11-01 09:52
 */

public class TestFile {


    @Test
    public void test() throws Exception {

        Resource resource = new FileSystemResource("file:./upload");

        System.out.println(resource.getFile().listFiles().length);

    }

    @Test
    public void test2() throws Exception {
        String path = "C:\\Users\\DIYILIU\\Desktop\\临时\\favicon.ico";
        File file = new File(path);

        BASE64Encoder encoder = new BASE64Encoder();
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        byte[] bytes = new byte[in.available()];
        in.read(bytes);

        String str = encoder.encode(bytes);
        System.out.println(str);
    }

    @Test
    public void testBase64() throws Exception {
        String path = "C:\\Users\\DIYILIU\\Desktop\\临时";
        File file = new File(path + "/t.ico");

        String str = "AAABAAEAMDAAAAEAIACoJQAAFgAAACgAAAAwAAAAYAAAAAEAIAAAAAAAACQAABILAAASCwAAAAAAAAAAAAANAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM//DQDP/w0Az/8NAM8=";

        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = decoder.decodeBuffer(str);
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] < 0) {
                bytes[i] += 256;
            }
        }

        FileCopyUtils.copy(bytes, new FileOutputStream(file));
    }
}

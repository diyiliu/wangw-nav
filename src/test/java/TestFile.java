import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Description: TestFile
 * Author: DIYILIU
 * Update: 2017-11-01 09:52
 */

public class TestFile {


    @Test
    public void test() throws Exception{

        Resource resource = new FileSystemResource("file:./upload");

        System.out.println(resource.getFile().listFiles().length);

    }
}

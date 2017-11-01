import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Description: TestDao
 * Author: DIYILIU
 * Update: 2017-11-01 16:26
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class TestDao {

    @Resource
    private QueryRunner queryRunner;

    @Test
    public void test() throws Exception{
        String sql = "INSERT INTO nav_type(name,top)VALUES (?,0)";
        Object[] params = new Object[]{"测试"};

        Map rs = queryRunner.insert(sql, new MapHandler(), params);

        System.out.println(rs);
    }
}

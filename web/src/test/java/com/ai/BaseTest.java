package com.ai;

import com.ai.MainApplicaion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2024/04/19/23:07
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MainApplicaion.class)
@ActiveProfiles("dev") //指定配置文件
public class BaseTest {

}

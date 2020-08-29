package com.multimodule.mysql;

import com.multimodule.mysql.entity.TestEntity;
import com.multimodule.mysql.entity.TestEntity2;
import com.multimodule.mysql.repository.TestRepository;
import com.multimodule.mysql.repository.TestRepository2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplicationConfig.class)
@ActiveProfiles(profiles = "IT")
public class JpaAwareMysqlMultiModuleTest {


    @Autowired
    private TestRepository testRepository;

    @Autowired
    private TestRepository2 testRepository2;

    @Autowired
    private TestService testService;

    @Test
    public void testInsertAndGet(){
        TestEntity testEntity=new TestEntity();
        testEntity.setName("hello");
        testRepository.save(testEntity);
        TestEntity testEntity1 = testRepository.findNameByName("hello");
        Assert.assertEquals(testEntity.getName(),testEntity1.getName());
    }



    @Test
    public void checkTransactionRevertOnException(){
        try {
            testService.throwException();
        }catch (RuntimeException re){
            TestEntity entity_one_entry = testRepository.findNameByName("entity one entry");
            Assert.assertEquals(entity_one_entry,null);
        }
    }

    @Test
    public void checkPositiveTransactionCase(){
        testService.withoutExceptionTransaction();
        TestEntity positive_entry1 = testRepository.findNameByName("positive entry1");
        TestEntity2 positive_entry2 = testRepository2.findNameByName("positive entry2");
        Assert.assertNotNull(positive_entry1);
        Assert.assertNotNull(positive_entry2);
    }



}

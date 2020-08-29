package com.multimodule.mysql;

import com.multimodule.mysql.entity.TestEntity;
import com.multimodule.mysql.entity.TestEntity2;
import com.multimodule.mysql.repository.TestRepository;
import com.multimodule.mysql.repository.TestRepository2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private TestRepository2 testRepository2;


    @Transactional(transactionManager = "module1-db1-master-transaction")
    public void  throwException(){
        TestEntity testEntity=new TestEntity();
        testEntity.setName("entity one entry");
        testRepository.save(testEntity);
        TestEntity2 testEntity1=new TestEntity2();
        testEntity1.setName("entity 2 entry");
        throw new RuntimeException();
    }

    @Transactional(transactionManager = "module1-db1-master-transaction")
    public void  withoutExceptionTransaction(){
        TestEntity testEntity=new TestEntity();
        testEntity.setName("positive entry1");
        testRepository.save(testEntity);
        TestEntity2 testEntity1=new TestEntity2();
        testEntity1.setName("positive entry2");
        testRepository2.save(testEntity1);
    }


}

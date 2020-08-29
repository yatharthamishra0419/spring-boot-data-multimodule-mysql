package com.multimodule.mysql.repository;

import com.multimodule.mysql.entity.TestEntity2;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository2 extends JpaRepository<TestEntity2,Integer> {
    public TestEntity2 findNameByName(String name);
}

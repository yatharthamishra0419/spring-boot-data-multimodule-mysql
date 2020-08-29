package com.multimodule.mysql.repository;

import com.multimodule.mysql.entity.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<TestEntity,Integer> {

    public TestEntity findNameByName(String name);
}

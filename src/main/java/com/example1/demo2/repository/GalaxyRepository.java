package com.example1.demo2.repository;

import com.example1.demo2.pojo.User;
import com.example1.demo2.service.impl.IKnowledgeGalaxyService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalaxyRepository extends CrudRepository<IKnowledgeGalaxyService, Integer> {
}

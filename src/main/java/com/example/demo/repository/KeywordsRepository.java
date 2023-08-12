package com.example.demo.repository;

import com.example.demo.entity.Keywords;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
@Document
public interface KeywordsRepository extends MongoRepository<Keywords,String> {
}

package com.example.demo.repository;

import com.example.demo.entity.News;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
@Document
public interface NewsRepository extends MongoRepository<News,String> {
    Optional<News> findByTitle(String id);
}

package com.reactivespring.repository;

import com.reactivespring.domain.Review;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.CrudRepository;

public interface ReviewReactiveRepository extends ReactiveMongoRepository<Review, String> {

}

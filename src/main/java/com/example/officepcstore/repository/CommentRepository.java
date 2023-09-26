package com.example.officepcstore.repository;


import com.example.officepcstore.models.enity.Comment;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository  extends MongoRepository<Comment, String> {
    Page<Comment>findAllByProduct_IdAndState(ObjectId id, String state, Pageable pageable);
}

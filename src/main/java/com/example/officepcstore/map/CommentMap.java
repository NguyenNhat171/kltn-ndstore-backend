package com.example.officepcstore.map;

import com.example.officepcstore.models.enity.Comment;
import com.example.officepcstore.payload.response.CommentResponse;
import org.springframework.stereotype.Service;

@Service
public class CommentMap {
    public CommentResponse getCommentResponse(Comment comment)
    {
        return
                new CommentResponse(comment.getId(), comment.getReview(), comment.getRateProductQuality(),
                        comment.getState(), comment.getUser().getId(),comment.getUser().getName(),comment.getProduct().getId(),comment.getProduct().getName() ,comment.getCommentCreateDate());
    }

}

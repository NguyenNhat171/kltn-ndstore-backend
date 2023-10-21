package com.example.officepcstore.map;


import com.example.officepcstore.models.enity.CommentProduct;
import com.example.officepcstore.payload.response.CommentResponse;
import org.springframework.stereotype.Service;

@Service
public class CommentMap {
    public CommentResponse getCommentResponse(CommentProduct comment)
    {
        return
                new CommentResponse(comment.getId(), comment.getReview(), comment.getVoteProduct(), comment.getUserComment().getId(),comment.getUserComment().getName(),comment.getProductComment().getId(),comment.getProductComment().getName() ,comment.getCommentDate());
    }

}

package com.back_end.forum.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private  Long commentId;
    private String content;
    private LocalDateTime createdAt;
    private String username;
    private Long parentId;
    private Integer rating;
    private int replyCount;
    private List<CommentResponse> replies;
    private List<AttachmentResponse> attachments;

//    public List<CommentResponse> getReplies() {
//        return replies;
//    }
//
//    public void setReplies(List<CommentResponse> replies) {
//        this.replies = replies;
//    }
}

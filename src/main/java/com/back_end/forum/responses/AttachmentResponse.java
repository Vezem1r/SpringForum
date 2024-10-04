package com.back_end.forum.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttachmentResponse {
    private Long attachmentId;
    private String filename;
    private String downloadUrl;
}

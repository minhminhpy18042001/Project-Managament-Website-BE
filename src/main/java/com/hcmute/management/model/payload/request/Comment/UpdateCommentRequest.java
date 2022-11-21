package com.hcmute.management.model.payload.request.Comment;

import lombok.*;

import javax.validation.constraints.NotEmpty;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateCommentRequest {
    @NotEmpty(message = "id không được để trống")
    private String id;
    @NotEmpty(message = "Nội dung không được để trống")
    private String message;
}

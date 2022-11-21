package com.hcmute.management.model.payload.request.Authenticate;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RefreshTokenRequest {
    @NotEmpty
    String refreshToken;
}

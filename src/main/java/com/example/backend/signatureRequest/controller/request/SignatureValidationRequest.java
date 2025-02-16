package com.example.backend.signatureRequest.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignatureValidationRequest {
    private String token;  // 서명 요청 토큰 (UUID)
    private String email;  // 사용자가 입력한 이메일
}

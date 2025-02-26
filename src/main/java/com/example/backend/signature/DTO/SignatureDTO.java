package com.example.backend.signature.DTO;

import com.example.backend.signature.entity.Signature;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignatureDTO {
    private String signerEmail;  // 서명자의 이메일
    private Integer type;  // 서명 타입 (예: 0=서명, 1=텍스트)
    private float width;  // 서명 박스 너비
    private float height; // 서명 박스 높이
    private SignaturePositionDTO position;  // 서명 위치
    private String imageName;
    private String textData;

    // 🔹 `Signature` 엔티티를 `SignatureDTO`로 변환하는 정적 메서드
    public static SignatureDTO fromEntity(Signature signature) {
        return new SignatureDTO(
                signature.getSignerEmail(),
                signature.getType(),
                signature.getWidth(),
                signature.getHeight(),
                new SignaturePositionDTO(
                        signature.getPageNumber(),
                        signature.getX(),
                        signature.getY()
                ),
                signature.getImageName(),
                signature.getTextData()
        );
    }
}


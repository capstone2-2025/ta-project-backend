package com.example.backend.mail.service;

import com.example.backend.document.entity.Document;
import com.example.backend.signatureRequest.entity.SignatureRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${custom.host.client}")
    private String client;
    @Value("${spring.mail.username}")
    private String emailAdress;
    private final JavaMailSender mailSender;


    public void sendSignatureRequestEmails(String senderName, String requestName ,List<SignatureRequest> requests) {
        for (SignatureRequest request : requests) {
            String recipientEmail = request.getSignerEmail();
            String token = request.getToken();
            String documentName = request.getDocument().getFileName();
            String description = request.getDocument().getDescription();
            String signatureUrl =  client + "/sign?token=" + token;

            sendEmail(requestName, senderName ,recipientEmail, documentName, description, signatureUrl);
        }
    }

    public void sendEmail(String requestName, String from, String to, String documentName, String description, String signatureUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 이메일 설정
            helper.setTo(to);
            helper.setSubject("[서명 요청] " + requestName);
            helper.setFrom(emailAdress);

            // 이메일 내용 (HTML) - 작업 설명 추가
            String emailContent = "<div style='background-color:#f4f8fb; padding:30px; font-family:Arial, sans-serif;'>"
                    + "<div style='max-width:600px; background-color:#ffffff; border-radius:10px; padding:20px; margin:auto; box-shadow:0px 4px 10px rgba(0,0,0,0.1);'>"
                    + "<h2 style='color:#0366d6; text-align:center;'>HISign 전자 서명 요청</h2>"
                    + "<p style='font-size:16px; color:#333;'>안녕하세요, 사랑 · 겸손 · 봉사 정신의 한동대학교 전자 서명 서비스, <b>HISign</b>입니다.</p>"
                    + "<p style='font-size:16px; color:#333;'><b>" + from + "</b>님으로부터 <b>'" + documentName + "'</b> 문서의 서명 요청이 도착하였습니다.</p>"
                    + "<p style='font-size:16px; color:#333;'>아래 링크를 클릭하여 서명을 진행해 주세요:</p>"

                    // ✅ 작업 설명 추가 (컨테이너 안에 강조)
                    + "<div style='background-color:#eef6ff; padding:15px; border-radius:5px; border-left:5px solid #0366d6; margin:15px 0; '>"
                    + "<p style='font-size:16px; font-weight:bold; color:#0366d6; margin:0;'>📌 요청사항:</p>"
                    + "<table role='presentation' width='100%' cellspacing='0' cellpadding='0' border='0'>"
                    + "    <tr>"
                    + "        <td align='center' style='padding:10px;'>"
                    + "            <p style='font-size:16px; color:#333; text-align:center; font-style:italic; font-weight:bold; padding:10px; display:inline-block; margin:0;'>"
                    + "                \"" + description + "\""
                    + "            </p>"
                    + "        </td>"
                    + "    </tr>"
                    + "</table>"
                    + "</div>"

                    + "<div style='text-align:center; margin:20px 0;'>"
                    + "<a href='" + signatureUrl + "' style='background-color:#0366d6; color:#ffffff; text-decoration:none; padding:12px 20px; border-radius:5px; font-size:18px; display:inline-block;'>서명하기</a>"
                    + "</div>"
                    + "<p style='font-size:14px; color:#666; text-align:center;'>※ 본 메일은 자동 발송되었으며, 회신이 불가능합니다.</p>"
                    + "</div>"
                    + "</div>";

            helper.setText(emailContent, true); // HTML 템플릿 적용
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패: " + e.getMessage(), e);
        }
    }


    public void sendCompletedSignatureMail(String recipientEmail, Document document, byte[] pdfData) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("[전자 서명 완료] " + document.getRequestName() );
            helper.setFrom(emailAdress);

            String emailContent = "<div style='background-color:#f4f8fb; padding:30px; font-family:Arial, sans-serif;'>"
                    + "<div style='max-width:600px; background-color:#ffffff; border-radius:10px; padding:20px; margin:auto; box-shadow:0px 4px 10px rgba(0,0,0,0.1);'>"
                    + "<h2 style='color:#0366d6; text-align:center;'>HISign 서명 완료 안내</h2>"
                    + "<p style='font-size:16px; color:#333;'>안녕하세요, 사랑 · 겸손 · 봉사 정신의 한동대학교 전자 서명 서비스, <b>HISign</b>입니다.</p>"
                    + "<p style='font-size:16px; color:#333;'><b>" + document.getMember().getName() + "</b>님이 요청한 <b>'" + document.getFileName() + "'</b> 문서의 서명이 <b>완료<b>되었습니다.</p>"
                    + "<p style='font-size:16px; color:#333;'>완료된 서명 문서가 첨부되어 있으니 확인해 주세요.</p>"
                    + "<p style='font-size:14px; color:#666; text-align:center;'>※ 본 메일은 자동 발송되었으며, 회신이 불가능합니다.</p>"
                    + "</div></div>";

            helper.setText(emailContent, true);
            helper.addAttachment(document.getFileName() + "_signed.pdf", new ByteArrayResource(pdfData));

            mailSender.send(message);
        } catch (MailSendException e) {
            System.err.println("❌ 이메일 전송 실패 (SMTP 문제): " + e.getMessage());
            throw new RuntimeException("이메일 전송 중 SMTP 오류 발생", e);
        } catch (MessagingException e) {
            System.err.println("⚠️ 이메일 메시지 생성 실패: " + e.getMessage());
            throw new RuntimeException("이메일 메시지 생성 실패", e);
        }
    }


}

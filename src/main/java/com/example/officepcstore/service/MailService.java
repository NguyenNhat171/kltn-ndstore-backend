package com.example.officepcstore.service;

import com.example.officepcstore.models.enums.EnumMailType;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class MailService {
    private JavaMailSender mailSender;
    private Configuration configuration;
    final String TYPE_EMAIL = "text/html";
    final String TITLE_EMAIL_AUTH = "Mã xác minh tài khoản NDStore Website";
    final String TITLE_EMAIL_RESET = "Mã xác minh để đặt lại mật khẩu NDStore Website";
    final String TITLE_ORDER_SUCCESS ="Cảm ơn bạn đã mua hàng tại NDStore Website";
    final String VERIFY_MAIL = "register-email.ftl";
    final String RESET_MAIL = "reset-email.ftl";
    final String ORDER_SUCCESS_EMAIL ="order-success.ftl";
    final String STORE_EMAIL = "officecomputershop@gmail.com";
    public void sendEmail(String toEmail, Map<String,Object> model,
                          EnumMailType type) throws MessagingException, IOException, TemplateException {
        log.info(Thread.currentThread().getName()+ "- send email start");
        MimeMessage mimeMailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMailMessage);
        Template template =null;
        configuration.setClassForTemplateLoading(this.getClass(), "/templates");
        if (type.equals(EnumMailType.AUTH)) {
            template = configuration.getTemplate(VERIFY_MAIL);
            model.put("title", TITLE_EMAIL_AUTH);
        }
        else if (type.equals(EnumMailType.RESET)){
            template = configuration.getTemplate(RESET_MAIL);
            model.put("title", TITLE_EMAIL_RESET);
        }
        else if(type.equals(EnumMailType.ORDER_SUCCESS))
        {
            template = configuration.getTemplate(ORDER_SUCCESS_EMAIL);
            model.put("title", TITLE_ORDER_SUCCESS);
        }
        model.put("email", toEmail);
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(Objects.requireNonNull(template),model);
        mimeMailMessage.setContent(html, TYPE_EMAIL);
        helper.setFrom(STORE_EMAIL);
        helper.setTo(toEmail);
        helper.setText(html,true);
        helper.setSubject((String) model.get("title"));
        mailSender.send(mimeMailMessage);
        log.info(Thread.currentThread().getName()+ "- send email end");
    }
}

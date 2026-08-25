//package io.github.arubaid.config;
//
//import io.github.arubaid.Application;
//import io.github.arubaid.common.notification.LoggingMailSender;
//import io.github.arubaid.common.notification.MailSender;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Import;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest(
//        classes = Application.class,
//        properties = {
//                "app.mail.mode=log",
//                "app.mail.from=no-reply@example.com"
//        }
//)
//@Import(MailConfigLogModeTest.TestMailConfig.class)
//class MailConfigLogModeTest {
//
//    @Autowired
//    private MailSender mailSender;
//
//    @MockitoBean
//    private JavaMailSender javaMailSender;
//
//    @Test
//    void shouldUseLoggingMailSenderWhenConfigured() {
//        assertThat(mailSender)
//                .isInstanceOf(LoggingMailSender.class);
//    }
//
//    @TestConfiguration
//    static class TestMailConfig {
//
//        @Bean
//        JavaMailSender javaMailSender() {
//            return org.mockito.Mockito.mock(
//                    JavaMailSender.class
//            );
//        }
//    }
//}
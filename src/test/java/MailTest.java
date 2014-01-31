import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

/**
 * ClassName:MailTest Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2014-3-13
 */
public class MailTest {
	public static void main(String[] args) throws MessagingException {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.163.com");
		mailSender.setUsername("ecboss_800best@163.com");
		mailSender.setPassword("ecboss");

		final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom("ecboss_800best@163.com");

		final InternetAddress[] tos = InternetAddress.parse("xma@800best.com,katrina1218@qq.com,wenchaokai@vip.qq.com");

		mailSender.send(new MimeMessagePreparator() {
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				messageHelper.setFrom(simpleMailMessage.getFrom());
				messageHelper.setTo(tos);

				messageHelper.setSubject("亲！！邮件来咯");

				messageHelper.setText("邮件哟！！！收到告诉我哟", true);
				messageHelper.setSentDate(new Date(System.currentTimeMillis()));
			}
		});
	}
}

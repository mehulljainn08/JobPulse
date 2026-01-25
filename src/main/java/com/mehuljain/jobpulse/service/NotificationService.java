package com.mehuljain.jobpulse.service;

import com.mehuljain.jobpulse.entity.Job;
import com.mehuljain.jobpulse.entity.User;
import com.mehuljain.jobpulse.event.JobSavedEvent;
import com.mehuljain.jobpulse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String senderEmail;


    private final List<Job> jobBuffer = new CopyOnWriteArrayList<>();


    @EventListener
    public void handleNewJob(JobSavedEvent event) {
        jobBuffer.add(event.getJob());
        log.info("Buffered job: {}. Total pending: {}", event.getJob().getJobTitle(), jobBuffer.size());
    }


    @Scheduled(fixedRate = 600000)
    public void sendBufferedJobs() {
        if (jobBuffer.isEmpty()) {
            return;
        }

        int jobCount = jobBuffer.size();
        log.info("Creating digest email for {} jobs...", jobCount);

        List<User> users = userRepository.findAll();

        // Build the email body ONCE
        String emailBody = buildHtmlEmail(jobBuffer);

        for (User user : users) {
            try {
                sendHtmlEmail(user, emailBody, jobCount);
            } catch (Exception e) {
                log.error("Failed to send digest to {}", user.getEmail());
            }
        }


        jobBuffer.clear();
        log.info("Buffer cleared.");
    }

    private void sendHtmlEmail(User user, String htmlBody, int count) throws Exception {
        if (user.getEmail() == null || user.getEmail().isBlank()) return;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(senderEmail);
        helper.setTo(user.getEmail());
        helper.setSubject("🔥 JobPulse Digest: " + count + " New Jobs Found!");
        helper.setText(htmlBody, true);

        mailSender.send(message);
        log.info("Digest sent to {}", user.getEmail());
    }

    private String buildHtmlEmail(List<Job> jobs) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h2>🚀 We found ").append(jobs.size()).append(" new jobs!</h2>");
        html.append("<table style='border-collapse: collapse; width: 100%;'>");
        html.append("<tr style='background-color: #f2f2f2;'><th style='padding: 8px; border: 1px solid #ddd;'>Role</th><th style='padding: 8px; border: 1px solid #ddd;'>Company</th><th style='padding: 8px; border: 1px solid #ddd;'>Link</th></tr>");

        for (Job job : jobs) {
            html.append("<tr>");
            html.append("<td style='padding: 8px; border: 1px solid #ddd;'>").append(job.getJobTitle()).append("</td>");
            html.append("<td style='padding: 8px; border: 1px solid #ddd;'>").append(job.getCompanyName()).append("</td>");
            html.append("<td style='padding: 8px; border: 1px solid #ddd;'><a href='").append(job.getApplyUrl()).append("'>Apply</a></td>");
            html.append("</tr>");
        }

        html.append("</table></body></html>");
        return html.toString();
    }
}
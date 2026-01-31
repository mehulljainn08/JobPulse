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

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String senderEmail;


    private final List<Job> jobBuffer = new ArrayList<>();

    @EventListener
    public void handleNewJob(JobSavedEvent event) {

        synchronized (jobBuffer) {
            jobBuffer.add(event.getJob());
            log.info("Buffered job: {}. Total pending: {}", event.getJob().getJobTitle(), jobBuffer.size());
        }
    }

    @Scheduled(fixedRate = 600000)
    public void sendBufferedJobs() {


        if (jobBuffer.isEmpty()) {
            return;
        }

        List<Job> batchToSend;


        synchronized (jobBuffer) {
            if (jobBuffer.isEmpty()) {
                return;
            }
            batchToSend = new ArrayList<>(jobBuffer);
            jobBuffer.clear();
        }


        int jobCount = batchToSend.size();
        log.info("Creating digest email for {} jobs...", jobCount);

        List<User> users = userRepository.findAll();
        String emailBody = buildHtmlEmail(batchToSend);

        for (User user : users) {
            try {
                sendHtmlEmail(user, emailBody, jobCount);
            } catch (Exception e) {
                log.error("Failed to send digest to {}", user.getEmail());
            }
        }
        log.info("Batch sent successfully.");
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
        html.append("<html><body style='font-family: Arial, sans-serif;'>");
        html.append("<h2>🚀 We found ").append(jobs.size()).append(" new jobs!</h2>");

        html.append("<table style='border-collapse: collapse; width: 100%; border: 1px solid #ddd;'>");
        html.append("<tr style='background-color: #f2f2f2;'>");
        html.append("<th style='padding: 10px; border: 1px solid #ddd; width: 25%;'>Role & Company</th>"); // Combined col
        html.append("<th style='padding: 10px; border: 1px solid #ddd; width: 60%;'>AI Insights</th>");
        html.append("<th style='padding: 10px; border: 1px solid #ddd; width: 15%;'>Action</th>");
        html.append("</tr>");

        for (Job job : jobs) {
            html.append("<tr>");

            // Col 1: Role & Company
            html.append("<td style='padding: 10px; border: 1px solid #ddd;'>")
                    .append("<div style='font-size:16px; font-weight:bold;'>").append(job.getJobTitle()).append("</div>")
                    .append("<div style='color:#555;'>").append(job.getCompanyName()).append("</div>")
                    .append("<div style='font-size:12px; color:#777;'>").append(job.getLocation()).append("</div>")
                    .append("</td>");


            String summary = (job.getAiSummary() != null) ? job.getAiSummary() : "Analysis Pending...";


            StringBuilder badges = new StringBuilder();
            if (job.getTechStack() != null) {
                for (String tech : job.getTechStack()) {
                    badges.append("<span style='background:#e1ecf4; color:#0052cc; padding:2px 8px; border-radius:12px; font-size:11px; margin-right:5px; display:inline-block; margin-bottom:3px;'>")
                            .append(tech)
                            .append("</span>");
                }
            }

            html.append("<td style='padding: 10px; border: 1px solid #ddd;'>")
                    .append("<div style='margin-bottom:8px;'>").append(summary).append("</div>")
                    .append("<div>").append(badges).append("</div>") // Badges here!
                    .append("</td>");

            // Col 3: Button
            html.append("<td style='padding: 10px; border: 1px solid #ddd; text-align: center;'>");
            html.append("<a href='").append(job.getApplyUrl()).append("' style='background-color: #007bff; color: white; padding: 8px 15px; text-decoration: none; border-radius: 5px; font-weight:bold;'>Apply</a>");
            html.append("</td>");

            html.append("</tr>");
        }

        html.append("</table>");
        html.append("<p style='font-size: 12px; color: #666; margin-top:20px;'>JobPulse AI Agent • Automated Digest</p>");
        html.append("</body></html>");
        return html.toString();
    }

}
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String senderEmail;

    // 🛑 CHANGE: Use BlockingQueue instead of List
    // This is designed exactly for "buffering" tasks safely.
    private final BlockingQueue<Job> jobBuffer = new LinkedBlockingQueue<>();

    @EventListener
    public void handleNewJob(JobSavedEvent event) {
        jobBuffer.add(event.getJob());
        log.info("Buffered job: {}. Total pending: {}", event.getJob().getJobTitle(), jobBuffer.size());
    }

    @Scheduled(fixedRate = 600000) // Runs every 10 minutes
    public void sendBufferedJobs() {
        if (jobBuffer.isEmpty()) {
            return;
        }


        // It clears the buffer as it moves them, leaving zero gaps for data loss.
        List<Job> batchToSend = new ArrayList<>();
        jobBuffer.drainTo(batchToSend);

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
        html.append("<th style='padding: 10px; border: 1px solid #ddd; width: 20%;'>Role</th>");
        html.append("<th style='padding: 10px; border: 1px solid #ddd; width: 15%;'>Company</th>");
        html.append("<th style='padding: 10px; border: 1px solid #ddd; width: 50%;'>AI Summary</th>");
        html.append("<th style='padding: 10px; border: 1px solid #ddd; width: 15%;'>Link</th>");
        html.append("</tr>");

        for (Job job : jobs) {
            html.append("<tr>");
            html.append("<td style='padding: 10px; border: 1px solid #ddd;'><b>").append(job.getJobTitle()).append("</b></td>");
            html.append("<td style='padding: 10px; border: 1px solid #ddd;'>").append(job.getCompanyName()).append("</td>");

            String summary = (job.getAiSummary() != null && !job.getAiSummary().isEmpty())
                    ? job.getAiSummary()
                    : "<i>Analysis Pending...</i>";

            html.append("<td style='padding: 10px; border: 1px solid #ddd; font-size: 14px;'>").append(summary).append("</td>");
            html.append("<td style='padding: 10px; border: 1px solid #ddd; text-align: center;'><a href='").append(job.getApplyUrl()).append("' style='background-color: #28a745; color: white; padding: 5px 10px; text-decoration: none; border-radius: 4px;'>Apply</a></td>");
            html.append("</tr>");
        }

        html.append("</table>");
        html.append("<p style='font-size: 12px; color: #666;'>JobPulse AI Agent • Automated Digest</p>");
        html.append("</body></html>");
        return html.toString();
    }
}
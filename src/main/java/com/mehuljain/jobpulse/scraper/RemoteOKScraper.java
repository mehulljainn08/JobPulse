package com.mehuljain.jobpulse.scraper;

import com.mehuljain.jobpulse.entity.Job;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class RemoteOKScraper implements JobScraper {

    // RSS Feed is much more reliable than HTML scraping
    private final String url = "https://remoteok.com/rss";

    @Override
    public List<Job> getJobs() {
        List<Job> jobList = new ArrayList<>();

        try {
            // 1. Fetch the RSS Feed (XML)
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
                    .parser(Parser.xmlParser()) // IMPORTANT: Use XML Parser
                    .timeout(10000)
                    .get();


            Elements items = doc.select("item");

            for (Element item : items) {
                // 3. Extract Data from XML tags
                String title = item.select("title").text();
                String link = item.select("link").text();
                String description = item.select("description").text();
                String company = item.select("company").text();
                System.out.println("Scraped RemoteOK Job: " + title);



                Job job = new Job();
                job.setJobTitle(title);
                job.setCompanyName(company);
                job.setLocation("Remote");
                job.setApplyUrl(link);
                job.setJobDescription(description);
                job.setSource("RemoteOK RSS");
                job.setActive(true);

                jobList.add(job);
            }
        } catch (IOException e) {
            System.err.println("Error scraping RemoteOK RSS: " + e.getMessage());
        }
        return jobList;
    }


}
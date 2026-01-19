package com.mehuljain.jobpulse.event;

import com.mehuljain.jobpulse.entity.Job;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class JobSavedEvent extends ApplicationEvent {

    private final Job job;

    public JobSavedEvent(Job job) {
        super(job);
        this.job=job;
    }
}

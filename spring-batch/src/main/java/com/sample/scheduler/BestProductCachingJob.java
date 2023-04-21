package com.sample.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class BestProductCachingJob implements Job {

//	private final ProductRepository productRepository;

	private final JobLauncher jobLauncher;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

	}
}

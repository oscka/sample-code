package com.sample.config;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Objects;

import com.sample.scheduler.BestProductCachingJob;

@EntityScan({"com.sample.domain"})
@ComponentScan(basePackages = {"com.sample.config.redis"})
@EnableJpaRepositories(basePackages = {"com.sample.domain.product"})
@RequiredArgsConstructor
@Configuration
public class QuartzConfig {


	// Job
	@Bean
	public JobDetailFactoryBean jobDetailFactoryBean() {
		JobDetailFactoryBean factory = new JobDetailFactoryBean();
		factory.setJobClass(BestProductCachingJob.class);
		return factory;
	}

	// cron trigger
	@Bean
	public CronTriggerFactoryBean cronTriggerFactoryBean(JobDetailFactoryBean jobDetailFactoryBean) {
		CronTriggerFactoryBean factory = new CronTriggerFactoryBean();
		factory.setJobDetail(Objects.requireNonNull(jobDetailFactoryBean.getObject()));
		factory.setCronExpression("0 0/1 * * * ?");        // 1분 마다 한번씩 실행
		return factory;
	}

	// scheduler
	@Bean
	public SchedulerFactoryBean schedulerFactoryBean(CronTriggerFactoryBean cronTriggerFactoryBean) {
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		factory.setTriggers(cronTriggerFactoryBean.getObject());
		factory.setApplicationContextSchedulerContextKey("applicationContext");
		return factory;
	}

}


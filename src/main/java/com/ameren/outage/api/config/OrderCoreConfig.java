package com.ameren.outage.api.config;

import java.util.Map;
import java.util.Set;

import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.MultiValueMap;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;

public abstract class OrderCoreConfig {
	private static final int DEFAULT_SQS_POOL_CORE_SIZE = 11;
	private static final String DEFAULT_SQS_THREAD_NAME_PREFIX = "SimpleMessageListenerContainer-";		
    protected static final String PRE_POST_FIX = "======================";
    
    protected Set<String> voiceOperatingCenters;
    protected String voiceOperatingCentersUrl;
    protected String customerByPremiseUrl;
    protected String customerByTransformerUrl;
    protected String callLogServiceUrl;
    protected int maxNoOfTransformers;
    protected Map<String, String> headerMap;
    protected String restClientId;
    protected String restClientSecret;
    protected int sqsConsumerCorePoolSize;


    public abstract String getVoiceOperatingCenterUrl();
    public abstract String getCustomerByPremiseUrl();
    public abstract String getCustomerByTransformerUrl();
    public abstract int getMaxNoOfTransformers();
    public abstract void setVoiceOperatingCenters(Set<String> voicePpoEligibleOperatingCenters);
    public abstract Set<String> getVoiceOperatingCenters();
    public abstract MultiValueMap<String, String> getHeaderMap();
    public abstract String getCallLogServiceUrl();
    public abstract int getSQSConsumerCorePoolSize();

    @Bean
	public AsyncTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setThreadNamePrefix(DEFAULT_SQS_THREAD_NAME_PREFIX);
		threadPoolTaskExecutor.setCorePoolSize(getSQSConsumerCorePoolSize() <= 0? DEFAULT_SQS_POOL_CORE_SIZE: getSQSConsumerCorePoolSize());

		// No use of a thread pool executor queue to avoid retaining message too long in memory
		threadPoolTaskExecutor.setQueueCapacity(0);
		threadPoolTaskExecutor.afterPropertiesSet();
		return threadPoolTaskExecutor;
	}

	@Bean("amazonSqsAsync")
	@Profile("test")
	public AmazonSQSAsync amazonSqsAsync() {
		return AmazonSQSAsyncClientBuilder.defaultClient();
	}
	
	@Bean
	public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(
			AmazonSQSAsync amazonSQS, AsyncTaskExecutor taskExecutor) {
		SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
		factory.setAmazonSqs(amazonSQS);
		factory.setTaskExecutor(taskExecutor);
		factory.setMaxNumberOfMessages(10);
		return factory;
	}  
    
}

package com.in28minutes.microservices.currencyexchangeservicev2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
public class CircuitBreakerController {

	private Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);

	@GetMapping("/sample-api-dummy-url")
	//@Retry(name="default")
	@Retry(name="sample-api-dummy-url", fallbackMethod = "dummyUrlhardcodeResponse")
	public String sampleApiDummyUrl() {
		logger.info("Sample api call dummy-url recived ");
		ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url",
				String.class);
		return forEntity.getBody();
	}
	
	public String dummyUrlhardcodeResponse(Exception ex) {
		return "sample-api-dummy-url-fallback-response";
	}
	
	@GetMapping("/sample-api-dummy-url-cb")
	@CircuitBreaker(name = "default", fallbackMethod = "dummyUrlhardcodeCbResponse")
	public String sampleApiDummyUrlCb() {
		logger.info("Sample api dummy-url circuit breaker call recived ");
		ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url",
				String.class);
		return forEntity.getBody();
	}
	
	public String dummyUrlhardcodeCbResponse(Exception ex) {
		logger.info("dummyUrlhardcodeCbResponse call received ");
		return "sample-api-dummy-url-cb-fallback-response";
	}

	@GetMapping("/sample-api")
	//@CircuitBreaker(name = "default", fallbackMethod = "hardcodedResponse")
	//@RateLimiter(name="default")
	// 10s => 1000 calls to the sample-api
	@Bulkhead(name = "sample-api")
	public String sampleApi() {
		logger.info("Sample api call recived ");
		return "sample-api";
	}

	public String hardcodedResponse(Exception ex) {
		return "sample-api-fallback-response";
	}

}

package com.book.info.service;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class HealthController {

  @GetMapping("/health")
  public Mono<Map<String, String>> health() {
    var healthStatus = Map.of("status", "UP");
    return Mono.just(healthStatus);
  }
}

package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InfoService {
    private final Logger logger = LoggerFactory.getLogger(InfoService.class);
    @Value("${server.port}")
    private int port;

    public String getPort() {
        logger.debug("Server port request");
        return "Server port = " + port;
    }
}

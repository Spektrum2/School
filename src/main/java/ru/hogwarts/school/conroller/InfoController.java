package ru.hogwarts.school.conroller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.service.InfoService;

@RestController
public class InfoController {

    private final InfoService infoService;

    public InfoController(InfoService infoService) {
        this.infoService = infoService;
    }

    @GetMapping("getPort")
    public String getPort() {
        return infoService.getPort();
    }

    @GetMapping("getNumber")

    public Integer getNumber() {
        return infoService.getNumber();
    }
}

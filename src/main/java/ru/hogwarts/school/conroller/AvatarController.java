package ru.hogwarts.school.conroller;

import org.springframework.data.util.*;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.record.AvatarRecord;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("avatar")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadAvatar(@RequestParam MultipartFile avatar) throws IOException {
        avatarService.uploadAvatar(avatar);
    }

    @GetMapping("/{id}/from-db")
    public ResponseEntity<byte[]> readAvatarFromDb(@PathVariable long id) {
        Pair<String, byte[]> pair = avatarService.readAvatarFromDb(id);
        return  ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(pair.getFirst()))
                .contentLength(pair.getSecond().length)
                .body(pair.getSecond());
    }

    @GetMapping("/{id}/from-fs")
    public ResponseEntity<byte[]> readAvatarFromFs(@PathVariable long id) throws IOException {
        Pair<String, byte[]> pair = avatarService.readAvatarFromFs(id);
        return  ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(pair.getFirst()))
                .contentLength(pair.getSecond().length)
                .body(pair.getSecond());
    }

    @GetMapping
    public Collection<AvatarRecord> readAllAvatar(@RequestParam("page") Integer pageNumber,
                                                  @RequestParam("size") Integer pageSize){
        return avatarService.readAllAvatar(pageNumber, pageSize);
    }
}

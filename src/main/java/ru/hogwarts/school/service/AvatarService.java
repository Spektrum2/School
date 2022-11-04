package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.component.RecordMapper;
import ru.hogwarts.school.exception.AvatarNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.record.AvatarRecord;
import ru.hogwarts.school.repository.AvatarRepository;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AvatarService {
    private final Logger logger = LoggerFactory.getLogger(AvatarService.class);
    @Value("${path.to.avatars.folder}")
    private String avatarsDir;

    private final RecordMapper recordMapper;
    private final AvatarRepository avatarRepository;

    public AvatarService(RecordMapper recordMapper, AvatarRepository avatarRepository) {
        this.recordMapper = recordMapper;
        this.avatarRepository = avatarRepository;
    }

    public void uploadAvatar(MultipartFile multipartFile) throws IOException {
        logger.info("Was invoked method for upload avatar");
        byte[] data = multipartFile.getBytes();
        Avatar avatar = create(multipartFile.getSize(), multipartFile.getContentType(), data);

        String extension = Optional.ofNullable(multipartFile.getOriginalFilename())
                .map(s -> s.substring(multipartFile.getOriginalFilename().lastIndexOf(".")))
                .orElse("");
        Path path = Paths.get(avatarsDir).resolve(avatar.getId() + extension);
        Files.createDirectories(path.getParent());
        Files.deleteIfExists(path);
        Files.write(path, data);
        avatar.setFilePath(path.toString());
        avatarRepository.save(avatar);
    }

    private Avatar create(long size, String contentType, byte[] data) {
        logger.info("Was invoked method for create avatar");
        Avatar avatar = new Avatar();
        avatar.setData(data);
        avatar.setFileSize(size);
        avatar.setMediaType(contentType);
        return avatarRepository.save(avatar);
    }

    public Pair<String, byte[]> readAvatarFromDb(long id) {
        logger.info("Was invoked method for read avatar from db");
        Avatar avatar = avatarRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not avatar with id = {}", id);
                    return new AvatarNotFoundException(id);
                });
        return Pair.of(avatar.getMediaType(), avatar.getData());
    }

    public Pair<String, byte[]> readAvatarFromFs(long id) throws IOException {
        logger.info("Was invoked method for read avatar from fs");
        Avatar avatar = avatarRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not avatar with id = {}", id);
                    return new AvatarNotFoundException(id);
                });
        return Pair.of(avatar.getMediaType(), Files.readAllBytes(Paths.get(avatar.getFilePath())));
    }

    public Collection<AvatarRecord> readAllAvatar(int pageNumber, int pageSize) {
        logger.info("Was invoked method for get all avatars page by page");
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return avatarRepository.findAll(pageRequest).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }
}

package ru.hogwarts.school.service;

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
    @Value("${path.to.avatars.folder}")
    private String avatarsDir;

    private RecordMapper recordMapper;
    private final AvatarRepository avatarRepository;

    public AvatarService(RecordMapper recordMapper, AvatarRepository avatarRepository) {
        this.recordMapper = recordMapper;
        this.avatarRepository = avatarRepository;
    }

    public void uploadAvatar(MultipartFile multipartFile) throws IOException {
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
        Avatar avatar = new Avatar();
        avatar.setData(data);
        avatar.setFileSize(size);
        avatar.setMediaType(contentType);
        return avatarRepository.save(avatar);
    }

    public Pair<String, byte[]> readAvatarFromDb(long id) {
        Avatar avatar = avatarRepository.findById(id).orElseThrow(() -> new AvatarNotFoundException(id));
        return Pair.of(avatar.getMediaType(), avatar.getData());
    }

    public Pair<String, byte[]> readAvatarFromFs(long id) throws IOException {
        Avatar avatar = avatarRepository.findById(id).orElseThrow(() -> new AvatarNotFoundException(id));
        return Pair.of(avatar.getMediaType(), Files.readAllBytes(Paths.get(avatar.getFilePath())));
    }

    public Collection<AvatarRecord> readAllAvatar(int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return avatarRepository.findAll(pageRequest).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }
}

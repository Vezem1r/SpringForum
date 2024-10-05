package com.back_end.forum.service;

import com.back_end.forum.model.Banner;
import com.back_end.forum.repository.BannerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BannerService {

    private BannerRepository bannerRepository;

    private static final String BANNER_DIR = "src/main/resources/static/banners";
    public Banner saveBanner(MultipartFile bannerFile) throws IOException {
        String originalFilename = bannerFile.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path uploadPath = Paths.get(BANNER_DIR, uniqueFilename);
        Files.write(uploadPath, bannerFile.getBytes());
        Banner banner = new Banner();
        banner.setFilename(originalFilename);
        banner.setSize(bannerFile.getSize());
        banner.setContentType(bannerFile.getContentType());
        banner.setFilePath(uploadPath.toString());
        banner.setCreatedAt(LocalDateTime.now());
        return bannerRepository.save(banner);
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}

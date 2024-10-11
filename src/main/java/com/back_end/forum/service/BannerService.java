package com.back_end.forum.service;

import com.back_end.forum.model.Banner;
import com.back_end.forum.repository.BannerRepository;
import com.back_end.forum.utils.FolderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class BannerService {

    private final BannerRepository bannerRepository;

    private static final String BANNER_DIR = "static/banners";

    public BannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;

        FolderUtils.createDirectories(BANNER_DIR);
    }

    public Banner saveBanner(MultipartFile bannerFile) throws IOException {
        String originalFilename = bannerFile.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;
        Path uploadPath = Paths.get(BANNER_DIR, uniqueFilename);

        log.info("Saving banner: {} to path: {}", originalFilename, uploadPath);

        Files.write(uploadPath, bannerFile.getBytes());

        Banner banner = new Banner();
        banner.setFilename(originalFilename);
        banner.setSize(bannerFile.getSize());
        banner.setContentType(bannerFile.getContentType());
        banner.setFilePath(uploadPath.toString());
        banner.setCreatedAt(LocalDateTime.now());

        log.info("Banner saved successfully: {}", banner);

        return bannerRepository.save(banner);
    }
}

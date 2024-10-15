package com.back_end.forum.controller.admin;

import com.back_end.forum.dto.AdminPageDto;
import com.back_end.forum.model.Tag;
import com.back_end.forum.service.TagService;
import com.back_end.forum.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final TagService tagService;
    private final AdminService adminService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<AdminPageDto> getAdminDashboard() {
        AdminPageDto adminPageDto = adminService.getAdminPage();
        return ResponseEntity.ok(adminPageDto);
    }

    @DeleteMapping("/tag/delete/{id}")
    public ResponseEntity<String> deleteTag(@PathVariable Long id) {
        Tag deletedTag = tagService.deleteTag(id);
        return ResponseEntity.ok("Tag has been removed");
    }
}

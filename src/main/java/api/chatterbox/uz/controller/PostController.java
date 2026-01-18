package api.chatterbox.uz.controller;

import api.chatterbox.uz.dto.AppResponse;
import api.chatterbox.uz.dto.post.*;
import api.chatterbox.uz.enums.GeneralStatus;
import api.chatterbox.uz.service.PostService;
import api.chatterbox.uz.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@Tag(name = "ProfileController", description = "API set for working with Post")
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping("")
    @Operation(summary = "Create Post", description = "Api used for post creation")
    public ResponseEntity<PostDTO> create(@Valid @RequestBody PostCreateDTO dto) {
        return ResponseEntity.ok(postService.create(dto));
    }

    @GetMapping("/profile")
    @Operation(summary = "Profile Post List", description = "Get all profile post list")
    public ResponseEntity<Page<PostDTO>> profilePostList(@RequestParam(value = "page", defaultValue = "1") int page,
                                                         @RequestParam(value = "size", defaultValue = "12") int size) {
        return ResponseEntity.ok(postService.getProfilePostList(PageUtil.page(page), size));
    }

    @GetMapping("/public/{id}")
    @Operation(summary = "Get post by id", description = "Api returns post by id")
    public ResponseEntity<PostDTO> byId(@PathVariable("id") String id) {
        return ResponseEntity.ok(postService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Post", description = "Api used for post update")
    public ResponseEntity<PostDTO> update(@PathVariable("id") String id,
                                          @Valid @RequestBody PostCreateDTO dto) {
        return ResponseEntity.ok(postService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete post by id", description = "Api used for deleting post")
    public ResponseEntity<AppResponse<String>> delete(@PathVariable("id") String id) {
        return ResponseEntity.ok(postService.delete(id));
    }

    @PostMapping("/public/filter")
    @Operation(summary = "Post public filter", description = "Api used for post filtering")
    public ResponseEntity<Page<PostDTO>> filter(@Valid @RequestBody PostFilterDTO dto,
                                                @RequestParam(value = "page", defaultValue = "1") int page,
                                                @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.filter(dto, PageUtil.page(page), size));
    }

    @PostMapping("/public/similar")
    @Operation(summary = "Get similar post list", description = "Api used for getting similar post list")
    public ResponseEntity<List<PostDTO>> similarPostList(@Valid @RequestBody SimilarPostListDTO dto) {
        return ResponseEntity.ok(postService.getSimilarPostList(dto));
    }

    @PostMapping("/filter")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // changed from hasRole to hasAuthority
    @Operation(summary = "Post filter for admin", description = "Api used for filtering post list. Api for admin")
    public ResponseEntity<Page<PostDTO>> filter(@RequestBody PostAdminFilterDTO dto,
                                                @RequestParam(value = "page", defaultValue = "1") int page,
                                                @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.adminFilter(dto, PageUtil.page(page), size));
    }

    @PutMapping("/change-status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeStatus(@PathVariable String id, @RequestParam GeneralStatus status) {
        String response = postService.changeStatus(id, status);
        return ResponseEntity.ok(response);
    }
}


package com.hcmute.management.controller;

import com.hcmute.management.handler.FileNotImageException;
import com.hcmute.management.handler.MethodArgumentNotValidException;
import com.hcmute.management.model.entity.ProgressEntity;
import com.hcmute.management.model.entity.StudentEntity;
import com.hcmute.management.model.entity.UserEntity;
import com.hcmute.management.model.payload.SuccessResponse;
import com.hcmute.management.model.payload.request.Progress.AddNewProgressRequest;
import com.hcmute.management.model.payload.request.Progress.UpdateProgressRequest;
import com.hcmute.management.model.payload.response.ErrorResponse;
import com.hcmute.management.security.JWT.JwtUtils;
import com.hcmute.management.service.AttachmentService;
import com.hcmute.management.service.ProgressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

@ComponentScan
@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;
    private final AttachmentService attachmentService;

    @Autowired
    JwtUtils jwtUtils;

    public static String E400 = "Bad request";
    public static String E404 = "Not found";
    public static String E401 = "Unauthorize";

    @PostMapping(value = "",consumes = {"multipart/form-data"})
    @ApiOperation("Create")
    @Transactional()
    public ResponseEntity<Object> addProgress(@Valid AddNewProgressRequest addNewProgressRequest, @RequestPart MultipartFile[] files, BindingResult errors, HttpServletRequest httpServletRequest) throws Exception {
        if (errors.hasErrors()) {
            throw new MethodArgumentNotValidException(errors);
        }
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        SuccessResponse response = new SuccessResponse();
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token is  expired");
            }
             else {
                 try {
                     ProgressEntity progress = progressService.saveProgress(addNewProgressRequest);
                     attachmentService.uploadFile(files, progress);
                     return new ResponseEntity<>(progress, HttpStatus.OK);
                 }catch (FileNotImageException fileNotImageException)
                 {
                     return new ResponseEntity<>(new ErrorResponse("Unsupported Media Type","FILE_NOT_IMAGE",fileNotImageException.getMessage()),HttpStatus.UNSUPPORTED_MEDIA_TYPE);
                 }
            }
        } else throw new BadCredentialsException("access token is missing");
    }

    @GetMapping("")
    @ApiOperation("Get all")
    public ResponseEntity<Object> getAllProgress() {
        List<ProgressEntity> listProgress = progressService.findAllProgress();

            Map<String, Object> map = new HashMap<>();
            map.put("Content", listProgress);
            return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ApiOperation("Get by id")
    public ResponseEntity<Object> getProgressById(@PathVariable("id") String id) {
        ProgressEntity progress = progressService.findById(id);
        if (progress == null) {
            return new ResponseEntity<>(new ErrorResponse(E404,"PROGRESS_NOT_FOUND", "Progress not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(progress, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ApiOperation("Update")
    @ResponseBody
    public ResponseEntity<Object> updateProgress(HttpServletRequest req, @RequestBody @Valid UpdateProgressRequest updateProgressRequest, @PathVariable("id") String id) {
        String authorizationHeader = req.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());
            if (jwtUtils.validateExpiredToken(accessToken)) {
                throw new BadCredentialsException("access token is expired");
            }
            ProgressEntity progress = new ProgressEntity();
            if (progressService.findById(id) != null) {
                progress = progressService.updateProgress(updateProgressRequest, id);
                return new ResponseEntity<>(progress, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ErrorResponse(E404, "PROGRESS_NOT_FOUND","Progress not found"), HttpStatus.NOT_FOUND);
            }
        }
        throw new BadCredentialsException("access token is missing");
    }

    @DeleteMapping("")
    @ApiOperation("Delete")
    public ResponseEntity<SuccessResponse> deleteProgress(@RequestBody List<String> listProgressId, HttpServletRequest httpServletRequest) {
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        SuccessResponse response = new SuccessResponse();
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());
            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token is  expired");
            }
            progressService.deleteById(listProgressId);
            return new ResponseEntity<>(HttpStatus.OK);
        } else throw new BadCredentialsException("access token is missing");
    }

}

package com.limnj.noticeboardadmin.file;

import com.limnj.noticeboardadmin.util.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service @Transactional
@RequiredArgsConstructor
public class FileInfoServiceImpl implements FileInfoService {

    private final FileInfoMapper fileInfoMapper;
    private final FileService fileService;

    @Override
    public int saveFile(FileRequestDto requestDto, MultipartFile multipartFile, String location) {
        String oriFileName = multipartFile.getOriginalFilename();
        String fileName = fileService.uploadFile(location, multipartFile);
        String fileUrl = "/files/" + requestDto.getFileType().toString().toLowerCase() + "/" + fileName;
        Long fileSize = multipartFile.getSize();

        requestDto.updateFileInfo(fileName, oriFileName, fileUrl, fileSize);
        return fileInfoMapper.saveFile(requestDto);
    }

    @Override
    public int deleteFile(Long fileId, String location, String fileName) {
        fileService.deleteFile(location, fileName); // 파일 삭제
        return fileInfoMapper.deleteFile(fileId);
    }

    @Override
    public Optional<FileResponseDto> findByTypeId(Long typeId, FileType fileType) {
        return Optional.ofNullable(fileInfoMapper.findByType(typeId, fileType));
    }
}

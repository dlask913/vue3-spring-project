package com.limnj.noticeboardadmin.notice;

import com.limnj.noticeboardadmin.file.FileInfoRequestDto;
import com.limnj.noticeboardadmin.file.FileInfoResponseDto;
import com.limnj.noticeboardadmin.file.FileInfoService;
import com.limnj.noticeboardadmin.file.FileType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService{

    private final NoticeMapper noticeMapper;
    private final FileInfoService fileInfoServiceImpl;
    @Value("${fileLocation}")
    private String fileLocation;

    @Override
    public int saveNotice(NoticeRequestDto noticeRequestDto, MultipartFile noticeFile) {
        noticeRequestDto.savePostType();
        int result = noticeMapper.insertNotice(noticeRequestDto);

        if(noticeFile != null){ // 첨부파일 있으면 저장
            FileInfoRequestDto fileInfoRequestDto = FileInfoRequestDto.builder()
                    .typeId(noticeRequestDto.getId())
                    .fileType(FileType.NOTICE)
                    .build();
            fileInfoServiceImpl.saveFile(fileInfoRequestDto, noticeFile, fileLocation);
        }
        return result;
    }

    @Override
    public int updateNotice(NoticeRequestDto noticeRequestDto, MultipartFile noticeFile) {
        fileInfoServiceImpl.deleteFileIfPresent(noticeRequestDto.getId(), fileLocation, FileType.NOTICE);

        if(noticeFile != null){ // 첨부파일 있으면 저장
            FileInfoRequestDto fileInfoRequestDto = FileInfoRequestDto.builder()
                    .typeId(noticeRequestDto.getId())
                    .fileType(FileType.NOTICE)
                    .build();
            fileInfoServiceImpl.saveFile(fileInfoRequestDto, noticeFile, fileLocation);
        }
        return noticeMapper.updateNotice(noticeRequestDto);
    }

    @Override
    public int deleteNotice(Long noticeId) {
        return noticeMapper.deleteNotice(noticeId);
    }

    @Override
    public NoticeResponseDto findNoticeByNoticeId(Long noticeId) {
        return noticeMapper.findNoticeById(noticeId);
    }

    @Override
    public List<NoticeResponseDto> findAllNotices() {
        return noticeMapper.findAllNotices();
    }
}

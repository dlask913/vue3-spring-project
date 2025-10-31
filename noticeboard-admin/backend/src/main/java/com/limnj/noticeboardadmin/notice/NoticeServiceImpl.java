package com.limnj.noticeboardadmin.notice;

import com.limnj.noticeboardadmin.file.FileInfoRequestDto;
import com.limnj.noticeboardadmin.file.FileInfoService;
import com.limnj.noticeboardadmin.file.FileType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

        if(noticeFile != null){
            FileInfoRequestDto fileInfoRequestDto = FileInfoRequestDto.builder()
                    .typeId(noticeRequestDto.getId())
                    .fileType(FileType.NOTICE)
                    .build();
            fileInfoServiceImpl.saveFile(fileInfoRequestDto, noticeFile, fileLocation);
        }
        return result;
    }

    @Override
    public int updateNotice(NoticeRequestDto noticeRequestDto) {
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

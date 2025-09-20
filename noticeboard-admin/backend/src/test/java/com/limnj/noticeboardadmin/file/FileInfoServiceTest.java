package com.limnj.noticeboardadmin.file;

import com.limnj.noticeboardadmin.notice.NoticeService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class FileInfoServiceTest {

    @Autowired
    FileInfoService fileInfoServiceImpl;
    @Autowired
    FileInfoMapper fileInfoMapper;

    @Autowired
    NoticeService noticeServiceImpl;
    @Value("${fileLocation}")
    String fileLocation;

    @Test
    void saveFileTest() {
        // given
        Long noticeId = 1L;
        MockMultipartFile noticePdf = new MockMultipartFile(
                "공지사항 PDF",
                "notice.pdf",
                String.valueOf(MediaType.APPLICATION_PDF),
                "공지사항 내용!".getBytes()
        );
        FileInfoRequestDto fileInfoRequestDto = FileInfoRequestDto.builder()
                .fileType(FileType.NOTICE)
                .typeId(noticeId)
                .build();

        // when
        fileInfoServiceImpl.saveFile(fileInfoRequestDto, noticePdf, fileLocation);

        // then
        FileInfoResponseDto findFile = fileInfoMapper.findByType(noticeId, FileType.NOTICE);
        Assertions.assertThat(findFile.getFileType()).isEqualTo(FileType.NOTICE.name());
        Assertions.assertThat(findFile.getTypeId()).isEqualTo(noticeId);
    }

    @Test
    void deleteFileTest() {
        // given
        Long noticeId = 1L;
        MockMultipartFile noticePdf = new MockMultipartFile(
                "공지사항 PDF",
                "notice.pdf",
                String.valueOf(MediaType.APPLICATION_PDF),
                "공지사항 내용!".getBytes()
        );
        FileInfoRequestDto fileInfoRequestDto = FileInfoRequestDto.builder()
                .fileType(FileType.NOTICE)
                .typeId(noticeId)
                .build();

        fileInfoServiceImpl.saveFile(fileInfoRequestDto, noticePdf, fileLocation);
        FileInfoResponseDto findFile = fileInfoMapper.findByType(noticeId, FileType.NOTICE);

        // when
        fileInfoServiceImpl.deleteFile(findFile.getId(), fileLocation, findFile.getFileName());

        // then
        Assertions.assertThat(fileInfoMapper.findByType(noticeId, FileType.NOTICE)).isNull();
    }

}
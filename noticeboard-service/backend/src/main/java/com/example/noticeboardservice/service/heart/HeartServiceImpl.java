package com.example.noticeboardservice.service.heart;

import com.example.noticeboardservice.dto.heart.HeartDto;
import com.example.noticeboardservice.mapper.heart.HeartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class HeartServiceImpl implements HeartService{
    private final HeartMapper heartMapper;

    @Override
    public HeartDto findHeart(Long memberId, Long commentId) {
        HeartDto heartDto = HeartDto.builder()
                .memberId(memberId)
                .commentId(commentId)
                .build();
        List<HeartDto> hearts = heartMapper.findHeartByCommentId(heartDto);
        Optional<HeartDto> findHeartByMember = hearts.stream()
                .filter(h -> Objects.equals(h.getMemberId(), memberId))
                .filter(h -> Objects.equals(h.getCommentId(), commentId))
                .findFirst();

        if (findHeartByMember.isEmpty()) { // 로그인 안했을 때
            HeartDto heartDtoTmp = HeartDto.builder()
                    .commentId(commentId)
                    .build();
            heartDtoTmp.updateCnt(hearts.size());
            return heartDtoTmp;
        }
        findHeartByMember.get().updateCnt(hearts.size()); // 로그인 했을 때
        return findHeartByMember.get();
    }

    @Override
    public int saveHeart(HeartDto heartDto) {
        return heartMapper.saveHeart(heartDto);
    }
    @Override
    public int deleteHeart(Long heartId) {
        return heartMapper.deleteHeart(heartId);
    }

}

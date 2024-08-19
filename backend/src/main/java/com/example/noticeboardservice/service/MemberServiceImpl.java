package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.*;
import com.example.noticeboardservice.exception.MemberNotFoundException;
import com.example.noticeboardservice.exception.PasswordMismatchException;
import com.example.noticeboardservice.mapper.MemberMapper;
import com.example.noticeboardservice.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberMapper memberMapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final ImageService imageServiceImpl;
    @Value("${memberImgLocation}")
    private String memberImgLocation;

    @Override
    public LoginResponseDto login(LoginDto loginDto) {
        MemberResponseDto memberRequestDto = findByEmail(loginDto.getEmail())
                .orElseThrow(MemberNotFoundException::new);

        if (!(memberRequestDto.getEmail().equals(loginDto.getEmail())
                && memberRequestDto.getPassword().equals(loginDto.getPassword()))) {
            throw new PasswordMismatchException();
        }

        String token = jwtTokenUtil.generateToken(memberRequestDto.getEmail());

        return LoginResponseDto.builder()
                .memberId(memberRequestDto.getId())
                .token(token)
                .build();
    }

    @Override
    public int registerMember(MemberRequestDto memberRequestDto) {
        return memberMapper.insertMember(memberRequestDto);
    }
    @Override
    public int updateMember(MemberRequestDto memberRequestDto, MultipartFile memberImg) {
        ImageDto imageDto = ImageDto.builder()
                .typeId(memberRequestDto.getId())
                .imageType(ImageType.MEMBER)
                .build();

        if(memberImg != null){ // 이미지 있을 때만 저장
            imageServiceImpl.findByTypeId(memberRequestDto.getId(), ImageType.MEMBER)
                    .ifPresent(image -> imageServiceImpl.deleteImage(
                            image.getId(), memberImgLocation, image.getImgName())); // 기존 이미지 삭제

            imageServiceImpl.saveImage(imageDto, memberImg, memberImgLocation);
        }

        return memberMapper.updateMember(memberRequestDto);
    }

    @Override
    public MemberResponseDto findMember(Long memberId) {
        MemberResponseDto findMember = Optional.ofNullable(memberMapper.findMember(memberId))
                .orElseThrow(MemberNotFoundException::new);

        if(findMember.getImgUrl() == null) { // 저장된 이미지가 없다면 디폴트 이미지로 내보내기
            findMember.setDefaultImg();
        }

        return findMember;
    }

    @Override
    public Optional<MemberResponseDto> findByEmail(String email) {
        return Optional.ofNullable(memberMapper.findByEmail(email));
    }

    @Override
    public int deleteMember(Long memberId) {
        imageServiceImpl.findByTypeId(memberId, ImageType.MEMBER)
                .ifPresent(imageDto -> imageServiceImpl.deleteImage(
                        imageDto.getId(), memberImgLocation, imageDto.getImgName())); // 있으면 삭제

        return memberMapper.deleteMember(memberId);
    }

    @Override
    public List<MemberResponseDto> findAllMembers() {
        return memberMapper.findAllMembers();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberResponseDto member = findByEmail(email).orElseThrow(MemberNotFoundException::new);
        return new User(
                member.getEmail(), member.getPassword(),
                true, true, true, true,
                new ArrayList<>()
        );
    }
}

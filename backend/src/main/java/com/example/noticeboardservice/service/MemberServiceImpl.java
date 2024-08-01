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
        MemberDto memberDto = findByEmail(loginDto.getEmail())
                .orElseThrow(MemberNotFoundException::new);

        if (!(memberDto.getEmail().equals(loginDto.getEmail())
                && memberDto.getPassword().equals(loginDto.getPassword()))) {
            throw new PasswordMismatchException();
        }

        String token = jwtTokenUtil.generateToken(memberDto.getEmail());

        return LoginResponseDto.builder()
                .memberId(memberDto.getId())
                .token(token)
                .build();
    }

    @Override
    public int registerMember(MemberDto memberDto) {
        return memberMapper.insertMember(memberDto);
    }
    @Override
    public int updateMember(MemberDto memberDto, MultipartFile memberImg) {
        imageServiceImpl.findByTypeId(memberDto.getId(), ImageType.MEMBER)
                .ifPresent(imageDto -> imageServiceImpl.deleteImage(
                        imageDto.getId(), memberImgLocation, imageDto.getImgName())); // 있으면 삭제

        ImageDto imageDto = ImageDto.builder()
                .typeId(memberDto.getId())
                .imageType(ImageType.MEMBER)
                .build();

        imageServiceImpl.saveImage(imageDto, memberImg, memberImgLocation);

        return memberMapper.updateMember(memberDto);
    }

    @Override
    public Optional<MemberDto> findMember(Long memberId) {
        return Optional.ofNullable(memberMapper.findMember(memberId));
    }

    @Override
    public Optional<MemberDto> findByEmail(String email) {
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
    public List<MemberDto> findAllMembers() {
        return memberMapper.findAllMembers();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberDto member = findByEmail(email).orElseThrow(MemberNotFoundException::new);
        return new User(
                member.getEmail(), member.getPassword(),
                true, true, true, true,
                new ArrayList<>()
        );
    }
}

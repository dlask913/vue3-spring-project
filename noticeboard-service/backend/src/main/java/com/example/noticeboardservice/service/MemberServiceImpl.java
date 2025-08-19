package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.*;
import com.example.noticeboardservice.exception.MemberDuplicateException;
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
    private final AddressService addressServiceImpl;
    @Value("${member-img-location}")
    private String memberImgLocation;

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        MemberResponseDto memberResponseDto = findMemberByEmail(loginRequestDto.getEmail())
                .orElseThrow(MemberNotFoundException::new);

        if (!(memberResponseDto.email().equals(loginRequestDto.getEmail())
                && memberResponseDto.password().equals(loginRequestDto.getPassword()))) {
            throw new PasswordMismatchException();
        }

        String accessToken = jwtTokenUtil.generateToken(memberResponseDto.email());
        String refreshToken = jwtTokenUtil.generateRefreshToken(memberResponseDto.email());

        return LoginResponseDto.builder()
                .memberId(memberResponseDto.id())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public int registerMember(MemberRequestDto memberRequestDto) {
        if(findMemberByEmail(memberRequestDto.getEmail()).isPresent()){ // 이메일 중복 체크
            throw new MemberDuplicateException();
        }

        int memberResult = memberMapper.insertMember(memberRequestDto);

        AddressRequestDto address = memberRequestDto.getAddress();
        address.saveMemberId(memberRequestDto.getId());
        int addressResult = addressServiceImpl.insertAddress(address);

        return (memberResult > 0 && addressResult > 0) ? 1 : 0;
    }
    @Override
    public int updateMember(MemberRequestDto memberRequestDto, MultipartFile memberImg) {
        ImageRequestDto imageRequestDto = ImageRequestDto.builder()
                .typeId(memberRequestDto.getId())
                .imageType(ImageType.MEMBER)
                .build();

        if(memberImg != null){ // 이미지 있을 때만 저장
            imageServiceImpl.findByTypeId(memberRequestDto.getId(), ImageType.MEMBER)
                    .ifPresent(image -> imageServiceImpl.deleteImage(
                            image.id(), memberImgLocation, image.imgName())); // 기존 이미지 삭제

            imageServiceImpl.saveImage(imageRequestDto, memberImg, memberImgLocation);
        }

        return memberMapper.updateMember(memberRequestDto);
    }

    @Override
    public MemberResponseDto findMember(Long memberId) {
        return Optional.ofNullable(memberMapper.findMember(memberId))
                .orElseThrow(MemberNotFoundException::new);
    }

    @Override
    public Optional<MemberResponseDto> findMemberByEmail(String email) {
        return Optional.ofNullable(memberMapper.findMemberByEmail(email));
    }

    @Override
    public int deleteMember(Long memberId) {
        imageServiceImpl.findByTypeId(memberId, ImageType.MEMBER)
                .ifPresent(imageDto -> imageServiceImpl.deleteImage(
                        imageDto.id(), memberImgLocation, imageDto.imgName())); // 있으면 삭제

        return memberMapper.deleteMember(memberId);
    }

    @Override
    public List<MemberResponseDto> findAllMembers() {
        return memberMapper.findAllMembers();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberResponseDto member = findMemberByEmail(email).orElseThrow(MemberNotFoundException::new);
        return new User(
                member.email(), member.password(),
                true, true, true, true,
                new ArrayList<>()
        );
    }
}

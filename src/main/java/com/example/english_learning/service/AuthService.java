package com.example.english_learning.service;

import com.example.english_learning.Utils.CheckEmailUtils;
import com.example.english_learning.dto.request.ResetPasswordRequest;
import com.example.english_learning.dto.request.auth.LoginRequest;
import com.example.english_learning.dto.request.auth.RegisterRequest;
import com.example.english_learning.dto.response.LoginResponse;
import com.example.english_learning.mapper.AuthMapper;
import com.example.english_learning.models.User;
import com.example.english_learning.repository.UserRepository;
import com.example.english_learning.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private OtpService otpService;

    @Autowired
    private MailService mailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private CheckEmailUtils checkEmailUtils;

    public LoginResponse login(LoginRequest loginRequest) {

        // 1. Lấy user theo email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Email hoặc mật khẩu không đúng!"
                ));

        // 2. So sánh mật khẩu mã hóa
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email hoặc mật khẩu không đúng!"
            );
        }

        String token = jwtUtil.generateAccessToken(user);

        return LoginResponse.builder()
                .message("Đăng nhập thành công")
                .token(token)
                .user(Map.of(
                        "userId", user.getId(),
                        "fullName", user.getName(),
                        "email", user.getEmail()
                ))
                .build();
    }


    public String register(RegisterRequest registerRequest) {

        if (!checkEmailUtils.isValidGmail(registerRequest.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email không hợp lệ!"
            );
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email đã được sử dụng!"
            );
        }

        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        userRepository.save(authMapper.toEntity(registerRequest));

        return "Đăng ký thành công";
    }

    public String sendOtp(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email chưa được đăng ký!"
            );
        }

        String otp = otpService.generateOtp(email);
        mailService.sendOtp(email, otp);

        return "Đã gửi mã OTP đến email của bạn";
    }

    public Object updateRole(int id, String role) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Tài khoản không tồn tại")
        );

        user.setRole(role);
        userRepository.save(user);

        return Map.of("messengar", "Cập nhật vai trò thành công");
    }

    public String resetPassword(ResetPasswordRequest resetPasswordRequest) {
        if (!userRepository.existsByEmail(resetPasswordRequest.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email chưa được đăng ký!"
            );
        }

        if (!otpService.validateOtp(resetPasswordRequest.getEmail(), resetPasswordRequest.getOtp())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Mã OTP không hợp lệ hoặc đã hết hạn!"
            );
        }

        var user = userRepository.findByEmail(resetPasswordRequest.getEmail()).get();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);

        otpService.clearOtp(resetPasswordRequest.getEmail());

        return "Đặt lại mật khẩu thành công";
    }

    public String resendOtp(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email chưa được đăng ký!"
            );
        }

        String otp = otpService.generateResetOtp(email);
        mailService.sendOtp(email, otp);

        return "Đã gửi lại mã OTP đến email của bạn";
    }

    public User getUserById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy người dùng với ID: " + id
                ));

        return user;
    }

    public List<User> getUserAll() {
        List<User> user = userRepository.findAll();

        return user;
    }

    // ==================== DELETE USER BY ID (ADMIN ONLY) ====================
    public String deleteUserById(int id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy người dùng với ID: " + id
            );
        }

        userRepository.deleteById(id);
        return "Xóa người dùng thành công (ID: " + id + ")";
    }
}

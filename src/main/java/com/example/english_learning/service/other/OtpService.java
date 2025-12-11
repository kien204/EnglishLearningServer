package com.example.english_learning.service.other;

import com.example.english_learning.models.Otp;
import com.example.english_learning.repository.OtpRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    private final OtpRepository otpRepository;

    public OtpService(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    // Tạo OTP ngẫu nhiên 6 số
    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Kiểm tra xem có bản ghi cũ cho email này không
        Otp otpEntity = otpRepository.findByEmail(email)
                .orElse(new Otp());

        otpEntity.setEmail(email);
        otpEntity.setOtp(otp);
        otpEntity.setCreatedAt(LocalDateTime.now());
        otpEntity.setExpiresAt(LocalDateTime.now().plusMinutes(5)); // hết hạn sau 5 phút
        otpEntity.setLastRequestedAt(LocalDateTime.now());

        otpRepository.save(otpEntity);

        return otp;
    }

    // Giới hạn gửi lại OTP sau 60s
    public String generateResetOtp(String email) {
        Otp existingOtp = otpRepository.findByEmail(email).orElse(null);

        if (existingOtp != null && existingOtp.getLastRequestedAt() != null) {
            if (existingOtp.getLastRequestedAt().isAfter(LocalDateTime.now().minusSeconds(60))) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Bạn phải đợi 60s mới có thể yêu cầu lại OTP"
                );
            }
        }

        return generateOtp(email);
    }

    // Kiểm tra OTP
    public boolean validateOtp(String email, String otp) {
        Otp otpEntity = otpRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy OTP"));

        if (LocalDateTime.now().isAfter(otpEntity.getExpiresAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP đã hết hạn");
        }

        boolean valid = otpEntity.getOtp().equals(otp);
        if (valid) {
            otpRepository.delete(otpEntity); // xoá sau khi dùng
        }
        return valid;
    }

    // Xoá OTP (tuỳ chọn)
    public void clearOtp(String email) {
        otpRepository.findByEmail(email).ifPresent(otpRepository::delete);
    }
}

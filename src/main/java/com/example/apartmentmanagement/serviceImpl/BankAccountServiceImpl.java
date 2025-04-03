package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.BankAccountQRResponseDTO;
import com.example.apartmentmanagement.dto.BankAccountRequestDTO;
import com.example.apartmentmanagement.entities.BankAccount;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.enums.BankEnum;
import com.example.apartmentmanagement.repository.BankAccountRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.BankAccountService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public BankAccountQRResponseDTO addBankAccountWithQR(BankAccountRequestDTO bankAccountRequestDTO) {
        // Tìm user
        User user = userRepository.findById(bankAccountRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Tạo tài khoản ngân hàng
        BankAccount bankAccount = BankAccount.builder()
                .accountNumber(bankAccountRequestDTO.getAccountNumber())
                .accountName(bankAccountRequestDTO.getAccountName())
                .bank(bankAccountRequestDTO.getBank())
                .isActive(true)
                .user(user)
                .build();

        bankAccount.setUser(user);
        // Lưu tài khoản
        bankAccountRepository.save(bankAccount);
        userRepository.save(user);

        // Trả về response
        return BankAccountQRResponseDTO.builder()
                .userId(bankAccountRequestDTO.getUserId())
                .accountNumber(bankAccountRequestDTO.getAccountNumber())
                .accountName(bankAccountRequestDTO.getAccountName())
                .bankName(bankAccount.getBank().getFullName())
                .bankBin(bankAccount.getBank().getBin())
                .build();
    }

    @Override
    public BankAccountQRResponseDTO generateQR(BankAccountRequestDTO bankAccountRequestDTO) {
        BankAccount bankAccount = bankAccountRepository.findByUser_UserId(bankAccountRequestDTO.getCreatedUserId());
        if (bankAccount == null) {
            throw new RuntimeException("Không tìm thấy account bank");
        }

        String amountString = bankAccountRequestDTO.getAmount() > 0
                ? String.valueOf((int) bankAccountRequestDTO.getAmount())
                : "";

        String qrContent = generateVietQRContent(
                bankAccount.getAccountNumber(),
                bankAccountRequestDTO.getContent(),
                amountString
        );

        String qrCodeBase64 = generateBase64QRCode(qrContent);

        // Trả về response
        return BankAccountQRResponseDTO.builder()
                .userId(bankAccount.getUser().getUserId())
                .accountNumber(bankAccount.getAccountNumber())
                .accountName(bankAccount.getAccountName())
                .bankName(bankAccount.getBank().getFullName())
                .bankBin(bankAccount.getBank().getBin())
                .qrCodeContent(qrContent)
                .qrCodeBase64(qrCodeBase64)
                .build();
    }

    public String generateVietQRContent(String accountNumber, String description, String amount) {
        // Loại bỏ ký tự không hợp lệ
        String totalAmount = amount.replaceAll("[^0-9]", "");
        String transferContent = description.length() > 50 ? description.substring(0, 50) : description;

        // Tạo chuỗi VietQR
        String qrData = String.format(
                "00020101021238570010A000000727012700069704050113%s0208QRIBFTTA5303704540%s%s5802VN62%s63",
                accountNumber, totalAmount.length(), totalAmount, transferContent
        );

        // Tính CRC-16
        String crc = getCrc16Valid(qrData);

        return qrData + crc;
    }

    public String generateBase64QRCode(String qrContent) {
        try {
            // Sinh ảnh QR
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    qrContent,
                    BarcodeFormat.QR_CODE,
                    300,
                    300
            );

            // Chuyển sang ByteArrayOutputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);

            // Chuyển sang Base64
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi sinh QR code", e);
        }
    }

    public static int crc16(final byte[] buffer) {
        int crc = 0xFFFF;

        for (byte b : buffer) {
            crc = ((crc >>> 8) | (crc << 8)) & 0xffff;
            crc ^= (b & 0xff);
            crc ^= ((crc & 0xff) >> 4);
            crc ^= (crc << 12) & 0xffff;
            crc ^= ((crc & 0xFF) << 5) & 0xffff;
        }
        crc &= 0xffff;
        return crc;
    }

    private static String getCrc16Valid(String vietQRCode) {
        int crc = crc16(vietQRCode.getBytes(StandardCharsets.UTF_8));
        return String.format("%04X", crc);
    }
}
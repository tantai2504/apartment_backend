package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.DepositListResponseDTO;
import com.example.apartmentmanagement.dto.DepositPaymentDTO;
import com.example.apartmentmanagement.dto.DepositRequestDTO;
import com.example.apartmentmanagement.dto.DepositResponseDTO;
import com.example.apartmentmanagement.entities.*;
import com.example.apartmentmanagement.repository.DepositRepository;
import com.example.apartmentmanagement.repository.PaymentRepository;
import com.example.apartmentmanagement.repository.PostRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.DepositService;
import com.example.apartmentmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.apartmentmanagement.service.NotificationService;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;

import java.util.HashMap;
import java.util.List;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DepositServiceImpl implements DepositService {

    @Autowired
    private PayOS payOS;

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Override
    public DepositResponseDTO processPaymentSuccess(DepositPaymentDTO depositPaymentDTO) {
        Deposit deposit = depositRepository.findById(depositPaymentDTO.getDepositId()).get();
        Post post = postRepository.findById(depositPaymentDTO.getPostId()).get();
        Apartment apartment = post.getApartment();
        User user = userRepository.findById(depositPaymentDTO.getDepositUserId()).get();

        if (post.getDepositCheck().equals("ongoing") && post.getDepositUserId() != null) {
            User postOwner = post.getUser();
            Payment payment = new Payment();
            payment.setPaymentCheck(true);
            payment.setPrice(depositPaymentDTO.getDepositPrice());
            payment.setPaymentInfo("Thanh toán tiền đặt cọc");
            payment.setPaymentDate(LocalDateTime.now());
            payment.setUser(user);
            payment.setPaymentType("deposit");
            payment.setDeposit(deposit);
            Payment paymentSaved =  paymentRepository.save(payment);

            deposit.setApartment(apartment);
            deposit.setUser(user);
            deposit.setPayment(paymentSaved);
            deposit.setStatus("done");
            depositRepository.save(deposit);

            post.setDepositCheck("done");
            postRepository.save(post);

            // Tạo thông báo cho người đặt cọc
            notificationService.createAndBroadcastNotification(
                    "Bạn đã đặt cọc thành công cho bài đăng: " + post.getTitle(),
                    "deposit",
                    user.getUserId()
            );

            // Tạo thông báo cho chủ bài đăng
            notificationService.createAndBroadcastNotification(
                    "Bài đăng " + post.getTitle() + " đã được đặt cọc",
                    "deposit",
                    postOwner.getUserId()
            );

            return new DepositResponseDTO(
                    depositPaymentDTO.getDepositUserId(),
                    depositPaymentDTO.getPostId(),
                    depositPaymentDTO.getDepositPrice(),
                    depositPaymentDTO.getDepositId(),
                    "done"
            );
        }
        else {
            throw new RuntimeException("Chưa có đối tượng đặt cọc");
        }
    }

    @Override
    public DepositResponseDTO depositFlag(DepositRequestDTO depositRequestDTO) {
        Post post = postRepository.findById(depositRequestDTO.getPostId()).get();
        Apartment apartment = post.getApartment();
        User user = userRepository.findById(depositRequestDTO.getDepositUserId()).get();

        if (post.getDepositUserId() == null) {
            Deposit deposit = new Deposit();
            deposit.setApartment(apartment);
            deposit.setUser(user);
            deposit.setPayment(null);
            deposit.setStatus("ongoing");
            deposit.setPrice(post.getDepositPrice());
            Deposit depositSave = depositRepository.save(deposit);
            Long depositSaveId = depositSave.getDepositId();

            post.setDepositUserId(depositRequestDTO.getDepositUserId());
            post.setDepositCheck("ongoing");
            postRepository.save(post);

            return new DepositResponseDTO(
                    depositRequestDTO.getDepositUserId(),
                    depositRequestDTO.getPostId(),
                    depositRequestDTO.getDepositPrice(),
                    depositSaveId,
                    "ongoing"
            );
        } else if (post.getDepositUserId() != null && post.getDepositCheck().equals("ongoing")) {
            throw new RuntimeException("Đang có người thực hiện quá trình đặt cọc");
        }
        return null;
    }

    @Override
    public DepositResponseDTO cancel(DepositPaymentDTO depositPaymentDTO) {
        Post post = postRepository.findById(depositPaymentDTO.getPostId()).get();
        Apartment apartment = post.getApartment();
        User user = userRepository.findById(depositPaymentDTO.getDepositUserId()).get();
        Deposit deposit = depositRepository.findById(depositPaymentDTO.getDepositId()).get();
        if (post.getDepositCheck().equals("ongoing") && post.getDepositUserId() != null) {
            deposit.setApartment(apartment);
            deposit.setUser(user);
            deposit.setPayment(null);
            deposit.setStatus("cancel");
            depositRepository.save(deposit);

            post.setDepositUserId(null);
            post.setDepositCheck(null);
            postRepository.save(post);
            return new DepositResponseDTO(
                    depositPaymentDTO.getDepositUserId(),
                    depositPaymentDTO.getPostId(),
                    depositPaymentDTO.getDepositPrice(),
                    depositPaymentDTO.getDepositId(),
                    "cancel"
            );
        } else {
            throw new RuntimeException("Không thể hủy thanh toán mục chưa thực hiện thanh toán");
        }
    }

    @Override
    public List<DepositListResponseDTO> getAllDeposits() {
        List<Deposit> deposits = depositRepository.findAll();
        return deposits.stream().map(deposit -> {
            DepositListResponseDTO dto = new DepositListResponseDTO();
            dto.setDepositId(deposit.getDepositId());
            dto.setApartmentName(deposit.getApartment().getApartmentName());
            dto.setDepositUserName(deposit.getUser().getUserName());
            dto.setDepositPrice(deposit.getPrice());
            dto.setStatus(deposit.getStatus());
            User getUsername = userService.getUserByEmailOrUserName(deposit.getApartment().getHouseholder());
            dto.setPostOwnerName(getUsername.getFullName());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> paymentResponseData(CheckoutResponseData checkoutResponseData, DepositResponseDTO dto) {
        Map<String, Object> data = new HashMap<>();
        data.put("depositId",dto.getDepositId());
        data.put("postId",dto.getPostId());
        data.put("depositUserId",dto.getDepositUserId());
        data.put("depositPrice",dto.getDepositPrice());
        data.put("checkoutUrl", checkoutResponseData.getCheckoutUrl());
        return data;
    }
}


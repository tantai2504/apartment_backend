package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.DepositListResponseDTO;
import com.example.apartmentmanagement.dto.DepositRequestDTO;
import com.example.apartmentmanagement.dto.DepositResponseDTO;
import com.example.apartmentmanagement.entities.Deposit;
import com.example.apartmentmanagement.entities.Payment;
import com.example.apartmentmanagement.entities.Post;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.DepositRepository;
import com.example.apartmentmanagement.repository.PaymentRepository;
import com.example.apartmentmanagement.repository.PostRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.apartmentmanagement.service.NotificationService;
import vn.payos.PayOS;
import java.util.List;

import java.time.LocalDateTime;
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

    @Override
    public DepositResponseDTO processPaymentSuccess(DepositRequestDTO depositRequestDTO) {
        Post post = postRepository.findById(depositRequestDTO.getPostId()).get();

        if (post.getDepositCheck().equals("ongoing") && post.getDepositUserId() != null) {
            User user = userRepository.findById(depositRequestDTO.getDepositUserId()).get();
            User postOwner = post.getUser();

            Payment payment = new Payment();
            payment.setPaymentCheck(true);
            payment.setPaymentInfo("Thanh toán tiền đặt cọc");
            payment.setPaymentDate(LocalDateTime.now());
            payment.setUser(user);
            payment.setPaymentType("deposit");
            paymentRepository.save(payment);

            Deposit deposit = new Deposit();
            deposit.setPost(post);
            deposit.setUser(user);
            deposit.setPayment(payment);
            deposit.setStatus("done");
            depositRepository.save(deposit);

            post.setDepositCheck("done");
            post.setPayment(payment);
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
                    deposit.getDepositId(),
                    deposit.getStatus(),
                    deposit.getUser().getUserId()
            );
        }
        else {
            throw new RuntimeException("Chưa có đối tượng đặt cọc");
        }
    }

    @Override
    public DepositResponseDTO depositFlag(DepositRequestDTO depositRequestDTO) {
        Post post = postRepository.findById(depositRequestDTO.getPostId()).get();
        if (post.getDepositUserId() == null) {
            post.setDepositUserId(depositRequestDTO.getDepositUserId());
            post.setDepositCheck("ongoing");
            post.setDepositPrice(depositRequestDTO.getDepositPrice());
            postRepository.save(post);
        } else if (post.getDepositUserId() != null && post.getDepositCheck().equals("ongoing")) {
            throw new RuntimeException("Đang có người thực hiện quá trình đặt cọc");
        }
        return new DepositResponseDTO(
                post.getPostId(),
                post.getDepositCheck(),
                post.getDepositUserId()
        );
    }

    @Override
    public DepositResponseDTO cancel(DepositRequestDTO depositRequestDTO) {
        Post post = postRepository.findById(depositRequestDTO.getPostId()).get();
        if (post.getDepositCheck().equals("ongoing") && post.getDepositUserId() != null) {
            post.setDepositUserId(null);
            post.setDepositCheck("none");
            postRepository.save(post);
            return new DepositResponseDTO(
                    post.getPostId(),
                    post.getDepositCheck(),
                    post.getDepositUserId()
            );
        } else {
            throw new RuntimeException("Không thể hoàn lại thanh toán");
        }
    }

    @Override
    public List<DepositListResponseDTO> getAllDeposits() {
        List<Deposit> deposits = depositRepository.findAll();
        return deposits.stream().map(deposit -> {
            DepositListResponseDTO dto = new DepositListResponseDTO();
            dto.setDepositId(deposit.getDepositId());
            dto.setStatus(deposit.getStatus());

            dto.setPostOwnerId(deposit.getPost().getUser().getUserId());
            dto.setPostOwnerName(deposit.getPost().getUser().getFullName());

            dto.setDepositUserId(deposit.getUser().getUserId());
            dto.setDepositUserName(deposit.getUser().getFullName());

            dto.setPostId(deposit.getPost().getPostId());
            dto.setPostTitle(deposit.getPost().getTitle());
            dto.setDepositPrice(deposit.getPost().getDepositPrice());
            dto.setDepositCheck(deposit.getPost().getDepositCheck());

            if (deposit.getPayment() != null) {
                dto.setPaymentId(deposit.getPayment().getPaymentId());
                dto.setPaymentDate(deposit.getPayment().getPaymentDate());
                dto.setPaymentInfo(deposit.getPayment().getPaymentInfo());
            }

            dto.setApartmentName(deposit.getPost().getApartment().getApartmentName());

            return dto;
        }).collect(Collectors.toList());
    }
}

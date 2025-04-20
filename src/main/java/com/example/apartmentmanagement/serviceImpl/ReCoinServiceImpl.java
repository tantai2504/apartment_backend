package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.ReCoinRequestDTO;
import com.example.apartmentmanagement.dto.ReCoinResponseDTO;
import com.example.apartmentmanagement.dto.ReCoinUpdateRequestDTO;
import com.example.apartmentmanagement.entities.ReCoin;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.ReCoinRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.ReCoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class ReCoinServiceImpl implements ReCoinService {

    @Autowired
    private ReCoinRepository reCoinRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ReCoinResponseDTO addRecoin(ReCoinRequestDTO reCoinRequestDTO){
        try{
            // Tạo payload theo format VietQR (ví dụ đơn giản)
            String rawData = String.format("bank:%s|pin:%s|account:%s|name:%s",
                    reCoinRequestDTO.getBankName(),
                    reCoinRequestDTO.getBankPin(),
                    reCoinRequestDTO.getBankNumber(),
                    reCoinRequestDTO.getAccountName());

            // Encode base64
            String encodedData = Base64.getEncoder().encodeToString(rawData.getBytes(StandardCharsets.UTF_8));
            //https://img.vietqr.io/image/<BANK_ID>-<ACCOUNT_NO>-<TEMPLATE>.png?amount=<AMOUNT>&addInfo=<DESCRIPTION>&accountName=<ACCOUNT_NAME>
            //https://img.vietqr.io/image/vietinbank-113366668888-compact2.jpg?amount=790000&addInfo=dong%20qop%20quy%20vac%20xin&accountName=Quy%20Vac%20Xin%20Covid

            // Tạo URL cho VietQR
            String imgQR = "https://img.vietqr.io/image/" +
                    reCoinRequestDTO.getBankPin() + "-" +
                    reCoinRequestDTO.getBankNumber() + "-compact2.jpg?amount="+reCoinRequestDTO.getAmount()+"&addInfo="+URLEncoder.encode(reCoinRequestDTO.getContent(), StandardCharsets.UTF_8)+"&accountName=" +
                    URLEncoder.encode(reCoinRequestDTO.getAccountName(), StandardCharsets.UTF_8);

            User userRequest = userRepository.findById(reCoinRequestDTO.getUserRequestId()).get();
            userRequest.setAccountBalance(userRequest.getAccountBalance()-reCoinRequestDTO.getAmount());
            User userSaved = userRepository.save(userRequest);

            ReCoin reCoin = new ReCoin();
            reCoin.setUser(userSaved);
            reCoin.setBankNumber(reCoinRequestDTO.getBankNumber());
            reCoin.setBankName(reCoinRequestDTO.getBankName());
            reCoin.setBankPin(reCoinRequestDTO.getBankPin());
            reCoin.setAccountName(reCoinRequestDTO.getAccountName());
            reCoin.setAmount(reCoinRequestDTO.getAmount());
            reCoin.setImgQR(imgQR);
            reCoin.setStatus("pending");
            reCoin.setContent(reCoinRequestDTO.getContent());
            LocalDateTime now = LocalDateTime.now();
            reCoin.setDateTime(now);
            reCoinRepository.save(reCoin);

            ReCoinResponseDTO reCoinResponseDTO = new ReCoinResponseDTO(
                    reCoinRequestDTO.getAmount(),
                    imgQR,
                    reCoinRequestDTO.getUserRequestId(),
                    reCoinRequestDTO.getContent()
                    );
            return reCoinResponseDTO;
        }catch(RuntimeException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ReCoinResponseDTO> listAllReCoin() {
        return reCoinRepository.findAll().stream()
                .map( reCoin -> new ReCoinResponseDTO(
                        reCoin.getReCoinId(),
                        reCoin.getBankNumber(),
                        reCoin.getBankName(),
                        reCoin.getBankPin(),
                        reCoin.getAccountName(),
                        reCoin.getAmount(),
                        reCoin.getImgQR(),
                        reCoin.getImgBill(),
                        reCoin.getStatus(),
                        reCoin.getUser().getUserId(),
                        reCoin.getContent(),
                        reCoin.getDateTime(),
                        reCoin.getUser().getFullName(),
                        reCoin.getDateAcceptReject(),
                        reCoin.getDateComplete()
                )).toList();
    }

    @Override
    public List<ReCoinResponseDTO> listReCoinByUserId(Long userId) {
        return reCoinRepository.findByUser_UserId(userId).stream()
                .map( reCoin -> new ReCoinResponseDTO(
                        reCoin.getReCoinId(),
                        reCoin.getBankNumber(),
                        reCoin.getBankName(),
                        reCoin.getBankPin(),
                        reCoin.getAccountName(),
                        reCoin.getAmount(),
                        reCoin.getImgQR(),
                        reCoin.getImgBill(),
                        reCoin.getStatus(),
                        reCoin.getUser().getUserId(),
                        reCoin.getContent(),
                        reCoin.getDateTime(),
                        reCoin.getUser().getFullName(),
                        reCoin.getDateAcceptReject(),
                        reCoin.getDateComplete()
                )).toList();
    }

    @Override
    public void acceptReCoin(ReCoinUpdateRequestDTO reCoinUpdateRequestDTO) {
        ReCoin reCoin = reCoinRepository.findById(reCoinUpdateRequestDTO.getReCoinId()).get();
        reCoin.setImgBill(reCoinUpdateRequestDTO.getImgBill());
        reCoin.setStatus("processing");
        LocalDateTime now = LocalDateTime.now();
        reCoin.setDateAcceptReject(now);
        reCoinRepository.save(reCoin);
    }

    @Override
    public void rejectReCoin(ReCoinUpdateRequestDTO reCoinUpdateRequestDTO) {
        ReCoin reCoin = reCoinRepository.findById(reCoinUpdateRequestDTO.getReCoinId()).get();
        User userRequest = reCoin.getUser();
        userRequest.setAccountBalance(userRequest.getAccountBalance()+reCoin.getAmount());
        User userSaved = userRepository.save(userRequest);
        reCoin.setUser(userSaved);
        reCoin.setStatus("reject");
        LocalDateTime now = LocalDateTime.now();
        reCoin.setDateAcceptReject(now);
        reCoinRepository.save(reCoin);
    }

    @Override
    public void acceptReceivedReCoin(ReCoinUpdateRequestDTO reCoinUpdateRequestDTO) {
        ReCoin reCoin = reCoinRepository.findById(reCoinUpdateRequestDTO.getReCoinId()).get();
        reCoin.setStatus("completed");
        LocalDateTime now = LocalDateTime.now();
        reCoin.setDateComplete(now);
        reCoinRepository.save(reCoin);
    }
}

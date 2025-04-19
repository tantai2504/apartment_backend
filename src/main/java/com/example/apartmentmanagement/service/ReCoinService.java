package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.ReCoinRequestDTO;
import com.example.apartmentmanagement.dto.ReCoinResponseDTO;
import com.example.apartmentmanagement.dto.ReCoinUpdateRequestDTO;
import com.example.apartmentmanagement.entities.ReCoin;

import java.util.List;

public interface ReCoinService {
    ReCoinResponseDTO addRecoin(ReCoinRequestDTO reCoinRequestDTO);

    List<ReCoinResponseDTO> listAllReCoin();

    List<ReCoinResponseDTO> listReCoinByUserId(Long userId);

    void acceptReCoin(ReCoinUpdateRequestDTO reCoinUpdateRequestDTO);

    void rejectReCoin(ReCoinUpdateRequestDTO reCoinUpdateRequestDTO);

    void acceptReceivedReCoin(ReCoinUpdateRequestDTO reCoinUpdateRequestDTO);
}

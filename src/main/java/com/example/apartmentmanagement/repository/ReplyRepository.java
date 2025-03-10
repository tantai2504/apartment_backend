package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findByReport_ReportId(Long reportId);
}

package com.example.backend.member.repository;

import com.example.backend.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    <T> Optional<T> findByUniqueId(String uniqueId);

    Optional<Object> findByEmail(String email);

    // 📌 특정 문서의 uniqueId를 기반으로 멤버 이름 조회
    @Query("SELECT m.name FROM Member m WHERE m.uniqueId = :uniqueId")
    String findMemberNameByUniqueId(@Param("uniqueId") String uniqueId);
}

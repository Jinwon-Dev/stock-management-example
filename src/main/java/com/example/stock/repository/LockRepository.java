package com.example.stock.repository;

import com.example.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LockRepository extends JpaRepository<Stock, Long> {

    /**
     * 장점
     * : Time out을 구현하기 쉬움
     * : 데이터 삽입시 정합성을 맞춰야 하는 경우
     *
     * 단점
     * : 트랜잭션 종료시에 락 해제, 세션 관리에 신경써야 함
     * : 구현 방법이 어려움
     */
    @Query(value = "SELECT get_lock(:key, 3000)", nativeQuery = true)
    void getLock(final String key);

    @Query(value = "SELECT release_lock(:key)", nativeQuery = true)
    void releaseLock(final String key);
}

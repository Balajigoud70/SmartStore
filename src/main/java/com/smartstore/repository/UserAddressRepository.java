package com.smartstore.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.smartstore.entity.UserAddress;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    // లాగిన్ అయిన యూజర్ ఐడీని బట్టి వాళ్ళ అడ్రస్‌లు మాత్రమే లాగడానికి బ్రదర్
    List<UserAddress> findByUserId(Long userId);
}
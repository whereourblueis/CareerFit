package com.codelab.micproject.booking.repository;


import com.codelab.micproject.booking.domain.*;
import com.codelab.micproject.account.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.OffsetDateTime;
import java.util.List;


public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByConsultantAndStartAtLessThanEqualAndEndAtGreaterThanEqual(User c, OffsetDateTime end, OffsetDateTime start);
    List<Appointment> findByUser(User u);
}
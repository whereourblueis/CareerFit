package com.codelab.micproject.booking.service;


import com.codelab.micproject.account.user.repository.UserRepository;
import com.codelab.micproject.booking.domain.Availability;
import com.codelab.micproject.booking.dto.*;
import com.codelab.micproject.booking.repository.AvailabilityRepository;
import com.codelab.micproject.security.oauth2.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.*;
import java.util.*;


@Service @RequiredArgsConstructor
public class AvailabilityService {
    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;


    @Transactional
    public AvailabilityView upsert(UserPrincipal me, UpsertAvailabilityReq req){
        var meUser = userRepository.findById(me.id()).orElseThrow();
        var a = Availability.builder()
                .consultant(meUser)
                .weekday(req.weekday())
                .startTime(req.startTime())
                .endTime(req.endTime())
                .slotMinutes(req.slotMinutes())
                .zoneId(req.zoneId())
                .build();
        availabilityRepository.save(a);
        return new AvailabilityView(a.getId(), a.getWeekday(), a.getStartTime(), a.getEndTime(), a.getSlotMinutes(), a.getZoneId());
    }


    /**
     * 주어진 기간[from,to] 동안 반복 가용 시간으로 생성되는 슬롯을 계산한다.
     */
    @Transactional(readOnly = true)
    public List<SlotDto> generateSlots(Long consultantId, LocalDate from, LocalDate to){

        if (to.isBefore(from)) {
            throw new IllegalArgumentException("`to` must be the same or after `from`");
        }
        long days = java.time.temporal.ChronoUnit.DAYS.between(from, to) + 1;
        if (days > 60) {
            throw new IllegalArgumentException("Date range is too wide (max 60 days)");
        }

        var consultant = userRepository.findById(consultantId).orElseThrow();
        var availList = availabilityRepository.findByConsultant(consultant);
        List<SlotDto> out = new ArrayList<>();
        for (var day = from; !day.isAfter(to); day = day.plusDays(1)){
            int wd = day.getDayOfWeek().getValue(); // 1~7
            for (var av : availList){
                if (av.getWeekday() != wd) continue;
                var zone = av.getZoneId()!=null? ZoneId.of(av.getZoneId()) : ZoneId.systemDefault();
                var startLocal = LocalTime.parse(av.getStartTime());
                var endLocal = LocalTime.parse(av.getEndTime());
                var slot = Duration.ofMinutes(av.getSlotMinutes());
                for (var t = startLocal; !t.plus(slot).isAfter(endLocal); t = t.plus(slot)){
                    var startZdt = ZonedDateTime.of(day, t, zone);
                    var endZdt = startZdt.plus(slot);
                    out.add(new SlotDto(startZdt.toOffsetDateTime(), endZdt.toOffsetDateTime()));
                }
            }
        }
        return out;
    }
}
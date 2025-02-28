package com.example.nagoyameshi.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Store;
import com.example.nagoyameshi.entity.User;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
	Page<Reservation> findByUser(User user, Pageable pageable);
	List<Reservation> findByStoreAndUser(Store store, User user);
	List<Reservation> findByStoreAndUserAndReservationDateAndReservationTimeBetween(Store store, User user, LocalDate reservationDate, LocalTime startTime, LocalTime endTime);
	
}
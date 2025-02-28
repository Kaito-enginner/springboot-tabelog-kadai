package com.example.nagoyameshi.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Store;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationRegisterForm;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.StoreRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class ReservationService {
	private final ReservationRepository reservationRepository;
	private final StoreRepository storeRepository;
	private final UserRepository userRepository;
	
	public ReservationService(ReservationRepository reservationRepository, StoreRepository storeRepository, UserRepository userRepository) {
		this.reservationRepository = reservationRepository;
		this.storeRepository = storeRepository;
		this.userRepository = userRepository;
	}
	
	public void create(ReservationRegisterForm reservationRegisterForm) {
		Reservation reservation = new Reservation();
		Store store = storeRepository.getReferenceById(reservationRegisterForm.getStoreId());
		User user = userRepository.getReferenceById(reservationRegisterForm.getUserId());
		
		
		reservation.setStore(store);
		reservation.setUser(user);
		reservation.setReservationDate(reservationRegisterForm.getReservationDate());
		reservation.setReservationTime(reservationRegisterForm.getReservationTime());
		reservation.setNumberOfPeople(reservationRegisterForm.getNumberOfPeople());
		
		reservationRepository.save(reservation);
	}
	
	// 同店舗に同一人物が予約しているかチェックする
	public boolean duplicateCheckFirst(ReservationRegisterForm reservationRegisterForm) {
		Store store = storeRepository.getReferenceById(reservationRegisterForm.getStoreId());
		User user = userRepository.getReferenceById(reservationRegisterForm.getUserId());
		
		List<Reservation> reservation = reservationRepository.findByStoreAndUser(store, user);
		
		return reservation != null;
	}
	
	// 同日の+-2時間以内に予約がないかチェックする
	public boolean duplicateCheckSecond(ReservationRegisterForm reservationRegisterForm) {
		LocalDate reservationDate = reservationRegisterForm.getReservationDate();
		LocalTime reservationTime = reservationRegisterForm.getReservationTime();
		Store store = storeRepository.getReferenceById(reservationRegisterForm.getStoreId());
		User user = userRepository.getReferenceById(reservationRegisterForm.getUserId());

		// 予約時間から+-2時間した時間を取得する
		LocalTime startTime = reservationTime.minusHours(2);
		LocalTime endTime = reservationTime.plusHours(2);
		
		List<Reservation> reservation = reservationRepository.findByStoreAndUserAndReservationDateAndReservationTimeBetween(store, user, reservationDate, startTime, endTime);
		
		return reservation != null && reservation.size() != 0;
	}
}

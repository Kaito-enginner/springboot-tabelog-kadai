package com.example.nagoyameshi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Store;
import com.example.nagoyameshi.entity.StoreCategories;
import com.example.nagoyameshi.form.StoreEditForm;
import com.example.nagoyameshi.form.StoreRegisterForm;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.StoreCategoriesRepository;
import com.example.nagoyameshi.repository.StoreRepository;


@Service
public class StoreService {
	private final StoreRepository storeRepository;
	private final StoreCategoriesRepository storeCategoriesRepository;
	private final CategoryRepository categoryRepository;
	
	public StoreService(StoreRepository storeRepository, StoreCategoriesRepository storeCategoriesRepository, CategoryRepository categoryRepository) {
		this.storeRepository = storeRepository;
		this.storeCategoriesRepository = storeCategoriesRepository;
		this.categoryRepository = categoryRepository;
	}
	
	// 店舗情報を登録
	@Transactional
	public void create(StoreRegisterForm storeRegisterForm) {
		Store store = new Store();
	    MultipartFile imageFile = storeRegisterForm.getImageFile();
	    
	    if(!imageFile.isEmpty()) {
	    	String imageName = imageFile.getOriginalFilename();
	    	String hashedImageName = generateNewFileName(imageName);
	    	Path filePath = Paths.get("src/main/resources/static/images/storeImages/" + hashedImageName);
	    	copyImageFile(imageFile, filePath);
	    	store.setImageName(hashedImageName);
	    }
		store.setName(storeRegisterForm.getName());
		store.setDescription(storeRegisterForm.getDescription());
		store.setPrice(storeRegisterForm.getPrice());
		store.setOpeningHouresStart(storeRegisterForm.getOpeningHouresStart());
		store.setOpeningHouresEnd(storeRegisterForm.getOpeningHouresEnd());
		store.setHoliday(storeRegisterForm.getHoliday());
		store.setAddress(storeRegisterForm.getAddress());
		store.setPhoneNumber(storeRegisterForm.getPhoneNumber());
		
		storeRepository.save(store);
				
		List<Integer> categories = storeRegisterForm.getCategoryId();
		
		for(int i = 0; i < categories.size(); i++) {
			StoreCategories storeCategories = new StoreCategories();
			Integer categoryId = categories.get(i);
			Category category = categoryRepository.getReferenceById(categoryId);
			storeCategories.setStore(store);
			storeCategories.setCategory(category);
			
			storeCategoriesRepository.save(storeCategories);
		}
	}
	
	
	// 店舗情報を更新
	@Transactional
	public void update(StoreEditForm storeEditForm) {
		Store store = storeRepository.getReferenceById(storeEditForm.getId());
	    MultipartFile imageFile = storeEditForm.getImageFile();
		
	    if(!imageFile.isEmpty()) {
	    	String imageName = imageFile.getOriginalFilename();
	    	String hashedImageName = generateNewFileName(imageName);
	    	Path filePath = Paths.get("src/main/resources/static/images/storeImages/" + hashedImageName);
	    	copyImageFile(imageFile, filePath);
	    	store.setImageName(hashedImageName);
	    }
	    
	    store.setName(storeEditForm.getName());
	    store.setDescription(storeEditForm.getDescription());
	    store.setAddress(storeEditForm.getAddress());
	    store.setPrice(storeEditForm.getPrice());
	    store.setOpeningHouresStart(storeEditForm.getOpeningHouresStart());
	    store.setOpeningHouresEnd(storeEditForm.getOpeningHouresEnd());
	    store.setHoliday(storeEditForm.getHoliday());
	    store.setPhoneNumber(storeEditForm.getPhoneNumber());
	    
	    storeRepository.save(store);
	    
	    List<StoreCategories> deleteStoreCategories = storeCategoriesRepository.findByStoreId(store.getId());
		for(int i = 0; i < deleteStoreCategories.size(); i++) {
			Integer StoreCategoriesId = deleteStoreCategories.get(i).getId();
	    	storeCategoriesRepository.deleteById(StoreCategoriesId);
		}
	    
		List<Integer> categories = storeEditForm.getCategoryId();
	    for(int i = 0; i < categories.size(); i++) {
			StoreCategories storeCategories = new StoreCategories();
	    	Integer categoryId = categories.get(i);
	    	Category category = categoryRepository.getReferenceById(categoryId);
	    	storeCategories.setStore(store);
	    	storeCategories.setCategory(category);
	    	storeCategoriesRepository.save(storeCategories);
	    }
	}
	

	// UUIDを使って生成したファイル名を返す
	public String generateNewFileName(String fileName) {
	    String[] fileNames = fileName.split("\\.");                
	    for (int i = 0; i < fileNames.length - 1; i++) {
	        fileNames[i] = UUID.randomUUID().toString();            
	    }
	    String hashedFileName = String.join(".", fileNames);
	    return hashedFileName;
	}     
	   
	// 画像ファイルを指定したファイルにコピーする
	public void copyImageFile(MultipartFile imageFile, Path filePath) {           
	    try {
	        Files.copy(imageFile.getInputStream(), filePath);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }          
	} 
	
	
	//現在の日にちを取得し、定休日を除く30日分の日付リストを作成
	public List<LocalDate> createDateList(Store store) {
        LocalDate date = LocalDate.now().plusDays(1);
        
        List<LocalDate> dateList = new ArrayList<>();
        for(int i = 0; i < 30; i++) {
        	LocalDate reservationDate = date.plusDays(i);
        	Integer dayOfWeek = reservationDate.getDayOfWeek().getValue();
        	if(!dayOfWeek.equals(store.getHoliday())) {
            	dateList.add(reservationDate);
        	}
        }
        return dateList;
	}
	
	//営業時間を取得し、30分ごとの時間リストを作成
	public List<String> createTimeList(Store store) {
		LocalTime startTime = store.getOpeningHouresStart();
		LocalTime endTime = store.getOpeningHouresEnd();
				
		List<String> timeList = new ArrayList<>();
		LocalTime time = startTime;
		while (!time.isAfter(endTime)) {
			timeList.add(time.toString());
		    time = time.plusMinutes(30);
		}
		return timeList;
	}
}
















CREATE TABLE IF NOT EXISTS categories(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50) NOT NULL
);


CREATE TABLE IF NOT EXISTS stores(
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(50) NOT NULL,  
   image_name VARCHAR(255) NOT NULL,
   description VARCHAR(255) NOT NULL,
   price INT NOT NULL,
   openinghoures_start TIME  NOT NULL,
   openinghoures_end TIME  NOT NULL,
   holiday INT NOT NULL,
   address VARCHAR(255) NOT NULL,
   phone_number VARCHAR(50) NOT NULL,
   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS store_categories(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	store_id INT NOT NULL,
	category_id INT NOT NULL,
	FOREIGN KEY (store_id) REFERENCES stores (id),
	FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE IF NOT EXISTS roles (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS users(
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(50) NOT NULL,  
   furigana VARCHAR(255) NOT NULL,
   postal_code VARCHAR(50) NOT NULL,
   address VARCHAR(255) NOT NULL,
   phone_number VARCHAR(50) NOT NULL,
   email VARCHAR(255) NOT NULL,
   password VARCHAR(255) NOT NULL,
   role_id INT NOT NULL, 
   enabled BOOLEAN NOT NULL,
   subscription_id VARCHAR(255),
   customer_id VARCHAR(255),
   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE IF NOT EXISTS verification_tokens(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id  INT NOT NULL UNIQUE,
	token VARCHAR(255) NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   	FOREIGN KEY (user_id) REFERENCES users (id)
);


CREATE TABLE IF NOT EXISTS passwordreset_tokens(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id  INT NOT NULL,
	token VARCHAR(255) NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   	FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS favorites(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	store_id INT NOT NULL,
	user_id INT NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	FOREIGN KEY (store_id) REFERENCES stores (id),
	FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS reviews(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	store_id INT NOT NULL,
	user_id INT NOT NULL,
	evaluation INT NOT NULL,
	comment VARCHAR(255) NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	FOREIGN KEY (store_id) REFERENCES stores (id),
	FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS reservations(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	store_id INT NOT NULL,
	user_id INT NOT NULL,
	reservation_date DATE NOT NULL,
	reservation_time TIME NOT NULL,
	number_of_people INT NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	FOREIGN KEY (store_id) REFERENCES stores (id),
	FOREIGN KEY (user_id) REFERENCES users (id)
);



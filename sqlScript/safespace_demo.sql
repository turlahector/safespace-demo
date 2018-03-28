use safe_space;

/*
 * CREATION OF TABLES
 * 
 */

CREATE TABLE role (
	role_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(32)
);

CREATE TABLE user (
	user_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_name VARCHAR(128),
	first_name VARCHAR(128),
	last_name VARCHAR(128),
	email VARCHAR(128),
	password VARCHAR(128),
	role_id INT,
	enabled boolean DEFAULT FALSE,
	public_key VARCHAR(255),
	FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE,
	UNIQUE (public_key)
);

/*
 * INITIAL VALUES
 * 
 */

INSERT INTO role(name) 
SELECT 'USER' FROM role WHERE NOT EXISTS(SELECT 1 FROM role WHERE name ='USER')
UNION
SELECT 'ADMIN' FROM role WHERE NOT EXISTS(SELECT 1 FROM role WHERE name ='ADMIN');

INSERT INTO user (user_name, email, password,enabled,role_id) VALUES 
('guest', 'guest@outresource.com.au', 'mouse123',true,(SELECT role_id FROM role WHERE name = 'USER' ORDER BY role_id DESC LIMIT 1)),
('jeriel','mercadojerielf@gmail.com','mouse123',true,(SELECT role_id FROM role WHERE name = 'USER' ORDER BY role_id DESC LIMIT 1)),
('jerielAdmin','mercadojerielf@gmail.com','mouse123',true,(SELECT role_id FROM role WHERE name = 'ADMIN' ORDER BY role_id DESC LIMIT 1));


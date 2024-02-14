-- This SQL creates the necessary tables and inserts example data and test data into them
-- The name of the database should be called "groupdatabase"

CREATE TABLE doctors (
  DoctorID INT PRIMARY KEY NOT NULL,
  Forename VARCHAR(45) NOT NULL,
  Surname VARCHAR(45) NOT NULL
);
CREATE TABLE accounts (
  Forename VARCHAR(45) NOT NULL,
  Surname VARCHAR(45) NOT NULL,
  Email VARCHAR(100) PRIMARY KEY NOT NULL,
  Password VARCHAR(45) NOT NULL,
  isDoctor VARCHAR(3) NOT NULL,
  DoctorID INT,
  CONSTRAINT fk_account_doctorid FOREIGN KEY (DoctorID) REFERENCES doctors(DoctorID)
);
CREATE TABLE booking (
  BookingID INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  Email VARCHAR(255) NOT NULL,
  DoctorID INT NOT NULL,
  Date DATE NOT NULL,
  Time TIME NOT NULL,
  Notes VARCHAR(255),
  Prescriptions VARCHAR(255),
  CONSTRAINT fk_booking_email FOREIGN KEY (Email) REFERENCES accounts(Email),
  CONSTRAINT fk_booking_doctorid FOREIGN KEY (DoctorID) REFERENCES doctors(DoctorID)
);
CREATE TABLE userlogs (
  LogID INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  Email VARCHAR(255) NOT NULL,
  LogDate DATE NOT NULL,
  LogTime TIME NOT NULL,
  FeatureAccessed VARCHAR(255) NOT NULL,
  CONSTRAINT fk_userlogs_email FOREIGN KEY (Email) REFERENCES accounts(Email)
);
CREATE TABLE `messages` (
  `msgID` INT NOT NULL AUTO_INCREMENT,
  `recipient` VARCHAR(255) NOT NULL,
  `msg` VARCHAR(255) NOT NULL,
  `sender` VARCHAR(255) NULL,
  PRIMARY KEY (`msgID`));

-- insert example patient accounts
INSERT INTO accounts (Forename, Surname, Email, Password, isDoctor) 
VALUES 
('Alice', 'Smith', 'alice.smith@example.com', 'mypassword', 'NO'),
('Bob', 'Johnson', 'bob.johnson@example.com', 'supersecret', 'NO'),
('Charlie', 'Davis', 'charlie.davis@example.com', '12345678', 'NO');

-- Insert doctors into doctor table
INSERT INTO doctors (DoctorID, Forename, Surname) 
VALUES 
(1, 'Dr. Emma', 'Wilson'),
(2, 'Dr. Jack', 'Thomas'),
(3, 'Dr. Sarah', 'Chen');

-- Insert 3 doctors
INSERT INTO accounts (Forename, Surname, Email, Password, isDoctor, DoctorID) 
VALUES 
('Dr. Emma', 'Wilson', 'emma.wilson@nhs.co.uk', 'password', 'YES', '1'),
('Dr. Jack', 'Thomas', 'jack.thomas@nhs.co.uk', 'password', 'YES', '2'),
('Dr. Sarah', 'Chen', 'sarah.chen@nhs.co.uk', 'password', 'YES', '3'); 
-- Insert welcome messages

INSERT INTO messages (recipient, msg, sender) VALUES 
('alice.smith@example.com', 'Welcome new user!', 'System'),
('bob.johnson@example.com', 'Welcome new user!', 'System'),
('charlie.davis@example.com', 'Welcome new user!', 'System'),
('emma.wilson@nhs.co.uk', 'Welcome Doctor!', 'System'),
('jack.thomas@nhs.co.uk', 'Welcome Doctor!', 'System'),
('sarah.chen@nhs.co.uk', 'Welcome Doctor!', 'System'); 

-- Insert test account for tests
INSERT INTO accounts  (Forename, Surname, Email, Password, isDoctor, DoctorID) 
VALUES ("u1", "u1", "u1@gmail.com", "password", "no", "1");
INSERT INTO messages (recipient, msg, sender) VALUES 
('u1@gmail.com', 'Welcome new user!', 'System'),
-- Adds a booking at a date thats already happened, used to test visit details
INSERT INTO Booking (Email, DoctorID, Date, Time, Notes, Prescriptions) 
VALUES ("u1@gmail.com", "1", "2023-01-30", "12:00", "Patient had mild flu", "No medication needed");

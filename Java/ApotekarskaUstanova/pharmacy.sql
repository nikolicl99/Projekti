-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 30, 2025 at 12:29 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `pharmacy`
--

-- --------------------------------------------------------

--
-- Table structure for table `address`
--

CREATE TABLE `address` (
  `Id` int(11) NOT NULL,
  `Town` int(11) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `number` varchar(255) DEFAULT NULL,
  `apt_number` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `address`
--

INSERT INTO `address` (`Id`, `Town`, `address`, `number`, `apt_number`) VALUES
(1, 1, 'Glavna ulica', '12', '0'),
(2, 2, 'Ulica pored reke', '15', '0'),
(3, 3, 'Ulica zalaska sunca', '8', '0'),
(4, 4, 'Zeleni bulevar', '24', '0'),
(5, 5, 'Park Lane', '19', '0'),
(6, 6, 'Hrastova ulica', '11', '0'),
(7, 7, 'Ulica vile', '33', '0'),
(8, 8, 'Javorova avenija', '27', '0'),
(9, 9, 'Pogled na jezero', '10', '0'),
(10, 10, 'Bregovita ulica', '22', '0'),
(11, 11, 'Obalu puta', '50', '0'),
(12, 1, 'asdas', 'asdas', ''),
(13, 4, 'kkll', 'kkl', '0'),
(14, 1, 'asdasd', 'asdas', '0'),
(15, 1, 'asdasd', 'asdasd', '0'),
(16, 1, 'asdas', 'asdasd', '0'),
(17, 1, 'asda', 'asdas', '0'),
(18, 26, 'asdas', 'sdasd', '0'),
(19, 31, 'asdas', 'asdasd', '0'),
(20, 46, 'asdasd', 'sdasd', '0'),
(21, 31, 'asdasd', 'asdasd', '0'),
(22, 11, 'asdkoaks', 'asdkao', 'ooko'),
(23, 21, 'asdasd', 'asdas', 'asda'),
(24, 1, 'asdasd', 'asdasd', '0'),
(25, 18, 'Bojana Krstica', '21', '1'),
(26, 2, 'asdas', '123', '12'),
(27, 1, 'asda', 'as', '12'),
(28, 1, 'asd', 'asd', 'asd'),
(29, 1, 'asd', 'asd', 'asd'),
(30, 1, 'asd', 'asd', '0'),
(31, 1, 'asda', 'asd', '0'),
(32, 1, 'asd', 'asd', '0'),
(33, 1, 'asd', 'asd', '0'),
(34, 1, 'asd', 'asd', '0'),
(35, 1, 'asd', 'asd', '0'),
(36, 8, 'Nova ulica', '32', '2');

-- --------------------------------------------------------

--
-- Table structure for table `cards`
--

CREATE TABLE `cards` (
  `id` int(11) NOT NULL,
  `card_number` varchar(16) NOT NULL,
  `pin` varchar(4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cards`
--

INSERT INTO `cards` (`id`, `card_number`, `pin`) VALUES
(1, '1234567812345678', '1234'),
(2, '8765432187654321', '5678'),
(3, '1111222233334444', '0000'),
(4, '5555666677778888', '9999'),
(5, '9999888877776666', '4321');

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `name`, `description`) VALUES
(1, 'Lekovi', NULL),
(2, 'Proizvodi za higijenu', NULL),
(3, 'Sredstva za čišćenje', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `employees`
--

CREATE TABLE `employees` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `surname` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `type` int(11) NOT NULL,
  `address` int(11) NOT NULL,
  `employed` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `employees`
--

INSERT INTO `employees` (`id`, `name`, `surname`, `email`, `password`, `mobile`, `type`, `address`, `employed`) VALUES
(1, 'Marko', 'Marković', 'marko@email.com', '$2a$10$CpuqCDWmLAtzyTRsDdgJG.969.67/jJwxrIomKpqmBHu1GDebtn9O', '0641234567', 2, 1, 1),
(2, 'Jovana', 'Jovanović', 'jovana@email.com', '$2a$10$CpuqCDWmLAtzyTRsDdgJG.969.67/jJwxrIomKpqmBHuasdmasd12', '0647654321', 2, 2, 0),
(6, 'Jelena', 'Nikolić', 'jelena.niasdkolic@example.com', '$2a$10$EVIgbIGBtAYsAquSOhAw3Os.uyUalNiDgqMZo5/ok9lKnMXpa0k0W', '+381661234567', 1, 3, 1);

-- --------------------------------------------------------

--
-- Table structure for table `locations`
--

CREATE TABLE `locations` (
  `location_id` int(11) NOT NULL,
  `section` varchar(255) DEFAULT NULL,
  `shelf` varchar(255) DEFAULT NULL,
  `row` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `locations`
--

INSERT INTO `locations` (`location_id`, `section`, `shelf`, `row`, `description`) VALUES
(1, 'Section A', 'Shelf 1', 'Row 1', 'Pain Relief Medications'),
(2, 'Section A', 'Shelf 1', 'Row 2', 'Pain Relief Medications'),
(3, 'Section A', 'Shelf 2', 'Row 1', 'Cold and Flu Medications'),
(4, 'Section A', 'Shelf 2', 'Row 2', 'Cold and Flu Medications'),
(5, 'Section B', 'Shelf 1', 'Row 1', 'Antibiotics'),
(6, 'Section B', 'Shelf 1', 'Row 2', 'Antibiotics'),
(7, 'Section B', 'Shelf 2', 'Row 1', 'Antihistamines'),
(8, 'Section B', 'Shelf 2', 'Row 2', 'Antihistamines'),
(9, 'Section C', 'Shelf 1', 'Row 1', 'Vitamins and Supplements'),
(10, 'Section C', 'Shelf 1', 'Row 2', 'Vitamins and Supplements'),
(11, 'Section C', 'Shelf 2', 'Row 1', 'Digestive Health'),
(12, 'Section C', 'Shelf 2', 'Row 2', 'Digestive Health'),
(13, 'Section D', 'Shelf 1', 'Row 1', 'Skin Care Products'),
(14, 'Section D', 'Shelf 1', 'Row 2', 'Skin Care Products'),
(15, 'Section D', 'Shelf 2', 'Row 1', 'First Aid Supplies'),
(16, 'Section D', 'Shelf 2', 'Row 2', 'First Aid Supplies'),
(17, 'Section E', 'Shelf 1', 'Row 1', 'Baby Care Products'),
(18, 'Section E', 'Shelf 1', 'Row 2', 'Baby Care Products'),
(19, 'Section E', 'Shelf 2', 'Row 1', 'Elderly Care Products'),
(20, 'Section E', 'Shelf 2', 'Row 2', 'Elderly Care Products');

-- --------------------------------------------------------

--
-- Table structure for table `municipality`
--

CREATE TABLE `municipality` (
  `Id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `municipality`
--

INSERT INTO `municipality` (`Id`, `name`) VALUES
(1, 'Beograd'),
(2, 'Novi Sad'),
(3, 'Niš'),
(4, 'Kragujevac'),
(5, 'Subotica'),
(6, 'Čačak'),
(7, 'Zrenjanin'),
(8, 'Leskovac'),
(9, 'Pančevo'),
(10, 'Sombor');

-- --------------------------------------------------------

--
-- Table structure for table `order`
--

CREATE TABLE `order` (
  `id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `selected_date` date NOT NULL,
  `selected_time` time NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `order`
--

INSERT INTO `order` (`id`, `supplier_id`, `selected_date`, `selected_time`) VALUES
(1, 3, '2025-03-16', '21:09:44'),
(2, 3, '2025-03-16', '21:13:26'),
(3, 3, '2025-03-16', '21:15:57'),
(4, 3, '2025-03-16', '21:19:26'),
(5, 4, '2025-03-17', '03:33:15'),
(6, 4, '2025-03-17', '03:34:33'),
(7, 3, '2025-03-17', '03:36:01'),
(8, 4, '2025-03-17', '03:37:38'),
(9, 3, '2025-03-17', '03:40:23');

-- --------------------------------------------------------

--
-- Table structure for table `order_items`
--

CREATE TABLE `order_items` (
  `id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `order_items`
--

INSERT INTO `order_items` (`id`, `order_id`, `product_id`, `quantity`) VALUES
(1, 4, 1, 2),
(2, 4, 2, 3),
(3, 5, 2, 2),
(4, 5, 1, 774),
(5, 6, 2, 2),
(6, 6, 1, 774),
(7, 7, 1, 2),
(8, 7, 2, 1),
(9, 8, 2, 2),
(10, 8, 1, 774),
(11, 9, 2, 2),
(12, 9, 1, 775);

-- --------------------------------------------------------

--
-- Table structure for table `prescriptions`
--

CREATE TABLE `prescriptions` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `issue_date` date NOT NULL,
  `type` varchar(255) NOT NULL,
  `obtained` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `prescriptions`
--

INSERT INTO `prescriptions` (`id`, `user_id`, `issue_date`, `type`, `obtained`) VALUES
(1, 1, '2025-02-01', 'e-prescription', 1),
(2, 2, '2025-02-05', 'e-prescription', 1),
(3, 3, '2025-02-10', 'paper', 0);

-- --------------------------------------------------------

--
-- Table structure for table `prescription_items`
--

CREATE TABLE `prescription_items` (
  `id` int(11) NOT NULL,
  `prescription_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `discount` int(2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `prescription_items`
--

INSERT INTO `prescription_items` (`id`, `prescription_id`, `product_id`, `quantity`, `discount`) VALUES
(1, 1, 1, 2, 40),
(2, 1, 2, 1, 100),
(3, 2, 1, 1, 40),
(4, 3, 2, 3, 50);

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `dosage` int(11) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `subcategory_id` int(11) DEFAULT NULL,
  `purchase_price` double NOT NULL,
  `selling_price` double NOT NULL,
  `stock_quantity` int(11) DEFAULT NULL,
  `min_quantity` int(11) DEFAULT NULL,
  `max_quantity` int(11) DEFAULT NULL,
  `status_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`id`, `name`, `dosage`, `description`, `subcategory_id`, `purchase_price`, `selling_price`, `stock_quantity`, `min_quantity`, `max_quantity`, `status_id`) VALUES
(1, 'Paracetamol', 400, 'Analgetik', 2, 10, 15, 100, 1000, 200, 1),
(2, 'Sapun', 0, 'Proizvod za higijenu', 3, 5, 7, 50, 5, 100, 1),
(3, 'Ibuprofen', 200, 'Protiv bolova i upala', 2, 8.5, 12, 150, 50, 300, 1),
(4, 'Amoksicilin', 500, 'Antibiotik širokog spektra', 1, 15, 22.5, 80, 20, 200, 1),
(5, 'Vitamin C', 1000, 'Dodatak ishrani', 4, 5, 8, 200, 100, 500, 1),
(6, 'Brufen', 400, 'Protiv bolova i groznice', 2, 9, 14, 120, 30, 250, 1),
(7, 'Aspirin', 500, 'Protiv bolova i protiv groznice', 2, 7, 10.5, 180, 40, 350, 1),
(8, 'Cetirizin', 10, 'Antihistaminik', 4, 6.5, 9.8, 90, 20, 150, 1),
(9, 'Omeprazol', 20, 'Za smanjenje želučane kiseline', 4, 12, 18, 70, 15, 120, 1),
(10, 'Diazepam', 5, 'Anksiolitik', 1, 18, 25, 40, 10, 80, 1),
(11, 'Loratadin', 10, 'Antialergijski lek', 4, 7.5, 11, 110, 25, 200, 1),
(12, 'Metformin', 850, 'Antidijabetik', 1, 9.5, 14.5, 60, 15, 100, 1);

-- --------------------------------------------------------

--
-- Table structure for table `product_batches`
--

CREATE TABLE `product_batches` (
  `id` bigint(20) NOT NULL,
  `product_id` int(20) DEFAULT NULL,
  `ean13` bigint(20) NOT NULL,
  `batch_number` varchar(255) NOT NULL,
  `expiration_date` date NOT NULL,
  `quantity_received` int(11) DEFAULT NULL,
  `quantity_remaining` int(11) NOT NULL,
  `shipment_id` bigint(11) DEFAULT NULL,
  `location` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `product_batches`
--

INSERT INTO `product_batches` (`id`, `product_id`, `ean13`, `batch_number`, `expiration_date`, `quantity_received`, `quantity_remaining`, `shipment_id`, `location`) VALUES
(1, 1, 8600100118197, 'BATCH001', '2026-01-01', 50, 50, 1, 2),
(2, 2, 8600200168436, 'BATCH001', '2026-02-01', 25, 25, 1, 5),
(3, 1, 8600100252778, 'BATCH002', '2026-03-01', 50, 50, 2, 2),
(8, 1, 1234567890123, 'BATCH001', '2025-12-31', 100, 100, 1, 5),
(9, 1, 1234567890123, 'BATCH001', '2025-12-31', 100, 0, 1, 5),
(10, 1, 8600100314303, 'BATCH003', '2025-03-15', 2, 2, 21, 1),
(11, 2, 8600200237891, 'BATCH002', '2025-03-15', 3, 3, 21, 1),
(12, 2, 8600200377959, 'BATCH003', '2025-03-15', 3, 3, 22, 1),
(13, 1, 8600100432491, 'BATCH004', '2025-03-15', 4, 4, 22, 1),
(14, 2, 8600200489590, 'BATCH004', '2025-03-15', 2, 2, 23, 1),
(15, 1, 8600100528886, 'BATCH005', '2025-03-15', 3, 3, 23, 1),
(16, 2, 8600200557490, 'BATCH005', '2025-03-15', 2, 2, 24, 1),
(17, 1, 8600100651504, 'BATCH006', '2025-03-15', 4, 4, 24, 1),
(18, 2, 8600200658413, 'BATCH006', '2025-03-15', 2, 2, 25, 1),
(19, 1, 8600100717414, 'BATCH007', '2025-03-15', 3, 3, 25, 1),
(20, 2, 8600200735765, 'BATCH007', '2025-03-15', 2, 2, 26, 1),
(21, 1, 8600100879835, 'BATCH008', '2025-03-15', 3, 3, 26, 1),
(22, 2, 8600200850706, 'BATCH008', '2025-03-15', 2, 2, 27, 1),
(23, 1, 8600100942769, 'BATCH009', '2025-03-15', 3, 3, 27, 1),
(24, 2, 8600200963022, 'BATCH009', '2025-03-15', 2, 2, 28, 1),
(25, 1, 8600101068581, 'BATCH010', '2025-03-15', 3, 3, 28, 1),
(26, 2, 8600201096812, 'BATCH010', '2025-03-17', 2, 2, 29, 1),
(27, 1, 8600101106010, 'BATCH011', '2025-03-26', 5, 5, 29, 1),
(28, 2, 8600201128348, 'BATCH011', '2025-03-28', 1, 1, 29, 1),
(29, 2, 8600201238942, 'BATCH012', '2025-03-28', 2, 2, 30, 13),
(30, 1, 8600101285262, 'BATCH012', '2025-03-28', 2, 2, 30, 1),
(31, 2, 8600201391850, 'BATCH013', '2025-03-21', 2, 2, 31, 1),
(32, 1, 8600101334308, 'BATCH013', '2025-03-21', 4, 4, 31, 1);

-- --------------------------------------------------------

--
-- Table structure for table `salaries`
--

CREATE TABLE `salaries` (
  `id` int(11) NOT NULL,
  `employee_id` int(11) NOT NULL,
  `amount` double NOT NULL,
  `payment_date` date NOT NULL,
  `month` varchar(255) NOT NULL,
  `year` int(11) NOT NULL,
  `created_at` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `salaries`
--

INSERT INTO `salaries` (`id`, `employee_id`, `amount`, `payment_date`, `month`, `year`, `created_at`) VALUES
(1, 2, 525, '2025-03-16', 'MARCH', 2025, '2025-03-16'),
(2, 1, 2315, '2025-03-17', 'MARCH', 2025, '2025-03-17');

-- --------------------------------------------------------

--
-- Table structure for table `salary_details`
--

CREATE TABLE `salary_details` (
  `id` int(11) NOT NULL,
  `salary_id` int(11) NOT NULL,
  `hours_worked` double NOT NULL,
  `overtime_hours` double NOT NULL,
  `bonus` double NOT NULL,
  `deductions` double NOT NULL,
  `notes` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `salary_details`
--

INSERT INTO `salary_details` (`id`, `salary_id`, `hours_worked`, `overtime_hours`, `bonus`, `deductions`, `notes`) VALUES
(1, 1, 21, 21, 21, 21, 'asd'),
(2, 2, 200, 20, 15, 0, '');

-- --------------------------------------------------------

--
-- Table structure for table `sales`
--

CREATE TABLE `sales` (
  `id` int(11) NOT NULL,
  `total_price` double NOT NULL,
  `receipt_change` double DEFAULT NULL,
  `transaction_date` date NOT NULL,
  `transaction_time` time NOT NULL,
  `cashier_id` int(11) NOT NULL,
  `payment_type` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sales`
--

INSERT INTO `sales` (`id`, `total_price`, `receipt_change`, `transaction_date`, `transaction_time`, `cashier_id`, `payment_type`) VALUES
(1, 18, 0, '2025-03-08', '00:00:00', 1, 'Card'),
(2, 150.75, 0, '2025-03-11', '14:30:00', 1, 'CASH'),
(3, 18, 4, '2025-03-12', '00:09:20', 1, 'Cash'),
(4, 18, 5, '2025-03-12', '00:53:41', 1, 'Cash'),
(5, 18, 3, '2025-03-12', '01:01:21', 1, 'Cash'),
(6, 18, 3, '2025-03-12', '01:08:50', 1, 'Cash'),
(7, 18, 1, '2025-03-12', '01:26:11', 1, 'Cash'),
(8, 18, 1, '2025-03-12', '02:46:19', 1, 'Cash'),
(9, 18, 4, '2025-03-12', '02:55:46', 1, 'Cash'),
(10, 18, 3, '2025-03-12', '03:01:32', 1, 'Cash'),
(11, 18.71, 2.29, '2025-03-17', '04:11:30', 1, 'Cash'),
(12, 35.96, 0.04, '2025-03-17', '04:18:03', 1, 'Cash'),
(13, 6.86, 0, '2025-03-17', '04:19:00', 1, 'Card'),
(14, 6.86, 0.14, '2025-03-17', '04:20:26', 1, 'Cash'),
(15, 48, 2, '2025-03-17', '04:23:34', 1, 'Cash'),
(16, 48, 2, '2025-03-17', '04:32:24', 1, 'Cash'),
(17, 29.25, 0, '2025-03-17', '04:33:33', 1, 'Card'),
(18, 48, 2, '2025-03-17', '04:37:18', 1, 'Cash'),
(19, 25.45, 0, '2025-03-17', '04:38:13', 1, 'Card'),
(20, 48, 2, '2025-03-17', '04:43:08', 1, 'Cash'),
(21, 22.45, 0, '2025-03-17', '04:44:00', 1, 'Card'),
(22, 48, 4, '2025-03-17', '04:50:02', 1, 'Cash'),
(23, 22.45, 0, '2025-03-17', '04:50:35', 1, 'Card');

-- --------------------------------------------------------

--
-- Table structure for table `sales_items`
--

CREATE TABLE `sales_items` (
  `id` int(11) NOT NULL,
  `sales_id` int(11) NOT NULL,
  `product_id` bigint(11) NOT NULL,
  `receipt_type` varchar(255) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `total_price` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sales_items`
--

INSERT INTO `sales_items` (`id`, `sales_id`, `product_id`, `receipt_type`, `quantity`, `total_price`) VALUES
(1, 1, 1, 'E-Recept', 2, 18),
(2, 1, 2, 'E-Recept', 1, 0),
(3, 6, 1, 'E-Recept', 2, 18),
(4, 6, 2, 'E-Recept', 1, 0),
(5, 7, 1, 'E-Recept', 2, 18),
(6, 7, 2, 'E-Recept', 1, 0),
(7, 9, 1, 'E-Recept', 2, 18),
(8, 9, 2, 'E-Recept', 1, 0),
(9, 10, 1, 'E-Recept', 2, 18),
(10, 10, 2, 'E-Recept', 1, 0),
(11, 11, 1, 'Papirni recept', 1, 11.85),
(12, 11, 2, 'Papirni recept', 1, 6.86),
(13, 12, 2, 'Papirni recept', 1, 6.86),
(14, 12, 1, 'Papirni recept', 2, 29.1),
(15, 13, 2, 'Papirni recept', 1, 6.86),
(16, 14, 2, 'Papirni recept', 1, 6.86),
(17, 15, 1, 'E-Recept', 2, 18),
(18, 15, 2, 'E-Recept', 1, 0),
(19, 15, 1, '-', 2, 30),
(20, 16, 1, 'E-Recept', 2, 18),
(21, 16, 2, 'E-Recept', 1, 0),
(22, 16, 1, '-', 2, 30),
(23, 17, 1, 'Papirni recept', 2, 24),
(24, 17, 2, 'Papirni recept', 1, 5.25),
(25, 18, 1, 'E-Recept', 2, 18),
(26, 18, 2, 'E-Recept', 1, 0),
(27, 18, 1, '-', 2, 30),
(28, 19, 2, 'Papirni recept', 2, 11.2),
(29, 19, 1, 'Papirni recept', 1, 14.25),
(30, 20, 1, 'E-Recept', 2, 18),
(31, 20, 2, 'E-Recept', 1, 0),
(32, 20, 1, '-', 2, 30),
(33, 21, 2, 'Papirni recept', 2, 11.2),
(34, 21, 1, 'Papirni recept', 1, 11.25),
(35, 22, 1, 'E-Recept', 2, 18),
(36, 22, 2, 'E-Recept', 1, 0),
(37, 22, 1, '-', 2, 30),
(38, 23, 2, 'Papirni recept', 2, 11.2),
(39, 23, 1, 'Papirni recept', 1, 11.25);

-- --------------------------------------------------------

--
-- Table structure for table `shipments`
--

CREATE TABLE `shipments` (
  `id` bigint(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `arrival_date` date NOT NULL,
  `arrival_time` time NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `shipments`
--

INSERT INTO `shipments` (`id`, `supplier_id`, `arrival_date`, `arrival_time`) VALUES
(1, 3, '2025-01-01', '10:00:00'),
(2, 4, '2025-02-01', '12:30:00'),
(3, 3, '2025-03-15', '00:40:28'),
(4, 3, '2025-03-15', '00:47:44'),
(5, 3, '2025-03-15', '00:58:14'),
(6, 3, '2025-03-15', '01:03:33'),
(7, 3, '2025-03-15', '01:07:36'),
(8, 3, '2025-03-15', '17:34:58'),
(9, 3, '2025-03-15', '18:49:05'),
(10, 3, '2025-03-15', '19:07:59'),
(11, 3, '2025-03-15', '19:41:47'),
(12, 3, '2025-03-15', '19:46:10'),
(13, 3, '2025-03-15', '19:48:20'),
(14, 3, '2025-03-15', '19:49:49'),
(15, 3, '2025-03-15', '19:51:48'),
(16, 3, '2025-03-15', '20:20:14'),
(17, 3, '2025-03-15', '20:14:55'),
(18, 3, '2025-03-15', '20:45:56'),
(19, 3, '2025-03-15', '21:50:21'),
(20, 3, '2025-03-15', '21:56:47'),
(21, 3, '2025-03-15', '21:59:15'),
(22, 3, '2025-03-15', '22:03:26'),
(23, 3, '2025-03-15', '22:40:11'),
(24, 3, '2025-03-15', '22:42:51'),
(25, 3, '2025-03-15', '22:44:03'),
(26, 3, '2025-03-15', '23:14:48'),
(27, 3, '2025-03-15', '23:15:37'),
(28, 3, '2025-03-15', '23:25:51'),
(29, 4, '2025-03-17', '02:01:30'),
(30, 4, '2025-03-17', '02:11:10'),
(31, 6, '2025-03-21', '21:01:30');

-- --------------------------------------------------------

--
-- Table structure for table `shipment_items`
--

CREATE TABLE `shipment_items` (
  `id` bigint(20) NOT NULL,
  `shipment_id` bigint(11) NOT NULL,
  `product_id` bigint(11) NOT NULL,
  `quantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `shipment_items`
--

INSERT INTO `shipment_items` (`id`, `shipment_id`, `product_id`, `quantity`) VALUES
(1, 1, 1, 50),
(2, 1, 2, 25),
(3, 2, 1, 50),
(6, 27, 22, 2),
(7, 27, 23, 3),
(8, 28, 24, 2),
(9, 28, 25, 3),
(10, 29, 26, 2),
(11, 29, 27, 5),
(12, 29, 28, 1),
(13, 30, 29, 2),
(14, 30, 30, 2),
(15, 31, 31, 2),
(16, 31, 32, 4);

-- --------------------------------------------------------

--
-- Table structure for table `status`
--

CREATE TABLE `status` (
  `id` int(11) NOT NULL,
  `status_name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `status`
--

INSERT INTO `status` (`id`, `status_name`) VALUES
(1, 'active'),
(2, 'inactive');

-- --------------------------------------------------------

--
-- Table structure for table `subcategories`
--

CREATE TABLE `subcategories` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `category_id` int(11) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `subcategories`
--

INSERT INTO `subcategories` (`id`, `name`, `category_id`, `description`) VALUES
(1, 'Antibiotici', 1, NULL),
(2, 'Analgetici', 1, NULL),
(3, 'Sapun', 2, NULL),
(4, 'Dezinfekcija', 3, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `suppliers`
--

CREATE TABLE `suppliers` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `address` int(11) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `suppliers`
--

INSERT INTO `suppliers` (`id`, `name`, `address`, `email`, `phone`) VALUES
(3, 'Tech Solutions', 7, 'contact@techsolutions.rs', '011/123-4567'),
(4, 'Market Plus', 8, 'info@marketplus.rs', '021/987-6543'),
(5, 'Office Express', 9, 'sales@officeexpress.rs', '018/555-7777'),
(6, 'Auto Parts DOO', 10, 'support@autoparts.rs', '023/333-2222'),
(7, 'Green Energy', 11, 'hello@greenenergy.rs', '024/999-8888'),
(15, 'Novi Dobavljac', 25, 'Dobavljac@email.com', '066999222');

-- --------------------------------------------------------

--
-- Table structure for table `town`
--

CREATE TABLE `town` (
  `Id` int(11) NOT NULL,
  `Municipality` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `Postal_Code` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `town`
--

INSERT INTO `town` (`Id`, `Municipality`, `name`, `Postal_Code`) VALUES
(1, 1, 'Zemun', 11000),
(2, 1, 'Novi Beograd', 11070),
(3, 1, 'Stari Grad', 11000),
(4, 1, 'Voždovac', 11020),
(5, 1, 'Čukarica', 11030),
(6, 2, 'Petrovaradin', 21000),
(7, 2, 'Futog', 21101),
(8, 2, 'Sremska Kamenica', 21204),
(9, 2, 'Liman', 21000),
(10, 2, 'Detelinara', 21000),
(11, 3, 'Niška Banja', 18000),
(12, 3, 'Pantelej', 18000),
(13, 3, 'Medijana', 18000),
(14, 3, 'Palilula', 18000),
(15, 3, 'Crveni Krst', 18000),
(16, 4, 'Stanovo', 34000),
(17, 4, 'Pivara', 34000),
(18, 4, 'Aerodrom', 34000),
(19, 4, 'Bresnica', 34000),
(20, 4, 'Vašarište', 34000),
(21, 5, 'Palić', 24000),
(22, 5, 'Bajmok', 24000),
(23, 5, 'Kelebija', 24000),
(24, 5, 'Čantavir', 24000),
(25, 5, 'Bački Vinogradi', 24000),
(26, 6, 'Ljubić', 32000),
(27, 6, 'Konjevići', 32000),
(28, 6, 'Prislonica', 32000),
(29, 6, 'Preljina', 32000),
(30, 6, 'Banjica', 32000),
(31, 7, 'Bagljaš', 23000),
(32, 7, 'Mužlja', 23000),
(33, 7, 'Ečka', 23000),
(34, 7, 'Klek', 23000),
(35, 7, 'Aradac', 23000),
(36, 8, 'Dubočica', 16000),
(37, 8, 'Bobište', 16000),
(38, 8, 'Moša Pijade', 16000),
(39, 8, 'Hisar', 16000),
(40, 8, 'Zele Veljković', 16000),
(41, 9, 'Mladost', 26000),
(42, 9, 'Strelište', 26000),
(43, 9, 'Vojlovica', 26000),
(44, 9, 'Dolovo', 26000),
(45, 9, 'Omoljica', 26000),
(46, 10, 'Stanišić', 25000),
(47, 10, 'Bezdan', 25000),
(48, 10, 'Gakovo', 25000),
(49, 10, 'Kljajićevo', 25000),
(50, 10, 'Aleksa Šantić', 25000);

-- --------------------------------------------------------

--
-- Table structure for table `type_of_employee`
--

CREATE TABLE `type_of_employee` (
  `ID` int(11) NOT NULL,
  `Type_Name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `type_of_employee`
--

INSERT INTO `type_of_employee` (`ID`, `Type_Name`) VALUES
(1, 'Admin'),
(2, 'Farmaceut'),
(4, 'Logistika');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `health_card_number` varchar(255) NOT NULL,
  `date_of_birth` datetime(6) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `first_name`, `last_name`, `health_card_number`, `date_of_birth`, `phone`, `email`) VALUES
(1, 'Marko', 'Marković', 'HC123456', '1985-05-10 00:00:00.000000', '0612345678', 'marko@example.com'),
(2, 'Jovana', 'Jovanović', 'HC654321', '1990-08-15 00:00:00.000000', '0623456789', 'jovana@example.com'),
(3, 'Petar', 'Petrović', 'HC789012', '1982-03-20 00:00:00.000000', '0634567890', 'petar@example.com');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `address`
--
ALTER TABLE `address`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `address_ibfk_1` (`Town`);

--
-- Indexes for table `cards`
--
ALTER TABLE `cards`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `card_number` (`card_number`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `employees`
--
ALTER TABLE `employees`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `type` (`type`),
  ADD KEY `employees_ibfk_1` (`address`);

--
-- Indexes for table `locations`
--
ALTER TABLE `locations`
  ADD PRIMARY KEY (`location_id`);

--
-- Indexes for table `municipality`
--
ALTER TABLE `municipality`
  ADD PRIMARY KEY (`Id`);

--
-- Indexes for table `order`
--
ALTER TABLE `order`
  ADD PRIMARY KEY (`id`),
  ADD KEY `order_ibfk_1` (`supplier_id`);

--
-- Indexes for table `order_items`
--
ALTER TABLE `order_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `order_items_ibfk_1` (`order_id`),
  ADD KEY `order_items_ibfk_2` (`product_id`);

--
-- Indexes for table `prescriptions`
--
ALTER TABLE `prescriptions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `prescriptions_ibfk_1` (`user_id`);

--
-- Indexes for table `prescription_items`
--
ALTER TABLE `prescription_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `prescription_items_ibfk_1` (`prescription_id`),
  ADD KEY `prescription_items_ibfk_2` (`product_id`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`),
  ADD KEY `products_ibfk_1` (`subcategory_id`),
  ADD KEY `products_ibfk_2` (`status_id`);

--
-- Indexes for table `product_batches`
--
ALTER TABLE `product_batches`
  ADD PRIMARY KEY (`id`),
  ADD KEY `product_batches_ibfk_1` (`product_id`),
  ADD KEY `product_batches_ibfk_2` (`shipment_id`),
  ADD KEY `product_batches_ibfk_3` (`location`);

--
-- Indexes for table `salaries`
--
ALTER TABLE `salaries`
  ADD PRIMARY KEY (`id`),
  ADD KEY `employee_id` (`employee_id`);

--
-- Indexes for table `salary_details`
--
ALTER TABLE `salary_details`
  ADD PRIMARY KEY (`id`),
  ADD KEY `salary_id` (`salary_id`);

--
-- Indexes for table `sales`
--
ALTER TABLE `sales`
  ADD PRIMARY KEY (`id`),
  ADD KEY `sales_ibfk_1` (`cashier_id`);

--
-- Indexes for table `sales_items`
--
ALTER TABLE `sales_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `sales_items_ibfk_1` (`sales_id`),
  ADD KEY `sales_items_ibfk_2` (`product_id`);

--
-- Indexes for table `shipments`
--
ALTER TABLE `shipments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `supplier_id` (`supplier_id`);

--
-- Indexes for table `shipment_items`
--
ALTER TABLE `shipment_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `shipment_items_ibfk_1` (`product_id`),
  ADD KEY `shipment_items_ibfk_2` (`shipment_id`);

--
-- Indexes for table `status`
--
ALTER TABLE `status`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `subcategories`
--
ALTER TABLE `subcategories`
  ADD PRIMARY KEY (`id`),
  ADD KEY `category_id` (`category_id`);

--
-- Indexes for table `suppliers`
--
ALTER TABLE `suppliers`
  ADD PRIMARY KEY (`id`),
  ADD KEY `suppliers_ibfk_1` (`address`);

--
-- Indexes for table `town`
--
ALTER TABLE `town`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `town_ibfk_1` (`Municipality`);

--
-- Indexes for table `type_of_employee`
--
ALTER TABLE `type_of_employee`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `health_card_number` (`health_card_number`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `address`
--
ALTER TABLE `address`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT for table `cards`
--
ALTER TABLE `cards`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `employees`
--
ALTER TABLE `employees`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `locations`
--
ALTER TABLE `locations`
  MODIFY `location_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `municipality`
--
ALTER TABLE `municipality`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `order`
--
ALTER TABLE `order`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `order_items`
--
ALTER TABLE `order_items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `prescriptions`
--
ALTER TABLE `prescriptions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `prescription_items`
--
ALTER TABLE `prescription_items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `product_batches`
--
ALTER TABLE `product_batches`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT for table `salaries`
--
ALTER TABLE `salaries`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `salary_details`
--
ALTER TABLE `salary_details`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `sales`
--
ALTER TABLE `sales`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `sales_items`
--
ALTER TABLE `sales_items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=40;

--
-- AUTO_INCREMENT for table `shipments`
--
ALTER TABLE `shipments`
  MODIFY `id` bigint(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT for table `shipment_items`
--
ALTER TABLE `shipment_items`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `status`
--
ALTER TABLE `status`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `subcategories`
--
ALTER TABLE `subcategories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `suppliers`
--
ALTER TABLE `suppliers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `town`
--
ALTER TABLE `town`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=51;

--
-- AUTO_INCREMENT for table `type_of_employee`
--
ALTER TABLE `type_of_employee`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `address`
--
ALTER TABLE `address`
  ADD CONSTRAINT `address_ibfk_1` FOREIGN KEY (`Town`) REFERENCES `town` (`Id`);

--
-- Constraints for table `employees`
--
ALTER TABLE `employees`
  ADD CONSTRAINT `employees_ibfk_1` FOREIGN KEY (`address`) REFERENCES `address` (`Id`),
  ADD CONSTRAINT `type` FOREIGN KEY (`type`) REFERENCES `type_of_employee` (`ID`) ON UPDATE CASCADE;

--
-- Constraints for table `order`
--
ALTER TABLE `order`
  ADD CONSTRAINT `order_ibfk_1` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`);

--
-- Constraints for table `order_items`
--
ALTER TABLE `order_items`
  ADD CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`),
  ADD CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

--
-- Constraints for table `prescriptions`
--
ALTER TABLE `prescriptions`
  ADD CONSTRAINT `prescriptions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `prescription_items`
--
ALTER TABLE `prescription_items`
  ADD CONSTRAINT `prescription_items_ibfk_1` FOREIGN KEY (`prescription_id`) REFERENCES `prescriptions` (`id`),
  ADD CONSTRAINT `prescription_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

--
-- Constraints for table `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `products_ibfk_1` FOREIGN KEY (`subcategory_id`) REFERENCES `subcategories` (`id`),
  ADD CONSTRAINT `products_ibfk_2` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`);

--
-- Constraints for table `product_batches`
--
ALTER TABLE `product_batches`
  ADD CONSTRAINT `product_batches_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  ADD CONSTRAINT `product_batches_ibfk_2` FOREIGN KEY (`shipment_id`) REFERENCES `shipments` (`id`),
  ADD CONSTRAINT `product_batches_ibfk_3` FOREIGN KEY (`location`) REFERENCES `locations` (`location_id`);

--
-- Constraints for table `salaries`
--
ALTER TABLE `salaries`
  ADD CONSTRAINT `salaries_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`);

--
-- Constraints for table `salary_details`
--
ALTER TABLE `salary_details`
  ADD CONSTRAINT `salary_details_ibfk_1` FOREIGN KEY (`salary_id`) REFERENCES `salaries` (`id`);

--
-- Constraints for table `sales`
--
ALTER TABLE `sales`
  ADD CONSTRAINT `sales_ibfk_1` FOREIGN KEY (`cashier_id`) REFERENCES `employees` (`id`);

--
-- Constraints for table `sales_items`
--
ALTER TABLE `sales_items`
  ADD CONSTRAINT `sales_items_ibfk_1` FOREIGN KEY (`sales_id`) REFERENCES `sales` (`id`),
  ADD CONSTRAINT `sales_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product_batches` (`id`);

--
-- Constraints for table `shipments`
--
ALTER TABLE `shipments`
  ADD CONSTRAINT `shipments_ibfk_1` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`);

--
-- Constraints for table `shipment_items`
--
ALTER TABLE `shipment_items`
  ADD CONSTRAINT `shipment_items_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product_batches` (`id`),
  ADD CONSTRAINT `shipment_items_ibfk_2` FOREIGN KEY (`shipment_id`) REFERENCES `shipments` (`id`);

--
-- Constraints for table `subcategories`
--
ALTER TABLE `subcategories`
  ADD CONSTRAINT `subcategories_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`);

--
-- Constraints for table `suppliers`
--
ALTER TABLE `suppliers`
  ADD CONSTRAINT `suppliers_ibfk_1` FOREIGN KEY (`address`) REFERENCES `address` (`Id`);

--
-- Constraints for table `town`
--
ALTER TABLE `town`
  ADD CONSTRAINT `town_ibfk_1` FOREIGN KEY (`Municipality`) REFERENCES `municipality` (`Id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

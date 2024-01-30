-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 29, 2023 at 02:47 PM
-- Server version: 10.4.27-MariaDB
-- PHP Version: 8.2.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `vinarija`
--

-- --------------------------------------------------------

--
-- Table structure for table `vinarija`
--

CREATE TABLE `vinarija` (
  `ID` int(3) NOT NULL,
  `Vrsta_Vina` text NOT NULL,
  `Vrsta_Ambalaze` text NOT NULL,
  `Zapremina_Ambalaze` int(250) NOT NULL,
  `Cena` int(250) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `vinarija`
--

INSERT INTO `vinarija` (`ID`, `Vrsta_Vina`, `Vrsta_Ambalaze`, `Zapremina_Ambalaze`, `Cena`) VALUES
(1, 'Crveno', 'Staklo', 250, 1000),
(2, 'Crveno', 'Limenka', 500, 500),
(3, 'Crveno', 'Plastika', 1000, 1250),
(4, 'Belo', 'Staklo', 1000, 2500),
(5, 'Belo', 'Limenka', 250, 500),
(6, 'Belo', 'Plastika', 1500, 2000),
(7, 'Roze', 'Staklo', 1500, 3000),
(8, 'Roze', 'Limenka', 250, 500),
(9, 'Roze', 'Plastika', 1000, 2000);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `vinarija`
--
ALTER TABLE `vinarija`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `vinarija`
--
ALTER TABLE `vinarija`
  MODIFY `ID` int(3) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

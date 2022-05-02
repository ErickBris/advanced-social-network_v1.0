-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Jun 01, 2015 at 10:45 PM
-- Server version: 5.6.21
-- PHP Version: 5.6.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `socnetwork`
--

-- --------------------------------------------------------

--
-- Table structure for table `sn_activation`
--

CREATE TABLE IF NOT EXISTS `sn_activation` (
`id` int(11) NOT NULL,
  `hash` varchar(100) NOT NULL,
  `userid` int(11) NOT NULL,
  `date` int(11) NOT NULL
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `sn_admins`
--

CREATE TABLE IF NOT EXISTS `sn_admins` (
`id` int(11) NOT NULL,
  `username` varchar(225) NOT NULL,
  `password` varchar(225) NOT NULL
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `sn_admins`
--

INSERT INTO `sn_admins` (`id`, `username`, `password`) VALUES
(2, 'admin', '21232f297a57a5a743894a0e4a801fc3');

-- --------------------------------------------------------

--
-- Table structure for table `sn_apilogs`
--

CREATE TABLE IF NOT EXISTS `sn_apilogs` (
`id` int(11) NOT NULL,
  `number` int(11) NOT NULL,
  `requestID` int(11) NOT NULL,
  `name` varchar(225) DEFAULT NULL,
  `email` varchar(225) DEFAULT NULL,
  `address` varchar(225) DEFAULT NULL,
  `uniqid` int(11) DEFAULT NULL,
  `password` int(11) DEFAULT NULL
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `sn_comments`
--

CREATE TABLE IF NOT EXISTS `sn_comments` (
`id` int(11) NOT NULL,
  `from` int(11) NOT NULL,
  `to` int(11) NOT NULL,
  `text` text NOT NULL,
  `date` int(11) NOT NULL
) ENGINE=MyISAM AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `sn_config`
--

CREATE TABLE IF NOT EXISTS `sn_config` (
`id` int(11) NOT NULL,
  `name` varchar(225) NOT NULL,
  `value` longtext NOT NULL,
  `for` varchar(225) NOT NULL
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `sn_config`
--

INSERT INTO `sn_config` (`id`, `name`, `value`, `for`) VALUES
(1, 'url', 'http://localhost/WebBackend/', 'site'),
(2, 'disclaimer', '<h3>Basic Terms</h3>\\r\\n<h6>You must be at least 14 years old to use the Service.</h6><br>\\r\\n<h6>You may not post violent, nude, partially nude, discriminatory, unlawful, infringing, hateful, pornographic or sexually suggestive photos or other content via the Service.</h6><br>\\r\\n<h6>You are responsible for any activity that occurs through your account</h6><br>\\r\\n<h6>You agree that you will not solicit, collect or use the login credentials of other users.</h6><br>\\r\\n<h6>You are responsible for keeping your password secret and secure.</h6><br>\\r\\n<h6>You must not defame, stalk, bully, abuse, harass, threaten, impersonate or intimidate people .</h6><br>\\r\\n<h3>Other</h3><br>\\r\\n<h6>You may not use the Service for .</h6><br>\\r\\n<h6>You are solely responsible for your conduct and any data</h6><br>', 'site'),
(3, 'emailactivation', '0', 'users'),
(4, 'site_name', 'SocNetwork', 'site'),
(5, 'googleApiConfig', 'put your google api key here', 'site');

-- --------------------------------------------------------

--
-- Table structure for table `sn_conversations`
--

CREATE TABLE IF NOT EXISTS `sn_conversations` (
`id` int(11) NOT NULL,
  `from` int(11) NOT NULL,
  `to` int(11) NOT NULL,
  `date` int(11) NOT NULL,
  `unseenCount` int(11) NOT NULL DEFAULT '0'
) ENGINE=MyISAM AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `sn_conversations`
--

INSERT INTO `sn_conversations` (`id`, `from`, `to`, `date`, `unseenCount`) VALUES
(23, 29, 28, 1433122693, 0);

-- --------------------------------------------------------

--
-- Table structure for table `sn_follows`
--

CREATE TABLE IF NOT EXISTS `sn_follows` (
`id` int(11) NOT NULL,
  `from` int(11) NOT NULL,
  `to` int(11) NOT NULL,
  `date` int(11) NOT NULL
) ENGINE=MyISAM AUTO_INCREMENT=78 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `sn_images`
--

CREATE TABLE IF NOT EXISTS `sn_images` (
`id` int(11) NOT NULL,
  `original_name` varchar(225) NOT NULL,
  `new_name` varchar(225) NOT NULL,
  `path` varchar(225) NOT NULL,
  `hash` varchar(100) NOT NULL
) ENGINE=MyISAM AUTO_INCREMENT=112 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `sn_images`
--

INSERT INTO `sn_images` (`id`, `original_name`, `new_name`, `path`, `hash`) VALUES
(111, 'Wallpaper-2668.jpg', '823873aaaf6ff2a6096d556360b263b2.jpg', '01-06-15', 'a7daeff7d5a1a970939e01f2e35f8d25'),
(110, 'image-9589.jpg', '1b478dd5ed4fdf3aa2924cb459cc4679.jpg', '31-05-15', 'c60852c01446f51e81092194c747a67c');

-- --------------------------------------------------------

--
-- Table structure for table `sn_likes`
--

CREATE TABLE IF NOT EXISTS `sn_likes` (
`id` int(11) NOT NULL,
  `from` int(11) NOT NULL,
  `to` int(11) NOT NULL,
  `date` int(11) NOT NULL
) ENGINE=MyISAM AUTO_INCREMENT=47 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `sn_links`
--

CREATE TABLE IF NOT EXISTS `sn_links` (
`id` int(11) NOT NULL,
  `link` varchar(200) NOT NULL,
  `image` text,
  `desc` text,
  `title` varchar(200) DEFAULT NULL,
  `hash` varchar(100) NOT NULL,
  `type` varchar(30) NOT NULL DEFAULT 'other'
) ENGINE=MyISAM AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `sn_messages`
--

CREATE TABLE IF NOT EXISTS `sn_messages` (
`id` int(11) NOT NULL,
  `from` int(11) NOT NULL,
  `to` int(11) NOT NULL,
  `message` text NOT NULL,
  `date` int(11) NOT NULL,
  `conversationID` int(11) NOT NULL,
  `unseen` int(11) NOT NULL DEFAULT '1'
) ENGINE=MyISAM AUTO_INCREMENT=151 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `sn_places`
--

CREATE TABLE IF NOT EXISTS `sn_places` (
`id` int(11) NOT NULL,
  `longitude` varchar(100) NOT NULL,
  `latitude` varchar(100) NOT NULL,
  `place_name` text NOT NULL
) ENGINE=MyISAM AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `sn_places`
--

INSERT INTO `sn_places` (`id`, `longitude`, `latitude`, `place_name`) VALUES
(9, '-9.598106666666666', '30.42775333333333', 'Rue du Caire, Agadir 80000, Morocco');

-- --------------------------------------------------------

--
-- Table structure for table `sn_posts`
--

CREATE TABLE IF NOT EXISTS `sn_posts` (
`id` int(11) NOT NULL,
  `status` text,
  `place` varchar(200) DEFAULT NULL,
  `link` varchar(200) DEFAULT NULL,
  `youtube` varchar(100) DEFAULT NULL,
  `image` varchar(100) DEFAULT NULL,
  `date` int(11) NOT NULL,
  `views` int(11) NOT NULL,
  `ownerID` int(11) NOT NULL,
  `privacy` int(11) NOT NULL
) ENGINE=MyISAM AUTO_INCREMENT=93 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `sn_posts`
--

INSERT INTO `sn_posts` (`id`, `status`, `place`, `link`, `youtube`, `image`, `date`, `views`, `ownerID`, `privacy`) VALUES
(90, 'hello world', 'Rue du Caire, Agadir 80000, Morocco', NULL, NULL, NULL, 1433099032, 0, 28, 1),
(91, 'HELLO WORLD', NULL, NULL, NULL, NULL, 1433184095, 0, 28, 1),
(92, 'Great Time with some beautiful friends', NULL, NULL, NULL, NULL, 1433184171, 0, 28, 1);

-- --------------------------------------------------------

--
-- Table structure for table `sn_reports`
--

CREATE TABLE IF NOT EXISTS `sn_reports` (
`id` int(11) NOT NULL,
  `reporterID` int(11) NOT NULL,
  `reason` text,
  `postID` int(11) NOT NULL
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `sn_sessions`
--

CREATE TABLE IF NOT EXISTS `sn_sessions` (
`id` int(11) NOT NULL,
  `userID` int(11) NOT NULL,
  `token` varchar(225) NOT NULL,
  `date` int(11) NOT NULL
) ENGINE=MyISAM AUTO_INCREMENT=166 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `sn_sessions`
--

INSERT INTO `sn_sessions` (`id`, `userID`, `token`, `date`) VALUES
(165, 28, '4863ed373a830ff2cf2ae764c96ea713', 1433190744),
(164, 28, '3135873e16382936a6230ce379e8e6c1', 1433190337),
(163, 28, '444d92cfa3f96d4525791b4c17e7d754', 1433190260),
(162, 28, '83e3fdf337205c1511fb91fc99248478', 1433190178),
(161, 28, '174aedc0d71c71b91dfcc6a15ae42c13', 1433183425),
(160, 28, '3de6a7d1a6691ce09c967c307bc8b4dc', 1433122751),
(159, 29, '447fc6545d20d045f27abff639578a47', 1433122659),
(158, 28, 'd949008e3a5a34f3cf49c7f7985e1407', 1433121826),
(157, 28, '3ef70e89c9eb186d79542f4378976abf', 1433098891);

-- --------------------------------------------------------

--
-- Table structure for table `sn_users`
--

CREATE TABLE IF NOT EXISTS `sn_users` (
`id` int(11) NOT NULL,
  `username` varchar(225) NOT NULL,
  `email` varchar(225) NOT NULL,
  `password` varchar(225) NOT NULL,
  `name` varchar(225) DEFAULT NULL,
  `picture` varchar(100) NOT NULL,
  `cover` varchar(100) DEFAULT NULL,
  `job` varchar(225) DEFAULT NULL,
  `address` varchar(225) DEFAULT NULL,
  `date` int(11) NOT NULL,
  `reg_id` text,
  `active` int(11) NOT NULL DEFAULT '1'
) ENGINE=MyISAM AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `sn_users`
--

INSERT INTO `sn_users` (`id`, `username`, `email`, `password`, `name`, `picture`, `cover`, `job`, `address`, `date`, `reg_id`, `active`) VALUES
(29, 'omar10', 'atouch.mohamed@gmail.com', '5f4dcc3b5aa765d61d8327deb882cf99', NULL, 'a7daeff7d5a1a970939e01f2e35f8d25', NULL, NULL, NULL, 1433122649, 'APA91bEdqQIuXPkZVrkjFoTCI3JUUiYJHwevI90n32m2R4kJ_J7cG9Z0ZPUJSZVvObsJUYIjUK3ZO_Jis5ju0jQpmAQkvbP4vTXQ6O2t576UMiE1ifwPxhpA07J1dgKsb-zX4kK2kOmZktRu6d0p4Bo1CFFBSnCJOg', 1),
(28, 'atouchsimo', 'atouch.mohamed@gmail.com', '5f4dcc3b5aa765d61d8327deb882cf99', NULL, 'c60852c01446f51e81092194c747a67c', NULL, NULL, NULL, 1433098883, 'fUsobf7l_i4:APA91bE2VNK0F_52rWwgoCdA9yZuxLgbjFOPM26Xv58AXzJ3NZuc6a7_KGeUq1VYINGNLuU5di7hSP2HwxUgaos3GFpW7lJZ5Gwa-dteAbZCQhpcnQ6mdj7HAep2qXuWD5-1WsbV3uHJ', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `sn_activation`
--
ALTER TABLE `sn_activation`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sn_admins`
--
ALTER TABLE `sn_admins`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sn_apilogs`
--
ALTER TABLE `sn_apilogs`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sn_comments`
--
ALTER TABLE `sn_comments`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sn_config`
--
ALTER TABLE `sn_config`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sn_conversations`
--
ALTER TABLE `sn_conversations`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sn_follows`
--
ALTER TABLE `sn_follows`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sn_images`
--
ALTER TABLE `sn_images`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sn_likes`
--
ALTER TABLE `sn_likes`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sn_links`
--
ALTER TABLE `sn_links`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sn_messages`
--
ALTER TABLE `sn_messages`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sn_places`
--
ALTER TABLE `sn_places`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sn_posts`
--
ALTER TABLE `sn_posts`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sn_reports`
--
ALTER TABLE `sn_reports`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sn_sessions`
--
ALTER TABLE `sn_sessions`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sn_users`
--
ALTER TABLE `sn_users`
 ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `sn_activation`
--
ALTER TABLE `sn_activation`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `sn_admins`
--
ALTER TABLE `sn_admins`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `sn_apilogs`
--
ALTER TABLE `sn_apilogs`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=11;
--
-- AUTO_INCREMENT for table `sn_comments`
--
ALTER TABLE `sn_comments`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=33;
--
-- AUTO_INCREMENT for table `sn_config`
--
ALTER TABLE `sn_config`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT for table `sn_conversations`
--
ALTER TABLE `sn_conversations`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=24;
--
-- AUTO_INCREMENT for table `sn_follows`
--
ALTER TABLE `sn_follows`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=78;
--
-- AUTO_INCREMENT for table `sn_images`
--
ALTER TABLE `sn_images`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=112;
--
-- AUTO_INCREMENT for table `sn_likes`
--
ALTER TABLE `sn_likes`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=47;
--
-- AUTO_INCREMENT for table `sn_links`
--
ALTER TABLE `sn_links`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=30;
--
-- AUTO_INCREMENT for table `sn_messages`
--
ALTER TABLE `sn_messages`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=151;
--
-- AUTO_INCREMENT for table `sn_places`
--
ALTER TABLE `sn_places`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=10;
--
-- AUTO_INCREMENT for table `sn_posts`
--
ALTER TABLE `sn_posts`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=93;
--
-- AUTO_INCREMENT for table `sn_reports`
--
ALTER TABLE `sn_reports`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT for table `sn_sessions`
--
ALTER TABLE `sn_sessions`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=166;
--
-- AUTO_INCREMENT for table `sn_users`
--
ALTER TABLE `sn_users`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=30;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

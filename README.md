# Dengue Report System

## Overview

The Dengue Report System is a Java application designed to analyze and visualize Dengue cases in the state of Pahang, Malaysia. The system utilizes data from two Excel files spanning the years 2014 to 2019 and provides insights into Dengue cases by area.

## Features

- **Read Excel Files:** The system reads two Excel files containing Dengue statistics for the years 2014-2017 and 2018-2019.

- **Combine Data:** Combines data from both files to create a comprehensive list of Dengue cases.

- **Graphical User Interface (GUI):** The system presents information through a user-friendly GUI.

- **Total Cases Calculation:** Calculates the total Dengue cases for each area and year.

- **Table Display:** Displays Dengue cases in a tabular format, showing data for each area from 2014 to 2019.

- **Navigation:** Allows users to navigate between different views, such as viewing all Dengue cases or cases per area.

## Usage

1. Clone the repository.
2. Open the project in a Java IDE.
3. Run the `DengueReportSystem` class to start the application.
4. Explore the GUI to view Dengue statistics and navigate between different sections.

## System Architecture

### `DengueReportSystem` Class

- Reads Excel files using the `XlsxReader` class.
- Combines data from different years into a single list.
- Passes the combined data to the `GUIHomePage` class.

### `GUIHomePage` Class

- Creates a GUI to display Dengue statistics.
- Calculates total Dengue cases for each year and area.
- Uses a table to present the data.
- Allows navigation to other GUI views.

## Dependencies

- Java
- Apache POI (for Excel file reading)

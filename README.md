
---

# Java Pinger Application

[![Java Version](https://img.shields.io/badge/Java-17+-blue)](https://www.java.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-green)](https://opensource.org/licenses/MIT)
[![Status](https://img.shields.io/badge/Status-Active-brightgreen)]

---

## Overview

The **Java Pinger** is a desktop application built with **Java Swing (JFrame)** that allows users to monitor network connectivity. It tracks the status of DNS servers or IP addresses, making it an essential tool for network monitoring and troubleshooting.

The application provides **real-time alerts** when a host is unreachable or when requests time out and can **save logs** for further analysis.

**Java Pinger is completely open source and free, designed for networking enthusiasts, IT professionals, and anyone who wants to monitor internet or server connectivity.**

---

## Tech Stack / Tools Used

* Java
* Java Swing

---

## Features

* Ping DNS or IP addresses
* Real-time alerts when hosts are unreachable or requests time out
* Logging of ping results with timestamps
* User-friendly GUI
* Customizable ping intervals
* Multi-host monitoring
* Historical statistics for success/failure rate and average response time
* Export logs in text or CSV format
* Network outage detection
* Configurable alert thresholds
* Pause/resume monitoring

---

## Screenshots

<img width="700" height="550" alt="image" src="https://github.com/user-attachments/assets/ec10805b-f16e-4802-b1b3-2d3013128c99" />

*Main monitoring window showing ping results and logs.*

---

## Use Cases

* Network monitoring for home or office networks
* Detecting internet outages via DNS tracking
* Troubleshooting network issues for IT support
* Keeping logs of connectivity history for auditing or analysis
* Monitoring servers or critical devices in small business networks

---

## Requirements

* Java 17 or later
* Windows, Linux, or macOS
* No additional libraries required

---

## Installation

1. Clone the repository:

```bash
git clone https://github.com/justrhey/Pinger.git
```

2. Compile Java source files:

```bash
javac -d bin src/*.java
```

3. Run the application:

```bash
java -cp bin Main
```

---

## Usage

1. Launch the application
2. Enter the IP address or DNS hostname to monitor
3. Set the ping interval (optional)
4. Click **Start Ping** to begin monitoring
5. View real-time results and alerts
6. Save or export logs for reporting and analysis
7. Pause/resume monitoring as needed

---

## Contributing

This project is **open source and free**. Contributions are welcome!

1. Fork the repository
2. Create a new branch for your feature/fix
3. Commit your changes
4. Submit a pull request

---

## License

This project is licensed under the **MIT License**


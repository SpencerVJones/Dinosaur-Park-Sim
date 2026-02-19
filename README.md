<div align="center">
  <h2 align="center"> Dinosaur Park Simulator </h2>
  <div align="left">

![Repo Views](https://visitor-badge.laobi.icu/badge?page_id=SpencerVJones/Dinosaur-Park-Sim)
</div>

  <p align="center">
    A Java RPG-style dinosaur management game with a modern Vaadin UI, progression systems, and a Render Docker deployment.
    <br />
    <br />
    <a href="https://github.com/SpencerVJones/Dinosaur-Park-Sim/issues">Report Bug</a>
    |
    <a href="https://github.com/SpencerVJones/Dinosaur-Park-Sim/issues">Request Feature</a>
  </p>
</div>

<!-- PROJECT SHIELDS -->
<div align="center">

![License](https://img.shields.io/github/license/SpencerVJones/Dinosaur-Park-Sim?style=for-the-badge)
![Contributors](https://img.shields.io/github/contributors/SpencerVJones/Dinosaur-Park-Sim?style=for-the-badge)
![Forks](https://img.shields.io/github/forks/SpencerVJones/Dinosaur-Park-Sim?style=for-the-badge)
![Stargazers](https://img.shields.io/github/stars/SpencerVJones/Dinosaur-Park-Sim?style=for-the-badge)
![Issues](https://img.shields.io/github/issues/SpencerVJones/Dinosaur-Park-Sim?style=for-the-badge)
![Last Commit](https://img.shields.io/github/last-commit/SpencerVJones/Dinosaur-Park-Sim?style=for-the-badge)
![Repo Size](https://img.shields.io/github/repo-size/SpencerVJones/Dinosaur-Park-Sim?style=for-the-badge)
![Platform](https://img.shields.io/badge/platform-Web%20Game-1f2937.svg?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-17-blue.svg?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-6DB33F.svg?style=for-the-badge)
![Vaadin](https://img.shields.io/badge/Vaadin-24.5.5-00B4F0.svg?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36.svg?style=for-the-badge)
![Docker](https://img.shields.io/badge/Docker-Deploy-2496ED.svg?style=for-the-badge)
![Render](https://img.shields.io/badge/Render-Deploy-4A90E2.svg?style=for-the-badge)
</div>

## Table of Contents
- [Overview](#overview)
- [Technologies Used](#technologies-used)
- [Features](#features)
- [Demo](#demo)
- [Project Structure](#project-structure)
- [Game Routes](#game-routes)
- [Testing](#testing)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Run Locally](#run-locally)
  - [Build](#build)
- [Usage](#usage)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## Overview
**Dinosaur Park Simulator** is a Java-first dinosaur management game that blends:
- A polished Vaadin command center UI
- RPG-like progression (XP, levels, rank, missions, achievements)
- Strategy systems for exploration, stewardship, and budget-constrained operations

Gameplay loops include:
- Discovering species and filling a Safari Log
- Completing encounters and quizzes
- Running lab simulations (feeding, habitat, revenue, compatibility)
- Managing War Room actions with real affordability checks

## Technologies Used
- Java 17
- Spring Boot 3.3.5
- Vaadin 24.5.5
- Maven
- Docker
- Render Blueprint (`render.yaml`)
- Custom Java test runner + unit tests

## Features
- **Explorer mode** with searchable dinosaur data grid and species profiles
- **Safari Log** persistence per session with codex progression rewards
- **Quick Quests**: random encounters, quiz arena, mission tracking
- **Steward Lab tools**: feeding planner, habitat planner, revenue simulator, compatibility checker
- **War Room simulation** with dynamic operation costs and blocked actions when budget is insufficient
- **Commander progression system**: XP, levels, rank titles, mission milestones, achievements, tokens
- **Custom dark RPG theme** (Vaadin theme: `ancient-eden`)
- **Render-ready Docker deployment** with health checks

## Demo
**Live:** [https://ancient-eden-command-center.onrender.com/](https://ancient-eden-command-center.onrender.com/)


https://github.com/user-attachments/assets/bd602913-d5c3-4e37-a97a-2c4c786cd5c1


## Project Structure
```bash
Dinosaur-Park-Sim/
|-- README.md
|-- LICENSE
|-- pom.xml
|-- Dockerfile
|-- .dockerignore
|-- render.yaml
|-- scripts/
|   |-- build.sh
|   |-- run.sh
|   `-- test.sh
`-- src/
    |-- main/
    |   |-- java/dinosaur/park/
    |   |   |-- CampaignEngine.java
    |   |   |-- DinosaurCatalog.java
    |   |   |-- ParkOperations.java
    |   |   |-- RangerProgression.java
    |   |   `-- ...
    |   |-- java/dinosaur/park/web/
    |   |   |-- AncientEdenApplication.java
    |   |   |-- AncientEdenAppShell.java
    |   |   |-- HealthController.java
    |   |   |-- ui/
    |   |   `-- view/
    |   |-- frontend/themes/ancient-eden/
    |   |   |-- theme.json
    |   |   `-- styles.css
    |   `-- resources/
    |       |-- application.properties
    |       `-- dinosaur/park/data/
    |           |-- dino_catalog_names.txt
    |           `-- real_dinosaurs.psv
    `-- test/java/dinosaur/park/
```

## Game Routes
- `GET /` - Dashboard
- `GET /explorer` - Explorer data grid + profile + safari log actions
- `GET /steward-lab` - Planners and simulators
- `GET /quests` - Encounters + quiz + mission board
- `GET /war-room` - Budget strategy operations
- `GET /health` - deployment health endpoint


## Testing
Run the project test suite:
```bash
./scripts/test.sh
```

Or with Maven:
```bash
mvn test
```

## Getting Started
### Prerequisites
- JDK 17+
- Maven 3.9+
- Optional: Docker Desktop

### Installation
1. Clone:
```bash
git clone https://github.com/makesspence/Dinosaur-Park-Sim.git
```
2. Enter project:
```bash
cd Dinosaur-Park-Sim
```
3. Make scripts executable:
```bash
chmod +x scripts/build.sh scripts/run.sh scripts/test.sh
```

### Run Locally
```bash
./scripts/run.sh
```
Open:

`http://localhost:8080`

If port `8080` is busy:
```bash
APP_PORT=8081 ./scripts/run.sh
```

### Build
```bash
./scripts/build.sh
```

## Usage
Start the application locally and open the printed localhost URL (default: http://localhost:8080).

Use the navigation menu to access:
- Dashboard (park overview and status)
- Explorer (browse dinosaurs and log discoveries)
- Quests (complete encounters and quizzes)
- Steward Lab (run planners and simulations)
- War Room (make strategic budget decisions)

Progress through the game by exploring species, completing quests, earning XP, and upgrading your park strategically. 

## Roadmap
- [ ] Add persistent save/load profiles (database or file-backed sessions)
- [ ] Add audio pack management and richer encounter cinematics
- [ ] Add multiplayer or async leaderboard progression
- [ ] Expand analytics and balancing telemetry
- [ ] Add integration tests for full route/flow coverage

## Contributing
Contributions are welcome! Feel free to submit issues or pull requests with bug fixes, improvements, or new features.
- Fork the Project
- Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
- Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
- Push to the Branch (`git push origin feature/AmazingFeature`)
- Open a Pull Request

### Contributors
<a href="https://github.com/SpencerVJones/Dinosaur-Park-Sim/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=SpencerVJones/Dinosaur-Park-Sim"/>
</a>


## License
Distributed under the MIT License. See LICENSE for more information.


## Contact
Spencer Jones
📧 [jonesspencer99@icloud.com](mailto:jonesspencer99@icloud.com)  
🔗 [GitHub Profile](https://github.com/SpencerVJones)  
🔗 [Project Repository](https://github.com/SpencerVJones/Dinosaur-Park-Sim)

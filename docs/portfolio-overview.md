# Portfolio Overview: Dinosaur Park Sim

## Project Summary

Dinosaur Park Sim is a Java 17 desktop simulation platform that blends data visualization, operational modeling, and interactive game-like systems in a cohesive Swing application.

## What This Demonstrates

- **Product thinking**: multi-surface UI with clear user workflows (Explorer, Lab, Campaign, Adventures)
- **Domain modeling**: reusable simulation logic isolated in `ParkOperations` and `CampaignEngine`
- **Data engineering**: local curated dataset + catalog expansion through deterministic ingestion logic
- **Engineering quality**: automated tests and CI pipeline with reproducible build/test scripts
- **Maintainability**: modular package structure and resource layout (`src/main/java`, `src/main/resources`, `src/test/java`)

## Notable Technical Decisions

1. **Data source strategy**
- Curated PSV data powers high-quality baseline records.
- Catalog import layer expands breadth while estimating reasonable gameplay attributes.

2. **Logic/UI separation**
- Operational formulas and simulation utilities live outside the UI for testability and reuse.

3. **No-dependency test harness**
- Project ships with deterministic tests runnable on any JDK 17 environment.
- Removes tooling friction for reviewers and recruiters cloning the repo.

4. **CI first**
- GitHub Actions validates build + tests on every push and pull request.

## Suggested Demo Flow

1. Start app with `./scripts/run.sh`
2. Open Explorer and filter for Late Cretaceous carnivores
3. Select a species, inspect profile, and play species call
4. Move to Park Lab and run revenue + compatibility analysis
5. Trigger campaign actions and review changing status metrics

## Resume-Friendly Impact Statement

Built a Java desktop simulation platform with data ingestion, scenario modeling, and CI-backed automated quality checks; delivered modular architecture, deterministic tests, and reproducible local tooling for professional-grade maintainability.

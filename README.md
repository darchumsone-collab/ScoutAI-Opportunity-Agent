ScoutAI – Autonomous Opportunity Discovery Agent

«An AI-powered opportunity intelligence platform built with Hermes Agent.»

Overview

ScoutAI is an autonomous opportunity discovery platform that helps users discover, evaluate, and act on meaningful opportunities.

Instead of forcing users to manually search through multiple websites, ScoutAI leverages Hermes Agent to plan, research, analyze, rank, and recommend opportunities based on user goals.

The platform focuses on:

- Jobs
- Internships
- Scholarships
- Grants
- Fellowships
- Startup Competitions
- Hackathons
- Tenders

Problem Statement

Finding opportunities has become increasingly fragmented.

Users often spend hours:

- Searching across multiple platforms
- Comparing opportunities
- Reviewing eligibility requirements
- Identifying skill gaps
- Creating application plans

Many opportunities are missed simply because users cannot evaluate everything efficiently.

ScoutAI addresses this challenge through autonomous opportunity discovery and decision support.

Why Hermes Agent?

ScoutAI was designed around agentic workflows rather than traditional search.

Hermes Agent enables:

- Goal-oriented planning
- Multi-step reasoning
- Tool orchestration
- Opportunity evaluation
- Recommendation generation
- Action plan creation

Instead of simply responding to prompts, the agent actively works toward solving user objectives.

Key Features

Opportunity Discovery

Automatically discovers:

- Jobs
- Internships
- Scholarships
- Grants
- Fellowships
- Startup Competitions
- Hackathons
- Tenders

Opportunity Intelligence Reports

Provides:

- Match Scores
- Strength Analysis
- Weakness Analysis
- Missing Skills
- Recommended Actions

Agent Activity Timeline

Displays how Hermes Agent reached recommendations through:

- Planning
- Research
- Discovery
- Matching
- Ranking
- Recommendation Generation

Career Growth Mapping

Identifies:

- Skill gaps
- Learning opportunities
- Career pathways
- Recommended next steps

Application Strategy Generation

Creates:

- Application checklists
- Preparation plans
- Submission strategies
- Opportunity prioritization

Agent Workflow

User Goal
    ↓
Hermes Agent Planning
    ↓
Opportunity Discovery
    ↓
Requirement Extraction
    ↓
Profile Matching
    ↓
Opportunity Ranking
    ↓
Recommendation Generation
    ↓
Application Strategy
    ↓
Action Plan

Architecture

Android Client
        │
        ▼
FastAPI Backend
        │
        ▼
Hermes Agent
        │
 ┌──────┼──────┐
 ▼      ▼      ▼
Search  Analysis Ranking
Tools   Engine   Engine
        │
        ▼
Recommendations
        │
        ▼
Action Plans

Tech Stack

Mobile

- Kotlin
- Jetpack Compose
- Material 3
- MVVM Architecture

Backend

- FastAPI
- Python
- PostgreSQL

AI & Search

- Hermes Agent
- Tavily
- OpenRouter

Infrastructure

- Docker
- GitHub

Installation

Clone the Repository

git clone https://github.com/darchumsone-collab/ScoutAI-Opportunity-Agent.git
cd ScoutAI-Opportunity-Agent

Configure Environment Variables

Create a ".env" file:

OPENROUTER_API_KEY=
TAVILY_API_KEY=
DATABASE_URL=

Start Backend

docker-compose up --build

Run Android App

Open the project in Android Studio and run the application on an emulator or physical device.

Repository Structure

ScoutAI-Opportunity-Agent/
│
├── app/
├── backend/
├── docs/
├── assets/
├── README.md
├── LICENSE
├── .gitignore
├── docker-compose.yml
└── .env.example

Future Roadmap

- Continuous opportunity monitoring
- Enhanced agent memory
- Calendar integrations
- Application success prediction
- Advanced recommendation personalization
- Team collaboration features

Hermes Agent Challenge

This project was built and submitted for the Hermes Agent Challenge.

The primary objective was to demonstrate meaningful agentic behavior through:

- Planning
- Reasoning
- Tool usage
- Multi-step execution
- Decision support

Author

Darlington Chidera Mbawike

GitHub:
https://github.com/darchumsone-collab

Repository:
https://github.com/darchumsone-collab/ScoutAI-Opportunity-Agent

License

MIT License

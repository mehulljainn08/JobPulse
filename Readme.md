JobPulse 🚀
AI-Powered Job Aggregator & Intelligent Referral Assistant

JobPulse is a backend-first platform designed to streamline the job hunt. It automates the chaos of checking multiple job boards, intelligently filters opportunities using AI, and—most importantly—leverages your existing professional network to surface referral opportunities automatically.

Unlike generic scrapers, JobPulse focuses on the "Hidden Job Market" strategy by prioritizing jobs where the user already has a connection.

✨ Key Features
1. 🕷️ Multi-Source Job Scraping

Scrapes jobs from static job boards (e.g., RemoteOK, WeWorkRemotely) using Jsoup.

Normalizes data (Title, Company, Location, Description) into a unified schema.

Deduplication Engine: Uses a content-hashing algorithm to prevent storing duplicate listings across different days or sources.

2. 🤝 The "Network Matcher" (Unique Feature)

LinkedIn Integration (Safe Mode): Allows users to upload their LinkedIn connections via CSV export (no risky bot scraping).

Relational Matching: When a new job is found, the system queries the Connections database to see if the user knows anyone at that company.

Fuzzy Matching: Handles variations in company names (e.g., matching "Google India" job to a connection at "Google LLC").

3. 🧠 AI Enrichment Layer

Smart Summaries: Compresses lengthy JDs into 2-line executive summaries.

Relevance Scoring: Assigns a score (0-100) based on the user's tech stack (e.g., High score if JD mentions "Spring Boot" and "Kafka").

Referral Draft Generator: If a connection is found, the AI generates a personalized cold-message draft tailored to that specific connection and job role, ready for the user to send.

4. 📧 Personalized Daily Digests

Sends a single email every morning (via Spring Scheduler).

Priority Sorting: Jobs with User Connections appear at the top, followed by High Relevance Score jobs.

Includes "One-Click" links to apply or reach out to connections.

🛠️ Tech Stack
Component	Technology	Description
Backend	Java 17, Spring Boot	Core REST API & Business Logic
Database	PostgreSQL	Relational storage for Jobs, Users, and Connections
Scraping	Jsoup	HTML parsing and extraction
Scheduling	Spring @Scheduled	CRON jobs for scraping and email dispatch
AI/LLM	OpenAI API / Gemini	Text summarization and message generation
Data Processing	OpenCSV	High-performance CSV parsing for bulk contact uploads
Containerization	Docker	Easy deployment and environment consistency
🧩 System Architecture
Code snippet
graph TD
    A[Scheduler] -->|Trigger| B(Job Scraper Service)
    B -->|Fetch HTML| C{Job Boards}
    B -->|Parse & Hash| D[(PostgreSQL - Jobs)]
    
    E[User] -->|Uploads Connections.csv| F(CSV Parser Service)
    F -->|Normalize & Save| G[(PostgreSQL - Connections)]
    
    H[AI Service] -->|Enrich| D
    
    I[Digest Service] -->|1. Fetch New Jobs| D
    I -->|2. Check for Connections| G
    I -->|3. Generate Referral Drafts| H
    I -->|4. Send Email| J(SMTP Server)
    J -->|Daily Digest| E
🚀 Technical Highlights (Why this project matters)
Complex Data Relationships: Implements a normalized Many-to-Many relationship between Users, Jobs, and Connections.

Asynchronous Processing: Scraping and AI enrichment happen asynchronously to prevent blocking the main thread.

Bulk Data Handling: efficiently processes thousands of LinkedIn connections from CSV imports without memory overflows.

Fault Tolerance: Implements retry logic for scraping failures and API rate limits.

🏃 Getting Started
Prerequisites

Java 17+

PostgreSQL

Maven

OpenAI API Key (or alternative LLM)

Installation

Clone the repo

Bash
git clone https://github.com/mehulljainn08/JobPulse.git
cd JobPulse
Configure Database Update src/main/resources/application.properties:

Properties
spring.datasource.url=jdbc:postgresql://localhost:5432/jobpulse
spring.datasource.username=your_user
spring.datasource.password=your_password
openai.api.key=your_api_key
Run the Application

Bash
mvn spring-boot:run
Load Connections

Export connections from LinkedIn (Settings -> Data Privacy -> Get a copy of your data).

Use the API endpoint /api/connections/upload to upload the CSV.

# JobPulse 🚀  
## AI-Powered Job Aggregator & Intelligent Referral Assistant (Backend-First)

JobPulse is a backend-first platform built to simplify and optimize the job hunt.

Instead of wasting time manually checking multiple job boards every day, JobPulse **automatically aggregates job postings**, enriches them using AI (summary + skill tags + relevance scoring), and most importantly, helps users leverage the **hidden job market** by prioritizing jobs where the user already has a **network connection**.

This project is designed to be **practical, scalable, and system-design friendly**, focusing heavily on clean architecture, data consistency, and automation.

---

## ✨ Key Features

### 1) 🕷️ Multi-Source Job Aggregation
- Scrapes job listings from **static job boards** (ex: RemoteOK, WeWorkRemotely) using **Jsoup**
- Normalizes job data into a unified schema:
  - `Title`, `Company`, `Location`, `Description`, `Apply Link`, `Source`
- Built-in **Deduplication Engine**
  - Generates a stable `jobHash` to prevent saving duplicate jobs across runs/days/sources
  - Ensures idempotent behavior (running scrapers multiple times does not create duplicates)

---

### 2) 🤝 The Network Matcher (Unique Feature)
JobPulse focuses on “referral-first” discovery.

Instead of just listing jobs, it automatically highlights jobs where:
✅ the user already knows someone in that company

**LinkedIn Integration (Safe Mode):**
- Users upload their LinkedIn connections via **CSV export**  
- No unsafe automation / no scraping LinkedIn profiles

**Matching Engine:**
- When a new job is ingested, JobPulse matches it against the user’s connections database
- Supports fuzzy matching for company name variations:
  - `"Google India"` → `"Google LLC"`
  - `"Amazon Web Services"` → `"Amazon"`

Output:
- **Referral-ready jobs feed** (jobs where a matching connection exists)

---

### 3) 🧠 AI Enrichment Layer
JobPulse adds practical AI features that improve decision-making:

✅ **Smart Summaries**  
Turns long job descriptions into a short 2-line summary.

✅ **Relevance Scoring (0–100)**  
Scores each job based on user preferences (skills/roles), e.g.:
- Higher score if JD includes: `Spring Boot`, `Kafka`, `Redis`

✅ **Referral Draft Generator**  
If a connection match exists, JobPulse generates a personalized referral message draft:
- tailored to the job + connection
- ready to copy/send by the user

---

## 🏗️ Architecture Overview

JobPulse is built as a clean pipeline:

1. **Scrape jobs** from sources  
2. **Normalize + Deduplicate** listings  
3. **Persist jobs** in PostgreSQL  
4. **Enrich jobs using AI** (summary + tags + score)  
5. **Match jobs with user’s connections**  
6. Generate **referral outreach drafts**

### High-Level Flow
Scrapers → Raw Jobs → Ingestion Service → DB
↓
AI Enrichment
↓
Connection Matching Engine
↓
Referral-ready Jobs + Drafts


---

## 🛠️ Tech Stack

- **Backend:** Spring Boot (Java)
- **Scraping:** Jsoup (Static HTML scraping)
- **Database:** PostgreSQL
- **Containerization:** Docker Compose
- **Scheduling:** Spring Scheduler (`@Scheduled`)
- **Email Digests:** JavaMailSender *(planned/optional)*
- **AI Layer:** OpenAI API / LLM API *(pluggable)*

---

## 🗃️ Data Model (Core Tables)

### `users`
Stores user identity and preferences.

### `jobs`
Stores normalized job postings with deduplication (`jobHash` unique).

### `connections`
Stores user-imported network data (CSV-based safe LinkedIn integration).

### `job_matches`
Stores job ↔ connection matches for referral discovery.

### `outreach_drafts`
Stores AI-generated referral message drafts.

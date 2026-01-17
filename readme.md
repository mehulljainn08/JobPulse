# JobPulse 🚀  
### AI-powered Job Scraper + Personalized Email Digests

JobPulse is a backend-focused project that scrapes jobs from static job boards, stores them reliably with deduplication, enriches them using AI (summary + skill tags + relevance score), and sends personalized email digests to users based on their preferences.

---

## ✨ Features

### ✅ Job Scraping
- Scrapes jobs from static websites (example sources: RemoteOK, WeWorkRemotely)
- Extracts:
  - Job Title  
  - Company  
  - Location  
  - Apply Link  
  - Posted Date *(if available)*  
  - Job Description *(if available)*  

### ✅ Reliable Storage + Deduplication
- Jobs are stored in PostgreSQL
- Duplicate jobs are automatically ignored using a unique `jobHash`

### ✅ AI Enrichment (Core “AI” layer)
Each job is enriched using AI to provide:
- **Relevance Score (0–100)** (based on user preferences)
- **2-line Summary**
- **Skill Tags** (ex: Java, Spring Boot, Kafka, SQL, AWS)

### ✅ Daily Email Digest (Automated)
- Sends a daily email (example: 9 AM) containing:
  - New jobs from last 24 hours
  - Top ranked jobs first (AI-based sorting)
  - Quick summaries and skills

### ✅ Personalization (Multi-user)
- Each user can set job preferences:
  - Keywords (ex: `backend`, `java`, `spring boot`)
  - Location filters (ex: `remote`, `bangalore`)
  - Frequency (daily digest)

---

## 🛠️ Tech Stack

- **Backend:** Spring Boot (Java)
- **Scraping:** Jsoup
- **Database:** PostgreSQL
- **Scheduler:** Spring `@Scheduled`
- **Email:** JavaMailSender (SMTP)
- **AI Layer:** OpenAI API (or any LLM API)

---

## 🧩 System Flow

1. **Scraper fetches jobs** from selected job sources  
2. Jobs are **parsed + normalized**
3. Deduplication using `jobHash` ensures **no duplicate inserts**
4. AI enriches each job:
   - summary, skill tags, relevance score
5. Scheduler triggers digest job daily
6. Users receive **personalized job digest emails**

---


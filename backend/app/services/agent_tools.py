import json
import datetime
from typing import List, Dict, Any, Optional
from langchain_core.tools import tool
from langchain_community.tools.tavily_search import TavilySearchResults
from app.core.config import settings
from app.models.opportunity import Opportunity, MatchResult, OpportunityType
from app.models.profile import Profile, Resume
from app.models.application import Application, ApplicationStatus
from app.models.agent import AgentLog
from sqlalchemy.orm import Session

class ScoutTools:
    def __init__(self, db: Session, user_id: int, execution_id: int, logger_func, llm):
        self.db = db
        self.user_id = user_id
        self.execution_id = execution_id
        self.log = logger_func
        self.llm = llm

    def get_tools(self):
        @tool
        def search_web_tool(query: str) -> str:
            """Search the web for opportunities using Tavily."""
            search = TavilySearchResults(api_key=settings.TAVILY_API_KEY)
            results = search.run(query)
            self.log(f"Web search for: {query}", tool_name="search_web_tool", tool_input=query)
            return json.dumps(results)

        @tool
        def extract_opportunity_details(raw_text_or_url: str) -> str:
            """Extract structured details (title, organization, description, type, deadline, requirements) from raw text or a search result."""
            prompt = f"""
            Extract structured opportunity data from the following text.
            If it's not an opportunity, say "NOT_OPPORTUNITY".
            Text: {raw_text_or_url}
            
            Return JSON:
            {{
                "title": "...",
                "organization": "...",
                "description": "...",
                "opportunity_type": "job|internship|scholarship|grant|competition|hackathon|fellowship|tender",
                "deadline": "ISO-8601 string or null",
                "requirements": ["list", "of", "strings"],
                "salary_range": "...",
                "location": "...",
                "remote": true/false
            }}
            """
            response = self.llm.invoke(prompt)
            cleaned = response.content.replace("```json", "").replace("```", "").strip()
            self.log("Extracted opportunity details", tool_name="extract_opportunity_details", tool_output=cleaned)
            return cleaned

        @tool
        def save_opportunity(structured_data_json: str) -> str:
            """Save a structured opportunity to the database."""
            try:
                data = json.loads(structured_data_json)
                opp = Opportunity(
                    title=data['title'],
                    organization=data.get('organization'),
                    description=data.get('description'),
                    opportunity_type=OpportunityType(data['opportunity_type'].lower()),
                    location=data.get('location'),
                    remote=data.get('remote', False),
                    deadline=datetime.datetime.fromisoformat(data['deadline']) if data.get('deadline') else None,
                    requirements=data.get('requirements', []),
                    salary_range=data.get('salary_range')
                )
                self.db.add(opp)
                self.db.commit()
                return f"Saved opportunity: {opp.title} (ID: {opp.id})"
            except Exception as e:
                return f"Failed to save: {str(e)}"

        @tool
        def analyze_resume_compatibility(opportunity_id: int) -> str:
            """Analyze the user's primary resume against a specific opportunity and generate a match score (0-100) and gap analysis."""
            resume = self.db.query(Resume).filter(Resume.user_id == self.user_id, Resume.is_primary == True).first()
            opportunity = self.db.query(Opportunity).get(opportunity_id)
            
            if not resume or not opportunity:
                return "Resume or Opportunity not found."
            
            prompt = f"""
            Compare this resume with the job description.
            Resume: {resume.raw_text}
            Job Description: {opportunity.description}
            
            Return a JSON object with:
            - score (0-100)
            - reasoning (string)
            - gaps (list of strings)
            - strengths (list of strings)
            """
            response = self.llm.invoke(prompt)
            cleaned = response.content.replace("```json", "").replace("```", "").strip()
            analysis = json.loads(cleaned)
            
            match = MatchResult(
                user_id=self.user_id,
                opportunity_id=opportunity_id,
                score=analysis['score'] / 100.0,
                reasoning=analysis['reasoning'],
                gap_analysis={"gaps": analysis['gaps'], "strengths": analysis['strengths']}
            )
            self.db.add(match)
            self.db.commit()
            return cleaned

        @tool
        def generate_application_strategy(opportunity_id: int) -> str:
            """Generate a multi-step application strategy, checklist, and timeline."""
            opportunity = self.db.query(Opportunity).get(opportunity_id)
            prompt = f"Create a step-by-step application plan for: {opportunity.title}. Include priority and risk assessment. Return JSON."
            response = self.llm.invoke(prompt)
            cleaned = response.content.replace("```json", "").replace("```", "").strip()
            
            app = self.db.query(Application).filter(Application.user_id==self.user_id, Application.opportunity_id==opportunity_id).first()
            if not app:
                app = Application(user_id=self.user_id, opportunity_id=opportunity_id, application_plan=json.loads(cleaned))
                self.db.add(app)
            else:
                app.application_plan = json.loads(cleaned)
            self.db.commit()
            return cleaned

        @tool
        def agent_memory_store(key: str, value: str) -> str:
            """Store an important fact about the user or a discovery in long-term memory."""
            self.log(f"Stored in memory: {key} = {value}", tool_name="agent_memory_store")
            # In a full implementation, this might go to a 'memories' table
            return "Stored."

        @tool
        def send_notification_tool(message: str) -> str:
            """Send a notification to the user about an important update or deadline."""
            self.log(f"Notification triggered: {message}", tool_name="send_notification_tool")
            # Integration point for Firebase Cloud Messaging
            return "Notification queued."

        return [
            search_web_tool, 
            extract_opportunity_details, 
            save_opportunity, 
            analyze_resume_compatibility, 
            generate_application_strategy,
            agent_memory_store,
            send_notification_tool
        ]

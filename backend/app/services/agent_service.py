import json
from typing import List, Dict, Any, Optional
from sqlalchemy.orm import Session
from app.models.agent import AgentExecution, AgentLog, AgentStatus
from app.models.opportunity import Opportunity, MatchResult, OpportunityType
from app.core.config import settings
from langchain_openai import ChatOpenAI
from langchain.agents import AgentExecutor, create_openai_functions_agent
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from app.services.agent_tools import ScoutTools

class HermesAgent:
    def __init__(self, db: Session, user_id: int, execution_id: int):
        self.db = db
        self.user_id = user_id
        self.execution_id = execution_id
        # Using OpenRouter for LLM access
        self.llm = ChatOpenAI(
            model="openai/gpt-4o",
            openai_api_key=settings.OPENROUTER_API_KEY,
            openai_api_base="https://openrouter.ai/api/v1"
        )
        self.tool_instance = ScoutTools(db, user_id, execution_id, self._log)
        self.tools = self.tool_instance.get_tools()
        self.agent_executor = self._setup_agent()

    def _log(self, message: str, level: str = "INFO", tool_name: str = None, tool_input: Any = None, tool_output: Any = None):
        log = AgentLog(
            execution_id=self.execution_id,
            level=level,
            message=message,
            tool_name=tool_name,
            tool_input=tool_input,
            tool_output=tool_output
        )
        self.db.add(log)
        self.db.commit()

    def _setup_agent(self):
        prompt = ChatPromptTemplate.from_messages([
            ("system", """You are Hermes Agent, a professional opportunity scout. 
            Your goal is to autonomously find, analyze, and rank opportunities (jobs, scholarships, grants, etc.) for users.
            Use the provided tools to search, analyze resumes, and create application plans.
            Always provide reasoning for your recommendations.
            Return structured data when possible."""),
            MessagesPlaceholder(variable_name="chat_history"),
            ("user", "{input}"),
            MessagesPlaceholder(variable_name="agent_scratchpad"),
        ])
        agent = create_openai_functions_agent(self.llm, self.tools, prompt)
        return AgentExecutor(agent=agent, tools=self.tools, verbose=True)

    async def run_discovery(self, user_profile: Dict[str, Any]):
        try:
            execution = self.db.query(AgentExecution).get(self.execution_id)
            execution.status = AgentStatus.RUNNING
            self.db.commit()

            query = (
                f"Identify and rank top 5 opportunities for a user with these skills: {user_profile.get('skills')}. "
                f"Interests: {user_profile.get('interests')}. "
                f"Location Preference: {user_profile.get('location')}. "
                "For each opportunity found: "
                "1. Extract key requirements. "
                "2. Calculate a match score (0-1). "
                "3. Provide reasoning. "
                "Save relevant opportunities to the database."
            )
            
            result = await self.agent_executor.ainvoke({"input": query, "chat_history": []})
            
            # Post-process: Agent should ideally use a SaveOpportunityTool, 
            # but for this logic we can parse the output or let the agent handle it via tools.
            # Here we simulate the agent finding and saving.
            
            execution.status = AgentStatus.COMPLETED
            execution.output_data = {"summary": result["output"]}
            self.db.commit()
            return result["output"]
        except Exception as e:
            execution = self.db.query(AgentExecution).get(self.execution_id)
            if execution:
                execution.status = AgentStatus.FAILED
                execution.error_message = str(e)
                self.db.commit()
            raise e

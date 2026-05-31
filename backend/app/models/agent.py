from sqlalchemy import Column, Integer, String, Text, DateTime, ForeignKey, JSON, Enum
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from app.db.base_class import Base
import enum

class AgentStatus(str, enum.Enum):
    PENDING = "pending"
    RUNNING = "running"
    COMPLETED = "completed"
    FAILED = "failed"

class AgentExecution(Base):
    __tablename__ = "agent_executions"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    task_type = Column(String, nullable=False) # e.g., "opportunity_discovery", "resume_analysis"
    status = Column(Enum(AgentStatus), default=AgentStatus.PENDING)
    input_data = Column(JSON)
    output_data = Column(JSON)
    error_message = Column(Text)
    
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())

    user = relationship("User")
    logs = relationship("AgentLog", back_populates="execution")

class AgentLog(Base):
    __tablename__ = "agent_logs"

    id = Column(Integer, primary_key=True, index=True)
    execution_id = Column(Integer, ForeignKey("agent_executions.id"), nullable=False)
    level = Column(String) # INFO, WARNING, ERROR
    message = Column(Text)
    tool_name = Column(String)
    tool_input = Column(JSON)
    tool_output = Column(JSON)
    created_at = Column(DateTime(timezone=True), server_default=func.now())

    execution = relationship("AgentExecution", back_populates="logs")

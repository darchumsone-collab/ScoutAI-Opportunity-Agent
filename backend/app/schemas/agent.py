from typing import List, Optional, Any, Dict
from pydantic import BaseModel
from datetime import datetime
from app.models.agent import AgentStatus

class AgentLogBase(BaseModel):
    level: str
    message: str
    tool_name: Optional[str] = None
    tool_input: Optional[Any] = None
    tool_output: Optional[Any] = None

class AgentLog(AgentLogBase):
    id: int
    execution_id: int
    created_at: datetime

    class Config:
        from_attributes = True

class AgentExecutionBase(BaseModel):
    task_type: str
    input_data: Optional[Dict[str, Any]] = None

class AgentExecutionCreate(AgentExecutionBase):
    pass

class AgentExecution(AgentExecutionBase):
    id: int
    user_id: int
    status: AgentStatus
    output_data: Optional[Dict[str, Any]] = None
    error_message: Optional[str] = None
    created_at: datetime
    updated_at: Optional[datetime] = None
    logs: List[AgentLog] = []

    class Config:
        from_attributes = True

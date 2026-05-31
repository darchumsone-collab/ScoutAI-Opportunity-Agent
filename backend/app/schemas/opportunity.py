from typing import List, Optional, Any
from pydantic import BaseModel, HttpUrl
from datetime import datetime
from app.models.opportunity import OpportunityType

class OpportunityBase(BaseModel):
    title: str
    organization: Optional[str] = None
    description: Optional[str] = None
    opportunity_type: OpportunityType
    location: Optional[str] = None
    remote: bool = False
    url: Optional[str] = None
    deadline: Optional[datetime] = None
    requirements: Optional[List[str]] = None
    salary_range: Optional[str] = None

class OpportunityCreate(OpportunityBase):
    pass

class OpportunityUpdate(OpportunityBase):
    title: Optional[str] = None
    opportunity_type: Optional[OpportunityType] = None

class Opportunity(OpportunityBase):
    id: int
    created_at: datetime
    updated_at: Optional[datetime] = None

    class Config:
        from_attributes = True

class MatchResultBase(BaseModel):
    score: float
    reasoning: str
    gap_analysis: Optional[dict] = None

class MatchResult(MatchResultBase):
    id: int
    user_id: int
    opportunity_id: int
    opportunity: Opportunity

    class Config:
        from_attributes = True

from typing import Optional, Any, Dict
from pydantic import BaseModel
from datetime import datetime
from app.models.application import ApplicationStatus
from app.schemas.opportunity import Opportunity

class ApplicationBase(BaseModel):
    status: ApplicationStatus = ApplicationStatus.PLANNED
    notes: Optional[str] = None
    application_plan: Optional[Dict[str, Any]] = None

class ApplicationCreate(ApplicationBase):
    opportunity_id: int

class ApplicationUpdate(ApplicationBase):
    pass

class Application(ApplicationBase):
    id: int
    user_id: int
    opportunity_id: int
    opportunity: Opportunity
    created_at: datetime
    updated_at: Optional[datetime] = None

    class Config:
        from_attributes = True

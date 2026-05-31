from sqlalchemy import Column, Integer, String, Text, Float, DateTime, ForeignKey, Enum, JSON, Boolean
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from app.db.base_class import Base
import enum

class OpportunityType(str, enum.Enum):
    JOB = "job"
    INTERNSHIP = "internship"
    SCHOLARSHIP = "scholarship"
    GRANT = "grant"
    COMPETITION = "competition"
    HACKATHON = "hackathon"
    FELLOWSHIP = "fellowship"
    TENDER = "tender"

class Opportunity(Base):
    __tablename__ = "opportunities"

    id = Column(Integer, primary_key=True, index=True)
    title = Column(String, index=True, nullable=False)
    organization = Column(String, index=True)
    description = Column(Text)
    opportunity_type = Column(Enum(OpportunityType), nullable=False)
    location = Column(String)
    remote = Column(Boolean, default=False)
    url = Column(String, unique=True)
    deadline = Column(DateTime(timezone=True))
    requirements = Column(JSON)  # List of requirements extracted by AI
    salary_range = Column(String)
    
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())

class MatchResult(Base):
    __tablename__ = "match_results"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    opportunity_id = Column(Integer, ForeignKey("opportunities.id"), nullable=False)
    score = Column(Float)
    reasoning = Column(Text)
    gap_analysis = Column(JSON)
    
    user = relationship("User")
    opportunity = relationship("Opportunity")

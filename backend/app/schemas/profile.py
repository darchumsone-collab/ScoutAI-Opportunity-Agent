from typing import List, Optional
from pydantic import BaseModel
from datetime import date, datetime

class SkillBase(BaseModel):
    name: str
    level: Optional[str] = None

class SkillCreate(SkillBase):
    pass

class Skill(SkillBase):
    id: int
    profile_id: int

    class Config:
        from_attributes = True

class EducationBase(BaseModel):
    institution: str
    degree: Optional[str] = None
    field_of_study: Optional[str] = None
    start_date: Optional[date] = None
    end_date: Optional[date] = None
    description: Optional[str] = None

class EducationCreate(EducationBase):
    pass

class Education(EducationBase):
    id: int
    profile_id: int

    class Config:
        from_attributes = True

class ExperienceBase(BaseModel):
    company: str
    position: str
    location: Optional[str] = None
    start_date: Optional[date] = None
    end_date: Optional[date] = None
    is_current: bool = False
    description: Optional[str] = None

class ExperienceCreate(ExperienceBase):
    pass

class Experience(ExperienceBase):
    id: int
    profile_id: int

    class Config:
        from_attributes = True

class ProfileBase(BaseModel):
    bio: Optional[str] = None
    location_preference: Optional[str] = None
    remote_preference: bool = True
    experience_level: Optional[str] = None
    career_goals: Optional[str] = None

class ProfileCreate(ProfileBase):
    pass

class ProfileUpdate(ProfileBase):
    skills: Optional[List[SkillCreate]] = None
    education: Optional[List[EducationCreate]] = None
    experience: Optional[List[ExperienceCreate]] = None

class Profile(ProfileBase):
    id: int
    user_id: int
    skills: List[Skill] = []
    education: List[Education] = []
    experience: List[Experience] = []
    created_at: datetime
    updated_at: Optional[datetime] = None

    class Config:
        from_attributes = True

class ResumeBase(BaseModel):
    is_primary: bool = False

class ResumeCreate(ResumeBase):
    file_path: str
    raw_text: Optional[str] = None

class Resume(ResumeBase):
    id: int
    user_id: int
    file_path: str
    created_at: datetime

    class Config:
        from_attributes = True

class ResumeAnalysis(BaseModel):
    id: int
    resume_id: int
    structured_data: Optional[dict] = None
    extracted_skills: Optional[dict] = None
    recommendations: Optional[dict] = None
    created_at: datetime

    class Config:
        from_attributes = True

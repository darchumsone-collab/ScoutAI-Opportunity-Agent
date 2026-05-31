from typing import Any, List
from fastapi import APIRouter, Depends, HTTPException, UploadFile, File
from sqlalchemy.orm import Session
import PyPDF2
import io

from app import models, schemas
from app.api import deps

router = APIRouter()

@router.get("/me", response_model=schemas.profile.Profile)
def read_profile(
    db: Session = Depends(deps.get_db),
    current_user: models.user.User = Depends(deps.get_current_active_user),
) -> Any:
    profile = db.query(models.profile.Profile).filter(models.profile.Profile.user_id == current_user.id).first()
    if not profile:
        profile = models.profile.Profile(user_id=current_user.id)
        db.add(profile)
        db.commit()
        db.refresh(profile)
    return profile

@router.put("/me", response_model=schemas.profile.Profile)
def update_profile(
    *,
    db: Session = Depends(deps.get_db),
    profile_in: schemas.profile.ProfileUpdate,
    current_user: models.user.User = Depends(deps.get_current_active_user),
) -> Any:
    profile = db.query(models.profile.Profile).filter(models.profile.Profile.user_id == current_user.id).first()
    if not profile:
        profile = models.profile.Profile(user_id=current_user.id)
        db.add(profile)
    
    update_data = profile_in.dict(exclude_unset=True)
    
    # Handle direct fields
    for field in ["bio", "location_preference", "remote_preference", "experience_level", "career_goals"]:
        if field in update_data:
            setattr(profile, field, update_data[field])
            
    # Handle relationships (Simplified for production: clear and replace or update existing)
    if "skills" in update_data:
        db.query(models.profile.Skill).filter(models.profile.Skill.profile_id == profile.id).delete()
        for skill_in in update_data["skills"]:
            skill = models.profile.Skill(**skill_in, profile_id=profile.id)
            db.add(skill)
            
    if "education" in update_data:
        db.query(models.profile.Education).filter(models.profile.Education.profile_id == profile.id).delete()
        for edu_in in update_data["education"]:
            edu = models.profile.Education(**edu_in, profile_id=profile.id)
            db.add(edu)

    if "experience" in update_data:
        db.query(models.profile.Experience).filter(models.profile.Experience.profile_id == profile.id).delete()
        for exp_in in update_data["experience"]:
            exp = models.profile.Experience(**exp_in, profile_id=profile.id)
            db.add(exp)
    
    db.commit()
    db.refresh(profile)
    return profile

@router.post("/resume", response_model=schemas.profile.Resume)
async def upload_resume(
    *,
    db: Session = Depends(deps.get_db),
    file: UploadFile = File(...),
    current_user: models.user.User = Depends(deps.get_current_active_user),
) -> Any:
    if file.content_type != "application/pdf":
        raise HTTPException(status_code=400, detail="Only PDF files are supported")
    
    content = await file.read()
    pdf_reader = PyPDF2.PdfReader(io.BytesIO(content))
    text = ""
    for page in pdf_reader.pages:
        text += page.extract_text()

    # Save resume record
    resume = models.profile.Resume(
        user_id=current_user.id,
        file_path=f"uploads/{current_user.id}_{file.filename}",
        raw_text=text,
        is_primary=True
    )
    
    # Mark others as not primary
    db.query(models.profile.Resume).filter(
        models.profile.Resume.user_id == current_user.id
    ).update({"is_primary": False})
    
    db.add(resume)
    db.commit()
    db.refresh(resume)
    
    # Trigger Hermes Agent for Resume Analysis (Async)
    # In production, this would be a background task via Celery
    return resume

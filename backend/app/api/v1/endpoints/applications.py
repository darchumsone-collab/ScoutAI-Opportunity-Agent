from typing import Any, List
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app import models, schemas
from app.api import deps

router = APIRouter()

@router.post("/", response_model=schemas.application.Application)
def create_application(
    *,
    db: Session = Depends(deps.get_db),
    application_in: schemas.application.ApplicationCreate,
    current_user: models.user.User = Depends(deps.get_current_active_user),
) -> Any:
    # Check if opportunity exists
    opportunity = db.query(models.opportunity.Opportunity).filter(
        models.opportunity.Opportunity.id == application_in.opportunity_id
    ).first()
    if not opportunity:
        raise HTTPException(status_code=404, detail="Opportunity not found")
    
    # Check if application already exists
    existing = db.query(models.application.Application).filter(
        models.application.Application.user_id == current_user.id,
        models.application.Application.opportunity_id == application_in.opportunity_id
    ).first()
    if existing:
        raise HTTPException(status_code=400, detail="Application already exists")

    application = models.application.Application(
        **application_in.dict(),
        user_id=current_user.id
    )
    db.add(application)
    db.commit()
    db.refresh(application)
    return application

@router.get("/", response_model=List[schemas.application.Application])
def read_applications(
    db: Session = Depends(deps.get_db),
    skip: int = 0,
    limit: int = 100,
    current_user: models.user.User = Depends(deps.get_current_active_user),
) -> Any:
    applications = db.query(models.application.Application)\
        .filter(models.application.Application.user_id == current_user.id)\
        .offset(skip).limit(limit).all()
    return applications

@router.get("/{id}", response_model=schemas.application.Application)
def read_application(
    *,
    db: Session = Depends(deps.get_db),
    id: int,
    current_user: models.user.User = Depends(deps.get_current_active_user),
) -> Any:
    application = db.query(models.application.Application).filter(
        models.application.Application.id == id,
        models.application.Application.user_id == current_user.id
    ).first()
    if not application:
        raise HTTPException(status_code=404, detail="Application not found")
    return application

@router.put("/{id}", response_model=schemas.application.Application)
def update_application(
    *,
    db: Session = Depends(deps.get_db),
    id: int,
    application_in: schemas.application.ApplicationUpdate,
    current_user: models.user.User = Depends(deps.get_current_active_user),
) -> Any:
    application = db.query(models.application.Application).filter(
        models.application.Application.id == id,
        models.application.Application.user_id == current_user.id
    ).first()
    if not application:
        raise HTTPException(status_code=404, detail="Application not found")
    
    update_data = application_in.dict(exclude_unset=True)
    for field in update_data:
        setattr(application, field, update_data[field])
    
    db.commit()
    db.refresh(application)
    return application

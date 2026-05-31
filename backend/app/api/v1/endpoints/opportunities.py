from typing import Any, List
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app import models, schemas
from app.api import deps

router = APIRouter()

@router.get("/", response_model=List[schemas.opportunity.Opportunity])
def read_opportunities(
    db: Session = Depends(deps.get_db),
    skip: int = 0,
    limit: int = 100,
    current_user: models.user.User = Depends(deps.get_current_active_user),
) -> Any:
    opportunities = db.query(models.opportunity.Opportunity).offset(skip).limit(limit).all()
    return opportunities

@router.get("/{id}", response_model=schemas.opportunity.Opportunity)
def read_opportunity(
    *,
    db: Session = Depends(deps.get_db),
    id: int,
    current_user: models.user.User = Depends(deps.get_current_active_user),
) -> Any:
    opportunity = db.query(models.opportunity.Opportunity).filter(models.opportunity.Opportunity.id == id).first()
    if not opportunity:
        raise HTTPException(status_code=404, detail="Opportunity not found")
    return opportunity

@router.get("/matches", response_model=List[schemas.opportunity.MatchResult])
def read_matches(
    db: Session = Depends(deps.get_db),
    current_user: models.user.User = Depends(deps.get_current_active_user),
) -> Any:
    matches = db.query(models.opportunity.MatchResult)\
        .filter(models.opportunity.MatchResult.user_id == current_user.id)\
        .order_by(models.opportunity.MatchResult.score.desc()).all()
    return matches

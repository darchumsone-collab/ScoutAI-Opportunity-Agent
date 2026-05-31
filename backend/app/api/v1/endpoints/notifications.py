from typing import Any, List
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app import models, schemas
from app.api import deps

router = APIRouter()

@router.get("/", response_model=List[schemas.notification.Notification])
def read_notifications(
    db: Session = Depends(deps.get_db),
    current_user: models.user.User = Depends(deps.get_current_active_user),
    skip: int = 0,
    limit: int = 100,
) -> Any:
    notifications = db.query(models.notification.Notification).filter(
        models.notification.Notification.user_id == current_user.id
    ).order_by(models.notification.Notification.created_at.desc()).offset(skip).limit(limit).all()
    return notifications

@router.put("/{id}", response_model=schemas.notification.Notification)
def update_notification(
    *,
    db: Session = Depends(deps.get_db),
    id: int,
    notification_in: schemas.notification.NotificationUpdate,
    current_user: models.user.User = Depends(deps.get_current_active_user),
) -> Any:
    notification = db.query(models.notification.Notification).filter(
        models.notification.Notification.id == id,
        models.notification.Notification.user_id == current_user.id
    ).first()
    if not notification:
        raise HTTPException(status_code=404, detail="Notification not found")
    
    notification.is_read = notification_in.is_read
    db.add(notification)
    db.commit()
    db.refresh(notification)
    return notification

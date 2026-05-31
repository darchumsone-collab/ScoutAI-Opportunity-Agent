from sqlalchemy.orm import Session
from app.models.application import AuditLog
from typing import Any, Dict, Optional

class AuditService:
    @staticmethod
    def log_action(
        db: Session,
        user_id: Optional[int],
        action: str,
        resource_type: Optional[str] = None,
        resource_id: Optional[str] = None,
        details: Optional[Dict[str, Any]] = None,
        ip_address: Optional[str] = None
    ):
        audit_entry = AuditLog(
            user_id=user_id,
            action=action,
            resource_type=resource_type,
            resource_id=resource_id,
            details=details,
            ip_address=ip_address
        )
        db.add(audit_entry)
        db.commit()

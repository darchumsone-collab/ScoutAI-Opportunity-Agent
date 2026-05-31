from typing import Any, List
from fastapi import APIRouter, Depends, HTTPException, BackgroundTasks
from sqlalchemy.orm import Session

from app import models, schemas
from app.api import deps
from app.services.agent_service import HermesAgent

router = APIRouter()

@router.post("/discovery", response_model=schemas.agent.AgentExecution)
async def start_discovery(
    *,
    db: Session = Depends(deps.get_db),
    background_tasks: BackgroundTasks,
    current_user: models.user.User = Depends(deps.get_current_active_user),
) -> Any:
    profile = db.query(models.profile.Profile).filter(models.profile.Profile.user_id == current_user.id).first()
    if not profile:
        raise HTTPException(status_code=400, detail="Profile must be completed before discovery")

    execution = models.agent.AgentExecution(
        user_id=current_user.id,
        task_type="opportunity_discovery",
        input_data={"profile_id": profile.id, "interests": profile.interests}
    )
    db.add(execution)
    db.commit()
    db.refresh(execution)

    async def run_agent_discovery(exec_id: int, user_id: int):
        db_session = deps.SessionLocal()
        try:
            agent = HermesAgent(db_session, user_id, exec_id)
            user_profile_data = {
                "skills": profile.skills,
                "interests": profile.interests,
                "location": profile.location_preference
            }
            await agent.run_discovery(user_profile_data)
        finally:
            db_session.close()

    background_tasks.add_task(run_agent_discovery, execution.id, current_user.id)
    
    return execution

@router.post("/plan/{opportunity_id}", response_model=schemas.agent.AgentExecution)
async def generate_application_plan(
    *,
    db: Session = Depends(deps.get_db),
    opportunity_id: Int,
    background_tasks: BackgroundTasks,
    current_user: models.user.User = Depends(deps.get_current_active_user),
) -> Any:
    opportunity = db.query(models.opportunity.Opportunity).get(opportunity_id)
    if not opportunity:
        raise HTTPException(status_code=404, detail="Opportunity not found")

    execution = models.agent.AgentExecution(
        user_id=current_user.id,
        task_type="application_planning",
        input_data={"opportunity_id": opportunity_id}
    )
    db.add(execution)
    db.commit()
    db.refresh(execution)

    async def run_agent_planning(exec_id: int, user_id: int, opp_id: int):
        db_session = deps.SessionLocal()
        try:
            agent = HermesAgent(db_session, user_id, exec_id)
            query = f"Generate a complete application plan, cover letter, and timeline for opportunity ID {opp_id}."
            await agent.agent_executor.ainvoke({"input": query, "chat_history": []})
            
            exec_record = db_session.query(models.agent.AgentExecution).get(exec_id)
            exec_record.status = models.agent.AgentStatus.COMPLETED
            db_session.commit()
        except Exception as e:
            exec_record = db_session.query(models.agent.AgentExecution).get(exec_id)
            exec_record.status = models.agent.AgentStatus.FAILED
            exec_record.error_message = str(e)
            db_session.commit()
        finally:
            db_session.close()

    background_tasks.add_task(run_agent_planning, execution.id, current_user.id, opportunity_id)
    
    return execution

@router.get("/executions", response_model=List[schemas.agent.AgentExecution])
def read_executions(
    db: Session = Depends(deps.get_db),
    current_user: models.user.User = Depends(deps.get_current_active_user),
    skip: int = 0,
    limit: int = 100,
) -> Any:
    executions = db.query(models.agent.AgentExecution)\
        .filter(models.agent.AgentExecution.user_id == current_user.id)\
        .order_by(models.agent.AgentExecution.created_at.desc())\
        .offset(skip).limit(limit).all()
    return executions

@router.get("/executions/{execution_id}", response_model=schemas.agent.AgentExecution)
def read_execution(
    execution_id: int,
    db: Session = Depends(deps.get_db),
    current_user: models.user.User = Depends(deps.get_current_active_user),
) -> Any:
    execution = db.query(models.agent.AgentExecution).filter(
        models.agent.AgentExecution.id == execution_id,
        models.agent.AgentExecution.user_id == current_user.id
    ).first()
    if not execution:
        raise HTTPException(status_code=404, detail="Execution not found")
    return execution

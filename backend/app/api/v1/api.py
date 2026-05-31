from fastapi import APIRouter
from app.api.v1.endpoints import login, users, opportunities, profiles, agents, applications

api_router = APIRouter()
api_router.include_router(login.router, tags=["login"])
api_router.include_router(users.router, prefix="/users", tags=["users"])
api_router.include_router(profiles.router, prefix="/profiles", tags=["profiles"])
api_router.include_router(opportunities.router, prefix="/opportunities", tags=["opportunities"])
api_router.include_router(agents.router, prefix="/agents", tags=["agents"])
api_router.include_router(applications.router, prefix="/applications", tags=["applications"])

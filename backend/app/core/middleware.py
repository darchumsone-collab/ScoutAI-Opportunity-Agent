import time
from fastapi import Request, HTTPException
from starlette.middleware.base import BaseHTTPMiddleware
from typing import Dict, Tuple

class RateLimitMiddleware(BaseHTTPMiddleware):
    def __init__(self, app, requests_limit: int = 100, window_seconds: int = 60):
        super().__init__(app)
        self.requests_limit = requests_limit
        self.window_seconds = window_seconds
        self.clients: Dict[str, List[float]] = {}

    async def dispatch(self, request: Request, call_next):
        client_ip = request.client.host
        now = time.time()
        
        if client_ip not in self.clients:
            self.clients[client_ip] = []
        
        # Filter requests within the window
        self.clients[client_ip] = [t for t in self.clients[client_ip] if now - t < self.window_seconds]
        
        if len(self.clients[client_ip]) >= self.requests_limit:
            raise HTTPException(status_code=429, detail="Too many requests")
            
        self.clients[client_ip].append(now)
        response = await call_next(request)
        return response

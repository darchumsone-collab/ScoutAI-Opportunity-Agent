# ScoutAI Opportunity Agent

An AI-powered discovery platform for jobs, scholarships, and grants.

## 🚀 Getting Started

### Backend Setup (FastAPI)
1. Navigate to the `backend` folder.
2. Activate your virtual environment:
   ```powershell
   .\venv\Scripts\Activate.ps1
   ```
3. Install dependencies:
   ```powershell
   pip install sqlalchemy uvicorn fastapi psycopg2-binary pydantic-settings python-multipart
   pip install -r requirements.txt
   ```
4. **Crucial:** Start the server using the host flag to allow real device connectivity:
   ```powershell
   python -m uvicorn app.main:app --host 0.0.0.0 --port 8000
   ```

### Android App Setup
1. Open the project in Android Studio.
2. The app is configured to connect to the backend at `192.168.8.254`.
3. If your PC's IP changes, update `ApiConfig` in `NetworkModule.kt`.

## 🛠 Troubleshooting Connectivity
If your physical device cannot connect to the backend:
1. **Firewall:** Add an Inbound Rule in Windows Firewall to allow TCP port `8000`.
2. **Wi-Fi:** Ensure both the phone and PC are on the same Wi-Fi network.
3. **Host Flag:** Verify the backend was started with `--host 0.0.0.0`.
4. **Diagnostics:** Long-press the "Discover Opportunities" title in the app to open the Network Diagnostics panel.

## 📱 Features
- AI-driven opportunity discovery.
- Real-time search with debouncing.
- Category filtering with Bottom Sheet.
- Real-device networking support.

import emails
from emails.template import JinjaTemplate
from app.core.config import settings
from typing import Any, Dict

def send_email(
    email_to: str,
    subject_template: str = "",
    html_template: str = "",
    template_data: Dict[str, Any] = {},
) -> None:
    message = emails.Message(
        subject=JinjaTemplate(subject_template),
        html=JinjaTemplate(html_template),
        mail_from=(settings.PROJECT_NAME, "no-reply@scoutai.com"),
    )
    # In a real production environment, use SMTP settings from config
    # For now, we simulate the send or use a local SMTP/Mailtrap if configured
    # response = message.send(to=email_to, render=template_data, smtp=settings.SMTP_CONFIG)
    print(f"Sending email to {email_to} with subject: {subject_template}")

def send_reset_password_email(email_to: str, email: str, token: str) -> None:
    project_name = settings.PROJECT_NAME
    subject = f"{project_name} - Password recovery for user {email}"
    link = f"{settings.BACKEND_CORS_ORIGINS[0]}/reset-password?token={token}"
    send_email(
        email_to=email_to,
        subject_template=subject,
        html_template="""
            <p>Hello,</p>
            <p>You requested a password reset for your {{ project_name }} account.</p>
            <p>Please click the link below to reset your password:</p>
            <a href="{{ link }}">{{ link }}</a>
            <p>If you did not request this, please ignore this email.</p>
        """,
        template_data={"project_name": project_name, "link": link},
    )

"""initial migration

Revision ID: 6f0a3b2c1d4e
Revises: 
Create Date: 2024-10-25 10:00:00.000000

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '6f0a3b2c1d4e'
down_revision = None
branch_labels = None
depends_on = None


def upgrade() -> None:
    # Users table
    op.create_table(
        'users',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('email', sa.String(), nullable=False),
        sa.Column('hashed_password', sa.String(), nullable=False),
        sa.Column('full_name', sa.String(), nullable=True),
        sa.Column('is_active', sa.Boolean(), nullable=True),
        sa.Column('is_verified', sa.Boolean(), nullable=True),
        sa.Column('role', sa.Enum('GUEST', 'REGISTERED', 'PREMIUM', 'ADMIN', name='userrole'), nullable=True),
        sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.text('now()'), nullable=True),
        sa.Column('updated_at', sa.DateTime(timezone=True), nullable=True),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_users_email'), 'users', ['email'], unique=True)
    op.create_index(op.f('ix_users_id'), 'users', ['id'], unique=False)

    # Profiles table
    op.create_table(
        'profiles',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('user_id', sa.Integer(), nullable=False),
        sa.Column('skills', sa.JSON(), nullable=True),
        sa.Column('interests', sa.JSON(), nullable=True),
        sa.Column('experience_level', sa.String(), nullable=True),
        sa.Column('location_preference', sa.String(), nullable=True),
        sa.Column('career_goals', sa.Text(), nullable=True),
        sa.Column('bio', sa.Text(), nullable=True),
        sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.text('now()'), nullable=True),
        sa.Column('updated_at', sa.DateTime(timezone=True), nullable=True),
        sa.ForeignKeyConstraint(['user_id'], ['users.id'], ),
        sa.PrimaryKeyConstraint('id'),
        sa.UniqueConstraint('user_id')
    )
    op.create_index(op.f('ix_profiles_id'), 'profiles', ['id'], unique=False)

    # Opportunities table
    op.create_table(
        'opportunities',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('title', sa.String(), nullable=False),
        sa.Column('organization', sa.String(), nullable=True),
        sa.Column('description', sa.Text(), nullable=True),
        sa.Column('opportunity_type', sa.Enum('JOB', 'INTERNSHIP', 'SCHOLARSHIP', 'GRANT', 'COMPETITION', 'HACKATHON', 'FELLOWSHIP', 'TENDER', name='opportunitytype'), nullable=False),
        sa.Column('location', sa.String(), nullable=True),
        sa.Column('remote', sa.Boolean(), nullable=True),
        sa.Column('url', sa.String(), nullable=True),
        sa.Column('deadline', sa.DateTime(timezone=True), nullable=True),
        sa.Column('requirements', sa.JSON(), nullable=True),
        sa.Column('salary_range', sa.String(), nullable=True),
        sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.text('now()'), nullable=True),
        sa.Column('updated_at', sa.DateTime(timezone=True), nullable=True),
        sa.PrimaryKeyConstraint('id'),
        sa.UniqueConstraint('url')
    )
    op.create_index(op.f('ix_opportunities_id'), 'opportunities', ['id'], unique=False)
    op.create_index(op.f('ix_opportunities_organization'), 'opportunities', ['organization'], unique=False)
    op.create_index(op.f('ix_opportunities_title'), 'opportunities', ['title'], unique=False)

    # Match results table
    op.create_table(
        'match_results',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('user_id', sa.Integer(), nullable=False),
        sa.Column('opportunity_id', sa.Integer(), nullable=False),
        sa.Column('score', sa.Float(), nullable=True),
        sa.Column('reasoning', sa.Text(), nullable=True),
        sa.Column('gap_analysis', sa.JSON(), nullable=True),
        sa.ForeignKeyConstraint(['opportunity_id'], ['opportunities.id'], ),
        sa.ForeignKeyConstraint(['user_id'], ['users.id'], ),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_match_results_id'), 'match_results', ['id'], unique=False)

    # Applications table
    op.create_table(
        'applications',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('user_id', sa.Integer(), nullable=False),
        sa.Column('opportunity_id', sa.Integer(), nullable=False),
        sa.Column('status', sa.Enum('PLANNED', 'IN_PROGRESS', 'SUBMITTED', 'INTERVIEWING', 'OFFERED', 'REJECTED', 'WITHDRAWN', name='applicationstatus'), nullable=True),
        sa.Column('notes', sa.Text(), nullable=True),
        sa.Column('application_plan', sa.JSON(), nullable=True),
        sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.text('now()'), nullable=True),
        sa.Column('updated_at', sa.DateTime(timezone=True), nullable=True),
        sa.ForeignKeyConstraint(['opportunity_id'], ['opportunities.id'], ),
        sa.ForeignKeyConstraint(['user_id'], ['users.id'], ),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_applications_id'), 'applications', ['id'], unique=False)


def downgrade() -> None:
    op.drop_table('applications')
    op.drop_table('match_results')
    op.drop_index(op.f('ix_opportunities_title'), table_name='opportunities')
    op.drop_index(op.f('ix_opportunities_organization'), table_name='opportunities')
    op.drop_index(op.f('ix_opportunities_id'), table_name='opportunities')
    op.drop_table('opportunities')
    op.drop_index(op.f('ix_profiles_id'), table_name='profiles')
    op.drop_table('profiles')
    op.drop_index(op.f('ix_users_id'), table_name='users')
    op.drop_index(op.f('ix_users_email'), table_name='users')
    op.drop_table('users')

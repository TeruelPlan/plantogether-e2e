-- Additional databases for PlanTogether services.
-- plantogether_trip is created automatically by POSTGRES_DB env var.
CREATE DATABASE plantogether_poll;
CREATE DATABASE plantogether_destination;
CREATE DATABASE plantogether_expense;
CREATE DATABASE plantogether_task;
CREATE DATABASE plantogether_chat;
CREATE DATABASE plantogether_notification;

GRANT ALL PRIVILEGES ON DATABASE plantogether_poll TO plantogether;
GRANT ALL PRIVILEGES ON DATABASE plantogether_destination TO plantogether;
GRANT ALL PRIVILEGES ON DATABASE plantogether_expense TO plantogether;
GRANT ALL PRIVILEGES ON DATABASE plantogether_task TO plantogether;
GRANT ALL PRIVILEGES ON DATABASE plantogether_chat TO plantogether;
GRANT ALL PRIVILEGES ON DATABASE plantogether_notification TO plantogether;

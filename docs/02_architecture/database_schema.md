# Database Schema

## users table
```sql
CREATE TABLE users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,  -- BCrypt hashed
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
```

## notes table
```sql
CREATE TABLE notes (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title       VARCHAR(255) NOT NULL,
    content     TEXT NOT NULL,
    owner_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    pinned      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
```

## note_shares table (many-to-many)
```sql
CREATE TABLE note_shares (
    note_id     UUID NOT NULL REFERENCES notes(id) ON DELETE CASCADE,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    shared_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (note_id, user_id)
);
```

## Indexes
```sql
CREATE INDEX idx_notes_owner_id ON notes(owner_id);
CREATE INDEX idx_note_shares_user_id ON note_shares(user_id);
CREATE INDEX idx_users_email ON users(email);
```

## JPA will auto-create these tables via ddl-auto=update

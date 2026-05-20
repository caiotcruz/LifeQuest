-- ============================================================
--  LifeQuest — V1: Schema inicial
--  Flyway migration
-- ============================================================

-- ── Users ────────────────────────────────────────────────────
CREATE TABLE users (
    id                              BIGSERIAL PRIMARY KEY,
    username                        VARCHAR(50)  NOT NULL,
    email                           VARCHAR(150) NOT NULL,
    password_hash                   VARCHAR(255) NOT NULL,
    avatar                          VARCHAR(255),
    level                           INT          NOT NULL DEFAULT 1,
    total_xp                        BIGINT       NOT NULL DEFAULT 0,
    current_streak                  INT          NOT NULL DEFAULT 0,
    longest_streak                  INT          NOT NULL DEFAULT 0,
    streak_recovery_count           INT          NOT NULL DEFAULT 0,
    streak_recovery_used_this_month INT          NOT NULL DEFAULT 0,
    is_active                       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at                      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at                      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_users_email    UNIQUE (email),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT chk_users_level   CHECK (level >= 1),
    CONSTRAINT chk_users_xp      CHECK (total_xp >= 0)
);

-- ── Activities (catálogo) ─────────────────────────────────────
CREATE TABLE activities (
    id            BIGSERIAL PRIMARY KEY,
    title         VARCHAR(100) NOT NULL,
    description   VARCHAR(255),
    category      VARCHAR(30)  NOT NULL,
    xp_reward     INT          NOT NULL DEFAULT 20,
    icon_name     VARCHAR(50),
    is_predefined BOOLEAN      NOT NULL DEFAULT TRUE,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT chk_activities_xp       CHECK (xp_reward > 0),
    CONSTRAINT chk_activities_category CHECK (category IN (
        'HEALTH', 'STUDY', 'WORK', 'PERSONAL_DEVELOPMENT', 'CREATIVITY'
    ))
);

-- ── User Activities (log de conclusões) ───────────────────────
CREATE TABLE user_activities (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT       NOT NULL REFERENCES users(id)      ON DELETE CASCADE,
    activity_id      BIGINT       NOT NULL REFERENCES activities(id) ON DELETE RESTRICT,
    completed_date   DATE         NOT NULL,
    completed_at     TIMESTAMPTZ  NOT NULL,
    xp_earned        INT          NOT NULL,
    duration_minutes INT,
    notes            VARCHAR(255),
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_ua_xp CHECK (xp_earned >= 0)
);

CREATE INDEX idx_ua_user_date     ON user_activities (user_id, completed_date);
CREATE INDEX idx_ua_user_activity ON user_activities (user_id, activity_id);
CREATE INDEX idx_ua_completed_at  ON user_activities (completed_at);

-- ── User Schedules (agenda/recorrência) ───────────────────────
CREATE TABLE user_schedules (
    id              BIGSERIAL   PRIMARY KEY,
    user_id         BIGINT      NOT NULL REFERENCES users(id)      ON DELETE CASCADE,
    activity_id     BIGINT      NOT NULL REFERENCES activities(id) ON DELETE RESTRICT,
    recurrence_type VARCHAR(20) NOT NULL,
    start_date      DATE        NOT NULL,
    end_date        DATE,
    is_active       BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_us_recurrence CHECK (recurrence_type IN (
        'ONCE', 'DAILY', 'WEEKLY', 'MONTHLY'
    ))
);

CREATE INDEX idx_us_user_active ON user_schedules (user_id, is_active);

-- ── Schedule Days of Week ─────────────────────────────────────
CREATE TABLE schedule_days_of_week (
    schedule_id BIGINT     NOT NULL REFERENCES user_schedules(id) ON DELETE CASCADE,
    day_of_week VARCHAR(9) NOT NULL,
    PRIMARY KEY (schedule_id, day_of_week),
    CONSTRAINT chk_day_of_week CHECK (day_of_week IN (
        'MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY'
    ))
);

-- ── Schedule Days of Month ────────────────────────────────────
CREATE TABLE schedule_days_of_month (
    schedule_id  BIGINT NOT NULL REFERENCES user_schedules(id) ON DELETE CASCADE,
    day_of_month INT    NOT NULL,
    PRIMARY KEY (schedule_id, day_of_month),
    CONSTRAINT chk_day_of_month CHECK (day_of_month BETWEEN 1 AND 31)
);

-- ── Badges (catálogo) ─────────────────────────────────────────
CREATE TABLE badges (
    id          BIGSERIAL PRIMARY KEY,
    badge_type  VARCHAR(50)  NOT NULL UNIQUE,
    title       VARCHAR(80)  NOT NULL,
    description VARCHAR(255) NOT NULL,
    icon_name   VARCHAR(50),
    xp_bonus    INT          NOT NULL DEFAULT 0
);

-- ── User Badges (conquistas) ──────────────────────────────────
CREATE TABLE user_badges (
    id        BIGSERIAL   PRIMARY KEY,
    user_id   BIGINT      NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    badge_id  BIGINT      NOT NULL REFERENCES badges(id) ON DELETE RESTRICT,
    earned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_user_badge UNIQUE (user_id, badge_id)
);

CREATE INDEX idx_ub_user ON user_badges (user_id);

-- ── XP Level Config ───────────────────────────────────────────
CREATE TABLE xp_level_config (
    level       INT         PRIMARY KEY,
    xp_required BIGINT      NOT NULL,
    title       VARCHAR(50),

    CONSTRAINT chk_level_positive CHECK (level > 0),
    CONSTRAINT chk_xp_positive    CHECK (xp_required >= 0)
);

-- ── Updated_at auto-trigger ───────────────────────────────────
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();


-- ============================================================
--  LifeQuest — V2: Seed de atividades pré-definidas e níveis
-- ============================================================

-- ── XP Level Config (progressão exponencial) ─────────────────
INSERT INTO xp_level_config (level, xp_required, title) VALUES
(1,  0,       'Iniciante'),
(2,  100,     'Aprendiz'),
(3,  250,     'Dedicado'),
(4,  500,     'Comprometido'),
(5,  900,     'Focado'),
(6,  1400,    'Disciplinado'),
(7,  2100,    'Consistente'),
(8,  3000,    'Determinado'),
(9,  4100,    'Persistente'),
(10, 5500,    'Veterano'),
(15, 15000,   'Experiente'),
(20, 32000,   'Mestre'),
(25, 60000,   'Grande Mestre'),
(30, 100000,  'Lendário'),
(40, 200000,  'Mítico'),
(50, 400000,  'Imortal');

-- ── Saúde e Fitness ───────────────────────────────────────────
INSERT INTO activities (title, description, category, xp_reward, icon_name, is_predefined) VALUES
('Academia',        'Treino na academia',              'HEALTH', 20, 'dumbbell',     TRUE),
('Corrida',         'Corrida ao ar livre ou esteira',  'HEALTH', 20, 'run',          TRUE),
('Caminhada',       'Caminhada leve ou moderada',      'HEALTH', 15, 'walk',         TRUE),
('Yoga',            'Sessão de yoga',                  'HEALTH', 20, 'yoga',         TRUE),
('Alongamento',     'Exercícios de alongamento',       'HEALTH', 10, 'stretch',      TRUE),
('Treino Futebol',  'Treino ou jogo de futebol',       'HEALTH', 20, 'ball',         TRUE),
('Treino Basquete', 'Treino ou jogo de basquete',      'HEALTH', 20, 'basketball',   TRUE),
('Treino Vôlei',    'Treino ou jogo de vôlei',         'HEALTH', 20, 'ball',         TRUE),
('Beach Tennis',    'Treino de beach tennis',          'HEALTH', 20, 'tennis',       TRUE),
('Natação',         'Sessão de natação',               'HEALTH', 20, 'swim',         TRUE),
('Ciclismo',        'Pedal ou bicicleta ergométrica',  'HEALTH', 20, 'bike',         TRUE),
('Crossfit',        'Treino de crossfit',              'HEALTH', 25, 'barbell',      TRUE),
('Dormir cedo',     'Dormir antes das 23h',            'HEALTH', 15, 'moon',         TRUE),
('Beber água',      'Atingir meta diária de água',     'HEALTH', 10, 'droplet',      TRUE);

-- ── Estudos ───────────────────────────────────────────────────
INSERT INTO activities (title, description, category, xp_reward, icon_name, is_predefined) VALUES
('Estudar programação', 'Sessão de estudo de código',     'STUDY', 25, 'code',       TRUE),
('Estudar matemática',  'Exercícios ou teoria de mat.',   'STUDY', 25, 'math',       TRUE),
('Estudar idiomas',     'Prática de idioma estrangeiro',  'STUDY', 25, 'language',   TRUE),
('Resolver exercícios', 'Lista de exercícios/problemas',  'STUDY', 25, 'pencil',     TRUE),
('Ler livro técnico',   'Leitura de livro da área',       'STUDY', 20, 'book',       TRUE),
('Fazer revisão',       'Revisar conteúdo estudado',      'STUDY', 20, 'refresh',    TRUE),
('Assistir aula',       'Aula online ou presencial',      'STUDY', 20, 'video',      TRUE);

-- ── Trabalho ──────────────────────────────────────────────────
INSERT INTO activities (title, description, category, xp_reward, icon_name, is_predefined) VALUES
('Projeto pessoal', 'Trabalhar em projeto próprio',      'WORK', 30, 'rocket',        TRUE),
('Trabalho focado', 'Bloco de trabalho profundo',        'WORK', 30, 'focus',         TRUE),
('Reunião',         'Reunião de trabalho ou mentoria',   'WORK', 20, 'users',         TRUE),
('Planejamento',    'Planejar semana ou dia',            'WORK', 25, 'calendar',      TRUE),
('Organização',     'Organizar arquivos, tarefas, etc.', 'WORK', 20, 'folder',        TRUE),
('Networking',      'Conectar com profissionais',        'WORK', 20, 'network',       TRUE);

-- ── Desenvolvimento Pessoal ───────────────────────────────────
INSERT INTO activities (title, description, category, xp_reward, icon_name, is_predefined) VALUES
('Meditação',          'Sessão de meditação',             'PERSONAL_DEVELOPMENT', 20, 'brain',   TRUE),
('Diário',             'Escrever no diário pessoal',      'PERSONAL_DEVELOPMENT', 15, 'journal', TRUE),
('Leitura',            'Leitura de livro não-técnico',    'PERSONAL_DEVELOPMENT', 20, 'book',    TRUE),
('Organização pessoal','Organizar ambiente ou rotina',    'PERSONAL_DEVELOPMENT', 15, 'home',    TRUE),
('Terapia',            'Sessão de terapia/psicólogo',    'PERSONAL_DEVELOPMENT', 20, 'heart',   TRUE),
('Journaling',         'Reflexão escrita detalhada',      'PERSONAL_DEVELOPMENT', 15, 'pen',     TRUE);

-- ── Criatividade ──────────────────────────────────────────────
INSERT INTO activities (title, description, category, xp_reward, icon_name, is_predefined) VALUES
('Desenho',         'Sessão de desenho ou ilustração',   'CREATIVITY', 15, 'pencil',  TRUE),
('Música',          'Praticar instrumento ou cantar',    'CREATIVITY', 15, 'music',   TRUE),
('Escrita',         'Escrever texto criativo',           'CREATIVITY', 15, 'pen',     TRUE),
('Fotografia',      'Sessão fotográfica',                'CREATIVITY', 15, 'camera',  TRUE),
('Edição de vídeo', 'Editar vídeo ou conteúdo',         'CREATIVITY', 15, 'video',   TRUE);

-- ── Badges ────────────────────────────────────────────────────
INSERT INTO badges (badge_type, title, description, icon_name, xp_bonus) VALUES
-- Consistência
('STREAK_7',           '7 Dias Seguidos',    'Completou metas por 7 dias consecutivos',   'fire',       50),
('STREAK_30',          'Mês Completo',       'Completou metas por 30 dias consecutivos',  'fire',      150),
('STREAK_100',         '100 Dias!',          'Completou metas por 100 dias consecutivos', 'fire',      500),
-- Fitness
('WORKOUTS_20',        '20 Treinos',         'Concluiu 20 atividades físicas',            'dumbbell',   80),
('FULL_WEEK_FITNESS',  'Semana Fitness',     '7 dias seguidos com atividade física',      'trophy',    100),
('KM_100_RUNNING',     '100 km Corridos',    'Acumulou 100 km de corrida',               'run',       200),
-- Estudos
('STUDY_50H',          '50 Horas Estudadas', 'Acumulou 50 horas de estudo',              'book',      200),
('STUDY_SESSIONS_30',  '30 Sessões de Estudo','Concluiu 30 sessões de estudo',           'graduation', 100),
('STUDY_STREAK_7',     'Estudando Sempre',   '7 dias seguidos estudando',                'star',       80),
-- Trabalho
('WORK_TASKS_100',     'Centenário',         'Concluiu 100 tarefas profissionais',        'briefcase', 150),
('PRODUCTIVE_DAYS_30', '30 Dias Produtivos', '30 dias com tarefas de trabalho',          'chart',     150),
-- Level
('LEVEL_10',           'Veterano',           'Alcançou o nível 10',                       'medal',     100),
('LEVEL_25',           'Grande Mestre',      'Alcançou o nível 25',                       'crown',     300),
('LEVEL_50',           'Lendário',           'Alcançou o nível 50',                       'star',      500),
-- Especiais
('FIRST_ACTIVITY',     'Primeiro Passo',     'Concluiu a primeira atividade',             'flag',       20),
('PERFECT_WEEK',       'Semana Perfeita',    'Completou 100% das metas em uma semana',   'gem',       100),
('EARLY_BIRD',         'Madrugador',         'Concluiu atividade antes das 7h por 5 dias','sunrise',   75);
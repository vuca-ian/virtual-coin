CREATE TABLE IF NOT EXISTS t_user_access
(
    id VARCHAR(32) NOT NULL,
    user_name VARCHAR(32) NOT NULL,
    access_key  VARCHAR(256) NOT NULL,
    secret_key VARCHAR(256) NOT NULL,
    state VARCHAR(32) NULL,
    created_time  TIMESTAMP(0),
    created_by    VARCHAR(32),
    modified_time TIMESTAMP(0),
    modified_by   VARCHAR(32),
    deleted       BOOLEAN NOT NULL,
    primary key (id)
);
COMMENT ON TABLE t_user_access IS '用户';
COMMENT ON COLUMN t_user_access.user_name IS '用户名';
COMMENT ON COLUMN t_user_access.access_key IS '访问KEY';
COMMENT ON COLUMN t_user_access.secret_key IS '密钥';
COMMENT ON COLUMN t_user_access.state IS '状态';
COMMENT ON COLUMN t_user_access.created_time is '创建时间';
COMMENT ON COLUMN t_user_access.created_by is '创建人';
COMMENT ON COLUMN t_user_access.modified_time is '修改时间';
COMMENT ON COLUMN t_user_access.modified_by is '修改人';
COMMENT ON COLUMN t_user_access.deleted is '删除状态， 0-未删除  1-已删除';


CREATE TABLE IF NOT EXISTS t_account
(
    id VARCHAR(32) NOT NULL,
    user_id VARCHAR(32) NOT NULL,
    account_id  INT8 NOT NULL,
    type VARCHAR(256) NOT NULL,
    subtype VARCHAR(32) NULL,
    state VARCHAR(32) NULL,
    created_time  TIMESTAMP(0),
    created_by    VARCHAR(32),
    modified_time TIMESTAMP(0),
    modified_by   VARCHAR(32),
    deleted       BOOLEAN NOT NULL,
    primary key (id)
    );
COMMENT ON TABLE t_account IS '账户表';
COMMENT ON COLUMN t_account.user_id IS '用户id';
COMMENT ON COLUMN t_account.account_id IS '账号id';
COMMENT ON COLUMN t_account.type IS '账号类型';
COMMENT ON COLUMN t_account.state IS '状态';
COMMENT ON COLUMN t_account.created_time is '创建时间';
COMMENT ON COLUMN t_account.created_by is '创建人';
COMMENT ON COLUMN t_account.modified_time is '修改时间';
COMMENT ON COLUMN t_account.modified_by is '修改人';
COMMENT ON COLUMN t_account.deleted is '删除状态， 0-未删除  1-已删除';

CREATE TABLE IF NOT EXISTS t_indicator
(
    id VARCHAR(32) NOT NULL,
    indicator VARCHAR(32) NOT NULL,
    primary key (id)
    );
COMMENT ON TABLE t_indicator IS '指示器';


CREATE TABLE IF NOT EXISTS t_job_history
(
    id VARCHAR(32) NOT NULL,
    symbol VARCHAR(32) NOT NULL,
    period VARCHAR(32) NOT NULL,
    last_data_time varchar(20) NOT NULL,
    loop_count int4 NOT NULL,
    primary key (id)
    );
COMMENT ON TABLE t_job_history IS '任务历史表';
COMMENT ON COLUMN t_job_history.symbol IS '符号';
COMMENT ON COLUMN t_job_history.period IS '周期';
COMMENT ON COLUMN t_job_history.last_data_time IS '最后一条数据时间';
COMMENT ON COLUMN t_job_history.loop_count IS '遍历次数';


CREATE TABLE IF NOT EXISTS t_candlestick_ethusdt_1min
(
    id VARCHAR(32) NOT NULL,
    symbol VARCHAR(32) NOT NULL,
    period VARCHAR(32) NOT NULL,
    count varchar(20) NOT NULL,
    amount VARCHAR(32) NOT NULL,
    open decimal(8,4) NOT NULL,
    close decimal(8,4) NOT NULL,
    low decimal(8,4) NOT NULL,
    high decimal(8,4) NOT NULL,
    vol VARCHAR(32) NOT NULL,
    analysis VARCHAR(32) NOT NULL,
    indicator VARCHAR(32) NOT NULL,
    primary key (id)
);
COMMENT ON TABLE t_candlestick_ethusdt_1min IS 'K线烛台';
COMMENT ON COLUMN t_candlestick_ethusdt_1min.symbol IS '符号';
COMMENT ON COLUMN t_candlestick_ethusdt_1min.period IS '周期';
COMMENT ON COLUMN t_candlestick_ethusdt_1min.count IS '数量';
COMMENT ON COLUMN t_candlestick_ethusdt_1min.amount IS '总量';
COMMENT ON COLUMN t_candlestick_ethusdt_1min.open IS '开始';
COMMENT ON COLUMN t_candlestick_ethusdt_1min.close IS '结束';
COMMENT ON COLUMN t_candlestick_ethusdt_1min.low IS '低';
COMMENT ON COLUMN t_candlestick_ethusdt_1min.high IS '高';
COMMENT ON COLUMN t_candlestick_ethusdt_1min.vol IS '量';
COMMENT ON COLUMN t_candlestick_ethusdt_1min.analysis IS '分析';
COMMENT ON COLUMN t_candlestick_ethusdt_1min.indicator IS '指标';



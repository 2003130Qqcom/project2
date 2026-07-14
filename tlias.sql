-- ============================================================
-- TLIAS 教务管理系统 - 数据库初始化脚本
-- 使用前: 创建数据库
--   mysql -u root -p -e "CREATE DATABASE tlias DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;"
-- 然后执行本脚本:
--   mysql -u root -p tlias < tlias.sql
-- ============================================================

-- 部门表
CREATE TABLE IF NOT EXISTS dept (
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '部门ID',
    name        VARCHAR(50) NOT NULL COMMENT '部门名称',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
) COMMENT '部门表';

-- 员工表
CREATE TABLE IF NOT EXISTS emp (
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    username    VARCHAR(32) NOT NULL UNIQUE COMMENT '用户名（登录用）',
    password    VARCHAR(64) NOT NULL COMMENT '密码',
    name        VARCHAR(32) NOT NULL COMMENT '姓名',
    gender      TINYINT NOT NULL COMMENT '性别: 1=男, 2=女',
    phone       VARCHAR(16) COMMENT '手机号',
    job         TINYINT COMMENT '职位: 1=班主任, 2=讲师, 3=学工主管, 4=教研主管, 5=咨询师',
    salary      INT COMMENT '薪资',
    image       VARCHAR(500) COMMENT '头像URL',
    entry_date  DATE COMMENT '入职日期',
    dept_id     INT COMMENT '部门ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
) COMMENT '员工表';

-- 员工工作经历表
CREATE TABLE IF NOT EXISTS emp_expr (
    id       INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    emp_id   INT NOT NULL COMMENT '员工ID',
    begin    DATE COMMENT '开始日期',
    end      DATE COMMENT '结束日期',
    company  VARCHAR(100) COMMENT '公司名称',
    job      VARCHAR(50) COMMENT '职位名称'
) COMMENT '员工工作经历表';

-- 班级表
CREATE TABLE IF NOT EXISTS clazz (
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name        VARCHAR(50) NOT NULL COMMENT '班级名称',
    room        VARCHAR(20) COMMENT '教室',
    begin_date  DATE COMMENT '开课日期',
    end_date    DATE COMMENT '结课日期',
    master_id   INT COMMENT '班主任ID',
    subject     TINYINT COMMENT '学科',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
) COMMENT '班级表';

-- 学员表
CREATE TABLE IF NOT EXISTS student (
    id               INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name             VARCHAR(32) NOT NULL COMMENT '姓名',
    no               VARCHAR(32) NOT NULL COMMENT '学号',
    gender           TINYINT NOT NULL COMMENT '性别: 1=男, 2=女',
    phone            VARCHAR(16) COMMENT '手机号',
    id_card          VARCHAR(18) COMMENT '身份证号',
    is_college       TINYINT DEFAULT 0 COMMENT '是否院校生: 1=是, 0=否',
    address          VARCHAR(200) COMMENT '联系地址',
    degree           TINYINT COMMENT '学历: 1=初中, 2=高中, 3=大专, 4=本科, 5=硕士, 6=博士',
    graduation_date  DATE COMMENT '毕业时间',
    clazz_id         INT COMMENT '班级ID',
    violation_count  SMALLINT DEFAULT 0 COMMENT '违纪次数',
    violation_score  SMALLINT DEFAULT 0 COMMENT '违纪扣分',
    create_time      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
) COMMENT '学员表';

-- 操作日志表（AOP自动记录）
CREATE TABLE IF NOT EXISTS operate_log (
    id              INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    operate_emp_id  INT COMMENT '操作人ID',
    operate_time    DATETIME COMMENT '操作时间',
    class_name      VARCHAR(500) COMMENT '目标类全限定名',
    method_name     VARCHAR(100) COMMENT '目标方法名',
    method_params   VARCHAR(2000) COMMENT '方法参数JSON',
    return_value    VARCHAR(2000) COMMENT '返回值JSON',
    cost_time       BIGINT COMMENT '执行耗时(ms)'
) COMMENT '操作日志表';

-- 员工操作日志表（手动记录）
CREATE TABLE IF NOT EXISTS emp_log (
    id            INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    operate_time  DATETIME NOT NULL COMMENT '操作时间',
    info          VARCHAR(500) COMMENT '操作描述'
) COMMENT '员工操作日志表';

-- ========== 初始数据 ==========

-- 默认部门
INSERT INTO dept (name) VALUES
    ('教学部'),
    ('市场部'),
    ('教研部'),
    ('学工部');

-- 默认用户 (密码: 123456 - 项目使用明文密码)
INSERT INTO emp (username, password, name, gender, job, dept_id, entry_date) VALUES
    ('admin', '123456', '系统管理员', 1, 1, 1, CURDATE()),
    ('zhangsan', '123456', '张三', 1, 2, 1, '2024-01-01'),
    ('lisi', '123456', '李四', 2, 3, 4, '2024-02-01');

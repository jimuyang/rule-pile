-- auto-generated definition
create table ru_editor_store
(
    id               bigint auto_increment comment '主键'
        primary key,
    content          text                                      not null comment '存储内容',
    deleted          tinyint      default 0                    not null comment '软删标识',
    ezone_shard_info bigint                                    null comment 'shard info',
    created_at       datetime     default CURRENT_TIMESTAMP    not null comment '创建时间',
    updated_at       timestamp    default CURRENT_TIMESTAMP    not null on update CURRENT_TIMESTAMP comment '更新时间',
    drc_check_time   timestamp(3) default CURRENT_TIMESTAMP(3) not null on update CURRENT_TIMESTAMP(3) comment 'drc check time'
)
    comment '编辑规则存储';

create index ix_created_at
    on ru_editor_store (created_at);

create index ix_drc_check_time
    on ru_editor_store (drc_check_time);

create index ix_updated_at
    on ru_editor_store (updated_at);

-- auto-generated definition
create table ru_rule
(
    id               bigint auto_increment comment '主键'
        primary key,
    code             varchar(200) default ''                   not null comment '唯一code',
    name             varchar(100) default ''                   not null comment '规则名称',
    limit_scenes     varchar(200) default ''                   not null comment '仅限这些场景使用 空为无限制',
    deleted          tinyint      default 0                    not null comment '软删标识',
    ezone_shard_info bigint                                    null comment 'shard info',
    created_at       datetime     default CURRENT_TIMESTAMP    not null comment '创建时间',
    updated_at       timestamp    default CURRENT_TIMESTAMP    not null on update CURRENT_TIMESTAMP comment '更新时间',
    drc_check_time   timestamp(3) default CURRENT_TIMESTAMP(3) not null on update CURRENT_TIMESTAMP(3) comment 'drc check time'
)
    comment '规则主表';

create index ix_created_at
    on ru_rule (created_at);

create index ix_drc_check_time
    on ru_rule (drc_check_time);

create index ix_updated_at
    on ru_rule (updated_at);

-- auto-generated definition
create table ru_rule_bean
(
    id               bigint auto_increment comment '主键'
        primary key,
    type             tinyint      default 0                    not null comment '实例类型',
    definition_id    bigint       default 0                    not null comment 'rule definition id',
    remark           varchar(200) default ''                   not null comment '生成实例时备注',
    content          text                                      not null comment '规则内容',
    deleted          tinyint      default 0                    not null comment '软删标识',
    ezone_shard_info bigint                                    null comment 'shard info',
    created_at       datetime     default CURRENT_TIMESTAMP    not null comment '创建时间',
    updated_at       timestamp    default CURRENT_TIMESTAMP    not null on update CURRENT_TIMESTAMP comment '更新时间',
    drc_check_time   timestamp(3) default CURRENT_TIMESTAMP(3) not null on update CURRENT_TIMESTAMP(3) comment 'drc check time'
)
    comment '规则实例';

create index ix_created_at
    on ru_rule_bean (created_at);

create index ix_definition_id
    on ru_rule_bean (definition_id);

create index ix_drc_check_time
    on ru_rule_bean (drc_check_time);

create index ix_updated_at
    on ru_rule_bean (updated_at);

-- auto-generated definition
create table ru_rule_definition
(
    id               bigint auto_increment comment '主键'
        primary key,
    store_id         bigint        default 0                    not null comment 'editor store id',
    rule_id          bigint        default 0                    not null comment '规则id',
    rule_code        varchar(200)  default ''                   not null comment '规则code',
    input            varchar(500)  default ''                   not null comment '规则输入定义',
    options          varchar(500)  default ''                   not null comment '规则选项定义',
    logic            text                                       null comment '规则自身逻辑',
    output           varchar(500)  default ''                   not null comment '规则输出定义',
    deleted          tinyint       default 0                    not null comment '软删标识',
    ezone_shard_info bigint                                     null comment 'shard info',
    created_at       datetime      default CURRENT_TIMESTAMP    not null comment '创建时间',
    updated_at       timestamp     default CURRENT_TIMESTAMP    not null on update CURRENT_TIMESTAMP comment '更新时间',
    drc_check_time   timestamp(3)  default CURRENT_TIMESTAMP(3) not null on update CURRENT_TIMESTAMP(3) comment 'drc check time',
    content          varchar(3000) default ''                   not null comment '其他内容存储'
)
    comment '规则定义';

create index ix_created_at
    on ru_rule_definition (created_at);

create index ix_drc_check_time
    on ru_rule_definition (drc_check_time);

create index ix_rule_id
    on ru_rule_definition (rule_id);

create index ix_store_id
    on ru_rule_definition (store_id);

create index ix_updated_at
    on ru_rule_definition (updated_at);

-- auto-generated definition
create table ru_rule_dependency
(
    id                bigint auto_increment comment '主键'
        primary key,
    definition_id     bigint       default 0                    not null comment '引用方rule definition',
    dep_definition_id bigint       default 0                    not null comment '依赖方rule definition',
    dep_rule_id       bigint       default 0                    not null comment '依赖方rule id',
    deleted           tinyint      default 0                    not null comment '软删标识',
    ezone_shard_info  bigint                                    null comment 'shard info',
    created_at        datetime     default CURRENT_TIMESTAMP    not null comment '创建时间',
    updated_at        timestamp    default CURRENT_TIMESTAMP    not null on update CURRENT_TIMESTAMP comment '更新时间',
    drc_check_time    timestamp(3) default CURRENT_TIMESTAMP(3) not null on update CURRENT_TIMESTAMP(3) comment 'drc check time'
)
    comment '规则引用和依赖';

create index ix_created_at
    on ru_rule_dependency (created_at);

create index ix_definition_id
    on ru_rule_dependency (definition_id);

create index ix_dep_definition_id
    on ru_rule_dependency (dep_definition_id);

create index ix_dep_rule_id
    on ru_rule_dependency (dep_rule_id);

create index ix_drc_check_time
    on ru_rule_dependency (drc_check_time);

create index ix_updated_at
    on ru_rule_dependency (updated_at);

-- auto-generated definition
create table ru_scene
(
    id               bigint auto_increment comment '主键'
        primary key,
    name             varchar(100) default ''                   not null comment '场景名称',
    deleted          tinyint      default 0                    not null comment '软删标识',
    ezone_shard_info bigint                                    null comment 'shard info',
    created_at       datetime     default CURRENT_TIMESTAMP    not null comment '创建时间',
    updated_at       timestamp    default CURRENT_TIMESTAMP    not null on update CURRENT_TIMESTAMP comment '更新时间',
    drc_check_time   timestamp(3) default CURRENT_TIMESTAMP(3) not null on update CURRENT_TIMESTAMP(3) comment 'drc check time'
)
    comment '规则场景';

create index ix_created_at
    on ru_scene (created_at);

create index ix_drc_check_time
    on ru_scene (drc_check_time);

create index ix_updated_at
    on ru_scene (updated_at);


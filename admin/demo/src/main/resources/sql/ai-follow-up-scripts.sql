create table if not exists ai_follow_up_scripts (
    id bigint primary key auto_increment,
    lead_id bigint not null,
    service_name varchar(120) not null,
    content text not null,
    model varchar(80) not null,
    created_at datetime not null,
    updated_at datetime not null,
    unique key uk_ai_follow_up_scripts_lead_id (lead_id),
    index idx_ai_follow_up_scripts_created_at (created_at)
) engine=InnoDB default charset=utf8mb4;

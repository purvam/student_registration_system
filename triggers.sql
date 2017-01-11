

create or replace trigger "INS_LOGS_DEL_ENROLLMENT"
AFTER
delete on "ENROLLMENTS"
for each row
begin
insert into logs values (log_sequence.nextval, USER,sysdate, 'enrollments', 'delete', :old.B# || ',' || :old.classid);
end;

create or replace trigger "INS_LOGS_DEL_STUDENTS"
AFTER
delete on "STUDENTS"
for each row
begin
insert into logs values (log_sequence.nextval, USER, sysdate, 'students', 'delete', :old.B#);
end;


create or replace trigger "INS_LOGS_INS_ENROLLMENT"
AFTER
insert on "ENROLLMENTS"
for each row
begin
insert into logs values (log_sequence.nextval, USER, sysdate, 'enrollments', 'insert', :new.B# || ',' || :new.classid);
end;


create or replace trigger "INS_LOGS_INS_STUDENTS"
AFTER
insert on "STUDENTS"
for each row
begin
insert into logs values (log_sequence.nextval, USER, sysdate, 'students', 'insert', :new.B#);
end;


create or replace trigger "UPD_CLASS_INS_ENROLLMENTS_T1"
AFTER
insert on "ENROLLMENTS"
for each row
begin
update classes 
    set class_size = class_size + 1 
    where classid = :new.classid;
end;


create or replace trigger "UPD_CLASS_DEL_ENROLLMENTS"
AFTER
delete on "ENROLLMENTS"
for each row
begin
update classes 
    set class_size = class_size - 1 
    where classid = :old.classid;
end;



create or replace trigger "DEL_ENROLLMENT_DEL_STUDENTS"
BEFORE
delete on "STUDENTS"
for each row
begin
    delete from enrollments where B#= :old.B#;
end;
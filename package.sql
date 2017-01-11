create or replace package Pkg_Student_Registration is type details_cursor is ref cursor;
    
procedure show_students(student_cursor OUT SYS_REFCURSOR);

procedure show_courses(course_cursor OUT SYS_REFCURSOR);

procedure show_course_credits(credit_cursor OUT SYS_REFCURSOR);

procedure show_classes(class_cursor OUT SYS_REFCURSOR);

procedure show_enrollments(enrollment_cursor OUT SYS_REFCURSOR);

procedure show_grades(grades_cursor OUT SYS_REFCURSOR);

procedure show_prerequisites(prerequisites_cursor OUT SYS_REFCURSOR);

procedure show_logs(logs_cursor OUT SYS_REFCURSOR);

procedure show_students_details(B#In IN students.B#%type,ret_message OUT varchar2,classes_details_cursor OUT SYS_REFCURSOR);

procedure show_class_details(classidIn IN classes.classid%type,ret_message OUT varchar2,classes_details_cursor OUT SYS_REFCURSOR,student_details_cursor OUT SYS_REFCURSOR);

procedure show_prerequisites_details(dept_codeIn in prerequisites.dept_code%type,course#In in prerequisites.course#%type,results out varchar);

procedure student_enrollment(B#In in students.B#%type, classidIn in classes.classid%type, results out varchar);

procedure student_drop_enrollment1(B#In in students.B#%type, classidIn in classes.classid%type, results out varchar);

procedure delete_student(B#In in students.B#%type,results out varchar);

end Pkg_Student_Registration;


create or replace package body Pkg_Student_Registration as

procedure show_students(student_cursor OUT SYS_REFCURSOR)
is
begin
OPEN student_cursor FOR
select * from students;
end show_students;

procedure show_courses(course_cursor OUT SYS_REFCURSOR)
is
begin
OPEN course_cursor FOR
select * from courses;
end show_courses;

procedure show_course_credits(credit_cursor OUT SYS_REFCURSOR)
is
begin
OPEN credit_cursor FOR
select * from course_credit;
end show_course_credits;

procedure show_classes(class_cursor OUT SYS_REFCURSOR)
is
begin
OPEN class_cursor FOR
select * from classes;
end show_classes;

    
procedure show_enrollments(enrollment_cursor OUT SYS_REFCURSOR)
is
begin
OPEN enrollment_cursor FOR
select * from enrollments;
end show_enrollments;

procedure show_grades(grades_cursor OUT SYS_REFCURSOR)
is
begin
OPEN grades_cursor FOR
select * from grades;
end show_grades;

procedure show_prerequisites(prerequisites_cursor OUT SYS_REFCURSOR)
is
begin
OPEN prerequisites_cursor FOR
select * from prerequisites;
end show_prerequisites;

procedure show_logs(logs_cursor OUT SYS_REFCURSOR)
is
begin
OPEN logs_cursor FOR
select * from logs;
end show_logs; 

procedure show_students_details(B#In IN students.B#%type,ret_message OUT varchar2,classes_details_cursor OUT SYS_REFCURSOR)
is
B#_check number(2);
class_students_check number(2);
begin 
select count(B#) into B#_check from students where B# = B#In;
select count(B#) into class_students_check from enrollments where B# = B#In;

if B#_check = 0 then    
ret_message := 'The B# is invalid';
elsif class_students_check = 0 then    
ret_message := 'The student has not taken any course.';
else
open classes_details_cursor for 
select c.classid,c.dept_code,c.course#,c.sect#,c.year,c.semester,g.lgrade,g.ngrade
from classes c,courses co, students s, enrollments e, grades g 
where s.B# = B#In
and s.B# = e.B#
and e.classid = c.classid
and e.lgrade = g.lgrade
and c.dept_code || c.course# = co.dept_code || co.course# 
order by s.B#;
ret_message := 'success';
end if;
end show_students_details;

procedure show_class_details(classidIn IN classes.classid%type,ret_message OUT varchar2,classes_details_cursor OUT SYS_REFCURSOR,student_details_cursor OUT SYS_REFCURSOR)
is
classid_check number(2);
class_students_check number(2);
begin
select count(classid) into classid_check from classes where classid = classidIn;
select count(B#) into class_students_check from enrollments where classid = classidIn;

if classid_check = 0 then    
ret_message := 'The classid is invalid';
elsif class_students_check = 0 then    -- check if student enrolled in class
ret_message := 'No student is enrolled in the class';
else
open classes_details_cursor for 
select c.classid,co.title from classes c,courses co 
where c.dept_code = co.dept_code
and c.course# = co.course# 
and c.classid = classidIn;

open student_details_cursor for
select s.B#,s.firstname 
from students s,enrollments e 
where e.classid = classidIn 
and s.B# = e.B#;
ret_message := 'success';
end if;
end show_class_details;

procedure show_prerequisites_details(dept_codeIn in prerequisites.dept_code%type,course#In in prerequisites.course#%type,results out varchar)
is

dep_code varchar2(4);
cr_no number(3);
temp_res varchar(9000);

cursor prereq_cursor is
(select pre_dept_code, pre_course# from prerequisites where dept_code = dept_codeIn and course# = course#In);
 prereq_rec prereq_cursor%rowtype;

cursor course_cursor is
(select * from courses where dept_code = dept_codeIn and course# = course#In);

course_rec course_cursor%rowtype;

BEGIN


open course_cursor;
fetch course_cursor into course_rec;

if (course_cursor%notfound) then     
    results := 'Entered course not found.';
else

    open prereq_cursor;    
    fetch prereq_cursor into prereq_rec;
    
    while prereq_cursor%found loop
            dep_code := prereq_rec.pre_dept_code;
            cr_no := prereq_rec.pre_course#;
            if (prereq_rec.pre_dept_code IS NOT NULL) then
                IF (results IS not NULL) THEN
                    results := results || chr(10) || dep_code || cr_no;
                end if;
                IF (results IS NULL) THEN
                    results := dep_code || cr_no;
                end if;

                show_prerequisites_details(dep_code, cr_no, temp_res);        
            
                IF (temp_res IS not NULL) THEN
                    results := results || chr(10) || temp_res;
                end if;
            else
                return;
            end if;
        
            fetch prereq_cursor into prereq_rec;
     end loop;
             
end if;                        
end show_prerequisites_details;

procedure student_enrollment(B#In in students.B#%type, classidIn in classes.classid%type, results out varchar)

is

cursor stu_cursor is
select * from students where B# = B#In;
 stu_rec stu_cursor%rowtype;

 
cursor classes_cursor is
select * from classes where classid = classidIn;
 classes_rec classes_cursor%rowtype;


cursor getEnrolledStu_cursor is
select B#, firstname from students where B# in (select B# from enrollments where classid = classidIn);
 getEnrolledStu_rec getEnrolledStu_cursor%rowtype;

cursor Course_cursor is
select (dept_code || course#) as course from classes where classid = classidIn;
 Course_rec Course_cursor%rowtype;
 
cursor coursesTaken_cursor is
select (dept_code || course#) as course from classes 
where classid in (select classid from enrollments
where B# = B#In and 
classid in (select classid from classes where (semester, year) in (select semester, year from classes where classid = classidIn)));
 coursesTaken_rec coursesTaken_cursor%rowtype;

  
cursor enrollCount_cursor is
select count(*) as count from enrollments
where B# = B#In and 
classid in (select classid from classes where (semester, year) in (select semester, year from classes where classid = classidIn));
 enrollCount_rec enrollCount_cursor%rowtype;
  
cursor prereq_cursor is
(select (pre_dept_code || pre_course#) as course from prerequisites 
where (dept_code, course#) in (select dept_code, course# from classes where classid = classidIn));
 prereq_rec prereq_cursor%rowtype;
 
cursor completecourses_cursor is
select (dept_code || course#) as course from classes 
where classid in (select classid from enrollments where B# = B#In and lgrade in ('A', 'A-', 'B+', 'B', 'B-', 'C+', 'C'));
 completecourses_rec completecourses_cursor%rowtype;

prereq_flag boolean;

begin

prereq_flag := false;

open stu_cursor;
fetch stu_cursor into stu_rec;

if (stu_cursor%notfound) then
    results := 'This B# is invalid';
else 

    open classes_cursor;
    fetch classes_cursor into classes_rec;
    
    if (classes_cursor%notfound) then
        results := 'This classid is invalid';
    else 
        
        if(classes_rec.class_size >= classes_rec.limit) then
            results := 'Class is full';  
        else
        
            open Course_cursor;
            fetch Course_cursor into Course_rec;
        
            open coursesTaken_cursor;
            fetch coursesTaken_cursor into coursesTaken_rec;
            

            
            while coursesTaken_cursor%found loop
                
                if (Course_rec.course = coursesTaken_rec.course) then 
                    results := 'The student is already in the class.';
                    return;
                end if;
                fetch coursesTaken_cursor into coursesTaken_rec;
            end loop;
            
            open enrollCount_cursor;
            fetch enrollCount_cursor into enrollCount_rec;
                
            if (enrollCount_cursor%found) then
                if (enrollCount_rec.count = 4) then
                    results := 'Students cannot be enrolled in more than four classes in the same semester';
                    return;
                end if;
                    
                open prereq_cursor;
                fetch prereq_cursor into prereq_rec;
                open completecourses_cursor;
                while prereq_cursor%found loop
                    fetch completecourses_cursor into completecourses_rec;
                    
                    while completecourses_cursor%found loop
                                    
                        if (completecourses_rec.course = prereq_rec.course) then
                            prereq_flag := true;
                        end if;
                            
                        fetch completecourses_cursor into completecourses_rec;
                    end loop;
                        
                        if(not prereq_flag) then
                            results := 'Prerequisite courses have not been completed';
                            return;
                        end if;
                        prereq_flag := false;
                        fetch prereq_cursor into prereq_rec;
                end loop;


                    if(enrollCount_rec.count < 3) then
                        INSERT INTO enrollments VALUES (B#In, classidIn, null);
                        results := 'Student enrollment successful. ';
                    end if;    
                    
                    
                    if(enrollCount_rec.count = 3) then
                        results := results || 'You are overloaded.';
                        INSERT INTO enrollments VALUES (B#In, classidIn, null);
                    end if;
                    
                    
                end if;
            end if; 
        end if;
    end if;   
  
end;

procedure student_drop_enrollment1(B#In in students.B#%type, classidIn in classes.classid%type, results out varchar)
is
v_B# students.B#%type;
v_class_id classes.classid%type;
t_sid students.B#%type;
t_class_id classes.classid%type;
v_check number(1);
v_pre_course_check number(1);
v_stu_class_cnt_check number(1);
v_class_enroll_check number(2);
 
begin
v_B#:=B#In;
v_class_id:=classidIn;
t_class_id:='00000';
v_check:=1;
v_pre_course_check:=0;

select B# into t_sid from students where B#=v_B#;

t_class_id:=null;

select classid into t_class_id from classes where classid=v_class_id;

select count(e.B#) into v_check from classes cl join enrollments e 
on cl.classid=e.classid where e.B#=v_B# and e.classid=v_class_id;

select count(pre_course#) into v_pre_course_check
from prerequisites 
start with course#=(select course# from classes where classid=v_class_id)
connect by prior course# = pre_course#;

if(v_check=0)
then
results:='The Student is not enrolled in the class.';

elsif(v_pre_course_check > 0)
then
results:='The drop is not permitted because another class uses it as a prerequisite.';

else
delete from enrollments where B#=v_B# and classid=v_class_id;
results:='The Student is unenrolled. ';

select count(classid) into v_stu_class_cnt_check from enrollments where B#=v_B#;

if(v_stu_class_cnt_check=0)
then
results:= results || 'This student is not enrolled in any classes. ';
end if;

select count(B#) into v_class_enroll_check  from enrollments where classid=v_class_id;

if(v_class_enroll_check=0)
then
results:=results || 'The class now has no students.';
end if;
end if;

exception when no_data_found
then
if t_sid is null 
then 
results:='The Student Id is invalid.';
end if;

if t_class_id is null
then
results:='The Class Id is invalid.';
end if;
end student_drop_enrollment1;

procedure delete_student(B#In in students.B#%type,results out varchar)
is
t_B# students.B#%type;
begin
select B# into t_B# from students where B#=B#In;

delete from students where B#=B#In;
results:= 'The Student Record is Deleted.';
commit;

exception when no_data_found
then
if t_B# is null 
then
results:='The Student Id is invalid';
end if;
end delete_student;

end Pkg_Student_Registration;
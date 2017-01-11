package myProject;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

import oracle.jdbc.OracleTypes;
import oracle.jdbc.pool.OracleDataSource;

public class StudentRegistrationSystem {

	private static Scanner line ;
	private static Connection connection;
	private static CallableStatement CS;
	private static ResultSet RS;
	
	private void deleteStudent() {
		try
		{
			System.out.print("Enter the student id: ");
			String B_No = line.nextLine();
			CS = connection.prepareCall("{call Pkg_Student_Registration.delete_student(:1,:2)}");
			
			CS.setString(1, B_No);
			 
			CS.registerOutParameter(2, Types.VARCHAR);
			CS.executeQuery();
			System.out.println(CS.getString(2));
			CS.close();
			
		}
		catch (SQLException e) { System.out.println ("SQLException" + e.getMessage());}
		catch (Exception e) {System.out.println(e);}

		
	}

	private void dropStudentFromClass() {
		try
		{
			System.out.print("Enter the student id: ");
			String B_No = line.nextLine();
			System.out.print("Enter the class id: ");
			String classid = line.nextLine();
			
			CS = connection.prepareCall("{call Pkg_Student_Registration.student_drop_enrollment1(:1,:2,:3)}");
			
			CS.setString(1, B_No);
			CS.setString(2, classid);
			 
			CS.registerOutParameter(3, Types.VARCHAR);
			CS.executeQuery();
			System.out.println(CS.getString(3));
			CS.close();
			
		}
		catch (SQLException e) { System.out.println ("SQLException" + e.getMessage());}
		catch (Exception e) {System.out.println(e);}

		
	}

	private void enrollStdudent() {
		try
		{
			System.out.print("Enter the student id: ");
			String B_No = line.nextLine();
			System.out.print("Enter the class id: ");
			String classid = line.nextLine();
			
			//calling the procedure to enroll student in class
			CS = connection.prepareCall("{call Pkg_Student_Registration.student_enrollment(:1,:2,:3)}");
			
			CS.setString(1, B_No);
			CS.setString(2, classid);
			 
			CS.registerOutParameter(3, Types.VARCHAR);
			CS.executeQuery();
			System.out.println(CS.getString(3));
			CS.close();
			
		}
		catch (SQLException e) { System.out.println ("SQLException" + e.getMessage());}
		catch (Exception e) {System.out.println(e);}

		
	}

	private void showPrerequisitesOfCourses() {
		try{
			System.out.print("Enter the dept code: ");
			String dept_code = line.nextLine();
			System.out.print("Enter the course no: ");
			String course_no = line.nextLine();

			CS = connection.prepareCall("{call Pkg_Student_Registration.show_prerequisites_details(:1,:2, :3)}");

			CS.setString(1, dept_code);
			CS.setString(2, course_no);
			CS.registerOutParameter(3, Types.VARCHAR);

			CS.executeQuery();
			
			if(CS.getString(3) == null){
				System.out.println("No prerequisites found");
			}
			else{
				System.out.println(CS.getString(3));
			}
			CS.close();
		}catch (SQLException e) {
			System.out.println ("SQLException" + e.getMessage());
		}
		catch (Exception e) {
			System.out.println (e);
		}

		
	}

	private void showClassesStudents() {
		try{
			System.out.print("Enter the Class id: ");
			String classid = line.nextLine();

			
			CS = connection.prepareCall("{call Pkg_Student_Registration.show_class_details(:1,:2,:3,:4)}");

			CS.setString(1, classid);
			CS.registerOutParameter(2, OracleTypes.VARCHAR);
			CS.registerOutParameter(3, OracleTypes.CURSOR);
			CS.registerOutParameter(4, OracleTypes.CURSOR);
			
			CS.execute();
			
			
			String res = CS.getString(2);
			
			if(res.equals("success")){
				
				RS = (ResultSet)CS.getObject(3);
				
				System.out.println("The Class details are:\n");
				System.out.println("CLASSID\tTITLE");
				System.out.println("-------\t-----");
				
				while(RS.next()){
		            System.out.println(RS.getString(1) + "\t" + RS.getString(2));
				}
				
				RS = (ResultSet)CS.getObject(4);
				
				System.out.println("\nStudents enrolled in class are:\n");
				System.out.println("SID\tFIRSTNAME");
				System.out.println("---\t---------");
				while(RS.next()){
		            System.out.println(RS.getString(1) + "\t" + RS.getString(2));
				}
				
			}else{
				System.out.println(res);
			}
			
			CS.close();
		
		}catch (SQLException e) {
			System.out.println ("SQLException" + e.getMessage());
		}
		catch (Exception e) {
			System.out.println (e);
		}		
		
	}

	private void showStudentClasses() {
		try{
			System.out.print("Enter the Student id: ");
			String B_no = line.nextLine();

			
			CS = connection.prepareCall("{call Pkg_Student_Registration.show_students_details(:1,:2,:3)}");

			CS.setString(1, B_no);
			CS.registerOutParameter(2, OracleTypes.VARCHAR);
			CS.registerOutParameter(3, OracleTypes.CURSOR);
			
			CS.execute();
			
			
			String res = CS.getString(2);
			
			if(res.equals("success")){
				
				RS = (ResultSet)CS.getObject(3);
				
				System.out.println("The Student details are:\n");
				System.out.println("CLASSID\tDEPT_CODE\tCOURSE#\tSECT#\tYEAR\tSEMESTER\tLGRADE\tNGRADE");
				System.out.println("-------\t---------\t-------\t-----\t----\t--------\t------\t------");
				
				//printing the result obtained
				while(RS.next()){
		            System.out.println(RS.getString(1) + "\t" + RS.getString(2) + "\t\t" + RS.getString(3) + "\t" 
		            					+ RS.getString(4)+ "\t" + RS.getString(5)+ "\t" + RS.getString(6)+ "\t\t" 
		            					+ RS.getString(7)+ "\t" + RS.getString(8));
				}
				
			/*	rs = (ResultSet)cs.getObject(4);
				
				System.out.println("\nAnd the students enrolled in it are:\n");
				System.out.println("SID\tLASTNAME");
				
				while(rs.next()){
		            System.out.println(rs.getString(1) + "\t" + rs.getString(2));
				}*/
				
			}else{
				System.out.println(res);
			}
			
			CS.close();
		
		}catch (SQLException e) {
			System.out.println ("SQLException" + e.getMessage());
		}
		catch (Exception e) {
			System.out.println (e);
		}
				
	}

	private void showLogs() {
		try{
			CS = connection.prepareCall("{call Pkg_Student_Registration.show_logs(:1)}");
			CS.registerOutParameter(1, OracleTypes.CURSOR);
			CS.execute();
			RS = (ResultSet)CS.getObject(1);
			
			System.out.println("LOGID\tWHO\t\tTIME\t\t\tTABLE_NAME\tOPERATION\tKEY_VALUE");
			System.out.println("-----\t---\t\t----\t\t\t----------\t---------\t---------");
			while(RS.next()){
	            System.out.println(RS.getString(1) + "\t" + RS.getString(2) + "\t" + RS.getString(3)+ 
	            		"\t" + RS.getString(4)+ "\t" + RS.getString(5)+ "\t\t" + RS.getString(6));
			}
			CS.close();
		}catch (SQLException e) {
			System.out.println ("SQLException" + e.getMessage());
		}
		catch (Exception e) {
			System.out.println (e);
		}
		
		
	}

	private void showPrerequisites() {
		try{
			CS = connection.prepareCall("{call Pkg_Student_Registration.show_prerequisites(:1)}");
			CS.registerOutParameter(1, OracleTypes.CURSOR);
			CS.execute();
			RS = (ResultSet)CS.getObject(1);
			
			System.out.println("DEPT_CODE\tCOURSE_NO\tPRE_DEPT_CODE\tPRE_COURSE_NO");
			System.out.println("---------\t---------\t-------------\t-------------");
			while(RS.next()){
	            System.out.println(RS.getString(1) + "\t\t" + RS.getString(2) + "\t\t" + RS.getString(3)+ "\t\t" + RS.getString(4));
			}
			CS.close();
		}catch (SQLException e) {
			System.out.println ("SQLException" + e.getMessage());
		}
		catch (Exception e) {
			System.out.println (e);
		}

		
	}

	private void showGrades() {
		try{
			CS = connection.prepareCall("{call Pkg_Student_Registration.show_grades(:1)}");
			CS.registerOutParameter(1, OracleTypes.CURSOR);
			CS.execute();
			RS = (ResultSet)CS.getObject(1);
			
			System.out.println("LGRADE\tNGRADE");
			System.out.println("------\t------");
			while(RS.next()){
	            System.out.println(RS.getString(1) + "\t" + RS.getString(2));
			}
			CS.close();
		}catch (SQLException e) {
			System.out.println ("SQLException" + e.getMessage());
		}
		catch (Exception e) {
			System.out.println (e);
		}

	}

	private void showEnrollments() {
		try{
			CS = connection.prepareCall("{call Pkg_Student_Registration.show_enrollments(:1)}");
			CS.registerOutParameter(1, OracleTypes.CURSOR);
			CS.execute();
			RS = (ResultSet)CS.getObject(1);
			
			System.out.println("STUDENT_B#\tCLASSID\tLGRADE");
			System.out.println("----------\t-------\t------");
			while(RS.next()){
	            System.out.println(RS.getString(1) + "\t\t" + RS.getString(2) + "\t" + RS.getString(3));
			}
			CS.close();
		}catch (SQLException e) {
			System.out.println ("SQLException" + e.getMessage());
		}
		catch (Exception e) {
			System.out.println (e);
		}
		
	}

	private void showClasses() {
		try{
			CS = connection.prepareCall("{call Pkg_Student_Registration.show_classes(:1)}");
			CS.registerOutParameter(1, OracleTypes.CURSOR);
			CS.execute();
			RS = (ResultSet)CS.getObject(1);
			
			System.out.println("CLASSID\tDEPT_CODE\tCOURSE_NO\tSECT_NO\tYEAR\tSEMESTER\tLIMIT\tCLASS_SIZE");
			System.out.println("-------\t---------\t---------\t-------\t----\t--------\t-----\t----------");
			while(RS.next()){
	            System.out.println(RS.getString(1) + "\t" + RS.getString(2)+ "\t\t" + RS.getString(3)
	            + "\t\t" + RS.getString(4)+ "\t" + RS.getString(5)+ "\t" + RS.getString(6)
	            + "\t\t" + RS.getString(7)+ "\t" + RS.getString(8));
			}
			CS.close();
		}catch (SQLException e) {
			System.out.println ("SQLException" + e.getMessage());
		}
		catch (Exception e) {
			System.out.println (e);
		}
		
	}

	private void showCourseCredits() {
		try{
			CS = connection.prepareCall("{call Pkg_Student_Registration.show_course_credits(:1)}");
			CS.registerOutParameter(1, OracleTypes.CURSOR);
			CS.execute();
			RS = (ResultSet)CS.getObject(1);
			
			System.out.println("COURSE_NO\tCREDIT");
			System.out.println("---------\t------");
			while(RS.next()){
	            System.out.println(RS.getString(1) + "\t\t" + RS.getString(2));
			}
			CS.close();
		}catch (SQLException e) {
			System.out.println ("SQLException" + e.getMessage());
		}
		catch (Exception e) {
			System.out.println (e);
		}

	}

	private void showCourses() {
		try{
			CS = connection.prepareCall("{call Pkg_Student_Registration.show_courses(:1)}");
			CS.registerOutParameter(1, OracleTypes.CURSOR);
			CS.execute();
			RS = (ResultSet)CS.getObject(1);
			
			System.out.println("DEPT_CODE\tCOURSE_NO\tTITLE");
			System.out.println("---------\t---------\t--------");
			while(RS.next()){
	            System.out.println(RS.getString(1) + "\t\t" + RS.getString(2) + "\t\t" + RS.getString(3));
			}
			CS.close();
		}catch (SQLException e) {
			System.out.println ("SQLException" + e.getMessage());
		}
		catch (Exception e) {
			System.out.println (e);
		}

		
	}

	private void showStudents() {
		try{
			CS = connection.prepareCall("{call Pkg_Student_Registration.show_students(:1)}");
			CS.registerOutParameter(1, OracleTypes.CURSOR);
			CS.execute();
			RS = (ResultSet)CS.getObject(1);
	
			System.out.println("SID\tFIRSTNAME\tLASTNAME\tSTATUS\tGPA\tEMAIL");
			System.out.println("---\t---------\t--------\t------\t---\t-----");
			while(RS.next()){
	            System.out.println(RS.getString(1) + "\t" + RS.getString(2) + "\t\t" + RS.getString(3)+ "\t\t"  
	            					+ RS.getString(4) + "\t" + RS.getString(5) + "\t" + RS.getString(6));
			}
			CS.close();
		}catch (SQLException e) {
			System.out.println ("SQLException" + e.getMessage());
		}
		catch (Exception e) {
			System.out.println (e);
		}
		
	}

	
	public static void main(String[] args) {
		
		line = new Scanner(System.in);
	
		try{
			OracleDataSource ds = new oracle.jdbc.pool.OracleDataSource();
	        ds.setURL("jdbc:oracle:thin:@localhost:1521:XE");
	        connection = ds.getConnection("DBPROJECT2", "oracle@123");
	        
	        if(connection.isValid(10)){
		    	  System.out.println("Connection successfull");
		    }
		    else{
		    	  System.out.println("Connection Unsuccessfull --- Try Again\nExiting");
		    	  System.exit(1);
		    }
	        
	        StudentRegistrationSystem SRS = new StudentRegistrationSystem();
			 
			while(true){			
				
				System.out.println("\t1. Show Students");
				System.out.println("\t2. Show Courses");
				System.out.println("\t3. Show Course Credits");
				System.out.println("\t4. Show Classes");
				System.out.println("\t5. Show Enrollments");
				System.out.println("\t6. Show Grades");
				System.out.println("\t7. Show Prerequisites");
				System.out.println("\t8. Show Logs");
				System.out.println("\t9. Show a student enrolled classes");
				System.out.println("\t10. Show all prerequisites of a course");
				System.out.println("\t11. Show a class detail including students enrolled in that class");
				System.out.println("\t12. Enroll a student");
				System.out.println("\t13. Drop student from class");
				System.out.println("\t14. Delete a student");
				System.out.println("\t15. Exit");
			
				System.out.print("\nEnter choice: ");
				String choice = line.nextLine();
				
				
				switch(choice){
				
				case "1":	SRS.showStudents();
							break;
							
				case "2":	SRS.showCourses();
							break;
				
				case "3":	SRS.showCourseCredits();
							break;
				
				case "4":	SRS.showClasses();
							break;
				
				case "5":	SRS.showEnrollments();
							break;
				
				case "6":	SRS.showGrades();
							break;
				
				case "7":	SRS.showPrerequisites();
							break;
							
				case "8":	SRS.showLogs();
							break;
				
				case "9":	SRS.showStudentClasses();
							break;
				
				case "10":	SRS.showPrerequisitesOfCourses();
							break;
							
				case "11":	SRS.showClassesStudents();
							break;
				
				
				case "12":	SRS.enrollStdudent();
							break;
				
				case "13":	SRS.dropStudentFromClass();
							break;
							
				case "14":	SRS.deleteStudent();
							break;
				
				case "15":	System.exit(0);
				
				default:	System.out.println("Wrong choice. Please try again");
				
				}
				
				System.out.print("\nEnter n to exit or y to continue: ");
				String ch = line.nextLine();
				
				if(ch.equals("n")){
					System.out.println("You selected to end the program.");
					System.exit(0);
				}
				
	
			}
		}
		catch (SQLException e) {
			System.out.println ("SQLException" + e.getMessage());
		}
		catch (Exception e) {
			System.out.println (e);
		}
		
		
	}

	
}

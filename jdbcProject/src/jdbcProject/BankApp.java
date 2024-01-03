package jdbcProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Savepoint;
import java.util.Scanner;


public class BankApp 
{
  public static void main(String args[])
  {
	  String url="jdbc:mysql://localhost:3306/jdbc";
	  String un="root";
	  String pwd="root";
	  Connection con=null;
	  try
	  {
		  Class.forName("com.mysql.jdbc.Driver"); 
		  System.out.println("Driver Loaded Successfully");
		  con= DriverManager.getConnection(url,un,pwd);
		  System.out.println("Connection Established");
		  Scanner sc=new Scanner(System.in);
		  
		  //Login Module
		  System.out.println("Welcome to Bank------>");
		  System.out.println("Enter Account Number: ");
		  int acc_num=sc.nextInt();
		  System.out.println("Enter Pin Number: ");
		  int pin=sc.nextInt();
		  PreparedStatement pstmt1 = con.prepareStatement("select * from bank where acc_num = ? and pin = ?");
		  pstmt1.setInt(1,acc_num);
		  pstmt1.setInt(2,pin);
		  ResultSet res1=pstmt1.executeQuery();
		  res1.next();
		  String name =res1.getString(2);
		  int balance=res1.getInt(4);
		  System.out.println("Welcome "+name);
		  System.out.println("Avalible Balance "+balance);
		  
		  //Transfer Module
		  System.out.println("Transfer Details----->");
		  System.out.println("Enter the Beneficiary Account Number: ");
		  int bacc_num=sc.nextInt();
		  System.out.println("Enter the Transfer Amount");
		  int transfer_amount=sc.nextInt();
		  
		  
		  con.setAutoCommit(false); 
		  Savepoint s=con.setSavepoint();
		  PreparedStatement pstmt2 = con.prepareStatement("update bank set balance = balance - ? where acc_num=?");
		  pstmt2.setInt(1,transfer_amount);
		  pstmt2.setInt(2, acc_num);
		  pstmt2.executeUpdate();
		  System.out.println("Incoming Credit Request------>");
		  System.out.println(name+ "Account Number "+ acc_num + " Wants to transfer "+ transfer_amount);
		  System.out.println("Press Y to receive");
		  System.out.println("Press N to Reject");
		  String choice=sc.next();
		  if(choice.equals("Y"))
		  {
			  PreparedStatement pstmt3 = con.prepareStatement("update bank set balance = balance + ? where acc_num=?");
			  pstmt3.setInt(1, transfer_amount);
			  pstmt3.setInt(2, bacc_num);
			  pstmt3.executeUpdate();
			  PreparedStatement pstmt4 = con.prepareStatement("select * from bank where acc_num=?");
			  pstmt4.setInt(1,bacc_num);
			  ResultSet res2=pstmt4.executeQuery();
			  res2.next();
			  
			  System.out.println("Updated Balance "+res2.getInt(4));  
			  
		  }
		  else
		  {
			  con.rollback();
			  PreparedStatement pstmt5 = con.prepareStatement("select * from bank where acc_num=?");
			  pstmt5.setInt(1,bacc_num);
			  ResultSet res2=pstmt5.executeQuery();
			  res2.next();
			  System.out.println("Existing Balance "+res2.getInt(4)); 
		  }
		  
		  con.commit(); 
	  }
	  catch (Exception e)
	  {
		  e.printStackTrace();
	  }
  }
}

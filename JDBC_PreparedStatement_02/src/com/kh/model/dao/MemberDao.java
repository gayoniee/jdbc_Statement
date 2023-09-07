package com.kh.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.kh.model.vo.Member;

// DAO(Data Access Object) : DB에 직접적으로 접근해서 사용자의 요청에 맞는 sql문 실행 후 결관 반환(JDBC)
//							 결과를 Controller로 다시 리턴
public class MemberDao {
	
	/*
	 * *JDBC용 객체 
	 * - Connection : DB의 연결정보를 담고있는 객체 
	 * - [Prepared]Statement : 연결된 DB에 SQL문을 전달해서 실행하고, 결과를 받아내는 객체 
	 * - ResultSet : SELECT문 실행 후 조회된 결과물들이 담겨있는 객체
	 * 
	 * *JDBC 과정(순서 중요!)
	 * 1) jdbc driver 등록 : 해당 DBMS(오라클)가 제공하는 클래스 등록 
	 * 2) Connection생성 : 연결하고자 하는 DB정보를 입력해서 해당 DB와 연결하면서 생성 
	 * 3) Statement 생성: Connection 객체를 이용해서 생성(sql문 실행 및 결과 받는 객체) 
	 * 4) sql문 전달하면서 실행 : Statement 객체를 이용해서 sql문 실행 
	 * 5) 결과 받기 > SELECT문 실행 => ResultSet 객체 (조회된 데이터들이 담겨있음 => 6_1) 
	 * 			  > DML문 실행 => int(처리된 행의 수)
	 * 
	 * 6_1) ResultSet에 담겨있는 데이터들을 하나씩 하나씩 뽑아서 vo객체에 차근차근 옮겨 담기[+ ArrayList에 담아주기]
	 * 6_2) 트랜잭션 처리 (성공했다면 commit, 실패했다면 rollback 실행)
	 * 
	 * 7) 다 사용한 JDBC용 객체를 반드시 자원 반납(close) => 생성된 역순으로
	 * 
	 */
	/**
	 * 사용자가 입력한 정보들을 DB에 추가시켜주는 메소드
	 * @param m : 사용자가 입력한 값들이 담겨있는 member 객체
	 * @return : insert문 실행 후 처리된 행의 수
	 */
	public int insertMember(Member m) {
		// insert문 => 처리된 행의 수(int) => 트랜잭션 처리
		
		// 필요한 변수들 세팅
		int result = 0;  // 처리된 결과(처리된 행의 수)를 받아줄 변수
		Connection conn = null;  // 연결된 DB의 연결정보를 담는 객체
		PreparedStatement pstmt = null;  // 완성된 sql문 전달해서 곧바로 실행 후 결과를 받는 객체
		
		// 실행할 sql문(완성된 형태)
		//	INSERT INTO MEMBER
		//	VALUES(SEQ_USERNO.NEXTVAL, 'user01', 'pass01', '홍길동', 
		//			null, 23, 'user01@iei.or.kr', '01022222222', '부산', 
		//				등산, 영화보기', '2021-08-02');
		
		String sql = "INSERT INTO MEMBER VALUES(SEQ_USERNO.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";
				
		try {
			// 1)jdbc driver 등록
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			// 2) Connection 객체 생성 => DB연결
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "JDBC", "JDBC");
			
			// 3) Statement 객체 생성
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, m.getUserId());
			pstmt.setString(2, m.getUserPwd());
			pstmt.setString(3, m.getUserName());
			pstmt.setString(4, m.getGender());
			pstmt.setInt(5, m.getAge());
			pstmt.setString(6, m.getEmail());
			pstmt.setString(7, m.getPhone());
			pstmt.setString(8, m.getAddress());
			pstmt.setString(9, m.getHobby());
			
			// 4, 5) sql문 전달하면서 실행 후 결과 받기(처리된 행의 수)
			result = pstmt.executeUpdate();
			
			// 6) 트랜잭션 처리
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7) 다 쓴 jdbc 객체들 반환
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return result;
		
	}
	
	public ArrayList<Member> selectList() {
		// select문(여러행 조회) => ResultSet 객체 => ArrayList<Member>
		
		// 필요한 변수들 세팅
		ArrayList<Member> list = new ArrayList<>();  // 비어있는 상태
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;  // select문 실행시 조회된 결과값들이 최초로 담기는 객체
		
		// 실행할 sql문
		String sql = "SELECT * FROM MEMBER";
		
		// 1) JDBC 드라이버 등록
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			// 2) Connection 생성
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "JDBC", "JDBC");
			
			// 3) Statement 생성
			pstmt = conn.prepareStatement(sql);
			
			// 4, 5) sql문 실행 및 결과받기
			rset = pstmt.executeQuery();
			
			// 6) ResultSet으로부터 데이터를 하나씩 꺼내서 
			while(rset.next()) {
				Member m = new Member();
				m.setUserNo(rset.getInt("userNo"));
				m.setUserId(rset.getString("userId"));
				m.setUserPwd(rset.getString("userPwd"));
				m.setUserName(rset.getString("userName"));
				m.setGender(rset.getString("gender"));
				m.setAge(rset.getInt("age"));
				m.setEmail(rset.getString("email"));
				m.setPhone(rset.getString("phone"));
				m.setAddress(rset.getString("address"));
				m.setHobby(rset.getString("hobby"));
				m.setEnrollDate(rset.getDate("enrollDate"));
				// 현재 참조하고있는 행에 대한 모든 컬럼의 데이터들을 한 Member 객체에 담기 끝!
				
				list.add(m);  // 리스트에 담기
				
			}
			// 반복문이 끝난 시점
			// 조회된 데이터가 없다면 리스트는 비어있을 것
			// 조회된 데이터가 있다면 list에는 한개 이상 담겨있을 것
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rset.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
	public Member selectByUserId(String userId) {
		// select문(한개) => ResultSet객체 => Member 객체
		
		Member m = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		
		String sql = "SELECT * FROM MEMBER WHERE USERID = ?";
		
		try {// 1) JDBC 드라이버 등록
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			// 2) Connection 객체 생성 => DB연결
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "JDBC", "JDBC");
			
			// 3) Statement 생성
			pstmt = conn.prepareStatement(sql);  // 미완성 쿼리 전달
			pstmt.setString(1, userId);
			
			// 4, 5) sql문 전달하면서 실행 후 결과 받기
			rset = pstmt.executeQuery();
			
			if(rset.next()) {
				m = new Member(rset.getInt("userNo"),
						rset.getString("userId"),
						rset.getString("userPwd"),
						rset.getString("userName"),
						rset.getString("gender"),
						rset.getInt("age"),
						rset.getString("email"),
						rset.getString("phone"),
						rset.getString("address"),
						rset.getString("hobby"),
						rset.getDate("enrollDate"));
			}
			// 위의 조건문 다 끝난 시점
			// 조회된 데이터 있다면 => m => null
			// 조회된 데이터 없다면 => m은 생성된 객체
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rset.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return m;
	}
	
	public ArrayList<Member> selectByUserName(String keyword) {
		// select문(여러행) => ResultSet 객체 => ArrayList<Member> 객체에 담기
		
		ArrayList<Member> list = new ArrayList<>(); // 텅빈 상태
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		
		// "SELECT * FROM MEMBER WHERE USERNAME LIKE '%" + keyword + "%'"
		String sql = "SELECT * FROM MEMBER WHERE USERNAME LIKE '%' || ? || '%'";
		
		try {// 1) JDBC 드라이버 등록
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			// 2) Connection 생성
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "JDBC", "JDBC");
			
			// 3) Statement 생성
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, keyword);
			
			// 4, 5) sql문 실행 및 결과받기
			rset = pstmt.executeQuery();
			
			while(rset.next()) {
				Member m = new Member(rset.getInt("userNo"),
						rset.getString("userId"),
						rset.getString("userPwd"),
						rset.getString("userName"),
						rset.getString("gender"),
						rset.getInt("age"),
						rset.getString("email"),
						rset.getString("phone"),
						rset.getString("address"),
						rset.getString("hobby"),
						rset.getDate("enrollDate"));
				
				list.add(m); 
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rset.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
		
	}
	
	public int updateMember(Member m) {
		// update문 => 처리된 행의 수(int) => 트랜잭션 처리
		int result = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		/*
		 * update member
		 * set userpwd = 'xx', email = 'xx', phone = 'xx', address = 'xx'
		 * where userid = 'xxx';
		 */
		
		String sql = "UPDATE MEMBER SET USERPWD = ?, EMAIL = ?, PHONE = ?, ADDRESS = ? WHERE USERID = ?";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "JDBC", "JDBC");
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, m.getUserPwd());
			pstmt.setString(2, m.getEmail());
			pstmt.setString(3, m.getPhone());
			pstmt.setString(4, m.getAddress());
			pstmt.setString(5, m.getUserId());
			
			result = pstmt.executeUpdate();
			
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public int deleteMember(String userId) {
		//delete문 => 처리된 행의 수 => 트랜잭션 처리
		int result = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "DELETE FROM MEMBER WHERE USERID = ?";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "JDBC", "JDBC");
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			
			result = pstmt.executeUpdate();
			
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
}

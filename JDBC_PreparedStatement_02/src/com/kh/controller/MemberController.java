package com.kh.controller;

import java.util.ArrayList;

import com.kh.model.dao.MemberDao;
import com.kh.model.vo.Member;
import com.kh.view.MemberMenu;

//Controller : View를 통해서 사용자가 요청한 기능에 대해서 처리하는 담당
//			   해당 메소드로 전달된 데이터 [가공처리 후] dao로 전달하며 호출
//			   dao로부터 반환받은 결과에 따라서 성공인지 실패인지 판단 후 응답화면 결정(View 메소드 호출)
public class MemberController {
	
	/**
	 * 사용자의 추가요청을 처리해주는 메소드
	 * @param userId ~ hobby : 사용자가 입력했던 정보들이 답겨있는 매개변수
	 */
	public void insertMember(String userId, String userPwd, String userName, String gender, String age,
			String email, String phone, String address, String hobby) {
		
		// view로부터 전달받은 값을 바로 dao쪽으로 전달x
		// 어딘가(Member 객체(vo))에 담아서 전달
		
		// 방법1) 기본생성자로 생성한 후 각 필드의 setter메소드를 통해 일일이 담는 방법
		// 방법2) 매개변수 생성자로 생성과 동시에 담는 방법
		
		Member m = new Member(userId, userPwd, userName, gender, Integer.parseInt(age), email, phone, address, hobby);
		
		int result = new MemberDao().insertMember(m);
		
		if(result > 0) {  // insert가 성공적으로 완료
			new MemberMenu().displaySuccess("성공적으로 회원이 추가되었습니다.");
		} else {  // insert가 실패
			new MemberMenu().displayFail("회원추가를 실패하였습니다.");
		}
		
	}
	
	public void selectList() {
		ArrayList<Member> list = new MemberDao().selectList();
		
		// 조회된 결과에 따라 사용자가 보게될 응답화면 지정
		if(list.isEmpty()) { // list가 비어있을 경우 (list.size() == 0)
			new MemberMenu().displayNoData("전체 조회 결과가 없습니다.");
		} else { //조회된 데이터가 있을 경우
			new MemberMenu().displayMemberList(list);
		}
	}
	
	/**
	 * 사용자의 아이디로 회원 검색 요청을 처리해주는 메소드
	 * @param userId : 사용자가 입력한 겁색하고자하는 회원 아이디
	 */
	public void selectByUserId(String userId) {
		Member m = new MemberDao().selectByUserId(userId);
		
		if(m == null) { // 검색결과 없는 경우
			new MemberMenu().displayNoData(userId + "에 해당하는 조회 결과가 없습니다.");
		} else { // 검색 결과 있는 경우
			new MemberMenu().displayMember(m);
		}
	}
	
	/**
	 * 이름으로 키워드 검색 요청시 처리해주는 메소드
	 * @param keyword : 사용자가 입력한 검색할 키워드명
	 */
	public void selectByUserName(String keyword) {
		ArrayList<Member> list = new MemberDao().selectByUserName(keyword);
		
		if(list.size() > 0) {  // 리스트의 검색 결과가 담겨 있을 때
			new MemberMenu().displayMemberList(list);
		} else {
			new MemberMenu().displayNoData(keyword + "로 조회되는 데이터가 없습니다.");
		}
	}
	
	public void updateMember(String userId, String userPwd, 
							String email, String phone, String address) {
		
		Member m = new Member();
		m.setUserId(userId);
		m.setUserPwd(userPwd);
		m.setEmail(email);
		m.setPhone(phone);
		m.setAddress(address);
		
		int result = new MemberDao().updateMember(m);
		
		if(result > 0) {
			new MemberMenu().displaySuccess("성공적으로 회원정보가 변경되었습니다.");
		} else {
			new MemberMenu().displayFail("회원 정보 변경에 실패하였습니다.");
		}
	}

	public void deleteMember(String userId) {
		int result = new MemberDao().deleteMember(userId);		
		
		if(result > 0) {
			new MemberMenu().displaySuccess("성공적으로 회원 탈퇴처리 되었습니다.");
		} else {
			new MemberMenu().displayFail("회원 탈퇴에 실패하였습니다.");
		}
		
	}
		
}

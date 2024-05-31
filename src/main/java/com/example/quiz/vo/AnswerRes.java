package com.example.quiz.vo;

import java.util.List;

import com.example.quiz.entity.Answer;

public class AnswerRes extends BaseRes{
	
	private List<Answer> answerList;
	
	public AnswerRes() {
		super();
		
	}

	public AnswerRes(int code, String message) {
		super(code, message);
		
	}

	public AnswerRes(int code, String message, List<Answer> answerList) {
		super(code, message);
		this.answerList = answerList;
	}

	public List<Answer> getAnswerList() {
		return answerList;
	}

	public void setAnswerList(List<Answer> answerList) {
		this.answerList = answerList;
	}

}

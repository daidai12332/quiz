package com.example.quiz.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class QuizId implements Serializable{

	private int quizId;
	
	private int quId;

	public QuizId() {
		super();
		
	}

	public QuizId(int quizId, int quId) {
		super();
		this.quizId = quizId;
		this.quId = quId;
	}

	public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public int getQuId() {
		return quId;
	}

	public void setQuId(int quId) {
		this.quId = quId;
	}
	
}

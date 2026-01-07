import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../components/Practice.css";
import MainLogo from "../assets/MainLogo.png";

// 데이터베이스 받아와야함
const personalityQuestions = [
  "본인을 가장 잘 표현할 수 있는 키워드는 무엇인가요?",
  "실패했던 경험과 그 경험에서 배운 점은 무엇인가요?",
  "동료와 의견이 충돌했을 때 어떻게 해결했는지 말해보세요.",
  "최근에 가장 보람을 느꼈던 경험은 무엇인가요?",
  "당신이 중요하게 생각하는 가치는 무엇이며, 그 이유는 무엇인가요?",
  "집에 가고싶나요?",
  "네",
  "제발",
];

// 데이터베이스 받아와야함
const jobQuestions = [
  "이 직무를 지원하게 된 동기는 무엇인가요?",
  "업무에서 가장 자신 있는 역량은 무엇인가요?",
  "해당 직무를 수행하기 위해 준비한 경험은 무엇인가요?",
  "최근 직무 관련 트렌드 중 관심 있는 것은 무엇인가요?",
  "해당 직무에서 이루고 싶은 목표는 무엇인가요?",
];

const Practice = () => {
  const [questionType, setQuestionType] = useState(null);
  const [questions, setQuestions] = useState([]);
  const [selected, setSelected] = useState([]);
  const navigate = useNavigate();

  // 유형 선택
  const handleTypeClick = (type) => {
    setQuestionType(type);
    if (type === "personality") {
      setQuestions(personalityQuestions);
    } else {
      setQuestions(jobQuestions);
    }
    setSelected([]);
  };

  // 질문 선택/해제
  const toggleSelect = (q) => {
    if (selected.includes(q)) {
      setSelected(selected.filter((item) => item !== q));
    } else {
      setSelected([...selected, q]);
    }
  };

  // 랜덤 질문 뽑고 interview 페이지로 이동
  const handleStart = () => {
    if (selected.length === 0) return alert("질문을 선택해주세요!");

    // /PracticeInterview 페이지로 이동하면서 선택된 질문 배열 전체 전달
    navigate("/PracticeInterview", { state: { questionList: selected } });
  };

  return (
    <div className="home-container">
      <div className="doc-feedback-container">
        {/* 제목/설명 */}
        <div className="doc-feedback-header">
          <h2>나 혼자 연습</h2>
          <p>
            원하는 인성 및 직무 면접 질문을 선택하여 나만의 답변을 체계적으로
            준비하세요.
          </p>
        </div>

        {/* 메인 질문 선택 UI */}
        <div className="practice-containerr">
          <aside className="sidebar">
            <h3>면접 유형 선택</h3>
            <button
              className={`type-btn ${
                questionType === "personality" ? "active" : ""
              }`}
              onClick={() => handleTypeClick("personality")}
            >
              인성 유형 질문
            </button>
            <button
              className={`type-btn ${questionType === "job" ? "active" : ""}`}
              onClick={() => handleTypeClick("job")}
            >
              직무 유형 질문
            </button>
          </aside>

          <section className="questions-section">
            <div className="questions-header">
              <h3>총 질문 개수 : {questions.length}</h3>
            </div>
            <div className="questions-list">
              {questions.map((q, idx) => (
                <div
                  key={idx}
                  className={`question-item ${
                    selected.includes(q) ? "selected" : ""
                  }`}
                  onClick={() => toggleSelect(q)}
                >
                  {q}
                </div>
              ))}
            </div>
            <div className="footer">
              <p>선택 된 질문 : {selected.length}개</p>
              <button className="start-btn" onClick={handleStart}>
                시작하기
              </button>
            </div>
          </section>
        </div>
      </div>
    </div>
  );
};

export default Practice;

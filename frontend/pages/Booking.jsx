import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../components/Booking.css";
import MainLogo from "../assets/MainLogo.png";

const Booking = () => {
  const navigate = useNavigate();

  // 예시 전문가 데이터
  const experts = [
    {
      id: 1,
      name: "이지은",
      affi: "카카오",
      role: "백엔드 개발자",
      exp: "10년",
      letter: "안녕",
    },
    {
      id: 2,
      name: "정혜린",
      affi: "카카오",
      role: "데이터 엔지니어",
      exp: "7년",
      letter: "안녕",
    },
    {
      id: 3,
      name: "김민수",
      affi: "카카오",
      role: "프론트엔드 개발자",
      exp: "8년",
      letter: "안녕",
    },
    {
      id: 4,
      name: "박준호",
      affi: "카카오",
      role: "풀스택 개발자",
      exp: "7년",
      letter: "안녕",
    },
  ];

  const [selectedExpert, setSelectedExpert] = useState(null);

  const handleBooking = () => {
    if (!selectedExpert) {
      alert("전문가를 선택해주세요!");
      return;
    }
    navigate("/BookingDate", { state: { expert: selectedExpert } });
  };

  return (
    <div className="home-container">
      <div className="doc-feedback-container">
        {/* 제목/설명 */}
        <div className="doc-feedback-header">
          <h2>1:1 화상면접 ➡ 화상면접 일정 예약 / 결제</h2>
          <p>
            원하는 면접 전문가를 직접 선택하고 1:1 화상면접 일정을 예약하세요.
          </p>
        </div>

        {/* 등급 설명 */}
        <div className="level-info">
          <p>
            <b>컨설턴트 등급은 아래 경력 기준을 따릅니다.</b>
          </p>
          <p>
            <b>JUNIOR</b> : 컨설턴트 경력 5년차 이하
          </p>
          <p>
            <b>SENIOR</b> : 컨설턴트 경력 6 ~ 10년차 이하
          </p>
          <p>
            <b>EXECUTIVE</b> : 컨설턴트 경력 10년차 초과
          </p>
        </div>

        {/* 전문가 리스트 */}
        <div className="experts-list">
          {experts.map((expert) => (
            <div
              key={expert.id}
              className={`expert-card ${
                selectedExpert?.id === expert.id ? "selected" : ""
              }`}
              onClick={() => setSelectedExpert(expert)}
            >
              <div className="expert-info">
                <p>이름 : {expert.name}</p>
                <p>소속 : {expert.affi}</p>
                <p>직책/직업 : {expert.role}</p>
                <p>총 경력 : {expert.exp}</p>
                <p>소개글 : {expert.letter}</p>
              </div>
            </div>
          ))}
        </div>

        {/* 예약 버튼 */}
        <div className="booking-button-container">
          <button className="booking-btn" onClick={handleBooking}>
            예약하기
          </button>
        </div>
      </div>
    </div>
  );
};

export default Booking;

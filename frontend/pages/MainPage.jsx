// src/pages/MainPage.jsx
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../components/MainPage.css";
import api from "@/client/axios";
import DocReviewImg from "../assets/DocReviewImg.png";
import PracticeImg from "../assets/PracticeImg.png";
import MeetingImg from "../assets/MeetingImg.png";

const MainPage = () => {
  const navigate = useNavigate();
  const [me, setMe] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let alive = true;
    (async () => {
      try {
        const { data } = await api.get("/auth/me", { withCredentials: true });
        if (alive) setMe(data);
      } catch {
        if (alive) navigate("/", { replace: true }); // 로그인 화면
      } finally {
        if (alive) setLoading(false);
      }
    })();
    return () => { alive = false; };
  }, [navigate]);

  const handleDocReviewClick = () => navigate("/DocReview");
  const handlePracticeClick = () => navigate("/Practice");
  const handleMeetingClick = () => navigate("/Meeting");
  const handleBookingClick = () => navigate("/Booking");

  if (loading) return null;

  return (
    <div className="home-container">
      {/* ✅ 공용 헤더는 Layout이 렌더링. 여기서는 본문만 */}
      <div className="greeting">{(me?.name ?? "회원")}님 안녕하세요!</div>

      <div className="content-grid">
        <div className="left-column">
          <div className="card" onClick={handleDocReviewClick} style={{ cursor: "pointer" }}>
            <img src={DocReviewImg} alt="AI 자소서 피드백" />
            <div className="card-text">
              <h3>AI 자소서 피드백</h3>
              <p>맞춤형 자소서 분석</p>
            </div>
          </div>

          <div className="card" onClick={handlePracticeClick} style={{ cursor: "pointer" }}>
            <img src={PracticeImg} alt="나혼자 연습" />
            <div className="card-text">
              <h3>나혼자 연습</h3>
              <p>스스로 피드백</p>
            </div>
          </div>
        </div>

        <div className="right-column">
          <div className="card large-card">
            <img src={MeetingImg} alt="화상면접" />
            <div className="card-text">
              <h3>1:1 화상면접</h3>
              <p>전문가와 실전 면접</p>
            </div>
            <div className="meeting-buttons">
              <button onClick={handleMeetingClick}>화상면접 입장하기 ➡</button>
              <button onClick={handleBookingClick}>화상면접 일정 예약</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
export default MainPage;

import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom"; // 추가
// import api from "../api/axios";
import "../components/MyPageSchedule.css";

const MyPageSchedule = () => {
  const [schedules, setSchedules] = useState([]);
  const navigate = useNavigate(); // useNavigate 선언 추가

  useEffect(() => {
    // 🔹 임시 데이터 (나중에 백엔드 연결되면 api.get으로 교체)
    const mockData = [
      {
        id: 1,
        year: 2025,
        month: 9,
        day: 25,
        time: "14:00",
        consultant: "정혜인",
        status: "upcoming", // upcoming | done
      },
      {
        id: 2,
        year: 2025,
        month: 8,
        day: 10,
        time: "11:00",
        consultant: "최지훈",
        status: "done",
      },
      {
        id: 3,
        year: 2025,
        month: 7,
        day: 3,
        time: "16:30",
        consultant: "김민수",
        status: "done",
      },
    ];

    setSchedules(mockData);
  }, []);

  return (
    <div className="schedule-container">
      <h2>📅 일정 관리</h2>
      <p className="subtitle">컨설턴트는 변경할 수 없습니다.</p>

      <ul className="schedule-list">
        {schedules.length > 0 ? (
          schedules.map((sch) => (
            <li key={sch.id} className={`schedule-item ${sch.status}`}>
              <div>
                <span className="date">
                  {sch.year}-{sch.month}-{sch.day} {sch.time}
                </span>
                <span className="consultant">컨설턴트: {sch.consultant}</span>
              </div>

              {sch.status === "upcoming" ? (
                <button
                  className="btn change"
                  onClick={() =>
                    navigate("/Booking", { state: { schedule: sch } })
                  } // 수정: state로 schedule 전달
                >
                  일정 변경
                </button>
              ) : (
                <span className="done-label">종료됨</span>
              )}
            </li>
          ))
        ) : (
          <p className="empty">일정이 없습니다.</p>
        )}
      </ul>
    </div>
  );
};

export default MyPageSchedule;

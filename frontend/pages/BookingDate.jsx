import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom"; // useNavigate 추가
import "../components/BookingDate.css";

const BookingDate = () => {
  const navigate = useNavigate(); // useNavigate 훅
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth());
  const [selectedDate, setSelectedDate] = useState(null);
  const [selectedTime, setSelectedTime] = useState("");

  // 예시: 나중에 DB/API에서 받아오기
  const availableDates = [6, 8, 10, 13, 15, 17, 22, 24, 27, 30];
  const availableTimes = [
    "11:00 ~ 12:00",
    "13:00 ~ 14:00",
    "15:00 ~ 16:00",
    "17:00 ~ 18:00",
    "19:00 ~ 20:00",
  ];

  const months = [
    "1월",
    "2월",
    "3월",
    "4월",
    "5월",
    "6월",
    "7월",
    "8월",
    "9월",
    "10월",
    "11월",
    "12월",
  ];

  const years = [new Date().getFullYear(), new Date().getFullYear() + 1];

  const firstDayOfMonth = new Date(selectedYear, selectedMonth, 1).getDay();
  const daysInMonth = new Date(selectedYear, selectedMonth + 1, 0).getDate();

  const calendarDays = [];
  for (let i = 0; i < firstDayOfMonth; i++) calendarDays.push(null);
  for (let i = 1; i <= daysInMonth; i++) calendarDays.push(i);

  const weekDays = ["일", "월", "화", "수", "목", "금", "토"];

  // 예약 버튼 클릭 시 /Payment로 이동
  const handleConfirm = () => {
    // 선택한 날짜와 시간을 query나 state로 넘길 수도 있음
    navigate("/Payment", {
      state: {
        year: selectedYear,
        month: selectedMonth + 1,
        date: selectedDate,
        time: selectedTime,
      },
    });
  };

  return (
    <div className="home-container">
      <div className="doc-feedback-container">
        <div className="doc-feedback-header">
          <h2>1:1 화상면접 ➡ 화상면접 일정 예약</h2>
          <p>
            원하는 면접 전문가를 직접 선택하고 1:1 화상면접 일정을 예약하세요.
          </p>
        </div>

        <div className="booking-container">
          {/* 좌측 달력 */}
          <div className="calendar">
            <h3>일정 예약</h3>
            <p>예약하고자 하는 일정을 선택하세요.</p>

            {/* 년도/월 선택 */}
            <div className="year-month-select">
              <select
                value={selectedYear}
                onChange={(e) => setSelectedYear(Number(e.target.value))}
              >
                {years.map((year) => (
                  <option key={year} value={year}>
                    {year}년
                  </option>
                ))}
              </select>
              <select
                value={selectedMonth}
                onChange={(e) => setSelectedMonth(Number(e.target.value))}
              >
                {months.map((month, idx) => (
                  <option key={idx} value={idx}>
                    {month}
                  </option>
                ))}
              </select>
            </div>

            {/* 요일 */}
            <div className="weekdays-grid">
              {weekDays.map((wd) => (
                <div key={wd} className="weekday">
                  {wd}
                </div>
              ))}
            </div>

            {/* 날짜 */}
            <div className="dates-grid">
              {calendarDays.map((day, idx) => (
                <div
                  key={idx}
                  className={`date-item ${
                    day && availableDates.includes(day)
                      ? "available"
                      : "disabled"
                  } ${selectedDate === day ? "selected" : ""}`}
                  onClick={() =>
                    day && availableDates.includes(day) && setSelectedDate(day)
                  }
                >
                  {day || ""}
                </div>
              ))}
            </div>
          </div>

          {/* 우측 시간 선택 */}
          <div className="time-selection">
            <h3>시간 예약</h3>
            {selectedDate ? (
              <>
                <p>
                  {selectedYear}년 {selectedMonth + 1}월 {selectedDate}일을
                  선택하셨습니다.
                </p>
                <select
                  value={selectedTime}
                  onChange={(e) => setSelectedTime(e.target.value)}
                >
                  <option value="">예약 시간을 선택하세요.</option>
                  {availableTimes.map((time) => (
                    <option key={time} value={time}>
                      {time}
                    </option>
                  ))}
                </select>
                {selectedTime && (
                  <button className="confirm-btn" onClick={handleConfirm}>
                    예약 확인 및 결제
                  </button>
                )}
              </>
            ) : (
              <p>날짜를 선택해주세요.</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default BookingDate;

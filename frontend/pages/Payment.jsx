import React, { useState } from "react"; // DB 연결하고 useEffect 추가 필요
import { Link, useNavigate } from "react-router-dom"; // ✅ useNavigate 추가
import "../components/Payment.css";

const Payment = ({ expertName, bookingDate, hasTicket }) => {
  const [selectedTicket, setSelectedTicket] = useState(null);
  const [paymentCompleted, setPaymentCompleted] = useState(hasTicket || false);
  const navigate = useNavigate(); // ✅ useNavigate 훅 선언

  const tickets = [
    {
      id: 1,
      title: "1회권",
      originalprice: 20000,
      price: 9800,
      discount: "51%",
      validity: "구매일 이후 1개월",
    },
    {
      id: 3,
      title: "3회권",
      originalprice: 60000,
      price: 23700,
      discount: "60%",
      validity: "구매일 이후 3개월",
    },
    {
      id: 7,
      title: "7회권",
      originalprice: 140000,
      price: 40600,
      discount: "71%",
      validity: "구매일 이후 6개월",
    },
  ];

  const handlePayment = () => {
    if (selectedTicket || hasTicket) {
      setPaymentCompleted(true);

      // ✅ 결제 완료 알림 후 메인 페이지로 이동
      alert("✅ 결제가 완료되었습니다!");
      navigate("/MainPage");
    } else {
      alert("결제하실 사용권을 선택해주세요.");
    }
  };

  return (
    <div className="home-container">
      <div className="doc-feedback-container">
        <div className="doc-feedback-header">
          <h2>1:1 화상면접 ➡ 화상면접 결제</h2>
          <p>결제를 완료하면 전문가와의 화상 면접 예약이 확정됩니다.</p>
        </div>

        {/* 전문가 정보 */}
        <div className="booking-info">
          <p>
            예약 전문가: <strong>{expertName}</strong>
          </p>
          <p>
            예약 일정: <strong>{bookingDate}</strong>
          </p>
        </div>

        {/* 티켓 선택 */}
        {!paymentCompleted && (
          <div className="ticket-options">
            {tickets.map((ticket) => (
              <div
                key={ticket.id}
                className={`ticket-card ${
                  selectedTicket === ticket.id ? "selected" : ""
                }`}
                onClick={() => setSelectedTicket(ticket.id)}
              >
                <h3>{ticket.title}</h3>
                <p className="originalprice">
                  {ticket.originalprice.toLocaleString()}원
                </p>
                <p className="price">
                  {ticket.price.toLocaleString()}원{" "}
                  <span className="discount">{ticket.discount} 할인</span>
                </p>
                <p className="validity">유효기간: {ticket.validity}</p>
              </div>
            ))}
          </div>
        )}

        {/* 결제 버튼 */}
        {!paymentCompleted && (
          <button className="pay-button" onClick={handlePayment}>
            결제하기
          </button>
        )}
      </div>
    </div>
  );
};

export default Payment;

import React, { useEffect, useState } from "react";
import "../components/MyPagePayment.css";

const MyPagePayment = () => {
  const [payments, setPayments] = useState([]);
  const [selectedPaymentId, setSelectedPaymentId] = useState(null);

  useEffect(() => {
    // 🔹 임시 더미 데이터
    const mockPayments = [
      {
        id: 1,
        title: "1회권",
        price: 9800,
        validity: "2025.09.05 ~ 2025.10.04",
        used: 0,
        total: 1,
        card: "삼성카드(4929)",
        history: [{ date: "2025.09.05 14:30", card: "삼성카드(4929)" }],
      },
      {
        id: 2,
        title: "3회권",
        price: 23700,
        validity: "2025.09.05 ~ 2025.12.04",
        used: 1,
        total: 3,
        card: "신한카드(1234)",
        history: [{ date: "2025.09.06 10:00", card: "신한카드(1234)" }],
      },
    ];

    setPayments(mockPayments);
  }, []);

  return (
    <div className="payments-container">
      <h2>💳 결제 내역</h2>
      {payments.length === 0 ? (
        <p className="empty">결제 내역이 없습니다.</p>
      ) : (
        <ul className="payments-list">
          {payments.map((payment) => (
            <li
              key={payment.id}
              className={`payment-item ${
                selectedPaymentId === payment.id ? "selected" : ""
              }`}
              onClick={() =>
                setSelectedPaymentId(
                  selectedPaymentId === payment.id ? null : payment.id
                )
              }
            >
              <div className="payment-info">
                <span className="title">{payment.title}</span>
                <span className="price">
                  {payment.price.toLocaleString()}원
                </span>
              </div>
              <div className="payment-detail">
                <span className="validity">이용 기간 : {payment.validity}</span>
                <span className="usage">
                  사용 횟수 : {payment.used} / {payment.total}
                </span>
              </div>

              {selectedPaymentId === payment.id && (
                <div className="payment-history">
                  <h4>사용 내역</h4>
                  {payment.history.map((h, idx) => (
                    <p key={idx}>
                      {h.date} <br /> {h.card}
                    </p>
                  ))}
                </div>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default MyPagePayment;

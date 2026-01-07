import React, { useState } from "react";
import { Link } from "react-router-dom";
import "../components/MyPageEditProfile.css";
import api from "@/client/axios"

const MyPageEditProfile = () => {
  const [password, setPassword] = useState("");
  const [passwordMessage, setPasswordMessage] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [name, setName] = useState("");
  const [phone, setPhone] = useState("");
  const [email, setEmail] = useState("");
  const [verificationCode, setVerificationCode] = useState("");
  const [emailSuccess, setEmailSuccess] = useState("");
  const [emailError, setEmailError] = useState("");
  const [verifySuccess, setVerifySuccess] = useState(false);
  const [verifyError, setVerifyError] = useState("");
  const [formError, setFormError] = useState("");

  // 비밀번호 입력 체크
  const handlePasswordChange = (e) => {
    const value = e.target.value;
    setPassword(value);
    const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
    if (regex.test(value)) {
      setPasswordMessage("사용 가능한 비밀번호입니다.");
    } else {
      setPasswordMessage(
        "영문 대/소문자, 숫자를 포함하여 8자 이상 입력하세요."
      );
    }
  };

  // 이메일 인증 전송
  const sendVerificationEmail = async () => {
    if (!email || !name || !phone) {
      // password 체크 제거
      setFormError("이름, 전화번호, 이메일은 필수 입력 사항입니다");
      return;
    }
    try {
      const response = await api.post("/email/send", { email });
      if (response.status === 200) {
        setEmailSuccess("인증 이메일이 전송되었습니다.");
        setEmailError("");
      }
    } catch (error) {
      setEmailError(
        "이메일 전송 실패: " + (error.response?.data?.message || "서버 오류")
      );
      setEmailSuccess("");
    }
  };

  // 이메일 코드 인증
  const verifyCode = async () => {
    try {
      const response = await api.post("/email/verify", {
        email,
        code: verificationCode,
      });
      if (response.status === 200) {
        setVerifySuccess(true);
        setVerifyError("");
      } else {
        setVerifyError("인증번호가 올바르지 않습니다.");
        setVerifySuccess(false);
      }
    } catch (error) {
      setVerifyError(
        "인증 실패: " + (error.response?.data?.message || "서버 오류")
      );
      setVerifySuccess(false);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (!name || !phone || !email || !password) {
      setFormError("모든 필수 정보를 입력해주세요.");
      return;
    }
    if (!verifySuccess) {
      setFormError("이메일 인증을 완료해야 저장할 수 있습니다.");
      return;
    }

    const payload = { password, name, phone, email };
    console.log("서버에 보낼 데이터:", payload);
    alert("회원정보가 저장되었습니다. (데모)");
  };

  return (
    <div className="edit-profile-container">
      <header className="edit-header">
        <h2>회원정보 수정</h2>
        <p className="subtitle">
          변경하지 않을 정보는 이전과 동일하게 기입해주세요.
        </p>
        <p className="subtitle">아이디는 변경할 수 없습니다.</p>
      </header>

      <form className="edit-form" onSubmit={handleSubmit}>
        {/* 비밀번호 */}
        <div className="form-row">
          <label>비밀번호</label>
          <div className="input-with-btn">
            <input
              type={showPassword ? "text" : "password"}
              value={password}
              onChange={handlePasswordChange}
              placeholder="변경할 비밀번호 입력 (미기입 시 이전 비밀번호 유지)"
            />
            <button
              type="button"
              className="toggle-pw"
              onClick={() => setShowPassword((s) => !s)}
            >
              {showPassword ? "숨기기" : "보기"}
            </button>
          </div>
          {password && <p className="password-message">{passwordMessage}</p>}
        </div>

        {/* 이름 */}
        <div className="form-row">
          <label>이름</label>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="이름 입력"
          />
        </div>

        {/* 전화번호 */}
        <div className="form-row">
          <label>전화번호</label>
          <input
            type="tel"
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
            placeholder="하이픈(-) 없이 입력"
          />
        </div>

        {/* 이메일 */}
        <div className="form-row">
          <label>이메일</label>
          <div className="input-with-btn email">
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="example@email.com"
            />
            <button
              type="button"
              className="btn send"
              onClick={sendVerificationEmail}
            >
              인증메일 전송
            </button>
          </div>
          {emailSuccess && <p className="success">{emailSuccess}</p>}
          {emailError && <p className="error">{emailError}</p>}
        </div>

        {/* 인증번호 */}
        <div className="form-row">
          <label>인증번호</label>
          <div className="input-with-btn verification">
            <input
              type="text"
              value={verificationCode}
              onChange={(e) => setVerificationCode(e.target.value)}
              placeholder="인증번호 입력"
            />
            <button type="button" className="btn verify" onClick={verifyCode}>
              인증 확인
            </button>
          </div>
          {verifySuccess && <p className="success">인증 완료</p>}
          {verifyError && <p className="error">{verifyError}</p>}
        </div>

        {/* 전체 오류 */}
        {formError && <p className="error">{formError}</p>}

        {/* 버튼 */}
        <div className="form-actions">
          <Link to="/mypage" className="btn cancel">
            취소
          </Link>
          <button type="submit" className="btn primary">
            저장
          </button>
        </div>
      </form>
    </div>
  );
};

export default MyPageEditProfile;

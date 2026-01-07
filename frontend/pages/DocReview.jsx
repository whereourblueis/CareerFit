import React, { useState } from "react";
import { Link } from "react-router-dom";
import "../components/DocReview.css";
import MainLogo from "../assets/MainLogo.png";

const DocReview = () => {
  const [inputText, setInputText] = useState("");
  const [checkedText, setCheckedText] = useState("");

  // 글자 수 세기
  const countWithSpaces = inputText.length;
  const countWithoutSpaces = inputText.replace(/\s/g, "").length;

  // 점검하기 클릭 시 오른쪽 박스에 표시
  const handleCheck = () => {
    setCheckedText(inputText);
  };

  return (
    <div className="home-container">
      <div className="doc-feedback-container">
        {/* 제목/설명 */}
        <div className="doc-feedback-header">
          <h2>AI 자기소개서 피드백</h2>
          <p>
            자기소개서를 입력하면 AI가 내용을 매끄럽게 수정하고 오탈자를
            점검합니다.
          </p>
        </div>

        {/* 입력/출력 박스 */}
        <div className="doc-feedback-boxes">
          {/* 왼쪽 입력 */}
          <div className="input-box">
            <textarea
              placeholder="내용을 직접 작성하거나, 복사해서 붙여 넣어주세요."
              value={inputText}
              onChange={(e) => setInputText(e.target.value)}
            />
            <div className="bottom-row">
              <div className="char-count">
                <p>
                  공백포함 : 총 {countWithSpaces}자 ({countWithSpaces}byte)
                </p>
                <p>
                  공백제외 : 총 {countWithoutSpaces}자 ({countWithoutSpaces}
                  byte)
                </p>
              </div>
              <button className="check-btn" onClick={handleCheck}>
                점검하기
              </button>
            </div>
          </div>
          {/* 오른쪽 출력 */}
          <div className="output-box">
            <textarea
              readOnly
              value={checkedText}
              placeholder="점검 된 내용이 표시됩니다."
            />
            <div className="char-count">
              <p>
                공백포함 : 총 {checkedText.length}자 ({checkedText.length}byte)
              </p>
              <p>
                공백제외 : 총 {checkedText.replace(/\s/g, "").length}자 (
                {checkedText.replace(/\s/g, "").length}byte)
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DocReview;

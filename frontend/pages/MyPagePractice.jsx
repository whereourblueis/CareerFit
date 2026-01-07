import React, { useEffect, useState } from "react";
import "../components/MyPagePractice.css";

const MyPagePractice = () => {
  const [videos, setVideos] = useState([]);

  useEffect(() => {
    // 🔹 임시 더미 데이터
    const mockVideos = [
      {
        id: 1,
        title: "자기소개 연습",
        length: "02:15",
        date: "2025-09-20 14:30",
        src: "/videos/sample1.mp4",
        showVideo: false,
      },
      {
        id: 2,
        title: "기술 질문 연습",
        length: "03:40",
        date: "2025-09-18 10:20",
        src: "/videos/sample2.mp4",
        showVideo: false,
      },
    ];
    setVideos(mockVideos);
  }, []);

  const toggleVideo = (id) => {
    setVideos((prev) =>
      prev.map((vid) =>
        vid.id === id ? { ...vid, showVideo: !vid.showVideo } : vid
      )
    );
  };

  const handleTitleChange = (id, value) => {
    setVideos((prev) =>
      prev.map((vid) => (vid.id === id ? { ...vid, title: value } : vid))
    );
  };

  return (
    <div className="practice-container">
      <h2>🎬 나 혼자 연습 영상</h2>
      {videos.length === 0 ? (
        <p className="empty">저장된 영상이 없습니다.</p>
      ) : (
        <ul className="videos-list">
          {videos.map((vid) => (
            <li
              key={vid.id}
              className="video-item"
              onClick={() => toggleVideo(vid.id)} // 전체 클릭 가능
            >
              <input
                className="video-title-input"
                value={vid.title}
                onChange={(e) => handleTitleChange(vid.id, e.target.value)}
                // onClick 제거
              />
              {vid.showVideo && (
                <div className="video-wrapper">
                  <video className="video-player" src={vid.src} controls />
                </div>
              )}
              <div className="video-meta">
                <span>길이 : {vid.length}</span>
                <br />
                <span>녹화일 : {vid.date}</span>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default MyPagePractice;

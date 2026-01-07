// ✅ 백엔드 서버 주소 (로컬 Spring Boot 애플리케이션)
const APPLICATION_SERVER_URL = "http://localhost:8080/";

// OpenVidu 객체
var OV;
var session;

// DOM 요소
const joinBtn = document.getElementById('join-btn');
const joinExistingBtn = document.getElementById('join-existing-btn');
const leaveBtn = document.getElementById('leave-btn');
const sessionIdInput = document.getElementById('session-id-input');

// 이벤트 리스너
joinBtn.addEventListener('click', () => joinSession(true));
joinExistingBtn.addEventListener('click', () => joinSession(false));
leaveBtn.addEventListener('click', leaveSession);
window.addEventListener('beforeunload', leaveSession);


function joinSession(isNewSession) {
    if (isNewSession) {
        createNewSession().then(sessionId => {
            sessionIdInput.value = sessionId;
            getToken(sessionId).then(token => connectToSession(token));
        }).catch(error => {
            console.error("세션 생성 중 오류 발생:", error);
            alert("세션 생성에 실패했습니다. 백엔드 서버와 OpenVidu 서버가 실행 중인지 확인하세요.");
        });
    } else {
        const sessionId = sessionIdInput.value;
        if (!sessionId) {
            alert("참여할 세션 ID를 입력해주세요.");
            return;
        }
        getToken(sessionId).then(token => connectToSession(token));
    }
}

function connectToSession(token) {
    OV = new OpenVidu();
    session = OV.initSession();

    // 새 사용자 스트림
    session.on('streamCreated', (event) => {
        session.subscribe(event.stream, 'subscriber');
    });

    session.on('streamDestroyed', () => {
        console.log("상대방이 나갔습니다.");
    });

    session.on('exception', (exception) => {
        console.warn(exception);
    });

    session.connect(token, { clientData: 'User ' + Math.floor(Math.random() * 100) })
        .then(() => {
            const publisher = OV.initPublisher('publisher', {
                audioSource: undefined,
                videoSource: undefined,
                publishAudio: true,
                publishVideo: true,
                resolution: '640x480',
                frameRate: 30,
                insertMode: 'APPEND',
                mirror: true
            });

            session.publish(publisher);

            joinBtn.disabled = true;
            joinExistingBtn.disabled = true;
            sessionIdInput.disabled = true;
            leaveBtn.disabled = false;
        })
        .catch(error => {
            console.log('세션 연결 중 오류:', error.code, error.message);
            alert("세션 연결에 실패했습니다. 토큰이 올바른지 확인하세요.");
        });
}

function leaveSession() {
    if (session) session.disconnect();
    session = null;
    OV = null;

    joinBtn.disabled = false;
    joinExistingBtn.disabled = false;
    sessionIdInput.disabled = false;
    leaveBtn.disabled = true;
    document.getElementById('publisher').innerHTML = '';
    document.getElementById('subscriber').innerHTML = '';
    console.log('통화 종료');
}

// --- ✅ Spring Boot 백엔드 API를 호출하도록 수정 ---

async function createNewSession() {
    // ✅ 백엔드의 '/api/openvidu/sessions' 엔드포인트 호출
    const response = await fetch(APPLICATION_SERVER_URL + 'api/openvidu/sessions', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
        // ✅ body는 백엔드에서 필요 없으므로 제거 (필요 시 빈 객체 {} 전송)
    });
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    // ✅ 백엔드가 String(텍스트)를 반환하므로 .json()이 아닌 .text() 사용
    const sessionId = await response.text();
    console.log("새로운 세션 생성:", sessionId);
    return sessionId;
}

async function getToken(sessionId) {
    // ✅ 백엔드의 '/api/openvidu/sessions/{sessionId}/connections' 엔드포인트 호출
    // ✅ 'connection' -> 'connections' 오타 수정
    const response = await fetch(APPLICATION_SERVER_URL + 'api/openvidu/sessions/' + sessionId + '/connections', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
    });
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    // ✅ 백엔드가 String(텍스트)를 반환하므로 .json()이 아닌 .text() 사용
    const token = await response.text();
    console.log("토큰 수신:", token);
    return token;
}

// === 아래 코드를 새로 추가합니다. ===

// "혼자 면접 연습하기" 버튼에 클릭 이벤트 리스너를 추가합니다.
document.getElementById('solo-practice-btn').addEventListener('click', (event) => {
    event.preventDefault();
    startSoloPractice();
});

/**
 * 녹화 기능이 켜진 개인 연습 세션을 시작합니다.
 */
async function startSoloPractice() {
    try {
        console.log("개인 면접 연습 세션을 시작합니다...");

        // 1. 세션 생성
        const sessionResponse = await fetch('/api/openvidu/sessions', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ recordingMode: 'ALWAYS' }),
        });
        if (!sessionResponse.ok) throw new Error('백엔드에서 세션 생성에 실패했습니다.');
        const sessionId = await sessionResponse.text();
        console.log("세션 생성 완료:", sessionId);

        // 2. 토큰 발급
        const tokenResponse = await fetch(`/api/openvidu/sessions/${sessionId}/connections`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({}),
        });
        if (!tokenResponse.ok) throw new Error('백엔드에서 토큰 발급에 실패했습니다.');
        const token = await tokenResponse.text();
        console.log("토큰 발급 완료:", token);

        alert(`개인 연습 세션이 시작되었습니다. (세션 ID: ${sessionId}). 녹화가 진행 중입니다.`);

        connectToSession(token);
        // -------------------------------------------------

    } catch (error) {
        console.error("개인 면접 연습 시작 중 오류 발생:", error);
        alert("세션 시작에 실패했습니다. 브라우저 콘솔을 확인해주세요.");
    }
}
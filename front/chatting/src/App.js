import { useState, useEffect } from "react"; // React 훅 불러오기
import "./App.css"; // CSS 스타일링
import SockJS from "sockjs-client"; // SockJS 클라이언트
import { Client } from "@stomp/stompjs"; // STOMP 클라이언트
import axios from "axios";

function App() {
  // STOMP 클라이언트 상태
  const [stompClient, setStompClient] = useState(null);
  // 현재 방에서 수신한 채팅 메시지 배열
  const [receivedMessages, setReceivedMessages] = useState([]);
  // 입력 중인 채팅 메시지
  const [inputMessage, setInputMessage] = useState("");
  // 닉네임 입력 상태 (아직 확정되지 않은 값)
  const [nicknameInput, setNicknameInput] = useState("");
  // 확정된 닉네임
  const [nickname, setNickname] = useState("");
  // 현재 선택한 방
  const [roomId, setRoomId] = useState(1);
  // 현재 방 접속자 목록
  const [users, setUsers] = useState([]);

  // STOMP 클라이언트 초기화 (앱 시작 시 한 번만)
  useEffect(() => {
    const socket = new SockJS("http://localhost:8080/ws"); // SockJS 소켓 생성

    const client = new Client({
      webSocketFactory: () => socket, // SockJS 사용
      reconnectDelay: 5000, // 연결 끊김 시 5초 후 재연결
      onConnect: () => {
        console.log("STOMP 연결 성공");
      },
    });

    client.activate(); // 클라이언트 활성화
    setStompClient(client); // 상태에 저장

    // 클린업 함수
    // 앱 종료 시 클라이언트 비활성화
    return () => client.deactivate();
  }, []);

  // roomId 또는 닉네임 변경 시 입장/퇴장 + 구독 처리
  useEffect(() => {
    if (!stompClient || !stompClient.connected) return; // 연결 체크
    if (!nickname) return; // 닉네임 확정 전에는 실행하지 않음

    let chatSub, chatHistory, userSub;

    // 새 채팅방 구독
    chatSub = stompClient.subscribe(`/sub/messages/${roomId}`, (message) => {
      const received = JSON.parse(message.body);
      setReceivedMessages((prev) => [...prev, received]);
    });

    // 새 채팅방 구독
    chatHistory = stompClient.subscribe(`/sub/chatHistory/${roomId}`, (messages) => {
      const received = JSON.parse(messages.body);
      setReceivedMessages((prev) => [...prev, ...received]);
    });

    // 채팅방에 참여한 구독자 리스트에 추가됨
    userSub = stompClient.subscribe(`/sub/users/${roomId}`, (message) => {
      const userList = JSON.parse(message.body);
      setUsers(userList);
    });

    // 입장 메시지 전송
    const joinMessage = { type: "JOIN", sender: nickname, content: "" };
    stompClient.publish({
      destination: `/pub/send/${roomId}`,
      body: JSON.stringify(joinMessage),
    });

    // 언마운트 또는 roomId 변경 시 퇴장 메시지 전송 및 구독 해제
    return () => {
      chatSub && chatSub.unsubscribe();
      chatHistory && chatHistory.unsubscribe();
      userSub && userSub.unsubscribe();

      const leaveMessage = { type: "LEAVE", sender: nickname, content: "" };
      if (stompClient.connected) { // 연결 여부 다시 체크
        stompClient.publish({
          destination: `/pub/send/${roomId}`,
          body: JSON.stringify(leaveMessage),
        });
      }
    };
  }, [roomId, nickname, stompClient]);

  // 채팅 메시지 전송
  const sendMessage = () => {
    if (!stompClient || !stompClient.connected) return; // 연결 체크
    if (inputMessage.trim()) {
      const msg = { type: "CHAT", sender: nickname, content: inputMessage };
      stompClient.publish({
        destination: `/pub/send/${roomId}`,
        body: JSON.stringify(msg),
      });
      setInputMessage(""); // 입력창 초기화
    }
  };

  // 닉네임 확정 버튼
  const confirmNickname = () => {
    if (nicknameInput.trim()) {
      setNickname(nicknameInput); // 입력값 확정
    }
  };

  // 채팅 기록 불러오기
  const getChattingHistory = async (targetRoomId) => {
    setReceivedMessages([]);

    try {
      const chatroomData = {
        roomId:targetRoomId,
        userId:nicknameInput
      };
      console.log("요청 데이터:", chatroomData); 
      const response = await axios.post("http://localhost:8080/chat_history", chatroomData);
      console.log("응답 데이터:", response.data);
      setReceivedMessages(response.data);

    } catch (error) {
      alert("채팅기록을 불러올 수 없습니다.");
    }
  }

  return (
    <div>
      <h1>💬 멀티룸 채팅</h1>

      {/* 닉네임 입력 */}
      {!nickname && (
        <div>
          <input
            type="text"
            placeholder="닉네임 입력"
            value={nicknameInput}
            onChange={(e) => setNicknameInput(e.target.value)}
          />
          <button onClick={() => {
            confirmNickname();
            getChattingHistory(roomId);
          }}>입장</button>
        </div>
      )}

      {/* 닉네임이 확정되면 채팅 UI 표시 */}
      {nickname && (
        <>
          {/* 방 선택 */}
          <div>
            <label>방 선택: </label>
            <select
              value={roomId}
              onChange={(e) => {
                const newRoomId = e.target.value;
                setRoomId(newRoomId); // 방 변경
                getChattingHistory(newRoomId);   // 채팅 내역 불러오기
              }}
            >
              <option value={1}>Room 1</option>
              <option value={2}>Room 2</option>
              <option value={3}>Room 3</option>
            </select>
          </div>

          {/* 메시지 입력 */}
          <div>
            <input
              type="text"
              placeholder="메시지 입력"
              value={inputMessage}
              onChange={(e) => setInputMessage(e.target.value)}
            />
            <button onClick={sendMessage}>전송</button>
          </div>

          {/* 접속자 목록 */}
          <div>
            <p>현재 방 접속자 : &nbsp;
              {users.map((user, idx) => (
                <span key={idx}>{user} &nbsp;</span>
              ))}
            </p>
          </div>

          {/* 채팅 메시지 표시 */}
          <ul>
            {receivedMessages.map((msg, idx) => (
              <li key={idx}>
                {msg.type === "CHAT" && <strong>{msg.sender} : {msg.content}</strong>}
                {msg.type === "JOIN" && <em>{msg.sender} {msg.content}</em>}
                {msg.type === "LEAVE" && <em>{msg.content}</em>}
              </li>
            ))}
          </ul>
        </>
      )}
    </div>
  );
}

export default App;

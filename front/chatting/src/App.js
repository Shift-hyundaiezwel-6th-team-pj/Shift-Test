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
  // 사용자가 참여한 채팅방 목록
  const [rooms, setRooms] = useState([]);
  // 사용자 정보
  const [userInfo, setUserInfo] = useState();
  // 닉네임 입력 상태 (아직 확정되지 않은 값)
  const [nicknameInput, setNicknameInput] = useState("");
  // 확정된 닉네임
  const [nickname, setNickname] = useState("");
  // 현재 선택한 방
  const [roomId, setRoomId] = useState("1");
  // 현재 방 접속자 목록
  const [users, setUsers] = useState([]);
  // 에러 표시
  const [error, setError] = useState("");

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

  const checkUserInfo = async () => {
    if (!nicknameInput.trim()) return;

    try {
      const userInfo = await axios.get(`http://localhost:8080/users/${nicknameInput}`);
      setNickname(nicknameInput);
      setUserInfo(userInfo.data)
      setError("");
    } catch (err) {
      if (err.userInfo && err.userInfo.status === 404) {
        setError("존재하지 않는 사용자입니다.");
      } else {
        setError("사용자 PK를 입력하세요.");
      }
    }

    try {
      const userChatRoomInfo = await axios.get(`http://localhost:8080/chatrooms/user/${nicknameInput}`);
      setRooms(userChatRoomInfo.data);
    } catch (err) {
      setError("오류 발생")
    }
  };

  // roomId 또는 닉네임 변경 시 입장/퇴장 + 구독 처리
  useEffect(() => {
    if (!stompClient || !stompClient.connected) return; // 연결 체크
    if (!nickname) return; // 닉네임 확정 전에는 실행하지 않음

    let chatSub, userSub;

    // 새 채팅방 구독
    chatSub = stompClient.subscribe(`/sub/messages/${roomId}`, (message) => {
      const received = JSON.parse(message.body);
      setReceivedMessages((prev) => [...prev, received]);
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
      //*************************************************************************************************************** */
      //*******************************임시로 데이터를 넣은 부분으로 수정 필요******************************************** */
      const msg = { type: "CHAT", chatRoomId : 1, fromId : nickname, sender: nickname, content: inputMessage, isRead : 'N' };
      //*************************************************************************************************************** */
      stompClient.publish({
        destination: `/pub/send/${roomId}`,
        body: JSON.stringify(msg),
      });
      setInputMessage(""); // 입력창 초기화
      console.log(inputMessage);
    }
  };

  return (
    <div>
      <h1>💬 멀티룸 채팅</h1>

      {/* 닉네임 입력 */}
      {!nickname && (
        <div>
          <input
            type="text"
            placeholder="User PK 입력"
            value={nicknameInput}
            onChange={(e) => setNicknameInput(e.target.value)}
          />
          <button onClick={checkUserInfo}>입장</button>
          {error && <p style={{ color: "red" }}>{error}</p>}
        </div>
      )}

      {/* 닉네임이 확정되면 채팅 UI 표시 */}
      {userInfo && (
        <>
          <div>
          <h4>사용자 정보</h4>
          <p>PK : {userInfo.id}</p>
          <p>ID : {userInfo.userId}</p>
          <p>Name : {userInfo.name}</p>
        </div>
          {/* 방 선택 */}
          <div>
            <label>방 선택: </label>
            <div
              value={roomId}
              onChange={(e) => {
                setRoomId(e.target.value); // 방 변경
                setReceivedMessages([]);   // 메시지 초기화
              }}
            >
              {rooms.map((room) => (
                <div key={room.chatRoomId}>
                  <a href="#">
                    {room.fromId}와 {room.toId}의 채팅방
                  </a>
                  <br />
                </div>
              ))}

              </div>
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
                {msg.type === "CHAT" && <strong>{msg.fromId} : {msg.content}</strong>}
                {msg.type === "JOIN" && <em>{msg.fromId} {msg.content}</em>}
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

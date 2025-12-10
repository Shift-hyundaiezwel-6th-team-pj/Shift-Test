import React, { useEffect, useState } from "react";
import axios from "axios";
import SockJS from "sockjs-client"; // SockJS 클라이언트
import { Client } from "@stomp/stompjs"; // STOMP 클라이언트
import {
  Container,
  Row,
  Col,
  Card,
  Input,
  Button,
  InputGroup,
  Badge,
} from "reactstrap";

function Messenger({userId}) {

  const [stompClient, setStompClient] = useState(null);  // STOMP 클라이언트 상태
  const [chatrooms, setChatrooms] = useState([]);  // 채팅방 목록
  const [selectedRoom, setSelectedRoom] = useState(null);  // 선택된 채팅방
  const [messages, setMessages] = useState([]);  // 채팅내역

  // STOMP 클라이언트 초기화
  // 백엔드에서 채팅방 목록 가져오기
  useEffect(() => {
    if (!userId) return;

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

    const fetchChatrooms = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/chatrooms/user/${userId}`);
        setChatrooms(response.data);

      } catch (error) {
        console.error("채팅방 목록 불러오기 실패:", error);
      }
    };

    fetchChatrooms();

    // 클린업 함수
    // 앱 종료 시 클라이언트 비활성화
    return () => client.deactivate();
  }, [userId]);

  // 채팅방 클릭 핸들러
  const handleRoomClick = async (room) => {
    setSelectedRoom(room);
    setMessages([]);

    try {
      console.log("채팅 내역 요청 보냄", {
        fromId: userId,
        toId: room.toUserId,
      });

      const response = await axios.post(
        "http://localhost:8080/chatroom/chat-history",
        {
          fromId: userId,
          toId: room.toUserId,
        }
      );

      console.log("채팅 내역 요청 받음");
      setMessages(response.data); // 서버 응답을 저장
      console.log("채팅 내역 세팅 완료");

    } catch (error) {
      console.error("채팅 내역 불러오기 실패:", error);
    }
  };

  return (
    <main className="content">
      <Container className="p-0">
        <h1 className="h3 mb-3">Messages</h1>
        <Card className="p-0">
          <Row className="g-0">
            {/* Sidebar */}
            <Col xs="12" lg="5" xl="3" className="border-end">
              <div className="px-4 d-none d-md-block">
                <div className="d-flex align-items-center">
                  <div className="flex-grow-1">
                    <Input type="text" className="my-3" placeholder="Search..." />
                  </div>
                </div>
              </div>

              {/* 채팅방 리스트 */}
              {chatrooms.map((room) => (
                <a
                  key={room.chatroomId}
                  href="#!"
                  className={`list-group-item list-group-item-action border-0 ${
                    selectedRoom?.chatroomId === room.chatroomId ? "active" : ""
                  }`}
                  onClick={(e) => {
                    e.preventDefault(); // URL 변경 방지
                    handleRoomClick(room);
                  }}
                >
                  <Badge color="success" pill className="float-end">
                    {/* 이 자리에 안읽은 메세지 갯수 표시 */}
                  </Badge>
                  <div className="d-flex align-items-start">
                    <img
                      src={`https://bootdey.com/img/Content/avatar/avatar1.png`}
                      className="rounded-circle me-1"
                      alt={room.chatroomName}
                      width="40"
                      height="40"
                    />
                    <div className="flex-grow-1 ms-3">
                      {room.chatroomName}
                      <div className="small">
                        <span
                          className={`fas fa-circle ${
                            room.connectionStatus === "ON"
                              ? "chat-online"
                              : "chat-offline"
                          }`}
                        ></span>{" "}
                        {room.connectionStatus === "ON" ? "Online" : "Offline"}
                      </div>
                    </div>
                  </div>
                </a>
              ))}

              <hr className="d-block d-lg-none mt-1 mb-0" />
            </Col>

            {/* 채팅창 */}
            <Col xs="12" lg="7" xl="9">
              {/* 상단 헤더 */}
              <div className="py-2 px-4 border-bottom d-none d-lg-block">
                {selectedRoom ? (
                  <div className="d-flex align-items-center py-1">
                    <div className="position-relative">
                      <img
                        src="https://bootdey.com/img/Content/avatar/avatar1.png"
                        className="rounded-circle me-1"
                        alt={selectedRoom.chatroomName}
                        width="40"
                        height="40"
                      />
                    </div>
                    <div className="flex-grow-1 ps-3">
                      <strong>{selectedRoom.chatroomName}</strong>
                      <div className="text-muted small">
                        <em>
                          {selectedRoom.connectionStatus === "ON"
                            ? "Online"
                            : "Offline"}
                        </em>
                      </div>
                    </div>
                    <div>
                      <Button color="primary" size="lg" className="me-1 px-3">
                        <i className="bi bi-telephone"></i>
                      </Button>
                      <Button color="info" size="lg" className="me-1 px-3 d-none d-md-inline-block">
                        <i className="bi bi-camera-video"></i>
                      </Button>
                      <Button color="light" size="lg" className="border px-3">
                        <i className="bi bi-three-dots"></i>
                      </Button>
                    </div>
                  </div>
                ) : (
                  <div className="text-center py-3 text-muted">
                    채팅방을 선택하세요
                  </div>
                )}
              </div>

              {/* 메세지 내역 영역 */}
              <div className="position-relative">
                <div className="chat-messages p-4">
                  {selectedRoom ? (
                    messages.length > 0 ? (
                      messages.map((msg) => (
                        <div
                          key={msg.messageId}
                          className={`chat-message-${
                            msg.isFromUser === "Y" ? "right" : "left"
                          } pb-4`}
                        >
                          <div>
                            <img
                              src={`https://bootdey.com/img/Content/avatar/avatar${
                                msg.isFromUser === "Y"
                                  ? 1
                                  : (selectedRoom.toUserId % 5) + 1
                              }.png`}
                              className="rounded-circle me-1"
                              alt="user"
                              width="40"
                              height="40"
                            />
                            <div className="text-muted small text-nowrap mt-2">
                              {new Date(msg.sentDate).toLocaleTimeString("ko-KR", {
                                hour: "2-digit",
                                minute: "2-digit",
                              })}
                            </div>
                          </div>
                          <div className="flex-shrink-1 bg-light rounded py-2 px-3 ms-3 me-3">
                            <div className="fw-bold mb-1">
                              {msg.isFromUser === "Y" ? "You" : selectedRoom.chatroomName}
                            </div>
                            {msg.content}
                          </div>
                        </div>
                      ))
                    ) : (
                      <div className="text-center text-muted mt-5">
                        채팅 내역이 없습니다.
                      </div>
                    )
                  ) : (
                    <div
                      className="d-flex justify-content-center align-items-center text-muted"
                      style={{ height: "600px", backgroundColor: "#fff" }}
                    >
                      대화 내용을 보려면 채팅방을 선택하세요.
                    </div>
                  )}
                </div>
              </div>

              {/* Message Input */}
              {selectedRoom && (
                <div className="flex-grow-0 py-3 px-4 border-top">
                  <InputGroup>
                    <Input type="text" placeholder="Type your message" />
                    <Button color="primary">Send</Button>
                  </InputGroup>
                </div>
              )}
            </Col>
          </Row>
        </Card>
      </Container>
    </main>
  );
}

export default Messenger;

import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Container,
  Row,
  Col,
  Card,
  CardBody,
  Input,
  Button
} from "reactstrap";

import logo from "../assets/logo.png";

function Login({ onLoginSuccess }) {

  const [userId, setUserId] = useState("");
  const navigate = useNavigate();

  const handleLogin = () => {
    onLoginSuccess(userId);    // 로그인 상태 갱신
    navigate("/chat");   // 메신저 페이지로 이동
  };

  return (
    <div className="d-flex align-items-center justify-content-center vh-100 bg-light">
      <Container>
        <Row className="justify-content-center">
          <Col md="6" lg="4">
            <Card className="shadow-sm">
              <CardBody className="p-4 text-center">
                {/* 로고 */}
                <img
                  src={logo}
                  alt="Logo"
                  width="100%"
                  height="100%"
                  className="mb-4"
                />

                {/* 아이디 입력칸 */}
                <Input
                  type="text"
                  placeholder="User ID"
                  className="mb-3"
                  value={userId}
                  onChange={(e) => setUserId(e.target.value)}
                />

                {/* 로그인 버튼 */}
                <Button color="primary" block onClick={handleLogin}>
                  로그인
                </Button>
              </CardBody>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
}

export default Login;

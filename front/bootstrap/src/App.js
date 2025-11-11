import React, { useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./components/Login";          // 로그인 페이지
import Messenger from "./components/Messenger";  // 채팅 메신저 페이지

function App() {
  // 간단한 로그인 상태 (실제에선 JWT 토큰 사용)
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userId, setUserId] = useState(null);

  // 로그인 성공 시 userId와 로그인 상태를 설정하는 함수
  const handleLoginSuccess = (id) => {
    setUserId(id);
    setIsLoggedIn(true);
  };

  return (
    <Router>
      <Routes>
        {/* 로그인 페이지 */}
        <Route
          path="/"
          element={<Login onLoginSuccess={handleLoginSuccess} />}
        />

        {/* 로그인 성공 시만 접근 가능한 채팅 페이지 */}
        <Route
          path="/chat"
          element={isLoggedIn ? <Messenger userId={userId}/> : <Navigate to="/" replace />}
        />

        {/* 잘못된 경로는 로그인으로 리다이렉트 */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}

export default App;

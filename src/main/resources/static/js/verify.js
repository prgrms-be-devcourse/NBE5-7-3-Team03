document.addEventListener("DOMContentLoaded", function () {
    const sendBtn = document.getElementById("send-code-btn");
    if (sendBtn) {
        sendBtn.addEventListener("click", sendVerificationCode);
    }

    const verifyBtn = document.getElementById("verify-code-btn");
    if (verifyBtn) {
        verifyBtn.addEventListener("click", verifyEmailCode);
    }
});

function sendVerificationCode() {
    const email = document.getElementById("email").value;
    const univName = document.getElementById("univName").value;

    if (!email || !univName) {
        alert("이메일과 학교명을 모두 입력해주세요.");
        return;
    }

    fetch(`/send-verification-code?email=${encodeURIComponent(email)}&univName=${encodeURIComponent(univName)}`)
        .then(response => response.json())
        .then(data => {
            if (data.data.success) {
                alert(data.msg);
            } else {
                alert(data.msg);
            }
        })
        .catch(error => {
            console.error("인증 요청 중 오류 발생:", error);
            alert("인증 요청 중 오류가 발생했습니다.");
        });
}

function verifyEmailCode() {
    const email = document.getElementById("email").value;
    const univName = document.getElementById("univName").value;
    const verificationCode = document.getElementById("verification-code").value;

    if (!email || !univName || !verificationCode) {
        alert("모든 항목을 입력해주세요.");
        return;
    }

    fetch(`/verify-email-code?email=${encodeURIComponent(email)}&univName=${encodeURIComponent(univName)}&verificationCode=${encodeURIComponent(verificationCode)}`)
        .then(response => response.json())
        .then(data => {
            const resultDiv = document.getElementById("verification-result");
            if (data.data.success) {
                resultDiv.style.color = "green";
                resultDiv.textContent = data.msg;
                document.getElementById("verified").value = "true"; // hidden input 수정
            } else {
                resultDiv.style.color = "red";
                resultDiv.textContent = data.msg;
                document.getElementById("verified").value = "false";
            }
        })
        .catch(error => {
            console.error("코드 검증 오류:", error);
            alert("코드 검증 중 오류가 발생했습니다.");
        });
}
document.addEventListener("DOMContentLoaded", function () {
    const emailInput = document.getElementById("email");
    const emailBtn = document.getElementById("check-email-btn");
    const emailResult = document.getElementById("email-check-result");

    const nicknameInput = document.getElementById("nickname");
    const nicknameBtn = document.getElementById("check-nickname-btn");
    const nicknameResult = document.getElementById("nickname-check-result");

    emailBtn.addEventListener("click", function () {
        const email = emailInput.value.trim();
        if (!email) {
            emailResult.textContent = "이메일을 입력해주세요.";
            emailResult.style.color = "red";
            return;
        }

        fetch(`/api/member/check-email?email=${encodeURIComponent(email)}`)
            .then(res => res.json())
            .then(data => {
                emailResult.textContent = data.msg;
                emailResult.style.color = data.data.exists ? "red" : "green";
            })
    });

    nicknameBtn.addEventListener("click", function () {
        const nickname = nicknameInput.value.trim();
        if (!nickname) {
            nicknameResult.textContent = "닉네임을 입력해주세요.";
            nicknameResult.style.color = "red";
            return;
        }

        fetch(`/api/member/check-nickname?nickname=${encodeURIComponent(nickname)}`)
            .then(res => res.json())
            .then(data => {
                nicknameResult.textContent = data.msg;
                nicknameResult.style.color = data.data.exists ? "red" : "green";
            })
    });
});
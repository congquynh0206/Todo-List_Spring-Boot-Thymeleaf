document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("signUpForm");

    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const usernameInput = document.getElementById("displayName");

    const emailError = document.getElementById("emailError");
    const passwordError = document.getElementById("passwordError");
    const usernameError = document.getElementById("usernameError");

    form.addEventListener("submit", function (e) {
        e.preventDefault();
        let isValid = true;

        // Reset lỗi cũ
        [emailError, passwordError, usernameError].forEach(err => {
            err.textContent = "";
            err.style.display = "none";
        });

        // Validate email format
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailPattern.test(emailInput.value.trim())) {
            emailError.textContent = "Email invalid!";
            emailError.style.display = "block";
            isValid = false;
        }

        // Validate mật khẩu > 6 ký tự
        if (passwordInput.value.trim().length < 6) {
            passwordError.textContent = "Password must be at least 6 character!";
            passwordError.style.display = "block";
            isValid = false;
        }
        if (isValid) {
            const submitBtn = form.querySelector('button[type="submit"]');
            submitBtn.disabled = true;
            submitBtn.textContent = "Registering...";
            form.submit();
        }
    });

    // Hiển thị thông báo thành công nếu có tham số success
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('success') === 'true') {
        showSuccessMessage();
    }

    // Hiển thị thông báo thành công và chuyển hướng
    function showSuccessMessage() {
        const formContainer = document.querySelector('.form-container');
        const successDiv = document.createElement('div');
        successDiv.className = 'success-message';
        successDiv.innerHTML = `
            <h3> Register successfully!</h3>
            <p>Redirecting to login page...</p>
        `;
        formContainer.innerHTML = '';
        formContainer.appendChild(successDiv);
        setTimeout(() => {
            window.location.href = '/login';
        }, 2000);
    }
});
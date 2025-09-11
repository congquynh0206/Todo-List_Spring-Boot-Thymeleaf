document.addEventListener("DOMContentLoaded", () => {
    const togglePassword = document.getElementById("togglePassword");
    const passwordInput = document.getElementById("password");

    // hiện/ẩn icon khi nhập
    passwordInput.addEventListener("input", () => {
        if (passwordInput.value.length > 0) {
            togglePassword.style.display = "block";
        } else {
            togglePassword.style.display = "none";
            passwordInput.setAttribute("type", "password"); // reset về password
            togglePassword.src = "icon/eye-showpass.png"; // reset icon
        }
    });

    // click icon để show/hide
    togglePassword.addEventListener("click", () => {
        const type = passwordInput.getAttribute("type") === "password" ? "text" : "password";
        passwordInput.setAttribute("type", type);

        togglePassword.src = type === "text"
            ? "icon/eye-hidepass.png"
            : "icon/eye-showpass.png";
    });
});

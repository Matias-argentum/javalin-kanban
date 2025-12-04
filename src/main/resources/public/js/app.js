const BASE_URL = "http://localhost:7000";

document.addEventListener("DOMContentLoaded", (event) => {

    const btn = document.getElementById("submitBtn");

    btn.addEventListener("click", () => {
        blockElement(btn);
        handleLogin();
        unblockElement(btn);
    })
});

const handleLogin = async () => {

    const username = document.getElementById("user").value;
    const password = document.getElementById("password").value;
    
    if (username.length < 1 || password.length < 1) {
        showMessage("Ingrese usuario y contraseña", "error");
        return;
    }
    try {
        const data = await login(username, password);
        showMessage("Inicio de sesión exitoso", "success");
        setTimeout(() => {
            //console.log(data);
            saveAuthData(data.token, data.username, data.role);
            redirectByRole(data.role);
        }, 2000);

    } catch (error) {
        showMessage(error.message, "error");
    }

}

const login = async (username, password) => {

    try {
        const response = await fetch(BASE_URL + "/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json" 
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });
        console.log(response);

        if (!response.ok) {
            throw new Error("Credenciales invalidas");
        }

        const data = await response.json();
        return data;

    } catch (error) {
        throw error;
    }


}

const showMessage = (message, type) => {
    const notificationDiv = document.getElementById("notificationDiv");
    const notificationMessage = document.getElementById("notificationMessage");
    notificationDiv.classList.remove("is-success");
    notificationDiv.classList.remove("is-danger");

    if (type === "success") {
        notificationDiv.classList.add("is-success");
    } else {
        notificationDiv.classList.add("is-danger");
    }

    notificationMessage.innerText = message;
    notificationDiv.classList.remove("is-hidden");
    setTimeout(() => {
        notificationDiv.classList.add("is-hidden");
    }, 3000);


}

const saveAuthData = (token, username, role) => {
    localStorage.setItem("token", token);
    localStorage.setItem("username", username);
    localStorage.setItem("role", role);
}

const redirectByRole = (role) => {
    if (role === "ADMIN") {
        window.location.href = "/admin.html";
    } else if (role === "USER") {
        window.location.href = "/user.html";
    }
}

const blockElement = (element) => {
    element.setAttribute("disabled", true);
}

const unblockElement = (element) => {
    element.removeAttribute("disabled");
}

const renderLoader = (section) => {
    section.innerHTML = `<progress class="progress is-small is-primary" max="100">15%</progress>`;
}
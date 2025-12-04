const BASE_URL = "http://localhost:7000";

document.addEventListener("DOMContentLoaded", (event) => {

    const logoutBtn = document.getElementById("logoutBtn");
    const seeUsersBtn = document.getElementById("seeUsersBtn");
    const seeBoardsBtn = document.getElementById("seeBoardsBtn");
    const createUserBtn = document.getElementById("createUserBtn");
    const createBoardsBtn = document.getElementById("createBoardsBtn");
    const assignBoardBtn = document.getElementById("asignBoardBtn");

    if (!isAdmin() || !tokenExists()) {
        logout();
    }

    setUserName();
    logoutBtn.addEventListener("click", () => {
        logout();
    });

    seeUsersBtn.addEventListener("click", async () => {
        blockElement(seeUsersBtn);
        await renderUsers();
        unblockElement(seeUsersBtn);
    });

    seeBoardsBtn.addEventListener("click", async () => {
        blockElement(seeBoardsBtn);
        await renderBoards();
        unblockElement(seeBoardsBtn);
    });

    createUserBtn.addEventListener("click", async () => {
        blockElement(createUserBtn);
        await renderNewUserForm();
        unblockElement(createUserBtn);
    });

    createBoardsBtn.addEventListener("click", async () => {
        blockElement(createBoardsBtn);
        await renderNewBoardForm();
        unblockElement(createBoardsBtn);
    });

    assignBoardBtn.addEventListener("click", async () => {
        blockElement(assignBoardBtn);
        await renderAssignUserToBoardForm();
        unblockElement(assignBoardBtn);
    });


});


const tokenExists = () => {
    if (localStorage.getItem("token") != null && localStorage.getItem("token") != "") {
        return true;
    }

    return false;
}

const isAdmin = () => {
    if (localStorage.getItem("role") != "ADMIN") {
        return false;
    }

    return true;
}

const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    localStorage.removeItem("role");

    window.location.href = "/index.html";
}

const setUserName = () => {
    const username = document.getElementById("username");
    username.innerText = `Hola, ${localStorage.getItem("username")}`;
}

const showMessage = (message, type) => {
    const notification = document.getElementById("notificationDiv");
    const notificationMessage = document.getElementById("notificationMessage");
    notificationMessage.innerText = "";
    notificationMessage.innerText = message;
    notification.classList.remove("is-danger", "is-success");
    if (type === "error") {
        notification.classList.add("is-danger");
    } else if (type === "success") {
        notification.classList.add("is-success");
    } else {
        return;
    }

    notification.classList.remove("is-hidden");

    setTimeout(() => {
        notification.classList.add("is-hidden");
        if (document.getElementById("submitBtn")) {
            unblockElement(document.getElementById("submitBtn"));
        }

    }, 3000);
}

const renderUsers = async () => {
    const section = document.getElementById("dynamicSection");
    section.innerText = "";
    renderLoader(section);

    let users;
    try {
        const data = await apiGet("/api/protected/admin/users");
        users = data["all users"];
    } catch (error) {
        showMessage(error, "error");
        setTimeout(() => {
            logout();
        }, 3000);
        return;
    }

    const container = document.createElement("div");
    container.classList.add("columns", "is-centered");

    const column = document.createElement("div");
    column.classList.add("column", "is-half");

    const table = document.createElement("table");
    table.classList.add("table", "is-striped", "is-hoverable", "is-bordered", "is-fullwidth");

    const thead = document.createElement("thead");
    const headerRow = document.createElement("tr");
    const th1 = document.createElement("th");
    th1.textContent = "ID";
    const th2 = document.createElement("th");
    th2.textContent = "USERNAME";
    const th3 = document.createElement("th");
    th3.textContent = "ROLE";

    headerRow.appendChild(th1);
    headerRow.appendChild(th2);
    headerRow.appendChild(th3);
    thead.appendChild(headerRow);

    const tableBody = document.createElement("tbody");
    users.forEach((user) => {
        const row = document.createElement("tr");
        const dataId = document.createElement("td");
        const dataUsername = document.createElement("td");
        const dataRole = document.createElement("td");

        dataId.innerText = user.id;
        dataUsername.innerText = user.username;
        dataRole.innerText = user.role;

        row.appendChild(dataId);
        row.appendChild(dataUsername);
        row.appendChild(dataRole);

        tableBody.appendChild(row);
    });

    table.appendChild(thead);
    table.appendChild(tableBody);

    column.appendChild(table);
    container.appendChild(column);

    section.innerHTML = "";
    section.appendChild(container);
}


const renderBoards = async () => {
    const section = document.getElementById("dynamicSection");
    section.innerText = "";
    renderLoader(section);
    let boards;
    try {
        const data = await apiGet("/api/protected/admin/boards");
        boards = data["all boards"];
    } catch (error) {
        showMessage(error, "error");
        setTimeout(() => {
            logout();
        }, 3000);
        return;
    }

    const container = document.createElement("div");
    container.classList.add("columns", "is-centered");

    const column = document.createElement("div");
    column.classList.add("column", "is-half");

    const table = document.createElement("table");
    table.classList.add("table", "is-striped", "is-hoverable", "is-bordered", "is-fullwidth");

    const thead = document.createElement("thead");
    const headerRow = document.createElement("tr");
    const th1 = document.createElement("th");
    th1.textContent = "ID";
    const th2 = document.createElement("th");
    th2.textContent = "BOARD NAME";
    const th3 = document.createElement("th");
    th3.textContent = "SEE";

    headerRow.appendChild(th1);
    headerRow.appendChild(th2);
    headerRow.appendChild(th3);
    thead.appendChild(headerRow);

    const tableBody = document.createElement("tbody");
    boards.forEach((board) => {
        const row = document.createElement("tr");
        const dataId = document.createElement("td");
        const dataBoard = document.createElement("td");
        const dataBtn = document.createElement("td");

        const btn = document.createElement("button");
        btn.classList.add("button", "is-link", "is-small");
        btn.innerText = "see board";

        dataBtn.appendChild(btn);

        dataId.innerText = board.id;
        dataBoard.innerText = board.name;

        row.appendChild(dataId);
        row.appendChild(dataBoard);
        row.appendChild(dataBtn);

        tableBody.appendChild(row);
    });

    table.appendChild(thead);
    table.appendChild(tableBody);

    column.appendChild(table);
    container.appendChild(column);

    section.innerText = "";
    section.appendChild(container);
}

const renderNewUserForm = async () => {
    const section = document.getElementById("dynamicSection");
    section.innerText = "";
    renderLoader(section);
    const mediumSection = document.createElement("section");

    mediumSection.innerHTML = `
    <div class="columns is-centered">
        <div class="column is-half">
                <div class="field">
                    <label class="label">Usuario</label>
                    <div class="control">
                        <input class="input" type="text" placeholder="NOmbre Nuevo Usuario" id="user">
                    </div>
                </div>

                <div class="field">
                    <label class="label">Contraseña</label>
                    <div class="control">
                        <input class="input" type="password" placeholder="Contraseña" id="password">
                    </div>
                </div>

                <div class="field is-grouped">
                    <div class="control">
                        <button class="button is-primary" id="submitBtn">Crear Usuario</button>
                    </div>
                </div>
        </div>
    </div>
    `

    section.innerText = "";
    section.appendChild(mediumSection);

    assignEvent("submitBtn", "click", () => {
        blockElement(document.getElementById("submitBtn"));
        handleSubmitNewUser();
    });
}


const renderNewBoardForm = async () => {
    const section = document.getElementById("dynamicSection");
    section.innerText = "";
    renderLoader(section);
    const mediumSection = document.createElement("section");

    mediumSection.innerHTML = `
    <div class="columns is-centered">
        <div class="column is-half">
            <div class="field">
                <label class="label">Nuevo Tablero</label>
                <div class="control">
                    <input class="input" type="text" placeholder="Nombre Nuevo Tablero" id="boardName">
                </div>
            </div>

            <div class="field is-grouped">
                <div class="control">
                    <button class="button is-primary" id="submitBtn">Crear Tablero</button>
                </div>
            </div>
        </div>
    </div>
    
    `

    section.innerText = "";
    section.appendChild(mediumSection);

    assignEvent("submitBtn", "click", () => {
        blockElement(document.getElementById("submitBtn"));
        handleSubmitNewBoard();
    });


}

const renderAssignUserToBoardForm = async () => {
    const section = document.getElementById("dynamicSection");
    section.innerText = "";
    renderLoader(section);
    const newSection = document.createElement("section");

    newSection.innerHTML = `
        
        <div class="columns is-centered">
            <div class="column is-half">
                <div class="m-5 is-fullwidth">
                    <div>
                        <p>Elegí un tablero</p>
                    </div>
                    <div class="select is-info">
                        <select id="boardsSelect">
                            
                        </select>
                    </div>
                </div>
                <div class="m-5 is-fullwidth">
                    <div>
                        <p>Asigna un usuario</p>
                    </div>
                    <div class="select is-info">
                        <select id="usersSelect">
                            
                        </select>
                    </div>
                </div>
                <div class="field m-5">
                    <div class="control">
                        <button class="button is-primary" id="submitBtn">Asignar</button>
                    </div>
                </div>
             </div>
        </div>
    
    `

    section.innerText = "";
    section.appendChild(newSection);

    let boards = await getAllBoards();


    const boardsSelect = document.getElementById("boardsSelect");
    if (boards.length > 0) {
        boards.forEach(board => {
            const option = document.createElement("option");
            option.value = board.id;
            option.innerText = board.name;
            boardsSelect.appendChild(option);
        })

        const firstBoardId = boards[0].id;
        const initialUsers = await getAvailableUsers(firstBoardId);
        populateUsersSelect(initialUsers);;
    } else {
        showMessage("No hay tableros cargados", "error");
    }



    assignEvent("boardsSelect", "change", async () => {
        const boardId = document.getElementById("boardsSelect").value;
        const users = await getAvailableUsers(boardId);
        populateUsersSelect(users);
    });

    assignEvent("submitBtn", "click", async () => {
        blockElement(document.getElementById("submitBtn"));
        await handleSubmitAssignation();
    });
}

const apiGet = async (url) => {
    try {
        const token = localStorage.getItem("token");

        const response = await fetch(BASE_URL + url, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },

        });
        if (!response.ok) {
            console.log(response.status);
            throw new Error("Ocurrió un error");
        }

        const data = await response.json();
        return data;
    } catch (error) {
        throw error;
    }
}

const apiPost = async (url, data) => {

    try {
        const token = localStorage.getItem("token");
        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(data)

        });

        if (!response.ok) {
            if (response.status == 409) {
                throw new Error("El usuario ya existe ne la base de datos");
            } else if (response.status == 500 || response.status === 403 || response.status === 401) {
                throw new Error("Ocurrió un error");
            }

        }

        const postResult = await response.json();
        return postResult;

    } catch (error) {
        throw error;
    }

}

const assignEvent = (id, eventName, callback) => {
    const element = document.getElementById(id);
    if (element) {
        element.addEventListener(eventName, (event) => {
            callback(event);
        });
    }
}

const handleSubmitNewUser = async () => {
    const username = document.getElementById("user").value.trim();
    const password = document.getElementById("password").value.trim();

    if (username.length < 1 || password.length < 1) {
        showMessage("Usuario o contraseña vacios", "error");
        return;
    }

    const data = {
        username: username.trim(),
        password: password.trim(),
        role: "USER"
    }
    try {
        const response = await apiPost(BASE_URL + "/api/protected/admin/users", data);
        showMessage("Usuario: " + response.username + "\n" + "Creado con rol: " + response.role, "success");

        setTimeout(() => {
            seeUsersBtn.click();
        }, 2000);
    } catch (error) {
        showMessage(error, "error");
        setTimeout(() => {
            if (error.message == "Ocurrió un error") {
                logout();
            }
        }, 3000);
    }
}

const handleSubmitNewBoard = async () => {
    const name = document.getElementById("boardName").value.trim();
    console.log("NOMBREEE " + name);


    if (name.length < 1) {
        showMessage("El nombre del tablero no puede estar vacío", "error");
        return;
    }

    const data = { name: name };

    try {
        const response = await apiPost(BASE_URL + "/api/protected/admin/boards", data);
        showMessage("Tablero: " + response.name + " con id: " + response.id, "success");

        setTimeout(() => {
            seeBoardsBtn.click();
        }, 2000);
    } catch (error) {
        showMessage(error, "error");
        setTimeout(() => {
            if (error.message == "Ocurrió un error") {
                logout();
            }
        }, 3000);
    }
}

const getAllBoards = async () => {
    try {
        const data = await apiGet("/api/protected/admin/boards");
        boards = data["all boards"];
        return boards;
    } catch (error) {
        showMessage(error, "error");
        setTimeout(() => {
            logout();
        }, 3000);
        return;
    }
}

const getAllUsers = async () => {
    let users;
    try {
        const data = await apiGet("/api/protected/admin/users");
        users = data["all users"];
        return users;
    } catch (error) {
        showMessage(error, "error");
        setTimeout(() => {
            logout();
        }, 3000);
        return;
    }
}

const getAvailableUsers = async (boardId) => {
    let users;
    try {
        const data = await apiGet(`/api/protected/admin/boards/${boardId}/available-users`);
        users = data;
        return users;
    } catch (error) {
        showMessage(error, "error");
        setTimeout(() => {
            logout();
        }, 3000);
        return;
    }
}

const populateUsersSelect = (users) => {
    const usersSelect = document.getElementById("usersSelect");
    usersSelect.innerHTML = "";
    if (users.length > 0) {
        users.forEach(user => {
            const option = document.createElement("option");
            option.value = user.id;
            option.innerText = user.username;
            usersSelect.appendChild(option);
        });
    } else {
        console.log("length ", usersSelect.length);

        showMessage("No hay usuarios disponibles para este tablero", "error");
    }
}

const blockElement = (element) => {
    element.setAttribute("disabled", true);
}

const unblockElement = (element) => {
    element.removeAttribute("disabled");
}

const renderLoader = (section) => {
    section.innerHTML = `
    <div class="columns">
        <div class=" column is-one-quarter">
            <p>CARGANDO</p>
            <progress class="progress is-small is-primary" max="50">15%</progress>
        </div>
    </div>
    `;
}

const handleSubmitAssignation = async () => {

    const boardsSelectValue = document.getElementById("boardsSelect").value;
    const usersSelectValue = document.getElementById("usersSelect").value;

    if (boardsSelectValue == "" || usersSelectValue == "") {
        showMessage("Seleccione tablero y usuario", "error");
        return;
    }

    const data = { userId: usersSelectValue };

    try {
        const response = await apiPost(`${BASE_URL}/api/protected/admin/boards/${boardsSelectValue}/users`, data);

        showMessage("Usuario asignado al tablero", "success");
        setTimeout(() => {
            seeBoardsBtn.click();
        }, 2000);


    } catch (error) {
        console.log(error);
        showMessage(error, "error");
    }

}




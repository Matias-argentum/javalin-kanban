const BASE_URL = "http://localhost:7000";

document.addEventListener("DOMContentLoaded", () => {
    const logoutBtn = document.getElementById("logoutBtn");
    const seeBoardsBtn = document.getElementById("seeBoardsBtn");

    seeBoardsBtn.addEventListener("click", async () => {
        blockElement(seeBoardsBtn);
        await renderMyBoards();
        unblockElement(seeBoardsBtn);
    });

    if (!isUser() || !tokenExists()) {
        logout();
    }

    setUserName();
    logoutBtn.addEventListener("click", () => {
        logout();
    });

    const editTaslBtn = document.getElementById("submitEditedTask");

    editTaslBtn.addEventListener("click", () => {
        blockElement(editTaslBtn);
        handleUpdateTaskDescription();
        closeModal(document.getElementById("modal-edit"));
        unblockElement(editTaslBtn);
    });

});

const isUser = () => {
    return localStorage.getItem("role") === "USER";
}

const tokenExists = () => {
    return localStorage.getItem("token");
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

const apiGet = async (url) => {
    try {
        const token = localStorage.getItem("token");
        const response = await fetch(BASE_URL + url, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            if (response.status == 404) {
                throw new Error("Not found");
            } else if (response.status == 400) {
                throw new Error("Bad request");
            } else {
                throw new Error("Ocurrió un error");
            }
        }
        const data = await response.json();
        return data;
    } catch (error) {
        console.log("Error en API GET: " + error);
        throw error;
    }

}

const apiPost = async (url, data) => {
    try {
        const token = localStorage.getItem("token");
        const response = await fetch(BASE_URL + url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(data)
        });
        if (!response.ok) {
            if (response.status == 404) {
                throw new Error("Not found");
            } else if (response.status == 400) {
                throw new Error("Bad request");
            } else {
                throw new Error("Ocurrió un error");
            }
        }
        const postResult = await response.json();
        return postResult;

    } catch (error) {
        console.log("Error en API POST: " + error);
        throw error;
    }
}

const apiPut = async (url, data) => {
    try {
        const token = localStorage.getItem("token");
        const response = await fetch(BASE_URL + url, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(data)
        });
        if (!response.ok) {
            if (response.status == 404) {
                throw new Error("Not found");
            } else if (response.status == 400) {
                throw new Error("Bad request");
            } else {
                throw new Error("Ocurrió un error");
            }
        }
        const putResult = await response.json();
        return putResult;

    } catch (error) {
        console.log("Error en API PUT: " + error);
        throw error;
    }
}

const apiDelete = async (url) => {
    try {
        const token = localStorage.getItem("token");
        const response = await fetch(BASE_URL + url, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            if (response.status == 404) {
                throw new Error("Not found");
            } else if (response.status == 400) {
                throw new Error("Bad request");
            } else {
                throw new Error("Ocurrió un error");
            }
        }
        const data = await response.json();
        return data;
    } catch (error) {
        console.log("Error en API DELETE: " + error);
        throw error;
    }
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

const blockElement = (element) => {
    element.setAttribute("disabled", true);
}

const unblockElement = (element) => {
    element.removeAttribute("disabled");
}

const renderMyBoards = async () => {
    const section = document.getElementById("dynamicSection");
    section.innerText = "";
    renderLoader(section);
    let myBoards;
    try {
        myBoards = await apiGet("/api/protected/boards/my-boards");

        console.log(myBoards);

    } catch (error) {
        showMessage(error, "error");
        if (error == "Error: Ocurrió un error") {
            setTimeout(() => {
                logout();
            }, 3000);
        }
        return;
    }

    if (!myBoards || myBoards.length < 1) {
        section.innerHTML = "<p>No tenés tableros asignados</p>";
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
    th1.textContent = "NOMBRE";

    const th2 = document.createElement("th");
    th2.textContent = "ACCION";

    headerRow.appendChild(th1);
    headerRow.appendChild(th2);
    thead.appendChild(headerRow);

    const tbody = document.createElement("tbody");
    myBoards.forEach(board => {
        const row = document.createElement("tr");

        const tdName = document.createElement("td");
        tdName.textContent = board.name;

        const tdAction = document.createElement("td");
        const btnVer = document.createElement("button");
        btnVer.classList.add("button", "is-link", "is-small");
        btnVer.textContent = "Ver";

        btnVer.addEventListener("click", async () => {
            blockElement(btnVer);
            await renderKanban(board.id, board.name);
            unblockElement(btnVer);
        });

        tdAction.appendChild(btnVer);
        row.appendChild(tdName);
        row.appendChild(tdAction);
        tbody.appendChild(row);
    });

    table.appendChild(thead);
    table.appendChild(tbody);
    column.appendChild(table);
    container.appendChild(column);

    section.innerHTML = "";
    section.appendChild(container);
}

const renderKanban = async (boardId, boardName) => {
    const section = document.getElementById("dynamicSection");
    section.innerText = "";
    renderLoader(section);
    let tasks;
    try {
        tasks = await apiGet(`/api/protected/boards/${boardId}/tasks`);
    } catch (error) {
        if (error.message == "Ocurrió un error") {
            showMessage(error, "error");
            setTimeout(() => {
                logout()
            }, 2000);
            return;
        }
        showMessage(error, "error");
    }
    section.innerText = "";
    const kanban = createKanban(tasks, boardId);
    section.classList.add("is-fullheight");
    const board = document.createElement("h1");
    board.classList.add("title", "has-text-white", "m-2");
    board.innerText = boardName;
    section.appendChild(board)
    section.appendChild(kanban);

    addModalEditEvent(boardId);
}

const createKanban = (tasks, boardId) => {
    const container = document.createElement("div");
    container.classList.add("columns", "is-centered");
    container.style.minHeight = "70vh";

    const firstColumn = document.createElement("div");
    firstColumn.classList.add("column", "is-one-third", "has-background-dark", "m-2", "has-text-centered", "kanban-column-todo", "box");
    firstColumn.style.minHeight = "70vh";
    //firstColumn.style.borderRadius = "5px";
    firstColumn.style.border = "2px solid #66d1ff";
    firstColumn.innerHTML = '<h1 class="has-text-info subtitle">TO DO</h1>';
    addDragoverEvent(firstColumn);
    addDragleaveEvent(firstColumn);
    addDropEvent(firstColumn, boardId);

    const secondColumn = document.createElement("div");

    secondColumn.classList.add("column", "is-one-third", "has-background-dark", "m-2", "has-text-centered", "kanban-column-inprogress", "box");
    secondColumn.style.minHeight = "70vh";
    secondColumn.style.border = "2px solid #ffb70f";
    secondColumn.innerHTML = '<h1 class="has-text-warning subtitle">IN PROGRESS</h1>';
    addDragoverEvent(secondColumn);
    addDragleaveEvent(secondColumn);
    addDropEvent(secondColumn, boardId);

    const thirdColumn = document.createElement("div");
    thirdColumn.style.minHeight = "70vh";
    thirdColumn.classList.add("column", "is-one-third", "has-background-dark", "m-2", "has-text-centered", "kanban-column-done", "box");
    thirdColumn.style.border = "2px solid #48c78e";
    thirdColumn.innerHTML = '<h1 class="has-text-success subtitle">DONE</h1>';
    addDragoverEvent(thirdColumn);
    addDragleaveEvent(thirdColumn);
    addDropEvent(thirdColumn, boardId);

    firstColumn.appendChild(createNewTaskForm(boardId));

    if (tasks.length < 1) {
        container.appendChild(firstColumn);
        container.appendChild(secondColumn);
        container.appendChild(thirdColumn);
        return container;
    }
    const toDoTasks = filterTasks(tasks, "TO_DO");
    const inProgressTasks = filterTasks(tasks, "IN_PROGRESS");
    const doneTasks = filterTasks(tasks, "DONE");

    toDoTasks.forEach((task) => {
        const taskCard = createTaskCard(task, boardId);
        firstColumn.appendChild(taskCard);
    });

    inProgressTasks.forEach((task) => {
        const taskCard = createTaskCard(task, boardId);
        secondColumn.appendChild(taskCard);
    });

    doneTasks.forEach((task) => {
        const taskCard = createTaskCard(task, boardId);
        thirdColumn.appendChild(taskCard);
    });

    container.appendChild(firstColumn);
    container.appendChild(secondColumn);
    container.appendChild(thirdColumn);
    return container;
}

const createNewTaskForm = (boardId) => {
    const card = document.createElement("div");
    card.classList.add("box", "has-background-dark");
    card.style.border = "1px solid #00d1b2";

    const outerFieldDiv = document.createElement("div");
    outerFieldDiv.classList.add("field");

    const innerControlDiv = document.createElement("div");
    innerControlDiv.classList.add("control");

    const input = document.createElement("input");
    input.classList.add("input", "is-primary", "is-rounded");
    input.type = "text";
    input.placeholder = "descripcion de la nueva tarea";
    input.id = "newTask"

    innerControlDiv.appendChild(input);
    outerFieldDiv.appendChild(innerControlDiv);
    card.appendChild(outerFieldDiv)

    const outerGroupedDiv = document.createElement("div");
    outerGroupedDiv.classList.add("has-text-right");

    const secondInnerControlDiv = document.createElement("div");
    secondInnerControlDiv.classList.add("control");

    const button = document.createElement("button");
    button.classList.add("button", "is-primary", "is-small", "is-outlined");
    button.id = "submitNewTask";
    button.innerText = "Crear Tarea";

    secondInnerControlDiv.appendChild(button);
    outerGroupedDiv.appendChild(secondInnerControlDiv);
    card.appendChild(outerGroupedDiv);



    button.addEventListener("click", async () => {
        blockElement(button);
        await handleCreateTask(boardId);
        unblockElement(button);
    });


    return card;

}

const createTaskCard = (task, boardId) => {
    const card = document.createElement("div");
    card.classList.add("box", "has-background-dark", "has-text-left", "box");
    card.dataset.taskId = task.id;
    card.dataset.taskState = task.state;


    applyTaskStyle(card, task.state);

    card.addEventListener("mouseenter", () => {
        card.style.transform = "scale(1.05)"
    })

    card.addEventListener("mouseleave", () => {
        card.style.transform = "scale(1.0)";
    })

    card.addEventListener("dragstart", (event) => {
        event.dataTransfer.setData("taskId", task.id);
    })


    card.setAttribute("draggable", true)

    const taskText = document.createElement("p");
    taskText.classList.add("task-description");
    taskText.innerText = task.description;
    card.appendChild(taskText);

    const editBtn = document.createElement("button");
    editBtn.classList.add("button", "is-small", "is-info", "ml-2", "is-outlined", "modal-edit-task");
    editBtn.setAttribute("data-target", "modal-edit");
    editBtn.innerText = "Editar";

    const deleteBtn = document.createElement("button");
    deleteBtn.classList.add("button", "is-small", "is-danger", "ml-2", "is-outlined");
    deleteBtn.innerText = "Eliminar";


    deleteBtn.addEventListener("click", async () => {
        blockElement(deleteBtn);
        if (confirm("Seguro que queres elimar la tarea???")) {
            await handleDeleteTask(boardId, task.id, card);
        } else {
            console.log("No se confirma");

        }
        unblockElement(deleteBtn);
    });

    const actions = document.createElement("div");
    actions.classList.add("mt-1", "has-text-right");

    actions.appendChild(editBtn);
    actions.appendChild(deleteBtn);

    card.appendChild(actions);

    return card;
}


const filterTasks = (tasks, state) => {
    return tasks.filter(task => task.state === state);
}

const handleDeleteTask = async (boardId, taskId, cardElement) => {
    console.log("Acá llmar a pi delete con " + taskId + cardElement);
    let result;

    try {
        result = await apiDelete(`/api/protected/boards/${boardId}/tasks/${taskId}`);

    } catch (error) {
        if (error.message == "Ocurrió un error") {
            showMessage(error, "error");
            setTimeout(() => {
                logout()
            }, 2000);
            return;
        }
        showMessage(error, "error");
    }

    cardElement.remove();
    showMessage("Borrado Exitosamente", "success");
    return;
}

const handleCreateTask = async (boardId) => {
    console.log("entre???");

    let response;
    let taskDescription = document.getElementById("newTask").value;

    if (taskDescription.length < 1) {
        showMessage("La tarea no puede estar vacía", "error");
        return;
    }
    const state = "TO_DO";
    let data = {
        description: taskDescription,
        state: state
    }
    try {
        response = await apiPost(`/api/protected/boards/${boardId}/tasks`, data);
        document.getElementById("newTask").value = "";
        console.log(response.task);


    } catch (error) {
        if (error.message == "Ocurrió un error") {
            showMessage(error, "error");
            setTimeout(() => {
                logout()
            }, 2000);
            return;
        }
        showMessage(error, "error");
        return;
    }

    showMessage("Tarea agregada", "success");

    const column = document.querySelector(".kanban-column-todo");

    column.appendChild(createTaskCard(response.task, boardId));

}

const applyTaskStyle = (card, state) => {
    if (state === "TO_DO") {
        card.style.border = "1px solid #66d1ff";
        card.style.color = "#66d1ff";
    } else if (state === "IN_PROGRESS") {
        card.style.border = "1px solid #ffb70f";
        card.style.color = "#ffb70f";
    } else if (state === "DONE") {
        card.style.border = "1px solid #48c78e";
        card.style.color = "#48c78e";
    }
};
const addDragoverEvent = (column) => {
    column.addEventListener("dragover", (event) => {
        event.preventDefault();
        event.currentTarget.style.opacity = "0.8";
    });
}

const addDragleaveEvent = (column) => {
    column.addEventListener("dragleave", (event) => {
        event.preventDefault();
        event.currentTarget.style.opacity = "1";
    });
}

const addDropEvent = (column, boardId) => {
    column.addEventListener("drop", (event) => {
        event.preventDefault();

        const taskId = event.dataTransfer.getData("taskId") //esta linea
        const card = document.querySelector(`[data-task-id="${taskId}"]`); //esta lina

        const oldState = card.dataset.taskState
        const oldColumn = card.parentElement

        const newColumn = event.currentTarget;
        let newState = getStateByColumn(newColumn);

        event.currentTarget.appendChild(card);
        card.dataset.taskState = newState;
        
        applyTaskStyle(card, newState);

        if (newState === oldState) {
            showMessage("La task se queda ne su columna", "success");
            return;
        }

        handleUpdateTaskState(taskId, newState, card, oldColumn, oldState, boardId);
    });
}

const getStateByColumn = (targetColumn) => {

    let newState;

    if (targetColumn.classList.contains("kanban-column-todo")) {
        newState = "TO_DO";
    } else if (targetColumn.classList.contains("kanban-column-inprogress")) {
        newState = "IN_PROGRESS";
    } else if (targetColumn.classList.contains("kanban-column-done")) {
        newState = "DONE";
    }

    return newState;
}

const handleUpdateTaskState = async (taskId, newState, card, oldColumn, oldState, boardId) => {
    try {
        const data = { state: newState };
        const response = await apiPut(`/api/protected/boards/${boardId}/tasks/${taskId}/state`, data);
        console.log(response);
        showMessage("Task actualizada en backend", "success");
    } catch (error) {
        oldColumn.appendChild(card);
        card.dataset.taskState = oldState;
        applyTaskStyle(card, oldState);
        showMessage(error, "error");
        if (error == "Error: Ocurrió un error") {
            setTimeout(() => {
                logout();
            }, 3000);
        }
        return;
    }
}

const handleUpdateTaskDescription = async () => {
    const taskId = document.getElementById("editingTask").value;
    const newState = document.getElementById("selectEditingState").value;
    const description = document.getElementById("editingDescription").value;
    const boardId = document.getElementById("editingTaskBoardId").value;
    if (description.length < 1) {
        showMessage(error, "La description no puede estar vacia");
    }

    try {
        const data = { description: description, state: newState };
        console.log(data);
        removeCardFromDomById(taskId);
        const response = await apiPut(`/api/protected/boards/${boardId}/tasks/${taskId}`, data);
        console.log(response.task);

        insertUpdatedTaskToDom(response.task, boardId);
        addModalEditEvent(boardId);
        showMessage("Task actualizada en backend", "success");

    } catch (error) {
        showMessage(error, "error");
        if (error == "Error: Ocurrió un error") {
            setTimeout(() => {
                logout();
            }, 3000);
        }
        return;
    }
}

const removeCardFromDomById = (taskId) => {
    const cardElement = document.querySelector(`[data-task-id="${taskId}"]`);
    if (cardElement) {
        cardElement.remove();
    }
}

const insertUpdatedTaskToDom = (task, boardId) => {
    console.log("task en insert to board after update:  " + task);
    
    let column;
    if (task.state == "TO_DO") {
        column = document.querySelector(".kanban-column-todo");
    } else if (task.state == "IN_PROGRESS") {
        column = document.querySelector(".kanban-column-inprogress");
    } else if (task.state == "DONE") {
        column = document.querySelector(".kanban-column-done");
    }
    const taskCard = createTaskCard(task, boardId);
    //applyTaskStyle(taskCard, task.state);

    column.appendChild(taskCard, boardId);
}

const addModalEditEvent = (boardId) => {
    (document.querySelectorAll('.modal-edit-task') || []).forEach(($trigger) => {

        const modal = $trigger.dataset.target;
        const $target = document.getElementById(modal);

        $trigger.addEventListener('click', () => {

            const taskCard = $trigger.closest(".box");
            console.log("taskCard");

            console.log(taskCard);

            const taskId = taskCard.dataset.taskId;
            const state = taskCard.dataset.taskState;
            const description = taskCard.querySelector(".task-description").textContent;

            console.log("id ", taskId);
            console.log("state ", state);
            console.log("description ", description);
            document.getElementById("editingTask").value = taskId;
            document.getElementById("editingDescription").value = description;
            document.getElementById("editingTaskBoardId").value = boardId;

            const mySelect = document.getElementById("selectEditingState");
            const optionToSelect = Array.from(mySelect.options).find(option => option.value === state);
            if (optionToSelect) {
                optionToSelect.selected = true;
            }
            openModal($target);
        });
    });
}

// Functions to open and close a modal
function openModal($el) {
    $el.classList.add('is-active');
}

function closeModal($el) {
    $el.classList.remove('is-active');
}

function closeAllModals() {
    (document.querySelectorAll('.modal') || []).forEach(($modal) => {
        closeModal($modal);
    });
}

(document.querySelectorAll('.modal-background, .modal-close, .modal-card-head .delete, .modal-card-foot .button') || []).forEach(($close) => {
    const $target = $close.closest('.modal');

    $close.addEventListener('click', () => {
        closeModal($target);
    });
});

// Add a keyboard event to close all modals
document.addEventListener('keydown', (event) => {
    if (event.key === "Escape") {
        closeAllModals();
    }
});
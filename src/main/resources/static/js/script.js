let tasks = [
    {id: 1, title: "Task 1", description: "Description 1", dueDate: "2025-08-20", done: false},
    {id: 2, title: "Task 2", description: "Description 2", dueDate: "2025-08-22", done: true}
];

let currentTaskId = null;

const overlay = document.getElementById("overlay");
const detailModal = document.getElementById("detailModal");
const addModal = document.getElementById("addModal");
const taskStatus = document.getElementById("taskStatus"); // dòng status

// Nút đóng modal
document.getElementById("closeDetail").onclick = closeAll;
document.getElementById("closeAdd").onclick = closeAll;
overlay.onclick = closeAll;

// Render tasks ra giao diện
function renderTasks() {
    const container = document.querySelector(".todo-container");
    const addBtn = container.querySelector(".add-btn");

    container.querySelectorAll(".todo-item").forEach(e => e.remove());

    tasks.forEach(task => {
        const div = document.createElement("div");
        div.classList.add("todo-item");
        div.setAttribute("data-id", task.id);

        div.innerHTML = `
            <input type="checkbox" ${task.done ? "checked" : ""}>
            <span>${task.title}</span>
        `;

        const checkbox = div.querySelector("input[type='checkbox']");
        const text = div.querySelector("span");

        // Set trạng thái ban đầu
        if (task.done) {
            div.style.backgroundColor = "#c8f7c5";
            text.style.textDecoration = "line-through";
        }

        // Checkbox: toggle done, không mở modal
        checkbox.addEventListener("click", (e) => {
            e.stopPropagation();
            task.done = checkbox.checked;

            if (checkbox.checked) {
                div.style.backgroundColor = "#c8f7c5";
                text.style.textDecoration = "line-through";
            } else {
                div.style.backgroundColor = "";
                text.style.textDecoration = "none";
            }
        });

        // Click vào div ngoài checkbox -> mở detail modal
        div.addEventListener("click", () => {
            openDetail(task);
        });

        container.insertBefore(div, addBtn);
    });
}

// Mở detail modal
function openDetail(task) {
    currentTaskId = task.id;
    document.getElementById("detailTitle").value = task.title;
    document.getElementById("detailDesc").value = task.description;
    document.getElementById("detailDate").value = task.dueDate;

    // Update STATUS
    if (task.done) {
        taskStatus.innerHTML = `STATUS: <span style="color: green; font-weight: bold;">DONE</span>`;
    } else {
        taskStatus.innerHTML = `STATUS: <span style="color: goldenrod; font-weight: bold;">IN PROGRESS</span>`;
    }

    openModal(detailModal);
}

// Save task (Update)
document.getElementById("saveDetail").addEventListener("click", () => {
    const task = tasks.find(t => t.id === currentTaskId);
    if (task) {
        task.title = document.getElementById("detailTitle").value;
        task.description = document.getElementById("detailDesc").value;
        task.dueDate = document.getElementById("detailDate").value;
    }
    renderTasks();
    closeAll();
});

// Delete task
document.getElementById("deleteDetail").addEventListener("click", () => {
    tasks = tasks.filter(t => t.id !== currentTaskId);
    renderTasks();
    closeAll();
});

// Add task
document.getElementById("addTask").addEventListener("click", () => {
    const newTask = {
        id: Date.now(),
        title: document.getElementById("addTitle").value,
        description: document.getElementById("addDesc").value,
        dueDate: document.getElementById("addDate").value,
        done: false
    };
    tasks.push(newTask);
    renderTasks();
    closeAll();
});

// Modal helper
function openModal(modal) {
    overlay.style.display = "block";
    modal.style.display = "block";
}
function closeAll() {
    overlay.style.display = "none";
    detailModal.style.display = "none";
    addModal.style.display = "none";
}

// Nút Add new
document.querySelector(".add-btn").addEventListener("click", () => {
    document.getElementById("addTitle").value = "";
    document.getElementById("addDesc").value = "";
    document.getElementById("addDate").value = "";
    openModal(addModal);
});

// Render lần đầu
renderTasks();

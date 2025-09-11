document.addEventListener('DOMContentLoaded', function() {
    const overlay = document.getElementById('overlay');
    const addModal = document.getElementById('addModal');
    const detailModal = document.getElementById('detailModal');
    const addBtn = document.querySelector('.add-btn');
    const closeAddBtn = document.getElementById('closeAdd');
    const closeDetailBtn = document.getElementById('closeDetail');
    const todoItems = document.querySelectorAll('.todo-item');

    // Mở modal add task
    addBtn.addEventListener('click', function() {
        addModal.style.display = 'block';
        overlay.style.display = 'block';
    });

    // Đóng modal add
    closeAddBtn.addEventListener('click', function() {
        addModal.style.display = 'none';
        overlay.style.display = 'none';
    });

    // Đóng modal detail
    closeDetailBtn.addEventListener('click', function() {
        detailModal.style.display = 'none';
        overlay.style.display = 'none';
    });

    // Đóng modal khi click overlay
    overlay.addEventListener('click', function() {
        addModal.style.display = 'none';
        detailModal.style.display = 'none';
        overlay.style.display = 'none';
    });

    // Mở modal detail khi click vào task item
    todoItems.forEach(function(item) {
        item.addEventListener('click', function(e) {
            if (e.target.type !== 'checkbox') {
                const taskId = this.dataset.id;
                const title = this.dataset.title;
                const desc = this.dataset.desc;
                const date = this.dataset.date;

                document.getElementById('taskId').value = taskId;
                document.getElementById('detailTitle').value = title;
                document.getElementById('detailDesc').value = desc || '';
                document.getElementById('detailDate').value = date || '';

                // Hiển thị trạng thái task
                const status = this.querySelector('input[type="checkbox"]').checked ? 'Completed' : 'Pending';
                document.getElementById('taskStatus').textContent = `Status: ${status}`;

                detailModal.style.display = 'block';
                overlay.style.display = 'block';
            }
        });
    });

    // Xử lý save task detail
    document.getElementById('saveDetail').addEventListener('click', function() {
        const taskId = document.getElementById('taskId').value;
        const title = document.getElementById('detailTitle').value;
        const desc = document.getElementById('detailDesc').value;
        const date = document.getElementById('detailDate').value;

        // Tạo form để submit
        const form = document.createElement('form');
        form.method = 'post';
        form.action = '/update-task/' + taskId;

        // Tạo các input hidden
        const titleInput = document.createElement('input');
        titleInput.type = 'hidden';
        titleInput.name = 'title';
        titleInput.value = title;

        const descInput = document.createElement('input');
        descInput.type = 'hidden';
        descInput.name = 'description';
        descInput.value = desc;

        const dateInput = document.createElement('input');
        dateInput.type = 'hidden';
        dateInput.name = 'dueDate';
        dateInput.value = date;

        form.appendChild(titleInput);
        form.appendChild(descInput);
        form.appendChild(dateInput);

        document.body.appendChild(form);
        form.submit();
    });

    // Xử lý delete task
    document.getElementById('deleteDetail').addEventListener('click', function() {
        const taskId = document.getElementById('taskId').value;

            // Tạo form để submit delete
            const form = document.createElement('form');
            form.method = 'post';
            form.action = '/delete-task/' + taskId;

            document.body.appendChild(form);
            form.submit();

    });
});

function toggleStatus(checkbox) {
    const taskId = checkbox.getAttribute("data-id");
    const newStatus = checkbox.checked ? "FINISHED" : "CURRENT";

    fetch(`/tasks/${taskId}/status?status=${newStatus}`, {
        method: 'POST'
    })
        .then(response => {
            if (response.ok) {
                console.log(`Task ${taskId} updated to ${newStatus}`);
            } else {
                console.error('Failed to update status');
            }
        })
        .catch(error => console.error(error));
}
document.addEventListener('DOMContentLoaded', function() {
    const checkboxes = document.querySelectorAll('.todo-item input[type="checkbox"]');

    checkboxes.forEach(function(checkbox) {
        checkbox.addEventListener('change', function() {
            const todoItem = this.closest('.todo-item');

            if (this.checked) {
                // Đổi sang xanh
                todoItem.style.backgroundColor = "#e0f7e9";
                todoItem.style.textDecoration = "line-through";
            } else {
                // Trả về mặc định
                todoItem.style.backgroundColor = "";
                todoItem.style.textDecoration = "";
            }
        });
    });
});
document.addEventListener("DOMContentLoaded", function() {
    if (window.openAddModal) {
        document.getElementById("addModal").style.display = "block";
    }
});


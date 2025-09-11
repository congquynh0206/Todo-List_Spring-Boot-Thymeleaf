document.addEventListener("DOMContentLoaded", function () {
    fetch('/api/chart')
        .then(response => response.json())
        .then(response => {
            // ✅ Truy cập đúng dữ liệu trong response
            const labels = response.allTask.labels;
            const values = response.allTask.values;
            const completionRate = response.completionRate;

            // Task chart (Pie)
            const taskCtx = document.getElementById('taskChart').getContext('2d');
            new Chart(taskCtx, {
                type: 'pie',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Task Statistics',
                        data: values,
                        backgroundColor: ['#36A2EB', '#4CAF50', '#ef7245', '#EC2128FF']
                    }]
                },
                options: {
                    responsive: false,
                    plugins: {
                        legend: { position: 'top' },
                        title: { display: true, text: 'Task Overview' }
                    }
                }
            });

            // Completion Rate (Doughnut)
            const rateCtx = document.getElementById('rateChart').getContext('2d');
            new Chart(rateCtx, {
                type: 'doughnut',
                data: {
                    labels: ['Finished', 'Not Finish'],
                    datasets: [{
                        data: [completionRate, 100 - completionRate],
                        backgroundColor: ['#4CAF50', '#e5e5e5'],
                        borderWidth: 0
                    }]
                },
                options: {
                    responsive: false,
                    cutout: '70%',
                    plugins: {
                        legend: { display: false },
                        title: { display: true, text: 'Completion Rate' },
                        tooltip: {
                            callbacks: {
                                label: function (context) {
                                    return context.label + ": " + context.raw.toFixed(1) + "%";
                                }
                            }
                        }
                    }
                }
            });
        })
        .catch(error => console.error("Error loading chart data:", error));
});

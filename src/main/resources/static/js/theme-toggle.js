const toggleBtn = document.getElementById('toggle-theme');

const themeLinks = [
    document.getElementById('theme-home'),
    document.getElementById('theme-admin'),
    document.getElementById('theme-personal'),
    document.getElementById('theme-topbar')
];

const savedTheme = localStorage.getItem('themeMode'); // normal | alt
if (savedTheme === "alt") {
    themeLinks.forEach(link => {
        if(link) {
            link.setAttribute('href', link.getAttribute('href').replace(/\.css$/, "2.css"));
        }
    });
}

if (toggleBtn) {
    toggleBtn.addEventListener('click', () => {
        let isAlt = themeLinks.find(l => l && l.getAttribute('href').endsWith("2.css"));
        themeLinks.forEach(link => {
            if(link) {
                if (isAlt) {
                    link.setAttribute('href', link.getAttribute('href').replace(/2\.css$/, ".css"));
                    localStorage.setItem('themeMode', 'normal');
                } else {
                    link.setAttribute('href', link.getAttribute('href').replace(/\.css$/, "2.css"));
                    localStorage.setItem('themeMode', 'alt');
                }
            }
        });
    });
}

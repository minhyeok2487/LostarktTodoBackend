const body = document.querySelector("body"),
    headerBtn = document.querySelector(".image-text"),
    sidebar = body.querySelector(".sidebar"),
    toggle = body.querySelector(".toggle"),
    searchBtn = body.querySelector(".search-box"),
    modeSwtich = body.querySelector(".toggle-switch"),
    modeText = body.querySelector(".mode-text");

headerBtn.addEventListener("click", () => {
    location.href = "/";
})

toggle.addEventListener("click", () => {
    sidebar.classList.toggle("close");
});

searchBtn.addEventListener("click", () => {
    sidebar.classList.remove("close");
});


modeSwtich.addEventListener("click", () => {
    body.classList.toggle("dark");
    if (body.classList.contains("dark")) {
        modeText.innerText = "Light Mode";
    } else {
        modeText.innerText = "Dark Mode";
    }
});
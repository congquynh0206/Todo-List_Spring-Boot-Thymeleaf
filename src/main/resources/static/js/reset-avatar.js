function resetAvatar (){
    const form = document.createElement("form");
    form.action = "/reset-avatar";
    form.method = "post";
    document.body.appendChild(form);
    form.submit();
}
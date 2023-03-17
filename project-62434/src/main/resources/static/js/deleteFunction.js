function deleteUser(id) {
    if (confirm("Are you sure you want to delete the user with ID " + id + "?")) {
        var xhr = new XMLHttpRequest();
        xhr.open("DELETE", "/api/users/" + id, true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                location.reload();
            }
        };
        xhr.send();
    }
}
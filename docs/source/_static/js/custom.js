document.addEventListener("DOMContentLoaded", function () {
    var spans = document.querySelectorAll("span");

    spans.forEach(function(span) {
        if (span.textContent.endsWith("（规划）")) {
            span.style.textDecoration = "line-through";
        }
    });
});

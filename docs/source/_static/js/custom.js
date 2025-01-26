function addDel(){
 var spans = document.querySelectorAll("span");

    spans.forEach(function(span) {
        if (span.textContent.indexOf("（规划）")>0) {
            span.style.textDecoration = "line-through";
        }
    });
}

if (document.readyState === "loading"){
document.addEventListener("DOMContentLoaded", function () {
   addDel();
});
}else if (document.readyState === "complete"){
 addDel();
}
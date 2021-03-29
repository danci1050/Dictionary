const checkbox = document.getElementById('toogleA');
window.onload = function () {
    
    
checkbox.addEventListener('change', (event) => {
    console.log("changed");
    if(event.currentTarget.checked){
        javaSettingsIntegration.toggleAddNewWord(true);
    }else{
        javaSettingsIntegration.toggleAddNewWord(false);
    }
})
}

/* TODO: get this to work */
function setSettings(){
    if(javaSettingsIntegration.getAddNewWord){
        checkbox.checked=true;
    }
}
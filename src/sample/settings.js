const checkbox = document.getElementById('toogleA');
window.onload = function () {
   
checkbox.addEventListener('change', (event) => {
    
    if(event.currentTarget.checked){
        javaSettingsIntegration.toggleAddNewWord(true);
    }else{
        javaSettingsIntegration.toggleAddNewWord(false);
    }
})
}

function setSettings(){
    if(String(javaSettingsIntegration.getAddAWord())=="true"){
        console.log(javaSettingsIntegration.getAddAWord());
        checkbox.checked=true;
    }
}

function showDropdown(element) {
  var elems = document.getElementsByClassName("show");
  var rect = element.getBoundingClientRect();
  var maxwidth = window.innerWidth * 0.2;
  var childs = document.getElementById(element.id + "d").childNodes;
  var longestChild = 0;
  [].forEach.call(elems, function (el) {
    el.classList.remove("show");
  });

  for (var i = 0; i < childs.length; i++) {
    font = "16px times new roman";
    canvas = document.createElement("canvas");
    context = canvas.getContext("2d");
    context.font = font;
    width = context.measureText(childs[i].innerHTML).width;
    if (width > longestChild) {
      longestChild = width;
    }
  }
  document.getElementById(element.id + "d").style.width = Math.min(maxwidth, longestChild) + 50 + "px";
  document.getElementById(element.id + "d").classList.toggle("show");
}


window.onclick = function (event) {
  if (!event.target.matches('.dropbtn')) {
    var dropdowns = document.getElementsByClassName("dropdown-content");
    var i;
    for (i = 0; i < dropdowns.length; i++) {
      var openDropdown = dropdowns[i];
      if (openDropdown.classList.contains('show')) {
        openDropdown.classList.remove('show');
      }
    }
  }
}



function addTranslation(iteration, translation, alternativeTranslations) {
  var parent = document.createElement("div");
  var span = document.createElement("span");
  var div = document.createElement("div");
  translationField = document.getElementById("outputField");

  span.innerHTML = translation;

  span.classList.add("dropbtn");
  div.classList.add("dropdown-content");
  parent.classList.add("inlineDiv");
  span.id = iteration;
  div.id = iteration + "d";
  for (i = 0; i < alternativeTranslations.length; i++) {
    var alttrans = document.createElement("div")
    alttrans.classList.add("inlineblockDiv");
    alttrans.innerHTML = alternativeTranslations[i];
    alttrans.id = iteration + "d" + i;
    if ((i + 1) == alternativeTranslations.length) {
      alttrans.style.border = "none";

    }
    div.appendChild(alttrans);

    if(document.getElementById("input-textbox").value!=""){
      //TODO: change background somehow the output field should be adjusted
    }
  }

  parent.appendChild(span);
  parent.appendChild(div);
  translationField.appendChild(parent);

  span.addEventListener("click", function () { showDropdown(this); });
}

function addUntranslatedWord(word) {
  var parent = document.createElement("div");
  var span = document.createElement("span");
  var div = document.createElement("div");

  translationField = document.getElementById("outputField");

  span.innerHTML = translation;

  span.classList.add("dropbtn");
  div.classList.add("dropdown-content");
  parent.classList.add("inlineDiv");
  span.id = iteration;
  div.id = iteration + "d";

  parent.appendChild(span);
  parent.appendChild(div);
  translationField.appendChild(parent);

  span.addEventListener("click", function () { showDropdown(this); });
}


var textarea = document.getElementById("input-textbox");
var inputcol = document.getElementById("input-col");
function changeHeight() {

  textarea.style.height = "";
  textarea.style.height = textarea.scrollHeight + "px";
  inputcol.style.height = textarea.scrollHeight + 100 + "px";

};

function test() {
  
  for (j = 0; j < 30; j++) {
    addTranslation(j, "Non accusamus. ", ["alt 1alt 1alt 1alt 1", "alt 2"]);
  }
}


var tab1Location = "Dutch";
var tab2Location = "English";


function onGoogleButtonClick(evt){
  const googleButtons = document.querySelectorAll(".google-button");
  googleButtons.forEach((googleButton, index) =>{
    googleButton.classList.remove("google-button-select");
  })
    setGoogleButtonSelection(evt.target);
  }

function setGoogleButtonSelection(googleButton){
  if(googleButton.nodeName=="BUTTON"){
    googleButton.classList.add("google-button-select")
  }else{
    googleButton.parentElement.classList.add("google-button-select");
  }
  
  
}

function setLineStyle(tab) {

  var tabs = document.querySelectorAll("#tab > div > div > ul > li > a");
  tabs.forEach(t => {
    t.classList.remove("nav-link-selected");
  });
  tab.classList.add("nav-link-selected");

  tab1Location = tab.innerHTML;
  let line = document.getElementById("line");
  line.style.left = tab.offsetLeft + "px";
  line.style.width = tab.clientWidth + "px";
}


window.onload = function () {
  const googleButtons = document.querySelectorAll(".google-button");
  googleButtons.forEach((googleButton, index) =>{
    googleButton.onclick = onGoogleButtonClick;
    if(index ==0) setGoogleButtonSelection(googleButton);
  })


  const tabs = document.querySelectorAll('.tabs2 > .nav > .nav-item');
  tabs.forEach((tab, index) => {
    tab.onclick = onTabClick;

    if (index == 0) setLineStyle(tab.querySelector(".nav-link"));
  })

  const tabs2 = document.querySelectorAll('.tabs3 > .nav > .nav-item');
  tabs2.forEach((tab2, index2) => {
    tab2.onclick = onTabClick2;

    if (index2 == 0) setLineStyle2(tab2.querySelector(".nav-link"));
  })
  tab1Location = "Dutch";
  tab2Location = "English";
}

function onTabClick2(evt) {
  if (evt.target.innerHTML === tab1Location) {
    
    var firstTab = document.getElementById("tab").querySelector(" .nav > .nav-item > .nav-link");
    var secondTab = document.getElementById("tab").querySelectorAll(" .nav > .nav-item > .nav-link")[1];
      
      if(firstTab.innerHTML===tab1Location){
        setLineStyle(secondTab);
        setLineStyle2(evt.target);
      }else{
        setLineStyle(firstTab);
        setLineStyle2(evt.target);
      }
    
  } else {
    setLineStyle2(evt.target)
  }

}

function onTabClick(evt) {
    var firstTab = document.getElementById("tab2").querySelector(" .nav > .nav-item > .nav-link");
    var secondTab = document.getElementById("tab2").querySelectorAll(" .nav > .nav-item > .nav-link")[1];
  if (evt.target.innerHTML === tab2Location) {
    if(firstTab.innerHTML===tab2Location){
      
        setLineStyle2(secondTab);
        setLineStyle(evt.target);
    }else{
      
      setLineStyle2(firstTab);
      setLineStyle(evt.target);
    }
  } else {
    
    setLineStyle(evt.target)
    
  }
}

function setLineStyle2(tab2) {

    var tabs2 = document.querySelectorAll("#tab2 > ul > li > a");
    tabs2.forEach(tab => {
      tab.classList.remove("nav-link-selected");
    });
    tab2.classList.add("nav-link-selected");
 
    tab2Location=tab2.innerHTML;
  let line2 = document.getElementById("line2")
  line2.style.left = tab2.offsetLeft + "px";
  line2.style.width = tab2.clientWidth + "px";
}

let typingTimer;                //timer identifier
let doneTypingInterval = 400;  
let myInput = document.getElementById('input-textbox');
let previusText = "";

//on keyup, start the countdown
myInput.addEventListener('keyup', keyevent => {
    clearTimeout(typingTimer);
    if (myInput.value) {
        typingTimer = setTimeout(doneTyping, doneTypingInterval);
    }
});

//user is "finished typing," do something
function doneTyping () {
    if(previusText!==document.getElementById("input-textbox").value){
    previusText=document.getElementById("input-textbox").value;
    javaIntegration.translate(tab2Location,tab1Location,previusText);
    }
}


/* var buttons = document.getElementsByClassName("google-button");

Array.prototype.forEach.call(buttons, function (b) {
    b.addEventListener('click', createRipple);

});

function removeRipple(buttonevent){
  buttonevent.target.remove();
}


function createRipple (e) {
    var circle = document.createElement('div');
    this.appendChild(circle);
    circle.addEventListener("animationend", removeRipple);
    var d = Math.max(this.clientWidth, this.clientHeight);

    circle.style.width = circle.style.height = d + 'px';

  var rect = this.getBoundingClientRect();
  circle.style.left = e.pageX - screenLeft - this.offsetLeft - d / 2 + "px";
  circle.style.top = e.pageY - screenTop - this.offsetTop - d / 2 + "px";
  circle.classList.add('ripple');
  

} */


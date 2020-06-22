function getChatRoom() {
   // The fetch() function returns a Promise because the request is asynchronous.
   getUserLoginStatus();
}

function getUserLoginStatus(){
   fetch('/authdata').then(response => response.text()).then((quote) => {
     document.getElementById('auth-room').innerHTML = quote;
  });
}

function getComments(){
    fetch('/chat-room').then(response => response.json()).then(comment => {
    const chatListElement = document.getElementById('chat-container');
    chatListElement.innerHTML = '';
    console.log(comment);
    for (var i = 0; i < comment.length; i++){
        var obj = comment[i];
        if(obj.imageUrl){
        chatListElement.appendChild(createParagraphElement("Image attached: "))
        chatListElement.appendChild(createImageTag(obj.imageUrl));
        }
        if(obj.entityAnnotations){
            for (var i = 0; i < obj.entityAnnotations.size(); i++){
                chatListElement.appendChild(createParagraphElement(obj.entityAnnotations.get(i)))
            }
        }
        chatListElement.appendChild(createListElement('Name: ' + obj.messageSubject + "\n" + "Message/Comment: " + obj.text + " (" +  obj.score + ")"));
        chatListElement.appendChild(createParagraphElement("  "))
    }
  });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

/** Creates an <li> element containing text. */
function createParagraphElement(text) {
  const pElement = document.createElement('p');
  pElement.innerText = text;
  return pElement;
}

function createImageTag(text){
  var imgElement = document.createElement('img');
  imgElement.src = text;
  imgElement.width = 160;
  imgElement.height = 160;
  return imgElement;
}

async function getRandomQuoteUsingAsyncAwait() {
  const response = await fetch('/chat-room');
  const quote = await response.text();
  document.getElementById('chat-container').innerText = quote;
}

jQuery(document).ready(() => {
  // Logo animation
  const $logo = $("#logo");
  if (location.href.indexOf("#") !== -1) {
    $logo.show();
  }

  $(".menu .tabs a").click(() => {
    $logo.fadeIn("slow");
  });

  $(".tab-profile").click(() => {
    $logo.fadeOut("slow");
  });

  // Color manipulation
  $("#yellow-color").click(function(e) {
    $(".main-wrapper-resume").attr("id", "yellow");
  });

  $("#red-color").click(function(e) {
    $(".main-wrapper-resume").attr("id", "red");
  });

  $("#blue-color").click(function(e) {
    $(".main-wrapper-resume").attr("id", "blue");
  });

  $("#green-color").click(function(e) {
    $(".main-wrapper-resume").attr("id", "green");
  });

  $("#setting-icon").click(function(e) {
    $(".color-box").toggleClass("main");
  });

  // Portifolio items filtering
  const $catsfilter = $(".cats-filter");
  $catsfilter.find("a").click(function() {
    $(this)
      .parent()
      .parent()
      .find("a")
      .removeClass("current");
    $(this).addClass("current");
  });

  const $plist = $("#portifolio-list");
  const $pfilter = $("#portifolio-filter");

  $plist.isotope({
    filter: "*",
    layoutMode: "masonry",
    animationOptions: {
      duration: 750,
      easing: "linear"
    }
  });

  $pfilter.find("a").click(function() {
    const selector = $(this).attr("data-filter");
    $plist.isotope({
      filter: selector,
      layoutMode: "masonry",
      animationOptions: {
        duration: 750,
        easing: "linear",
        queue: false
      }
    });

    return false;
  });

  // Profile image animation
  $(".photo-inner ul").carouFredSel({
    direction: "left",
    circular: true,
    auto: true,
    scroll: {
      items: 1,
      fx: "crossfade",
      duration: 1500,
      wipe: true
    },
    swipe: {
      onTouch: true
    },
    items: {
      width: 153
    }
  });

  // Menu nav bar manipulation
  const $content = $("#content");

  $content.easytabs({
    animate: true,
    updateHash: false,
    transitionIn: "slideDown",
    transitionOut: "slideUp",
    animationSpeed: 600,
    tabs: ".tmenu",
    tabActiveClass: "active"
  });

  $content.find(".tabs li a").hover(
    function() {
      $(this)
        .stop()
        .animate({ marginTop: "-7px" }, 200);
    },
    function() {
      $(this)
        .stop()
        .animate({ marginTop: "0px" }, 300);
    }
  );
});
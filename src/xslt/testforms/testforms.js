function doRequest(form) {
   var elems = form.elements;
   var iframe = document.getElementById('xmlOutputFrame');
   var querySpan = document.getElementById('query');
   var requestParams = [];
   var formattedRequestString = '';
   var value, name, requestString;

   iframe.src = "about:blank";
   for (var i = 0; i != elems.length; i++) {
      if (!(name = elems[i].name) || name == '_environment') {
         continue;
      }

      if (elems[i].type == 'text' || elems[i].type == 'hidden' || elems[i].type == 'textarea') {
         value = elems[i].value;
      } else if (elems[i].type == 'select-one') {
         value = elems[i].options[elems[i].selectedIndex].value;
      }

      if (value) {
         if (name == '_action' || name == '_method' || name == '_target') {
            name = name.substring(1);
         }
         if (window.encodeURIComponent) {
            value = encodeURIComponent(value);
         } else {
            value = escape(value);
         }
         requestParams[requestParams.length] = name + '=' + value;
         if (formattedRequestString) {
            formattedRequestString += '&amp;';
         }

         if (name == '_function') {
            formattedRequestString += '<span class="functionparam">';
         } else {
            formattedRequestString += '<span class="param">';
         }

         formattedRequestString += '<span class="name">' + name + '</span>';
         formattedRequestString += '=<span class="value">' + value + '</span>';
         formattedRequestString += '</span>';
      }
   }

   requestString = form.action + '?' + requestParams.join('&');
   formattedRequestString = form.action + '?' + formattedRequestString;

   iframe.src = requestString;
   querySpan.innerHTML = formattedRequestString;
   return false;
}

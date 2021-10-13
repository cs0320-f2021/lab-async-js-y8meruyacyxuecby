<#assign content>

<div class="container">
  <div class="row">
    <div class="two columns">&nbsp;</div>
    <div class="eight columns">
      <div class="text-center">
        <h1>Autocorrect</h1>
      </div>
      <label for="autocorrect-input">Type here</label>
      <input id="autocorrect-input" name="text" class="u-full-width"
             type="text"
             placeholder="I finally get a bouquet and it's a goodbye present. That's depressing."/>
      <h3 id="loading" hidden>Loading...</h3>
      <ul id="suggestions" aria-live=polite>
      </ul>
    </div>
  </div>
</div>

</#assign>

<#include "main.ftl">

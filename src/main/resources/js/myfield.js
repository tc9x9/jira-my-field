(function ($) {

  var templatesapi = AJS.contextPath() + "/rest/myfield/1.0/";

  function updateConfig() {
    var restdata = '{ ' +
          '"customField1": "' + AJS.$("#customField1").attr("value") + '"}';
    AJS.$.ajax({
        url: templatesapi,
        type: "PUT",
        contentType: "application/json",
        data: restdata,
        processData: false,
        success: function(data) {
           JIRA.Messages.showSuccessMsg(AJS.I18n.getText("Configuration saved"));
        },
    });
  }

  $(document).ready(function() {
    $.ajax({
      url: templatesapi,
      dataType: "json"
    }).done(function(config) {
        $("#customField1").val(config.customField1)
      })
      $("#admin").submit(function(e) {
          e.preventDefault()
          updateConfig()
      })
    })

}) (AJS.$ || jQuery);

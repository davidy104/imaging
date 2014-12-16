var messageCacheOptions = {
    expires: 30000
};

var Bookshop = {
    addErrorMessage:function (message) {
        var alertTemplate = Handlebars.compile($("#template-alert-message-error").html());
        $("#message-holder").html(alertTemplate({message:message}));
        $("#alert-message-error").alert().delay(5000).fadeOut("fast", function() { $(this).remove(); });
    },

    addMessage:function (message) {
        var alertTemplate = Handlebars.compile($("#template-alert-message").html());
        $("#message-holder").html(alertTemplate({message:message}));
        $("#alert-message").alert().delay(5000).fadeOut("fast", function() { $(this).remove(); })
    },
    getErrorMessageCacheKey: function() {
        return "Bookshops.errorMessage";
    },
    getErrorMessageFromCache: function() {
        var errorMessage = amplify.store(Bookshop.getErrorMessageCacheKey());
        amplify.store(Bookshop.getErrorMessageCacheKey(), null);
        return errorMessage;
    },
    getMessageCacheKey: function() {
        return "Bookshops.message";
    },
    getMessageFromCache: function() {
        var message = amplify.store(Bookshop.getMessageCacheKey());
        amplify.store(Bookshop.getMessageCacheKey(), null);
        return message;
    },
    storeErrorMessageToCache: function(message) {
        amplify.store(Bookshop.getErrorMessageCacheKey(), message, messageCacheOptions);
    },
    storeMessageToCache: function(message) {
        amplify.store(Bookshop.getMessageCacheKey(), message, messageCacheOptions);
    }

};

$(document).ready(function () {

    var errorMessage = $(".errroblock");
    if (errorMessage.length > 0) {
    	Bookshop.addErrorMessage(errorMessage.text());
    }
    else {
        errorMessage = Bookshop.getErrorMessageFromCache();
        if (errorMessage) {
        	Bookshop.addErrorMessage(errorMessage);
        }
    }

    var feedbackMessage = $(".messageblock");
    if (feedbackMessage.length > 0) {
    	Bookshop.addMessage(feedbackMessage.text());
    }
    else {
        feedbackMessage = Bookshop.getMessageFromCache();
        if (feedbackMessage) {
        	Bookshop.addMessage(feedbackMessage);
        }
    }
});

$(document).bind('ajaxError', function(error, response) {
    if (response.status == "404") {
        window.location.href = "/error/404";
    }
    else {
        window.location.href = "/bookshop/error/error";
    }
});
